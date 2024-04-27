package com.example.gallery.Edit_tool_screen;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.gallery.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FilterFragment extends Fragment implements View.OnClickListener {
    EditActivity editActivity;
    Context context;
    ImageView contrast, grayscale, brightness, sepia, sharpen;
    SeekBar seekBar;
    TextView textView;
    String type = "contrast";
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
        RelativeLayout layoutOption = (RelativeLayout)inflater.inflate(R.layout.fragment_filter, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof EditActivity) {
                editActivity = (EditActivity) getActivity();
            } else {
                throw new IllegalStateException("EditActivity must implement callbacks");
            }
        }

        contrast = (ImageView) layoutOption.findViewById(R.id.contrast);
        grayscale = (ImageView) layoutOption.findViewById(R.id.grayscale);
        brightness = (ImageView) layoutOption.findViewById(R.id.brightness);
        sepia = (ImageView) layoutOption.findViewById(R.id.sepia);
        sharpen = (ImageView) layoutOption.findViewById(R.id.sharpen);
        seekBar = (SeekBar) layoutOption.findViewById(R.id.value);
        textView = (TextView) layoutOption.findViewById(R.id.showValue);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textView.setText(String.valueOf(i));
                if (type.equals("contrast")) {
                    editActivity.changeContrast(i);
                } else if (type.equals("grayscale")) {
                    editActivity.changeGrayscale(i);
                } else if (type.equals("brightness")) {
                    editActivity.changeBrightness(i);
                } else if (type.equals("sepia")) {
                    editActivity.changeSepia(i);
                } else if (type.equals("sharpen")) {
                    editActivity.changeSharpen(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        contrast.setOnClickListener(this);
        grayscale.setOnClickListener(this);
        brightness.setOnClickListener(this);
        sepia.setOnClickListener(this);
        sharpen.setOnClickListener(this);

        return layoutOption;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.contrast) {
            type = "contrast";
        } else if (view.getId() == R.id.grayscale) {
            type = "grayscale";
        } else if (view.getId() == R.id.brightness) {
            type = "brightness";
        } else if (view.getId() == R.id.sepia) {
            type = "sepia";
        } else if (view.getId() == R.id.sharpen) {
            type = "sharpen";
        }
    }
}
