package com.example.gallery;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class OptionFragment extends Fragment {
    DetailsActivity mainActivity;
    BottomNavigationView bottomOptionView;

    private ArrayList<String> images;

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
        RelativeLayout layoutOption = (RelativeLayout) inflater.inflate(R.layout.fragment_option, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof DetailsActivity) {
                mainActivity = (DetailsActivity) getActivity();
            } else {
                throw new IllegalStateException("MainActivity must implement callbacks");
            }
        }

        String selectedImage = getArguments().getString("selectedImage");

        bottomOptionView = layoutOption.findViewById(R.id.optionToolbar);
        bottomOptionView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.share) {

                return true;
            } else if (itemId == R.id.edit) {
                Intent intent = new Intent(requireContext(), EditActivity.class);
                intent.putExtra("SelectedImage", selectedImage);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.camera) {

                return true;
            } else if (itemId == R.id.delete) {
                confirmDeleteSelectedImage(selectedImage);
                return true;
            }

            return false;
        });

        return layoutOption;
    }

    private void confirmDeleteSelectedImage(String selectedImage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this image?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteSelectedImage(selectedImage);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

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
        }
        /*
        images = getAllShownImagesPath(requireContext());
        notifyDataSetChanged();
        */
    }
}
