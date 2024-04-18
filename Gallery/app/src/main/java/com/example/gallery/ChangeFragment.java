package com.example.gallery;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class ChangeFragment extends Fragment {
    private EditActivity editActivity;
    private Context context;
    private static String changeType;
    private TextView type;
    private TextView value;
    private SeekBar scrollValue;

    public static ChangeFragment newInstance(String strArg) {
        ChangeFragment fragment = new ChangeFragment();
        Bundle args = new Bundle();
        changeType = strArg;
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
        RelativeLayout changeOption = (RelativeLayout)inflater.inflate(R.layout.fragment_change, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof EditActivity) {
                editActivity = (EditActivity) getActivity();
            } else {
                throw new IllegalStateException("EditActivity must implement callbacks");
            }
        }

        type = changeOption.findViewById(R.id.changeType);
        value = changeOption.findViewById(R.id.value);
        scrollValue = changeOption.findViewById(R.id.seekbar);

        type.setText(changeType);
        value.setText("0");

        scrollValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (changeType == "Brightness") {
                    editActivity.changeBrightness(i);
                    value.setText(String.valueOf(i));
                } else if (changeType == "Contrast") {
                    editActivity.changeContrast(i);
                    value.setText(String.valueOf(i));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return changeOption;
    }
}
