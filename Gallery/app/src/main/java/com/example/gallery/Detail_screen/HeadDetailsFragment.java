package com.example.gallery.Detail_screen;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.gallery.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HeadDetailsFragment extends Fragment {
    DetailsActivity detailsActivity;
    Context context = null;
    ImageButton exit, addTag, addToAlbum, setWallpaper, advancedOption;
    String selectedImage;
    Bitmap originalBitmap;

    public static HeadDetailsFragment newInstance(String strArg) {
        HeadDetailsFragment fragment = new HeadDetailsFragment();
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
                detailsActivity = (DetailsActivity) context;
            }
        }
        catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DetailsActivity) {
            detailsActivity = (DetailsActivity) context;
        } else {
            throw new IllegalStateException("EditActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ConstraintLayout layoutImage = (ConstraintLayout) inflater.inflate(R.layout.header_details, null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (getActivity() instanceof DetailsActivity) {
                detailsActivity = (DetailsActivity) context;
            } else {
                throw new IllegalStateException("MainActivity must implement callbacks");
            }
        }

        selectedImage = getArguments().getString("selectedImage");
        originalBitmap = BitmapFactory.decodeFile(selectedImage);

        exit = (ImageButton) layoutImage.findViewById(R.id.getBackButton);
        addTag = (ImageButton) layoutImage.findViewById(R.id.component1);
        addToAlbum = (ImageButton) layoutImage.findViewById(R.id.component2);
        setWallpaper = (ImageButton) layoutImage.findViewById(R.id.component3);
        advancedOption = (ImageButton) layoutImage.findViewById(R.id.component4);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (detailsActivity != null) {
                    detailsActivity.finish();
                }
            }
        });
        addTag.setOnClickListener(new View.OnClickListener() {
            Button positiveButton = null;
            List<String> allValues = new ArrayList<>();
            List<Integer> editTextIds = new ArrayList<>();
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.dialog_add_tag, null);

                LinearLayout linearLayoutContainer = dialogView.findViewById(R.id.linearLayoutContainer);
                ScrollView scrollView = dialogView.findViewById(R.id.allTags);
                ImageButton addTagButton = dialogView.findViewById(R.id.toAddMoreTag);
                ImageButton removeTagButton = dialogView.findViewById(R.id.toRemoveTag);

                // add the first linear layout
                LinearLayout newTagLayout = new LinearLayout(context);
                newTagLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                newTagLayout.setOrientation(LinearLayout.VERTICAL);

                TextView tagLabel = new TextView(context);
                tagLabel.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                tagLabel.setText("Tag " + (linearLayoutContainer.getChildCount() + 1));
                tagLabel.setTextColor(ContextCompat.getColor(context, R.color.black));
                tagLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

                TextView warningLabel = new TextView(context);
                tagLabel.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                warningLabel.setText("* Required input");
                warningLabel.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                warningLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                warningLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);

                EditText tagEditText = new EditText(context);
                tagEditText.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                tagEditText.setHint("Enter tag");
                tagEditText.setMaxLines(1);
                tagEditText.setInputType(InputType.TYPE_CLASS_TEXT);

                allValues.add("");

                int id = View.generateViewId();
                tagEditText.setId(id);
                editTextIds.add(id);

                newTagLayout.addView(tagLabel);
                newTagLayout.addView(tagEditText);
                newTagLayout.addView(warningLabel);

                linearLayoutContainer.addView(newTagLayout);
                tagEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String value = tagEditText.getText().toString().trim();
                        allValues.set(0, value);
                        if (value.equals("")) {
                            warningLabel.setVisibility(View.VISIBLE);
                            positiveButton.setEnabled(false);
                            positiveButton.setTextColor(Color.GRAY);
                        } else {
                            warningLabel.setVisibility(View.GONE);
                            for (int n = 0; n < allValues.size(); n++) {
                                if (allValues.get(n).equals("")) {
                                    return;
                                }
                            }
                            positiveButton.setEnabled(true);
                            positiveButton.setTextColor(Color.GREEN);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                removeTagButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (linearLayoutContainer.getChildCount() <= 2) {
                            removeTagButton.setEnabled(false);
                        } else {
                            removeTagButton.setEnabled(true);
                        }
                        addTagButton.setEnabled(true);

                        View toRemove = linearLayoutContainer.getChildAt(linearLayoutContainer.getChildCount() - 1);
                        linearLayoutContainer.removeView(toRemove);

                        if (linearLayoutContainer.getChildCount() == 3) {
                            ViewGroup.LayoutParams layoutParams = scrollView.getLayoutParams();
                            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            scrollView.setLayoutParams(layoutParams);
                        }

                        allValues.remove(allValues.size() - 1);
                        ((ArrayList<String>) allValues).trimToSize();

                        editTextIds.remove(editTextIds.size() - 1);
                        ((ArrayList<Integer>) editTextIds).trimToSize();

                        for (int m = 0; m < allValues.size(); m++) {
                            if (allValues.get(m).equals("")) {
                                positiveButton.setEnabled(false);
                                positiveButton.setTextColor(Color.GRAY);
                                return;
                            }
                        }
                        positiveButton.setEnabled(true);
                        positiveButton.setTextColor(Color.GREEN);
                    }
                });
                addTagButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        positiveButton.setEnabled(false);
                        positiveButton.setTextColor(Color.GRAY);

                        if (linearLayoutContainer.getChildCount() >= 3) {
                            int childHeight = linearLayoutContainer.getChildAt(0).getHeight();
                            int desiredHeight = 3 * childHeight;

                            ViewGroup.LayoutParams layoutParams = scrollView.getLayoutParams();
                            layoutParams.height = desiredHeight;
                            scrollView.setLayoutParams(layoutParams);
                        }

                        if (linearLayoutContainer.getChildCount() >= 6) {
                            addTagButton.setEnabled(false);
                        } else {
                            addTagButton.setEnabled(true);
                        }
                        removeTagButton.setEnabled(true);

                        LinearLayout newTagLayout = new LinearLayout(context);
                        newTagLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        newTagLayout.setOrientation(LinearLayout.VERTICAL);

                        TextView tagLabel = new TextView(context);
                        tagLabel.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        tagLabel.setText("Tag " + (linearLayoutContainer.getChildCount() + 1));
                        tagLabel.setTextColor(ContextCompat.getColor(context, R.color.black));
                        tagLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

                        TextView warningLabel = new TextView(context);
                        tagLabel.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        warningLabel.setText("* Required input");
                        warningLabel.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                        warningLabel.setTextColor(ContextCompat.getColor(context, R.color.red));
                        warningLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);

                        EditText tagEditText = new EditText(context);
                        tagEditText.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        tagEditText.setHint("Enter tag");
                        tagEditText.setMaxLines(1);
                        tagEditText.setInputType(InputType.TYPE_CLASS_TEXT);

                        allValues.add("");

                        int id = View.generateViewId();
                        tagEditText.setId(id);
                        editTextIds.add(id);

                        tagEditText.addTextChangedListener(new TextWatcher() {
                            int index;
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                for (int k = 0; k < editTextIds.size(); k++) {
                                    int tempId = editTextIds.get(k);
                                    if (tempId == tagEditText.getId()) {
                                        index = k;
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                String value = tagEditText.getText().toString().trim();
                                allValues.set(index, value);
                                if (value.equals("")) {
                                    warningLabel.setVisibility(View.VISIBLE);
                                    positiveButton.setEnabled(false);
                                    positiveButton.setTextColor(Color.GRAY);
                                } else {
                                    warningLabel.setVisibility(View.GONE);
                                    for (int n = 0; n < allValues.size(); n++) {
                                        if (allValues.get(n).equals("")) {
                                            return;
                                        }
                                    }
                                    positiveButton.setEnabled(true);
                                    positiveButton.setTextColor(Color.GREEN);
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });

                        newTagLayout.addView(tagLabel);
                        newTagLayout.addView(tagEditText);
                        newTagLayout.addView(warningLabel);

                        linearLayoutContainer.addView(newTagLayout);
                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.fullScroll(View.FOCUS_DOWN);
                            }
                        });
                    }
                });

                builder.setView(dialogView);
                builder.setTitle("Add Tag");
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(detailsActivity, "Tags: " + allValues.get(0), Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        positiveButton = alertDialog.getButton(dialogInterface.BUTTON_POSITIVE);
                        Button negativeButton = alertDialog.getButton(dialogInterface.BUTTON_NEGATIVE);
                        negativeButton.setTextColor(Color.RED);
                        positiveButton.setEnabled(false);
                        positiveButton.setTextColor(Color.GRAY);
                    }
                });
                alertDialog.show();
            }

            public boolean checkEmpty() {

                return false;
            }
        });
        addToAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        setWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View view1 = inflater.inflate(R.layout.dialog_wallpaper_confirmation, null);
                ImageView imageView = view1.findViewById(R.id.imageView);
                imageView.setImageBitmap(originalBitmap);

                int maxHeight = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.5);
                imageView.setMaxHeight(maxHeight);

                builder.setView(view1);
                builder.setTitle("Set Wallpaper");
                builder.setMessage("Do you want to set this image as your wallpaper?");
                builder.setPositiveButton("Set Wallpaper", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
                        try {
                            wallpaperManager.setBitmap(originalBitmap, null, true, WallpaperManager.FLAG_SYSTEM);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        advancedOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailsActivity.replaceAdvancedOptionFragment();
            }
        });
        return layoutImage;
    }
}
