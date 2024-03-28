package com.example.w5_exercise;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.io.File;

public class FragmentLeft extends Fragment {
    MainActivity mainActivity;
    Context context = null;
    String message = "";
    ListView listView;
    TextView txtBlue;

    File storagePath;
    String myDbPath;
    SQLiteDatabase db;
    private String[] items = {};

    private int curr_pos = 0;
    Integer[] thumbnails = {R.mipmap.m1_foreground, R.mipmap.m2_foreground, R.mipmap.m1_foreground, R.mipmap.m3_foreground, R.mipmap.m5_foreground, R.mipmap.m2_foreground, R.mipmap.m1_foreground, R.mipmap.m5_foreground};

    public static FragmentLeft newInstance(String strArg) {
        FragmentLeft fragment = new FragmentLeft();
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
            mainActivity = (MainActivity) getActivity();
            storagePath = mainActivity.getApplication().getFilesDir();
            myDbPath = storagePath + "/" + "DB_BTVN06";
            // Create or load student data
            createOrLoadStudentData();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layoutleft = (LinearLayout) inflater.inflate(R.layout.layout_list_outline, null);
        txtBlue = (TextView) layoutleft.findViewById(R.id.textMsg);
        listView = (ListView) layoutleft.findViewById(R.id.myList);
        listView.setBackgroundColor(Color.parseColor("#ffccddff"));

        if (getActivity() instanceof MainActivity) {
            mainActivity = (MainActivity) getActivity();
        } else {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }

        MyAdapter adapter = new MyAdapter(context, R.layout.layout_list_outline, thumbnails, items);
        listView.setAdapter(adapter);
        listView.setSelection(0);
        listView.smoothScrollToPosition(0);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                mainActivity.onMsgFromFragToMain("BLUE-FRAG", items[position]);
                curr_pos = position;
                txtBlue.setText("Mã số: " + items[position].split(",")[0]);
                for (int i = 0; i < parent.getChildCount(); i++) {
                    View itemView = parent.getChildAt(i);
                    itemView.setBackgroundColor(Color.WHITE);
                }
                v.setBackgroundColor(Color.rgb(68, 85, 90));
            }
        });

        return layoutleft;
    }

    public void navigateToFirstItem() {
        Release(curr_pos);
        curr_pos = 0;
        Select(curr_pos);
        listView.setSelection(0);
        txtBlue.setText("Mã số: " + items[curr_pos].split(",")[0]);
        listView.smoothScrollToPosition(0);
        mainActivity.onMsgFromFragToMain("BLUE-FRAG", items[0]);
    }

    public void navigateToLastItem() {
        Release(curr_pos);
        curr_pos = items.length - 1;
        Select(curr_pos);
        listView.setSelection(0);
        txtBlue.setText("Mã số: " + items[curr_pos].split(",")[0]);
        listView.smoothScrollToPosition(0);
        mainActivity.onMsgFromFragToMain("BLUE-FRAG", items[0]);
    }

    public void navigateToNextItem() {
        Release(curr_pos);
        if (curr_pos < items.length - 1) {
            curr_pos++;
        } else curr_pos = 0;
        listView.setSelection(curr_pos);
        txtBlue.setText("Mã số: " + items[curr_pos].split(",")[0]);
        listView.smoothScrollToPosition(curr_pos);
        Select(curr_pos);
        mainActivity.onMsgFromFragToMain("BLUE-FRAG", items[curr_pos]);
    }

    public void navigateToPreviousItem() {
        Release(curr_pos);
        if (curr_pos > 0) {
            curr_pos--;
        } else curr_pos = items.length - 1;
        listView.setSelection(curr_pos);
        txtBlue.setText("Mã số: " + items[curr_pos].split(",")[0]);
        listView.smoothScrollToPosition(curr_pos);
        Select(curr_pos);
        mainActivity.onMsgFromFragToMain("BLUE-FRAG", items[curr_pos]);
    }

    public void Select(int pos) {
        View v = listView.getChildAt(curr_pos);
        v.setBackgroundColor(Color.rgb(68, 85, 90));
    }

    public void Release(int pos) {
        View v = listView.getChildAt(curr_pos);
        v.setBackgroundColor(Color.rgb(255, 255, 255));
    }

    private void createOrLoadStudentData() {
        File dbFile = getActivity().getDatabasePath("DB_BTVN06");
        creatHocSinhData();
        if (!dbFile.exists()) {
            // Database does not exist, create it
            creatLopData();
            creatHocSinhData();
        } else {
            // Database exists, load data
            loadStudentData();
        }
    }

    private void creatLopData() {
        // Open or create the database
        SQLiteDatabase db = SQLiteDatabase.openDatabase(myDbPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);

        // Begin transaction
        db.beginTransaction();
        try {
            // Create the table if it doesn't exist
            db.execSQL("CREATE TABLE IF NOT EXISTS LOP (MaLop INTEGER PRIMARY KEY AUTOINCREMENT, TenLop TEXT);");

            // Insert data into the LOP table
            db.execSQL("INSERT INTO LOP (TenLop) VALUES ('KHTN');");

            // Set transaction as successful
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DatabaseError", "Error performing database operations: " + e.getMessage());
        } finally {
            // End transaction
            db.endTransaction();

            // Close the database
            db.close();
        }
    }
    private void creatHocSinhData() {
        // Open or create the database
        SQLiteDatabase db = SQLiteDatabase.openDatabase(myDbPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);

        // Begin transaction
        db.beginTransaction();
        try {
            // Create the table if it doesn't exist
            db.execSQL("CREATE TABLE HOCSINH (ID INTEGER PRIMARY KEY AUTOINCREMENT, MaHS TEXT, TenHS TEXT, MaLop INTEGER, DTB INTEGER);");

            // Insert data into the HOCSINH table
            db.execSQL("CREATE TRIGGER generate_MaHS AFTER INSERT ON HOCSINH " +
                    "BEGIN " +
                    "UPDATE HOCSINH SET MaHS = 'A'||'_'||NEW.ID WHERE rowid = NEW.rowid; " +
                    "END;");


            db.execSQL("INSERT INTO HOCSINH (MaHS, TenHS, MaLop, DTB) VALUES ('A1_9829', 'Lê Thị A', 1, 8);");
            db.execSQL("INSERT INTO HOCSINH (MaHS, TenHS, MaLop, DTB) VALUES ('A1_1809', 'Lê Thị B', 1, 9);");
            db.execSQL("INSERT INTO HOCSINH (MaHS, TenHS, MaLop, DTB) VALUES ('A2_3509', 'Lê Thị C', 1, 10);");
            db.execSQL("INSERT INTO HOCSINH (MaHS, TenHS, MaLop, DTB) VALUES ('A2_3100', 'Lê Thị D', 1, 7);");
            db.execSQL("INSERT INTO HOCSINH (MaHS, TenHS, MaLop, DTB) VALUES ('A1_1120', 'Lê Thị E', 1, 6);");
            db.execSQL("INSERT INTO HOCSINH (MaHS, TenHS, MaLop, DTB) VALUES ('A3_4120', 'Lê Thị F', 1, 5);");
            db.execSQL("INSERT INTO HOCSINH (MaHS, TenHS, MaLop, DTB) VALUES ('A2_8100', 'Lê Thị G', 1, 4);");
            db.execSQL("INSERT INTO HOCSINH (MaHS, TenHS, MaLop, DTB) VALUES ('A4_1160', 'Lê Thị H', 1, 3);");

            // Set transaction as successful
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DatabaseError", "Error performing database operations: " + e.getMessage());
        } finally {
            // End transaction
            db.endTransaction();

            // Close the database
            db.close();
        }
    }

    private void loadStudentData() {
        // Open the database
        SQLiteDatabase db = SQLiteDatabase.openDatabase(myDbPath, null, SQLiteDatabase.OPEN_READONLY);

        Cursor cursor = null;
        cursor= db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        // Check if cursor is valid
        if (cursor != null) {
            // Iterate through the cursor to retrieve table names
            while (cursor.moveToNext()) {
                // Get the table name from the cursor
                String tableName = cursor.getString(0); // Assuming name is at index 0

                // Output the table name
                Log.d("Table Name", tableName);
            }

            // Close the cursor
            cursor.close();
        }
        // Query the HOCSINH table to get all the data
        try {
             cursor= db.rawQuery("SELECT * FROM HOCSINH", null);
        } catch (SQLiteException e) {
            Log.e("SQLiteException", "Error executing query: " + e.getMessage());
            // Handle the exception here, such as showing an error message to the user
        }


        // Initialize the items array with the appropriate size
        items = new String[cursor.getCount()];

        // Iterate through the cursor to fetch the data
        int i = 0;
        while (cursor.moveToNext()) {
            // Format the data and add it to the items array
            @SuppressLint("Range") String maHS = cursor.getString(cursor.getColumnIndex("MaHS"));
            @SuppressLint("Range") String tenHS = cursor.getString(cursor.getColumnIndex("TenHS"));
            items[i] = maHS + "," + tenHS;
            i++;
        }
        cursor.close();
        db.close();
    }
}
