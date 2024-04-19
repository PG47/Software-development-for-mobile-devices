package com.example.gallery;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private ArrayList<String> images;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        images = new ArrayList<>();
        getAllImages();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        SupportMapFragment fragment = new SupportMapFragment();
        transaction.add(R.id.google_map, fragment);
        transaction.commit();

        fragment.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        for (String path : images) {
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(path);
            } catch (IOException e) {
                Log.d("GEO", "No Exif");
            }

            float[] latLong = new float[2];
            assert exif != null;
            boolean hasLatLong = exif.getLatLong(latLong);

            if (hasLatLong) {
                Glide.with(requireContext())
                        .asBitmap()
                        .load(path)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                Bitmap smallMarker = Bitmap.createScaledBitmap(resource, 150, 150, false);
                                LatLng latLng = new LatLng(latLong[0], latLong[1]);
                                googleMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Vietnam")
                                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) { }
                        });
            }
        }
    }

    private void getAllImages() {
        Uri uri;
        Cursor cursor;
        int column_index_data;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA };

        cursor = requireActivity().getContentResolver().query(uri, projection, null,
                null, null);

        assert cursor != null;
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while (cursor.moveToNext()) {
            String absolutePathOfImage = cursor.getString(column_index_data);
            images.add(absolutePathOfImage);
        }

        cursor.close();
    }
}