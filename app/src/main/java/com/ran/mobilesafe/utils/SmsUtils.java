package com.ran.mobilesafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 短信备份工具类
 * <p/>
 * 作者: wangxiang on 15/11/5 15:50
 * 邮箱: vonshine15@163.com
 */
public class SmsUtils {
    /**
     * 短信备份回调函数
     */
    public interface BackupSmsCall {
        /**
         * 备份之前逻辑,由调用者实现
         *
         * @param count 备份短信总数
         */
        void backupBefore(int count);

        /**
         * 备份中逻辑,由调用者实现
         *
         * @param process 当前正在备份第几条数据
         */
        void onBackup(int process);
    }

    /**
     * 备份短信
     *
     * @param context
     * @param callback 回调函数,增加代码灵活性
     */
    public static boolean backupSms(Context context, BackupSmsCall callback) {
        // 判断是否存在SD卡
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            ContentResolver contentResolver = context.getContentResolver();

            try {
                File file = new File(Environment.getExternalStorageDirectory() + "/backup/", "sms.xml");
                if (file.exists()) {
                    if (!file.isFile()){
                        file.delete();
                        file.createNewFile();
                    }
                }else{
                    if (!file.getParentFile().exists()){
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                    }
                }

                FileOutputStream fos = new FileOutputStream(file);
                Uri uri = Uri.parse("content://sms/");
                Cursor cursor = contentResolver.query(uri, new String[]{"address", "type", "date", "date_sent", "body"}, null, null, null);

                int count = cursor.getCount();

                callback.backupBefore(count);
                XmlSerializer xmlSerializer = Xml.newSerializer();

                xmlSerializer.setOutput(fos, "utf-8");

                xmlSerializer.startDocument("utf-8", true);

                xmlSerializer.startTag(null, "smss");
                xmlSerializer.attribute(null, "size", String.valueOf(count));
                int process = 0;
                while (cursor.moveToNext()) {
                    xmlSerializer.startTag(null, "sms");
                    xmlSerializer.startTag(null, "address");
                    xmlSerializer.text(cursor.getString(0));
                    xmlSerializer.endTag(null, "address");

                    xmlSerializer.startTag(null, "type");
                    xmlSerializer.text(cursor.getString(1));
                    xmlSerializer.endTag(null, "type");

                    xmlSerializer.startTag(null, "date");
                    xmlSerializer.text(cursor.getString(2));
                    xmlSerializer.endTag(null, "date");

                    xmlSerializer.startTag(null, "date_sent");
                    xmlSerializer.text(cursor.getString(3));
                    xmlSerializer.endTag(null, "date_sent");

                    xmlSerializer.startTag(null, "body");
                    xmlSerializer.text(Crypto.encrypt(Crypto.SEED, cursor.getString(4)));
                    xmlSerializer.endTag(null, "body");

                    xmlSerializer.endTag(null, "sms");
                    process++;

                    callback.onBackup(process);
                }

                xmlSerializer.endTag(null, "smss");

                // 文件生成
                xmlSerializer.endDocument();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
