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
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EditFragment extends Fragment {
    EditActivity editActivity;
    Context context;
    FragmentTransaction transaction;
    BottomNavigationView bottomEditView;

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

        bottomEditView = layoutOption.findViewById(R.id.editToolbar);
        bottomEditView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.rotate) {
                RotateFragment rotateFragment = RotateFragment.newInstance("Rotate");
                transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.AllOptions, rotateFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            } else if (itemId == R.id.text) {
                AddTextFragment addTextFragment = AddTextFragment.newInstance("Text");
                transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.AllOptions, addTextFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            } else if (itemId == R.id.filter) {
                FilterFragment filterFragment = FilterFragment.newInstance("Filter");
                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.AllOptions, filterFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            } else if (itemId == R.id.brightness) {
                ChangeFragment changeFragment = ChangeFragment.newInstance("Brightness");
                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.AllOptions, changeFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            } else if (itemId == R.id.crop) {
                CropFragment cropFragment = CropFragment.newInstance("Crop");
                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.AllOptions, cropFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            }

            return false;
        });

//        brightness.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ChangeFragment changeFragment = ChangeFragment.newInstance("Brightness");
//                transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.AllOptions, changeFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
//            }
//        });
//
//        contrast.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ChangeFragment changeFragment = ChangeFragment.newInstance("Contrast");
//                transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.AllOptions, changeFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
//            }
//        });

        return layoutOption;
    }
}
