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

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class MainActivity extends AppCompatActivity implements NavigationChange {
    FragmentTransaction ft;
    HeadBarFragment f_headbar;
    BottomNavigationView bottomNavigationView;
    BottomNavigationView bottomSelectView;
    ImagesFragment imagesFragment;
    SelectOptions selectOptions;
    private boolean isSelectionMode = false;
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

        if (checkPermission()) {
            // do later
        } else {
            requestPermission();
            loadImages();
        }

        f_headbar = HeadBarFragment.newInstance("first-headbar");

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.head_bar, f_headbar);
        ft.addToBackStack(null); // Add transaction to the back stack
        ft.commit();

        imagesFragment = new ImagesFragment();
        AlbumFragment albumFragment = new AlbumFragment();
        MapFragment mapFragment = new MapFragment();
        SearchFragment searchFragment = new SearchFragment();

        selectOptions = (SelectOptions) imagesFragment;

        bottomSelectView = findViewById(R.id.selectToolbar);
        bottomSelectView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.share) {
                selectOptions.share();
                return true;
            } else if (itemId == R.id.add) {
                selectOptions.addAlbum();
                return true;
            } else if (itemId == R.id.secure) {
                selectOptions.secure();
                return true;
            } else if (itemId == R.id.delete) {
                selectOptions.delete();
                return true;
            }

            return false;
        });

        bottomNavigationView = findViewById(R.id.bottomToolbar);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.images) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFragment, imagesFragment)
                        .commit();
                return true;
            } else if (itemId == R.id.album) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFragment, albumFragment)
                        .commit();
                return true;
            } else if (itemId == R.id.map) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFragment, mapFragment)
                        .commit();
                return true;
            } else if (itemId == R.id.search) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFragment, searchFragment)
                        .commit();
                return true;
            }

            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.images);
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
                    loadImages();
                } else {
                    Log.d(tag,"onActivityResult: External Storage Permission is denied!");
                    Toast.makeText(MainActivity.this,"External Storage Permission is denied!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void loadImages() {
        ImagesFragment imagesFragment = new ImagesFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragment, imagesFragment)
                .commit();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Check if images fragment is not loaded yet
        /*if (bottomNavigationView.getSelectedItemId() == R.id.images) {
            ImagesFragment imagesFragment = (ImagesFragment) getSupportFragmentManager().findFragmentById(R.id.mainFragment);
            if (imagesFragment == null) {
                loadImages();
            }
        }*/
    }

    @Override
    public void onBackPressed() {
        if (imagesFragment != null) {
            imagesFragment.ExitSelection();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void startSelection() {
        bottomNavigationView.setVisibility(View.INVISIBLE);
        bottomSelectView.setVisibility(View.VISIBLE);
    }

    @Override
    public void endSelection() {
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomSelectView.setVisibility(View.INVISIBLE);
    }
}