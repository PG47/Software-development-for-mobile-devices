package com.example.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AddTextFragment extends Fragment {
    EditActivity editActivity;
    Context context;
    Button finishAddText;
    ViewGroup scrollViewGroup;
    ImageView selectedColor;
    Spinner spinner;
    TextView italic, bold;
    EditText fontSize;
    String strFontFamily = "Arial", strFontSize = "11";
    Boolean isItalic = false, isBold = false;
    Integer intColor = R.color.black;
    String[] listFonts = {"MONOSPACE", "SANS_SERIF", "SERIF", "DEFAULT_BOLD"};
    int[] colorInt = {
            R.color.white,
            R.color.red,
            R.color.orange,
            R.color.yellow,
            R.color.green,
            R.color.blue,
            R.color.indigo,
            R.color.purple,
            R.color.black,
            R.color.pink,
            R.color.brown,
            R.color.cyan,
            R.color.gray,
            R.color.yellowgreen
    };

    public static AddTextFragment newInstance(String strArg) {
        AddTextFragment fragment = new AddTextFragment();
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
        RelativeLayout textLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_text, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof EditActivity) {
                editActivity = (EditActivity) context;
            } else {
                throw new IllegalStateException("EditActivity must implement callbacks");
            }
        }

        editActivity.addEditText();

        selectedColor = (ImageView) textLayout.findViewById(R.id.boxColor);
        scrollViewGroup = (ViewGroup) textLayout.findViewById(R.id.listColor);
        spinner = (Spinner) textLayout.findViewById(R.id.listFonts);
        italic = (TextView) textLayout.findViewById(R.id.italic);
        bold = (TextView) textLayout.findViewById(R.id.bold);
        fontSize = (EditText) textLayout.findViewById(R.id.fontSize);
        italic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isItalic = !isItalic;
                strFontSize = String.valueOf(fontSize.getText());
                editActivity.updateEditText(strFontFamily, strFontSize, isItalic, isBold, intColor);
            }
        });
        bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isBold = !isBold;
                strFontSize = String.valueOf(fontSize.getText());
                editActivity.updateEditText(strFontFamily, strFontSize, isItalic, isBold, intColor);
            }
        });
        fontSize.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    strFontSize = String.valueOf(fontSize.getText());
                    editActivity.updateEditText(strFontFamily, strFontSize, isItalic, isBold, intColor);
                }
            }
        });

        fontSize.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                strFontSize = charSequence.toString();
                editActivity.updateEditText(strFontFamily, strFontSize, isItalic, isBold, intColor);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.font_families, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setGravity(Gravity.TOP);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strFontFamily = listFonts[i];
                strFontSize = String.valueOf(fontSize.getText());
                editActivity.updateEditText(strFontFamily, strFontSize, isItalic, isBold, intColor);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        for (int i = 0; i < colorInt.length; i++) {
            final View singleFrame = getLayoutInflater().inflate(R.layout.color_box, null);
            singleFrame.setId(i);
            ImageView boxOfColor = (ImageView) singleFrame.findViewById(R.id.boxColor);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(15, 0, 15, 0);
            singleFrame.setLayoutParams(layoutParams);
            singleFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intColor = colorInt[view.getId()];
                    strFontSize = String.valueOf(fontSize.getText());
                    editActivity.updateEditText(strFontFamily, strFontSize, isItalic, isBold, intColor);
                }
            });

            boxOfColor.setImageResource(colorInt[i]);
            scrollViewGroup.addView(singleFrame);
        }

//        finishAddText = (Button) textLayout.findViewById(R.id.actionDone);
//
//        finishAddText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                EditFragment editFragment = EditFragment.newInstance("Options");
//                transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.AllOptions, editFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
//            }
//        });


        return textLayout;
    }
}
