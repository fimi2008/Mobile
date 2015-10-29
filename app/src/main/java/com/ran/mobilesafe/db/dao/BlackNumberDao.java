package com.ran.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ran.mobilesafe.bean.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 黑名单查询dao
 * 作者: wangxiang on 15/10/29 15:53
 * 邮箱: vonshine15@163.com
 */
public class BlackNumberDao {

    private final BlackNumberOpenHelper helper;

    public BlackNumberDao(Context context) {
        helper = new BlackNumberOpenHelper(context);
    }

    /**
     * 新增黑名单号码
     *
     * @param number 黑名单号码
     * @param mode   拦截模式
     */
    public boolean add(String number, int mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("mode", mode);

        long rowId = db.insert("blackNumber", null, values);
        if (rowId == -1) {
            return false;
        }
        return true;
    }

    /**
     * 删除号码
     *
     * @param number 电话号码
     */
    public boolean del(String number) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rowNumber = db.delete("blackNumber", "number = ?", new String[]{number});
        if (rowNumber == 0) {
            return false;
        }
        return true;
    }

    /**
     * 更新黑名单号码
     *
     * @param number 电话号码
     * @param mode   拦截模式
     */
    public boolean update(String number, int mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("mode", mode);
        int rowNumber = db.update("blackNumber", values, "number = ?", new String[]{number});
        if (rowNumber == 0) {
            return false;
        }
        return true;
    }

    /**
     * 根据手机号码查询该号码的拦截模式
     *
     * @param number
     * @return
     */
    public int getModeByNumber(String number) {
        int mode = 0;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("blackNumber", new String[]{"mode"}, "number = ?", new String[]{number}, null, null, null);
        if (cursor.moveToNext()) {
            mode = cursor.getInt(0);
        }
        cursor.close();
        db.close();

        return mode;
    }

    /**
     * 查询所有的黑名单号码
     *
     * @return
     */
    public List<BlackNumberInfo> queryAll() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("blackNumber", new String[]{"number", "mode"}, null, null, null, null, null);
        List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
        BlackNumberInfo info;
        while (cursor.moveToNext()) {
            info = new BlackNumberInfo();
            info.setNumber(cursor.getString(0));
            info.setMode(cursor.getInt(1));

            list.add(info);
        }
        cursor.close();
        db.close();
        return list;
    }
}