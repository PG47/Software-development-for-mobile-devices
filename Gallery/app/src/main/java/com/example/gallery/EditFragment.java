package com.example.gallery;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class EditFragment extends Fragment {
    EditActivity editActivity;
    Context context;
    ImageButton rotate, addText, others, brightness, crop, contrast;
    FragmentTransaction transaction;

    public static EditFragment newInstance(String strArg) {
        EditFragment fragment = new EditFragment();
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
        RelativeLayout layoutOption = (RelativeLayout)inflater.inflate(R.layout.fragment_edit_option, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof EditActivity) {
                editActivity = (EditActivity) getActivity();
            } else {
                throw new IllegalStateException("EditActivity must implement callbacks");
            }
        }

        rotate = (ImageButton) layoutOption.findViewById(R.id.rotate);
        addText = (ImageButton) layoutOption.findViewById(R.id.text);
        others = (ImageButton) layoutOption.findViewById(R.id.others);
        brightness = (ImageButton) layoutOption.findViewById(R.id.brightness);
        crop = (ImageButton) layoutOption.findViewById(R.id.cropping);
        contrast = (ImageButton) layoutOption.findViewById(R.id.contrast);

        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RotateFragment rotateFragment = RotateFragment.newInstance("Rotate");
                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.AllOptions, rotateFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        addText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editActivity != null) {
                    editActivity.finish();
                }
            }
        });

        others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editActivity != null) {
                    editActivity.finish();
                }
            }
        });

        brightness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeFragment changeFragment = ChangeFragment.newInstance("Brightness");
                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.AllOptions, changeFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editActivity != null) {
                    editActivity.finish();
                }
            }
        });

        contrast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeFragment changeFragment = ChangeFragment.newInstance("Contrast");
                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.AllOptions, changeFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return layoutOption;
    }
}
