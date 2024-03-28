package com.example.gallery;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ImagesFragment extends Fragment implements SelectOptions {
    ImageButton selectAll;
    Boolean active = false;
    ImageButton selectExit;
    private ArrayList<String> images;
    private boolean isSelectionMode;
    NavigationChange callback;
    ImageAdapter adapter;

    public ImagesFragment() {
        // Required empty public constructor
    }

    public void setSelectionMode(boolean st) {
        isSelectionMode = st;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callback = (NavigationChange) requireActivity();
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
                adapter.toggleSelection(position);
            }
        });

        gallery.setOnItemLongClickListener((parent, view, position, id) -> {
            if (!isSelectionMode) {
                active=false;
                callback.startSelection();
                selectAll.setVisibility(View.VISIBLE);
                selectExit.setVisibility(View.VISIBLE);
            }
            isSelectionMode = true;
            adapter.toggleSelection(position);
            return true;
        });

        selectAll = rootView.findViewById(R.id.select_all);
        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable icon = getResources().getDrawable(R.drawable.ic_select_all_foreground);
                if (!active) {
                    adapter.toggleSelectAll();
                    active=true;
                    icon.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                    selectAll.setImageDrawable(icon);
                } else {
                    adapter.toggleDeSelectAll();
                    active=false;
                    icon.clearColorFilter();
                    selectAll.setImageDrawable(icon);
                }

            }
        });

        selectExit = rootView.findViewById(R.id.select_exit);
        selectExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExitSelection();
            }
        });

        return rootView;
    }

    public class ImageAdapter extends BaseAdapter {
        private final Activity context;
        private final ArrayList<Integer> selectedPositions;

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

        public void toggleSelectAll() {
            selectedPositions.clear();
            for (int i = 0; i < images.size(); i++) {
                selectedPositions.add(i);
            }
            notifyDataSetChanged();
        }
        public void toggleDeSelectAll() {
            selectedPositions.clear();
            notifyDataSetChanged();
        }

        public void exitSelectionMode() {
            selectedPositions.clear();
            isSelectionMode = false;
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

        public void confirmDeleteSelections() {
            int count = selectedPositions.size();
            AlertDialog.Builder builder = getBuilder("Delete selected items?",
                    "This will delete " + count + " item(s) permanently.", new CallbackDialog() {
                        @Override
                        public void onPositiveClick() {
                            deleteSelections();
                        }
                    });
            builder.show();
        }

        public void deleteSelections() {
            String[] projection = { MediaStore.Images.Media._ID };

            String selection = MediaStore.Images.Media.DATA + " = ?";
            Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver contentResolver = getActivity().getContentResolver();

            for (int i = 0; i < selectedPositions.size(); i++) {
                String[] selectionArgs = new String[] { images.get(selectedPositions.get(i)) };

                Cursor cursor = contentResolver.query(queryUri, projection, selection, selectionArgs, null);

                assert cursor != null;
                int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

                if (cursor.moveToFirst()) {
                    long id = cursor.getLong(column_index_data);
                    Uri deleteUri = ContentUris.withAppendedId(queryUri, id);
                    contentResolver.delete(deleteUri, null, null);
                }

                cursor.close();
            }

            images = getAllShownImagesPath(context);
            notifyDataSetChanged();
            ExitSelection();
        }

        public void shareSelections() {
            ArrayList<Uri> selectedUris = new ArrayList<>();
            for (int i : selectedPositions) {
                Glide.with(requireContext())
                        .asBitmap()
                        .load(images.get(i))
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                try {
                                    File tempFile = File.createTempFile("image", ".jpg", context.getCacheDir());
                                    FileOutputStream outputStream = new FileOutputStream(tempFile);

                                    resource.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                    outputStream.flush();
                                    outputStream.close();

                                    Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".file-provider", tempFile);
                                    selectedUris.add(uri);

                                    if (selectedUris.size() == selectedPositions.size()) {
                                        startShareIntent(selectedUris);
                                    }
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {}
                        });
            }
        }

        public void startShareIntent(ArrayList<Uri> selectedUris) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Shared from Group 7 Gallery App!");
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, selectedUris);
            shareIntent.setType("*/*");

            try {
                startActivity(Intent.createChooser(shareIntent, "Share to..."));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(requireActivity(), "No Apps Available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public ImageAdapter getImageAdapter() {
        return adapter;
    }
    public void ExitSelection() {
        adapter.exitSelectionMode();
        callback.endSelection();
        selectAll.setVisibility(View.INVISIBLE);
        selectExit.setVisibility(View.INVISIBLE);
    }

    private interface CallbackDialog {
        void onPositiveClick();
    }

    @NonNull
    private AlertDialog.Builder getBuilder(String title, String message, CallbackDialog callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callback.onPositiveClick();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {}
        });
        return builder;
    }

    @Override
    public void share() {
        adapter.shareSelections();
    }

    @Override
    public void addAlbum() {

    }

    @Override
    public void secure() {

    }

    @Override
    public void delete() {
        adapter.confirmDeleteSelections();
    }
}