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

import android.provider.MediaStore;
import android.widget.ImageButton;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class MainActivity extends AppCompatActivity implements NavigationChange, NavigationAlbum {
    FragmentTransaction ft;
    public static final int REQUEST_IMAGE_CAPTURE = 3;
    HeadBarFragment f_headbar;
    BottomNavigationView bottomNavigationView;
    BottomNavigationView bottomSelectView;
    PopupMenu popupMenu;
    ImagesFragment imagesFragment;
    SelectOptions selectOptions;
    AlbumFragment albumFragment;
    private boolean insideAlbum = false;
    private boolean isSelectionMode = false;
    private static final String tag = "PERMISSION_TAG";
    private static final int REQUEST_PERMISSIONS = 1234;
    private static final String [] PERMISSIONS = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
            //,Manifest.permission.CAMERA
    };
    private static final int PERMISSION_COUNT = 2;

    private void checkCameraPermission() {
        if(ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkPermission()) {
            // do later
        } else {
            requestPermission();
            checkCameraPermission();
            loadImages();
        }

//        ImageButton sortButton = findViewById(R.id.sort_button);
//        sortButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Đảo ngược giá trị của biến sortOrder giữa 0 và 1
//                sortOrder = (sortOrder == 0) ? 1 : 0;
//                sortImagesByOldestDate(); // Gọi phương thức sắp xếp
//            }
//        });



        f_headbar = HeadBarFragment.newInstance("first-headbar");
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.head_bar, f_headbar);
        ft.addToBackStack(null); // Add transaction to the back stack
        ft.commit();

        imagesFragment = new ImagesFragment();
        albumFragment = new AlbumFragment();
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
                popupMenu = new PopupMenu(MainActivity.this, findViewById(R.id.add));
                popupMenu.getMenuInflater().inflate(R.menu.add_to_album_menu, popupMenu.getMenu());

                // Inside your setOnMenuItemClickListener method
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Handle menu item click
                        int itemId = item.getItemId(); // Get the ID of the clicked menu item

                        // Use if-else statements to handle menu item clicks
                        if (itemId == R.id.menu_add_to_exist_album) {
                            // Handle "Add to existing album" menu item click
                            Log.d("PopupMenu", "Add to existing album clicked");
                            selectOptions.addAlbum();
                            return true;
                        } else if (itemId == R.id.menu_add_to_new_album) {
                            // Handle "Create new album" menu item click
                            Log.d("PopupMenu", "Create new album clicked");
                            selectOptions.newAlbum();
                            return true;
                        } else {
                            return false;
                        }
                    };
                });

                popupMenu.setOnDismissListener(menu -> {Log.d("PopupMenu", "Dismissed");});
                popupMenu.show();
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
                        .remove(imagesFragment)
                        .commit();

                imagesFragment = new ImagesFragment();
                selectOptions = (SelectOptions) imagesFragment;

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
//    private int sortOrder = 0; // Biến này lưu trạng thái hiện tại của thứ tự sắp xếp
//
//    private void sortImagesByOldestDate() {
//        if (imagesFragment != null) {
//            // Lấy danh sách các tệp ảnh từ imagesFragment
//            ArrayList<File> images = imagesFragment.getImagesList();
//
//            // Kiểm tra xem danh sách có null hay không
//            if (images != null) {
//                // Sắp xếp danh sách hình ảnh theo thứ tự đã chọn
//                Collections.sort(images, new Comparator<File>() {
//                    @Override
//                    public int compare(File file1, File file2) {
//                        long lastModified1 = file1.lastModified();
//                        long lastModified2 = file2.lastModified();
//
//                        // So sánh theo trạng thái sắp xếp hiện tại
//                        if (sortOrder == 0) {
//                            return Long.compare(lastModified1, lastModified2); // Sắp xếp theo cũ nhất
//                        } else {
//                            return Long.compare(lastModified2, lastModified1); // Sắp xếp theo mới nhất
//                        }
//                    }
//                });
//
//                // Cập nhật giao diện sau khi sắp xếp
//                imagesFragment.updateImages(images);
//            }
//        }
//    }

    public void refreshImages() {
        if (imagesFragment != null && imagesFragment.adapter != null) {
            imagesFragment.adapter.reloadImages();
        }
    }
    public void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            cameraActivityResultLauncher.launch(takePictureIntent);
        } else {
            Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager() &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        } else {
            int write = ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES);
            int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            return write == PackageManager.PERMISSION_GRANTED &&
                    read == PackageManager.PERMISSION_GRANTED &&
                    camera == PackageManager.PERMISSION_GRANTED;
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

    private final ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        // Image captured successfully, notify ImagesFragment to refresh
                        if (imagesFragment != null) {
                            imagesFragment.adapter.reloadImages();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to capture image", Toast.LENGTH_SHORT).show();
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
        refreshImages();
        /*if (bottomNavigationView.getSelectedItemId() == R.id.images) {
            ImagesFragment imagesFragment = (ImagesFragment) getSupportFragmentManager().findFragmentById(R.id.mainFragment);
            if (imagesFragment == null) {
                loadImages();
            }
        }*/
    }

    @Override
    public void onBackPressed() {
        if (insideAlbum) {
            closeAlbum();
            return;
        }

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

    @Override
    public void openAlbum(ArrayList<String> images) {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(imagesFragment)
                .commit();

        imagesFragment = new ImagesFragment(images);
        selectOptions = (SelectOptions) imagesFragment;
        insideAlbum = true;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragment, imagesFragment)
                .commit();
    }

    @Override
    public void closeAlbum() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragment, albumFragment)
                .commit();
        insideAlbum = false;
    }
}
