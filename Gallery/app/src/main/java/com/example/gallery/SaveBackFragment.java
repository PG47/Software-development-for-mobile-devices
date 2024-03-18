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

public class SaveBackFragment extends Fragment {
    EditActivity editActivity;
    Context context;
    ImageButton getBack, save;

    public static SaveBackFragment newInstance(String strArg) {
        SaveBackFragment fragment = new SaveBackFragment();
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
        RelativeLayout layoutSaveBack = (RelativeLayout)inflater.inflate(R.layout.fragment_save_back, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof EditActivity) {
                editActivity = (EditActivity) getActivity();
            } else {
                throw new IllegalStateException("EditActivity must implement callbacks");
            }
        }

        getBack = (ImageButton) layoutSaveBack.findViewById(R.id.backToDetails);
        save = (ImageButton) layoutSaveBack.findViewById(R.id.saveImage);

        getBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editActivity.finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editActivity.finish();
            }
        });

        return layoutSaveBack;
    }
}
