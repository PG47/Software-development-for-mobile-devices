package com.example.gallery.Edit_tool_screen;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gallery.Images_screen.ImagesFragment;
import com.example.gallery.Images_screen.SelectOptions;
import com.example.gallery.NavigationAlbum;
import com.example.gallery.NavigationChange;
import com.example.gallery.NavigationSearch;
import com.example.gallery.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class SimularResult extends AppCompatActivity implements NavigationChange, NavigationAlbum, NavigationSearch {
    BottomNavigationView bottomSelectView;
    PopupMenu popupMenu;
    ImagesFragment imagesFragment;
    SelectOptions selectOptions;
    private boolean isSelectionMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_simular);

        Intent intent = getIntent();
        ArrayList<String> images = intent.getStringArrayListExtra("ResultImages");
        if (images != null) {
            imagesFragment = new ImagesFragment(images,false);
        }

        selectOptions = (SelectOptions) imagesFragment;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragment, imagesFragment)
                .commit();

        bottomSelectView = findViewById(R.id.selectToolbar);
        bottomSelectView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.share) {
                selectOptions.share();
                return true;
            } else if (itemId == R.id.add) {
                popupMenu = new PopupMenu(SimularResult.this, findViewById(R.id.add));
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

    }

    public void refreshImages() {
        if (imagesFragment != null && imagesFragment.adapter != null) {
            imagesFragment.adapter.reloadImages();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Check if images fragment is not loaded yet
        //refreshImages();
    }

    @Override
    public void onBackPressed() {
        if (imagesFragment.isSelectionMode == true) {
            imagesFragment.ExitSelection();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void startSelection() {
        bottomSelectView.setVisibility(View.VISIBLE);
    }

    @Override
    public void endSelection() {
        bottomSelectView.setVisibility(View.INVISIBLE);
    }


    @Override
    public void openSearch(String keyword) {

    }

    @Override
    public void openTags(ArrayList<String> img_path) {

    }

    @Override
    public void closeSearch() {

    }

    @Override
    public void openAlbum(ArrayList<String> images, boolean secure, String album_name) {

    }

    @Override
    public void closeAlbum() {

    }
}
