package com.ran.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ran.mobilesafe.bean.BlackNumberInfo;
import com.ran.mobilesafe.bean.PageResult;

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
     * @return int
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
     * @return List<BlackNumberInfo>
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

    /**
     * 分页查询黑名单号码
     *
     * @param page     当前页
     * @param pageSize 每页展示数据条数
     * @return PageResult<BlackNumberInfo>
     * <p/>
     * limit:限制当前多少条数据
     * offset:从第几条开始
     */
    public PageResult<BlackNumberInfo> queryPar(int page, int pageSize) {
        page = page <= 0 ? 1 : page;
        int start = (page - 1) * pageSize;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from blackNumber ", null);
        cursor.moveToNext();
        int total = cursor.getInt(0);
        cursor.close();
        List<BlackNumberInfo> datas = null;
        if (total > 0) {
            cursor = db.rawQuery("select number,mode from blackNumber limit ? offset ?",
                    new String[]{String.valueOf(pageSize), String.valueOf(start)});
            datas = new ArrayList<BlackNumberInfo>();
            BlackNumberInfo info;
            while (cursor.moveToNext()) {
                info = new BlackNumberInfo();
                info.setNumber(cursor.getString(0));
                info.setMode(cursor.getInt(1));

                datas.add(info);
            }
            cursor.close();
        }

        db.close();

        PageResult<BlackNumberInfo> result = new PageResult<BlackNumberInfo>(datas, total, page, pageSize);
        return result;
    }

    /**
     * 分批加载数据
     *
     * @param start 开始位置
     * @param max   最多显示的条数
     * @return PageResult<BlackNumberInfo>
     */
    public PageResult<BlackNumberInfo> queryLoading(int start, int max) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from blackNumber ", null);
        cursor.moveToNext();
        int total = cursor.getInt(0);
        cursor.close();
        List<BlackNumberInfo> datas = null;
        if (total > 0){
            cursor = db.rawQuery("select number,mode from blackNumber order by _id desc limit ? offset ?",
                    new String[]{String.valueOf(max), String.valueOf(start)});
            datas = new ArrayList<BlackNumberInfo>();
            BlackNumberInfo info;
            while (cursor.moveToNext()) {
                info = new BlackNumberInfo();
                info.setNumber(cursor.getString(0));
                info.setMode(cursor.getInt(1));

                datas.add(info);
            }
            cursor.close();
        }

        db.close();
        PageResult<BlackNumberInfo> result = new PageResult<BlackNumberInfo>(datas, total, max);
        return result;
    }
}