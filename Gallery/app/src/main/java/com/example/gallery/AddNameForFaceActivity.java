package com.example.gallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AddNameForFaceActivity extends AppCompatActivity {
    FragmentTransaction ft;
    SaveNameFragment saveNameFragment;
    GridFaceFragment gridFaceFragment;
    Bitmap[] listBitmaps;
    String[] listNames;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_name_for_face);

        Intent intent = getIntent();
        if (intent != null) {
            ArrayList<String> filePaths = intent.getStringArrayListExtra("filePaths");
            ArrayList<String> name = intent.getStringArrayListExtra("expectedNames");
            if (filePaths != null) {
                List<Bitmap> bitmaps = loadBitmapsFromFilePaths(filePaths);
                listBitmaps = bitmaps.toArray(new Bitmap[0]);
            }
            if (name != null) {
                listNames = name.toArray(new String[0]);
            }
        }

        saveNameFragment = SaveNameFragment.newInstance("save");
        gridFaceFragment = GridFaceFragment.newInstance("grid", listBitmaps, listNames);
        databaseHelper = new DatabaseHelper(this);


        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.SaveAndBack, saveNameFragment);
        ft.replace(R.id.FaceList, gridFaceFragment);

        ft.addToBackStack(null);
        ft.commit();
    }
    private List<Bitmap> loadBitmapsFromFilePaths(List<String> filePaths) {
        List<Bitmap> bitmaps = new ArrayList<>();
        for (String filePath : filePaths) {
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            if (bitmap != null) {
                bitmaps.add(bitmap);
            }
        }
        return bitmaps;
    }
    public void saveToDB() {
        String[] res = gridFaceFragment.getAllName();
        Bitmap[] res1 = gridFaceFragment.getAllImage();
        databaseHelper.saveFaceToDB(res, res1);
        this.finish();
    }
}