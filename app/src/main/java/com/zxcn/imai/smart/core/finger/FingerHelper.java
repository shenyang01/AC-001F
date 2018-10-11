package com.zxcn.imai.smart.core.finger;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.zxcn.finger.FingerOpenListener;
import com.zxcn.finger.IFpIdentifyCallBack;
import com.zxcn.finger.ZXFingerprintHelper;
import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.base.AppConstant;
import com.zxcn.imai.smart.base.SpConstant;
import com.zxcn.imai.smart.util.AdbShellUtils;
import com.zxcn.imai.smart.util.SpUtils;

/**
 * Created by ZXCN1 on 2017/8/17.
 */

public class FingerHelper {

    private Context mContext;

    private FingerprintManagerCompat manager;
    private FingerprintManagerCompat.AuthenticationCallback fingerListener;
    private CancellationSignal signal;

    private IFpIdentifyCallBack iFpIdentifyCallBack;

    public boolean hasFingerApi = false;

    private ZXFingerprintHelper zxFingerprintHelper;

    public final int limit = 5; //指纹识别有5次机会。

    private int count = 0;      //当前第几次识别指纹

    public static FingerHelper getInstance(Context context){
        return new FingerHelper(context);
    }

    private FingerHelper(Context context) {
        mContext = context;
        if(AppConstant.DEBUG) {
            //如果存在指纹api则进行指纹模块判断  android M 以下系统不带指纹API，但是各厂商可能支持
            if (SpUtils.getValue(SpConstant.HAS_API_FINGER, false)) {
                manager = FingerprintManagerCompat.from(context);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) ==
                        PackageManager.PERMISSION_GRANTED) {
                    //如果手机带有指纹模块，则初始化
                    if (manager.isHardwareDetected() && manager.hasEnrolledFingerprints()) {
                        hasFingerApi = true;
                        initFingerApi();
                        return;
                    } else {
                        Toast.makeText(context, R.string.toast_fp_switch_error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showTipsDialog();
                }
            } else {
                Toast.makeText(context, R.string.toast_fp_api_error, Toast.LENGTH_SHORT).show();
            }
            hasFingerApi = false;
        } else {

        }
    }

    public void open() {
        AdbShellUtils.runCMD("echo 1 >"+AppConstant.FINGER_POWER_PATCH);
        if (null != zxFingerprintHelper && !AppConstant.DEBUG) {
            zxFingerprintHelper.open();
        }
    }

    public void setCallBack(IFpIdentifyCallBack iFpIdentifyCallBack) {
        if (AppConstant.DEBUG) {
            this.iFpIdentifyCallBack = iFpIdentifyCallBack;
        } else {
            initZXFingerprint(iFpIdentifyCallBack);
        }
    }

    public void setStatus(int status){
        zxFingerprintHelper.setStatus(status);
    }

    public void initZXFingerprint(IFpIdentifyCallBack iFpIdentifyCallBack) {
        if(null == zxFingerprintHelper) {
            zxFingerprintHelper = new ZXFingerprintHelper(mContext, (Activity) mContext);
            zxFingerprintHelper.setFingerListener(iFpIdentifyCallBack);
        } else {
            zxFingerprintHelper .setFingerListener(iFpIdentifyCallBack);
        }
    }

    public void initZXFingerprintWithoutCallBack() {
//        zxFingerprint = new ZXFingerprint((Activity) mContext, (Activity) mContext, null);
        zxFingerprintHelper = new ZXFingerprintHelper(mContext, (Activity) mContext);
    }

    public void setFingerOpernListener(FingerOpenListener fingerOpernListener) {
        zxFingerprintHelper.setOpenListener(fingerOpernListener);
    }

    public void setIFpIdentifyCallBack(IFpIdentifyCallBack iFpIdentifyCallBack) {
        zxFingerprintHelper.setFingerListener(iFpIdentifyCallBack);
    }

    private void initFingerApi() {
        fingerListener = new FingerprintManagerCompat.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
                if (null != iFpIdentifyCallBack) {
                    iFpIdentifyCallBack.onAuthenticationError(errMsgId, errString);
                }
            }

            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                if (null != iFpIdentifyCallBack) {
                    iFpIdentifyCallBack.onAuthenticationHelp(helpMsgId, helpString);
                }
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                Log.e("tag"," --------------------------");
                if (null != iFpIdentifyCallBack) {
                    Log.e("tag",result.getCryptoObject().toString()+"  result.getCryptoObject()" +
                            " "+result.getCryptoObject());
                    iFpIdentifyCallBack.onAuthenticationSucceeded(0, null);
                }
            }

            @Override
            public void onAuthenticationFailed() {
                if (null != iFpIdentifyCallBack) {
                    iFpIdentifyCallBack.onAuthenticationFailed(-1);
                }
            }
        };
        signal = new CancellationSignal();
        manager.authenticate(null, 0, signal, fingerListener, null);
    }

    public void startEncroll() {
        if (!AppConstant.DEBUG){
            zxFingerprintHelper.startEnroll();
        } else {
            reAuth();
        }
    }
    public void close(){
        zxFingerprintHelper.close();
        AdbShellUtils.runCMD("echo 0 >"+AppConstant.FINGER_POWER_PATCH);
    }
    public void startMatch(String userType) {
        if (!AppConstant.DEBUG) {
            zxFingerprintHelper.startMatch(userType);
        } else {
            reAuth();
        }
    }

    public void stopEncroll() {
        if (!AppConstant.DEBUG){
//            zxFingerprint.stopErollfp(signal);
            zxFingerprintHelper.stop();
        } else {
            onCancel();
        }
    }

    public void stopMatch() {
        if (!AppConstant.DEBUG){
//            zxFingerprint.stopMatchfp(signal);
            zxFingerprintHelper.stop();
        } else {
            onCancel();
        }
    }

    public int getCount(){
        return count;
    }

    public int addAndGet() {
        return ++count;
    }


    /**
     * 取消检测
     */
    public void onCancel() {
        if (AppConstant.DEBUG && signal != null) {
            signal.cancel();
            signal = null;
        }
    }

    /**
     * 重新注册
     */
    public void reAuth() {
        if (AppConstant.DEBUG && null != manager) {
            if (signal == null) {
                signal = new CancellationSignal();
                manager.authenticate(null, 0, signal, fingerListener, null);
            }
        }
    }

    /**
     * 显示提示对话框
     */
    private void showTipsDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle(R.string.dialog_title_permission)
                .setMessage(R.string.dialog_message_permission)
                .setNegativeButton(R.string.dialog_btn_cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.dialog_btn_sure, (dialog, which) -> startAppSettings()).show();
    }

    /**
     * 启动当前应用设置页面
     */
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + mContext.getPackageName()));
        mContext.startActivity(intent);
    }
}
