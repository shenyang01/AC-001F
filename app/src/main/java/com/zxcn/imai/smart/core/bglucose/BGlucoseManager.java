package com.zxcn.imai.smart.core.bglucose;

import android.serialport.SerialPort;
import android.util.Log;


import com.zxcn.imai.smart.SmartApplication;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;

public class BGlucoseManager extends BGlucoseService {

    private static final String TAG = "8888_bg";
    /**
     * log打印开关
     */
    private final boolean DEBUG = true;
    private final boolean DEBUG1 = false;
    /**
     * 电源控制
     */
   // final String BLOOD_POWER_PATCH = "/sys/bus/platform/drivers/zxcn_signal/zxcn_power_3v3_ctrl";

 final String BLOOD_POWER_PATCH = "/sys/bus/platform/drivers/zxcn_signal/zxcn_uart3_5v_power_ctrl";

    private IglucoseCallback mCallback;

    private int count_insertRes = 0;  //插入试纸
    private int count_pullRes = 0;    //拔出试纸
    private int count_dropRes = 0;        //滴血事件
    private int count_countRes = 0;       //倒计时事件
    private int count_getresRes = 0;      //获得血糖结果
    private int count_gettempRes = 0; //获取温度，判断血糖设备是否在线

    private int insertEvent = 101;  //插入试纸
    private int pullEvent = 102;    //拔出试纸
    private int dropEvent = 103;        //滴血事件
    private int countEvent = 104;       //倒计时事件
    private int getresEvent = 105;      //获得血糖结果
    private int gettempEvent = 106;  //获取温度正常
    private int TempErrorException = 107;    //温度异常
    private int insertUsedPaperException = 108;    //插入已使用过的试纸

    public BGlucoseManager() {
    }

    public BGlucoseManager(IglucoseCallback callback) {
        this.mCallback = callback;
    }

    public SmartApplication getApplication() {
        return super.mApplication;
    }

    public SerialPort getSerialPort() {
        return super.mSerialPort;
    }

    public OutputStream getOutputStream() {
        return super.mOutputStream;
    }

    /**
     * 1.给血糖模块上电；2.打开串口，开启readthread，通过serialport读取血糖模块返回的数据；3.判断温度是否在正常范围内；4.初始化每个事件的计数
     */
    public void start(SmartApplication mApplication) {
        Log.v(TAG, "BGlucoseManager start ");

        //给血糖模块上电
        new Thread(new Runnable() {
            @Override
            public void run() {

                powerOff();

                try {
                    Thread.currentThread().sleep(10);//毫秒
                } catch (Exception e) {
                }

                powerOn();

            }
        }).start();

        //开启readthread
        super.start(mApplication);

        try {
            Thread.currentThread().sleep(1000);//毫秒
        } catch (Exception e) {
        }

        //获取温度值，正常范围内才可用
        getTemp();

        //初始化每个事件的计数
        count_insertRes = 0;
        count_pullRes = 0;
        count_dropRes = 0;
        count_countRes = 0;
        count_getresRes = 0;
        count_gettempRes = 0;
    }

    /**
     * 1.关闭readthread关闭串口；2.给血糖模块关电
     */
    public void stop() {
        Log.v(TAG, "BGlucoseManager stop ");
        super.stop();
        powerOff();
    }

    /**
     * 上电
     */
    private void powerOn() {
        Log.v(TAG, "BGlucoseManager powerOn ");
        String cmd;
        cmd = "echo 1 > " + BLOOD_POWER_PATCH;
        runCMD(cmd);

        Log.v(TAG, cmd);
    }

    /**
     * 关电
     */
    private void powerOff() {
        Log.v(TAG, "BGlucoseManager powerOff ");
        String cmd;
        cmd = "echo 0 > " + BLOOD_POWER_PATCH;
        runCMD(cmd);

        Log.v(TAG, cmd);
    }

    /**
     * 读串口，根据读取的不同值进行数据处理
     */
    @Override
    protected void onDataReceived(final byte[] buffer, final int size) {
        if (DEBUG) {
            Log.v(TAG, "BGlucoseManager onDataReceived ");
        }

        String out = byteToString(buffer);  //16进制字符串

        if (DEBUG) {
            Log.v(TAG, "out is ：" + out);
        }

        byte[] outByte = byteToHexByteArray(buffer);

        if (outByte[2] == 4 && outByte[3] == -59 && outByte[4] == 0 && outByte[6] == -59) { //插入试纸 -86,85,4,-59,0,0,-59；170,85,4,197,0,0,197

            count_insertRes++;
            Log.v(TAG, "insert event , count_insertRes is : " + count_insertRes);
            mCallback.onHelp(insertEvent, out, "插入试纸");

            if (count_insertRes == 5) {
                Log.v(TAG, "insert event callback, count_insertRes is : " + count_insertRes);
                count_insertRes = 0;
            }

        } else if (outByte[2] == 04 && outByte[3] == -59 && outByte[4] != 0) { //插入已用过的试纸 aa5504c53000F5 -86,85,4,-59, 48, 0,-11；
            //这种情况下，都需要调用start()重新打开串口
            Log.v(TAG, "insert usedpaper event ");

            mCallback.onException(insertUsedPaperException, out, "插入已用过的试纸");
            Log.v(TAG, "insert usedpaper event callback ");

        } else if (outByte[2] == 04 && outByte[3] == -55) { //拔出试纸 -86,85,4,-55, 0, 0,-55
            count_pullRes++;
            Log.v(TAG, "pull event , count_pullRes is : " + count_pullRes);
            mCallback.onHelp(pullEvent, out, "拔出试纸");

            if (count_pullRes == 2) {
                Log.v(TAG, "pull event callback, count_pullRes is : " + count_pullRes);
                count_pullRes = 0;
            }

        } else if (outByte[2] == 04 && outByte[3] == -58) { //滴血事件 -86,85,4,-58, 0, 0,-58；170,85,4,198,0,0,198
            count_dropRes++;
            Log.v(TAG, "drop event , count_dropRes is : " + count_dropRes);
            mCallback.onHelp(dropEvent, out, "滴血事件");

            if (count_dropRes == 1) {
                Log.v(TAG, "drop event callback, count_dropRes is : " + count_dropRes);
                count_dropRes = 0;
            }

        } else if (outByte[2] == 4 && outByte[3] == -57) { //倒计时事件 -86,85,4,-57, 0, 0,-58; 170,85,4,199,outbyte[4]倒计时时间
            count_countRes++;
            Log.v(TAG, "getres event , count_getresRes is : " + count_getresRes);

            mCallback.onHelp(countEvent, out, "倒计时事件");

        } else if (outByte[2] == 9 && outByte[3] == -56) { //获得血糖结果 -86,85,9,-56, 0, 0,-58; 结果为data[8]左移8位，或data[9]，再除以18
            count_getresRes++;
            Log.v(TAG, "getres event , count_getresRes is : " + count_getresRes + "outByte[5] is :" + outByte[5] + "outByte[6] is :" + outByte[6]);

            double result1 = ((byteToInt(outByte[5]) << 8) | byteToInt(outByte[6])) / 18f;

            DecimalFormat df = new DecimalFormat("#.#");
            String result = df.format(result1);

            mCallback.sendResult(getresEvent, out, "获得血糖结果", result);

        } else if (outByte[2] == 5 && outByte[3] == 104) { //获取温度，判断血糖设备是否在线
            count_gettempRes++;
            Log.v(TAG, "gettemp event , count_gettempRes is : " + count_gettempRes);

            double temp = ((byteToInt(outByte[4]) << 8) | byteToInt(outByte[5]))* 0.1;
            Log.v(TAG, "temp  is : " + temp);

            if (10 <= temp && temp <= 40) {
                Log.v(TAG, "gettemp event , temp is : " + temp);
                mCallback.sendResult(gettempEvent, out, "获取温度结果正常", temp+ "");
            } else {
                Log.v(TAG, "temp exception event ");
                mCallback.onException(TempErrorException, out, "temp exception event");
            }
        }
    }

    private static int byteToInt(byte b) {
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
    }

    /**
     * @Description: 获取血糖仪温度。设备激活后才能用。
     * @param: none
     * @return: callback
     */
    public void getTemp() {
        if (DEBUG) {
            Log.v(TAG, "getTemp start");
        }

        String t = "aa55045d00005d";
        byte[] aa = {(byte) 0xaa, (byte) 0x55, (byte) 0x04, (byte) 0x5d, (byte) 0x00, (byte) 0x00, (byte) 0x5d};

        String[] aa16 = new String[10];
        for (int i = 0; i < aa.length; i++) {
            Log.v(TAG, "aa10[" + i + "] is : " + aa[i]);

            aa16[i] = Integer.toHexString(aa[i]);
            Log.v(TAG, "aa16[" + i + "] is : " + aa16[i]);
        }

        try {
            getOutputStream().write(aa);
            getOutputStream().write('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description: 清除错误。
     * @param: none
     * @return: callback
     */
    private void clearError() {
        if (DEBUG) {
            Log.v(TAG, "clearError start");
        }

        String t = "aa 55 04 54 00 00 54";
        byte[] aa = {(byte) 0xaa, (byte) 0x55, (byte) 0x04, (byte) 0x54, (byte) 0x00, (byte) 0x00, (byte) 0x54};

        String[] aa16 = new String[10];
        for (int i = 0; i < aa.length; i++) {
            Log.v(TAG, "aa10[" + i + "] is : " + aa[i]);

            aa16[i] = Integer.toHexString(aa[i]);
            Log.v(TAG, "aa16[" + i + "] is : " + aa16[i]);
        }

        try {
            getOutputStream().write(aa);
            getOutputStream().write('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ****************************************************工具类************************************************************************************
     */

    /**
     * Bytes[]转换为十进制int[] “0 ~ 255”
     * <p>
     * ex: {10101010,1010101} 转换为{170,85}
     */
    public int[] byteArrayToIntArray(byte[] buffer) {
        int[] ret = new int[16];

        for (int i = 0; i < buffer.length; i++) {

            if (DEBUG1) {
                Log.v(TAG, "buffer[" + i + "] is :" + buffer[i]);
            }

            ret[i] = buffer[i] & 0xFF;
            if (DEBUG1) {
                Log.v(TAG, "ret[" + i + "] is :" + ret[i]);
            }
        }
        return ret;
    }

    /**
     * Bytes[]转换为有符号int  “-128 ~ 127”
     * <p>
     * ex: {10101010,1010101} 转换为{-86,85}
     */
    public byte[] byteToHexByteArray(byte[] buffer) {
        String ret = "";

        for (int i = 0; i < buffer.length; i++) {

            int j = buffer[i] & 0xFF;

            if (DEBUG1) {
                Log.v(TAG, "j is :" + j);
            }

            String hex = Integer.toHexString(buffer[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }

        if (DEBUG1) {
            Log.v(TAG, "byteToHexByteArray ret :" + ret);
        }

        String[] aa16 = new String[16];
        byte[] ret1 = hexStringToBytes(ret);
        for (int j = 0; j < ret1.length; j++) {
            if (DEBUG1) {
                Log.v(TAG, "byteToHexByteArray ret10[" + j + "] is : " + ret1[j]);
            }

            aa16[j] = Integer.toHexString(ret1[j]);
            if (DEBUG1) {
                Log.v(TAG, "byteToHexByteArray aa16[" + j + "] is : " + aa16[j]);
            }
        }

        return ret1;
    }

    /**
     * Bytes[] 转换为String
     * ex: {10101010，1010101，100，1011101，0，0，1011101} 转换为"AA55045D00005D"
     */
    public static String byteToString(byte[] buffer) {
        String ret = "";

        for (int i = 0; i < buffer.length; i++) {
            String hex = Integer.toHexString(buffer[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    /**
     * String转换为有符号Bytes[]
     * ex: "aa55045d00005d" 转换为{-86，85，4，93，0，0，93}
     */
    public byte[] hexStringToBytes(String hexString) {
        Log.v("test", "hexString is : " + hexString);
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }

        if (DEBUG1) {
            for (int j = 0; j < d.length; j++) {
                Log.v("test", "d[" + j + "] is : " + d[j]);
            }
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    private static String runCMD(String cmd) {
        String result = null;
        try {
            String[] cmdx = {"/system/bin/sh", "-c", cmd}; // file must

            int ret = ShellExe.execCommand(cmdx);

            result = ShellExe.getOutput();

        } catch (Exception e) {

            result = "ERR.JE";
        }
        return result;
    }

}

