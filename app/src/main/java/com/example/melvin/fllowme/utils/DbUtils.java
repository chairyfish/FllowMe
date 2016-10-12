package com.example.melvin.fllowme.utils;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.melvin.fllowme.bean.Users;
import com.example.melvin.fllowme.database.MyDB;

import java.util.Date;

/**
 * Created by Melvin on 2016/8/30.
 */
public class DbUtils {
    public static void addFriendData(SQLiteDatabase dbWriter, Users friend, String headpic_path) {
        ContentValues cv = new ContentValues();
        cv.put(MyDB.COLUMN_CONTACT_FRIEND, friend.getUsername());
        cv.put(MyDB.COLUMN_CONTACT_REMARK, friend.getNickname());
        cv.put(MyDB.COLUMN_CONTACT_HP_PATH, headpic_path);
        cv.put(MyDB.COLUMN_CONTACT_UPDATE_TIME, friend.getUpdatedAt());
        dbWriter.insert(MyDB.TABLE_CONTACT_NAME, null, cv);
    }

    public static void updateFriendRemark(SQLiteDatabase dbWriter, String username, String remark) {
        ContentValues cv = new ContentValues();
        cv.put(MyDB.COLUMN_CONTACT_REMARK, remark);
        dbWriter.update(MyDB.TABLE_CONTACT_NAME, cv, MyDB.COLUMN_CONTACT_FRIEND + " = ?", new String[]{username});
    }

    public static Date transformdate(String riqi) {
        String yy, mm, dd, h, m, s;
        int year, month, day, hour, minute, second;

        yy = riqi.substring(0, 4);
        mm = riqi.substring(5, 7);
        dd = riqi.substring(8, 10);
        h = riqi.substring(11, 13);
        m = riqi.substring(14, 16);
        s = riqi.substring(17);

        year = Integer.valueOf(yy);
        month = Integer.valueOf(mm);
        day = Integer.valueOf(dd);
        hour = Integer.valueOf(h);
        minute = Integer.valueOf(m);
        second = Integer.valueOf(s);

        Date date = new Date(year - 1900, month, day, hour, minute, second);
        Log.i("String", date.toString());
        return date;
    }

}
