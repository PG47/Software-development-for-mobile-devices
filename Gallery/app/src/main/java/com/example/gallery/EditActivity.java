package com.example.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class EditActivity extends AppCompatActivity {
    FragmentTransaction ft;
    SaveBackFragment fragmentSaveBack;
    EditFragment fragmentOptions;
    ImageFragment fragmentImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        fragmentSaveBack = SaveBackFragment.newInstance("SaveBack");
        fragmentImage = ImageFragment.newInstance("MyImage");
        fragmentOptions = EditFragment.newInstance("Options");

        Intent intent = getIntent();
        String selectedImage = intent.getStringExtra("SelectedImage");
        Bundle bundle = new Bundle();
        bundle.putString("selectedImage", selectedImage);
        fragmentImage.setArguments(bundle);

        ft = getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.SaveAndBack, fragmentSaveBack);
        ft.replace(R.id.Image, fragmentImage);
        ft.replace(R.id.AllOptions, fragmentOptions);

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

    public void updateRotate(int value) {
        fragmentImage.executeRotate(value);
    }
    public void startToZoom() {fragmentImage.executeZoom();}
    public void changeBrightness(int value) { fragmentImage.executeChangeBrightness(value); }
    public void changeContrast(int value) { fragmentImage.executeChangeContrast(value); }
    public void addEditText() { fragmentImage.executeAddEditText(); }
    public void updateEditText(String strFontFamily, String strFontSize, boolean isItalic, boolean isBold, int textColor) {
        fragmentImage.updateEditText(strFontFamily, strFontSize, isItalic, isBold, textColor);
    }

    public void enableEditText() {
    }
}
