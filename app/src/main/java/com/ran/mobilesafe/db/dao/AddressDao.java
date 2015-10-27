package com.ran.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 手机号码归属地,数据库查询
 * <p/>
 * 作者: wangxiang on 15/10/26 15:52
 * 邮箱: vonshine15@163.com
 */
public class AddressDao {
    // 注意该路径必须是这个目录data/data目录的文件,否则数据库访问不到
    private static final String PATH = "data/data/com.ran.mobilesafe/files/address.db";

    public static String getAddress(String number) {
        String address = "未知号码";
        // 获取数据库对象
        SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);

        // 手机号码特点: 1 + (3,4,5,6,7,8) + (9位数字)
        // 正则表达式: ^1[3-8]\d{9}$
        if (number.matches("^1[3-8]\\d{9}$")) { // 匹配手机号码格式
            Cursor cursor = db.rawQuery("select location from data2 where id=(select outkey from data1 where id = ? )",
                    new String[]{number.substring(0, 7)});
            if (cursor.moveToNext()) {
                address = cursor.getString(0);
            }
            cursor.close();
        } else if (number.matches("^\\d+$")) { // 匹配数字
            switch (number.length()) {
                case 3:
                    address = "报警电话";
                    break;
                case 4:
                    address = "模拟器号码";
                    break;
                case 5:
                    address = "客服电话";
                    break;
                case 8:
                    address = "客服电话";
                    break;
                default:
                    if (number.startsWith("0") && number.length() > 10) {// 有可能是长途电话
                        // 有些区号是4位,有些区号是3位(包括0)
                        Cursor cursor = db.rawQuery("select location from data2 where area = ?", new String[]{number.substring(1, 4)});
                        if (cursor.moveToNext()) {
                            address = cursor.getString(0);
                        }
                        cursor.close();
                    }
                    break;
            }
        }
        db.close(); // 关闭数据库
        return address;
    }
}
