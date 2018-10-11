package com.zxcn.imai.smart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.serialport.SerialPort;
import android.serialport.SerialPortFinder;
import android.util.Log;

import com.zxcn.imai.smart.activity.common.MainActivity;
import com.zxcn.imai.smart.db.DbUtils;
import com.zxcn.imai.smart.service.AlarmBroadcastReceiver;
import com.zxcn.imai.smart.service.ScreenService;
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

public class SmartApplication extends Application{
    String TAG = "060_application";

    public static Context mContext;
    public static SmartApplication instance;
    public static List<Activity> acys = new LinkedList<Activity>();

    public SerialPortFinder mSerialPortFinder = new SerialPortFinder();

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        //初始化Sp文件
        SpUtils.init(mContext);
        //
        StringResUtils.init(mContext);

        DbUtils.init(mContext);

        //初始化定时任务
        initAlarm();

        //启动应用级的service
        startService(new Intent(getApplicationContext(), ScreenService.class));
    }
    private Thread.UncaughtExceptionHandler handler = (t, e) -> {
        restartApp(); //发生崩溃异常时,重启应用
    };
    private void restartApp() {
        Log.e("tag","程序异常重启");
        Intent intent = new Intent(this, MainActivity.class);
        @SuppressLint("WrongConstant") PendingIntent restartIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, intent,Intent.FLAG_ACTIVITY_NEW_TASK);
        //退出程序
        AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000,
                restartIntent); // 2秒钟后重启应用

        //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    public static SmartApplication getInstance(){
        if (instance==null){
            instance=new SmartApplication();
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
        }
    }
    private void initAlarm(){
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        Calendar targetTime = (Calendar) now.clone();
        targetTime.setTime(new Date());
        targetTime.set(Calendar.HOUR_OF_DAY, 0);
        targetTime.set(Calendar.MINUTE, 0);
        targetTime.set(Calendar.SECOND, 0);
        targetTime.set(Calendar.MILLISECOND, 0);
        if (targetTime.before(now)){
            targetTime.add(Calendar.DATE, 1);
        }
//        Log.e("application", now.get(Calendar.YEAR)+ "-" + now.get(Calendar.MONTH) + "-" + now.get(Calendar.DAY_OF_MONTH));
        //setAlarm(targetTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY);
//        setAlarm(now.getTimeInMillis(), 20*1000);
    }

    private void setAlarm(long triggerAtMillis, long intervalMillis){
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(this, AlarmBroadcastReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        //一天一个周期，不停的发送
        am.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, intervalMillis, pendingIntent);
    }

    private SerialPort mSerialPort = null;  //全局值
    String PATH = "/dev/ttyMT3";
    int BAUDRATE = 9600;
    public SerialPort getSerialPort()throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {

            //设置device和波特率
            String path = PATH;
            int baudrate = BAUDRATE;
            Log.v(TAG,"device path is :"+path+", baudrate is :"+baudrate);

			/* Check parameters */
            if ((path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }

			/* Open the serial port */
            mSerialPort = new SerialPort(new File(path), baudrate, 0 ,0);
        }
        return mSerialPort;
    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }

    String PATH_spo = "/dev/ttyMT1";
    int BAUDRATE_spo = 4800;
    private SerialPort mSerialPort_spo = null;  //全局值
    public SerialPort getSerialPort_spo()throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort_spo == null) {

            //设置device和波特率
            String path = PATH_spo;
            int baudrate = BAUDRATE_spo;
            Log.v(TAG,"device path is :"+path+", baudrate is :"+baudrate);

			/* Check parameters */
            if ((path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }

			/* Open the serial port */
            mSerialPort_spo = new SerialPort(new File(path), baudrate, 0 ,1);
        }
        return mSerialPort_spo;
    }

    public void closeSerialPort_spo() {
        if (mSerialPort_spo != null) {
            mSerialPort_spo.close();
            mSerialPort_spo = null;
        }
    }



}
