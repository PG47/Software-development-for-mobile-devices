package com.example.gallery;

import static com.google.android.material.internal.ContextUtils.getActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class fragmentTop_headbar extends Fragment {
    MainActivity mainActivity;
    Context context = null;
    ImageButton add;
    ImageButton people;

    public static fragmentTop_headbar newInstance(String strArg) {
        fragmentTop_headbar fragment = new fragmentTop_headbar();
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
        // Inflate the correct layout
        ConstraintLayout layoutImage = (ConstraintLayout) inflater.inflate(R.layout.first_head_bar, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof MainActivity) {
                mainActivity = (MainActivity) getActivity(); // Use getActivity() to get the Activity
            } else {
                throw new IllegalStateException("MainActivity must implement callbacks");
            }
        }

        // Find your views by their IDs
        //ImageButton add = layoutImage.findViewById(R.id.getBackButton);
        //ImageButton people = layoutImage.findViewById(R.id.getBackButton);


        return layoutImage;
    }

}
