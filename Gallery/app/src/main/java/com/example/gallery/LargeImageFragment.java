package com.example.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LargeImageFragment extends Fragment {
    DetailsActivity detailsActivity;
    Context context = null;
    CropImageView cropImageView;
    Bitmap originalBitmap, tempBitmap;
    String imagePath;
    DatabaseHelper databaseHelper;

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
            context = getActivity();
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof DetailsActivity) {
                detailsActivity = (DetailsActivity) context;
            } else {
                throw new IllegalStateException("MainActivity must implement callbacks");
            }
        }

        String selectedImage = getArguments().getString("selectedImage");
        imagePath = selectedImage;
        originalBitmap = BitmapFactory.decodeFile(selectedImage);
        tempBitmap = originalBitmap;

        databaseHelper = new DatabaseHelper(context);

        cropImageView = (CropImageView) layoutImage.findViewById(R.id.cropImageView);
        cropImageView.setImageBitmap(originalBitmap);
        cropImageView.setShowCropOverlay(false);

        return layoutImage;
    }

    public void executeShowCropOverlay() {
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
}
