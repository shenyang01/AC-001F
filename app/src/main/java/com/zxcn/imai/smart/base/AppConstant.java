package com.zxcn.imai.smart.base;

/**
 * Created by ZXCN1 on 2017/8/8.
 */

public class AppConstant {

    public static final boolean DEBUG = false;

    public static final int PERMISSION_CAMERA = 1;

    public static final String USER_GRANDPA = "1";
    public static final String USER_GRANDMA = "2";
    public static final String USER_FATHER = "3";
    public static final String USER_MATHER = "4";

    public static final int INTERVAL_BACK_PRESS = 2;     //测量结果页，回退键2次点击间隔2s内退到主界面

    public static final String FINGERPRINT = "finger_print";

    public static final String TYPE_BP = "1";
    public static final String BLOOD_POWER_PATCH =
            "/sys/bus/platform/drivers/zxcn_signal/zxcn_blood_power_ctrl";
    public static final String FINGER_POWER_PATCH =
            "/sys/bus/platform/drivers/zxcn_signal/zxcn_finger_5v_power_ctrl";
    public static final String REGEX_ID_CARD = "(^\\d{15}$)|(^\\d{17}([0-9]|X)$)";//正则表达式:验证身份证
    public static final String MAKE_TYPE = "user_type";
}
