package com.example.gallery;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageOptions;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;


public class ImageFragment extends Fragment {
    EditActivity editActivity;
    Context context;
    float zoomLevel = (float) Math.sqrt(2);
    private ImageView myImage;
    private Bitmap originalBitmap;
    private Bitmap adjustedBitmap;
    String selectedImage;
    RelativeLayout layoutImage;
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
        layoutImage = (RelativeLayout)inflater.inflate(R.layout.fragment_image, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof EditActivity) {
                editActivity = (EditActivity) getActivity();
            } else {
                throw new IllegalStateException("EditActivity must implement callbacks");
            }
        }

        selectedImage = getArguments().getString("selectedImage");
        originalBitmap = BitmapFactory.decodeFile(selectedImage);
        adjustedBitmap = originalBitmap;

        myImage = (ImageView) layoutImage.findViewById(R.id.showImageView);
        myImage.setImageBitmap(originalBitmap);

//        if (selectedImage != null) {
//            Glide.with(context).load(selectedImage).centerCrop().into(this.myImage);
//        }
//        cropImageView = (CropImageView) layoutImage.findViewById(com.theartofdev.edmodo.cropper.R.id.cropImageView);
//        originalBitmap = BitmapFactory.decodeFile(selectedImage);
//
//        File file = new File(selectedImage);
//        imageUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
//        cropImageView.setImageUriAsync(imageUri);
//        cropImageView.setAspectRatio(1, 1);
//        cropImageView.setOnSetImageUriCompleteListener(new CropImageView.OnSetImageUriCompleteListener() {
//            @Override
//            public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
//                if (error != null) {
//                    // Handle error
//                    Log.e("ImageLoadError", "Error loading image: " + error.getMessage());
//                } else {
//                    // Image loaded successfully
//                    Log.d("ImageLoadSuccess", "Image loaded successfully!");
//                }
//            }
//        });
//
//        cropImageView.setVisibility(View.VISIBLE);
//        cropImageView.setFixedAspectRatio(true);
//        cropImageView.setAutoZoomEnabled(false);
//        cropImageView.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
//            @Override
//            public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
//                Uri croppedImageUri = result.getUri();
//                cropImageView.setImageUriAsync(croppedImageUri);
//            }
//        });

        return layoutImage;
    }

    public void executeRotate(int value) {
        float scaleFactor = calculateScaleFactor(value);
        Matrix matrix = new Matrix();
        matrix.postRotate(value - 45);
        matrix.postScale(scaleFactor, scaleFactor);
        Bitmap rotatedAndScaled = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
        myImage.setImageBitmap(rotatedAndScaled);
    }

    public void executeChangeBrightness(int value) {
        float brightnessFactor = Math.max(0, Math.min(100, value));;
        int width = adjustedBitmap.getWidth();
        int height = adjustedBitmap.getHeight();

        int[] pixels = new int[width * height];
        originalBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

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

        adjustedBitmap = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
        myImage.setImageBitmap(adjustedBitmap);
    }

    public void executeChangeContrast(int value) {
        float contrastLevel = value / 10F;
        int width = adjustedBitmap.getWidth();
        int height = adjustedBitmap.getHeight();

        int[] pixels = new int[width * height];
        originalBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

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

        adjustedBitmap = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
        myImage.setImageBitmap(adjustedBitmap);
    }

    public void executeAddEditText() {
        EditText editText = new EditText(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        editText.setLayoutParams(layoutParams);
        editText.setHint("Enter text here");
        editText.setInputType(InputType.TYPE_CLASS_TEXT);

        editText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                view.startDrag(null, new View.DragShadowBuilder(view), null, 0);
                return true;
            }
        });
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.requestFocus();
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
                        break;
                    case MotionEvent.ACTION_MOVE:
                        v.setX(event.getRawX() + dX);
                        v.setY(event.getRawY() + dY);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.setVisibility(View.VISIBLE);
                        break;
                }
                return false;
            }
        });

        layoutImage.addView(editText);
    }

    public void updateEditText() {

    }

    public void executeZoom() {
//        int cropWidth = 450; // Width of the crop rectangle
//        int cropHeight = 300; // Height of the crop rectangle
//
//// Calculate the position of the crop rectangle relative to the center of the cropImageView
//        int centerX = 1000 / 2; // X-coordinate of the center of the cropImageView
//        int centerY = 1500 / 2; // Y-coordinate of the center of the cropImageView
//        int left = centerX - (cropWidth / 2); // X-coordinate of the left edge of the crop rectangle
//        int top = centerY - (cropHeight / 2); // Y-coordinate of the top edge of the crop rectangle
//        int right = left + cropWidth; // X-coordinate of the right edge of the crop rectangle
//        int bottom = top + cropHeight;
//        Log.d("test value", "width: " + cropImageView.getWidth() + "height: " + cropImageView.getHeight() + ", " + left + ", " + top + ", " + right + ", " + bottom);
//        cropImageView.setCropRect(new Rect(left, top, right, bottom));
//        cropImageView.setAutoZoomEnabled(true);
    }
    private float calculateScaleFactor(float angle) {
        if (angle > 45) {
            return (float) Math.sin(Math.toRadians(angle)) * zoomLevel;
        }
        return (float) Math.cos(Math.toRadians(angle)) * zoomLevel;
    }

//    private void startCroppingActivity() {
//        CropImage.activity(imageUri)
//                .setGuidelines(CropImageView.Guidelines.ON)
//                .start(requireActivity());
//    }
}
