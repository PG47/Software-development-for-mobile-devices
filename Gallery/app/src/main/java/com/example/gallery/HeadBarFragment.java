package com.example.gallery;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
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
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HeadBarFragment extends Fragment {
    MainActivity mainActivity;
    Context context = null;
    ImageButton add;
    ImageButton login;
    Uri imageUri;
    HeadBarOptions callback;
    private boolean isLoggedIn = false;

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

        add = layoutImage.findViewById(R.id.add_button);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        login = layoutImage.findViewById(R.id.login_google_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLoggedIn) {
                    callback.loginGoogle();
                }
                else {
                    callback.showCloudImages();
                }
            }
        });

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Uri uri = bundle.getParcelable("uri");
            if (uri != null) {
                Glide.with(requireContext())
                        .asBitmap()
                        .load(uri)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                                int widthLight = bitmap.getWidth();
                                int heightLight = bitmap.getHeight();

                                Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                                        Bitmap.Config.ARGB_8888);

                                Canvas canvas = new Canvas(output);
                                Paint paintColor = new Paint();
                                paintColor.setFlags(Paint.ANTI_ALIAS_FLAG);

                                RectF rectF = new RectF(new Rect(0, 0, widthLight, heightLight));

                                canvas.drawRoundRect(rectF, widthLight / 2, heightLight / 2, paintColor);

                                Paint paintImage = new Paint();
                                paintImage.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
                                canvas.drawBitmap(bitmap, 0, 0, paintImage);

                                login.setImageBitmap(output);
                                isLoggedIn = true;
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
            }

            Boolean reset = bundle.getBoolean("reset");
            if (reset) {
                isLoggedIn = false;
                login.setImageResource(R.drawable.ic_user_google_foreground);
            }
        }

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
