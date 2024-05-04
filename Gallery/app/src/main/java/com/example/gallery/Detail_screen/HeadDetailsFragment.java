package com.example.gallery.Detail_screen;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.gallery.DatabaseHelper;
import com.example.gallery.Images_screen.SelectOptions;
import com.example.gallery.MainActivity;
import com.example.gallery.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HeadDetailsFragment extends Fragment{
    DetailsActivity detailsActivity;
    Context context = null;
    ImageButton exit, addTag, addToAlbum, setWallpaper, advancedOption;
    String selectedImage;
    Bitmap originalBitmap;
    DatabaseHelper databaseHelper;

    public static HeadDetailsFragment newInstance(String strArg) {
        HeadDetailsFragment fragment = new HeadDetailsFragment();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
    }

    public void updateSelectedImage(String imgPath) {
    }

    public interface OnPathChangeListener {
        void onPathChanged(String newImagePath);
    }
    private OnPathChangeListener onPathChangeListener;
    public void setOnPathChangeListener(OnPathChangeListener listener) {
        this.onPathChangeListener = listener;
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

        databaseHelper = new DatabaseHelper(context);

        exit = (ImageButton) layoutImage.findViewById(R.id.getBackButton);
        addTag = (ImageButton) layoutImage.findViewById(R.id.component1);
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
            @Override
            public void onClick(View view) {
                String[] currentTags = databaseHelper.getTags(selectedImage).split(", ");

                final Button[] positiveButton = {null};
                ArrayList<String> allValues = new ArrayList<>();
                ArrayList<Integer> editTextIds = new ArrayList<>();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.dialog_add_tag, null);

                LinearLayout linearLayoutContainer = dialogView.findViewById(R.id.linearLayoutContainer);
                ScrollView scrollView = dialogView.findViewById(R.id.allTags);
                ImageButton addTagButton = dialogView.findViewById(R.id.toAddMoreTag);
                ImageButton removeTagButton = dialogView.findViewById(R.id.toRemoveTag);

                removeTagButton.setEnabled(false);

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
                warningLabel.setLayoutParams(new LinearLayout.LayoutParams(
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

                int id = View.generateViewId();
                tagEditText.setId(id);
                editTextIds.add(id);

                newTagLayout.addView(tagLabel);
                newTagLayout.addView(tagEditText);
                newTagLayout.addView(warningLabel);

                linearLayoutContainer.addView(newTagLayout);

                if (currentTags.length >= 1) {
                    tagEditText.setText(currentTags[0]);
                    allValues.add(currentTags[0]);
                    warningLabel.setVisibility(View.GONE);

                    if (currentTags.length > 1) {
                        removeTagButton.setEnabled(true);
                    }

                    for (int i = 1; i < currentTags.length; i++) {
                        LinearLayout newTagLayout1 = new LinearLayout(context);
                        newTagLayout1.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        newTagLayout1.setOrientation(LinearLayout.VERTICAL);

                        TextView tagLabel1 = new TextView(context);
                        tagLabel1.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        tagLabel1.setText("Tag " + (linearLayoutContainer.getChildCount() + 1));
                        tagLabel1.setTextColor(ContextCompat.getColor(context, R.color.black));
                        tagLabel1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

                        TextView warningLabel1 = new TextView(context);
                        warningLabel1.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        warningLabel1.setText("* Required input");
                        warningLabel1.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                        warningLabel1.setTextColor(ContextCompat.getColor(context, R.color.red));
                        warningLabel1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                        warningLabel1.setVisibility(View.GONE);

                        EditText tagEditText1 = new EditText(context);
                        tagEditText1.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        tagEditText1.setMaxLines(1);
                        tagEditText1.setInputType(InputType.TYPE_CLASS_TEXT);
                        tagEditText1.setText(currentTags[i]);

                        allValues.add(currentTags[i]);

                        int id1 = View.generateViewId();
                        tagEditText1.setId(id1);
                        editTextIds.add(id1);

                        tagEditText1.addTextChangedListener(new TextWatcher() {
                            int index;
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                for (int k = 0; k < editTextIds.size(); k++) {
                                    int tempId = editTextIds.get(k);
                                    if (tempId == tagEditText1.getId()) {
                                        index = k;
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                String value = tagEditText1.getText().toString().trim();
                                allValues.set(index, value);
                                if (value.equals("")) {
                                    warningLabel1.setVisibility(View.VISIBLE);
                                    positiveButton[0].setEnabled(false);
                                    positiveButton[0].setTextColor(Color.GRAY);
                                } else {
                                    warningLabel1.setVisibility(View.GONE);
                                    for (int n = 0; n < allValues.size(); n++) {
                                        if (allValues.get(n).equals("")) {
                                            return;
                                        }
                                    }

                                    if (!value.equals(currentTags[index])) {
                                        positiveButton[0].setEnabled(true);
                                        positiveButton[0].setTextColor(Color.GREEN);
                                    } else {
                                        positiveButton[0].setEnabled(false);
                                        positiveButton[0].setTextColor(Color.GRAY);
                                    }
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });

                        newTagLayout1.addView(tagLabel1);
                        newTagLayout1.addView(tagEditText1);
                        newTagLayout1.addView(warningLabel1);

                        linearLayoutContainer.addView(newTagLayout1);

                        if (linearLayoutContainer.getChildCount() > 3) {
                            int childHeight1 = linearLayoutContainer.getChildAt(0).getHeight();
                            int desiredHeight1 = 3 * childHeight1;

                            ViewGroup.LayoutParams layoutParams1 = scrollView.getLayoutParams();
                            layoutParams1.height = desiredHeight1;
                            scrollView.setLayoutParams(layoutParams1);

                            scrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.fullScroll(View.FOCUS_DOWN);
                                }
                            });
                        }
                    }
                } else {
                    allValues.add("");
                }
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
                            positiveButton[0].setEnabled(false);
                            positiveButton[0].setTextColor(Color.GRAY);
                        } else {
                            warningLabel.setVisibility(View.GONE);
                            for (int n = 0; n < allValues.size(); n++) {
                                if (allValues.get(n).equals("")) {
                                    positiveButton[0].setEnabled(false);
                                    positiveButton[0].setTextColor(Color.GRAY);
                                    return;
                                }
                            }
                            if (currentTags.length == 0 || !value.equals(currentTags[0])) {
                                positiveButton[0].setEnabled(true);
                                positiveButton[0].setTextColor(Color.GREEN);
                            } else {
                                positiveButton[0].setEnabled(false);
                                positiveButton[0].setTextColor(Color.GRAY);
                            }
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
                        allValues.trimToSize();

                        editTextIds.remove(editTextIds.size() - 1);
                        editTextIds.trimToSize();

                        for (int m = 0; m < allValues.size(); m++) {
                            if (allValues.get(m).equals("")) {
                                positiveButton[0].setEnabled(false);
                                positiveButton[0].setTextColor(Color.GRAY);
                                return;
                            }
                        }
                        positiveButton[0].setEnabled(true);
                        positiveButton[0].setTextColor(Color.GREEN);
                    }
                });
                addTagButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        positiveButton[0].setEnabled(false);
                        positiveButton[0].setTextColor(Color.GRAY);

                        if (linearLayoutContainer.getChildCount() >= 3) {
                            int childHeight = linearLayoutContainer.getChildAt(0).getHeight();
                            int desiredHeight = 3 * childHeight;

                            ViewGroup.LayoutParams layoutParams = scrollView.getLayoutParams();
                            layoutParams.height = desiredHeight;
                            scrollView.setLayoutParams(layoutParams);
                        }

                        if (linearLayoutContainer.getChildCount() > 6) return;

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
                        warningLabel.setLayoutParams(new LinearLayout.LayoutParams(
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
                                    positiveButton[0].setEnabled(false);
                                    positiveButton[0].setTextColor(Color.GRAY);
                                } else {
                                    warningLabel.setVisibility(View.GONE);
                                    for (int n = 0; n < allValues.size(); n++) {
                                        if (allValues.get(n).equals("")) {
                                            return;
                                        }
                                    }
                                    positiveButton[0].setEnabled(true);
                                    positiveButton[0].setTextColor(Color.GREEN);
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
                        boolean result = databaseHelper.addOrUpdateTags(allValues, selectedImage);
                        if (result) {
                            Toast.makeText(detailsActivity, "Tag(s) for image has been saved successfully!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        positiveButton[0] = alertDialog.getButton(dialogInterface.BUTTON_POSITIVE);
                        Button negativeButton = alertDialog.getButton(dialogInterface.BUTTON_NEGATIVE);
                        negativeButton.setTextColor(Color.RED);
                        positiveButton[0].setEnabled(false);
                        positiveButton[0].setTextColor(Color.GRAY);

                        if (currentTags.length > 0 && !Objects.equals(currentTags[0], "")) {
                            positiveButton[0].setText("Update");
                        }

                        if (currentTags.length > 3) {
                            int childHeight1 = linearLayoutContainer.getChildAt(0).getHeight();
                            int desiredHeight1 = 3 * childHeight1;

                            ViewGroup.LayoutParams layoutParams1 = scrollView.getLayoutParams();
                            layoutParams1.height = desiredHeight1;
                            scrollView.setLayoutParams(layoutParams1);

                            scrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.fullScroll(View.FOCUS_DOWN);
                                }
                            });
                        }
                    }
                });
                alertDialog.show();
            }
        });
        advancedOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.three_dot_popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if(itemId == R.id.menu_change_name) {
                            //change image name
                            final Button[] positiveButton = {null};
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            LayoutInflater inflater = LayoutInflater.from(context);
                            View view1 = inflater.inflate(R.layout.dialog_change_name, null);
                            TextView curName = view1.findViewById(R.id.curName);
                            EditText inputNewName = view1.findViewById(R.id.inputNewName);
                            String currentName = detailsActivity.getCurrentName();

                            builder.setView(view1);
                            builder.setTitle("Change Image's name");
                            curName.setText(currentName);

                            inputNewName.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                    String value = inputNewName.getText().toString().trim();
                                    if (!value.equals(currentName) && !value.equals("")) {
                                        positiveButton[0].setEnabled(true);
                                        positiveButton[0].setTextColor(Color.GREEN);
                                    } else {
                                        positiveButton[0].setEnabled(false);
                                        positiveButton[0].setTextColor(Color.GRAY);
                                    }
                                }

                                @Override
                                public void afterTextChanged(Editable editable) {

                                }
                            });

                            builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String value = inputNewName.getText().toString().trim();
                                    String path = detailsActivity.setNewNameForImage(value);
                                    databaseHelper.updateNewPath(path + "/" + currentName, path + "/" + value);
                                }
                            });
                            builder.setNegativeButton("Cancel", null);
                            AlertDialog dialog = builder.create();
                            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialogInterface) {
                                    positiveButton[0] = dialog.getButton(dialogInterface.BUTTON_POSITIVE);
                                    Button negativeButton = dialog.getButton(dialogInterface.BUTTON_NEGATIVE);
                                    negativeButton.setTextColor(Color.RED);
                                    positiveButton[0].setEnabled(false);
                                    positiveButton[0].setTextColor(Color.GRAY);
                                }
                            });
                            dialog.show();
                            return  true;
                        } else if(itemId == R.id.menu_add_album) {
                            //add to album
                            PopupMenu popupMenu1 = new PopupMenu(getContext(), view);
                            popupMenu1.getMenuInflater().inflate(R.menu.add_to_album_menu, popupMenu1.getMenu());

                            // Inside your setOnMenuItemClickListener method
                            popupMenu1.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    // Handle menu item click
                                    int itemId = item.getItemId(); // Get the ID of the clicked menu item
                                    // Use if-else statements to handle menu item clicks
                                    if (itemId == R.id.menu_add_to_exist_album) {
                                        // Handle "Add to existing album" menu item click
                                        Log.d("PopupMenu", "Add to existing album clicked");
                                        add_to_Album();
                                        return true;
                                    } else if (itemId == R.id.menu_add_to_new_album) {
                                        // Handle "Create new album" menu item click
                                        Log.d("PopupMenu", "Create new album clicked");
                                        add_to_new_Album();
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }
                            });
                            popupMenu1.setOnDismissListener(menu -> {Log.d("PopupMenu", "Dismissed");});
                            popupMenu1.show();
                            return  true;
                        } else if(itemId == R.id.menu_set_as_wallpaper) {
                            final Button[] positiveButton = {null};
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
                            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialogInterface) {
                                    positiveButton[0] = dialog.getButton(dialogInterface.BUTTON_POSITIVE);
                                    Button negativeButton = dialog.getButton(dialogInterface.BUTTON_NEGATIVE);
                                    negativeButton.setTextColor(Color.RED);
                                    positiveButton[0].setTextColor(Color.GREEN);
                                }
                            });
                            dialog.show();
                            return true;
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });

        return layoutImage;
    }
    private ArrayList<String> getAllAlbums() {
        ArrayList<String> albumNames = new ArrayList<>();

        // Query the device's media store for the list of albums
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        String orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC";

        try (Cursor cursor = requireContext().getContentResolver().query(uri, projection, null, null, orderBy)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String albumName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                    if (!albumNames.contains(albumName)) {
                        albumNames.add(albumName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return albumNames;
    }
    public void add_to_Album() {
        ArrayList<String> albumNames = getAllAlbums();

        // Convert ArrayList<String> to String array
        String[] albumsArray = albumNames.toArray(new String[0]);

        // Create a dialog to act as the popup menu
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose Album");

        // Add albums to the list dynamically
        builder.setItems(albumsArray, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String selectedAlbum = albumNames.get(which);
                moveImagesToAlbum(selectedAlbum); // Call moveImagesToAlbum with the selected album name
            }
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Set dialog position to center
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        if (window != null) {
            lp.copyFrom(window.getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;
            window.setAttributes(lp);
        }


    }

    private void moveImagesToAlbum(String albumName) {
        // Get the directory path of the target album
        File albumDir = new File(Environment.getExternalStorageDirectory(), "DCIM/" + albumName);

        if (!albumDir.exists()) {
            // Create the target album directory if it doesn't exist
            if (!albumDir.mkdirs()) {
                // If directory creation fails, show an error toast and return
                Toast.makeText(requireContext(), "Failed to create album directory", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Move selected images to the target album directory
        //if it was the secured images, unlock it and remove from the secure album
        /*if(databaseHelper.find_album_id(albumName) != -1) {
            long r_img = databaseHelper.deleteImage;
        }*/

        // Get the source file
        File sourceFile = new File(selectedImage);

        // Get the destination file path
        String destinationFilePath = albumDir.getPath() + "/" + sourceFile.getName();

        // Create the destination file
        File destinationFile = new File(destinationFilePath);

        try {
            // Perform the file move operation
            if (sourceFile.renameTo(destinationFile)) {
                // If move operation is successful, update the gallery database
                MediaScannerConnection.scanFile(requireContext(), new String[]{destinationFilePath}, null, null);
                selectedImage = destinationFilePath;
                if (onPathChangeListener != null) {
                    onPathChangeListener.onPathChanged(selectedImage);
                }
            } else {
                // If move operation fails, show an error toast
                Toast.makeText(requireContext(), "Failed to move image: " + sourceFile.getName(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // If an exception occurs during the move operation, show an error toast
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error moving image: " + sourceFile.getName(), Toast.LENGTH_SHORT).show();
        }

        //if you moved all the img secure, delete the previous secured album
        /*if(!Secured_album.isEmpty()) {
            int count = databaseHelper.countImages_in_Album(Secured_album);
            if(count==0) {
                databaseHelper.delete_Album(Secured_album);
            }
        }*/

        // Notify user about successful move
        Toast.makeText(requireContext(), "Images moved to album: " +albumName , Toast.LENGTH_SHORT).show();
    }

    // Helper method to get album ID based on the album name
    private boolean CheckAlbum(String albumName) {
        String[] projection = {MediaStore.Images.Media.BUCKET_ID};
        String selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + "=?";
        String[] selectionArgs = {albumName};
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = requireActivity().getContentResolver().query(uri, projection, selection, selectionArgs, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                    if (columnIndex != -1) {
                        return true;
                    } else {
                        // Log an error if the column index is -1
                        Log.e("ImagesFragment", "Column index for BUCKET_ID is -1");
                    }
                }
            } finally {
                cursor.close();
            }
        } else {
            // Log an error if the cursor is null
            Log.e("ImagesFragment", "Cursor is null");
        }
        return false;
    }

    public void add_to_new_Album() {
        // Create an EditText view for user input
        final EditText input = new EditText(requireContext());

        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Create New Album");
        builder.setMessage("Enter the name for the new album:");

        // Add the EditText view to the dialog
        builder.setView(input);

        // Set positive button for user confirmation
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String albumName = input.getText().toString().trim();
                if (!albumName.isEmpty()) {
                    if (!CheckAlbum(albumName)) {
                        // Call method to add images to the new album with the provided name
                        addImagesToNewAlbum(albumName);
                    } else {
                        // Show error toast if album already exists
                        Toast.makeText(requireContext(), "Album already exists", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Show error toast if album name is empty
                    Toast.makeText(requireContext(), "Album name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set negative button for cancel action
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addImagesToNewAlbum(String albumName) {
        // Create a directory in the DCIM folder with the provided album name
        File albumDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), albumName);
        if (albumDir.mkdirs()) {
            // If directory creation is successful, show a success toast
            Toast.makeText(requireContext(), "Folder Created!\n" + albumDir.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } else {
            // If directory creation fails, show an error toast
            Toast.makeText(requireContext(), "Failed to create folder!", Toast.LENGTH_SHORT).show();
        }

        // Now move all the selected images to the new album
        moveImagesToAlbum(albumName);
    }

}
