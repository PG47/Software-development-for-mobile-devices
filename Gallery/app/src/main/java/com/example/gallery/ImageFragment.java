package com.example.gallery;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import android.graphics.Matrix;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import com.github.chrisbanes.photoview.PhotoView;

public class ImageFragment extends Fragment {
    EditActivity editActivity;
    Context context;
    PhotoView myImage;
    float zoomLevel = (float) Math.sqrt(2);
    View theBorder;
    int imageHeight;

    public static ImageFragment newInstance(String strArg) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                editActivity = (EditActivity) getActivity();
            }
        }
        catch (IllegalStateException e) {
            throw new IllegalStateException("EditActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout layoutImage = (RelativeLayout)inflater.inflate(R.layout.fragment_image, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof EditActivity) {
                editActivity = (EditActivity) getActivity();
            } else {
                throw new IllegalStateException("EditActivity must implement callbacks");
            }
        }

        String selectedImage = getArguments().getString("selectedImage");
        myImage = (PhotoView) layoutImage.findViewById(R.id.image);
        theBorder = (View) layoutImage.findViewById(R.id.border);

        imageHeight = myImage.getHeight();
        if (selectedImage != null) {
            Glide.with(context).load(selectedImage).centerCrop().into(this.myImage);
        }

        return layoutImage;
    }

    public void executeRotate(int value) {
        myImage.setRotation(value - 45);
        float scaleFactor = calculateScaleFactor(value);
        myImage.setScaleX(scaleFactor);
        myImage.setScaleY(scaleFactor);
    }
    private float calculateScaleFactor(float angle) {
        if (angle > 45) {
            return (float) Math.sin(Math.toRadians(angle)) * zoomLevel;
        }
        return (float) Math.cos(Math.toRadians(angle)) * zoomLevel;
    }
}
