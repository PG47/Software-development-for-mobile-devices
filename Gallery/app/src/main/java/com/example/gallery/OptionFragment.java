package com.example.gallery;

import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class OptionFragment extends Fragment {
    DetailsActivity mainActivity;
    LinearLayout editButton;
    BottomNavigationView bottomOptionView;
    public static OptionFragment newInstance(String strArg1) {
        OptionFragment fragment = new OptionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("arg1", strArg1);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mainActivity = (DetailsActivity) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout layoutOption = (RelativeLayout) inflater.inflate(R.layout.fragment_option, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof DetailsActivity) {
                mainActivity = (DetailsActivity) getActivity();
            } else {
                throw new IllegalStateException("MainActivity must implement callbacks");
            }
        }

        String selectedImage = getArguments().getString("selectedImage");

        bottomOptionView = layoutOption.findViewById(R.id.optionToolbar);
        bottomOptionView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.share) {

                return true;
            } else if (itemId == R.id.edit) {
                Intent intent = new Intent(requireContext(), EditActivity.class);
                intent.putExtra("SelectedImage", selectedImage);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.camera) {

                return true;
            } else if (itemId == R.id.delete) {

                return true;
            }

            return false;
        });

        return layoutOption;
    }
}
