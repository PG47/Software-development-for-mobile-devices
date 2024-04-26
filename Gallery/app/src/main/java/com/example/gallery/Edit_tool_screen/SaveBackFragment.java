package com.example.gallery.Edit_tool_screen;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.gallery.R;

public class SaveBackFragment extends Fragment {
    EditActivity editActivity;
    Context context;
    ImageButton getBack, save;
    TextView doneAction;
    FragmentTransaction transaction;
    String option;

    public static SaveBackFragment newInstance(String strArg) {
        SaveBackFragment fragment = new SaveBackFragment();
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
        RelativeLayout layoutSaveBack = (RelativeLayout)inflater.inflate(R.layout.fragment_save_back, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof EditActivity) {
                editActivity = (EditActivity) getActivity();
            } else {
                throw new IllegalStateException("EditActivity must implement callbacks");
            }
        }

        getBack = (ImageButton) layoutSaveBack.findViewById(R.id.backToDetails);
        save = (ImageButton) layoutSaveBack.findViewById(R.id.saveImage);
        doneAction = (TextView) layoutSaveBack.findViewById(R.id.actionDone);

        getBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editActivity != null) {
                    editActivity.finish();
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editActivity != null) {
                    editActivity.saveImage();
                    editActivity.finish();
                }
            }
        });

        doneAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditFragment editFragment = EditFragment.newInstance("Options");
                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.AllOptions, editFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                if (option == "rotate") {
                    editActivity.cropTheImage();
                } else if (option == "text") {
                    editActivity.addTextToImage();
                } else if (option == "filter") {

                } else if (option == "brightness") {
                    editActivity.saveChangeBrightness();
                } else if (option == "crop") {
                    editActivity.cropTheImage();
                }

                if (editActivity.checkChange() == true) {
                    save.setVisibility(View.VISIBLE);
                }
                doneAction.setVisibility(View.GONE);
            }
        });

        return layoutSaveBack;
    }

    public void executeInvisibleSave(String option) {
        save.setVisibility(View.GONE);
        doneAction.setVisibility(View.VISIBLE);
        this.option = option;
    }
}