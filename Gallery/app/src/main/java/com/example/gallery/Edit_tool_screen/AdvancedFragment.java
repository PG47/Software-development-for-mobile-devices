package com.example.gallery.Edit_tool_screen;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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

import java.util.ArrayList;

public class AdvancedFragment extends Fragment implements View.OnClickListener {
    EditActivity editActivity;
    Context context;
    ImageView faceDetection, textExtraction, similarPhoto;
    LinearLayout layout;
    TextView textView, addName, scanText;
    SparseArray<Face> faces;
    Handler handler;

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

        handler = new Handler(Looper.getMainLooper());

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

            new Thread(new Runnable() {
                @Override
                public void run() {
                    SparseArray<Face> facesRes = editActivity.FacesDetection();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            doneFaceDetection(facesRes);
                        }
                    });
                }
            }).start();
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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    editActivity.findSimular_images();
                }
            }).start();
        } else if (id == R.id.addName) {
            addName.setText("Loading...");
            addName.setEnabled(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<ArrayList<String>> res = editActivity.extractFaces(faces);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            moveToAddNameActivity(res);
                        }
                    });
                }
            }).start();
        } else if (id == R.id.scanText) {
            scanText.setVisibility(View.GONE);
            textView.setText("Scanning text...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String text = editActivity.extractText();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            doneExtractText(text);
                        }
                    });
                }
            }).start();
        }
    }

    public void doneExtractText(String text) {
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

        builder.setNeutralButton("Copy to clipboard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Text", textViewTemp.getText());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(editActivity, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        textView.setText("Scanned successfully!");
    }
    public void doneFaceDetection(SparseArray<Face> allFaces) {
        editActivity.doSetImage();
        faces = allFaces;
        String text = null;
        if (faces.size() == 0) {
            text = "There is no people in the image...";
        } else if (faces.size() == 1) {
            text = "There is one person in the image...";
        } else if (faces.size() > 1) {
            text = "There are " + faces.size() + " people in the image...";
        }

        textView.setText(text);
        if (faces.size() != 0) {
            addName.setVisibility(View.VISIBLE);
        }
    }
    public void moveToAddNameActivity(ArrayList<ArrayList<String>> res) {
        ArrayList<String> filePaths = res.get(0);
        ArrayList<String> expectedNames = res.get(1);

        Intent intent = new Intent(editActivity, AddNameForFaceActivity.class);

        intent.putStringArrayListExtra("filePaths", (ArrayList<String>) filePaths);
        intent.putStringArrayListExtra("expectedNames", (ArrayList<String>) expectedNames);

        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                editActivity.finish();
            } else if (resultCode == RESULT_CANCELED) {
                // The called activity was canceled or finished with an error
                // Handle the cancellation or error here if needed
            }
        }
    }
}
