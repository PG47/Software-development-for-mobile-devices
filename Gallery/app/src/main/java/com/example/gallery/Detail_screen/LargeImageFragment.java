package com.example.gallery.Detail_screen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gallery.DatabaseHelper;
import com.example.gallery.Edit_tool_screen.AddNameForFaceActivity;
import com.example.gallery.R;
import com.github.chrisbanes.photoview.OnSingleFlingListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.opencv.photo.Photo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LargeImageFragment extends Fragment {
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    DetailsActivity detailsActivity;
    Context context = null;
    CropImageView cropImageView=null;
    PhotoView photoView;
    Bitmap originalBitmap, tempBitmap;
    String imagePath;
    DatabaseHelper databaseHelper;
    private ArrayList<String> images;

    public interface OnImageChangeListener {
        void onImageChanged(String newImagePath);
        void onClickhide();
    }
    private OnImageChangeListener onImageChangeListener;

    public void setOnImageChangeListener(OnImageChangeListener listener) {
        this.onImageChangeListener = listener;
    }


    public static LargeImageFragment newInstance(String strArg) {
        LargeImageFragment fragment = new LargeImageFragment();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            //context = getActivity();
            context = getContext();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                detailsActivity = (DetailsActivity) context;
            }
        }
        catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DetailsActivity) {
            detailsActivity = (DetailsActivity) context;
        } else {
            throw new IllegalStateException("EditActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ConstraintLayout layoutImage = (ConstraintLayout) inflater.inflate(R.layout.fragment_large_image, null);

        Bundle args = getArguments();
        if (args != null) {
            imagePath = args.getString("selectedImage");
            images = args.getStringArrayList("imageList");
        }
        originalBitmap = BitmapFactory.decodeFile(imagePath);
        tempBitmap = originalBitmap;

        databaseHelper = new DatabaseHelper(context);
        int orientation = getOrientation(imagePath);

        // Rotate the bitmap based on the orientation
        Bitmap rotatedBitmap = rotateBitmap(originalBitmap, orientation);

        cropImageView = layoutImage.findViewById(R.id.cropImageView);
        cropImageView.setImageBitmap(rotatedBitmap);
        cropImageView.setShowCropOverlay(false);
        cropImageView.setVisibility(View.GONE);


        photoView = layoutImage.findViewById(R.id.photoView);
        photoView.setImageBitmap(rotatedBitmap);
        photoView.setVisibility(View.VISIBLE);

        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageChangeListener.onClickhide();
            }
        });
        photoView.setOnSingleFlingListener(new OnSingleFlingListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                imagePath = getNextImagePath(imagePath, images, -1);
                                updateImage();
                            } else {
                                imagePath = getNextImagePath(imagePath, images, 1);
                                updateImage();
                            }
                            detailsActivity.updateSelectedImage(imagePath);
                            result = true;
                        }
                    } else {}
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        });

        return layoutImage;
    }

    public void executeShowCropOverlay() {
        cropImageView.setVisibility(View.VISIBLE); // Show CropImageView
        cropImageView.setShowCropOverlay(true);
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

        return extractedText.toString();
    }
    public void executeFacesDetection() {
        FaceDetector faceDetector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .build();

        if (!faceDetector.isOperational()) {
            Log.d("Error:", "Get errors when setting up");
            return;
        }

        Frame frame = new Frame.Builder().setBitmap(originalBitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);
        tempBitmap = drawRectanglesOnBitmap(faces);
        cropImageView.setImageBitmap(tempBitmap);
        extractFaceBitmaps(faces);
    }
    public void extractFaceBitmaps(SparseArray<Face> faces) {
        List<Bitmap> faceBitmaps = new ArrayList<>();

        for (int i = 0; i < faces.size(); i++) {
            Face face = faces.valueAt(i);

            float x = face.getPosition().x;
            float y = face.getPosition().y;
            float width = face.getWidth();
            float height = face.getHeight();

            Bitmap faceBitmap = Bitmap.createBitmap(originalBitmap, (int) x, (int) y, (int) width, (int) height);

            faceBitmaps.add(faceBitmap);
        }

        List<String> filePaths = new ArrayList<>();
        List<String> expectedNames = new ArrayList<>();
        for (Bitmap bitmap : faceBitmaps) {
            String filePath = saveBitmapToFile(bitmap);
            filePaths.add(filePath);

            String name = databaseHelper.getExpectedName(bitmap);
            expectedNames.add(name);
        }

        Intent intent = new Intent(detailsActivity, AddNameForFaceActivity.class);
        intent.putStringArrayListExtra("filePaths", (ArrayList<String>) filePaths);
        intent.putStringArrayListExtra("expectedNames", (ArrayList<String>) expectedNames);
        startActivity(intent);
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
    public String executeGetCurrentName() {
        String[] comps = imagePath.split("/");
        return comps[comps.length - 1];
    }
    public boolean executeSetNewNameForImage(String name) {
        File oldFile = new File(imagePath);

        if (!oldFile.exists()) {
            return false;
        }

        File parentDir = oldFile.getParentFile();
        File newFile = new File(parentDir, name);

        Boolean res = oldFile.renameTo(newFile);
        if (res) {
            return true;
        }
        return false;
    }

    public void updateImage() {
        originalBitmap = BitmapFactory.decodeFile(imagePath);

        // Read the orientation information from the image file
        int orientation = getOrientation(imagePath);

        // Rotate the bitmap based on the orientation
        Bitmap rotatedBitmap = rotateBitmap(originalBitmap, orientation);

        // Update the ImageView with the rotated bitmap
        if (getView() != null) {
            photoView = getView().findViewById(R.id.photoView);
            photoView.setImageBitmap(rotatedBitmap);
            cropImageView = getView().findViewById(R.id.cropImageView);
            cropImageView.setImageBitmap(rotatedBitmap);
        }
        if (onImageChangeListener != null) {
            onImageChangeListener.onImageChanged(imagePath);
        }
    }

    // Method to get the orientation of the image
    private int getOrientation(String imagePath) {
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Method to rotate the bitmap based on orientation
    private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
    private String getNextImagePath(String currentImagePath, ArrayList<String> imageList, int direction) {
        int currentIndex = imageList.indexOf(currentImagePath);
        int nextIndex = currentIndex + direction;

        // Ensure next index stays within bounds
        if (nextIndex < 0) {
            nextIndex = imageList.size() - 1; // Wrap around to the last image
        } else if (nextIndex >= imageList.size()) {
            nextIndex = 0; // Wrap around to the first image
        }

        return imageList.get(nextIndex);
    }

}
