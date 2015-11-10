package com.ran.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ran.mobilesafe.properties.ParameterUtils;

import java.util.Map;
import java.util.Set;

/**
 * SharedPreferences工具类
 * <p/>
 * 作者: wangxiang on 15/11/10 09:32
 * 邮箱: vonshine15@163.com
 */
public class SharedPreferencesUtils {

    private static final String SP_NAME = ParameterUtils.SP_NAME;

    /**
     * SharedPreferences保存信息
     *
     * @param context
     * @param key
     * @param object
     */
    public static void putObject(Context context, String key, Object object) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        if (object instanceof Integer) {
            sp.edit().putInt(key, (Integer) object).commit();
        }
        if (object instanceof Boolean) {
            sp.edit().putBoolean(key, (Boolean) object).commit();
        }
        if (object instanceof String) {
            sp.edit().putString(key, (String) object).commit();
        }
        if (object instanceof Float) {
            sp.edit().putFloat(key, (Float) object).commit();
        }
        if (object instanceof Long) {
            sp.edit().putLong(key, (Long) object).commit();
        }
        if (object instanceof Set) {
            sp.edit().putStringSet(key, (Set) object).commit();
        }
    }

    public static int getInt(Context context, String key, int object){
        return (Integer)getObject(context, key, object);
    }

    public static boolean getBoolean(Context context, String key, boolean object){
        return (Boolean)getObject(context, key, object);
    }

    public static String getString(Context context, String key, String object){
        return (String)getObject(context, key, object);
    }

    public static Float getFloat(Context context, String key, float object){
        return (Float)getObject(context, key, object);
    }

    public static long getLong(Context context, String key, long object){
        return (Long)getObject(context, key, object);
    }

    public static Set<String> getStringSet(Context context, String key, Set<String> object){
        return (Set<String>)getObject(context, key, object);
    }

    public static Map<String, ?> getAll(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getAll();
    }

    private static Object getObject(Context context, String key, Object object){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        if (object instanceof Integer) {
            return sp.getInt(key, (Integer) object);
        }
        if (object instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) object);
        }
        if (object instanceof String) {
            return sp.getString(key, (String) object);
        }
        if (object instanceof Float) {
            return sp.getFloat(key, (Float) object);
        }
        if (object instanceof Long) {
            return sp.getLong(key, (Long) object);
        }
        if (object instanceof Set) {
            return sp.getStringSet(key, (Set) object);
        }

        return null;
    }

}
