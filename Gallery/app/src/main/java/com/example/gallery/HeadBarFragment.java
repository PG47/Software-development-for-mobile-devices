package com.example.gallery;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class HeadBarFragment extends Fragment {

    MainActivity mainActivity;
    Context context = null;
    ImageButton add;
    ImageButton people;
    public static HeadBarFragment newInstance(String strArg) {
        HeadBarFragment fragment = new HeadBarFragment();
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
                mainActivity = (MainActivity) context;
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
                mainActivity = (MainActivity) context;
            } else {
                throw new IllegalStateException("MainActivity must implement callbacks");
            }
        }



        //add = (ImageButton) layoutImage.findViewById(R.id.getBackButton);
        //people = (ImageButton) layoutImage.findViewById(R.id.getBackButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
            }
        });

        return layoutImage;
    }
}
