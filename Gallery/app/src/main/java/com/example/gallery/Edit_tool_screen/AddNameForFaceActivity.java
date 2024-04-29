package com.example.gallery.Edit_tool_screen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.gallery.DatabaseHelper;
import com.example.gallery.R;

import java.util.ArrayList;
import java.util.List;

public class AddNameForFaceActivity extends AppCompatActivity {
    FragmentTransaction ft;
    SaveNameFragment saveNameFragment;
    GridFaceFragment gridFaceFragment;
    Bitmap[] listBitmaps;
    String[] listNames;
    String[] listPaths;
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
                listPaths = filePaths.toArray(new String[0]);
            }
            if (name != null) {
                listNames = name.toArray(new String[0]);
            }
        }

        saveNameFragment = SaveNameFragment.newInstance("save");
        gridFaceFragment = GridFaceFragment.newInstance("grid", listPaths, listNames);
        databaseHelper = new DatabaseHelper(this);


        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.SaveAndBack, saveNameFragment);
        ft.replace(R.id.FaceList, gridFaceFragment);

        ft.addToBackStack(null);
        ft.commit();
    }
    public void saveToDB() {
        String[] res = gridFaceFragment.getAllName();
        String[] res1 = gridFaceFragment.getAllImagePath();
        databaseHelper.saveFaceToDB(res, res1);
        this.finish();
    }
}