package com.example.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class GridFaceFragment extends Fragment {
    AddNameForFaceActivity addNameForFaceActivity;
    Context context;
    GridView gridView;
    static Bitmap[] bitmaps;

    public static GridFaceFragment newInstance(String strArg, Bitmap[] listBitmaps) {
        GridFaceFragment fragment = new GridFaceFragment();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        bitmaps = listBitmaps;
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
        RelativeLayout gridLayer = (RelativeLayout) inflater.inflate(R.layout.fragment_list_face, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof AddNameForFaceActivity) {
                addNameForFaceActivity = (AddNameForFaceActivity) getActivity();
            } else {
                throw new IllegalStateException("AddNameForFaceActivity must implement callbacks");
            }
        }

        gridView = (GridView) gridLayer.findViewById(R.id.myGrid);
        gridView.setAdapter(new FaceAdapter(context, bitmaps));

        return gridLayer;
    }
    public String[] getAllName() {
        String[] res = ((FaceAdapter) gridView.getAdapter()).getAllNames();
        return res;
    }
    public Bitmap[] getAllImage() {
        Bitmap[] res = ((FaceAdapter) gridView.getAdapter()).getAllImages();
        return res;
    }
}
