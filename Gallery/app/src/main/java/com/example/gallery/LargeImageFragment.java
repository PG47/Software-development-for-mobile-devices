package com.example.gallery;

import android.content.Context;
import android.content.Intent;
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

public class LargeImageFragment extends Fragment {
    MainActivity mainActivity;
    Context context = null;
    ImageButton getBack;
    ImageView selectedImage;

    public static LargeImageFragment newInstance(String strArg) {
        LargeImageFragment fragment = new LargeImageFragment();
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
                mainActivity = (MainActivity) getActivity();
            }
        }
        catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout layoutImage = (RelativeLayout)inflater.inflate(R.layout.fragment_large_image, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof MainActivity) {
                mainActivity = (MainActivity) getActivity();
            } else {
                throw new IllegalStateException("MainActivity must implement callbacks");
            }
        }

        String selectedImage = getArguments().getString("selectedImage");
        this.selectedImage = (ImageView) layoutImage.findViewById(R.id.imageSelected);
        Glide.with(context).load(selectedImage).centerCrop().into(this.selectedImage);

        getBack = (ImageButton) layoutImage.findViewById(R.id.getBackButton);
        getBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.finishAffinity();
                System.exit(0);
            }
        });

        return layoutImage;
    }
}