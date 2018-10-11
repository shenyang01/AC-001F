package com.zxcn.imai.smart.util;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ZXCN1 on 2017/8/4.
 */

public class SpUtils {

    private static SpUtils INSTANCE;
    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;

    private SpUtils(Context context){
        sp = context.getSharedPreferences("zxcn", Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public static void init(Context context){
        if(null == INSTANCE){
            synchronized (SpUtils.class) {
                if (null == INSTANCE) {
                    INSTANCE = new SpUtils(context);
                }
            }
        }
    }

    public static boolean hasKey(String key) {
        return sp.contains(key);
    }

    public static boolean saveString(String key, String value){
        return editor.putString(key, value).commit();
    }

    public static boolean saveBoolean(String key, boolean value){
        return editor.putBoolean(key, value).commit();
    }

    public static boolean saveInt(String key, int value){
        return editor.putInt(key, value).commit();
    }

    public static boolean saveFloat(String key, float value){
        return editor.putFloat(key, value).commit();
    }

    public static boolean saveLong(String key, long value){
        return editor.putLong(key, value).commit();
    }

    public static String getValue(String key, String defaultValue){
        return sp.getString(key, defaultValue);
    }
    public static boolean getValue(String key, boolean defaultValue){
        return sp.getBoolean(key, defaultValue);
    }
    public static int getValue(String key, int defaultValue){
        return sp.getInt(key, defaultValue);
    }
    public static float getValue(String key, float defaultValue){
        return sp.getFloat(key, defaultValue);
    }
    public static long getValue(String key, long defaultValue){
        return sp.getLong(key, defaultValue);
    }

}
