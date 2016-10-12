package com.example.melvin.fllowme.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Melvin on 2016/8/30.
 */
public class MyDB extends SQLiteOpenHelper {

    public static final String TABLE_CONTACT_NAME = "contact";
    public static final String TABLE_NOTE_NAME = "note";
    public static final String COLUMN_CONTACT_FRIEND = "friend";
    public static final String COLUMN_CONTACT_REMARK = "remark";
    public static final String COLUMN_CONTACT_HP_PATH = "headpic_path";
    public static final String COLUMN_CONTACT_UPDATE_TIME = "update_time";
    public static final String COLUMN_NOTE_ID = "_id";
    public static final String COLUMN_NOTE_LONG = "longitude";//经度
    public static final String COLUMN_NOTE_LAI = "latitude";//纬度
    public static final String COLUMN_NOTE_CONTENT = "content";
    public static final String[] COLUMN_NOTE_PIC_PATH = {"path1", "path2", "path3"};
    public static final String COLUMN_NOTE_DATE = "date";

    public MyDB(Context context) {
        super(context, "followme", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CONTACT_NAME + " ("
                + COLUMN_CONTACT_FRIEND + " TEXT PRIMARY KEY NOT NULL,"
                + COLUMN_CONTACT_REMARK + " TEXT NOT NULL DEFAULT \"\""
//        +COLUMN_CONTACT_HP_PATH+" TEXT NOT NULL,"
//        +COLUMN_CONTACT_UPDATE_TIME+" TEXT NOT NULL"
                + ")");

        db.execSQL(
                "CREATE TABLE " + TABLE_NOTE_NAME + " ("
                        + COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COLUMN_NOTE_LONG + " REAL NOT NULL,"
                        + COLUMN_NOTE_LAI + " REAL NOT NULL ,"
                        + COLUMN_NOTE_CONTENT + " TEXT NOT NULL DEFAULT \"\","
                        + COLUMN_NOTE_PIC_PATH[0] + " TEXT NOT NULL DEFAULT \"\","
                        + COLUMN_NOTE_PIC_PATH[1] + " TEXT NOT NULL DEFAULT \"\","
                        + COLUMN_NOTE_PIC_PATH[2] + " TEXT NOT NULL DEFAULT \"\","
                        + COLUMN_NOTE_DATE + " TEXT NOT NULL"
                        + ")"
        );
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
