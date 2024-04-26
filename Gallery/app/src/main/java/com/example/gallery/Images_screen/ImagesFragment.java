package com.example.gallery.Images_screen;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.gallery.DatabaseHelper;
import com.example.gallery.Detail_screen.DetailsActivity;
import com.example.gallery.NavigationAlbum;
import com.example.gallery.NavigationChange;
import com.example.gallery.NavigationSearch;
import com.example.gallery.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class ImagesFragment extends Fragment implements SelectOptions {
    ImageButton selectAll;
    Boolean active = false;
    ImageButton selectExit;
    ImageButton exitAlbum;
    private ArrayList<String> images;
    public boolean isSelectionMode;
    NavigationChange callback;
    NavigationAlbum closeAlbum;
    NavigationSearch closeSearch;
    public ImageAdapter adapter;
    Boolean album = false;
    Boolean search = false;

    String Secured_album = "";
    DatabaseHelper databaseHelper;
    private static final int DETAILS_ACTIVITY_REQUEST_CODE = 1;

    private int sortOrder = 0;
    public ImagesFragment() {
        // Required empty public constructor
    }

    //Show images in album
    public ImagesFragment(ArrayList<String> _images) {
        album = true;
        images = _images;
    }

    //Show secured images in album
    public ImagesFragment(ArrayList<String> _images, String album_name) {
        images = _images;
        album = true;
        Secured_album = album_name;
    }

    //Show search result images
    public ImagesFragment(ArrayList<String> _images, Boolean srh) {
        search = srh;
        images = _images;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callback = (NavigationChange) requireActivity();
        closeAlbum = (NavigationAlbum) requireActivity();
        closeSearch = (NavigationSearch) requireActivity();
        databaseHelper = new DatabaseHelper(requireActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload the list of images if needed
        if (adapter != null) {
            adapter.notifyDataSetChanged(); // Notify the adapter that the data set has changed
        }
    }

    private void sortImages() {
        // Kiểm tra xem danh sách hình ảnh có tồn tại không
        if (images != null && images.size() > 0) {
            // Sử dụng Comparator để so sánh thời gian sửa đổi của hai tệp ảnh
            Collections.sort(images, new Comparator<String>() {
                @Override
                public int compare(String imagePath1, String imagePath2) {
                    // Lấy thời gian sửa đổi của file 1
                    File file1 = new File(imagePath1);
                    long lastModified1 = file1.lastModified();

                    // Lấy thời gian sửa đổi của file 2
                    File file2 = new File(imagePath2);
                    long lastModified2 = file2.lastModified();

                    // So sánh thời gian sửa đổi của hai file
                    // Nếu sortOrder = 0 (mặc định), sắp xếp từ mới nhất đến cũ nhất
                    // Nếu sortOrder = 1, sắp xếp từ cũ nhất đến mới nhất
                    if (sortOrder == 0) {
                        return Long.compare(lastModified2, lastModified1); // Đảo ngược thứ tự sắp xếp
                    } else {
                        return Long.compare(lastModified1, lastModified2);
                    }
                }
            });
            // Cập nhật lại giao diện sau khi sắp xếp
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
        if (sortOrder == 0) {
            Toast.makeText(requireContext(), "Images sorted from\nNewest Date", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Images sorted from\nOldest Date", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDateOptionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chọn thời gian");

        String[] options = {"Chọn năm", "Toàn thời gian"};

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int optionIndex) {
                if (optionIndex == 0) {
                    // Chọn năm
                    showYearPickerDialog();
                } else {
                    // Toàn thời gian
                    displayAllImages();
                }
            }
        });

        builder.show();
    }

    private void showYearPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chọn năm");

        // Tính toán danh sách các năm từ năm 1970 đến năm hiện tại
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int numYears = currentYear - 1970 + 1;
        String[] yearsArray = new String[numYears];
        for (int i = 0; i < numYears; i++) {
            yearsArray[i] = String.valueOf(currentYear - i);
        }

        builder.setItems(yearsArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int yearIndex) {
                int selectedYear = Integer.parseInt(yearsArray[yearIndex]);
                showMonthPickerDialog(selectedYear);
            }
        });

        builder.show();
    }

    private void showMonthPickerDialog(final int selectedYear) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chọn tháng");

        String[] monthsArray = new String[]{"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};

        builder.setItems(monthsArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int monthIndex) {
                int selectedMonth = monthIndex + 1;
                // Sau khi đã chọn năm và tháng, kiểm tra và hiển thị ảnh
                checkAndDisplayImages(selectedYear, selectedMonth);
            }
        });

        builder.show();
    }
    private void checkAndDisplayImages(int year, int month) {
        // Tạo Calendar instance và đặt ngày đầu tiên của tháng đã chọn
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);

        long startOfMonth = calendar.getTimeInMillis();
        int lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth);
        long endOfMonth = calendar.getTimeInMillis();

        // Kiểm tra và hiển thị ảnh trong khoảng thời gian đã chọn
        ArrayList<String> imagesInRange = new ArrayList<>();
        for (String imagePath : images) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                long lastModified = imageFile.lastModified();
                if (lastModified >= startOfMonth && lastModified <= endOfMonth) {
                    imagesInRange.add(imagePath);
                }
            }
        }

        if (imagesInRange.isEmpty()) {
            Toast.makeText(requireContext(), "Không có ảnh trong tháng " + month + " năm " + year, Toast.LENGTH_SHORT).show();
        } else {
            updateImages(imagesInRange);
        }
    }

    private void displayAllImages() {
        // Hiển thị toàn bộ các ảnh trong ứng dụng
        updateImages(getAllImagesPaths()); // Cập nhật danh sách ảnh với tất cả ảnh trong hệ thống
    }

    private ArrayList<String> getAllImagesPaths() {
        ArrayList<String> allImagesPaths = new ArrayList<>();

        // Lấy tất cả các ảnh từ MediaStore và thêm vào danh sách
        ContentResolver contentResolver = requireContext().getContentResolver(); // Truy cập ContentResolver thông qua Context
        Cursor cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media.DATA},
                null,
                null,
                null
        );

        if (cursor != null) {
            int dataIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA); // Lấy chỉ mục của cột DATA
            while (cursor.moveToNext()) {
                String imagePath = cursor.getString(dataIndex);
                allImagesPaths.add(imagePath);
            }
            cursor.close();
        }

        return allImagesPaths;
    }



    // Thay đổi phương thức updateImages để nhận danh sách đường dẫn của ảnh
    public void updateImages(ArrayList<String> updatedImagePaths) {
        // Cập nhật danh sách ảnh với danh sách mới
        images.clear();
        images.addAll(updatedImagePaths);

        // Cập nhật adapter
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_images, container, false);
        GridView gallery = rootView.findViewById(R.id.imagesGrid);
        adapter = new ImageAdapter(requireActivity());
        gallery.setAdapter(adapter);

        gallery.setOnItemClickListener((parent, view, position, id) -> {
            if (!isSelectionMode) {
                // Xử lý sự kiện khi click vào một item
                Intent intent = new Intent(requireContext(), DetailsActivity.class);
                intent.putExtra("SelectedImage", images.get(position));
                intent.putStringArrayListExtra("ImageList", images);
                //startActivity(intent);
                startActivityForResult(intent, DETAILS_ACTIVITY_REQUEST_CODE);
            } else {
                adapter.toggleSelection(position);
            }
        });

        // Xử lý sự kiện khi long click vào một item
        gallery.setOnItemLongClickListener((parent, view, position, id) -> {
            if (!isSelectionMode) {
                // Thay đổi trạng thái khi chọn nhiều item
                active = false;
                callback.startSelection();
                selectAll.setVisibility(View.VISIBLE);
                selectExit.setVisibility(View.VISIBLE);
            }
            isSelectionMode = true;

            adapter.toggleSelection(position);
            return true;
        });

        // Xử lý khi click vào nút select_all
        selectAll = rootView.findViewById(R.id.select_all);
        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable icon = getResources().getDrawable(R.drawable.ic_select_all_foreground);
                if (!active) {
                    adapter.toggleSelectAll();
                    active = true;
                    icon.setColorFilter(getResources().getColor(R.color.selected_color), PorterDuff.Mode.SRC_IN);
                    selectAll.setImageDrawable(icon);
                } else {
                    adapter.toggleDeSelectAll();
                    active = false;
                    icon.clearColorFilter();
                    selectAll.setImageDrawable(icon);
                }
            }
        });

        // Thiết lập sự kiện khi click vào nút date_button
        ImageButton dateButton = rootView.findViewById(R.id.date_button);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateOptionDialog();
            }
        });

        // Xử lý khi click vào nút select_exit
        selectExit = rootView.findViewById(R.id.select_exit);
        selectExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExitSelection();
            }
        });

        // Xử lý khi click vào nút exit_album_button
        exitAlbum = rootView.findViewById(R.id.exit_button);
        if(album || search) exitAlbum.setVisibility(View.VISIBLE);
        else exitAlbum.setVisibility(View.GONE);
        exitAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(album) {
                    closeAlbum.closeAlbum();
                    exitAlbum.setVisibility(View.GONE);
                    album = false;
                } else if (search) {
                    closeSearch.closeSearch();
                    exitAlbum.setVisibility(View.GONE);
                    search = false;
                }
            }
        });

        // Hiển thị nút exit_album_button nếu ở trong album
        if (album || search) exitAlbum.setVisibility(View.VISIBLE);

        ImageButton sortButton = rootView.findViewById(R.id.sort_button);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a dialog to act as the popup menu
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Sort by...").setItems(new CharSequence[]{"Oldest Date", "Newest Date"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                // Sort by oldest date
                                sortOrder = 1;
                                sortImages();
                                break;
                            case 1:
                                // Sort by newest date
                                sortOrder = 0;
                                sortImages();
                                break;
                        }
                    }
                });
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
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DETAILS_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Handle the result here, such as reloading images
                adapter.reloadImages();
            }
        }
    }

    public AlertDialog.Builder inputPasswordAlert(EditText input, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title);

        input.setTextSize(40);
        input.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(4);
        input.setFilters(filterArray);

        builder.setView(input);
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder;
    }

    public class ImageAdapter extends BaseAdapter {
        private final Activity context;
        private final ArrayList<Integer> selectedPositions;

        public ImageAdapter(Activity localContext) {
            context = localContext;
            if (!album && !search) {
                images = getAllShownImagesPath(context);
            }
            selectedPositions = new ArrayList<>();
        }

        public int getCount() {
            return images.size();
        }

        public boolean Is_SelectionMode() {
            return isSelectionMode;
        }

        public Object getItem(int position) {
            return images.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final ImageView imageView;


            if (convertView == null) {
                imageView = (ImageView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
            } else {
                imageView = (ImageView) convertView;
            }

            // Highlight selected items
            if (isSelectionMode) {
                imageView.setBackgroundResource(R.drawable.selected_image_background);
            } else {
                // Otherwise, set transparent background
                imageView.setBackgroundResource(android.R.color.transparent);
            }
            if (selectedPositions.contains(position)) {
                // If it's in selected positions, set background with chosen color
                imageView.setBackgroundResource(R.drawable.selected_chosen_image_background);
            }

            Glide.with(context).load(images.get(position)).centerCrop().into(imageView);

            return imageView;
        }

        public void toggleSelection(int position) {
            if (selectedPositions.contains(position)) {
                selectedPositions.remove((Integer) position);
            } else {
                selectedPositions.add(position);
            }
            notifyDataSetChanged();
        }

        public void toggleSelectAll() {
            selectedPositions.clear();
            for (int i = 0; i < images.size(); i++) {
                selectedPositions.add(i);
            }
            notifyDataSetChanged();
        }

        public void toggleDeSelectAll() {
            selectedPositions.clear();
            notifyDataSetChanged();
        }

        public void exitSelectionMode() {
            selectedPositions.clear();
            isSelectionMode = false;
            notifyDataSetChanged();
        }

        private ArrayList<String> getAllShownImagesPath(Activity activity) {
            Uri uri;
            Cursor cursor;
            int column_index_data;
            ArrayList<String> secureAlbums = databaseHelper.getAllAlbums();
            ArrayList<String> listOfAllImages = new ArrayList<>();
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String[] projection = {MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.DATE_TAKEN};

            cursor = activity.getContentResolver().query(uri, projection, null,
                    null, MediaStore.Images.Media.DATE_TAKEN + " DESC");

            if (cursor != null) {
                column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

                while (cursor.moveToNext()) {
                    String absolutePathOfImage = cursor.getString(column_index_data);
                    // Extract the folder name from the absolute path
                    String folderName = new File(absolutePathOfImage).getParentFile().getName();
                    // Check if the folder name is not in any of the secure albums
                    if (!secureAlbums.contains(folderName)) {
                        listOfAllImages.add(absolutePathOfImage);
                    }

                }

                cursor.close();
            }

            return listOfAllImages;
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

        private void refesh_after() {
            for(int i:selectedPositions) {
                images.remove(i);
            }
            notifyDataSetChanged();
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
            for (int i : selectedPositions) {
                //if it was the secured images, unlock it and remove from the secure album
                if(!Secured_album.isEmpty()) {
                    long r_img = databaseHelper.deleteImage(i, Secured_album);
                }

                // Get the source file
                File sourceFile = new File(images.get(i));

                // Get the destination file path
                String destinationFilePath = albumDir.getPath() + "/" + sourceFile.getName();

                // Create the destination file
                File destinationFile = new File(destinationFilePath);

                try {
                    // Perform the file move operation
                    if (sourceFile.renameTo(destinationFile)) {
                        // If move operation is successful, update the gallery database
                        MediaScannerConnection.scanFile(requireContext(), new String[]{destinationFilePath}, null, null);
                    } else {
                        // If move operation fails, show an error toast
                        Toast.makeText(requireContext(), "Failed to move image: " + sourceFile.getName(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // If an exception occurs during the move operation, show an error toast
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Error moving image: " + sourceFile.getName(), Toast.LENGTH_SHORT).show();
                }
            }

            //if you moved all the img secure, delete the previous secured album
            if(!Secured_album.isEmpty()) {
                int count = databaseHelper.countImages_in_Album(Secured_album);
                if(count==0) {
                    databaseHelper.delete_Album(Secured_album);
                }
            }

            //refresh
            if(album){
                refesh_after();
            }

            // Exit selection mode after moving images
            ExitSelection();

            // Notify user about successful move
            Toast.makeText(requireContext(), "Images moved to album: " + albumName, Toast.LENGTH_SHORT).show();
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

        public void confirmDeleteSelections() {
            int count = selectedPositions.size();
            AlertDialog.Builder builder = getBuilder("Delete selected items?",
                    "This will delete " + count + " item(s) permanently.", new CallbackDialog() {
                        @Override
                        public void onPositiveClick() {
                            deleteSelections();
                        }
                    });
            builder.show();
        }

        public void deleteSelections() {
            String[] projection = {MediaStore.Images.Media._ID};

            String selection = MediaStore.Images.Media.DATA + " = ?";
            Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver contentResolver = getActivity().getContentResolver();

            for (int i = 0; i < selectedPositions.size(); i++) {
                String[] selectionArgs = new String[]{images.get(selectedPositions.get(i))};

                Cursor cursor = contentResolver.query(queryUri, projection, selection, selectionArgs, null);

                assert cursor != null;
                int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

                if (cursor.moveToFirst()) {
                    long id = cursor.getLong(column_index_data);
                    Uri deleteUri = ContentUris.withAppendedId(queryUri, id);
                    contentResolver.delete(deleteUri, null, null);
                }

                cursor.close();
            }
            if(album || search) {
                refesh_after();
            } else images = getAllShownImagesPath(context);
            notifyDataSetChanged();
            ExitSelection();
        }

        public void shareSelections() {
            ArrayList<Uri> selectedUris = new ArrayList<>();
            for (int i : selectedPositions) {
                Glide.with(requireContext())
                        .asBitmap()
                        .load(images.get(i))
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                try {
                                    File tempFile = File.createTempFile("image", ".jpg", context.getCacheDir());
                                    FileOutputStream outputStream = new FileOutputStream(tempFile);

                                    resource.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                    outputStream.flush();
                                    outputStream.close();

                                    Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".file-provider", tempFile);
                                    selectedUris.add(uri);

                                    if (selectedUris.size() == selectedPositions.size()) {
                                        startShareIntent(selectedUris);
                                    }
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
            }
        }

        public void unlockSecure(){
            int count = selectedPositions.size();
            AlertDialog.Builder builder = getBuilder("Unlock selected images?",
                    "This will unlock " + count + " item(s) permanently.", new CallbackDialog() {
                        @Override
                        public void onPositiveClick() {
                            String[] projection = {MediaStore.Images.Media._ID};

                            String selection = MediaStore.Images.Media.DATA + " = ?";
                            Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                            ContentResolver contentResolver = getActivity().getContentResolver();
                            for (int i = 0; i < selectedPositions.size(); i++) {
                                String[] selectionArgs = new String[]{images.get(selectedPositions.get(i))};

                                Cursor cursor = contentResolver.query(queryUri, projection, selection, selectionArgs, null);

                                assert cursor != null;
                                int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

                                if (cursor.moveToFirst()) {
                                    long id = cursor.getLong(column_index_data);

                                    String oldpath = databaseHelper.deleteImage(id);

                                    File albumDir = new File(Environment.getExternalStorageDirectory(), "DCIM/" + oldpath);

                                    if (!albumDir.exists()) {
                                        // Create the target album directory if it doesn't exist
                                        if (!albumDir.mkdirs()) {
                                            // If directory creation fails, show an error toast and return
                                            Toast.makeText(requireContext(), "Failed to create album directory", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }

                                    // Get the source file
                                    File sourceFile = new File(images.get(i));

                                    // Get the destination file path
                                    String destinationFilePath = albumDir.getPath() + "/" + sourceFile.getName();

                                    // Create the destination file
                                    File destinationFile = new File(destinationFilePath);

                                    try {
                                        // Perform the file move operation
                                        if (sourceFile.renameTo(destinationFile)) {
                                            // If move operation is successful, update the gallery database
                                            MediaScannerConnection.scanFile(requireContext(), new String[]{destinationFilePath}, null, null);
                                        } else {
                                            // If move operation fails, show an error toast
                                            Toast.makeText(requireContext(), "Failed to move image: " + sourceFile.getName(), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {
                                        // If an exception occurs during the move operation, show an error toast
                                        e.printStackTrace();
                                        Toast.makeText(requireContext(), "Error moving image: " + sourceFile.getName(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                cursor.close();
                            }
                            int count = databaseHelper.countImages_in_Album(Secured_album);
                            if (count == 0) {
                                databaseHelper.delete_Album(Secured_album);
                            }
                            refesh_after();
                            exitSelectionMode();
                        }

                    });
            builder.show();
        }

        public void startShareIntent(ArrayList<Uri> selectedUris) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Shared from Group 7 Gallery App!");
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, selectedUris);
            shareIntent.setType("*/*");

            try {
                startActivity(Intent.createChooser(shareIntent, "Share to..."));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(requireActivity(), "No Apps Available", Toast.LENGTH_SHORT).show();
            }
        }

        public void secureEnterPassword() {
            int count = selectedPositions.size();

            final EditText pass = new EditText(requireContext());
            final EditText name = new EditText(requireContext());

            // Create an AlertDialog name album
            AlertDialog.Builder album = new AlertDialog.Builder(requireContext());
            album.setTitle("Create a new Secured Album");
            album.setMessage("Enter the name for the Secured Album:");

            // Add the EditText view to the dialog
            album.setView(name);

            // Set positive button for user confirmation
            album.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String albumName = name.getText().toString().trim();
                    if (!albumName.isEmpty()) {
                        if (!CheckAlbum(albumName)) {
                            // Call method to add set password
                            AlertDialog.Builder builder = inputPasswordAlert(pass,
                                    "Enter a 4-digit password to secure " + count + " image(s):");

                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String password = String.valueOf(pass.getText());

                                    AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
                                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });

                                    if (password.length() != 4) {
                                        alert.setTitle("Error").setMessage("Password must be exactly 4-digit long.");
                                    }
                                    else {
                                        secureSelections(albumName, password);
                                        alert.setTitle("Success").setMessage("Your images have been secured.");
                                    }

                                    alert.show();
                                }
                            });

                            builder.show();
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
            album.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Show the dialog
            AlertDialog dialog = album.create();
            dialog.show();
        }

        public void secureSelections(String albumName, String password) {
            File albumDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), albumName);
            if (albumDir.mkdirs()) {
                databaseHelper.creat_secure_album(albumName,password);
                String[] projection = {MediaStore.Images.Media._ID};

                String selection = MediaStore.Images.Media.DATA + " = ?";
                Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = requireActivity().getContentResolver();

                int album_id = databaseHelper.find_album_id(albumName);

                for (int i = 0; i < selectedPositions.size(); i++) {
                    String[] selectionArgs = new String[] {images.get(selectedPositions.get(i))};

                    Cursor cursor = contentResolver.query(queryUri, projection, selection, selectionArgs, null);

                    assert cursor != null;
                    int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

                    if (cursor.moveToFirst()) {
                        long id = cursor.getLong(column_index_data);

                        Cursor cursorID = databaseHelper.find_image_ID(id);
                        if (cursorID != null && cursorID.moveToFirst()) {
                            databaseHelper.updateData(id, album_id);
                            cursorID.close();
                        }

                        else {
                            String oldpath = extractFolderPath(images.get(selectedPositions.get(i)));
                            databaseHelper.insertImage(id, album_id, oldpath);
                        }
                    }

                    cursor.close();
                }

                moveImagesToAlbum(albumName);
                reloadImages();
                notifyDataSetChanged();
            } else {
                // If directory creation fails, show an error toast
                Toast.makeText(requireContext(), "Error in securing images!", Toast.LENGTH_SHORT).show();
            }
        }

        public String extractFolderPath(String oldpath) {
            String[] parts = oldpath.split("/");
            int dcimIndex = -1;
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals("DCIM")) {
                    dcimIndex = i;
                    break;
                }
            }
            if (dcimIndex != -1 && dcimIndex + 1 < parts.length) {
                StringBuilder folderPath = new StringBuilder();
                for (int i = dcimIndex + 1; i < parts.length-1; i++) {
                    folderPath.append(parts[i]);
                    if (i < parts.length - 2) {
                        folderPath.append("/");
                    }
                }
                return folderPath.toString();
            }
            return "";
        }




        //Load lại ảnh khi cân thiết
        public void reloadImages() {
            images = getAllShownImagesPath(context);
            adapter.notifyDataSetChanged();
        }
    }

    public ImageAdapter getImageAdapter() {
        return adapter;
    }

    public void ExitSelection() {
        adapter.exitSelectionMode();
        callback.endSelection();
        selectAll.setVisibility(View.INVISIBLE);
        selectExit.setVisibility(View.INVISIBLE);
    }

    private interface CallbackDialog {
        void onPositiveClick();
    }

    @NonNull
    private AlertDialog.Builder getBuilder(String title, String message, CallbackDialog callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callback.onPositiveClick();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return builder;
    }

    @Override
    public void share() {
        adapter.shareSelections();
    }

    @Override
    public void addAlbum() {
        adapter.add_to_Album();
    }

    @Override
    public void newAlbum() {
        adapter.add_to_new_Album();
    }

    @Override
    public void secure() {
        adapter.secureEnterPassword();
    }

    @Override
    public void delete() {
        adapter.confirmDeleteSelections();
    }

    @Override
    public void unlockSecure() {
        adapter.unlockSecure();
    }


    public void reloadImages() {
        if (adapter != null) {
            adapter.reloadImages();
        }
    }

    public ArrayList<File> getImagesList() {
        ArrayList<File> fileList = new ArrayList<>();
        for (String imagePath : images) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                fileList.add(imageFile);
            }
        }
        return fileList;
    }


}