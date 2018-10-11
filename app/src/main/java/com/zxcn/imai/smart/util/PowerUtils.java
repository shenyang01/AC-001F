package com.zxcn.imai.smart.util;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by xtich on 2017/12/1.
 */

public  class  PowerUtils {
   static PowerManager pm;
    static  PowerManager.WakeLock wakeLock;
    public  static PowerManager.WakeLock getWakeLock(Context context){
        if (pm==null)
          pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        if (wakeLock==null)
          wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "TAG");
       return wakeLock;
    }
}
