package com.example.gallery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.example.gallery.R;
import java.io.File;
import java.io.IOException;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 611103;
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_MEDIA_IMAGES
            //,Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int PERMISSION_COUNT = 1;
    private static final String tag= "PERMISSION_TAG";
    private boolean isGalleryInitialized = false;
    private ListView listView;
    private GalleryAdapter galleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.Pic_list);
        galleryAdapter = new GalleryAdapter();

        // Request permissions if not granted
        if (!checkPermission()) {
            requestPermissions();
        } else {
            initializeGallery();
        }
    }

    private void CreateFolder(String name) {
        if(checkPermission()==false) {
            requestPermissions();
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/" + name);
        boolean folder_created = file.mkdir();
        if(folder_created) {
            Toast.makeText(this,"Folder test created!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"Failed creating folder!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            for (String permission : PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }
    }

    private void requestPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Log.d(tag,"Request permission: try");

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(),null);
                intent.setData(uri);
                storageActivitiyResultLauncher.launch(intent);
            } catch (Exception e) {
                Log.d(tag,"Request permission: catch", e);
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storageActivitiyResultLauncher.launch(intent);
            }
        } else {
            Log.d("PermissionDebug", "Requesting permissions...");
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSIONS);
        }



    }

    private ActivityResultLauncher<Intent> storageActivitiyResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    Log.d(tag,"onActivityResult");
                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                        if(Environment.isExternalStorageManager()) {
                            Log.d(tag,"onActivityResult: Manage external Storage Permission is granted!");
                            CreateFolder("Testfolder_project");
                        } else {
                            Log.d(tag,"onActivityResult: Manage external Storage Permission is denied!");
                            Toast.makeText(MainActivity.this,"Manage external Storage Permission is denied!", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                    }
                }
            }
    );

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("PermissionDebug", "onRequestPermissionsResult triggered");
        if (requestCode  == REQUEST_PERMISSIONS) {
            if(grantResults.length > 0) {
                boolean write = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if(write && read) {
                    Log.d(tag,"onActivityResult: External Storage Permission is granted!");
                    CreateFolder("Testfolder_project");
                } else {
                    Log.d(tag,"onActivityResult: External Storage Permission is denied!");
                    Toast.makeText(MainActivity.this,"External Storage Permission is denied!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void initializeGallery() {
        Log.d("GalleryDebug", "Initializing gallery...");
        final File imagesDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath());
        final File[] files = imagesDir.listFiles();
        final List<String> filesList = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                final String path = file.getAbsolutePath();
                if (path.endsWith(".jpg") || path.endsWith(".png")) {
                    filesList.add(path);
                }
            }
        }
        galleryAdapter.setData(filesList);
        listView.setAdapter(galleryAdapter);
    }

    final class GalleryAdapter extends BaseAdapter {
        private List<String> data = new ArrayList<>();

        void setData(List<String> data) {
            this.data.clear();
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("LifecycleDebug", "onResume() called");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Permission_denied()) {
            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
            return;
        }

        if (!isGalleryInitialized) {
            initializeGallery();
        }
    }


    @SuppressLint("NewApi")
    private boolean Permission_denied() {
        for (int i = 0; i < PERMISSION_COUNT; i++) {
            if (checkSelfPermission(PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                Log.d("PermissionDebug", PERMISSIONS[i] + " is denied");
                return true;
            } else {
                Log.d("PermissionDebug", PERMISSIONS[i] + " is accepted");
            }
        }
        return false;
    }


}
