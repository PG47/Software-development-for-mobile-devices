package com.example.gallery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
    public void onResume() {
        super.onResume();
        // Reload the list of images if needed
        if (adapter != null) {
            adapter.notifyDataSetChanged(); // Notify the adapter that the data set has changed
        }
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
            if (isSelectionMode) {
                imageView.setBackgroundResource(R.drawable.selected_image_background);
            }else {
                // Otherwise, set transparent background
                imageView.setBackgroundResource(android.R.color.transparent);
            }
            if (selectedPositions.contains(position)) {
                // If it's in selected positions, set background with green color
                imageView.setBackgroundResource(R.drawable.selected_green_image_background);
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

        private ArrayList<String> getAllAlbums() {
            ArrayList<String> albumNames = new ArrayList<>();

            // Query the device's media store for the list of albums
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
            String orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC";

            try (Cursor cursor = requireContext().getContentResolver().query(uri, projection, null, null, orderBy)) {
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String albumName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                        if (!albumNames.contains(albumName)) {
                            albumNames.add(albumName);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return albumNames;
        }
        public void add_to_Album() {
            ArrayList<String> albumNames = getAllAlbums();

            // Convert ArrayList<String> to String array
            String[] albumsArray = albumNames.toArray(new String[0]);

            // Create a dialog to act as the popup menu
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Choose Album");

            // Add albums to the list dynamically
            builder.setItems(albumsArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String selectedAlbum = albumNames.get(which);
                    moveImagesToAlbum(selectedAlbum); // Call moveImagesToAlbum with the selected album name
                }
            });

            // Show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();

            // Set dialog position to center
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = dialog.getWindow();
            if (window != null) {
                lp.copyFrom(window.getAttributes());
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.CENTER;
                window.setAttributes(lp);
            }
        }

        private void moveImagesToAlbum(String albumName) {
            // Get the directory path of the target album
            File albumDir = new File(Environment.getExternalStorageDirectory(), "DCIM/" + albumName);

            if (!albumDir.exists()) {
                // Create the target album directory if it doesn't exist
                if (!albumDir.mkdirs()) {
                    // If directory creation fails, show an error toast and return
                    Toast.makeText(requireContext(), "Failed to create album directory", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Move selected images to the target album directory
            for (int i : selectedPositions) {
                // Get the source file
                File sourceFile = new File(images.get(i));

                // Get the destination file path
                String destinationFilePath = albumDir.getPath() + "/" + sourceFile.getName();

                // Create the destination file
                File destinationFile = new File(destinationFilePath);

                try {
                    // Perform the file move operation
                    if (sourceFile.renameTo(destinationFile)) {
                        // If move operation is successful, update the gallery database
                        MediaScannerConnection.scanFile(requireContext(), new String[]{destinationFilePath}, null, null);
                    } else {
                        // If move operation fails, show an error toast
                        Toast.makeText(requireContext(), "Failed to move image: " + sourceFile.getName(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // If an exception occurs during the move operation, show an error toast
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Error moving image: " + sourceFile.getName(), Toast.LENGTH_SHORT).show();
                }
            }

            // Exit selection mode after moving images
            ExitSelection();

            // Notify user about successful move
            Toast.makeText(requireContext(), "Images moved to album: " + albumName, Toast.LENGTH_SHORT).show();
        }


        // Helper method to get album ID based on the album name
        private boolean CheckAlbum(String albumName) {
            String[] projection = { MediaStore.Images.Media.BUCKET_ID };
            String selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + "=?";
            String[] selectionArgs = { albumName };
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            Cursor cursor = requireActivity().getContentResolver().query(uri, projection, selection, selectionArgs, null);

            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                        if (columnIndex != -1) {
                            return true;
                        } else {
                            // Log an error if the column index is -1
                            Log.e("ImagesFragment", "Column index for BUCKET_ID is -1");
                        }
                    }
                } finally {
                    cursor.close();
                }
            } else {
                // Log an error if the cursor is null
                Log.e("ImagesFragment", "Cursor is null");
            }
            return false;
        }

        public void add_to_new_Album() {
            // Create an EditText view for user input
            final EditText input = new EditText(requireContext());

            // Create an AlertDialog builder
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Create New Album");
            builder.setMessage("Enter the name for the new album:");

            // Add the EditText view to the dialog
            builder.setView(input);

            // Set positive button for user confirmation
            builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String albumName = input.getText().toString().trim();
                    if (!albumName.isEmpty()) {
                        if (!CheckAlbum(albumName)) {
                            // Call method to add images to the new album with the provided name
                            addImagesToNewAlbum(albumName);
                        } else {
                            // Show error toast if album already exists
                            Toast.makeText(requireContext(), "Album already exists", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Show error toast if album name is empty
                        Toast.makeText(requireContext(), "Album name cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Set negative button for cancel action
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Show the dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        private void addImagesToNewAlbum(String albumName) {
            // Create a directory in the DCIM folder with the provided album name
            File albumDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), albumName);
            if (albumDir.mkdirs()) {
                // If directory creation is successful, show a success toast
                Toast.makeText(requireContext(), "Folder Created!\n" + albumDir.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } else {
                // If directory creation fails, show an error toast
                Toast.makeText(requireContext(), "Failed to create folder!", Toast.LENGTH_SHORT).show();
            }

            // Now move all the selected images to the new album
            moveImagesToAlbum(albumName);
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
        adapter.add_to_Album();
    }

    @Override
    public void newAlbum() {
        adapter.add_to_new_Album();
    }

    @Override
    public void secure() {

    }

    @Override
    public void delete() {
        adapter.confirmDeleteSelections();
    }
}