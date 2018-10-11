package com.zxcn.imai.smart.base;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.serialport.SerialPort;
import android.serialport.SerialPortFinder;
import android.util.Log;

import com.bugtags.library.Bugtags;
import com.zxcn.imai.smart.db.DbUtils;
import com.zxcn.imai.smart.service.AlarmBroadcastReceiver;
import com.zxcn.imai.smart.service.ScreenService;
import com.zxcn.imai.smart.util.AdbShellUtils;
import com.zxcn.imai.smart.util.LogUtil;
import com.zxcn.imai.smart.util.SpUtils;
import com.zxcn.imai.smart.util.StringResUtils;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ZXCN1 on 2017/9/1.
 */

public class SmartApplication extends Application {
    public static Context mContext;
    private static SmartApplication instance;
    public static List<Activity> acys = new LinkedList<Activity>();

    public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
    private SerialPort mSerialPort = null;  //全局值

    String TAG = "060_application";
    String PATH = "/dev/ttyMT2";
    int BAUDRATE = 115200;

    @Override
    public void onCreate() {
        super.onCreate();
        Bugtags.start("874a62a0f59dd1d209b691d7e04b514f", this, Bugtags.BTGRemoteConfigStateNone);

        mContext = this;
        //初始化Sp文件
        SpUtils.init(mContext);
        //
        StringResUtils.init(mContext);

        DbUtils.init(mContext);
        //初始化定时任务
        initAlarm();
        //写入设备ID
        setDeviceID();
        //启动应用级的service
        startService(new Intent(getApplicationContext(), ScreenService.class));
    }


    /**
     * 设置设备id
     * android id  取后五位
     */
    private void setDeviceID() {
        String ANDROID_ID = Settings.System.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (null != ANDROID_ID) {
            ANDROID_ID = ANDROID_ID.substring(ANDROID_ID.length() - 5, ANDROID_ID.length());
            AdbShellUtils.runCMD("echo " + ANDROID_ID + "  >  " +
                    "/sys/bus/platform/drivers/zxcn_signal/zxcn_version");
        }
    }

    public static Context getContext() {
        return mContext;
    }

    public static SmartApplication getInstance() {
        if (instance == null) {
            instance = new SmartApplication();
        }
        return instance;
    }

    public void addAcitivity(Activity acy) {
        acys.add(acy);
    }

    public void removeActivity(Activity acy) {
        acys.remove(acy);
    }

    public void finshActivity() {
        for (Activity ac : acys) {
            ac.finish();
            LogUtil.i("acs", ac.getClass().toString());
        }
    }

    private void initAlarm() {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        Calendar targetTime = (Calendar) now.clone();
        targetTime.setTime(new Date());
        targetTime.set(Calendar.HOUR_OF_DAY, 0);
        targetTime.set(Calendar.MINUTE, 0);
        targetTime.set(Calendar.SECOND, 0);
        targetTime.set(Calendar.MILLISECOND, 0);
        if (targetTime.before(now)) {
            targetTime.add(Calendar.DATE, 1);
        }
//        setAlarm(targetTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY);
        setAlarm(now.getTimeInMillis(), 1000);  //10s
    }

    private void setAlarm(long triggerAtMillis, long intervalMillis) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(this, AlarmBroadcastReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        //一天一个周期，不停的发送
        am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, intervalMillis, pendingIntent);
    }

    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
            /* Read serial port para    meters */
            //设置device和波特率
            String path = PATH;
            int baudrate = BAUDRATE;
            Log.v(TAG, "device path is :" + path + ", baudrate is :" + baudrate);

            /* Check parameters */
            if ((path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }

            /* Open the serial port */
            mSerialPort = new SerialPort(new File(path), baudrate, 0, 0);
        }
        return mSerialPort;
    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }
}
