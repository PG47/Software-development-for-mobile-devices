package com.example.gallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

public class DetailsActivity extends AppCompatActivity {
    FragmentTransaction ft;
    LargeImageFragment fragmentImage;
    OptionFragment fragmentOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        fragmentImage = LargeImageFragment.newInstance("image");
        fragmentOption = OptionFragment.newInstance("option");

        Intent intent = getIntent();
        String selectedImage = intent.getStringExtra("SelectedImage");
        Bundle bundle = new Bundle();
        bundle.putString("selectedImage", selectedImage);
        fragmentImage.setArguments(bundle);

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
    }
}
