package com.zxcn.imai.smart.activity.pressure;

import android.os.Bundle;
import android.serialport.SerialPort;
import android.util.Log;


import com.zxcn.imai.smart.base.SmartApplication;

import java.io.IOException;
import java.io.OutputStream;

/**
 * ECGManager class
 *
 * @author huangyuan
 * @data 2017/12/14
 */
public class BPManager extends SerialportService_BP {

    private static final String TAG = "060_BPManager";
    /**
     * log打印开关
     */
    private final boolean DEBUG = true;

    /**
     * 命令行
     */
    private String opencmddata_s = "BEB001B0ce";  //BE B0 01 B0 ce  唤醒指令；
    private String startcmddata_s = "BEB001c036";  //BE B0 01 c0 36	启动测试命令；
    private String stopcmddata_s = "BEB001c168";  // BE B0 01 c1 68 	测试过程中停止命令；
    private String closecmddata_s = "BEB001d0ab";  //BE B0 01 d0 ab	系统休眠命令。
    /**
     * 从机应答结果
     */
    private String SUCCESS_s = "D0C20200002F"; //D0 C2 02 00 00 2F命令执行成功；208 194 2 0 0 47
    private String FAIL_s = "D0C202FFFF9B";  //D0 C2 02 FF FF 9B从机忙无法处理；208 194 2 255 255 155
    private String NOUSE_s = "D0C20200FF1A";  //D0 C2 02 00 FF 1A指令无效。 208 194 2 0 255 26

    /**
     * 串口反馈回来的事件
     * <p>
     */
    int SUCCESS = 1;
    int FAIL = 2;
    int NOUSE = 3;

    /**
     * define bundle keys
     */
    public static final String PRESSURE = "pressure";

    private int[] sendBPData = new int[10];
    private int sendtestData;


    public BPManager() {
    }

    public static BPManager getInstance() {
        return BPManagerHolder.instance;
    }

    private static class BPManagerHolder {
        private static final BPManager instance = new BPManager();
    }

    /**
     * 设置callback
     */
    private static IBPCallback mIBPCallback;

    public static void setCallback(IBPCallback callback) {
        BPManager.mIBPCallback = callback;
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
     * 1.给血压上电；2.打开串口，开启readthread，通过serialport读取血压模块返回的数据；
     */
    @Override
    public void start(SmartApplication mApplication) {
        Log.v(TAG, " start ");

        //开启readthread
        super.start(mApplication);
    }

    /**
     * 1.关闭readthread关闭串口；2.给血压模块关电
     */
    @Override
    public void stop() {
        Log.v(TAG, " stop ");
        super.stop();
    }

    /**
     * 读串口，根据读取的不同值进行数据处理
     */
    @Override
    protected void onDataReceived(final byte[] buffer, final int size) {
        if (DEBUG) {
            Log.v(TAG, "ECGManager onDataReceived ");
        }

        //16进制字符串
        String out = BPUtils.getInstance().byteToString(buffer);
        if (DEBUG) {
            Log.e(TAG, "out is xieya：" + out);
        }

        //找包头8
        int id0c2 = out.indexOf("D0C2");
        Log.e(TAG, "id0c2 ：" + id0c2);



        String s1 = out.substring(0, 2);
        String s2 = out.substring(2, 4);
        String s3 = out.substring(4, 6);
        String s4 = out.substring(6, 8);
        String s5 = out.substring(8, 10);
        String s6 = out.substring(10, 12);
        String s7 = out.substring(12, 14);
        String s8 = out.substring(14, 16);
        String s9 = out.substring(16, 18);
        String s10 = out.substring(18, 20);

        String stateres = out.substring(0, 12);

        if (stateres.equals(SUCCESS_s)) {

            if (DEBUG) {
                Log.e(TAG, "命令执行成功");
            }

            mIBPCallback.onHelp(SUCCESS, "命令执行成功");

        } else if (stateres.equals(FAIL_s)) {

            if (DEBUG) {
                Log.e(TAG, "命令执行失败");
            }

            mIBPCallback.onHelp(FAIL, "命令执行失败");

        } else if (stateres.equals(NOUSE_s)) {

            if (DEBUG) {
                Log.e(TAG, "命令无效");
            }

            mIBPCallback.onHelp(NOUSE, "命令无效");

        } else if (s1.equals("D0") && s2.equals("C2") && s3.equals("04") && s4.equals("CB")) {
            Bundle bundle = new Bundle();

            // D0 C2 04 CB
            sendBPData[0] = Integer.valueOf(s5, 16);
            sendBPData[1] = Integer.valueOf(s6, 16);
            sendBPData[2] = Integer.valueOf(s7, 16);


            if (DEBUG) {
                Log.e(TAG, "测量中， sendBPData[0]:" + sendBPData[0]
                        + "， sendBPData[1]:" + sendBPData[1]
                        + "， sendBPData[2]:" + sendBPData[2]
                );
            }

            bundle.putIntArray(PRESSURE, sendBPData);

            mIBPCallback.onMeasure(bundle);
        } else if (s1.equals("D0") && s2.equals("C2") && s3.equals("05") && s4.equals("CC")) {
            Bundle bundle = new Bundle();

            // D0 C2 05 CC
            sendBPData[0] = Integer.valueOf(s5, 16);
            sendBPData[1] = Integer.valueOf(s6, 16);
            sendBPData[2] = Integer.valueOf(s7, 16);

            if (DEBUG) {
                Log.e(TAG, "测量结束， sendBPData[0]:" + sendBPData[0]
                        + "， sendBPData[1]:" + sendBPData[1]
                        + "， sendBPData[2]:" + sendBPData[2]
                );
            }

            bundle.putIntArray(PRESSURE, sendBPData);
            mIBPCallback.onEnd(bundle);
            Log.e(TAG,"sendBPData "+sendBPData[0]+"     "+sendBPData[1]+"     " + sendBPData[2]);
            if (sendBPData[0] == 1 && sendBPData[1] == 0 && sendBPData[2] == 0) {
                //ERR1：传感器震荡异常
                Log.v(TAG, "ERR1");

            } else if (sendBPData[0] == 2 && sendBPData[1] == 0 && sendBPData[2] == 0) {
                //ERR2：检测不到足够的心跳或算不出血压
                Log.v(TAG, "ERR2");

            } else if (sendBPData[0] == 3 && sendBPData[1] == 0 && sendBPData[2] == 0) {
                //ERR3：测量结果异常
                Log.v(TAG, "ERR3");

            } else if (sendBPData[0] == 4 && sendBPData[1] == 0 && sendBPData[2] == 0) {
                //ERR4：袖带过松或漏气(10秒内加压不到30mmHg)
                Log.v(TAG, "ERR4");

            } else if (sendBPData[0] == 5 && sendBPData[1] == 0 && sendBPData[2] == 0) {
                //ERR5：气管被堵住
                Log.v(TAG, "ERR5");

            } else if (sendBPData[0] == 6 && sendBPData[1] == 0 && sendBPData[2] == 0) {
                //ERR6：测量时压力波动大
                Log.v(TAG, "ERR6");

            } else if (sendBPData[0] == 7 && sendBPData[1] == 0 && sendBPData[2] == 0) {
                //ERR7：压力超过上限
                Log.v(TAG, "ERR7");

            } else if (sendBPData[0] == 8 && sendBPData[1] == 0 && sendBPData[2] == 0) {
                //ERR8：仪器未标定
                Log.v(TAG, "ERR8");

            }
        } else if (s1.equals("D0") && s2.equals("C2") && s3.equals("03") && s4.equals("CE")) {
            // D0 C2 03 CE 00 90 5B
            sendtestData = (Integer.valueOf(s5, 16) << 8) | Integer.valueOf(s6, 16);

            if (DEBUG) {
                Log.e(TAG, "静态压力值:" + sendtestData);
            }

            mIBPCallback.onTestMeasure(0000, "正在加压静态压", sendtestData);

        } else if (s1.equals("D0") && s2.equals("C2") && s3.equals("03") && s4.equals("CD")) {
            // D0 C2 03 CE 00 90 5B
            sendtestData = (Integer.valueOf(s5, 16) << 8) | Integer.valueOf(s6, 16);

            if (DEBUG) {
                Log.e(TAG, "静态压力值:" + sendtestData);
            }
            mIBPCallback.onTestMeasure(1111, "正在加压固定压", sendtestData);
        }


    }


    /**
     * @Description: 唤醒指令
     * @param: none
     * @return: callback
     */
    public void openBP() {
        if (DEBUG) {
            Log.v(TAG, "openBP start");
        }

        byte[] bb = BPUtils.getInstance().hexStringToBytes(opencmddata_s);

        try {
            getOutputStream().write(bb);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            getOutputStream().write(bb);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @Description: 启动测试命令
     * @param: none
     * @return: callback
     */
    public void startBP() {
        if (DEBUG) {
            Log.v(TAG, "openBP start");
        }

        byte[] bb = BPUtils.getInstance().hexStringToBytes(startcmddata_s);

        try {
            getOutputStream().write(bb);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            getOutputStream().write(bb);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @Description: 测试过程中停止命令
     * @param: none
     * @return: callback
     */
    public void stopBP() {
        if (DEBUG) {
            Log.v(TAG, "openBP start");
        }

        byte[] bb = BPUtils.getInstance().hexStringToBytes(stopcmddata_s);

        try {
            getOutputStream().write(bb);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            getOutputStream().write(bb);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @Description: 测试过程中停止命令
     * @param: none
     * @return: callback
     */
    public void closeBP() {
        if (DEBUG) {
            Log.v(TAG, "openBP start");
        }

        byte[] bb = BPUtils.getInstance().hexStringToBytes(closecmddata_s);

        try {
            getOutputStream().write(bb);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            getOutputStream().write(bb);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * @Description: 设置静态压
     * @param: none
     * @return: callback
     */
    public void setStaticstate() {
        if (DEBUG) {
            Log.e(TAG, "setStatic start");
        }

        byte[] bb = BPUtils.getInstance().hexStringToBytes("BEB001c457");

        try {
            getOutputStream().write(bb);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            getOutputStream().write(bb);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @Description: 设置固态压力值
     * @param: none
     * @return: callback
     */
    public void setSolidstate(String ss) {
        if (DEBUG) {
            Log.v(TAG, "setSolidstate start");
        }

        byte[] bb = BPUtils.getInstance().hexStringToBytes(ss);

        try {
            getOutputStream().write(bb);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            getOutputStream().write(bb);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description: 停止固态加压
     * @param: none
     * @return: callback
     */
    public void stopSolidstate() {
        if (DEBUG) {
            Log.v(TAG, "stopSolidstate start");
        }

        byte[] bb = BPUtils.getInstance().hexStringToBytes("BEB001c3D4");

        try {
            getOutputStream().write(bb);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            getOutputStream().write(bb);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

