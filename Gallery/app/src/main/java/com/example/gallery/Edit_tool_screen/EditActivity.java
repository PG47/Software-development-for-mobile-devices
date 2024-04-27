package com.example.gallery.Edit_tool_screen;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.gallery.Images_screen.ImageFragment;
import com.example.gallery.R;
import com.google.android.gms.vision.face.Face;

public class EditActivity extends AppCompatActivity {
    FragmentTransaction ft;
    SaveBackFragment fragmentSaveBack;
    EditFragment fragmentOptions;
    ImageFragment fragmentImage;
    boolean checkExistOptions = true;

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
}
