package com.example.gallery;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AlbumFragment extends Fragment {
    private ArrayList<String> albums;
    private ArrayList<ArrayList<String>> albumsImages;
    AlbumAdapter adapter;
    NavigationAlbum openAlbum;
    DatabaseHelper databaseHelper;
    ArrayList<String> secure_albums;

    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openAlbum = (NavigationAlbum) requireActivity();
        databaseHelper = new DatabaseHelper(requireActivity());
        secure_albums = databaseHelper.getAllAlbums();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_album, container, false);
        GridView gallery = rootView.findViewById(R.id.albumsGrid);
        adapter = new AlbumAdapter(requireActivity());
        gallery.setAdapter(adapter);

        gallery.setOnItemClickListener((parent, view, position, id) -> {
            if(secure_albums.contains(albums.get(position))) {
                final EditText input = new EditText(requireContext());
                AlertDialog.Builder builder = inputPasswordAlert(input,
                        "Enter password to view image:");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputPass = String.valueOf(input.getText());

                        String album_name = albums.get(position);
                        if (!databaseHelper.checkPass(album_name, inputPass)) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
                            alert.setTitle("Error").setMessage("Incorrect password.");
                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            alert.show();
                        }
                        else {
                            // Xử lý sự kiện khi click vào một item
                            openAlbum.openAlbum(albumsImages.get(position));
                        }
                    }
                });

                builder.show();
                return;
            }
            openAlbum.openAlbum(albumsImages.get(position));
        });

        return rootView;
    }

    public class AlbumAdapter extends BaseAdapter {
        private final Activity context;

        public AlbumAdapter(Activity localContext) {
            context = localContext;
            getAllShownAlbumsPath(context);
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



            ImageView albumImage = layoutView.findViewById(R.id.albumImage);
            if(secure_albums.contains(albums.get(position))) {
                albumImage.setBackgroundResource(R.drawable.selected_image_background);
                Glide.with(context).load(R.drawable.ic_lock_foreground).centerCrop().into(albumImage);
                albumName.setText(albums.get(position) + "(secure)");
            } else {
                albumName.setText(albums.get(position));
                Glide.with(context).load(albumsImages.get(position).get(0)).centerCrop().into(albumImage);
            }
            return layoutView;
        }

        private void getAllShownAlbumsPath(Activity activity) {
            Uri uri;
            Cursor cursor;
            int column_index_name, column_index_id, column_index_data;

            ArrayList<Long> albumsID = new ArrayList<>();
            albums = new ArrayList<>();
            albumsImages = new ArrayList<>();

            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String[] projection = { MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.DATA };
            cursor = activity.getContentResolver().query(uri, projection, null,
                    null, null);

            assert cursor != null;
            column_index_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            column_index_id = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            while (cursor.moveToNext()) {
                String absolutePathOfAlbum = cursor.getString(column_index_name);
                Long albumID = cursor.getLong(column_index_id);

                if (!albumsID.contains(albumID)) {
                    albumsID.add(albumID);
                    albums.add(absolutePathOfAlbum);
                    albumsImages.add(new ArrayList<>());
                }

                int index = albumsID.indexOf(albumID);

                String imagePath = cursor.getString(column_index_data);
                // imageUri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + imageID);
                albumsImages.get(index).add(imagePath);
            }

            cursor.close();
        }
    }

    public AlertDialog.Builder inputPasswordAlert(EditText input, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title);

        input.setTextSize(40);
        input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(4);
        input.setFilters(filterArray);

        builder.setView(input);
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder;
    }
}