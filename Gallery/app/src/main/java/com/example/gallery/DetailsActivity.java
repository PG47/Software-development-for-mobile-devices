package com.example.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {
    FragmentTransaction ft;
    HeadDetailsFragment headDetailsFragment;
    LargeImageFragment fragmentImage;
    String img_path;
    OptionFragment fragmentOption;
    Boolean optionsHidden;
    Boolean showAdvancedOptions = false;

    private OnImageChangeListener onImageNewChangeListener;

    public void setOnImageChangeListener(OnImageChangeListener onImageChangeListener) {
        this.onImageNewChangeListener = onImageChangeListener;
    }

    public interface OnImageChangeListener {
        void onChange();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        onImageNewChangeListener = getIntent().getParcelableExtra("ImageChangeListener");

        headDetailsFragment = HeadDetailsFragment.newInstance("header");
        fragmentImage = LargeImageFragment.newInstance("image");
        fragmentOption = OptionFragment.newInstance("option");

        Intent intent = getIntent();
        img_path = intent.getStringExtra("SelectedImage");
        Bundle bundle = new Bundle();
        bundle.putString("selectedImage", img_path);


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

        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.detailsLayout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                if (!optionsHidden) {
                    ft.hide(headDetailsFragment).hide(fragmentOption);
                }
                else {
                    ft.show(headDetailsFragment).show(fragmentOption);
                }
                ft.commit();
                optionsHidden = !optionsHidden;
            }
        });

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

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void findSimular_images() {
        ArrayList<String> images = getAllShownImagesPath(this);
        ArrayList<String> result = null;
        for (String img:images){
            if(compare(img,img_path, 20)) {
                result.add(img);
            }
        }

        Intent intent = new Intent(this, SimularResult.class);
        intent.putExtra("ResultImages", result);
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
        Bitmap bitmap1 = BitmapFactory.decodeFile(img1);
        Bitmap bitmap2 = BitmapFactory.decodeFile(img2);

        // Check if bitmaps were loaded successfully
        if (bitmap1 == null || bitmap2 == null) {
            return false; // Images couldn't be loaded
        }

        // Call the original method to compare the bitmaps
        return areImagesSimilar(bitmap1, bitmap2, threshold);
    }

    private boolean areImagesSimilar(Bitmap bitmap1, Bitmap bitmap2, int threshold) {
        // Convert Bitmaps to OpenCV Mat objects
        Mat mat1 = new Mat();
        Mat mat2 = new Mat();
        Utils.bitmapToMat(bitmap1, mat1);
        Utils.bitmapToMat(bitmap2, mat2);

        // Resize images for faster processing (optional)
        Size size = new Size(400, 300);
        Imgproc.resize(mat1, mat1, size);
        Imgproc.resize(mat2, mat2, size);

        // Extract keypoints and descriptors using ORB
        ORB orb = ORB.create();
        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
        Mat descriptors1 = new Mat();
        Mat descriptors2 = new Mat();
        orb.detectAndCompute(mat1, new Mat(), keypoints1, descriptors1);
        orb.detectAndCompute(mat2, new Mat(), keypoints2, descriptors2);

        // Match descriptors using a DescriptorMatcher (here we use BruteForce)
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        MatOfDMatch matches = new MatOfDMatch();
        matcher.match(descriptors1, descriptors2, matches);

        // Count the number of good matches based on a threshold
        int goodMatches = 0;
        for (DMatch match : matches.toList()) {
            if (match.distance <= threshold) {
                goodMatches++;
            }
        }

        // Decide similarity based on the number of good matches
        return goodMatches >= threshold;
    }

}
