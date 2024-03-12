package com.example.gallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    FragmentTransaction ft;
    fragmentTop_headbar f_headbar;
    fragmentBot_bottombar f_botbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        f_headbar = fragmentTop_headbar.newInstance("first-blue");
        f_botbar = fragmentBot_bottombar.newInstance("first-red");

        // Begin a fragment transaction for FragmentLeft
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.headbar, f_headbar);
        ft.addToBackStack(null); // Add transaction to the back stack
        ft.commit();

        // Begin another fragment transaction for fragmentBot_bottombar
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.botom_bar, f_botbar);
        ft.addToBackStack(null); // Add transaction to the back stack
        ft.commit();
    }

    private static final int REQUEST_PERMISSIONS = 1234;
    private static final String [] PERMISSIONS = {
            Manifest.permission.READ_MEDIA_IMAGES
            //,Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int PERMISSION_COUNT = 1;

    @SuppressLint("NewApi")
    private boolean Permission_denied() {
        for(int i=0; i < PERMISSION_COUNT; i++) {
            if(checkSelfPermission(PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED)
                return true;
        }
        return false;
    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] Permissions, final int[] results) {
        super.onRequestPermissionsResult(requestCode,Permissions,results);
        if(requestCode == REQUEST_PERMISSIONS && results.length > 0) {
            if(Permission_denied()) {
                ((ActivityManager) Objects.requireNonNull(this.getSystemService(ACTIVITY_SERVICE)))
                        .clearApplicationUserData();
                recreate();
            } else {
                onResume();
            }
        }
    }

    private boolean isGalleryInitialized = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Permission_denied()) {
            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
            return;
        }

        if (!isGalleryInitialized) {
            final ListView listView = findViewById(R.id.Pic_list);
            final GalleryAdapter galleryAdapter = new GalleryAdapter();

            final File imagesDir = new File(String.valueOf(Environment.
                    getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)));
            final File[] files = imagesDir.listFiles();
            final int filesCount = files.length;

            final List<String> filesList = new ArrayList<>();
            for (int i = 0; i < filesCount; i++) {
                final String path = files[i].getAbsolutePath();
                if (path.endsWith(".jpg") || path.endsWith(".png")) {
                    filesList.add(path);
                }
            }

            galleryAdapter.setData(filesList);
            listView.setAdapter(galleryAdapter);

            isGalleryInitialized = true;
        }
    }

    final class GalleryAdapter extends BaseAdapter {
        private List<String> data = new ArrayList<>();

        void setData(List<String> data) {
            if (this.data.size() > 0) {
                data.clear();
            }

            this.data.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ImageView imageView;

            if (convertView == null) {
                imageView = (ImageView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            } else {
                imageView = (ImageView) convertView;
            }

            Glide.with(MainActivity.this).load(data.get(position)).centerCrop().into(imageView);

            return imageView;
        }
    }
}