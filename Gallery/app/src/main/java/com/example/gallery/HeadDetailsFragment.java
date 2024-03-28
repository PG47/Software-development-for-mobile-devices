package com.example.gallery;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class HeadDetailsFragment extends Fragment {
    DetailsActivity mainActivity;
    Context context = null;
    ImageButton exit;

    public static HeadDetailsFragment newInstance(String strArg) {
        HeadDetailsFragment fragment = new HeadDetailsFragment();
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
                mainActivity = (DetailsActivity) context;
            }
        }
        catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ConstraintLayout layoutImage = (ConstraintLayout) inflater.inflate(R.layout.header_details, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof DetailsActivity) {
                mainActivity = (DetailsActivity) context;
            } else {
                throw new IllegalStateException("MainActivity must implement callbacks");
            }
        }

        exit = (ImageButton) layoutImage.findViewById(R.id.getBackButton);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mainActivity != null) {
                    mainActivity.finish();
                }
            }
        });

        return layoutImage;
    }
}
