package com.zxcn.imai.smart.core.finger;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.cjt2325.cameralibrary.util.LogUtil;
import com.za.finger.ZAandroid;

/**
 * Created by ZXCN1 on 2017/9/9.
 */

public class ZXFingerprintHelperBak {

    private final String TAG = "ZXFingerprintHelper";

    public static final int STATUS_OPEN = 1;
    public static final int STATUS_ENROLL_START = 2;  //开始采集，提示用户按压手指
    public static final int STATUS_ENROLL_AGAIN = 22; //开始采集第二枚指纹图像，提示用户重新按压指纹
    public static final int STATUS_MATCH_START = 3;
    public static final int STATUS_SUCCESS = 4;//成功

    public static final int ERROL_OK1 = 1;
    public static final int ERROL_FAIL1 = 2;
    public static final int GETIMAGE_TIMEOUT1 = 3;
    public static final int GETIMAGEING1 = 4;
    public static final int GETIMAGE_OK1 = 5;
    public static final int GETIMAGE_FAIL1 = 6;
    public static final int GETONECHAR_OK1 = 7;
    public static final int GETONECHAR_FAIL1 = 8;
    public static final int REGMODULE_FAIL1 = 9;
    public static final int COM_ERROR1 = 10;
    public static final int DOWNCHAR_FAIL1 = 11;
    public static final int MATCHFPFROMSYSTEM_OK1 = 12;
    public static final int MATCHFPFROMSYSTEM_FAIL1 = 13;

    int DEV_ADDR = -1;

    private int testcount = 0;
    /**
     * 这个是干嘛的？
     */
    private int fpcharbuf = 1;
    /**
     * 这个是干嘛的？
     */
    private int iPageID = 0;

    private long mStartTime;
    private long mEndTime;
    private int mStatus;

    byte[] pTempletbase = new byte[2304];
    private int EROLL_TIMEOUT = 10000;      //采集指纹超时
    private int MATCH_TIMEOUT = 10000;      //匹配指纹超时

    private Handler objHandler_fp = new Handler();

    private ZAandroid zAandroid = new ZAandroid();

    private boolean stop = true;
//    private final int

    private Context mContext;

    private IFpIdentifyCallBackBak mIdentifyCallBack;
    private FingerOpenListenerBak mFingerOpenListener;

    /**
     * 当前工作的runnable
     */
    private Runnable workRunnable;

    public ZXFingerprintHelperBak(Context context) {
        mContext = context;
    }

    public void setOpenListener(FingerOpenListenerBak fingerOpenListener) {
        mFingerOpenListener = fingerOpenListener;
    }

    public void setFingerListener(IFpIdentifyCallBackBak iFpIdentifyCallBack) {
        mIdentifyCallBack = iFpIdentifyCallBack;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public int getStatus() {
        return mStatus;
    }

    public void open(){
        Runnable r = new Runnable() {
            public void run() {
                int ret = zAandroid.OpenDevs(mContext, 2, 0, 0);
                if(null != mFingerOpenListener) {
                    if(ret == 103) {
                        mFingerOpenListener.usbFail(ret);
                        Log.e(TAG, "usbfail,ret is " + ret);
                    } else if(ret == 101) {
                        mFingerOpenListener.openDeviceSuccess(ret);
                        mStatus = STATUS_OPEN;
                        Log.e(TAG, "opensuccess,ret is " + ret);
                    } else {
                        mFingerOpenListener.openDeviceFail(ret);
                        Log.e(TAG, "openfail,ret is " + ret);
                    }
                }
            }
        };
        Thread s = new Thread(r);
        s.start();
    }

    public void startEnroll() {
        stop = false;
        fpcharbuf = 1;
        reSetTime();
        mStartTime = System.currentTimeMillis();
        mStatus = STATUS_ENROLL_START;
        workRunnable = fperollTasks_tosystem;
        objHandler_fp.removeCallbacks(workRunnable);
        objHandler_fp.post(workRunnable);
    }

    public void startMatch() {
        stop = false;
        fpcharbuf = 1;
        reSetTime();
        mStartTime = System.currentTimeMillis();
        mStatus = STATUS_MATCH_START;
        workRunnable = fpmatchTasks_fromsystem;
        objHandler_fp.removeCallbacks(workRunnable);
        objHandler_fp.post(workRunnable);
    }

    public void stop() {
        stop = true;
    }

    private Runnable fperollTasks_tosystem = new Runnable() {
        public void run() {
            if (stop ) {
                return;
            }
            if (null == mIdentifyCallBack) {
                throw new NullPointerException("mIdentifyCallBack is null, can't run this task!");
            }
            String temp = "";
            mEndTime = System.currentTimeMillis();
            long timeCount = mEndTime - mStartTime;
            if(mStartTime > 0 && timeCount > 10000L) {
                temp = "读指纹等待超时";
                Log.d(TAG, temp);
                mIdentifyCallBack.onAuthenticationHelp(GETIMAGE_TIMEOUT1, temp);
                reSetTime();
                objHandler_fp.postDelayed(workRunnable, 100L);
            } else {
//                mStartTime = System.currentTimeMillis();
                if (mStatus == STATUS_ENROLL_START || mStatus == STATUS_ENROLL_AGAIN ) {
                    getFingerprint();
                } else {
                    Log.e(TAG, "status is error ! " + mStatus);
                    objHandler_fp.postDelayed(workRunnable, 100L);
                }
            }
        }
    };

    private Runnable fpmatchTasks_fromsystem = new Runnable() {
        public void run() {
            if (stop ) {
                return;
            }
            if (null == mIdentifyCallBack) {
                throw new NullPointerException("mIdentifyCallBack is null, can't run this task!");
            }
            String temp = "";
            mEndTime = System.currentTimeMillis();
            long timeCount = mEndTime - mStartTime;

            if(timeCount > 10000L) {
                temp = "读指纹等待超时";
                LogUtil.v(temp);
                objHandler_fp.postDelayed(fpmatchTasks_fromsystem, 100L);
                mIdentifyCallBack.onAuthenticationHelp(GETIMAGE_TIMEOUT1, temp);
            } else {
                if (mStatus == STATUS_MATCH_START) {
                    getFingerprint();
                } else {
                    Log.e(TAG, "status is error !  STATUS_MATCH_START " + mStatus);
                    objHandler_fp.postDelayed(fpmatchTasks_fromsystem, 100L);
                }
            }
        }
    };

    /**
     * 重置指纹识别的开始时间
     */
    private void reSetTime() {
        mStartTime = System.currentTimeMillis();
        mEndTime = System.currentTimeMillis();
    }

    /**
     *  获取指纹图形，并生成特征值
     */
    private void getFingerprint(){
        String temp = "";
        int nRet1 = zAandroid.ZAZGetImage(DEV_ADDR);
        if(0 == zAandroid.ZAZGetImage(DEV_ADDR)) {// 获取指纹成功
            temp = "请拿起手指";
            mIdentifyCallBack.onAuthenticationHelp(GETIMAGE_OK1, temp);
            // 生成特征值
            nRet1 = zAandroid.ZAZGenChar(DEV_ADDR, fpcharbuf);
            if (nRet1 == 0) {
                temp = "获取指纹成功";
                Log.d(TAG, temp);
//                objHandler_fp.postDelayed(fperollTasks_tosystem, 500L);
                if (mStatus == STATUS_ENROLL_START) {    //如果是采集的第一枚指纹，提示用户继续采集
                    mStatus = STATUS_ENROLL_AGAIN;
                    temp = "请再次放置手指";
                    fpcharbuf++;
                    mIdentifyCallBack.onAuthenticationHelp(GETONECHAR_OK1, temp);
                    objHandler_fp.postDelayed(workRunnable, 2000L);
                    Log.d(TAG, temp);
                } else if (mStatus == STATUS_ENROLL_AGAIN){
                    //合成指纹
                    regModule();
                } else if (mStatus == STATUS_MATCH_START) {
                    //进行指纹匹配
                    match();
                }
            } else {
                temp = "获取指纹失败，请重新放置手指";
                Log.d(TAG, temp);
                reSetTime();
                mIdentifyCallBack.onAuthenticationHelp(GETONECHAR_FAIL1, temp);
                objHandler_fp.postDelayed(workRunnable, 500L);
            }
        } else {
            if (nRet1 == 2) {
                temp = "正在读取指纹中   剩余时间:" + ((long) EROLL_TIMEOUT - (mEndTime - mStartTime)) / 1000L + "s";
                Log.d(TAG, temp);
                mIdentifyCallBack.onAuthenticationHelp(GETIMAGE_FAIL1, temp);
                objHandler_fp.postDelayed(workRunnable, 10L);
            } else if (nRet1 == 3){
                temp = "图像获取中";
                Log.d(TAG, temp + ": " + nRet1);
                objHandler_fp.postDelayed(workRunnable, 100L);
                mIdentifyCallBack.onAuthenticationHelp(GETIMAGEING1, temp);
                return;
            } else if (nRet1 != -2) {
                    temp = "通讯异常";
                    Log.d(TAG, temp + ": " + nRet1);
                    objHandler_fp.postDelayed(workRunnable, 100L);
                    mIdentifyCallBack.onAuthenticationHelp(COM_ERROR1, temp);
                    return;
            } else {
                testcount++;
                if (testcount >= 3) {
                    temp = "通讯异常1";
                    Log.d(TAG, temp + ": " + nRet1);
                    objHandler_fp.postDelayed(workRunnable, 100L);
                    mIdentifyCallBack.onAuthenticationHelp(COM_ERROR1, temp);
                    return;
                }
                temp = "正在读取指纹中   剩余时间:" + ((long) EROLL_TIMEOUT - (mEndTime - mStartTime)) / 1000L + "s";
                Log.d(TAG, temp);
                objHandler_fp.postDelayed(workRunnable, 100L);
                mIdentifyCallBack.onAuthenticationHelp(GETIMAGE_FAIL1, temp);
            }
        }
    }

    /**
     *  合并指纹
     */
    private void regModule() {
        String temp = "";
        int nRet1 = zAandroid.ZAZRegModule(DEV_ADDR);
        if (nRet1 != 0) {
            temp = "合成模板失败";
            Log.d(TAG, temp + ": " + nRet1);
            objHandler_fp.postDelayed(workRunnable, 100L);
//            mIdentifyCallBack.onAuthenticationHelp(REGMODULE_FAIL1, temp);
        } else {
            //合成模板成功，存储特征值
            nRet1 = zAandroid.ZAZStoreChar(DEV_ADDR, 1, iPageID);
            if (nRet1 == 0) {
                //存储特征值成功
                zAandroid.ZAZUpChar(DEV_ADDR, 1, pTempletbase, new int[1]);
                iPageID++;
                fpcharbuf = 1;
                mStatus = STATUS_SUCCESS;
                mIdentifyCallBack.onAuthenticationSucceeded(null);
                objHandler_fp.postDelayed(workRunnable, 3000L);
            } else {
                //存储特征值失败
                temp = "指纹合成失败，请重新放置指纹";
                mStatus = STATUS_ENROLL_START;
                mIdentifyCallBack.onAuthenticationHelp(REGMODULE_FAIL1, temp);
                objHandler_fp.postDelayed(workRunnable, 100L);
            }
        }
    }

    private void match(){
        int[] lefttime = new int[]{0, 0};
        int nRet1 = zAandroid.ZAZMatch(DEV_ADDR, lefttime);
        if(nRet1 == 0) {
            objHandler_fp.postDelayed(workRunnable, 10L);
            mIdentifyCallBack.onAuthenticationSucceeded(null);
            mStatus = STATUS_SUCCESS;
        } else {
            objHandler_fp.postDelayed(workRunnable, 10L);
            mIdentifyCallBack.onAuthenticationFailed();
        }
    }
}
