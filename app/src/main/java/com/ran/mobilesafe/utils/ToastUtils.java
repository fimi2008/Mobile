package com.ran.mobilesafe.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * 吐司提示工具类(已区分是否在主线程)
 *
 * 作者: wangxiang on 15/10/20 13:16
 * 邮箱: vonshine15@163.com
 */
public class ToastUtils {

    /**
     * 为了简化Toast提示显示
     *
     * @param context
     * @param str
     */
    public static void show(final Activity context, final String str){
        if ("main".equals(Thread.currentThread().getName())){
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
        }else{
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
