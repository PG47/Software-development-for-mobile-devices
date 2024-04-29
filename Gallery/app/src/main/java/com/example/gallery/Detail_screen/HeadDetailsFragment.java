package com.example.gallery.Detail_screen;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.gallery.R;

import java.io.IOException;

public class HeadDetailsFragment extends Fragment {
    DetailsActivity detailsActivity;
    Context context = null;
    ImageButton exit, addTag, addToAlbum, setWallpaper, advancedOption;
    String selectedImage;
    Bitmap originalBitmap;

    public static HeadDetailsFragment newInstance(String strArg) {
        HeadDetailsFragment fragment = new HeadDetailsFragment();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                detailsActivity = (DetailsActivity) context;
            }
        }
        catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DetailsActivity) {
            detailsActivity = (DetailsActivity) context;
        } else {
            throw new IllegalStateException("EditActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ConstraintLayout layoutImage = (ConstraintLayout) inflater.inflate(R.layout.header_details, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof DetailsActivity) {
                detailsActivity = (DetailsActivity) context;
            } else {
                throw new IllegalStateException("MainActivity must implement callbacks");
            }
        }

        selectedImage = getArguments().getString("selectedImage");
        originalBitmap = BitmapFactory.decodeFile(selectedImage);

        exit = (ImageButton) layoutImage.findViewById(R.id.getBackButton);
        addTag = (ImageButton) layoutImage.findViewById(R.id.component1);
        addToAlbum = (ImageButton) layoutImage.findViewById(R.id.component2);
        setWallpaper = (ImageButton) layoutImage.findViewById(R.id.component3);
        advancedOption = (ImageButton) layoutImage.findViewById(R.id.component4);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (detailsActivity != null) {
                    detailsActivity.finish();
                }
            }
        });
        addTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        addToAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        setWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View view1 = inflater.inflate(R.layout.dialog_wallpaper_confirmation, null);
                ImageView imageView = view1.findViewById(R.id.imageView);
                imageView.setImageBitmap(originalBitmap);
                builder.setView(view1);
                builder.setTitle("Set Wallpaper");
                builder.setMessage("Do you want to set this image as your wallpaper?");
                builder.setPositiveButton("Set Wallpaper", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
                        try {
                            wallpaperManager.setBitmap(originalBitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        advancedOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailsActivity.replaceAdvancedOptionFragment();
            }
        });
        return layoutImage;
    }
}
