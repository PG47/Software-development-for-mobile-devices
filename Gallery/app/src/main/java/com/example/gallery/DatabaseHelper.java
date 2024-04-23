package com.example.gallery;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.SparseArray;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

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
    private static final String COLUMN_ORIGIN_PATH = "old_dir";

    private static final String FACES = "faces";
    private static final String LIST_FACES = "list_faces";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_LIST_INT = "image_int";
    private static final String COLUMN_IMAGE = "image";

    private static final String SQL_CREATE_SECURE_ALBUM = "CREATE TABLE IF NOT EXISTS " + SECURE_ALBUM + " (" +
            COLUMN_ALBUM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_ALBUM_NAME + " TEXT UNIQUE, " +
            COLUMN_PASSWORD + " VARCHAR(4))";

    private static final String SQL_CREATE_SECURE_IMAGES = "CREATE TABLE IF NOT EXISTS " + SECURE_IMAGES + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_MEDIA_ID + " LONG, " +
            COLUMN_ALBUM_ID + " INTERGER, " +
            COLUMN_ORIGIN_PATH + " TEXT, " +
            "FOREIGN KEY(" + COLUMN_ALBUM_ID + ") REFERENCES " + SECURE_ALBUM + "(" + COLUMN_ALBUM_ID + "))";

    private static final String SQL_CREATE_FACES = "CREATE TABLE IF NOT EXISTS " + FACES + "(" +
            COLUMN_NAME + " TEXT PRIMARY KEY, " +
            COLUMN_LIST_INT + " TEXT)";
    private static final String SQL_LIST_FACES = "CREATE TABLE IF NOT EXISTS " + LIST_FACES + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_IMAGE + " BLOB)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL_CREATE_SECURE_ALBUM);
        db.execSQL(SQL_CREATE_SECURE_IMAGES);
        db.execSQL(SQL_CREATE_FACES);
        db.execSQL(SQL_LIST_FACES);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
    public String getTemp() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + COLUMN_LIST_INT + " FROM " + FACES + " WHERE " + COLUMN_NAME + " = ?";
        Cursor cursor = null;
        String listInt = null;

        try {
            cursor = db.rawQuery(sql, new String[]{"Thành"});
            if (cursor.moveToFirst()) {
                listInt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LIST_INT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return listInt;
    }
    public String getExpectedName(Bitmap bitmap) {
        String expectedName = "";
        Bitmap[] listBitmaps;
        Cursor cursor = null;
        Cursor cursor1 = null;
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            String query = "SELECT * FROM " + FACES;
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    String allIdImages = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LIST_INT));
                    expectedName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                    String[] splitIdImages = allIdImages.split(", ");
                    listBitmaps = new Bitmap[splitIdImages.length];

                    String idList = allIdImages;
                    query = "SELECT " + COLUMN_IMAGE + " FROM " + LIST_FACES +
                            " WHERE " + COLUMN_ID + " IN (" + idList + ")";
                    cursor1 = db.rawQuery(query, null);

                    int index = 0;
                    if (cursor1.moveToFirst()) {
                        do {
                            byte[] byteArray = cursor1.getBlob(cursor1.getColumnIndexOrThrow(COLUMN_IMAGE));
                            Bitmap buildBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                            listBitmaps[index++] = buildBitmap;
                        } while (cursor1.moveToNext());
                    }

                    float result = compareSimilarity(listBitmaps, bitmap);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (cursor1 != null) {
                cursor1.close();
            }
            db.close();
        }
        return expectedName;
    }
    public float compareSimilarity(Bitmap[] listBitmaps, Bitmap bitmap) {
        return 0.1F;
    }
    public void saveFaceToDB(String[] names, Bitmap[] images) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            for (int i = 0; i < names.length; i++) {
                long imageId = insertFaceImage(db, images[i]);
                String name = getFullName(db, names[i]);

                if (name == "-1") {
                    insertListFace(db, names[i], imageId);
                } else {
                    updateListFace(db, name, imageId);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }
    private long insertFaceImage(SQLiteDatabase db, Bitmap image) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE, getBitmapAsByteArray(image));
        return db.insert(LIST_FACES, null, values);
    }
    private byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
    private String getFullName(SQLiteDatabase db, String name) {
        Cursor cursor = db.query(FACES, new String[]{COLUMN_NAME}, COLUMN_NAME + "=?", new String[]{name}, null, null, null);
        String fullName = "-1";
        if (cursor.moveToFirst()) {
            fullName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID));
        }
        cursor.close();
        return fullName;
    }
    private void insertListFace(SQLiteDatabase db, String name, long imageId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_LIST_INT, String.valueOf(imageId));
        db.insert(FACES, null, values);
    }
    private void updateListFace(SQLiteDatabase db, String name, long imageId) {
        Cursor cursor = db.query(FACES, new String[]{COLUMN_LIST_INT}, COLUMN_ID + "=?", new String[]{name}, null, null, null);
        if (cursor.moveToFirst()) {
            String existingList = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LIST_INT));
            String updatedList = existingList + "," + imageId;
            ContentValues values = new ContentValues();
            values.put(COLUMN_LIST_INT, updatedList);
            db.update(FACES, values, COLUMN_ID + "=?", new String[]{name});
        }
        cursor.close();
    }
    public void creat_secure_album(String album_name, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT INTO " + SECURE_ALBUM + " (" + COLUMN_ALBUM_NAME + ", " + COLUMN_PASSWORD + ") " +
                "VALUES ('" + album_name + "', " + password + ")";
        db.execSQL(sql);
        db.close();
    }

    public void insertImage(long img_id, int al_id, String old_path) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT INTO " + SECURE_IMAGES + " (" + COLUMN_MEDIA_ID + ", " + COLUMN_ALBUM_ID + ", " + COLUMN_ORIGIN_PATH + ") " +
                "VALUES ('" + img_id + "', " + al_id + "', " + old_path + ")";
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
