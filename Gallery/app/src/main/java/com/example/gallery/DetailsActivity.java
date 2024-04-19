package com.example.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
        String selectedImage = intent.getStringExtra("SelectedImage");
        Bundle bundle = new Bundle();
        bundle.putString("selectedImage", selectedImage);

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

    public void findSimular_images() {
        ArrayList<String> images = getAllShownImagesPath(this);
        // Assuming you have the required data in 'images' ArrayList
        // Now, you can send this data back to ImagesFragment
        Intent resultIntent = new Intent();
        resultIntent.putStringArrayListExtra("Images", images);
        setResult(Activity.RESULT_OK, resultIntent);
        // Finish DetailsActivity, but keep it hidden
        moveTaskToBack(true); // Hide the activity without finishing
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true); // Hide the activity without finishing when back button is pressed
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
                if (!secureAlbums.contains(folderName)) {
                    listOfAllImages.add(absolutePathOfImage);
                }

            }

            cursor.close();
        }

        return listOfAllImages;
    }


}
