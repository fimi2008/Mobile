package com.ran.mobilesafe.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 吐司提示工具类
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
    public static void show(Context context, String str){
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}
