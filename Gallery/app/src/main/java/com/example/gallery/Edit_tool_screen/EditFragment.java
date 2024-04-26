package com.example.gallery.Edit_tool_screen;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.gallery.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EditFragment extends Fragment {
    EditActivity editActivity;
    Context context;
    FragmentTransaction transaction;
    BottomNavigationView bottomEditView;
    Integer[] iconImages = {R.drawable.ic_rotate_foreground, R.drawable.ic_text_foreground, R.mipmap.ic_filter_new_foreground, R.drawable.ic_blur_foreground, R.drawable.ic_crop_foreground, R.mipmap.ic_ai_foreground};
    String[] iconNames = {"Rotate", "Text", "Filter", "Blur", "Crop", "AI"};
    ViewGroup scrollView;
    public static EditFragment newInstance(String strArg) {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
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
        RelativeLayout layoutOption = (RelativeLayout)inflater.inflate(R.layout.fragment_edit_option, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof EditActivity) {
                editActivity = (EditActivity) getActivity();
            } else {
                throw new IllegalStateException("EditActivity must implement callbacks");
            }
        }

        scrollView = layoutOption.findViewById(R.id.functions);
        for (int i = 0; i < iconImages.length; i++) {
            final View singleFrame = getLayoutInflater().inflate(R.layout.fragment_custom_list_functions, null);
            singleFrame.setId(i);
            ImageView icon = (ImageView) singleFrame.findViewById(R.id.option);
            TextView name = (TextView) singleFrame.findViewById(R.id.optionName);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(20, 0, 20, 0);
            singleFrame.setLayoutParams(layoutParams);
            singleFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            icon.setImageResource(iconImages[i]);
            name.setText(iconNames[i]);
            scrollView.addView(singleFrame);
        }

//        bottomEditView = layoutOption.findViewById(R.id.editToolbar);
//        bottomEditView.setOnItemSelectedListener(item -> {
//            int itemId = item.getItemId();
//
//            if (itemId == R.id.rotate) {
//                RotateFragment rotateFragment = RotateFragment.newInstance("Rotate");
//                transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.AllOptions, rotateFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
//                editActivity.setCropOverlay();
//                editActivity.invisibleSave("rotate");
//                return true;
//            } else if (itemId == R.id.text) {
//                AddTextFragment addTextFragment = AddTextFragment.newInstance("Text");
//                transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.AllOptions, addTextFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
//                editActivity.invisibleSave("text");
//                return true;
//            } else if (itemId == R.id.filter) {
//                FilterFragment filterFragment = FilterFragment.newInstance("Filter");
//                transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.AllOptions, filterFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
//                editActivity.invisibleSave("filter");
//                return true;
//            } else if (itemId == R.id.brightness) {
//                ChangeFragment changeFragment = ChangeFragment.newInstance("Brightness");
//                transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.AllOptions, changeFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
//                editActivity.invisibleSave("brightness");
//                return true;
//            } else if (itemId == R.id.crop) {
//                CropMenuFragment cropMenuFragment = CropMenuFragment.newInstance("Crop");
//                transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.AllOptions, cropMenuFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
//                editActivity.setCropOverlay();
//                editActivity.invisibleSave("crop");
//                return true;
//            }
//
//            return false;
//        });

        return layoutOption;
    }
}
