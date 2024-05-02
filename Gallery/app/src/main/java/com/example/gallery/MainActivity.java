package com.example.gallery;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.credentials.CredentialManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Handler;
import android.provider.MediaStore;


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
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.gallery.Album_screen.AlbumFragment;
import com.example.gallery.Detail_screen.DetailsActivity;
import com.example.gallery.Images_screen.ImagesFragment;
import com.example.gallery.Images_screen.SelectOptions;
import com.example.gallery.Map_screen.MapFragment;
import com.example.gallery.Search_screen.SearchFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.opencv.android.OpenCVLoader;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.Headers;
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
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;
    private boolean isLoggedIn = false;
    private String googleUserId = "";
    private final OkHttpClient client = new OkHttpClient();
    private boolean insideAlbum = false;
    private boolean insideSearch = false;
    private boolean isSelectionMode = false;
    private boolean isSecure = false;
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
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 123);
    }

    @Override
    public void showCloudImages() {
        Intent intent = new Intent(MainActivity.this, CloudActivity.class);
        intent.putExtra("id", googleUserId);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 123) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            verifyIdTokenOnServer(account);
        } catch (ApiException e) {
            Log.e("CRED", "Google sign in error", e);
        }
    }

    private void verifyIdTokenOnServer(GoogleSignInAccount account) {
        String idToken = account.getIdToken();
        assert idToken != null;

        RequestBody requestBody = new FormBody.Builder()
                .add("token", idToken)
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

                    isLoggedIn = true;

                    Headers responseHeaders = response.headers();
                    for (int i = 0; i < responseHeaders.size(); i++) {
                        Log.d("CRED", responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    assert response.body() != null;
                    googleUserId = response.body().string();
                    Log.d("CRED", googleUserId);

                    Uri uri = account.getPhotoUrl();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("uri", uri);

                    HeadBarFragment fragmentHeadBar = new HeadBarFragment();
                    fragmentHeadBar.setArguments(bundle);
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.head_bar, fragmentHeadBar);
                    ft.commit();
                } catch (IOException e) {
                    Log.d("CRED", "Error", e);
                }
            }
        });

        thread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(OpenCVLoader.initDebug()) {

        }

        if (checkPermission()) {
            // do later
        } else {
            requestPermission();
            checkCameraPermission();
            requestStoragePermission();
            loadImages();
        }

        f_headbar = HeadBarFragment.newInstance("first-headbar");
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.head_bar, f_headbar);
        //ft.addToBackStack(null); // Add transaction to the back stack
        ft.commit();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(getString(R.string.server_client_id))
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("logout", false)) {
            mGoogleSignInClient.signOut();
            isLoggedIn = false;

            Bundle bundle = new Bundle();
            bundle.putBoolean("reset", true);

            HeadBarFragment fragmentHeadBar = new HeadBarFragment();
            fragmentHeadBar.setArguments(bundle);
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.head_bar, fragmentHeadBar);
            ft.commit();
        }

        else {
            mGoogleSignInClient.silentSignIn()
                    .addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
                        @Override
                        public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                            handleSignInResult(task);
                        }
                    });
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
            } else if (itemId == R.id.cloud) {
                if (!isLoggedIn) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Error");
                    builder.setMessage("You must log in to your Google account to backup images to cloud.");
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) { }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    selectOptions.uploadCloud(googleUserId);
                }
                return true;
            }
            else if (itemId == R.id.secure) {
                if(!isSecure) {
                    selectOptions.secure();
                } else selectOptions.unlockSecure();
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


    private boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (imagesFragment.isSelectionMode == true) {
            imagesFragment.ExitSelection();
            return;
        }
        if (insideAlbum) {
            closeAlbum();
            return;
        }
        if (insideSearch) {
            closeSearch();
            return;
        }
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
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
    public void openAlbum(ArrayList<String> images, boolean secure, String album_name) {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(imagesFragment)
                .commit();
        isSecure = secure;

        MenuItem secureMenuItem = bottomSelectView.getMenu().findItem(R.id.secure);

        // Modify the icon and text based on the value of isSecure
        if (isSecure) {
            secureMenuItem.setIcon(R.mipmap.unclock_foreground); // Change to your unlock icon
            secureMenuItem.setTitle("Unlock");
            imagesFragment = new ImagesFragment(images,album_name);
        } else {
            imagesFragment = new ImagesFragment(images);
        }


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
