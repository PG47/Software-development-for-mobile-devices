package com.example.gallery;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Objects;

public class SupportAdvancedOptionsFragment extends Fragment {
    DetailsActivity detailsActivity;
    Context context;
    private static String option;
    TextView textFromImage;
    Button actionDone;
    String text;
    ScrollView textScroll;

    public static SupportAdvancedOptionsFragment newInstance(String strArg) {
        SupportAdvancedOptionsFragment fragment = new SupportAdvancedOptionsFragment();
        Bundle args = new Bundle();
        option = strArg;
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DetailsActivity) {
            detailsActivity = (DetailsActivity) context;
        } else {
            throw new IllegalStateException("DetailsActivity must implement callbacks");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                detailsActivity = (DetailsActivity) getActivity();
            }
        }
        catch (IllegalStateException e) {
            throw new IllegalStateException("EditActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout layoutSupport = (RelativeLayout)inflater.inflate(R.layout.fragment_support, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof DetailsActivity) {
                detailsActivity = (DetailsActivity) getActivity();
            } else {
                throw new IllegalStateException("EditActivity must implement callbacks");
            }
        }

        textFromImage = (TextView) layoutSupport.findViewById(R.id.textFromImage);
        actionDone = (Button) layoutSupport.findViewById(R.id.getImageDone);
        textScroll = (ScrollView) layoutSupport.findViewById(R.id.textScroll);

        actionDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text = detailsActivity.extractText();
                textFromImage.setText(text);
                textScroll.setVisibility(View.VISIBLE);
                textFromImage.setVisibility(View.VISIBLE);
                view.setVisibility(View.GONE);
            }
        });

        if (Objects.equals(option, "Faces Detection")) {
            textScroll.setVisibility(View.GONE);
            actionDone.setVisibility(View.GONE);
            textFromImage.setVisibility(View.GONE);
        } else if (Objects.equals(option, "Text Extraction")) {
            actionDone.setVisibility(View.VISIBLE);
        }

        return layoutSupport;
    }
}
