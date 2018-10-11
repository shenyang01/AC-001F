package com.zxcn.imai.smart.activity.pressure;

import android.util.Log;

import com.zxcn.imai.smart.util.ShellExe;

/**
 ECGManager 对应的工具类
 */
public class BPUtils {
    private static final String TAG = "8888_ECGUtils";
    /**
     * log打印开关
     */
    private final boolean DEBUG1 = true;

    private BPUtils() {
    }

    public static BPUtils getInstance () {
        return BPUtilsHolder.instance;
    }

    /**
     * 静态内部类,只有在装载该内部类时才会去创建单例对象
     */
    private static class BPUtilsHolder {
        private static final BPUtils instance = new BPUtils();
    }


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

    public String genCMD(String inputCmd) {
        String m = checkcode_0007(inputCmd);
        String m1 = " ";
        if (m.length() == 2) {
            m1 = inputCmd.concat(m);
        } else if (m.length() == 1) {
            m1 = inputCmd.concat("0").concat(m);
        }
        Log.v(TAG, "CMD is：" + m1);
        return m1;
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
    public static String runCMD(String cmd) {
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
