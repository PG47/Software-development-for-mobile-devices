package com.example.gallery;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class AlbumFragment extends Fragment {
    private ArrayList<String> albums;
    AlbumAdapter adapter;

    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_album, container, false);
        GridView gallery = rootView.findViewById(R.id.albumsGrid);
        adapter = new AlbumAdapter(requireActivity());
        gallery.setAdapter(adapter);

        return rootView;
    }

    public class AlbumAdapter extends BaseAdapter {
        private final Activity context;

        public AlbumAdapter(Activity localContext) {
            context = localContext;
            albums = getAllShownAlbumsPath(context);
        }

        public int getCount() {
            return albums.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final View layoutView;

            if (convertView == null) {
                layoutView = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
            } else {
                layoutView = (View) convertView;
            }

            TextView albumName = layoutView.findViewById(R.id.albumName);
            albumName.setText(albums.get(position));

            ImageView albumImage = layoutView.findViewById(R.id.albumImage);
            Glide.with(context).load(R.drawable.ic_album_foreground).centerCrop().into(albumImage);

            return layoutView;
        }

        private ArrayList<String> getAllShownAlbumsPath(Activity activity) {
            Uri uri;
            Cursor cursor;
            int column_index_data;
            ArrayList<String> listOfAllAlbums = new ArrayList<>();
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String[] projection = { MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
            cursor = activity.getContentResolver().query(uri, projection, null,
                    null, null);

            assert cursor != null;
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            while (cursor.moveToNext()) {
                String absolutePathOfAlbum = cursor.getString(column_index_data);
                if (!listOfAllAlbums.contains(absolutePathOfAlbum)) {
                    listOfAllAlbums.add(absolutePathOfAlbum);
                }
            }

            cursor.close();

            return listOfAllAlbums;
        }
    }
}