package com.zxcn.imai.smart.core.spo;

import android.serialport.SerialPort;
import android.util.Log;

import com.zxcn.imai.smart.SmartApplication;

import java.io.OutputStream;

import static java.lang.Thread.sleep;

/**
 * BOxygenManager class
 *
 * @author huangyuan
 * @data 2017/12/14
 */
public class BOxygenManager extends SerialportService {

    private static final String TAG = "060_BOxygenManager";
    /**
     * log打印开关
     */
    private final boolean DEBUG = true;
    private final boolean DEBUG1 = true;

    /**
     * 电源控制
     */
    //    final String BLOOD_POWER_PATCH = "/sys/bus/platform/drivers/zxcn_signal/zxcn_power_3v3_ctrl";
    //    final String BLOOD_POWER_PATCH = "/sys/bus/platform/drivers/zxcn_signal/zxcn_uart3_5v_power_ctrl";
    final String BLOOD_POWER_PATCH = "/sys/bus/platform/drivers/zxcn_signal/zxcn_gpio127_uart1_2_3v3_ctrl";


    /**
     * 串口反馈回来的事件
     * <p>
     * 101;  //检测手指插入结果
     * 102;  //脉率测试过程中的瞬间结果
     * 103;  //血氧测试过程中的瞬间结果
     * 104;  //血氧容积
     * 105;  //脉率平均值
     * 106;  //血氧平均值
     */
    int fingerResult = 101;
    int pluseRateResult = 102;
    int bOxygenResult = 103;
    int bOxygenResult1 = 104;
    int pluseRateAVGResult = 105;
    int bOxygenAVGResult = 106;
    int probeResult = 107;

    /**
     * 解析串口数据控制
     * <p>
     * isPackageHeader 包头正确
     * isHaveFinger 已有手指放入探头
     * isFirstFinger 第一次检测到手指
     * isPutin 检测到手指
     */
    static int count8 = 0;
    boolean isPackageHeader = false;
    boolean isHaveFinger = false;
    boolean isFirstFinger = false;
    boolean isPutin = false;

    final int COUNT = 400;
    int[] sumPR = new int[COUNT];
    int mSumRP = 0;
    int[] sumBO = new int[COUNT];
    int msumBO = 0;
    /**
     * 第一次检测到手指的时间
     * <p>
     * SECONDCOUNT 插入手之後多久開始選取數據
     * PRlEVEL 过滤脉率
     * BOlEVEL 过滤血氧
     */
    long totalSeconds1;
    long totalSeconds2;
    final long SECONDCOUNT = 5;
    final int PRlEVEL = 40;
    final int BOlEVEL = 90;
    private boolean isFrist = true;
    private static BOxygenManager instance;

    private IBOxygenCallback mIBOxygenCallback;


    private BOxygenManager() {
    }

    public static BOxygenManager getInstance() {
        return BPManagerHolder.instance;
    }

    private static class BPManagerHolder {
        private static final BOxygenManager instance = new BOxygenManager();
    }

    public BOxygenManager(IBOxygenCallback callback) {
        this.mIBOxygenCallback = callback;
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
     * 1.给模块上电；2.打开串口，开启readthread，通过inputstream read 串口数据；
     */
    @Override
    public void start(SmartApplication mApplication) {
        Log.v(TAG, "BOxygenManager start ");

        //给血糖模块上电
        new Thread(new Runnable() {
            @Override
            public void run() {
                powerOff();

                try {
                    sleep(50);
                } catch (Exception e) {
                }

                powerOn();
            }
        }).start();

        //开启readthread
        super.start(mApplication);
    }

    /**
     * 1.关闭readthread关闭串口；2.给模块关电
     */
    @Override
    public void stop() {
        Log.v(TAG, "BOxygenManager stop ");
        super.stop();
        isFrist = true;
        count8 = 0;
        powerOff();
        isFirstFinger = false;
    }

    /**
     * 上电
     */
    private void powerOn() {
        Log.v(TAG, "BOxygenManager powerOn ");
        String cmd;
        cmd = "echo 1 > " + BLOOD_POWER_PATCH;
        runCMD(cmd);

        Log.v(TAG, cmd);
    }

    /**
     * 关电
     */
    private void powerOff() {
        Log.v(TAG, "BOxygenManager powerOff ");
        String cmd;
        cmd = "echo 0 > " + BLOOD_POWER_PATCH;
        runCMD(cmd);

        Log.v(TAG, cmd);
    }

    String S1;

    /**
     * 读串口，根据读取的不同值进行数据处理
     */
    @Override
    protected void onDataReceived(final byte[] buffer, final int size) {
        totalSeconds2 = (System.currentTimeMillis()) / 1000;
        if (isFrist) {
            totalSeconds1 = (System.currentTimeMillis()) / 1000;
            isFrist = false;
        }
        //16进制字符串
        String out = byteToString(buffer);

        //找包头8
        int i8 = out.indexOf("8");

        if (i8 >= 0 && (out.length() >= i8 + 11)) {
            //取出有效的字节（10个字节）
            S1 = out.substring(i8, i8 + 10);
            if (DEBUG) {
                Log.v(TAG, "S1 ：" + S1);
            }
            String[] sbuffer = new String[20];
            byte[] bb = hexStringToBytes(S1);
            for (int j = 0; j < bb.length; j++) {
                sbuffer[j] = byteToBit(bb[j]);
            }

            int i0 = Integer.valueOf(sbuffer[0].substring(0, 1));
            int i1 = Integer.valueOf(sbuffer[1].substring(0, 1));
            int i2 = Integer.valueOf(sbuffer[2].substring(0, 1));
            int i3 = Integer.valueOf(sbuffer[3].substring(0, 1));
            int i4 = Integer.valueOf(sbuffer[4].substring(0, 1));
            int maibo = Integer.valueOf(sbuffer[2].substring(3, 4)); //判断手指有没有插入
//            if(sbuffer[2].equals("50")){
//                isPackageHeader = true;
//                mIBOxygenCallback.sendResult(fingerResult, "检测到手指", 0);
//            }
            if (DEBUG) {
                Log.e(TAG, "S1 is ：" + S1);
            }
            isPackageHeader = (i1 == 0 && i2 == 0 && i3 == 0 && i4 == 0 && maibo == 0);

            if (isPackageHeader) {
                mIBOxygenCallback.sendResult(fingerResult, "检测到手指", 0);

                String i26 = sbuffer[2].substring(1, 2);
                String ii3 = sbuffer[3].substring(1, 8);
                String sp = i26 + ii3;

                int pulserate;
                pulserate = Integer.parseInt(sp, 2);

                if (DEBUG) {
                    Log.v(TAG, "pulserate is : " + pulserate);
                }
                //检测到手指5秒后，开始保存脉率结果
                mIBOxygenCallback.sendResult(pluseRateResult, "脉率结果", pulserate);
                if (mSumRP < COUNT && (totalSeconds2 >= (totalSeconds1 + SECONDCOUNT)) && (pulserate >= PRlEVEL)) {
                    sumPR[mSumRP++] = pulserate;
                }
                //血氧解析
                int bo1;
                bo1 = Integer.parseInt(sbuffer[4], 2);
                if (DEBUG) {
                    Log.v(TAG, "bo1 is : " + bo1);
                }
                mIBOxygenCallback.sendResult(bOxygenResult, "血氧结果", bo1);
                //检测到手指5秒后，开始保存血氧结果
                if (msumBO < COUNT && (totalSeconds2 >= (totalSeconds1 + SECONDCOUNT)) && (bo1 >= BOlEVEL)) {
                    sumBO[msumBO++] = bo1;
                }
                //血氧容积解析
                int bo2 = 0;
                bo2 = Integer.parseInt(sbuffer[1], 2);
                if (DEBUG) {
                    Log.v(TAG, "bo2 is : " + bo2);
                }
                mIBOxygenCallback.sendResult(bOxygenResult1, "血氧容积结果", bo2);

                isPackageHeader = false;
            } else {
                mIBOxygenCallback.sendResult(fingerResult, "请插入手指", 1);
            }
        }

    }


    /**
     * ****************************************************工具类************************************************************************************
     */

    /**
     * 把byte转为字符串的bit
     * <p>
     * http://blog.csdn.net/wodeyuer125/article/details/45100319
     */
    public String byteToBit(byte b) {
        return ""
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
    }


    /**
     * @Description: 计算校验位，由前面的十六进制数据按位异或http://blog.csdn.net/acrambler/article/details/45743157
     * @param: "810100028501"
     * @return: "06"
     */
    public String checkcode_0007(String para) {
        int length = para.length() / 2;
        String[] dateArr = new String[length];

        for (int i = 0; i < length; i++) {
            dateArr[i] = para.substring(i * 2, i * 2 + 2);
        }
        String code = "00";
        for (int i = 0; i < dateArr.length; i++) {
            code = xor(code, dateArr[i]);
        }
        Log.v(TAG, "code is :" + code);
        return code;
    }

    private String xor(String strHex_X, String strHex_Y) {
        //将x、y转成二进制形式
        String anotherBinary = Integer.toBinaryString(Integer.valueOf(strHex_X, 16));
        String thisBinary = Integer.toBinaryString(Integer.valueOf(strHex_Y, 16));
        String result = "";
        //判断是否为8位二进制，否则左补零
        if (anotherBinary.length() != 8) {
            for (int i = anotherBinary.length(); i < 8; i++) {
                anotherBinary = "0" + anotherBinary;
            }
        }
        if (thisBinary.length() != 8) {
            for (int i = thisBinary.length(); i < 8; i++) {
                thisBinary = "0" + thisBinary;
            }
        }
        //异或运算
        for (int i = 0; i < anotherBinary.length(); i++) {
            //如果相同位置数相同，则补0，否则补1
            if (thisBinary.charAt(i) == anotherBinary.charAt(i)) {
                result += "0";
            } else {
                result += "1";
            }
        }
        Log.e("code", result);
        return Integer.toHexString(Integer.parseInt(result, 2));
    }

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

    private static int byteToInt(byte b) {
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
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

    /**
     * java运行adb命令函数
     */
    private static String runCMD(String cmd) {
        String result;
        try {
            String[] cmdx = {"/system/bin/sh", "-c", cmd};
            ShellExe.execCommand(cmdx);
            result = ShellExe.getOutput();
        } catch (Exception e) {
            result = "ERR.JE";
        }
        return result;
    }

}

