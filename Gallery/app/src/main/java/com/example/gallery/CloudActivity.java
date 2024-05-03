package com.example.gallery;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gallery.Album_screen.AlbumFragment;
import com.example.gallery.Images_screen.ImagesFragment;
import com.example.gallery.Images_screen.SelectOptions;
import com.example.gallery.Map_screen.MapFragment;
import com.example.gallery.Search_screen.SearchFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class CloudActivity extends AppCompatActivity implements NavigationChange, NavigationAlbum, NavigationSearch {
    private ArrayList<String> images;
    private ImagesFragment imagesFragment;
    private SelectOptions selectOptions;
    private BottomNavigationView bottomCloudView;
    private String googleUserId;
    private DatabaseHelper databaseHelper;

    private void reloadImagesUI(boolean replace) {
        if (replace) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(imagesFragment)
                    .commit();
        }
        else {
            getExistingCloudImages();
        }

        imagesFragment = new ImagesFragment(images);
        selectOptions = (SelectOptions) imagesFragment;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragment, imagesFragment)
                .commit();
    }

    private void getExistingCloudImages() {
        images.clear();

        Uri uri;
        Cursor cursor;
        int column_index_data;

        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.Images.Media.DATA };
        String selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " = ?";
        String[] selectionArgs = {"Firebase"};

        cursor = CloudActivity.this.getContentResolver().query(uri, projection, selection,
                selectionArgs, null);

        assert cursor != null;
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        while (cursor.moveToNext()) {
            String absolutePathOfImage = cursor.getString(column_index_data);
            images.add(absolutePathOfImage);
        }

        cursor.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud);

        images = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);

        Intent intentChange = getIntent();
        googleUserId = intentChange.getStringExtra("id");

//        imagesFragment = new ImagesFragment();
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.mainFragment, imagesFragment)
//                .commit();

        reloadImagesUI(false);

        bottomCloudView = findViewById(R.id.cloudToolbar);
        bottomCloudView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.reload) {
                images.clear();

                ProgressDialog dialog = new ProgressDialog(CloudActivity.this);
                dialog.setTitle("Downloading images...");
                dialog.setMessage("Please wait for all images to be downloaded.");
                dialog.setIndeterminate(false);
                dialog.setCancelable(false);
                dialog.show();

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(googleUserId);
                storageReference.listAll()
                        .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                            @Override
                            public void onSuccess(ListResult listResult) {
                                List<StorageReference> srList = listResult.getItems();
                                int size = srList.size();

                                if (size == 0) {
                                    Toast.makeText(CloudActivity.this, "There's no images to download.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                final int[] count = { 0 };

                                String path = "/storage/emulated/0/Pictures/Firebase";
                                File directory = new File(path);
                                if (directory.exists() && directory.isDirectory()) {
                                    boolean success = databaseHelper.deleteRecursive(directory);
                                }

                                for (StorageReference sr : srList) {
                                    long SIZE = 1024 * 1024 * 10;
                                    sr.getBytes(SIZE)
                                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                @Override
                                                public void onSuccess(byte[] bytes) {
                                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                    downloadImage(CloudActivity.this, bitmap);
                                                    count[0]++;
                                                    if (count[0] == size) {
                                                        dialog.dismiss();
                                                        reloadImagesUI(true);
                                                        Toast.makeText(CloudActivity.this, "Downloaded " + size + " image(s) to Firebase Album.", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                return true;
            } else if (itemId == R.id.logout) {
                Intent intent = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    intent = new Intent(CloudActivity.this, MainActivity.class);
                    intent.putExtra("logout", true);
                    startActivity(intent);
                }
                return true;
            }

            return false;
        });
    }

    private void downloadImage(Context context, Bitmap bitmap) {
        OutputStream fos;
        Uri uri = null;
        ContentResolver contentResolver = context.getContentResolver();

        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        if (uri == null) {
            uri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
        }

        String img = String.valueOf(Calendar.getInstance().getTimeInMillis());
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.DISPLAY_NAME, img + ".jpg");
        cv.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Firebase/");
        cv.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

        Uri finalUri = contentResolver.insert(uri, cv);
        assert finalUri != null;
        images.add(finalUri.toString());

        try {
            fos = contentResolver.openOutputStream(Objects.requireNonNull(finalUri));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            Objects.requireNonNull(fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startSelection() {

    }

    @Override
    public void endSelection() {

    }

    @Override
    public void openAlbum(ArrayList<String> images, boolean secure, String album_name) {

    }

    @Override
    public void closeAlbum() {

    }

    @Override
    public void openSearch(String keyword) {

    }

    @Override
    public void closeSearch() {

    }
}