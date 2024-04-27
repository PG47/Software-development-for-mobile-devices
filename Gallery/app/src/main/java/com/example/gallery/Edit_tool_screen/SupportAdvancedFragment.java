package com.example.gallery.Edit_tool_screen;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.gallery.Detail_screen.DetailsActivity;
import com.example.gallery.Detail_screen.SupportAdvancedOptionsFragment;
import com.example.gallery.R;

import java.util.Objects;

public class SupportAdvancedFragment extends Fragment {
    EditActivity editActivity;
    Context context;
    private static String option;
    TextView textFromImage, curName, newName, changeBtn;
    EditText inputNewName;
    Button actionDone;
    String text;
    ScrollView textScroll;

    public static SupportAdvancedFragment newInstance(String strArg) {
        SupportAdvancedFragment fragment = new SupportAdvancedFragment();
        Bundle args = new Bundle();
        option = strArg;
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
            throw new IllegalStateException("DetailsActivity must implement callbacks");
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
        RelativeLayout layoutSupport = (RelativeLayout)inflater.inflate(R.layout.fragment_support, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof EditActivity) {
                editActivity = (EditActivity) getActivity();
            } else {
                throw new IllegalStateException("EditActivity must implement callbacks");
            }
        }

        textFromImage = (TextView) layoutSupport.findViewById(R.id.textFromImage);
        actionDone = (Button) layoutSupport.findViewById(R.id.getImageDone);
        textScroll = (ScrollView) layoutSupport.findViewById(R.id.textScroll);
        curName = (TextView) layoutSupport.findViewById(R.id.currentName);
        newName = (TextView) layoutSupport.findViewById(R.id.newName);
        inputNewName = (EditText) layoutSupport.findViewById(R.id.setNewName);
        changeBtn = (TextView) layoutSupport.findViewById(R.id.changeBtn);

//        actionDone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Extract text
//                text = detailsActivity.extractText();
//
//                // Create a dialog
//                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//                builder.setTitle("Text from Image");
//
//                // Set the text
//                final TextView textView = new TextView(requireContext());
//                textView.setText(text);
//                textView.setBackgroundColor(Color.WHITE);
//                textView.setTextColor(Color.BLACK);
//                textView.setPadding(16, 16, 16, 16);
//                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//                textView.setTextIsSelectable(true); // Allow text selection
//
//                // Add long-click listener to copy text
//                textView.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        // Copy text to clipboard
//                        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
//                        ClipData clip = ClipData.newPlainText("Text", textView.getText());
//                        clipboard.setPrimaryClip(clip);
//
//                        // Show toast indicating text is copied
//                        Toast.makeText(requireContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();
//
//                        return true; // Indicate that the long-click event is consumed
//                    }
//                });
//
//                // Add the text view to the dialog layout
//                builder.setView(textView);
//
//                // Set buttons
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                        // Call the listener method to dismiss the fragment
//                        requireActivity().getSupportFragmentManager().popBackStack();
//                    }
//                });
//
//                // Show the dialog
//                AlertDialog dialog = builder.create();
//                dialog.show();
//            }
//        });
//
//        changeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!TextUtils.isEmpty(inputNewName.getText())) {
//                    String name = String.valueOf(inputNewName.getText());
//                    detailsActivity.setNewNameForImage(name);
//                } else {
//                    return;
//                }
//            }
//        });
//
//        if (Objects.equals(option, "Faces Detection")) {
//
//        } else if (Objects.equals(option, "Text Extraction")) {
//            actionDone.setVisibility(View.VISIBLE);
//        } else if (Objects.equals(option, "Change Name")) {
//            String currentName = detailsActivity.getCurrentName();
//            curName.setVisibility(View.VISIBLE);
//            curName.setText("Current name: " + currentName);
//            newName.setVisibility(View.VISIBLE);
//            inputNewName.setVisibility(View.VISIBLE);
//            changeBtn.setVisibility(View.VISIBLE);
//        }

        return layoutSupport;
    }
}
