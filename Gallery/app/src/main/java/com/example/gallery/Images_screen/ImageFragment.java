package com.example.gallery.Images_screen;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
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
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.gallery.DatabaseHelper;
import com.example.gallery.Edit_tool_screen.AddNameForFaceActivity;
import com.example.gallery.Edit_tool_screen.EditActivity;
import com.example.gallery.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
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
    Integer color, degValue = 0;
    Boolean horizontalFlip = false, verticalFlip = false;
    float xText, yText;
    DatabaseHelper databaseHelper;
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

        databaseHelper = new DatabaseHelper(context);

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
        BitmapDrawable drawable = new BitmapDrawable(getResources(), adjustedBitmap);
        drawable.setBounds(0, 0, bitmapWidth, bitmapHeight);

        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(rotatedBitmap);
        canvas.rotate(value - 180, bitmapWidth / 2f, bitmapHeight / 2f);
        drawable.draw(canvas);

        tempBitmap = rotatedBitmap;
        degValue = 0;

        cropImageView.setImageBitmap(tempBitmap);
    }
    public void executeFastRotate(int value) {
        BitmapDrawable drawable = new BitmapDrawable(getResources(), adjustedBitmap);
        drawable.setBounds(0, 0, bitmapWidth, bitmapHeight);

        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(rotatedBitmap);
        canvas.rotate(value, bitmapWidth / 2f, bitmapHeight / 2f);
        drawable.draw(canvas);

        tempBitmap = rotatedBitmap;
        degValue = 0;

        cropImageView.setImageBitmap(tempBitmap);
    }
    public void executeMinus90Deg() {
        degValue = degValue - 90;
        cropImageView.setRotatedDegrees(degValue);
    }
    public void executePlus90Deg() {
        degValue = degValue + 90;
        cropImageView.setRotatedDegrees(degValue);
    }
    public void executeCropImage() {
        cropImageView.setShowCropOverlay(false);
        Bitmap croppedBitmap = cropImageView.getCroppedImage();

        adjustedBitmap = croppedBitmap;
        bitmapWidth = adjustedBitmap.getWidth();
        bitmapHeight = adjustedBitmap.getHeight();

        cropImageView.setImageBitmap(adjustedBitmap);
    }
    public void executeColorFilter(int optionColorSet) {
        ColorMatrix colorMatrix = null;
        if (optionColorSet == 0) {
            cropImageView.setImageBitmap(adjustedBitmap);
        } else if (optionColorSet == 1) {
            colorMatrix = createFreshFilter();
        } else if (optionColorSet == 2) {
            colorMatrix = createTransparentFilter();
        } else if (optionColorSet == 3) {
            colorMatrix = createWarmFilter();
        } else if (optionColorSet == 4) {
            colorMatrix = createFilmFilter();
        } else if (optionColorSet == 5) {
            colorMatrix = createModernYellowFilter();
        } else if (optionColorSet == 6) {
            colorMatrix = createBlackWhiteFilter();
        } else if (optionColorSet == 7) {
            colorMatrix = createSepiaFilter();
        } else if (optionColorSet == 8) {
            colorMatrix = createFogFilter();
        } else if (optionColorSet == 9) {
            colorMatrix = createFantasyFilter();
        }

        if (optionColorSet != 0) {
            Bitmap colorBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(colorBitmap);
            Paint paint = new Paint();
            paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            canvas.drawBitmap(adjustedBitmap, 0, 0, paint);
            tempBitmap = colorBitmap;
            cropImageView.setImageBitmap(colorBitmap);
        }
    }
    public Bitmap[] executeGetAppliedColorSet() {
        Bitmap[] res = new Bitmap[10];
        ColorMatrix colorMatrix = null;
        res[0] = adjustedBitmap;
        for (int i = 1; i < 10; i++) {
            if (i == 1) {
                colorMatrix = createFreshFilter();
            } else if (i == 2) {
                colorMatrix = createTransparentFilter();
            } else if (i == 3) {
                colorMatrix = createWarmFilter();
            } else if (i == 4) {
                colorMatrix = createFilmFilter();
            } else if (i == 5) {
                colorMatrix = createModernYellowFilter();
            } else if (i == 6) {
                colorMatrix = createBlackWhiteFilter();
            } else if (i == 7) {
                colorMatrix = createSepiaFilter();
            } else if (i == 8) {
                colorMatrix = createFogFilter();
            } else if (i == 9) {
                colorMatrix = createFantasyFilter();
            }

            Bitmap colorBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(colorBitmap);
            Paint paint = new Paint();
            paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            canvas.drawBitmap(adjustedBitmap, 0, 0, paint);

            res[i] = colorBitmap;
        }
        return res;
    }
    public static ColorMatrix createFreshFilter() {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(new float[] {
                1.2f, 0, 0, 0, 0,
                0,1.2f, 0, 0, 0,
                0, 0,1.2f, 0, 0,
                0, 0, 0, 1.0f, 0
        });
        return new ColorMatrix(colorMatrix);
    }
    public static ColorMatrix createTransparentFilter() {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(new float[] {
                1, 0, 0, 0, 0,
                0, 1, 0, 0, 0,
                0, 0, 1, 0, 0,
                0, 0, 0, 0.5f, 0
        });
        return colorMatrix;
    }
    public static ColorMatrix createWarmFilter() {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(new float[] {
                1.3f, 0, 0, 0, 0,
                0, 1.1f, 0, 0, 0,
                0, 0, 0.8f, 0, 0,
                0, 0, 0, 1.0f, 0
        });
        return colorMatrix;
    }
    public static ColorMatrix createFilmFilter() {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(new float[] {
                1.2f, 0, 0, 0, -10,
                0, 1.2f, 0, 0, -10,
                0, 0, 1.2f, 0, -10,
                0, 0, 0, 1.0f, 0
        });
        return colorMatrix;
    }
    public static ColorMatrix createModernYellowFilter() {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(new float[] {
                1.2f, 0, 0, 0, 0,
                0, 1.2f, 0, 0, 0,
                0, 0, 0.8f, 0, 0,
                0, 0, 0, 1.0f, 0
        });
        return colorMatrix;
    }
    public static ColorMatrix createBlackWhiteFilter() {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        return colorMatrix;
    }
    public static ColorMatrix createSepiaFilter() {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(new float[] {
                0.393f, 0.769f, 0.189f, 0, 0,
                0.349f, 0.686f, 0.168f, 0, 0,
                0.272f, 0.534f, 0.131f, 0, 0,
                0, 0, 0, 1.0f, 0
        });
        return colorMatrix;
    }
    public static ColorMatrix createFogFilter() {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(new float[] {
                1.0f, 0, 0, 0, 0,
                0, 1.0f, 0, 0, 0,
                0, 0, 1.0f, 0, 0,
                0, 0, 0, 1.2f, 0
        });
        return colorMatrix;
    }
    public static ColorMatrix createFantasyFilter() {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(new float[] {
                1.2f, 0, 0, 0, 0,
                0, 1.2f, 0, 0, 0,
                0, 0, 1.2f, 0, 0,
                0, 0, 0, 1.0f, 0
        });
        return colorMatrix;
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
    public void executeSaveChangeFilter() {
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

        tempBitmap = Bitmap.createBitmap(pixels, bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        cropImageView.setImageBitmap(tempBitmap);
    }
    public void executeChangeBlur(int value) {
        float blurRadius = value / 4.5F;

        adjustedBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(adjustedBitmap);
        canvas.drawBitmap(originalBitmap, 0, 0, null);

        tempBitmap = applyBlur(adjustedBitmap, blurRadius);

        cropImageView.setImageBitmap(tempBitmap);
    }
    private Bitmap applyBlur(Bitmap src, float blurRadius) {
        if (blurRadius <= 0) {
            return src;
        }

        Bitmap blurredBitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);

        RenderScript rs = RenderScript.create(context);

        Allocation input = Allocation.createFromBitmap(rs, src, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(blurRadius);

        script.setInput(input);
        script.forEach(output);

        output.copyTo(blurredBitmap);

        rs.destroy();

        return blurredBitmap;
    }
    public void executeChangeSepia(int value) {
        float intensity = value / 10f;

        int[] pixels = new int[bitmapWidth * bitmapHeight];
        adjustedBitmap.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);

        for (int i = 0; i < pixels.length; i++) {
            int alpha = (pixels[i] >> 24) & 0xFF;
            int red = (pixels[i] >> 16) & 0xFF;
            int green = (pixels[i] >> 8) & 0xFF;
            int blue = pixels[i] & 0xFF;

            int sepiaRed = (int) (0.393 * red + 0.769 * green + 0.189 * blue);
            int sepiaGreen = (int) (0.349 * red + 0.686 * green + 0.168 * blue);
            int sepiaBlue = (int) (0.272 * red + 0.534 * green + 0.131 * blue);

            sepiaRed = (int) (red + (sepiaRed - red) * intensity);
            sepiaGreen = (int) (green + (sepiaGreen - green) * intensity);
            sepiaBlue = (int) (blue + (sepiaBlue - blue) * intensity);

            sepiaRed = Math.min(255, Math.max(0, sepiaRed));
            sepiaGreen = Math.min(255, Math.max(0, sepiaGreen));
            sepiaBlue = Math.min(255, Math.max(0, sepiaBlue));

            pixels[i] = (alpha << 24) | (sepiaRed << 16) | (sepiaGreen << 8) | sepiaBlue;
        }
        tempBitmap = Bitmap.createBitmap(pixels, bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        cropImageView.setImageBitmap(tempBitmap);
    }
    public void executeChangeGrayscale(int value) {
        float intensity = value / 10f;

        int[] pixels = new int[bitmapWidth * bitmapHeight];
        adjustedBitmap.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);

        for (int i = 0; i < pixels.length; i++) {
            int alpha = (pixels[i] >> 24) & 0xFF;
            int red = (pixels[i] >> 16) & 0xFF;
            int green = (pixels[i] >> 8) & 0xFF;
            int blue = pixels[i] & 0xFF;

            int gray = (int) (0.299 * red + 0.587 * green + 0.114 * blue);

            gray = (int) (gray + (gray - red) * intensity);

            gray = Math.min(255, Math.max(0, gray));

            pixels[i] = (alpha << 24) | (gray << 16) | (gray << 8) | gray;
        }
        tempBitmap = Bitmap.createBitmap(pixels, bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        cropImageView.setImageBitmap(tempBitmap);
    }
    public void executeChangeSharpen(int value) {
        float intensity = value / 10f;

        int[] pixels = new int[bitmapWidth * bitmapHeight];
        adjustedBitmap.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);

        for (int i = 0; i < pixels.length; i++) {
            int alpha = (pixels[i] >> 24) & 0xFF;
            int red = (pixels[i] >> 16) & 0xFF;
            int green = (pixels[i] >> 8) & 0xFF;
            int blue = pixels[i] & 0xFF;

            int newRed = red + (int) (intensity * (red - 128));
            int newGreen = green + (int) (intensity * (green - 128));
            int newBlue = blue + (int) (intensity * (blue - 128));

            newRed = Math.min(255, Math.max(0, newRed));
            newGreen = Math.min(255, Math.max(0, newGreen));
            newBlue = Math.min(255, Math.max(0, newBlue));

            pixels[i] = (alpha << 24) | (newRed << 16) | (newGreen << 8) | newBlue;
        }
        tempBitmap = Bitmap.createBitmap(pixels, bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        cropImageView.setImageBitmap(tempBitmap);
    }
    public void executeAddEditText() {
        editText = new EditText(context);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.leftToLeft = R.id.myRl;
        layoutParams.rightToRight = R.id.myRl;
        layoutParams.topToTop = R.id.myRl;
        layoutParams.bottomToBottom = R.id.myRl;

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

                        xText = (float) Math.max(0, Math.min(relativeX * adjustedBitmap.getWidth(), adjustedBitmap.getWidth()));
                        yText = (float) Math.max(0, Math.min(relativeY * adjustedBitmap.getHeight(), adjustedBitmap.getHeight()));

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
    public void executeSetOriginalImage() {
        cropImageView.setImageBitmap(adjustedBitmap);
    }
    public void invisibleEditText() {
        if (editText != null) {
            editText.setVisibility(View.GONE);
        }
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
        float fontSizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Float.parseFloat(fontSize) * 1.5F, getResources().getDisplayMetrics());
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
    public void executeCancelCropOverlay() {
        cropImageView.setShowCropOverlay(false);
    }
    public void executeSetUpNormal() {
        cropImageView.setImageBitmap(adjustedBitmap);
    }
    public void executeSetUpHorizontalFlip() {
        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);
        if (tempBitmap != null) {
            tempBitmap = Bitmap.createBitmap(tempBitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
        } else {
            tempBitmap = Bitmap.createBitmap(adjustedBitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
        }

        horizontalFlip = !horizontalFlip && !verticalFlip;
        cropImageView.setImageBitmap(tempBitmap);
    }
    public void executeSetUpVerticalFlip() {
        Matrix matrix = new Matrix();
        matrix.setScale(1, -1);
        if (tempBitmap != null) {
            tempBitmap = Bitmap.createBitmap(tempBitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
        } else {
            tempBitmap = Bitmap.createBitmap(adjustedBitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
        }

        verticalFlip = !verticalFlip && !horizontalFlip;
        cropImageView.setImageBitmap(tempBitmap);
    }
    public void executeSetRatio(int x, int y) { cropImageView.setAspectRatio(x,y); }
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
    public SparseArray<Face> executeFacesDetection() {
        FaceDetector faceDetector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .build();

        if (!faceDetector.isOperational()) {
            Log.d("Error:", "Get errors when setting up");
        }

        Frame frame = new Frame.Builder().setBitmap(adjustedBitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);
        tempBitmap = drawRectanglesOnBitmap(faces);
//        cropImageView.setImageBitmap(tempBitmap);
        return faces;
    }
    public void setImage() {
        cropImageView.setImageBitmap(tempBitmap);
    }
    public Bitmap drawRectanglesOnBitmap(@NonNull SparseArray<Face> faces) {
        Bitmap bitmapCopy = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(bitmapCopy);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15);

        for (int i = 0; i < faces.size(); i++) {
            Face face = faces.valueAt(i);
            float x = face.getPosition().x;
            float y = face.getPosition().y;
            float width = face.getWidth();
            float height = face.getHeight();
            canvas.drawRect(x, y, x + width, y + height, paint);
        }

        return bitmapCopy;
    }
    public ArrayList<ArrayList<String>> extractFaceBitmaps(SparseArray<Face> faces) {
        List<Bitmap> faceBitmaps = new ArrayList<>();

        for (int i = 0; i < faces.size(); i++) {
            Face face = faces.valueAt(i);

            float x = face.getPosition().x;
            float y = face.getPosition().y;
            float width = face.getWidth();
            float height = face.getHeight();

            Bitmap faceBitmap = Bitmap.createBitmap(originalBitmap, (int) x, (int) y, (int) width, (int) height);
            Bitmap circularBitmap = cropToCircle(faceBitmap);
            faceBitmaps.add(circularBitmap);
        }

        List<String> filePaths = new ArrayList<>();
        List<String> expectedNames = new ArrayList<>();
        for (Bitmap bitmap : faceBitmaps) {
            String filePath = saveBitmapToFile(bitmap);
            filePaths.add(filePath);

            String name = databaseHelper.getExpectedName(bitmap);
            expectedNames.add(name);
        }

        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();
        arrayLists.add((ArrayList<String>) filePaths);
        arrayLists.add((ArrayList<String>) expectedNames);
        return arrayLists;
    }
    public Bitmap cropToCircle(Bitmap faceBitmap) {
        Mat mat = new Mat();
        Utils.bitmapToMat(faceBitmap, mat);

        int radius = (int) (0.4 * mat.width());
        Point center = new Point(mat.width() / 2, mat.height() / 2);

        Mat mask = new Mat(mat.rows(), mat.cols(), CvType.CV_8UC1, Scalar.all(0));
        Imgproc.circle(mask, center, radius, new Scalar(255), -1);

        Mat croppedMat = new Mat();
        mat.copyTo(croppedMat, mask);

        Bitmap croppedBitmap = Bitmap.createBitmap(croppedMat.cols(), croppedMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(croppedMat, croppedBitmap);

        return croppedBitmap;
    }
    private String saveBitmapToFile(Bitmap bitmap) {
        File file = new File(getContext().getCacheDir(), "temp_image_" + System.currentTimeMillis() + ".png");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }
    public String executeExtractText() {
        tempBitmap = cropImageView.getCroppedImage();
        cropImageView.setShowCropOverlay(false);

        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        if (!textRecognizer.isOperational()) {
            return "Text recognizer not operational";
        }

        Frame frame = new Frame.Builder().setBitmap(tempBitmap).build();
        SparseArray<TextBlock> textBlocks = textRecognizer.detect(frame);

        StringBuilder extractedText = new StringBuilder();
        for (int i = 0; i < textBlocks.size(); i++) {
            TextBlock textBlock = textBlocks.valueAt(i);
            extractedText.append(textBlock.getValue());
            extractedText.append("\n");
        }

        cropImageView.setImageBitmap(adjustedBitmap);

        return extractedText.toString();
    }
}
