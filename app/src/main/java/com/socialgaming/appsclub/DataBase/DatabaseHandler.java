package com.socialgaming.appsclub.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.socialgaming.appsclub.Item.SubCategoryList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "video_status";

    // event table name
    private static final String TABLE_NAME_VIDEO_DOWNLOAD = "download";

    // event table name
    private static final String TABLE_NAME_FAV = "favourite";

    // event Table Columns names
    private static final String ID = "auto_id";
    private static final String KEY_VIDEO_ID = "video_id";
    private static final String KEY_CATEGORY_ID = "video_category_id";
    private static final String KEY_CATEGORY_NAME = "video_category_name";
    private static final String KEY_VIDEO_NAME = "video_name";
    private static final String KEY_VIDEO_IMAGE_S = "video_image_s";
    private static final String KEY_VIDEO_IMAGE_B = "video_image_b";
    private static final String KEY_VIDEO_VIEW = "video_view";
    private static final String KEY_VIDEO_LIKE_FLAG = "video_like_flag";
    private static final String KEY_VIDEO_LIKE_COUNT = "video_like_count";
    private static final String KEY_VIDEO_URI = "video_uri";
    private static final String KEY_VIDEO_TYPE_LAYOUT = "video_type";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_DOWNLOAD_TABLE = "CREATE TABLE " + TABLE_NAME_VIDEO_DOWNLOAD + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + KEY_VIDEO_ID + " TEXT,"
                + KEY_CATEGORY_ID + " TEXT," + KEY_VIDEO_NAME + " TEXT,"
                + KEY_CATEGORY_NAME + " TEXT," + KEY_VIDEO_IMAGE_S + " TEXT,"
                + KEY_VIDEO_IMAGE_B + " TEXT," + KEY_VIDEO_URI + " TEXT,"
                + KEY_VIDEO_TYPE_LAYOUT + " TEXT"
                + ")";
        Log.d("Query_database",CREATE_DOWNLOAD_TABLE);

        db.execSQL(CREATE_DOWNLOAD_TABLE);

        String CREATE_FAV_TABLE = "CREATE TABLE " + TABLE_NAME_FAV + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + KEY_VIDEO_ID + " TEXT,"
                + KEY_CATEGORY_ID + " TEXT," + KEY_CATEGORY_NAME + " TEXT,"
                + KEY_VIDEO_NAME + " TEXT," + KEY_VIDEO_IMAGE_S + " TEXT,"
                + KEY_VIDEO_IMAGE_B + " TEXT," + KEY_VIDEO_VIEW + " TEXT,"
                + KEY_VIDEO_LIKE_FLAG + " TEXT," + KEY_VIDEO_LIKE_COUNT + " TEXT,"
                + KEY_VIDEO_URI + " TEXT," + KEY_VIDEO_TYPE_LAYOUT + " TEXT"
                + ")";
        db.execSQL(CREATE_FAV_TABLE);

        Log.d("Query_database",CREATE_FAV_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_VIDEO_DOWNLOAD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_FAV);
        onCreate(db);
    }


    //-------------------------Favourite Table-----------------//
    // Adding Favourite Video Status
    public void addDetailFav(SubCategoryList scdList) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_VIDEO_ID, scdList.getId());
        values.put(KEY_CATEGORY_ID, scdList.getCid());
        values.put(KEY_CATEGORY_NAME, scdList.getCategory_name());
        values.put(KEY_VIDEO_NAME, scdList.getVideo_title());
        values.put(KEY_VIDEO_IMAGE_S, scdList.getVideo_thumbnail_s());
        values.put(KEY_VIDEO_IMAGE_B, scdList.getVideo_thumbnail_b());
        values.put(KEY_VIDEO_VIEW, scdList.getTotal_viewer());
        values.put(KEY_VIDEO_LIKE_FLAG, scdList.getAlready_like());
        values.put(KEY_VIDEO_LIKE_COUNT, scdList.getTotal_likes());
        values.put(KEY_VIDEO_URI, scdList.getVideo_url());
        values.put(KEY_VIDEO_TYPE_LAYOUT, scdList.getVideo_layout());


        db.insert(TABLE_NAME_FAV, null, values);
        db.close(); // Closing database connection
    }


    // Getting All Video Status
    public List<SubCategoryList> getVideoDetailFav(String type) {
        List<SubCategoryList> scdLists = new ArrayList<SubCategoryList>();

        String selectQuery;
        if (type.equals("Landscape")) {
            selectQuery = "SELECT  * FROM " + TABLE_NAME_FAV + " WHERE " + KEY_VIDEO_TYPE_LAYOUT + "=" + "'" + type + "'";
        } else {
            selectQuery = "SELECT  * FROM " + TABLE_NAME_FAV + " WHERE " + KEY_VIDEO_TYPE_LAYOUT + "=" + "'" + type + "'";
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                SubCategoryList list = new SubCategoryList();
                list.setId(cursor.getString(1));
                list.setCid(cursor.getString(2));
                list.setCategory_name(cursor.getString(3));
                list.setVideo_title(cursor.getString(4));
                list.setVideo_thumbnail_s(cursor.getString(5));
                list.setVideo_thumbnail_b(cursor.getString(6));
                list.setTotal_viewer(cursor.getString(7));
                list.setAlready_like(cursor.getString(8));
                list.setTotal_likes(cursor.getString(9));
                list.setVideo_url(cursor.getString(10));
                list.setVideo_layout(cursor.getString(11));

                // Adding video to list
                scdLists.add(list);
            } while (cursor.moveToNext());
        }

        // return home_category list
        Collections.reverse(scdLists);
        return scdLists;
    }

    //check video fav or not
    public boolean checkId_Fav(String id) {
        String selectQuery = "SELECT  * FROM " + TABLE_NAME_FAV + " WHERE " + KEY_VIDEO_ID + "=" + id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() == 0) {
            return true;
        } else {
            return false;
        }
    }

    // Updating video view
    public int updateVideoView(String id, String view) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_VIDEO_VIEW, view);

        // updating row
        return db.update(TABLE_NAME_FAV, values, KEY_VIDEO_ID + "=" + id, null);
    }

    // Updating video like
    public int updateVideoLike(String id, String like_count, String selected_flag) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_VIDEO_LIKE_COUNT, like_count);
        values.put(KEY_VIDEO_LIKE_FLAG, selected_flag);

        // updating row
        return db.update(TABLE_NAME_FAV, values, KEY_VIDEO_ID + "=" + id, null);
    }

    // Deleting video status
    public boolean deleteFav(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME_FAV, KEY_VIDEO_ID + "=" + id, null) > 0;
    }


    //-------------------------VIDEO STATUS DOWNLOAD Table-----------------//
    // Adding New Download Video
    public void addVideoDownload(SubCategoryList scdList) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_VIDEO_ID, scdList.getId());
        values.put(KEY_CATEGORY_ID, scdList.getCid());
        values.put(KEY_VIDEO_NAME, scdList.getVideo_title());
        values.put(KEY_CATEGORY_NAME, scdList.getCategory_name());
        values.put(KEY_VIDEO_IMAGE_S, scdList.getVideo_thumbnail_s());
        values.put(KEY_VIDEO_IMAGE_B, scdList.getVideo_thumbnail_b());
        values.put(KEY_VIDEO_URI, scdList.getVideo_url());
        values.put(KEY_VIDEO_TYPE_LAYOUT, scdList.getVideo_layout());


        db.insert(TABLE_NAME_VIDEO_DOWNLOAD, null, values);
        db.close(); // Closing database connection
    }


    // Getting Download Video
    public List<SubCategoryList> getVideoDownload(String type) {
        List<SubCategoryList> scdLists = new ArrayList<SubCategoryList>();

        String selectQuery;
        if (type.equals("Landscape")) {
            selectQuery = "SELECT  * FROM " + TABLE_NAME_VIDEO_DOWNLOAD + " WHERE " + KEY_VIDEO_TYPE_LAYOUT + "=" + "'" + type + "'";
        } else {
            selectQuery = "SELECT  * FROM " + TABLE_NAME_VIDEO_DOWNLOAD + " WHERE " + KEY_VIDEO_TYPE_LAYOUT + "=" + "'" + type + "'";
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                SubCategoryList list = new SubCategoryList();
                list.setId(cursor.getString(1));
                list.setCid(cursor.getString(2));
                list.setVideo_title(cursor.getString(3));
                list.setCategory_name(cursor.getString(4));
                list.setVideo_thumbnail_s(cursor.getString(5));
                list.setVideo_thumbnail_b(cursor.getString(6));
                list.setVideo_url(cursor.getString(7));
                list.setVideo_layout(cursor.getString(8));

                // Adding video to list
                scdLists.add(list);
            } while (cursor.moveToNext());
        }

        // return video list
        Collections.reverse(scdLists);
        return scdLists;
    }

    //check video download or not
    public boolean checkId_video_download(String id) {
        String selectQuery = "SELECT  * FROM " + TABLE_NAME_VIDEO_DOWNLOAD + " WHERE " + KEY_VIDEO_ID + "=" + id;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() == 0) {
            return true;
        } else {
            return false;
        }
    }

    // Deleting download video
    public boolean delete_video_download(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME_VIDEO_DOWNLOAD, KEY_VIDEO_ID + "=" + id, null) > 0;
    }

}
