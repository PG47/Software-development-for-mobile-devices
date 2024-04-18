package com.example.gallery;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CropMenuFragment extends Fragment {
    EditActivity editActivity;
    Context context;
    BottomNavigationView bottomCropNav;
    BottomNavigationView bottomAspectRatio;
    public static CropMenuFragment newInstance(String strArg) {
        CropMenuFragment fragment = new CropMenuFragment();
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EditActivity) {
            editActivity = (EditActivity) context;
        } else {
            throw new IllegalStateException("EditActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout cropOption = (RelativeLayout) inflater.inflate(R.layout.fragment_cropmenu, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof EditActivity) {
                editActivity = (EditActivity) getActivity();
            } else {
                throw new IllegalStateException("EditActivity must implement callbacks");
            }
        }

        bottomCropNav = cropOption.findViewById(R.id.editToolbar);
        bottomAspectRatio = cropOption.findViewById(R.id.aspectRatio);

        bottomCropNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.normal) {
                editActivity.setUpNormal();
                return true;
            } else if (itemId == R.id.fliphorizontally) {
                editActivity.setUpHorizontalFlip();
                return true;
            } else if (itemId == R.id.flipvertically) {
                editActivity.setUpVerticalFlip();
                return true;
            }

            return false;
        });
        bottomAspectRatio.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.op1) {
                editActivity.setRatio1();
                return true;
            } else if (itemId == R.id.op2) {
                editActivity.setRatio2();
                return true;
            } else if (itemId == R.id.op3) {
                editActivity.setRatio3();
                return true;
            } else if (itemId == R.id.op4) {
                editActivity.setRatio4();
                return true;
            } else if (itemId == R.id.op5) {
                editActivity.setRatio5();
                return true;
            }
            return false;
        });
        return cropOption;
    }
}
