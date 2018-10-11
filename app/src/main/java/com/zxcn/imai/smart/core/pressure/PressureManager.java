package com.zxcn.imai.smart.core.pressure;

import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;

public class PressureManager {

    private static final String TAG = "060_p";
    /**
     * log打印开关
     */
    private final boolean DEBUG = true;
    /**
     * 电源控制
     */
    final String BLOOD_PRESS_PATCH = "/sys/bus/platform/drivers/zxcn_signal/zxcn_blood_ctrl";
    final String ALL_POWER_PATCH = "/sys/bus/platform/drivers/zxcn_signal/zxcn_all_power";
    /**
     * callback
     * retOpenSuc: 成功打开血压计设备
     * retCloseSuc：成功关闭血压计设备
     * retStartSuc：成功启动血压测量
     * retStopSuc：成功停止血压测量
     */
    private int retOpenPressureSuc = 00;
    private int retOpenPressureFail = 01;
    private int retOpenSuc = 10;
    private int retOpenFail = 11;
    private int retCloseSuc = 20;
    private int retCloseFail = 21;
    private int retStartSuc = 30;
    private int retStartFail = 31;
    private int retStopSuc = 40;
    private int retStopFail = 41;
    /**
     * 控制线程的运行/停止
     */
    private boolean stopOpenThread = false;
    private boolean stopCloseThread = false;
    private boolean stopStartThread = false;
    private boolean stopStopThread = false;
    /**
     * 命令
     */
    //字符串
    private String opencmddata_s = "BE B0 01 B0 ce";  //BE B0 01 B0 ce  唤醒指令；
    private String startcmddata_s = "BE B0 01 c0 36";  //BE B0 01 c0 36	启动测试命令；
    private String stopcmddata_s = "BE B0 01 c1 68";  // BE B0 01 c1 68 	测试过程中停止命令；
    private String closecmddata_s = "BE B0 01 d0 ab";  //BE B0 01 d0 ab	系统休眠命令。

    //int数组
    private int[] opencmddata = new int[10];
    private int[] startcmddata = new int[10];
    private int[] stopcmddata = new int[10];
    private int[] closecmddata = new int[10];

    /**
     * 从机应答结果
     */
    //字符串
    private String SUCCESS_s = "D0 C2 02 00 00 2F"; //D0 C2 02 00 00 2F命令执行成功；208 194 2 0 0 47
    private String FAIL_s = "D0 C2 02 FF FF 9B";  //D0 C2 02 FF FF 9B从机忙无法处理；208 194 2 255 255 155
    private String NOUSE_s = "D0 C2 02 00 FF 1A";  //D0 C2 02 00 FF 1A指令无效。 208 194 2 0 255 26

    private int[] SUCCESS = {208, 194, 2, 0, 0, 47};
    private int[] FAIL = {208, 194, 2, 255, 255, 155};
    private int[] NOUSE = {208, 194, 2, 0, 255, 26};

    /**
     * 从机返回值
     */
    private int[] retOpen = new int[6];
    private int[] retClose = new int[6];
    private int[] retStart = new int[6];
    private int[] retStop = new int[6];

    /**
     * 获取测量血压值
     */
    private boolean stopGetBPdata = false;
    private int[] a16_getBPdata1 = new int[30];

    private int[] sendData = new int[10];
    /**
     * define bundle keys
     */
    public static final String PRESSURE = "pressure";

    private IpressureCallback callback;

    private static int[] rd_data = {0};
    private int[] wr_data = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public PressureManager() {
    }

    public PressureManager(IpressureCallback callback) {
        this.callback = callback;
    }

    /**
     *  加载so库
     */
    static {
        System.loadLibrary("native-lib");
    }

    private native int open();

    private native int close();

    private native int[] writeCmd(int length, int[] intArray);

    private native int[] readCmd(int[] array);

    /**
     * test
     */
    private void test() {

        opencmddata = stringArrayToIntArray(opencmddata_s);
        startcmddata = stringArrayToIntArray(startcmddata_s);
        stopcmddata = stringArrayToIntArray(stopcmddata_s);
        closecmddata = stringArrayToIntArray(closecmddata_s);

        SUCCESS = stringArrayToIntArray(SUCCESS_s);
        FAIL = stringArrayToIntArray(FAIL_s);
        NOUSE = stringArrayToIntArray(NOUSE_s);

        for (int i1 = 0; i1 < 5; i1++) {
            Log.d(TAG, " opencmddata[" + i1 + "] is : " + opencmddata[i1]);
        }

        for (int i2 = 0; i2 < 5; i2++) {
            Log.d(TAG, " startcmddata[" + i2 + "] is : " + startcmddata[i2]);
        }

        for (int i3 = 0; i3 < 5; i3++) {
            Log.d(TAG, " stopcmddata[" + i3 + "] is : " + stopcmddata[i3]);
        }

        for (int i4 = 0; i4 < 5; i4++) {
            Log.d(TAG, " closecmddata[" + i4 + "] is : " + closecmddata[i4]);
        }

        for (int j1 = 0; j1 < 5; j1++) {
            Log.d(TAG, " SUCCESS[" + j1 + "] is : " + SUCCESS[j1]);
        }

        for (int j2 = 0; j2 < 5; j2++) {
            Log.d(TAG, " FAIL[" + j2 + "] is : " + FAIL[j2]);
        }

        for (int j3 = 0; j3 < 5; j3++) {
            Log.d(TAG, " NOUSE[" + j3 + "] is : " + NOUSE[j3]);
        }
    }

    /**
     * 调试方法，UI输入命令
     */
    public void writeFromJava(int length, String[] putArray) {

        if (DEBUG) {
            Log.d(TAG, " writeFromJava length is : " + length);

            for (int j = 0; j < putArray.length; j++) {

                Log.d(TAG, " writeFromJava putArray is : " + putArray[j]);

                wr_data[j] = Integer.parseInt(putArray[j], 16);

            }
        }

        if (DEBUG) {
            for (int i = 0; i < wr_data.length; i++) {
                Log.d(TAG, " writeFromJava wr_data is : " + wr_data[i]);
            }
        }

        int[] ret = writeCmd(length, wr_data);

        for (int i = 0; i < ret.length; i++) {
            Log.v(TAG, "ret[" + i + "] is :" + ret[i]);
        }
    }

    /**
     * @Description: 打开血压计，作用：设置波特率
     * @param:
     * @return:
     */
    public boolean openPressure() {
        if (DEBUG) {
            Log.v(TAG, "openPressure start");
        }
        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    Thread.currentThread().sleep(2000);//毫秒
                } catch (Exception e) {
                }
            }
        }).start();

        int ret = open();
        if (ret > 0) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * @Description: 关闭血压计
     * @param:
     * @return: 返回值为0表示成功关闭血压计，返回-1表示关闭血压计失败
     */
    public boolean closePressure() {

        int ret = close();

        String cmd;
        cmd = "echo 0 > " + ALL_POWER_PATCH;
        runCMD(cmd);

        try {
            Thread.currentThread().sleep(500);//毫秒
        } catch (Exception e) {
        }

        cmd = "echo 1 > " + ALL_POWER_PATCH;
        runCMD(cmd);

        if (ret != 0) {
            if (DEBUG) {
                Log.v(TAG, "closePressure失败 ret是：" + ret);
            }
            return false;

        } else {
            if (DEBUG) {
                Log.v(TAG, "closePressure成功 ret是：" + ret);
            }
            return true;
        }
    }

    /**
     * @Description: 打开血压计设备
     * @param: none
     * @return: callback
     */
    public void openBP() {

        if (DEBUG) {
            Log.v(TAG, "openBP start");
        }

        stopOpenThread = false;

        boolean ret = openPressure();
        if (ret == true) {
            callback.onHelp(retOpenPressureSuc,"openPressure成功");
        } else {
            callback.onHelp(retOpenPressureFail,"openPressure失败");
        }

        Runnable runnable = new Runnable() {
            public void run() {

                while (true) {

                    if (stopOpenThread) {
                        return;
                    }

                    opencmddata = stringArrayToIntArray(opencmddata_s);
                    if (DEBUG) {
                        for (int i = 0; i < opencmddata.length; i++) {
                            Log.v(TAG, "opencmddata[" + i + "] is :" + opencmddata[i]);
                        }
                    }

                    writeCmd(5, opencmddata);

                    try {
                        Thread.currentThread().sleep(300);//毫秒
                    } catch (Exception e) {
                    }

                    writeCmd(5, opencmddata);

                    retOpen = getResult();

                    int ret[] = new int[6];

                    for (int i = 0; i < 6; i++) {
                        Log.v(TAG, "openBP retOpen[" + i + "] is :" + retOpen[i]);
                        ret[i] = retOpen[i];
                    }

                    if (isEqual(ret, SUCCESS)) {
                        if (DEBUG) {
                            Log.v(TAG, "打开血压计设备成功");
                        }
                        stopOpenThread = true;
                        callback.onSuccess(retOpenSuc, "打开血压计设备成功");

                    } else {
                        if (DEBUG) {
                            Log.v(TAG, "打开血压计设备失败");
                        }
                        callback.onFail(retOpenFail, "打开血压计设备失败");
                    }

                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * @Description: 停止openBP线程
     * @param: none
     * @return: none
     */
    public void stopOpenBP() {
        if (DEBUG) {
            Log.v(TAG, "stopOpenBP start");
        }
        stopOpenThread = true;
    }

    /**
     * @Description: 关闭血压计设备
     * @param: none
     * @return: callback
     */
    public void closeBP() {

        if (DEBUG) {
            Log.v(TAG, "closeBP start");
        }
        stopCloseThread = false;
        //        openPressure();

        Runnable runnable = new Runnable() {
            public void run() {
                while (true) {
                    if (stopCloseThread) {
                        return;
                    }

                    closecmddata = stringArrayToIntArray(closecmddata_s);
                    if (DEBUG) {
                        for (int i = 0; i < closecmddata.length; i++) {
                            Log.v(TAG, "closecmddata[" + i + "] is :" + closecmddata[i]);
                        }
                    }

                    writeCmd(5, closecmddata);

                    retClose = getResult();

                    int ret[] = new int[6];

                    for (int i = 0; i < 6; i++) {
                        Log.v(TAG, "closeBP retClose[" + i + "] is :" + retClose[i]);
                        ret[i] = retClose[i];
                    }

                    if (isEqual(ret, SUCCESS)) {
                        if (DEBUG) {
                            Log.v(TAG, "关闭血压计设备成功");
                        }
                        stopCloseThread = true;
                        callback.onSuccess(retCloseSuc, "关闭血压计设备成功");
                    } else {
                        if (DEBUG) {
                            Log.v(TAG, "关闭血压计设备失败");
                            callback.onFail(retCloseFail, "关闭血压计设备失败");
                        }
                    }

                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * @Description: 停止CloseBP线程
     * @param: none
     * @return: none
     */
    public void stopCloseBP() {
        if (DEBUG) {
            Log.v(TAG, "stopCloseBP start");
        }
        stopCloseThread = true;
    }

    /**
     * @Description: 启动血压测量
     * @param: none
     * @return: callback
     */
    public void startMeassure() {
        if (DEBUG) {
            Log.v(TAG, "startMeassure");
        }

        stopStartThread = false;

        openPressure();

        Runnable runnable = new Runnable() {
            public void run() {
                while (true) {

                    if (stopStartThread) {
                        return;
                    }

                    startcmddata = stringArrayToIntArray(startcmddata_s);
                    if (DEBUG) {
                        for (int i = 0; i < startcmddata.length; i++) {
                            Log.v(TAG, "startcmddata[" + i + "] is :" + startcmddata[i]);
                        }
                    }

                    writeCmd(5, startcmddata);

                    retStart = getResult();

                    int ret[] = new int[6];

                    for (int i = 0; i < 6; i++) {
                        Log.v(TAG, "startMeassure retStart[" + i + "] is :" + retStart[i]);
                        ret[i] = retStart[i];
                    }

                    if (isEqual(ret, SUCCESS)) {
                        if (DEBUG) {
                            Log.v(TAG, "启动测量成功");
                        }
                        stopStartThread = true;
                        callback.onSuccess(retStartSuc, "启动测量成功");
                    } else {
                        if (DEBUG) {
                            Log.v(TAG, "启动测量失败");
                        }
                        callback.onFail(retStartFail, "启动测量失败");
                    }

                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        stopMeassure();
                    }
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * @Description: 停止StartMeassure线程
     * @param: none
     * @return: none
     */
    public void stopStartMeassure() {
        if (DEBUG) {
            Log.v(TAG, "stopStartMeassure");
        }
        stopStartThread = true;
    }

    /**
     * @Description: 停止血压测量
     * @param:
     * @return:
     */
    public void stopMeassure() {
        if (DEBUG) {
            Log.v(TAG, "stopMeassure");
        }

        stopStopThread = false;

        Runnable runnable = new Runnable() {
            public void run() {
                while (true) {

                    if (stopStopThread) {
                        return;
                    }

                    stopcmddata = stringArrayToIntArray(stopcmddata_s);
                    if (DEBUG) {
                        for (int i = 0; i < stopcmddata.length; i++) {
                            Log.v(TAG, "stopcmddata[" + i + "] is :" + stopcmddata[i]);
                        }
                    }

                    writeCmd(5, stopcmddata);

                    retStop = getResult();

                    int ret[] = new int[6];

                    for (int i = 0; i < 6; i++) {
                        Log.v(TAG, "stopMeassure retStop[" + i + "] is :" + retStop[i]);
                        ret[i] = retStop[i];
                    }

                    if (isEqual(ret, SUCCESS)) {
                        if (DEBUG) {
                            Log.v(TAG, "停止测量成功");
                        }
                        stopStopThread = true;
                        callback.onSuccess(retStopSuc, "停止测量成功");
                    } else {
                        if (DEBUG) {
                            Log.v(TAG, "停止测量失败");
                        }
                        callback.onFail(retStopFail, "停止测量失败");
                    }

                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * @Description: 停止StopMeassure线程
     * @param: none
     * @return: none
     */
    public void stopStopMeassure() {
        if (DEBUG) {
            Log.v(TAG, "stopStopMeassure");
        }
        stopStopThread = true;
    }

    /**
     * @Description: 读取血压数据
     * @param: none
     * @return: callback
     */
    public void getBPdata() {

        if (DEBUG) {
            Log.v(TAG, "获取血压数据 getBPdata");
        }

        stopGetBPdata = false;

        Runnable runnable = new Runnable() {
            public void run() {
                while (true) {

                    if (DEBUG) {
                        Log.v(TAG, "getBPdata start");
                    }

                    //主动停止读取血压计数据
                    if (stopGetBPdata) {
                        if (DEBUG) {
                            Log.v(TAG, "主动停止读取血压计数据, stopGetBPdata is : " + stopGetBPdata);
                        }
                        return;
                    }

                    try {

                        a16_getBPdata1 = readCmd(rd_data);

                        if (DEBUG) {
                            for (int a = 0; a < a16_getBPdata1.length; a++) {
                                Log.v(TAG, "getBPdata1 a16_getBPdata1[" + a + "] is :" + a16_getBPdata1[a]);
                            }
                        }

                        Bundle bundle = new Bundle();

                        for (int i = 0; i < a16_getBPdata1.length; i++) {

                            if (a16_getBPdata1[i] == 208 && a16_getBPdata1[i + 1] == 194 && a16_getBPdata1[i + 2] == 4 && a16_getBPdata1[i + 3] == 203) {

                                Log.v(TAG, "callback.onMeasure");

                                sendData[0] = a16_getBPdata1[i + 4];
                                sendData[1] = a16_getBPdata1[i + 5];
                                sendData[2] = a16_getBPdata1[i + 6];

                                if (DEBUG) {
                                    for (int b = 0; b < sendData.length; b++) {
                                        Log.v(TAG, "onMeasure sendData sendData[" + b + "] is :" + sendData[b]);
                                    }
                                }

                                bundle.putIntArray(PRESSURE, sendData);

                                callback.onMeasure(bundle);

                            } else if (a16_getBPdata1[i] == 208 && a16_getBPdata1[i + 1] == 194 && a16_getBPdata1[i + 2] == 5 && a16_getBPdata1[i + 3] == 204) {

                                Log.v(TAG, "callback.onEnd");

                                sendData[0] = a16_getBPdata1[i + 4];
                                sendData[1] = a16_getBPdata1[i + 5];
                                sendData[2] = a16_getBPdata1[i + 6];

                                if (DEBUG) {
                                    for (int c = 0; c < sendData.length; c++) {
                                        Log.v(TAG, "onEnd sendData sendData[" + c + "] is :" + sendData[c]);
                                    }
                                }

                                bundle.putIntArray(PRESSURE, sendData);

                                callback.onEnd(bundle);
                                stopGetBPdata = true;
                            }
                        }

                    } catch (Exception e) {
                        if (DEBUG) {
                            Log.v(TAG, "Exception e");
                            System.out.println("unexpected:" + e.getMessage());
                        }
                        //                        stopGetBPdata = true;
                    }

                    try {
                        Thread.sleep(50L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        stopGetBPdata = true;
                    }
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * @Description: 停止读取血压数据
     * @param: none
     * @return: none
     */
    public void stopgetBPdata() {
        if (DEBUG) {
            Log.v(TAG, "停止获取血压数据 stopgetBPdata");
        }
        stopGetBPdata = true;

    }

    /**
     * @Description: 从C++读取buffer数据
     * @param:
     * @return:
     */
    private int[] getResult() {
        if (DEBUG) {
            Log.v(TAG, "getResult");
        }
        int[] a16 = new int[20];

        a16 = readCmd(rd_data);

        for (int i = 0; i < a16.length; i++) {
            Log.v(TAG, "a16[" + i + "] is :" + a16[i]);
        }
        return a16;
    }

    /**
     * @Description: 16进制字符串转换为int数组
     * @param:
     * @return:
     */
    private int[] stringArrayToIntArray(String putArray) {

        int[] intArray = new int[10];
        String[] inString = putArray.split(" ");

        for (int i = 0; i < inString.length; i++) {

            if (DEBUG) {
                //                Log.d(TAG, " stringArrayToIntArray putArray is : " + inString[i]);
            }

            intArray[i] = Integer.parseInt(inString[i], 16);

            if (DEBUG) {
                //                Log.d(TAG, " stringArrayToIntArray putArray is : " + intArray[i]);
            }
        }

        return intArray;
    }

    /**
     * @Description: 判断两个数组内容是否完全相等
     * @param:
     * @return:
     */
    private boolean isEqual(int[] arrayA, int[] arrayB) {

        for (int i = 0; i < arrayA.length; i++) {
            if (DEBUG) {
                Log.d(TAG, " isEqual arrayA[" + i + "] is : " + arrayA[i]);
            }
        }

        for (int j = 0; j < arrayB.length; j++) {
            if (DEBUG) {
                Log.d(TAG, " isEqual arrayB[" + j + "] is : " + arrayB[j]);
            }
        }

        if (arrayA.length != arrayB.length) {
            Log.v(TAG, "两个数组不相等1！" + "arrayA.length is ：" + arrayA.length + ", " + "arrayB.length is : " + arrayB.length);
            return false;
        } else {
            for (int i = 0; i < arrayA.length; i++) {
                if (arrayA[i] != arrayB[i]) {
                    Log.v(TAG, "两个数组不相等2");
                    return false;
                }
            }
            Log.v(TAG, "两个数组相等");
            return true;
        }
    }

    /**
     * @Description: 把int[] first与int[] second合并
     * @param:
     * @return:
     */
    private int[] concat(int[] first, int[] second) {
        int[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);

        //        for (int i = 0; i < result.length; i++) {
        //            Log.d(TAG, " result[ " + i + "] is :" + result[i]);
        //        }
        return result;
    }

    /**
     * @Description: 获取转换后的命令数组
     * @param:
     * @return:
     */
    private int[] getCmd(String cmd) {
        return stringArrayToIntArray(cmd);
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

