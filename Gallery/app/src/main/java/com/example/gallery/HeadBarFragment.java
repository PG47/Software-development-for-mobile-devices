package com.example.gallery;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HeadBarFragment extends Fragment {
    MainActivity mainActivity;
    Context context = null;
    ImageButton add;
    ImageButton people;
    Uri imageUri;
    HeadBarOptions callback;

    public static HeadBarFragment newInstance(String strArg) {
        HeadBarFragment fragment = new HeadBarFragment();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callback = (HeadBarOptions) requireActivity();
        try {
            context = getActivity();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mainActivity = (MainActivity) getActivity();
            }
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the correct layout
        ConstraintLayout layoutImage = (ConstraintLayout) inflater.inflate(R.layout.first_head_bar, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof MainActivity) {
                mainActivity = (MainActivity) getActivity(); // Use getActivity() to get the Activity
            } else {
                throw new IllegalStateException("MainActivity must implement callbacks");
            }
        }

        ImageButton add = layoutImage.findViewById(R.id.add_button);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        ImageButton login = layoutImage.findViewById(R.id.login_google_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.loginGoogle();
            }
        });

        return layoutImage;
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"new image");
        values.put(MediaStore.Images.Media.DESCRIPTION,"From the Gallery");

        imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePictureIntent, MainActivity.REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(getActivity(), "No camera app available", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            // Image captured, create the image file
            /* This one will crash if turn on222
            try {
                File photoFile = createImageFile();
                if (photoFile != null) {
                    imageUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", photoFile);
                    // Handle the imageUri as needed
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                // Handle the exception
            }*/
        }
    }

    private static final int PERMISSION_REQUEST_MANAGE_EXTERNAL_STORAGE = 1;

    private File createImageFile() throws IOException {
        // Check for permission to manage external storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                Environment.isExternalStorageManager()) {
            // Permission granted, continue with creating the image file
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";

            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Camera");
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }

            File imageFile = new File(storageDir, imageFileName + ".jpg");

            String currentPhotoPath = imageFile.getAbsolutePath();
            return imageFile;
        } else {
            // Permission not granted, request it
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, PERMISSION_REQUEST_MANAGE_EXTERNAL_STORAGE);
            return null;
        }
    }


}
