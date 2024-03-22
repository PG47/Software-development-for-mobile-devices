package com.example.gallery;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class RotateFragment extends Fragment {
    EditActivity editActivity;
    Context context;
    FragmentTransaction transaction;
    Button finishRotate;
    SeekBar changeValue;
    TextView rotateAngle;

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
            Log.d("test attach", "st" + context);
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
                Log.d("test create view", "st" + getActivity());
            } else {
                throw new IllegalStateException("EditActivity must implement callbacks");
            }
        }

        finishRotate = (Button) rotateOption.findViewById(R.id.actionDone);
        changeValue = (SeekBar) rotateOption.findViewById(R.id.testSeekBar);
        rotateAngle = (TextView) rotateOption.findViewById(R.id.rotateAngle);

        finishRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditFragment editFragment = EditFragment.newInstance("Options");
                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.AllOptions, editFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        changeValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                rotateAngle.setText((i - 45) + "°");
                editActivity.updateRotate(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return rotateOption;
    }
}
