package com.example.gallery.Edit_tool_screen;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.gallery.R;

public class RotateFragment extends Fragment {
    EditActivity editActivity;
    Context context;
    TextView rotate30deg, rotate45deg, rotate60deg, rotate90deg, rotate180deg;
    SeekBar changeValue;
    TextView rotateAngle;
    ImageView minus90deg, plus90deg;

    public static RotateFragment newInstance(String strArg) {
        RotateFragment fragment = new RotateFragment();
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
                editActivity = (EditActivity) context;
                Log.d("test create", "st" + context);
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
        RelativeLayout rotateOption = (RelativeLayout)inflater.inflate(R.layout.fragment_rotate, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof EditActivity) {
                editActivity = (EditActivity) context;
            } else {
                throw new IllegalStateException("EditActivity must implement callbacks");
            }
        }

        changeValue = (SeekBar) rotateOption.findViewById(R.id.testSeekBar);
        rotateAngle = (TextView) rotateOption.findViewById(R.id.rotateAngle);
        rotate30deg = (TextView) rotateOption.findViewById(R.id.rotate30deg);
        rotate45deg = (TextView) rotateOption.findViewById(R.id.rotate45deg);
        rotate60deg = (TextView) rotateOption.findViewById(R.id.rotate60deg);
        rotate90deg = (TextView) rotateOption.findViewById(R.id.rotate90deg);
        rotate180deg = (TextView) rotateOption.findViewById(R.id.rotate180deg);
        minus90deg = (ImageView) rotateOption.findViewById(R.id.minus90deg);
        plus90deg = (ImageView) rotateOption.findViewById(R.id.plus90deg);
        changeValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                rotateAngle.setText((i - 180) + "°");
                editActivity.updateRotate(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editActivity.startToZoom();
            }
        });
        rotate30deg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateAngle.setText("30°");
                editActivity.fastRotate(30);
                changeValue.setProgress(180 + 30);
            }
        });
        rotate45deg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateAngle.setText("45°");
                editActivity.fastRotate(45);
                changeValue.setProgress(180 + 45);
            }
        });
        rotate60deg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateAngle.setText("60°");
                editActivity.fastRotate(60);
                changeValue.setProgress(180 + 60);
            }
        });
        rotate90deg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateAngle.setText("90°");
                editActivity.fastRotate(90);
                changeValue.setProgress(180 + 90);
            }
        });
        rotate180deg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateAngle.setText("180°");
                editActivity.fastRotate(180);
                changeValue.setProgress(180 + 180);
            }
        });
        minus90deg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editActivity.minus90deg();
            }
        });
        plus90deg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editActivity.plus90deg();
            }
        });
        return rotateOption;
    }
}
