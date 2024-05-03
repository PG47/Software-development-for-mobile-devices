package com.example.gallery.Detail_screen;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;

import com.example.gallery.DatabaseHelper;
import com.example.gallery.Edit_tool_screen.SimularResult;
import com.example.gallery.R;

import java.io.File;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class DetailsActivity extends AppCompatActivity implements LargeImageFragment.OnImageChangeListener {
    FragmentTransaction ft;
    HeadDetailsFragment headDetailsFragment;
    LargeImageFragment fragmentImage;
    String img_path;
    OptionFragment fragmentOption;
    Boolean optionsHidden;
    Boolean showAdvancedOptions = false;
    private ArrayList<String> images;

    private OnImageChangeListener onImageNewChangeListener;

    public interface OnImageChangeListener {
        void onChange();
    }

    @Override
    public void onImageChanged(String newImagePath) {
        fragmentOption.OnChangeImage(newImagePath);
    }

    @Override
    public void onClickhide() {
        ft = getSupportFragmentManager().beginTransaction();
        if (!optionsHidden) {
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            ft.hide(headDetailsFragment);
            ft.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
            ft.hide(fragmentOption);
        }
        else {
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

            ft.show(headDetailsFragment);
            ft.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
            ft.show(fragmentOption);
        }
        ft.commit();
        optionsHidden = !optionsHidden;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        onImageNewChangeListener = getIntent().getParcelableExtra("ImageChangeListener");

        headDetailsFragment = HeadDetailsFragment.newInstance("header");
        fragmentImage = LargeImageFragment.newInstance("image");
        fragmentImage.setOnImageChangeListener(this);
        fragmentOption = OptionFragment.newInstance("option");

        Intent intent = getIntent();
        img_path = intent.getStringExtra("SelectedImage");
        ArrayList<String> images = intent.getStringArrayListExtra("ImageList"); // Receive the list of images

        Bundle bundle = new Bundle();
        bundle.putString("selectedImage", img_path);
        bundle.putStringArrayList("imageList", images);


        headDetailsFragment.setArguments(bundle);
        fragmentImage.setArguments(bundle);
        fragmentOption.setArguments(bundle);

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.headerImage, headDetailsFragment);
        ft.addToBackStack(null); // Add transaction to the back stack
        ft.commit();

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.detailsImage, fragmentImage);
        ft.addToBackStack(null); // Add transaction to the back stack
        ft.commit();

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.toDoWith, fragmentOption);
        ft.addToBackStack(null); // Add transaction to the back stack
        ft.commit();

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this,onBackPressedCallback);

        optionsHidden = false;

        fragmentOption.setOnImageDeleteListener(new OptionFragment.OnImageDeleteListener() {
            @Override
            public void onImageDeleted() {
                if(onImageNewChangeListener != null) {
                    onImageNewChangeListener.onChange();
                }
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }

    public void updateSelectedImage(String selectedImage) {
        img_path = selectedImage;
        headDetailsFragment.updateSelectedImage(img_path);
    }
    public void replaceAdvancedOptionFragment() {
        if (showAdvancedOptions == false) {
            AdvancedOptionsFragment advancedOptionsFragment = AdvancedOptionsFragment.newInstance("AdvancedOptions");
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.toDoWith, advancedOptionsFragment);
            ft.addToBackStack(null);
            ft.commit();
            showAdvancedOptions = true;
        } else {
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.toDoWith, fragmentOption);
            ft.addToBackStack(null);
            ft.commit();
            showAdvancedOptions = false;
        }
    }

    public void showCropOverlay() { fragmentImage.executeShowCropOverlay(); }
    public String extractText() { return fragmentImage.executeExtractText(); }
    public void FacesDetection() { fragmentImage.executeFacesDetection(); }
    public String getCurrentName() { return fragmentImage.executeGetCurrentName(); }
    public void setNewNameForImage(String name) { 
        boolean res = fragmentImage.executeSetNewNameForImage(name);
        if (res) {
            Toast.makeText(this, "Change successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void findSimular_images() {
        ArrayList<String> images = getAllShownImagesPath(this);
        ArrayList<String> result = new ArrayList<>(); // Initialize the result ArrayList

        for (String img : images) {
            if (compare(img, img_path, 90)) { // Assuming img_path is the path of the image to compare with
                result.add(img);
            }
        }

        Intent intent = new Intent(this, SimularResult.class);
        intent.putStringArrayListExtra("ResultImages", result); // Use putStringArrayListExtra for ArrayList<String>
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
                // Extract the folder name from the absolute path
                String folderName = new File(absolutePathOfImage).getParentFile().getName();
                // Check if the folder name is not in any of the secure albums
                if (!secureAlbums.contains(folderName) && !absolutePathOfImage.equals(img_path)) {
                    listOfAllImages.add(absolutePathOfImage);
                }

            }

            cursor.close();
        }

        return listOfAllImages;
    }

    boolean compare(String img1, String img2, int threshold) {
        // Load images from file paths
        Mat mat1 = Imgcodecs.imread(img1);
        Mat mat2 = Imgcodecs.imread(img2);

        // Check if images were loaded successfully
        if (mat1.empty() || mat2.empty()) {
            return false; // Images couldn't be loaded
        }

        // Convert images to grayscale
        Mat grayMat1 = new Mat();
        Mat grayMat2 = new Mat();

        Imgproc.cvtColor(mat1, grayMat1, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(mat2, grayMat2, Imgproc.COLOR_BGR2GRAY);

        // Resize images to a fixed size (if needed)
        Mat resizedMat1 = new Mat();
        Mat resizedMat2 = new Mat();
        Imgproc.resize(grayMat1, resizedMat1, new Size(64, 64)); // Resize to match the size used in hashing
        Imgproc.resize(grayMat2, resizedMat2, new Size(64, 64)); // Resize to match the size used in hashing

        // Compute histograms
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

        // Normalize histograms
        Core.normalize(hist1, hist1, 0, 1, Core.NORM_MINMAX, -1, new Mat());
        Core.normalize(hist2, hist2, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        // Compute histogram intersection similarity
        double similarity = Imgproc.compareHist(hist1, hist2, Imgproc.HISTCMP_INTERSECT);

        // Check if similarity meets the threshold
        return similarity >= threshold;
    }

}
