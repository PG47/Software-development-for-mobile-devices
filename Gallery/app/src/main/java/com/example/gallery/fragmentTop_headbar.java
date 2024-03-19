package com.example.gallery;

import static com.google.android.material.internal.ContextUtils.getActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class fragmentTop_headbar extends Fragment {
    MainActivity mainActivity;
    Context context = null;
    String message = "";
    ListView listView;
    TextView txtBlue;

    public static fragmentTop_headbar newInstance(String strArg) {
        fragmentTop_headbar fragment = new fragmentTop_headbar();
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
                mainActivity = (MainActivity) getActivity();
            }
        }
        catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }
}
