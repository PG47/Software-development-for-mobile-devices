package com.example.gallery;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.PopupMenu;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CloudActivity extends AppCompatActivity implements NavigationChange {
    private ImagesFragment imagesFragment;
    private BottomNavigationView bottomCloudView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud);

//        imagesFragment = new ImagesFragment();
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.mainFragment, imagesFragment)
//                .commit();

        bottomCloudView = findViewById(R.id.cloudToolbar);
        bottomCloudView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.reload) {

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

    @Override
    public void startSelection() {

    }

    @Override
    public void endSelection() {

    }
}