package com.zxcn.imai.smart.util;

import android.util.Log;

import com.zxcn.imai.smart.base.SpConstant;


/**
 * Created by xtich on 2017/11/1.
 */

public class LogUtils {
    private static  boolean isDebug=false;
    public static  void LogNet(Object obj){
        if (!isDebug){
            Log.e(SpConstant.API, (String) obj);
        }
    }
    public static  void LogFinger(Object obj){
        if (!isDebug){
            Log.e(SpConstant.FINGER, (String) obj);
        }
    }

    public static  void LogPressure(Object obj){
        if (!isDebug){
            Log.e(SpConstant.PRESSURE, (String) obj);
        }
    }
    public static  void LogSugar(Object obj){
        if (!isDebug){
            Log.e(SpConstant.SUGAR, (String) obj);
        }
    }

    public static void downLoad(Object obj) {
        if (!isDebug){
            Log.e(SpConstant.DOWN, (String) obj);
        }
    }

    public static void up(Object obj) {
        if (!isDebug){
            Log.e(SpConstant.UP, (String) obj);
        }
    }
    public static void save(Object obj) {
        if (!isDebug){
            Log.e(SpConstant.SAVE, (String) obj);
        }
    }
}
