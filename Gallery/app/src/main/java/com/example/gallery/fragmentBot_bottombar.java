package com.example.gallery;

import static com.google.android.material.internal.ContextUtils.getActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class fragmentBot_bottombar  extends Fragment {
    MainActivity mainActivity;
    Context context = null;
    String message = "";
    ListView listView;
    TextView txtBlue;

    public static fragmentBot_bottombar newInstance(String strArg) {
        fragmentBot_bottombar fragment = new fragmentBot_bottombar();
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
            mainActivity = (MainActivity) getActivity();
        }
        catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }
}
