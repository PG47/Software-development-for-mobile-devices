package com.example.gallery.Edit_tool_screen;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.gallery.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ColorSetFragment extends Fragment {
    EditActivity editActivity;
    Context context;
    FragmentTransaction transaction;
    String[] iconNames = {"Normal", "Fresh", "Transparent", "Warm", "Film", "Modern Yellow", "Black White", "Sepia", "Fog", "Fantasy"};
    ViewGroup scrollView;
    TextView showValue;
    public static ColorSetFragment newInstance(String strArg) {
        ColorSetFragment fragment = new ColorSetFragment();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
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
        RelativeLayout layoutOption = (RelativeLayout)inflater.inflate(R.layout.fragment_color_set, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof EditActivity) {
                editActivity = (EditActivity) getActivity();
            } else {
                throw new IllegalStateException("EditActivity must implement callbacks");
            }
        }

        scrollView = layoutOption.findViewById(R.id.colorSet);
        showValue = layoutOption.findViewById(R.id.showValue);

        Bitmap[] listBitmaps = editActivity.getAppliedColorSet();

        for (int i = 0; i < iconNames.length; i++) {
            final View singleFrame = getLayoutInflater().inflate(R.layout.fragment_custom_list_color_set, null);
            singleFrame.setId(i);
            ImageView icon = (ImageView) singleFrame.findViewById(R.id.option);
            TextView name = (TextView) singleFrame.findViewById(R.id.optionName);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(10, 0, 10, 0);
            singleFrame.setLayoutParams(layoutParams);
            singleFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer id = view.getId();
                    editActivity.updateReplaceInfo();
                    editActivity.updateColorSet(id);
                    showValue.setText(iconNames[id]);
                }
            });

            icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
            icon.setImageBitmap(listBitmaps[i]);
            name.setText(iconNames[i]);
            scrollView.addView(singleFrame);
        }

        return layoutOption;
    }
}
