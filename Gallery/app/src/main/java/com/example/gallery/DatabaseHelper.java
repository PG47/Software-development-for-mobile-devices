package com.example.gallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "team7_gallery.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SECURE_IMAGES = "secure_images";
    private static final String SECURE_ALBUM = "secure_album";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_ALBUM_NAME = "alb_name";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_MEDIA_ID = "media_id";
    private static final String COLUMN_ALBUM_ID = "album_id";

    private static final String SQL_CREATE_SECURE_ALBUM = "CREATE TABLE IF NOT EXISTS " + SECURE_ALBUM + " (" +
            COLUMN_ALBUM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_ALBUM_NAME + " TEXT UNIQUE, " +
            COLUMN_PASSWORD + " VARCHAR(4))";

    private static final String SQL_CREATE_SECURE_IMAGES = "CREATE TABLE IF NOT EXISTS " + SECURE_IMAGES + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_MEDIA_ID + " LONG," +
            COLUMN_ALBUM_ID + " INTERGER," +
            "FOREIGN KEY(" + COLUMN_ALBUM_ID + ") REFERENCES " + SECURE_ALBUM + "(" + COLUMN_ALBUM_ID + "))";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL_CREATE_SECURE_ALBUM);
        db.execSQL(SQL_CREATE_SECURE_IMAGES);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    public void creat_secure_album(String album_name, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT INTO " + SECURE_ALBUM + " (" + COLUMN_ALBUM_NAME + ", " + COLUMN_PASSWORD + ") " +
                "VALUES ('" + album_name + "', " + password + ")";
        db.execSQL(sql);
        db.close();
    }

    public void insertImage(long img_id, int al_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT INTO " + SECURE_IMAGES + " (" + COLUMN_MEDIA_ID + ", " + COLUMN_ALBUM_ID + ") " +
                "VALUES ('" + img_id + "', " + al_id + ")";
        db.execSQL(sql);
        db.close();
    }

    public void updateData(long id, int al_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE " + SECURE_IMAGES + " SET " + COLUMN_ALBUM_ID + " = '" + al_id +
                "' WHERE " + COLUMN_MEDIA_ID + " = " + id;
        db.execSQL(sql);
        db.close();
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + SECURE_IMAGES;
        return db.rawQuery(sql, null);
    }

    @SuppressLint("Range")
    public int find_album_id(String album_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_ALBUM_ID + " FROM " + SECURE_ALBUM + " WHERE " + COLUMN_ALBUM_NAME + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{album_name});
        int albumId = -1; // default value if album is not found
        if (cursor.moveToFirst()) {
            albumId = cursor.getInt(cursor.getColumnIndex(COLUMN_ALBUM_ID));
        }
        cursor.close();
        return albumId;
    }

    public ArrayList<String> getAllAlbums() {
        ArrayList<String> albums = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_ALBUM_NAME + " FROM " + SECURE_ALBUM;

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String albumName = cursor.getString(cursor.getColumnIndex(COLUMN_ALBUM_NAME));
                albums.add(albumName);
            } while (cursor.moveToNext());

            // Close the cursor to free up resources
            cursor.close();
        }

        // Close the database connection
        db.close();
        return albums;
    }

    public boolean checkPass(String AlbumName, String pass) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_PASSWORD + " FROM " + SECURE_ALBUM
                + " WHERE " + COLUMN_ALBUM_NAME + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{AlbumName});
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD));
            cursor.close();
            if(password.equals(pass)) {
                return true;
            }
            else  return false;
        }
        cursor.close();
        return false;
    }


    public Cursor find_image_ID(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + SECURE_IMAGES + " WHERE " + COLUMN_MEDIA_ID + " = " + id;
        return db.rawQuery(sql, null);
    }
}
