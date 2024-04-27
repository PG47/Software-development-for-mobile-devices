package com.example.gallery.Edit_tool_screen;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.gallery.R;
import com.google.android.gms.vision.face.Face;

public class AdvancedFragment extends Fragment implements View.OnClickListener {
    EditActivity editActivity;
    Context context;
    ImageView faceDetection, textExtraction, similarPhoto;
    LinearLayout layout;
    TextView textView, addName, scanText;
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
        scanText = (TextView) advancedLayout.findViewById(R.id.scanText);

        faceDetection.setOnClickListener(this);
        textExtraction.setOnClickListener(this);
        similarPhoto.setOnClickListener(this);
        addName.setOnClickListener(this);
        scanText.setOnClickListener(this);

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
            }, 100);
        } else if (id == R.id.textExtraction) {
            editActivity.setCropOverlay();
            layout.setVisibility(View.GONE);
            textView.setText("Choose an area");
            textView.setVisibility(View.VISIBLE);
            scanText.setVisibility(View.VISIBLE);
        } else if (id == R.id.similarity) {
            layout.setVisibility(View.GONE);
            textView.setText("Finding similar photos...");
            textView.setVisibility(View.VISIBLE);
        } else if (id == R.id.addName) {
            editActivity.extractFaces(faces);
        } else if (id == R.id.scanText) {
            scanText.setVisibility(View.GONE);
            textView.setText("Scanning text...");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String text = editActivity.extractText();

                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Text from Image");

                    final TextView textViewTemp = new TextView(requireContext());
                    textViewTemp.setText(text);
                    textViewTemp.setBackgroundColor(Color.WHITE);
                    textViewTemp.setTextColor(Color.BLACK);
                    textViewTemp.setPadding(16, 16, 16, 16);
                    textViewTemp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    textViewTemp.setTextIsSelectable(true);

                    textViewTemp.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Text", textViewTemp.getText());
                            clipboard.setPrimaryClip(clip);

                            Toast.makeText(requireContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();

                            return true;
                        }
                    });

                    builder.setView(textViewTemp);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    textView.setText("Scanned successfully!");
                }
            }, 100);
        }
    }
}
