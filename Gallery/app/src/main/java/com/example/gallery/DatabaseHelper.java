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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;

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
    private static final String TAG_ID = "tag_id";
    private static final String COLUMN_IMAGE_PATH = "image_path";
    private static final String COLUMN_TAG_ID = "ids";
    private static final String COLUMN_LIKE = "isLike";
    private static final String TAG_NAME = "tag_name";
    private static final String SQL_CREATE_SECURE_ALBUM = "CREATE TABLE IF NOT EXISTS " + SECURE_ALBUM + " (" +
            COLUMN_ALBUM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_ALBUM_NAME + " TEXT UNIQUE, " +
            COLUMN_PASSWORD + " VARCHAR(4))";

    private static final String SQL_CREATE_SECURE_IMAGES = "CREATE TABLE IF NOT EXISTS " + SECURE_IMAGES + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_MEDIA_ID + " LONG, " +
            COLUMN_ALBUM_ID + " INTEGER, " +
            COLUMN_ORIGIN_PATH + " TEXT, " +
            "FOREIGN KEY(" + COLUMN_ALBUM_ID + ") REFERENCES " + SECURE_ALBUM + "(" + COLUMN_ALBUM_ID + "))";

    private static final String SQL_CREATE_FACES = "CREATE TABLE IF NOT EXISTS " + FACES + "(" +
            COLUMN_NAME + " TEXT PRIMARY KEY, " +
            COLUMN_LIST_INT + " TEXT)";

    private static final String SQL_LIST_FACES = "CREATE TABLE IF NOT EXISTS " + LIST_FACES + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_IMAGE + " TEXT)";

    private static final String SQL_TAG_ID_LIKE = "CREATE TABLE IF NOT EXISTS " + TAG_ID + "(" +
            COLUMN_IMAGE_PATH + " TEXT PRIMARY KEY, " +
            COLUMN_TAG_ID + " TEXT, " +
            COLUMN_LIKE + " BOOLEAN DEFAULT FALSE)";

    private static final String SQL_TAG_NAME = "CREATE TABLE IF NOT EXISTS " + TAG_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL_CREATE_SECURE_ALBUM);
        db.execSQL(SQL_CREATE_SECURE_IMAGES);
        db.execSQL(SQL_CREATE_FACES);
        db.execSQL(SQL_LIST_FACES);
        db.execSQL(SQL_TAG_ID_LIKE);
        db.execSQL(SQL_TAG_NAME);
//        db.execSQL("DROP TABLE IF EXISTS " + TAG_ID);
//        db.execSQL("DROP TABLE IF EXISTS " + TAG_NAME);
//        db.execSQL("DROP TABLE IF EXISTS " + FACES);
//        db.execSQL("DROP TABLE IF EXISTS " + LIST_FACES);
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
    public List<String> getSearchResult(String value) {
        List<String> res = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TAG_NAME + " WHERE LOWER(" + COLUMN_NAME + ") = LOWER(?)";
        Cursor cursor = db.rawQuery(query, new String[]{value});
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            query = "SELECT * FROM " + TAG_ID;
            Cursor cursor1 = db.rawQuery(query, null);

            if (cursor1.moveToFirst()) {
                do {
                    String[] listIds = cursor1.getString(cursor1.getColumnIndexOrThrow(COLUMN_TAG_ID)).split(", ");
                    String imgPath = cursor1.getString(cursor1.getColumnIndexOrThrow(COLUMN_IMAGE_PATH));

                    for (int i = 0; i < listIds.length; i++) {
                        if (listIds[i] == String.valueOf(id)) {
                            res.add(imgPath);
                            break;
                        }
                    }
                } while (cursor1.moveToNext());
            }
        }
        return res;
    }
    public String getTags(String selectedImage) {
        SQLiteDatabase db = this.getReadableDatabase();
        String result = "";
        Cursor cursor, cursor1;
        String query = "SELECT " + COLUMN_TAG_ID + " FROM " + TAG_ID + " WHERE " + COLUMN_IMAGE_PATH + " = ?";
        cursor = db.rawQuery(query, new String[]{selectedImage});

        if (cursor.moveToFirst()) {
            String tag_ids = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAG_ID));
            tag_ids = tag_ids.replaceAll(", ", ",");

            query = "SELECT " + COLUMN_NAME + " FROM " + TAG_NAME + " WHERE " + COLUMN_ID + " IN( " + tag_ids + ")";
            cursor1 = db.rawQuery(query, null);

            if (cursor1.moveToFirst()) {
                do {
                    String value = cursor1.getString(cursor1.getColumnIndexOrThrow(COLUMN_NAME));
                    result += value + ", ";
                } while (cursor1.moveToNext());
            }

            result = result.substring(0, result.length() - 2);
        }
        return result;
    }
    public boolean addOrUpdateTags(ArrayList<String> allValues, String selectedImage) {
        SQLiteDatabase db = this.getReadableDatabase();
        String tag_ids = String.valueOf(getIds(db, allValues));
        boolean isExists = checkImageExists(db, selectedImage);
        long result = saveTags(db, selectedImage, tag_ids, isExists);
        Log.d("return", tag_ids);
        return true;
    }
    public boolean checkImageExists(SQLiteDatabase db, String selectedImage) {
        boolean res = false;
        Cursor cursor = null;
        String query = null;

        query = "SELECT * FROM " + TAG_ID + " WHERE " + COLUMN_IMAGE_PATH + " = ?";
        cursor = db.rawQuery(query, new String[]{selectedImage});

        if (cursor.moveToFirst()) {
            return true;
        }

        return res;
    }
    public StringBuilder getIds(SQLiteDatabase db, ArrayList<String> allValues) {
        StringBuilder result = new StringBuilder();
        Cursor cursor = null;
        String query = null;

        for (int i = 0; i < allValues.size(); i++) {
            query = "SELECT * FROM " + TAG_NAME + " WHERE LOWER(" + COLUMN_NAME + ") = LOWER(?)";
            cursor = db.rawQuery(query, new String[]{allValues.get(i)});
            int index = -1;

            if (cursor.moveToFirst()) {
                index = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            } else {
                index = addTagName(db, allValues.get(i));
            }
            result.append(index).append(", ");
        }

        result = new StringBuilder(result.substring(0, result.length() - 2));

        return result;
    }
    public long saveTags(SQLiteDatabase db, String selectedImage, String tag_ids, boolean isExists) {
        ContentValues values = new ContentValues();
        if (isExists) {
            values.put(COLUMN_TAG_ID, tag_ids);
            return db.update(TAG_ID, values, COLUMN_IMAGE_PATH + " = ?", new String[]{selectedImage});
        } else {
            values.put(COLUMN_IMAGE_PATH, selectedImage);
            values.put(COLUMN_TAG_ID, tag_ids);
            return db.insert(TAG_ID, null, values);
        }
    }
    public int addTagName(SQLiteDatabase db, String value) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, value);
        return (int) db.insert(TAG_NAME, null, values);
    }
    public ArrayList<String> SearchByTag(String key) {
        ArrayList<String> imagePaths = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to retrieve the tag ID for the given key
        String tagIdQuery = "SELECT " + COLUMN_ID + " FROM " + TAG_NAME + " WHERE LOWER(" + COLUMN_NAME + ") = LOWER(?)";
        Cursor tagIdCursor = db.rawQuery(tagIdQuery, new String[]{key});

        if (tagIdCursor.moveToFirst()) {
            int tagId = tagIdCursor.getInt(tagIdCursor.getColumnIndexOrThrow(COLUMN_ID));

            // Query to retrieve the image paths associated with the tag ID
            String imagePathQuery = "SELECT " + COLUMN_IMAGE_PATH + " FROM " + TAG_ID + " WHERE " + COLUMN_TAG_ID + " = ?";
            Cursor imagePathCursor = db.rawQuery(imagePathQuery, new String[]{String.valueOf(tagId)});

            if (imagePathCursor.moveToFirst()) {
                do {
                    String imagePath = imagePathCursor.getString(imagePathCursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH));
                    imagePaths.add(imagePath);
                } while (imagePathCursor.moveToNext());
            }

            imagePathCursor.close();
        }

        tagIdCursor.close();
        db.close();

        return imagePaths;
    }



    public String getExpectedName(Bitmap bitmap) {
        String expectedName = "Unknown", tempName = "";
        Bitmap[] listBitmaps;
        float result = 999999999F;
        Cursor cursor = null;
        Cursor cursor1 = null;
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            String query = "SELECT * FROM " + FACES;
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    String allIdImages = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LIST_INT));
                    tempName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));

                    String[] splitIdImages = allIdImages.split(", ");
                    listBitmaps = new Bitmap[splitIdImages.length];

                    String idList = allIdImages.replaceAll(" ", "");
                    query = "SELECT " + COLUMN_IMAGE + " FROM " + LIST_FACES +
                            " WHERE " + COLUMN_ID + " IN (" + idList + ")";
                    cursor1 = db.rawQuery(query, null);

                    int index = 0;
                    if (cursor1.moveToFirst()) {
                        do {
                            String path = cursor1.getString(cursor1.getColumnIndexOrThrow(COLUMN_IMAGE));
                            Bitmap buildBitmap = BitmapFactory.decodeFile(path);
                            listBitmaps[index++] = buildBitmap;
                        } while (cursor1.moveToNext());
                    }

                    float tempResult = compareSimilarity(listBitmaps, bitmap);
                    Log.d("test name + score", "value: " + tempName + ", " + tempResult);

                    if (tempResult < result && tempResult <= 30) {
                        result = tempResult;
                        expectedName = tempName;
                    }

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
        Mat mat1 = new Mat();
        Mat mat2 = new Mat();

        Utils.bitmapToMat(bitmap, mat1);
        double[] result = new double[listBitmaps.length];
        for (int i = 0; i < listBitmaps.length; i++) {
            Utils.bitmapToMat(listBitmaps[i], mat2);

            MatOfInt histSize = new MatOfInt(256);
            MatOfFloat ranges = new MatOfFloat(0f, 256f);
            Mat hist1 = new Mat();
            Mat hist2 = new Mat();
            Imgproc.calcHist(Arrays.asList(mat1), new MatOfInt(0), new Mat(), hist1, histSize, ranges);
            Imgproc.calcHist(Arrays.asList(mat2), new MatOfInt(0), new Mat(), hist2, histSize, ranges);

            Core.normalize(hist1, hist1, 0, 1, Core.NORM_MINMAX, -1, new Mat());
            Core.normalize(hist2, hist2, 0, 1, Core.NORM_MINMAX, -1, new Mat());

            double score = Imgproc.compareHist(hist1, hist2, Imgproc.CV_COMP_CHISQR);
            result[i] = score;
        }

        double avgValue = 0;
        for (double v : result) {
            if (v <= 30) {
                return (float) v;
            }
            avgValue += v;
        }

        avgValue = avgValue / result.length;

        return (float) avgValue;
    }
    public void saveFaceToDB(String[] names, String[] paths) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            for (int i = 0; i < names.length; i++) {
                long imageId = insertFaceImage(db, paths[i]);
                String name = getFullName(db, names[i]);

                if (Objects.equals(name, "-1")) {
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
    private long insertFaceImage(SQLiteDatabase db, String path) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE, path);
        return db.insert(LIST_FACES, null, values);
    }
    private String getFullName(SQLiteDatabase db, String name) {
        Cursor cursor = db.query(FACES, new String[]{COLUMN_NAME}, COLUMN_NAME + "=?", new String[]{name}, null, null, null);
        String fullName = "-1";
        if (cursor.moveToFirst()) {
            fullName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
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
        Cursor cursor = db.query(FACES, new String[]{COLUMN_LIST_INT}, COLUMN_NAME + "=?", new String[]{name}, null, null, null);
        if (cursor.moveToFirst()) {
            String existingList = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LIST_INT));
            String updatedList = existingList + ", " + imageId;
            Log.d("test list", updatedList);
            ContentValues values = new ContentValues();
            values.put(COLUMN_LIST_INT, updatedList);
            db.update(FACES, values, COLUMN_NAME + "=?", new String[]{name});
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
                "VALUES ('" + img_id + "', " + al_id + ", '" + old_path + "')";
        db.execSQL(sql);
        db.close();
    }

    public long deleteImage(long img_id, String album_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(SECURE_IMAGES, COLUMN_MEDIA_ID + " = ? AND " + COLUMN_ALBUM_ID + " = (SELECT " + COLUMN_ALBUM_ID + " FROM " + SECURE_ALBUM + " WHERE " + COLUMN_ALBUM_NAME + " = ?)",
                new String[]{String.valueOf(img_id), album_name});

        db.close();
        return result;
    }

    public int countImages_in_Album(String album_name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT COUNT(*) FROM " + SECURE_IMAGES + " WHERE " + COLUMN_ALBUM_ID + " = (SELECT " + COLUMN_ALBUM_ID + " FROM " + SECURE_ALBUM + " WHERE " + COLUMN_ALBUM_NAME + " = ?)";
        Cursor cursor = db.rawQuery(countQuery, new String[]{album_name});
        int count = 0;
        if (cursor != null) {
            cursor.moveToFirst();
            count = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return count;
    }

    public void delete_Album(String album_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SECURE_ALBUM, COLUMN_ALBUM_NAME + " = ?", new String[]{album_name});
        db.close();

        // Delete the directory from the file system
        String path = "/storage/emulated/0/DCIM/" + album_name;
        File directory = new File(path);
        if (directory.exists() && directory.isDirectory()) {
            boolean success = deleteRecursive(directory);
        }
    }

    boolean deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            File[] contents = fileOrDirectory.listFiles();
            if (contents != null) {
                for (File child : contents) {
                    boolean success = deleteRecursive(child);
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return fileOrDirectory.delete();
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
            if (password.equals(pass)) {
                return true;
            } else return false;
        }
        cursor.close();
        return false;
    }


    public Cursor find_image_ID(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + SECURE_IMAGES + " WHERE " + COLUMN_MEDIA_ID + " = " + id;
        return db.rawQuery(sql, null);
    }

    @SuppressLint("Range")
    public String deleteImage(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT " + COLUMN_ORIGIN_PATH + " FROM " + SECURE_IMAGES + " WHERE " + COLUMN_MEDIA_ID + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});
        String oldpath = "";
        if (cursor.moveToFirst()) {
            oldpath = cursor.getString(cursor.getColumnIndex(COLUMN_ORIGIN_PATH));
        }
        long result = db.delete(SECURE_IMAGES, COLUMN_MEDIA_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return oldpath;
    }
}

