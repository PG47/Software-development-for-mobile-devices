package com.example.w5_exercise;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    DatabaseHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        // Add data to LOP table
        long lop1Id = insertLop("Lớp A");
        long lop2Id = insertLop("Lớp B");

        // Add data to HOCSINH table
        insertHocSinh("Nguyễn Văn A", lop1Id);
        insertHocSinh("Trần Thị B", lop1Id);
        insertHocSinh("Phạm Văn C", lop2Id);

        // Query and print data from HOCSINH table
        printHocSinhData();
    }

    private long insertLop(String tenLop) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TEN_LOP, tenLop);
        return db.insert(DatabaseHelper.TABLE_LOP, null, values);
    }

    private long insertHocSinh(String tenHocSinh, long maLop) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TEN_HS, tenHocSinh);
        values.put(DatabaseHelper.COLUMN_MA_LOP_HS, maLop);
        return db.insert(DatabaseHelper.TABLE_HOCSINH, null, values);
    }

    private void printHocSinhData() {
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_HOCSINH, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int maHS = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_MA_HS));
                @SuppressLint("Range") String tenHS = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TEN_HS));
                @SuppressLint("Range") int maLop = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_MA_LOP_HS));
                Log.d(TAG, "Mã HS: " + maHS + ", Tên HS: " + tenHS + ", Mã Lớp: " + maLop);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    public void onMsgFromFragToMain(String sender, String strValue) {
        if (sender.equals("BLUE-FRAG")) {
            try {
                .onMsgFromMainToFragment(strValue);
            } catch (Exception e) {
                Log.e("ERROR", "onStrFromFragToMain " + e.getMessage());
            }
        }
}
