package com.example.gallery;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class SaveNameFragment extends Fragment {
    AddNameForFaceActivity addNameForFaceActivity;
    Context context;
    ImageButton getBack;
    TextView done;
    public static SaveNameFragment newInstance(String strArg) {
        SaveNameFragment fragment = new SaveNameFragment();
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
                addNameForFaceActivity = (AddNameForFaceActivity) getActivity();
            }
        }
        catch (IllegalStateException e) {
            throw new IllegalStateException("EditActivity must implement callbacks");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddNameForFaceActivity) {
            addNameForFaceActivity = (AddNameForFaceActivity) context;
        } else {
            throw new IllegalStateException("EditActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout saveLayer = (RelativeLayout) inflater.inflate(R.layout.fragment_save_name_for_face, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof AddNameForFaceActivity) {
                addNameForFaceActivity = (AddNameForFaceActivity) getActivity();
            } else {
                throw new IllegalStateException("AddNameForFaceActivity must implement callbacks");
            }
        }

        getBack = (ImageButton) saveLayer.findViewById(R.id.cancelAddName);
        done = (TextView) saveLayer.findViewById(R.id.doneAddName);

        getBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNameForFaceActivity.finish();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNameForFaceActivity.saveToDB();
            }
        });
        return saveLayer;
    }
}
