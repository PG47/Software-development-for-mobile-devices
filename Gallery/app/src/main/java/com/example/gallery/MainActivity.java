package com.example.gallery;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.CreateCredentialRequest;
import androidx.credentials.CreateCredentialResponse;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.PrepareGetCredentialResponse;
import androidx.credentials.exceptions.ClearCredentialException;
import androidx.credentials.exceptions.CreateCredentialException;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.database.Cursor;
import android.os.CancellationSignal;
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

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class MainActivity extends AppCompatActivity implements NavigationChange, NavigationAlbum, NavigationSearch, HeadBarOptions {
    FragmentTransaction ft;
    public static final int REQUEST_IMAGE_CAPTURE = 3;
    HeadBarFragment f_headbar;
    BottomNavigationView bottomNavigationView;
    BottomNavigationView bottomSelectView;
    PopupMenu popupMenu;
    ImagesFragment imagesFragment;
    SelectOptions selectOptions;
    AlbumFragment albumFragment;
    MapFragment mapFragment;
    SearchFragment searchFragment;
    CredentialManager credentialManager;
    private final OkHttpClient client = new OkHttpClient();
    private boolean insideAlbum = false;
    private boolean insideSearch = false;
    private boolean isSelectionMode = false;
    private static final String tag = "PERMISSION_TAG";
    private static final int REQUEST_PERMISSIONS = 1234;
    private static final String [] PERMISSIONS = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
            //,Manifest.permission.CAMERA
    };
    private static final int PERMISSION_COUNT = 2;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private void requestStoragePermission() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }
    private void checkCameraPermission() {
        if(ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    @Override
    public void loginGoogle() {
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.server_client_id))
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(
            this,
            request,
            null,
            getMainExecutor(),
            new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                @Override
                public void onResult(GetCredentialResponse result) {
                    handleSignIn(result);
                }

                @Override
                public void onError(GetCredentialException e) {
                    Log.e("CRED", "Credential failure", e);
                }
            }
        );
    }

    public void handleSignIn(GetCredentialResponse result) {
        Credential credential = result.getCredential();
        if (credential instanceof CustomCredential) {
            if (GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(credential.getType())) {
                GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(((CustomCredential) credential).getData());
                RequestBody requestBody = new FormBody.Builder()
                        .add("token", googleIdTokenCredential.getIdToken())
                        .build();

                Request request = new Request.Builder()
                        .url("http://royalmike.com/php/google/token_sign_in")
                        .post(requestBody)
                        .build();

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try (Response response = client.newCall(request).execute()) {
                            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                            Headers responseHeaders = response.headers();
                            for (int i = 0; i < responseHeaders.size(); i++) {
                                Log.d("CRED", responseHeaders.name(i) + ": " + responseHeaders.value(i));
                            }

                            assert response.body() != null;
                            Log.d("CRED", response.body().string());
                        } catch (IOException e) {
                            Log.d("CRED", "Error", e);
                        }
                    }
                });

                thread.start();
            } else {
                Log.e("CRED", "Unexpected type of credential");
            }
        } else {
            Log.e("CRED", "Unexpected type of credential");
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
            requestStoragePermission();
            loadImages();
        }

        credentialManager = CredentialManager.create(this);

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
        mapFragment = new MapFragment();
        searchFragment = new SearchFragment();

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
            int manage = ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE);
            int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES);
            int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            int save = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            return manage == PackageManager.PERMISSION_GRANTED &&
                    write == PackageManager.PERMISSION_GRANTED &&
                    read == PackageManager.PERMISSION_GRANTED &&
                    camera == PackageManager.PERMISSION_GRANTED &&
                    save == PackageManager.PERMISSION_GRANTED;
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
        if (insideSearch) {
            closeSearch();
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

    @Override
    public void openSearch(String keyword) {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(imagesFragment)
                .commit();

        ArrayList<String> imagePaths = new ArrayList<>();

        // Query the media store to get images with names containing the keyword
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media.DATA},
                MediaStore.Images.Media.DISPLAY_NAME + " LIKE ?",
                new String[]{"%" + keyword + "%"},
                null
        );

//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//                @SuppressLint("Range") String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//                imagePaths.add(imagePath);
//            }
//            cursor.close();
//        }
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                if (columnIndex >= 0) {
                    String imagePath = cursor.getString(columnIndex);
                    imagePaths.add(imagePath);
                } else {
                    Log.e("MainActivity", "Column index is -1");
                }
            }
            cursor.close();
        } else {
            Log.e("MainActivity", "Cursor is null");
        }

        // Query the media store to get album IDs with names containing the keyword
        Cursor albumCursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media.BUCKET_ID},
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " LIKE ?",
                new String[]{"%" + keyword + "%"},
                null
        );

        if (albumCursor != null) {
            while (albumCursor.moveToNext()) {
                int columnIndex = albumCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                if (columnIndex >= 0) {
                    String albumId = albumCursor.getString(columnIndex);
                    Cursor albumImagesCursor = getContentResolver().query(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            new String[]{MediaStore.Images.Media.DATA},
                            MediaStore.Images.Media.BUCKET_ID + " = ?",
                            new String[]{albumId},
                            null
                    );
                    if (albumImagesCursor != null) {
                        while (albumImagesCursor.moveToNext()) {
                            int imagePathIndex = albumImagesCursor.getColumnIndex(MediaStore.Images.Media.DATA);
                            if (imagePathIndex >= 0) {
                                String imagePath = albumImagesCursor.getString(imagePathIndex);
                                imagePaths.add(imagePath);
                            } else {
                                Log.e("MainActivity", "Image path index is -1");
                            }
                        }
                        albumImagesCursor.close();
                    } else {
                        Log.e("MainActivity", "Album images cursor is null");
                    }
                } else {
                    Log.e("MainActivity", "Column index is -1 for album ID");
                }
            }
            albumCursor.close();
        } else {
            Log.e("MainActivity", "Album cursor is null");
        }

        imagesFragment = new ImagesFragment(imagePaths, true);
        selectOptions = (SelectOptions) imagesFragment;
        insideSearch = true;

        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment, imagesFragment).commit();
    }


    @Override
    public void closeSearch(){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragment, searchFragment)
                .commit();
        insideSearch = false;
    }
}
