package com.zxcn.imai.smart.zxing.utils;

import com.zxcn.imai.smart.util.AdbShellUtils;

/**
 蜂鸣器工具类
 */
public class BuzzerUtils {
    /**
     * log打印开关
     */

    /**
     * 单例模式
     */
    private BuzzerUtils() {
    }
    public static BuzzerUtils getInstance () {
        return ShellUtilsHolder.instance;
    }
    private static class ShellUtilsHolder {
        private static final BuzzerUtils instance = new BuzzerUtils();
    }

    /**
     * 嘀一声
     *
     * 嘀两声需调用两次
     */
    public static void di() {
        AdbShellUtils.runCMD("echo 1 > /sys/bus/platform/drivers/zxcn_signal/zxcn_gpio79_uart1_switch_ctrl");
    }
}
