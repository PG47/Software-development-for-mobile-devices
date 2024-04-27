package com.example.gallery.Edit_tool_screen;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.gallery.R;
import com.google.android.gms.vision.face.Face;

public class AdvancedFragment extends Fragment implements View.OnClickListener {
    EditActivity editActivity;
    Context context;
    ImageView faceDetection, textExtraction, similarPhoto;
    LinearLayout layout;
    TextView textView, addName;
    SparseArray<Face> faces;

    public static AdvancedFragment newInstance(String strArg) {
        AdvancedFragment fragment = new AdvancedFragment();
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
        RelativeLayout advancedLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_advanced_option, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof EditActivity) {
                editActivity = (EditActivity) getActivity();
            } else {
                throw new IllegalStateException("EditActivity must implement callbacks");
            }
        }

        faceDetection = (ImageView) advancedLayout.findViewById(R.id.faceDetection);
        textExtraction = (ImageView) advancedLayout.findViewById(R.id.textExtraction);
        similarPhoto = (ImageView) advancedLayout.findViewById(R.id.similarity);
        layout = (LinearLayout) advancedLayout.findViewById(R.id.listAdvancedOptions);
        textView = (TextView) advancedLayout.findViewById(R.id.setLoading);
        addName = (TextView) advancedLayout.findViewById(R.id.addName);

        faceDetection.setOnClickListener(this);
        textExtraction.setOnClickListener(this);
        similarPhoto.setOnClickListener(this);
        addName.setOnClickListener(this);

        return advancedLayout;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.faceDetection) {
            layout.setVisibility(View.GONE);
            textView.setText("Detecting faces...");
            textView.setVisibility(View.VISIBLE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    faces = editActivity.FacesDetection();
                    String text = null;
                    if (faces.size() == 0) {
                        text = "There is no people in the image...";
                    } else if (faces.size() == 1) {
                        text = "There is one person in the image...";
                    } else if (faces.size() > 1) {
                        text = "There are " + faces.size() + " people in the image...";
                    }

                    textView.setText(text);
                    addName.setVisibility(View.VISIBLE);
                }
            }, 1000);
        } else if (id == R.id.textExtraction) {
            editActivity.setCropOverlay();
            layout.setVisibility(View.GONE);
            textView.setText("Scanning text...");
            textView.setVisibility(View.VISIBLE);
        } else if (id == R.id.similarity) {
            layout.setVisibility(View.GONE);
            textView.setText("Finding similar photos...");
            textView.setVisibility(View.VISIBLE);
        } else if (id == R.id.addName) {
            editActivity.extractFaces(faces);
        }
    }
}
