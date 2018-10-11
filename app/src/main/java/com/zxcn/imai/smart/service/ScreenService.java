package com.zxcn.imai.smart.service;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;

import com.zxcn.imai.smart.SplishActivity;
import com.zxcn.imai.smart.activity.common.MainActivity;
import com.zxcn.imai.smart.activity.common.SettingActivity;
import com.zxcn.imai.smart.board.BootBroadcastReceiver;
import com.zxcn.imai.smart.util.SetStateUtils;

/**
 * Created by xtich on 2017/11/30.
 */

public class ScreenService extends Service {

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private int tag = 0;

    public void onCreate() {
        super.onCreate();
        /* 注册屏幕唤醒时的广播
         * 连按五下电源键进入设置模式
         */
        IntentFilter mScreenOnFilter = new IntentFilter("android.intent.action.SCREEN_ON");
        mScreenOnFilter.addAction("zxcg_setting");
        ScreenService.this.registerReceiver(mScreenOReceiver, mScreenOnFilter);

        /* 注册机器锁屏时的广播 */
        IntentFilter mScreenOffFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
        ScreenService.this.registerReceiver(mScreenOReceiver, mScreenOffFilter);
         //监听充电状态的广播
        ScreenService.this.registerReceiver(this.mBatteryReceiver, new IntentFilter(Intent
                .ACTION_BATTERY_CHANGED));
    }

    public void onDestroy() {
        super.onDestroy();
        ScreenService.this.unregisterReceiver(mScreenOReceiver);
    }

    /**
     * 锁屏的管理类叫KeyguardManager，
     * 通过调用其内部类KeyguardLockmKeyguardLock的对象的disableKeyguard方法可以取消系统锁屏，
     * newKeyguardLock的参数用于标识是谁隐藏了系统锁屏
     */
    private BroadcastReceiver mScreenOReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null)
                return;
            if (action.equals("zxcg_setting")) {
                Log.e("tag"," zxcg_setting -----");
                startActivity(new Intent(getApplicationContext(), SettingActivity.class));
            } else if (action.equals("android.intent.action.SCREEN_ON")) {
                Log.e("SC", "—— SCREEN_ON ——");
            } else if (action.equals("android.intent.action.SCREEN_OFF")) {
                Log.e("SC", "—— SCREEN_OFF ——");

                /***
                 *强制控制屏幕点亮
                 */
//
//                PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);// init powerManager
//                PowerManager.WakeLock mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP| PowerManager.SCREEN_DIM_WAKE_LOCK,"target"); //
//                mWakelock.acquire();
                if (tag >= 1) {
                    Intent lockScreen = new Intent(context, SplishActivity.class);
                    lockScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(lockScreen);
                }
            }
        }

    };


    private BroadcastReceiver mBatteryReceiver = new BootBroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent arg1) {
            super.onReceive(context, arg1);
            int status = arg1.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
            Activity activity = SetStateUtils.newInstance().getCurrentActivity();
            switch (status) {
                case BatteryManager.BATTERY_STATUS_CHARGING: //充电中
                case BatteryManager.BATTERY_STATUS_FULL:
                    SetStateUtils.newInstance().setBtState(true);
                    if (activity!=null&&!(activity instanceof SplishActivity)) {
                        startActivity(new Intent(getApplicationContext(), SplishActivity.class));
                        activity.finish();
                    }
                    tag = 1;
                    break;
                default:  //放电中
                    SetStateUtils.newInstance().setBtState(false);
                    if (activity instanceof SplishActivity) {
                        activity.finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                    tag = 0;
                    break;
            }
        }
    };

}
