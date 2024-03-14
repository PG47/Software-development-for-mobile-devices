package com.example.gallery;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.database.Cursor;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class MainActivity extends AppCompatActivity {
    FragmentTransaction ft;
    fragmentTop_headbar f_headbar;
    fragmentBot_bottombar f_botbar;
    private static final String tag = "PERMISSION_TAG";
    private static final int REQUEST_PERMISSIONS = 1234;
    private static final String [] PERMISSIONS = {
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.MANAGE_EXTERNAL_STORAGE
    };
    private static final int PERMISSION_COUNT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        f_headbar = fragmentTop_headbar.newInstance("first-blue");
        f_botbar = fragmentBot_bottombar.newInstance("first-red");

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.headbar, f_headbar);
        ft.addToBackStack(null); // Add transaction to the back stack
        ft.commit();

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.botom_bar, f_botbar);
        ft.addToBackStack(null); // Add transaction to the back stack
        ft.commit();

        if (checkPermission()) {
            GridView gallery = (GridView) findViewById(R.id.imagesGrid);
            gallery.setAdapter(new ImageAdapter(this));
        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Log.d(tag, "request permission: try...");
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                storageActivityResultLauncher.launch(intent);
            } catch (Exception e) {
                Log.d(tag, "request permission: catch", e);
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storageActivityResultLauncher.launch(intent);
            }
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS,
                    REQUEST_PERMISSIONS
            );
        }
    }

    public boolean checkPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int write = ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES);
            return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
        }
    }

    private final ActivityResultLauncher<Intent> storageActivityResultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                    if(Environment.isExternalStorageManager()) {
                        Log.d(tag,"onActivityResult: Manage external Storage Permission is granted!");
                    } else {
                        Log.d(tag,"onActivityResult: Manage external Storage Permission is denied!");
                        Toast.makeText(MainActivity.this,"Manage external Storage Permission is denied!", Toast.LENGTH_SHORT).show();
                    }
                } else {

                }
            }
        }
    );

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("PermissionDebug", "onRequestPermissionsResult triggered");
        if (requestCode  == REQUEST_PERMISSIONS) {
            if(grantResults.length > 0) {
                boolean write = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if(write && read) {
                    Log.d(tag,"onActivityResult: External Storage Permission is granted!");
                } else {
                    Log.d(tag,"onActivityResult: External Storage Permission is denied!");
                    Toast.makeText(MainActivity.this,"External Storage Permission is denied!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private ArrayList<String> images;

    private class ImageAdapter extends BaseAdapter {
        private Activity context;

        public ImageAdapter(Activity localContext) {
            context = localContext;
            images = getAllShownImagesPath(context);
        }

        public int getCount() {
            return images.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final ImageView imageView;

            if (convertView == null) {
                imageView = (ImageView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            } else {
                imageView = (ImageView) convertView;
            }

            Glide.with(MainActivity.this).load(images.get(position)).centerCrop().into(imageView);

            return imageView;
        }

        private ArrayList<String> getAllShownImagesPath(Activity activity) {
            Uri uri;
            Cursor cursor;
            int column_index_data, column_index_folder_name;
            ArrayList<String> listOfAllImages = new ArrayList<String>();
            String absolutePathOfImage = null;
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String[] projection = { MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

            cursor = activity.getContentResolver().query(uri, projection, null,
                    null, null);

            assert cursor != null;
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data);
                listOfAllImages.add(absolutePathOfImage);
            }

            return listOfAllImages;
        }
    }
}