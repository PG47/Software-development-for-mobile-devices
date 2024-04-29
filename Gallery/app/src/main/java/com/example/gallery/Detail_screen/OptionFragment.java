package com.example.gallery.Detail_screen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.gallery.Edit_tool_screen.EditActivity;
import com.example.gallery.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class OptionFragment extends Fragment {
    DetailsActivity mainActivity;
    BottomNavigationView bottomOptionView;
    String selectedImage;
    private OnImageDeleteListener onImageDeleteListener;

    public void setOnImageDeleteListener(OnImageDeleteListener listener) {
        this.onImageDeleteListener = listener;
    }


    public interface OnImageDeleteListener {
        void onImageDeleted();
    }

    public static OptionFragment newInstance(String strArg1) {
        OptionFragment fragment = new OptionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("arg1", strArg1);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mainActivity = (DetailsActivity) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ConstraintLayout layoutOption = (ConstraintLayout) inflater.inflate(R.layout.fragment_option, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof DetailsActivity) {
                mainActivity = (DetailsActivity) getActivity();
            } else {
                throw new IllegalStateException("MainActivity must implement callbacks");
            }
        }

        selectedImage = getArguments().getString("selectedImage");

        bottomOptionView = layoutOption.findViewById(R.id.optionToolbar);
        bottomOptionView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.share) {
                shareSelections(selectedImage);
            } else if (itemId == R.id.edit) {
                Intent intent = new Intent(requireContext(), EditActivity.class);
                intent.putExtra("SelectedImage", selectedImage);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.info) {
                showImageInfoDialog(selectedImage);
                return true;
            } else if (itemId == R.id.delete) {
                confirmDeleteSelectedImage(selectedImage);
                return true;
            }

            return false;
        });

        return layoutOption;
    }

    private void showImageInfoDialog(String selectedImage) {
        // Extract basic information
        File imageFile = new File(selectedImage);
        String name = imageFile.getName();
        String path = imageFile.getAbsolutePath();
        String size = formatSize(imageFile.length());
        String date = getModifiedDate(imageFile);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Image Information");
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_image_info, null);
        builder.setView(dialogView);

        TextView textName = dialogView.findViewById(R.id.text_name);
        TextView textPath = dialogView.findViewById(R.id.text_path);
        TextView textSize = dialogView.findViewById(R.id.text_size);
        TextView textDate = dialogView.findViewById(R.id.text_date);

        textName.setText(name);
        textPath.setText(path);
        textSize.setText(size);
        textDate.setText(date);

        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method to format file size in human-readable format
    private String formatSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return String.format(Locale.getDefault(), "%.1f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    // Method to get the last modified date of the file
    private String getModifiedDate(File file) {
        long lastModified = file.lastModified();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(lastModified));
    }

    private void confirmDeleteSelectedImage(String selectedImage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete 1 item?");
        builder.setMessage("This will delete 1 item permanently.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteSelectedImage(selectedImage);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public static ArrayList<String> getAllShownImagesPath(Activity activity) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.MediaColumns.DATA, MediaStore.Images.Media.DATE_TAKEN };
        Cursor cursor = activity.getContentResolver().query(uri, projection, null,
                null, MediaStore.Images.Media.DATE_TAKEN + " DESC");
        ArrayList<String> listOfAllImages = new ArrayList<>();
        if (cursor != null) {
            int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            while (cursor.moveToNext()) {
                String absolutePathOfImage = cursor.getString(column_index_data);
                listOfAllImages.add(absolutePathOfImage);
            }
            cursor.close();
        }
        return listOfAllImages;
    }

    // Các phương thức khác trong OptionFragment ở đây...

    private void deleteSelectedImage(String selectedImage) {
        ContentResolver contentResolver = requireActivity().getContentResolver();

        // Xác định đường dẫn của ảnh cần xóa
        String[] projection = {MediaStore.Images.Media._ID};
        String selection = MediaStore.Images.Media.DATA + " = ?";
        String[] selectionArgs = {selectedImage};
        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // Thực hiện truy vấn để lấy ID của ảnh cần xóa
        Cursor cursor = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
        if (cursor != null) {
            try {
                int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                if (cursor.moveToFirst()) {
                    long id = cursor.getLong(column_index_data);
                    Uri deleteUri = ContentUris.withAppendedId(queryUri, id);

                    // Xóa ảnh từ cơ sở dữ liệu media
                    contentResolver.delete(deleteUri, null, null);
                }
            } finally {
                cursor.close();
            }

            //Thông báo đã xóa ảnh
            if (onImageDeleteListener != null) {
                onImageDeleteListener.onImageDeleted();
            }
        }
    }

    public void shareSelections(String selectedImage) {
        Glide.with(requireContext())
                .asBitmap()
                .load(selectedImage)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            // Create a temporary file
                            File tempFile = File.createTempFile("image", ".jpg", requireContext().getCacheDir());
                            FileOutputStream outputStream = new FileOutputStream(tempFile);

                            // Compress bitmap into the temporary file
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            outputStream.flush();
                            outputStream.close();

                            // Get the URI of the temporary file
                            Uri uri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".file-provider", tempFile);

                            // Start share intent
                            startShareIntent(uri);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    public void startShareIntent(Uri selectedUri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Shared from Group 7 Gallery App!");
        shareIntent.putExtra(Intent.EXTRA_STREAM, selectedUri);
        shareIntent.setType("image/jpeg"); // Set the MIME type to image/jpeg for sharing images

        // Grant temporary permissions to the receiving app
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(Intent.createChooser(shareIntent, "Share to..."));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireActivity(), "No Apps Available", Toast.LENGTH_SHORT).show();
        }
    }

    public void OnChangeImage(String img) {
        selectedImage = img;
    }

}
