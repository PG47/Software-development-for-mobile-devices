package com.example.gallery;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "team7_gallery.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "secure_images";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_MEDIA_ID = "media_id";
    private static final String COLUMN_PASSWORD = "password";

    private static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_MEDIA_ID + " LONG," +
            COLUMN_PASSWORD + " VARCHAR(4))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    public void insertData(long id, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT INTO " + TABLE_NAME + " (" + COLUMN_MEDIA_ID + ", " + COLUMN_PASSWORD + ") " +
                "VALUES ('" + id + "', " + password + ")";
        db.execSQL(sql);
        db.close();
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(sql, null);
    }
}
