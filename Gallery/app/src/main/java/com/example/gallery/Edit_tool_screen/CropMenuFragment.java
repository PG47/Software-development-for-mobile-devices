package com.example.gallery.Edit_tool_screen;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.example.gallery.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CropMenuFragment extends Fragment implements View.OnClickListener {
    EditActivity editActivity;
    Context context;
    ImageView op1, op2, op3, op4, op5, op6, op7;

    public static CropMenuFragment newInstance(String strArg) {
        CropMenuFragment fragment = new CropMenuFragment();
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
        RelativeLayout cropOption = (RelativeLayout) inflater.inflate(R.layout.fragment_cropmenu, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof EditActivity) {
                editActivity = (EditActivity) getActivity();
            } else {
                throw new IllegalStateException("EditActivity must implement callbacks");
            }
        }

        op1 = (ImageView) cropOption.findViewById(R.id.op1);
        op2 = (ImageView) cropOption.findViewById(R.id.op2);
        op3 = (ImageView) cropOption.findViewById(R.id.op3);
        op4 = (ImageView) cropOption.findViewById(R.id.op4);
        op5 = (ImageView) cropOption.findViewById(R.id.op5);
        op6 = (ImageView) cropOption.findViewById(R.id.op6);
        op7 = (ImageView) cropOption.findViewById(R.id.op7);

        op1.setOnClickListener(this);
        op2.setOnClickListener(this);
        op3.setOnClickListener(this);
        op4.setOnClickListener(this);
        op5.setOnClickListener(this);
        op6.setOnClickListener(this);
        op7.setOnClickListener(this);

        return cropOption;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.op1) {
            editActivity.setRatio(1, 1);
        } else if (id == R.id.op2) {
            editActivity.setRatio(3, 2);
        } else if (id == R.id.op3) {
            editActivity.setRatio(4, 3);
        } else if (id == R.id.op4) {
            editActivity.setRatio(5, 4);
        } else if (id == R.id.op5) {
            editActivity.setRatio(9, 16);
        } else if (id == R.id.op6) {
            editActivity.setRatio(16, 9);
        } else if (id == R.id.op7) {
            editActivity.setRatio(16, 10);
        }
    }
}
