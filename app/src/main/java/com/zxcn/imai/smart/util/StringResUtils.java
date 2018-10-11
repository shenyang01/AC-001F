package com.zxcn.imai.smart.util;

import android.content.Context;

import com.zxcn.finger.ZXFingerprintHelper;
import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.base.AppConstant;

/**
 * Created by ZXCN1 on 2017/8/10.
 */

public class StringResUtils {

    public static Context mContext;

    private StringResUtils(Context context) {
        mContext = context;
    }

    public static void init(Context context) {
        if (null == mContext) {
            synchronized (StringResUtils.class) {
                if (null == mContext) {
                    new StringResUtils(context);
                }
            }
        }
    }

    public static void showFpInfo(int msgID) {
        if (AppConstant.DEBUG) {
            showDebugFpInfo(msgID);
        } else {
            showReleaseFpInfo(msgID);
        }

    }

    private static void showDebugFpInfo(int msgID) {
        switch (msgID) {
            case 1001:      // 等待按下手指
                ToastUtils.toastShort(mContext, R.string.toast_fp_put);
                break;
            case 1002:      // 手指按下
                ToastUtils.toastShort(mContext, R.string.toast_fp_identify);
                break;
            case 1003:      // 手指抬起
                ToastUtils.toastShort(mContext, R.string.toast_fp_put_again);
                break;
        }
    }

    private static void showReleaseFpInfo(int msgID) {
//        switch (msgID) {
//            case ZXFingerprintHelper.ERROL_OK1:
//                ToastUtils.toastShort(mContext, R.string.toast_fp_get_again);
//                break;
//            case ZXFingerprintHelper.ERROL_FAIL1:
//                ToastUtils.toastShort(mContext, R.string.toast_fp_pick_up);
//                break;
//            case ZXFingerprintHelper.MATCH_OK1:
//            case ZXFingerprintHelper.MATCH_FAIL1:
//                ToastUtils.toastShort(mContext, R.string.toast_fp_get_ing);
//                break;
//            case ZXFingerprintHelper.GETIMAGE_TIMEOUT1:
//            case ZXFingerprintHelper.ERROR_DOWN_CHAR:
//            case ZXFingerprintHelper.ERROR_GEN_CHAR:
//            case ZXFingerprintHelper.ERROR_GET:
//            case ZXFingerprintHelper.ERROR_TIMEOUT:
//            case ZXFingerprintHelper.ERROR_MATCH:
//                ToastUtils.toastShort(mContext, R.string.toast_fp_get_error);
//                break;
//        }
    }

    public static String getFpErrorTimesStr(int times) {
        String res = mContext.getResources().getString(R.string.concat_fp_error_time);
        return String.format(res, times);
    }

    public static String getValidaCode(){
        String code = String.valueOf((int) (Math.random()*10000));
        if(code.length() < 4 ) {
            if(code.length() == 3) {
                code = "0" + code;
            } else if (code.length() == 2) {
                code = "00" + code;
            } else {
                code = "000" + code;
            }
        }
        return code;
    }
}
