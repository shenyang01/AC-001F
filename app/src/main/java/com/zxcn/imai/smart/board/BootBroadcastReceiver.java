package com.zxcn.imai.smart.board;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zxcn.imai.smart.activity.common.MainActivity;

/**
 * Created by xtich on 2017/9/29.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {

    static final String ACTION = "android.intent.action.ACTION_SHUTDOWN"; //关机广播

    @Override
    public void onReceive(Context context, Intent intent) {
        String string = intent.getAction();
        if (string == null)
            return;
        if (string.equals(ACTION)) {
            Intent mainActivityIntent = new Intent(context, MainActivity.class);  // 要启动的Activity
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainActivityIntent);
        }
    }
}
