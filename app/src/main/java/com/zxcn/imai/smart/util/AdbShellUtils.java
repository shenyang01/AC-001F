package com.zxcn.imai.smart.util;

/**
 AdbShellUtils工具类
 */
public class AdbShellUtils {
    private static final String TAG = "8888_AdbShellUtils";
    /**
     * log打印开关
     */
    private final boolean DEBUG1 = true;

    /**
     * 单例模式
     */
    private AdbShellUtils() {
    }
    public static AdbShellUtils getInstance () {
        return ShellUtilsHolder.instance;
    }
    private static class ShellUtilsHolder {
        private static final AdbShellUtils instance = new AdbShellUtils();
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
