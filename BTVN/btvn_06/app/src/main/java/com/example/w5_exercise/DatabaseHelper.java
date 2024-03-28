package com.example.w5_exercise;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "mydatabase.db";
    public static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_HOCSINH = "HOCSINH";
    public static final String TABLE_LOP = "LOP";

    // Column names for HOCSINH table
    public static final String COLUMN_MA_HS = "MaHS";
    public static final String COLUMN_TEN_HS = "TenHS";
    public static final String COLUMN_MA_LOP_HS = "MaLop";

    // Column names for LOP table
    public static final String COLUMN_MA_LOP = "MaLop";
    public static final String COLUMN_TEN_LOP = "TenLop";

    // SQL statement to create HOCSINH table
    private static final String CREATE_TABLE_HOCSINH = "CREATE TABLE " + TABLE_HOCSINH + "("
            + COLUMN_MA_HS + " INTEGER PRIMARY KEY,"
            + COLUMN_TEN_HS + " TEXT,"
            + COLUMN_MA_LOP_HS + " INTEGER,"
            + "FOREIGN KEY(" + COLUMN_MA_LOP_HS + ") REFERENCES " + TABLE_LOP + "(" + COLUMN_MA_LOP + ")"
            + ")";

    // SQL statement to create LOP table
    private static final String CREATE_TABLE_LOP = "CREATE TABLE " + TABLE_LOP + "("
            + COLUMN_MA_LOP + " INTEGER PRIMARY KEY,"
            + COLUMN_TEN_LOP + " TEXT"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating tables
        db.execSQL(CREATE_TABLE_LOP);
        db.execSQL(CREATE_TABLE_HOCSINH);

        // Insert initial data into LOP table
        db.execSQL("INSERT INTO " + TABLE_LOP + " (" + COLUMN_TEN_LOP + ") VALUES ('Lớp A')");
        db.execSQL("INSERT INTO " + TABLE_LOP + " (" + COLUMN_TEN_LOP + ") VALUES ('Lớp B')");
        // You can insert more initial data here if needed
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOCSINH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOP);

        // Create tables again
        onCreate(db);
    }
}
