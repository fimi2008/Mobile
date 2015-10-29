package com.ran.mobilesafe.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 作者: wangxiang on 15/10/29 15:53
 * 邮箱: vonshine15@163.com
 */
public class BlackNumberOpenHelper extends SQLiteOpenHelper{

    public BlackNumberOpenHelper(Context context) {
        super(context, "safe.db", null, 1);
    }

    /**
     * blacknumber 表名
     * _id 主键自增长
     * number 手机号码
     * mode 拦截模式,1-短信拦截,2-电话拦截,3-全部拦截
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
    db.execSQL("create table blacknumber (_id integer primary key autoincrement, number varchar(20), mode varchar(2))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
