package com.example.gallery;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FilterFragment extends Fragment {
    EditActivity editActivity;
    Context context;
    FragmentTransaction transaction;
    BottomNavigationView bottomEditView;

    public static FilterFragment newInstance(String strArg) {
        FilterFragment fragment = new FilterFragment();
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
        RelativeLayout layoutOption = (RelativeLayout)inflater.inflate(R.layout.fragment_filter, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof EditActivity) {
                editActivity = (EditActivity) getActivity();
            } else {
                throw new IllegalStateException("EditActivity must implement callbacks");
            }
        }

        bottomEditView = layoutOption.findViewById(R.id.filterToolbar);
        bottomEditView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.contrast) {
                ChangeFragment changeFragment = ChangeFragment.newInstance("Contrast");
                transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.AllOptions, changeFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            } else if (itemId == R.id.grayscale) {
                ChangeFragment changeFragment = ChangeFragment.newInstance("Grayscale");
                transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.AllOptions, changeFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            } else if (itemId == R.id.sepia) {
                ChangeFragment changeFragment = ChangeFragment.newInstance("Sepia");
                transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.AllOptions, changeFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            } else if (itemId == R.id.blur) {
                ChangeFragment changeFragment = ChangeFragment.newInstance("Blur");
                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.AllOptions, changeFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            } else if (itemId == R.id.sharpen) {
                ChangeFragment changeFragment = ChangeFragment.newInstance("Sharpen");
                transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.AllOptions, changeFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            }

            return false;
        });
        return layoutOption;
    }
}
