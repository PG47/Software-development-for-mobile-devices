package com.example.gallery;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ImagesFragment extends Fragment {
    private ArrayList<String> images;
    private boolean isSelectionMode;

    public ImagesFragment() {
        // Required empty public constructor
    }

    public void setSelectionMode(boolean st) {
        isSelectionMode=st;
    }
    ImageAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_images, container, false);
        GridView gallery = rootView.findViewById(R.id.imagesGrid);
        adapter = new ImageAdapter(requireActivity());
        gallery.setAdapter(adapter);
        gallery.setOnItemClickListener((parent, view, position, id) -> {
            if (!isSelectionMode) {
                // Handle regular item click
                Intent intent = new Intent(requireContext(), DetailsActivity.class);
                intent.putExtra("SelectedImage", images.get(position));
                startActivity(intent);
            } else {
                isSelectionMode = true;
                adapter.toggleSelection(position);
            }
        });
        gallery.setOnItemLongClickListener((parent, view, position, id) -> {
            isSelectionMode = true;
            adapter.toggleSelection(position);
            return true;
        });
        return rootView;
    }

    public class ImageAdapter extends BaseAdapter {
        private Activity context;
        private ArrayList<Integer> selectedPositions;

        public ImageAdapter(Activity localContext) {
            context = localContext;
            images = getAllShownImagesPath(context);
            selectedPositions = new ArrayList<>();
        }

        public int getCount() {
            return images.size();
        }

        public boolean Is_SelectionMode() {
            return isSelectionMode;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final ImageView imageView;

            if (convertView == null) {
                imageView = (ImageView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
            } else {
                imageView = (ImageView) convertView;
            }

            // Highlight selected items
            if (selectedPositions.contains(position)) {
                imageView.setBackgroundResource(R.drawable.selected_image_background);
            } else {
                imageView.setBackgroundResource(android.R.color.transparent);
            }

            Glide.with(context).load(images.get(position)).centerCrop().into(imageView);

            return imageView;
        }


        public void toggleSelection(int position) {
            if (selectedPositions.contains(position)) {
                selectedPositions.remove((Integer) position);
            } else {
                selectedPositions.add(position);
            }
            notifyDataSetChanged();
        }

        public void exitSelectionMode() {
            selectedPositions.clear();
            isSelectionMode=false;
            notifyDataSetChanged();
        }

        private ArrayList<String> getAllShownImagesPath(Activity activity) {
            Uri uri;
            Cursor cursor;
            int column_index_data;
            ArrayList<String> listOfAllImages = new ArrayList<>();
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String[] projection = { MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.DATE_TAKEN };

            cursor = activity.getContentResolver().query(uri, projection, null,
                    null, MediaStore.Images.Media.DATE_TAKEN + " DESC");

            assert cursor != null;
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

            while (cursor.moveToNext()) {
                String absolutePathOfImage = cursor.getString(column_index_data);
                listOfAllImages.add(absolutePathOfImage);
            }

            cursor.close();

            return listOfAllImages;
        }
    }

    public ImageAdapter getImageAdapter() {
        return adapter;
    }
    public void ExitSelection() {
        adapter.exitSelectionMode();
    }
}