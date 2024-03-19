package com.example.gallery;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class ChangeFragment extends Fragment {
    EditActivity editActivity;
    Context context;
    FragmentTransaction transaction;
    Button finishChange;
    static String changeType;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout changeOption = (RelativeLayout)inflater.inflate(R.layout.fragment_change, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof EditActivity) {
                editActivity = (EditActivity) getActivity();
            } else {
                throw new IllegalStateException("EditActivity must implement callbacks");
            }
        }

        TextView textView = changeOption.findViewById(R.id.info);
        TextView textView1 = changeOption.findViewById(R.id.value);
        textView.setText(changeType);
        textView1.setText("0");

        finishChange = (Button) changeOption.findViewById(R.id.actionDone);
        finishChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditFragment editFragment = EditFragment.newInstance("Options");
                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.AllOptions, editFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return changeOption;
    }
}
