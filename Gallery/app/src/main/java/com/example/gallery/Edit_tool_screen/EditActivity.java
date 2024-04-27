package com.example.gallery.Edit_tool_screen;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.gallery.DatabaseHelper;
import com.example.gallery.Images_screen.ImageFragment;
import com.example.gallery.R;
import com.google.android.gms.vision.face.Face;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;

public class EditActivity extends AppCompatActivity {
    FragmentTransaction ft;
    SaveBackFragment fragmentSaveBack;
    EditFragment fragmentOptions;
    ImageFragment fragmentImage;
    boolean checkExistOptions = true;
    String img_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        fragmentSaveBack = SaveBackFragment.newInstance("SaveBack");
        fragmentImage = ImageFragment.newInstance("MyImage");
        fragmentOptions = EditFragment.newInstance("Options");

        Intent intent = getIntent();
        String selectedImage = intent.getStringExtra("SelectedImage");
        img_path = selectedImage;
        Bundle bundle = new Bundle();
        bundle.putString("selectedImage", selectedImage);
        fragmentImage.setArguments(bundle);

        ft = getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.SaveAndBack, fragmentSaveBack);
        ft.replace(R.id.Image, fragmentImage);
        ft.replace(R.id.AllOptions, fragmentOptions);

        ft.addToBackStack(null);
        ft.commit();

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this,onBackPressedCallback);
    }

    public void updateRotate(int value) {
        fragmentImage.executeRotate(value);
    }
    public void fastRotate(int value) { fragmentImage.executeFastRotate(value); }
    public void minus90deg() { fragmentImage.executeMinus90Deg(); }
    public void plus90deg() { fragmentImage.executePlus90Deg(); }
    public void cropTheImage() { fragmentImage.executeCropImage(); }
    public void startToZoom() {fragmentImage.executeZoom();}
    public void changeBrightness(int value) { fragmentImage.executeChangeBrightness(value); }
    public void saveChangeFilter() { fragmentImage.executeSaveChangeFilter(); }
    public void changeContrast(int value) { fragmentImage.executeChangeContrast(value); }
    public void changeBlur(int value) { fragmentImage.executeChangeBlur(value); }
    public void changeSepia(int value) { fragmentImage.executeChangeSepia(value); }
    public void changeGrayscale(int value) { fragmentImage.executeChangeGrayscale(value); }
    public void changeSharpen(int value) { fragmentImage.executeChangeSharpen(value); }
    public void updateColorSet(int index) { fragmentImage.executeUpdateColorSet(index); }
    public void setValuePercentage(int value) { fragmentImage.executeColorFilter(value); }



    public void addEditText() { fragmentImage.executeAddEditText(); }
    public void updateEditText(String strFontFamily, String strFontSize, boolean isItalic, boolean isBold, int textColor) {
        fragmentImage.updateEditText(strFontFamily, strFontSize, isItalic, isBold, textColor);
    }
    public void addTextToImage() { fragmentImage.executeAddTextToImage(); }
    public void saveImage() { fragmentImage.executeSaveImage(); }
    public void setCropOverlay() { fragmentImage.executeSetCropOverlay(); }
    public void cancelCropOverlay() { fragmentImage.executeCancelCropOverlay(); }
    public void setUpNormal() { fragmentImage.executeSetUpNormal(); }
    public SparseArray<Face> FacesDetection() { return fragmentImage.executeFacesDetection(); }
    public void extractFaces(SparseArray<Face> faces) { fragmentImage.extractFaceBitmaps(faces);}
    public void setUpHorizontalFlip() { fragmentImage.executeSetUpHorizontalFlip(); }
    public void setUpVerticalFlip() { fragmentImage.executeSetUpVerticalFlip(); }
    public void setRatio(int x, int y) { fragmentImage.executeSetRatio(x, y); }
    public void invisibleSave(String option) { fragmentSaveBack.executeInvisibleSave(option); }
    public boolean checkChange() { return fragmentImage.executeCheckChange(); }
    public void updateReplaceInfo() {
        checkExistOptions = false;
    }
    public void getBack() {
        if (checkExistOptions) {
            this.finish();
        } else {
            checkExistOptions = true;

            ft = getSupportFragmentManager().beginTransaction();

            fragmentSaveBack = SaveBackFragment.newInstance("SaveBack");
            fragmentOptions = EditFragment.newInstance("Options");

            ft.replace(R.id.SaveAndBack, fragmentSaveBack);
            ft.replace(R.id.AllOptions, fragmentOptions);

            fragmentImage.invisibleEditText();

            ft.addToBackStack(null);
            ft.commit();
        }
    }
    public void setOriginalImage() { fragmentImage.executeSetOriginalImage(); }
    public String extractText() { return fragmentImage.executeExtractText(); }
//    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void findSimular_images() {
        ArrayList<String> images = getAllShownImagesPath(this);
        ArrayList<String> result = new ArrayList<>();

        for (String img : images) {
            if (compare(img, img_path, 90)) {
                result.add(img);
            }
        }

        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent = new Intent(this, SimularResult.class);
        }
        intent.putStringArrayListExtra("ResultImages", result);
        startActivity(intent);
    }

    private ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri;
        Cursor cursor;
        int column_index_data;
        DatabaseHelper databaseHelper = new DatabaseHelper(activity);
        ArrayList<String> secureAlbums = databaseHelper.getAllAlbums();
        ArrayList<String> listOfAllImages = new ArrayList<>();
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.DATE_TAKEN};

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, MediaStore.Images.Media.DATE_TAKEN + " DESC");

        if (cursor != null) {
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

            while (cursor.moveToNext()) {
                String absolutePathOfImage = cursor.getString(column_index_data);
                String folderName = new File(absolutePathOfImage).getParentFile().getName();
                if (!secureAlbums.contains(folderName) && !absolutePathOfImage.equals(img_path)) {
                    listOfAllImages.add(absolutePathOfImage);
                }

            }

            cursor.close();
        }

        return listOfAllImages;
    }

    boolean compare(String img1, String img2, int threshold) {
        Mat mat1 = Imgcodecs.imread(img1);
        Mat mat2 = Imgcodecs.imread(img2);

        if (mat1.empty() || mat2.empty()) {
            return false;
        }

        Mat grayMat1 = new Mat();
        Mat grayMat2 = new Mat();

        Imgproc.cvtColor(mat1, grayMat1, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(mat2, grayMat2, Imgproc.COLOR_BGR2GRAY);

        Mat resizedMat1 = new Mat();
        Mat resizedMat2 = new Mat();
        Imgproc.resize(grayMat1, resizedMat1, new Size(64, 64));
        Imgproc.resize(grayMat2, resizedMat2, new Size(64, 64));

        MatOfFloat ranges = new MatOfFloat(0, 256);
        MatOfInt histSize = new MatOfInt(256);
        MatOfInt channels = new MatOfInt(0);
        Mat hist1 = new Mat();
        Mat hist2 = new Mat();
        ArrayList<Mat> images1 = new ArrayList<>();
        ArrayList<Mat> images2 = new ArrayList<>();
        images1.add(grayMat1);
        images2.add(grayMat2);
        Imgproc.calcHist(images1, channels, new Mat(), hist1, histSize, ranges);
        Imgproc.calcHist(images2, channels, new Mat(), hist2, histSize, ranges);

        Core.normalize(hist1, hist1, 0, 1, Core.NORM_MINMAX, -1, new Mat());
        Core.normalize(hist2, hist2, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        double similarity = Imgproc.compareHist(hist1, hist2, Imgproc.HISTCMP_INTERSECT);

        return similarity >= threshold;
    }
}
