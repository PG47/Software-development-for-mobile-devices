package com.example.gallery.Edit_tool_screen;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.gallery.R;

public class ChangeFragment extends Fragment implements View.OnClickListener {
    private EditActivity editActivity;
    private Context context;
    private static String changeType;
    private TextView type;
    private TextView value, level0, level1, level2, level3, level4;
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

        value = changeOption.findViewById(R.id.value);
        scrollValue = changeOption.findViewById(R.id.seekbar);
        level0 = (TextView) changeOption.findViewById(R.id.level0);
        level1 = (TextView) changeOption.findViewById(R.id.level1);
        level2 = (TextView) changeOption.findViewById(R.id.level2);
        level3 = (TextView) changeOption.findViewById(R.id.level3);
        level4 = (TextView) changeOption.findViewById(R.id.level4);

        level0.setOnClickListener(this);
        level1.setOnClickListener(this);
        level2.setOnClickListener(this);
        level3.setOnClickListener(this);
        level4.setOnClickListener(this);

        value.setText("0");

        scrollValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (changeType == "Blur") {
                    editActivity.changeBlur(i);
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.level0) {
            editActivity.changeBlur(0);
            level0.setBackgroundResource(R.drawable.btn_appearance_new);
            level1.setBackgroundResource(R.drawable.btn_appearance);
            level2.setBackgroundResource(R.drawable.btn_appearance);
            level3.setBackgroundResource(R.drawable.btn_appearance);
            level4.setBackgroundResource(R.drawable.btn_appearance);
        } else if (id == R.id.level1) {
            editActivity.changeBlur(25);
            level0.setBackgroundResource(R.drawable.btn_appearance);
            level1.setBackgroundResource(R.drawable.btn_appearance_new);
            level2.setBackgroundResource(R.drawable.btn_appearance);
            level3.setBackgroundResource(R.drawable.btn_appearance);
            level4.setBackgroundResource(R.drawable.btn_appearance);
        } else if (id == R.id.level2) {
            editActivity.changeBlur(50);
            level0.setBackgroundResource(R.drawable.btn_appearance);
            level1.setBackgroundResource(R.drawable.btn_appearance);
            level2.setBackgroundResource(R.drawable.btn_appearance_new);
            level3.setBackgroundResource(R.drawable.btn_appearance);
            level4.setBackgroundResource(R.drawable.btn_appearance);
        } else if (id == R.id.level3) {
            editActivity.changeBlur(75);
            level0.setBackgroundResource(R.drawable.btn_appearance);
            level1.setBackgroundResource(R.drawable.btn_appearance);
            level2.setBackgroundResource(R.drawable.btn_appearance);
            level3.setBackgroundResource(R.drawable.btn_appearance_new);
            level4.setBackgroundResource(R.drawable.btn_appearance);
        } else if (id == R.id.level4) {
            editActivity.changeBlur(100);
            level0.setBackgroundResource(R.drawable.btn_appearance);
            level1.setBackgroundResource(R.drawable.btn_appearance);
            level2.setBackgroundResource(R.drawable.btn_appearance);
            level3.setBackgroundResource(R.drawable.btn_appearance);
            level4.setBackgroundResource(R.drawable.btn_appearance_new);
        }
    }
}
