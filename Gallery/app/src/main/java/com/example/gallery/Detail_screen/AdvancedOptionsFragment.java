package com.example.gallery.Detail_screen;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.gallery.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdvancedOptionsFragment extends Fragment {
    DetailsActivity detailsActivity;
    Context context;
    FragmentTransaction transaction;
    BottomNavigationView bottomEditView;

    public static AdvancedOptionsFragment newInstance(String strArg) {
        AdvancedOptionsFragment fragment = new AdvancedOptionsFragment();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DetailsActivity) {
            detailsActivity = (DetailsActivity) context;
        } else {
            throw new IllegalStateException("EditActivity must implement callbacks");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                detailsActivity = (DetailsActivity) getActivity();
            }
        }
        catch (IllegalStateException e) {
            throw new IllegalStateException("EditActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ConstraintLayout layoutAdvancedOption = (ConstraintLayout) inflater.inflate(R.layout.fragment_advanced_option, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof DetailsActivity) {
                detailsActivity = (DetailsActivity) getActivity();
            } else {
                throw new IllegalStateException("EditActivity must implement callbacks");
            }
        }

        bottomEditView = layoutAdvancedOption.findViewById(R.id.advancedOptions);
        bottomEditView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.option1) {
                SupportAdvancedOptionsFragment supportAdvancedOptionsFragment = SupportAdvancedOptionsFragment.newInstance("Faces Detection");
                transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.toDoWith, supportAdvancedOptionsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                detailsActivity.FacesDetection();
                return true;
            } else if (itemId == R.id.option2) {
                SupportAdvancedOptionsFragment supportAdvancedOptionsFragment = SupportAdvancedOptionsFragment.newInstance("Text Extraction");
                transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.toDoWith, supportAdvancedOptionsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                detailsActivity.showCropOverlay();
                return true;
            } else if (itemId == R.id.option3) {
                detailsActivity.findSimular_images();
                return true;
            } else if (itemId == R.id.option4) {
                SupportAdvancedOptionsFragment supportAdvancedOptionsFragment = SupportAdvancedOptionsFragment.newInstance("Change Name");
                transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.toDoWith, supportAdvancedOptionsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            } else if (itemId == R.id.option5) {

                return true;
            }

            return false;
        });

        return layoutAdvancedOption;
    }
}
