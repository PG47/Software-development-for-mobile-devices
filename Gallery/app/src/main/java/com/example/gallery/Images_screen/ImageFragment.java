package com.example.gallery.Images_screen;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.gallery.Edit_tool_screen.EditActivity;
import com.example.gallery.R;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;


public class ImageFragment extends Fragment {
    EditActivity editActivity;
    Context context;
    private Bitmap originalBitmap;
    private Bitmap adjustedBitmap;
    private Bitmap tempBitmap;
    private int bitmapWidth, bitmapHeight;
    String selectedImage;
    ConstraintLayout layoutImage;
    EditText editText;
    CropImageView cropImageView;
    String fontFamily, fontSize = "11";
    Boolean italic, bold;
    Integer color;
    Boolean horizontalFlip = false, verticalFlip = false;
    float xText, yText;
    public static ImageFragment newInstance(String strArg) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                editActivity = (EditActivity) getActivity();
            }
        }
        catch (IllegalStateException e) {
            throw new IllegalStateException("EditActivity must implement callbacks");
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EditActivity) {
            editActivity = (EditActivity) context;
        } else {
            throw new IllegalStateException("EditActivity must implement callbacks");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutImage = (ConstraintLayout) inflater.inflate(R.layout.fragment_image, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof EditActivity) {
                editActivity = (EditActivity) getActivity();
            } else {
                throw new IllegalStateException("EditActivity must implement callbacks");
            }
        }

        selectedImage = getArguments().getString("selectedImage");
        originalBitmap = BitmapFactory.decodeFile(selectedImage);
        adjustedBitmap = Bitmap.createBitmap(originalBitmap);

        bitmapWidth = originalBitmap.getWidth();
        bitmapHeight = originalBitmap.getHeight();

        cropImageView = (CropImageView) layoutImage.findViewById(com.theartofdev.edmodo.cropper.R.id.cropImageView);

        cropImageView.setImageBitmap(adjustedBitmap);
        cropImageView.setShowCropOverlay(false);
        cropImageView.setOnSetImageUriCompleteListener(new CropImageView.OnSetImageUriCompleteListener() {
            @Override
            public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
                if (error != null) {
                    Log.e("ImageLoadError", "Error loading image: " + error.getMessage());
                } else {
                    Log.d("ImageLoadSuccess", "Image loaded successfully!");
                }
            }
        });

        return layoutImage;
    }
    public void executeRotate(int value) {
        cropImageView.setShowCropOverlay(true);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), adjustedBitmap);
        drawable.setBounds(0, 0, bitmapWidth, bitmapHeight);

        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(rotatedBitmap);
        canvas.rotate(value - 180, bitmapWidth / 2f, bitmapHeight / 2f);
        drawable.draw(canvas);

        tempBitmap = rotatedBitmap;

        cropImageView.setImageBitmap(tempBitmap);
    }
    public void executeFastRotate(int value) {
        cropImageView.setShowCropOverlay(true);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), adjustedBitmap);
        drawable.setBounds(0, 0, bitmapWidth, bitmapHeight);

        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(rotatedBitmap);
        canvas.rotate(value, bitmapWidth / 2f, bitmapHeight / 2f);
        drawable.draw(canvas);

        tempBitmap = rotatedBitmap;

        cropImageView.setImageBitmap(tempBitmap);
    }
    public void executeCropImage() {
        cropImageView.setShowCropOverlay(false);
        Bitmap croppedBitmap = cropImageView.getCroppedImage();

        adjustedBitmap = croppedBitmap;
        bitmapWidth = adjustedBitmap.getWidth();
        bitmapHeight = adjustedBitmap.getHeight();

        cropImageView.setImageBitmap(adjustedBitmap);
    }
    public void executeChangeBrightness(int value) {
        float brightnessFactor = Math.max(0, Math.min(100, value));;

        int[] pixels = new int[bitmapWidth * bitmapHeight];
        adjustedBitmap.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);

        for (int i = 0; i < pixels.length; i++) {
            int alpha = (pixels[i] >> 24) & 0xFF;
            int red = (pixels[i] >> 16) & 0xFF;
            int green = (pixels[i] >> 8) & 0xFF;
            int blue = pixels[i] & 0xFF;

            red = (int) (red + brightnessFactor);
            green = (int) (green + brightnessFactor);
            blue = (int) (blue + brightnessFactor);

            red = Math.min(255, Math.max(0, red));
            green = Math.min(255, Math.max(0, green));
            blue = Math.min(255, Math.max(0, blue));

            pixels[i] = (alpha << 24) | (red << 16) | (green << 8) | blue;
        }

        tempBitmap = Bitmap.createBitmap(pixels, bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        cropImageView.setImageBitmap(tempBitmap);
    }
    public void executeSaveChangeBrightness() {
        if (tempBitmap != null) {
            adjustedBitmap = tempBitmap;
            cropImageView.setImageBitmap(adjustedBitmap);
        }
    }
    public void executeChangeContrast(int value) {
        float contrastLevel = value / 10F;

        int[] pixels = new int[bitmapWidth * bitmapHeight];
        adjustedBitmap.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);

        for (int i = 0; i < pixels.length; i++) {
            int alpha = (pixels[i] >> 24) & 0xFF;
            int red = (pixels[i] >> 16) & 0xFF;
            int green = (pixels[i] >> 8) & 0xFF;
            int blue = pixels[i] & 0xFF;

            red = (int) ((red - 128) * contrastLevel + 128);
            green = (int) ((green - 128) * contrastLevel + 128);
            blue = (int) ((blue - 128) * contrastLevel + 128);

            red = Math.min(255, Math.max(0, red));
            green = Math.min(255, Math.max(0, green));
            blue = Math.min(255, Math.max(0, blue));

            pixels[i] = (alpha << 24) | (red << 16) | (green << 8) | blue;
        }

        adjustedBitmap = Bitmap.createBitmap(pixels, bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        cropImageView.setImageBitmap(adjustedBitmap);
    }

    public void executeChangeBlur(int value) {
        // Calculate blur radius based on the provided value
        float blurRadius = value / 4.5F;

        // Create an empty bitmap to store the blurred image
        adjustedBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);

        // Copy the original bitmap to the blurred bitmap
        Canvas canvas = new Canvas(adjustedBitmap);
        canvas.drawBitmap(originalBitmap, 0, 0, null);

        // Apply blur effect to the blurred bitmap
        adjustedBitmap = applyBlur(adjustedBitmap, blurRadius);

        // Set the adjusted bitmap to the ImageView for display
        cropImageView.setImageBitmap(adjustedBitmap);
    }

    private Bitmap applyBlur(Bitmap src, float blurRadius) {
        if (blurRadius <= 0) {
            return src;
        }

        // Create a new bitmap for the blurred image
        Bitmap blurredBitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);

        // Create a RenderScript context
        RenderScript rs = RenderScript.create(context);

        // Allocate memory for Renderscript to work with
        Allocation input = Allocation.createFromBitmap(rs, src, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        Allocation output = Allocation.createTyped(rs, input.getType());

        // Create a blur script
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(blurRadius);

        // Perform the blur operation
        script.setInput(input);
        script.forEach(output);

        // Copy the blurred image from the Allocation to the blurred bitmap
        output.copyTo(blurredBitmap);

        // Destroy RenderScript resources
        rs.destroy();

        return blurredBitmap;
    }

    public void executeChangeSepia(int value) {
        float intensity = value / 10f;

        // Create an array to store the pixel values of the original bitmap
        int[] pixels = new int[bitmapWidth * bitmapHeight];
        originalBitmap.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);

        // Loop through each pixel to apply sepia effect
        for (int i = 0; i < pixels.length; i++) {
            int alpha = (pixels[i] >> 24) & 0xFF;
            int red = (pixels[i] >> 16) & 0xFF;
            int green = (pixels[i] >> 8) & 0xFF;
            int blue = pixels[i] & 0xFF;

            int sepiaRed = (int) (0.393 * red + 0.769 * green + 0.189 * blue);
            int sepiaGreen = (int) (0.349 * red + 0.686 * green + 0.168 * blue);
            int sepiaBlue = (int) (0.272 * red + 0.534 * green + 0.131 * blue);

            // Apply intensity to sepia values
            sepiaRed = (int) (red + (sepiaRed - red) * intensity);
            sepiaGreen = (int) (green + (sepiaGreen - green) * intensity);
            sepiaBlue = (int) (blue + (sepiaBlue - blue) * intensity);

            // Clip values to ensure they are within the valid range
            sepiaRed = Math.min(255, Math.max(0, sepiaRed));
            sepiaGreen = Math.min(255, Math.max(0, sepiaGreen));
            sepiaBlue = Math.min(255, Math.max(0, sepiaBlue));

            // Combine sepia values into a single pixel
            pixels[i] = (alpha << 24) | (sepiaRed << 16) | (sepiaGreen << 8) | sepiaBlue;
        }

        // Create a new bitmap from the modified pixel array
        adjustedBitmap = Bitmap.createBitmap(pixels, bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);

        // Set the adjusted bitmap to the ImageView for display
        cropImageView.setImageBitmap(adjustedBitmap);
    }
    public void executeChangeGrayscale(int value) {
        float intensity = value / 10f;

        // Create an array to store the pixel values of the original bitmap
        int[] pixels = new int[bitmapWidth * bitmapHeight];
        originalBitmap.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);

        // Loop through each pixel to apply grayscale effect
        for (int i = 0; i < pixels.length; i++) {
            int alpha = (pixels[i] >> 24) & 0xFF;
            int red = (pixels[i] >> 16) & 0xFF;
            int green = (pixels[i] >> 8) & 0xFF;
            int blue = pixels[i] & 0xFF;

            // Calculate grayscale value
            int gray = (int) (0.299 * red + 0.587 * green + 0.114 * blue);

            // Apply intensity to grayscale value
            gray = (int) (gray + (gray - red) * intensity);

            // Clip values to ensure they are within the valid range
            gray = Math.min(255, Math.max(0, gray));

            // Combine grayscale values into a single pixel
            pixels[i] = (alpha << 24) | (gray << 16) | (gray << 8) | gray;
        }

        // Create a new bitmap from the modified pixel array
        adjustedBitmap = Bitmap.createBitmap(pixels, bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);

        // Set the adjusted bitmap to the ImageView for display
        cropImageView.setImageBitmap(adjustedBitmap);
    }

    public void executeChangeSharpen(int value) {
        // Ensure value is within a reasonable range
        float intensity = value / 10f;

        // Get the pixel array of the original bitmap
        int[] pixels = new int[bitmapWidth * bitmapHeight];
        originalBitmap.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);

        // Apply the sharpening effect to each pixel
        for (int i = 0; i < pixels.length; i++) {
            int alpha = (pixels[i] >> 24) & 0xFF;
            int red = (pixels[i] >> 16) & 0xFF;
            int green = (pixels[i] >> 8) & 0xFF;
            int blue = pixels[i] & 0xFF;

            // Calculate new pixel values
            int newRed = red + (int) (intensity * (red - 128));
            int newGreen = green + (int) (intensity * (green - 128));
            int newBlue = blue + (int) (intensity * (blue - 128));

            // Clip values to ensure they are within the valid range
            newRed = Math.min(255, Math.max(0, newRed));
            newGreen = Math.min(255, Math.max(0, newGreen));
            newBlue = Math.min(255, Math.max(0, newBlue));

            // Combine RGB values into a single pixel
            pixels[i] = (alpha << 24) | (newRed << 16) | (newGreen << 8) | newBlue;
        }

        // Create a new bitmap from the modified pixel array
        adjustedBitmap = Bitmap.createBitmap(pixels, bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);

        // Set the adjusted bitmap to the ImageView for display
        cropImageView.setImageBitmap(adjustedBitmap);
    }






    public void executeAddEditText() {
        editText = new EditText(context);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.leftToLeft = R.id.cropImageView;
        layoutParams.rightToRight = R.id.cropImageView;
        layoutParams.topToTop = R.id.cropImageView;
        layoutParams.bottomToBottom = R.id.cropImageView;

        editText.setLayoutParams(layoutParams);
        editText.setHint("Enter text here");
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setPadding(0, 0, 0, 0);
        editText.setBackground(null);

        editText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                view.startDrag(null, new View.DragShadowBuilder(view), null, 0);
                return true;
            }
        });
        editText.setOnTouchListener(new View.OnTouchListener() {
            float dX, dY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();
                        v.clearFocus();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float newX = event.getRawX() + dX;
                        float newY = event.getRawY() + dY;

                        newX = Math.max(0, Math.min(newX, cropImageView.getWidth()));
                        newY = Math.max(0, Math.min(newY, cropImageView.getHeight()));

                        float relativeX = newX / cropImageView.getWidth();
                        float relativeY = newY / cropImageView.getHeight();

                        xText = (float) (relativeX * adjustedBitmap.getWidth());
                        yText = (float) (relativeY * adjustedBitmap.getHeight());

                        v.setX(newX);
                        v.setY(newY);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.requestFocus();
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
                        break;
                }
                return true;
            }
        });

        layoutImage.addView(editText);
    }
    public void updateEditText(String strFontFamily, String strFontSize, boolean isItalic, boolean isBold, int textColor) {
        fontFamily = strFontFamily;
        fontSize = strFontSize;
        italic = isItalic;
        bold = isBold;

        textColor = ContextCompat.getColor(context, textColor);
        color = textColor;
        Typeface typeface;
        switch (strFontFamily) {
            case "MONOSPACE": {
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);
                break;
            }
            case "SANS_SERIF": {
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
                break;
            }
            case "SERIF": {
                typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL);
                break;
            }
            case "DEFAULT_BOLD": {
                typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL);
                break;
            }
            default:
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);
                break;
        }

        if (isItalic && isBold) {
            typeface = Typeface.create(typeface, Typeface.BOLD_ITALIC);
        } else if (isItalic) {
            typeface = Typeface.create(typeface, Typeface.ITALIC);
        } else if (isBold) {
            typeface = Typeface.create(typeface, Typeface.BOLD);
        }

        editText.setTypeface(typeface);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Float.parseFloat(strFontSize));
        editText.setTextColor(textColor);
    }
    public void executeZoom() {

    }
    public void saveImageToDevices(Bitmap imageBitmap) {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            String imageName = UUID.randomUUID().toString() + ".jpg";
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            dir.mkdirs();
            File imagePath = new File(dir, imageName);

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

            ContentResolver resolver = context.getContentResolver();
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            try {
                OutputStream outputStream = resolver.openOutputStream(imageUri);
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();

                MediaScannerConnection.scanFile(requireContext(),
                        new String[]{imagePath.getAbsolutePath()},
                        new String[]{"image/jpeg"},
                        null);

                Toast.makeText(requireContext(), "Image saved successfully", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }
    public void executeAddTextToImage() {
        editText.setVisibility(View.GONE);
        Bitmap mutableBitmap = adjustedBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawBitmap(adjustedBitmap, 0, 0, null);

        Paint paint = new Paint();
        float fontSizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Float.parseFloat(fontSize) * 3.5F, getResources().getDisplayMetrics());
        paint.setTextSize(fontSizePx);
        paint.setColor(color);

        Typeface typeface = Typeface.create(fontFamily, Typeface.NORMAL);
        if (italic && bold) {
            paint.setTypeface(Typeface.create(typeface, Typeface.BOLD_ITALIC));
        } else if (italic) {
            paint.setTypeface(Typeface.create(typeface, Typeface.ITALIC));
        } else if (bold) {
            paint.setTypeface(Typeface.create(typeface, Typeface.BOLD));
        }
        canvas.drawText(String.valueOf(editText.getText()), xText, yText, paint);

        adjustedBitmap = mutableBitmap;
        cropImageView.setImageBitmap(adjustedBitmap);
    }
    public void executeSaveImage() {
        saveImageToDevices(adjustedBitmap);
    }
    public void executeSetCropOverlay() {
        cropImageView.setAspectRatio(1, 1);
        cropImageView.setShowCropOverlay(true);
    }
    public void executeSetUpNormal() {
        cropImageView.setImageBitmap(adjustedBitmap);
    }
    public void executeSetUpHorizontalFlip() {
        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);
        if (horizontalFlip == false && verticalFlip == false) {
            tempBitmap = Bitmap.createBitmap(adjustedBitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
            horizontalFlip = true;
        } else {
            tempBitmap = Bitmap.createBitmap(tempBitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
            horizontalFlip = false;
        }
        cropImageView.setImageBitmap(tempBitmap);
    }
    public void executeSetUpVerticalFlip() {
        Matrix matrix = new Matrix();
        matrix.setScale(1, -1);
        if (verticalFlip == false && horizontalFlip == false) {
            tempBitmap = Bitmap.createBitmap(adjustedBitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
            verticalFlip = true;
        } else {
            tempBitmap = Bitmap.createBitmap(tempBitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
            verticalFlip = false;
        }
        cropImageView.setImageBitmap(tempBitmap);
    }
    public void executeSetRatio1() { cropImageView.setAspectRatio(1,1); }
    public void executeSetRatio2() { cropImageView.setAspectRatio(3,2); }
    public void executeSetRatio3() { cropImageView.setAspectRatio(4,3); }
    public void executeSetRatio4() { cropImageView.setAspectRatio(5,4); }
    public void executeSetRatio5() { cropImageView.setAspectRatio(16,9); }
    public boolean executeCheckChange() {
        if (originalBitmap.getWidth() != adjustedBitmap.getWidth() || originalBitmap.getHeight() != adjustedBitmap.getHeight()) {
            return true;
        }

        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (originalBitmap.getPixel(x, y) != adjustedBitmap.getPixel(x, y)) {
                    return true;
                }
            }
        }

        return false;
    }
}