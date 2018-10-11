package com.zxcn.imai.smart.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.zxcn.imai.smart.service.AlarmBroadcastReceiver;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by ZXCN1 on 2017/9/14.
 */

public class AlarmManagerUtil {
    public static void sendUpdateBroadcastRepeat(Context ctx){
        Intent intent =new Intent(ctx, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0);

        //开始时间
        long firstime= SystemClock.elapsedRealtime();

        AlarmManager am = (AlarmManager) ctx.getSystemService(ALARM_SERVICE);
        //一天一个周期，不停的发送广播
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime, 60*1000, pendingIntent);
    }
}
