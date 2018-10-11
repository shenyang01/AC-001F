package com.zxcn.imai.smart.activity.finger;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.za.finger.ZA_finger;
import com.zxcn.finger.FingerOpenListener;
import com.zxcn.finger.IFpIdentifyCallBack;
import com.zxcn.finger.ZXFingerprintHelper;
import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.activity.common.FunctionChoseActivity;
import com.zxcn.imai.smart.activity.common.MainActivity;
import com.zxcn.imai.smart.activity.common.RegisterActivity1;
import com.zxcn.imai.smart.activity.intr.present.DownLoadFingerDataPresenter;
import com.zxcn.imai.smart.activity.intr.present.DownLoadFingerView;
import com.zxcn.imai.smart.activity.pressure.MeasureTipActivity;
import com.zxcn.imai.smart.base.AppConstant;
import com.zxcn.imai.smart.base.BaseActivity;
import com.zxcn.imai.smart.base.SmartApplication;
import com.zxcn.imai.smart.base.SpConstant;
import com.zxcn.imai.smart.core.finger.FingerHelper;
import com.zxcn.imai.smart.db.DbUtils;
import com.zxcn.imai.smart.db.module.Cache_UserInfo;
import com.zxcn.imai.smart.db.module.SmartBean;
import com.zxcn.imai.smart.db.module.UserInfo;
import com.zxcn.imai.smart.http.result.ResUploadInfoBean;
import com.zxcn.imai.smart.ui.HeaderView;
import com.zxcn.imai.smart.ui.TipDialog;
import com.zxcn.imai.smart.util.AdbShellUtils;
import com.zxcn.imai.smart.util.SpUtils;
import com.zxcn.imai.smart.util.StringResUtils;
import com.zxcn.imai.smart.util.ToastUtils;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ZXCN1 on 2017/9/4.
 */

public class FingerIdentifyActivity1 extends BaseActivity implements IFpIdentifyCallBack, FingerOpenListener, DownLoadFingerView {

    private String TAG = "060_FingerIdentifyActivity";

    private HeaderView headerView;
    private ImageView handIV;
    private TextView tipTV;

    private FingerHelper fingerHelper;

    private boolean isFirst = true;
    private int count = 0;
    private TipDialog tipDialog;
    private Timer mTimer;
    private boolean tag = false;

    @Override
    protected View setView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_finger_identify, null);
    }

    @Override
    protected void setEvent() {
        SmartApplication.getInstance().addAcitivity(this);
        AdbShellUtils.runCMD("echo  >" + AppConstant.FINGER_POWER_PATCH);
        mTimer = new Timer();
        headerView = findViewById(R.id.headerView);
        handIV = findViewById(R.id.handIV);
        tipTV = findViewById(R.id.tipTV);
        headerView.setLeftClickListener(view -> finish());
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.finger_track);
        handIV.startAnimation(animation);
        Log.e("tag", "userInfo " + DbUtils.QueryAll(UserInfo.class).size());
    }

    @Override
    protected void getData() {
        DownLoadFingerDataPresenter loadFingerDataPresenter = new DownLoadFingerDataPresenter(this);
        loadFingerDataPresenter.uploadData();
    }


    @Override
    protected void onResume() {
        super.onResume();
        fingerHelper = FingerHelper.getInstance(this);
        fingerHelper.initZXFingerprintWithoutCallBack();
        fingerHelper.setFingerOpernListener(this);
        fingerHelper.setCallBack(this);
        openFinger();
    }

    private void openFinger() {
        if (!tag) {
            setTimerTask();
        }
    }

    public int type = 1;

    private void setTimerTask() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (1 == type) {
                    handler.sendEmptyMessageDelayed(0, 100);
                }
            }
        }, 200, 2000/* 表示1000毫秒之後，每隔1000毫秒執行一次 */);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    try {
                        fingerHelper.open();
                    } catch (Exception e) {
                        Log.e(TAG, "open finger module error");
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    if (!tag) {
                        ToastUtils.toastShort(getBaseContext(), "指纹开启成功");
                        fingerHelper.setStatus(3);
                        startFingerMatch();
                        tag = true;
                    }
                    break;
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        fingerHelper.stopMatch();
        Log.e("activity_status", "onpause");
    }


    public static void newActivity(Context context) {
        context.startActivity(new Intent(context, FingerIdentifyActivity1.class));
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        Log.d(TAG, "onAuthenticationFailed : " + errMsgId);
        if (showTipDialog(errMsgId)) {
            return;
        }
        if (TextUtils.isEmpty(errString)) {
            StringResUtils.showFpInfo(errMsgId);
        } else {
            tipTV.setText(errString);
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        Log.e("tag", "onAuthenticationFailed : " + helpMsgId);
        if (showTipDialog(helpMsgId)) {
            return;
        }
        if (TextUtils.isEmpty(helpString)) {
            StringResUtils.showFpInfo(helpMsgId);
        } else {
            tipTV.setText(helpString);
        }
    }

    @Override
    public void onAuthenticationSucceeded(int helpMsgId, Map<String, String> result) {

        fingerHelper.stopMatch();
//        MeasureTipActivity.newActivity(this);
        try {
            Log.e("tag", "onAuthenticationSucceeded   " + result.size());
            String string = result.get("user_type");
            String phone1 = result.get("phone");
            Log.e("060", "phone1 : " + phone1);
            SpUtils.saveString(SpConstant.MOBILE_PHONE, phone1);
            if (!TextUtils.isEmpty(string)) {
                SpUtils.saveBoolean("card_type", true);
                SpUtils.saveString("card_name", string);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent();
        SpUtils.saveBoolean("card_type", true);
        intent.setClass(FingerIdentifyActivity1.this, FunctionChoseActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAuthenticationFailed(int helpMsgId) {
        Log.e("tag", "onAuthenticationFailed" + helpMsgId);
        showTipDialog(helpMsgId);
    }

    private boolean showTipDialog(int helpMsgId) {
        if (helpMsgId == ZXFingerprintHelper.MATCH_FAIL1) {
            if (fingerHelper.addAndGet() < fingerHelper.limit) {
                tipTV.setText(StringResUtils.getFpErrorTimesStr(fingerHelper.getCount()));
                return true;
            }
            if (null == tipDialog) {
                tipDialog = new TipDialog(this, R.string.dialog_content_tip);
            } else {
                tipDialog.setContent(R.string.dialog_content_tip);
            }
            tipDialog.setOnClickListener(view -> {
//                FingerBindActivity.newActivity(this);
                startActivity(new Intent(FingerIdentifyActivity1.this, RegisterActivity1.class));

                finish();
            });
            tipDialog.show();
            fingerHelper.stopMatch();
            return true;
        }
        return false;
    }

    private void startFingerMatch() {
        if (SpUtils.getValue(SpConstant.FLAG_FINGER_OPEN, false)) {
//            fingerHelper.startMatch(SpUtils.getValue(SpConstant.USER_TYPE, AppConstant.USER_GRANDPA));
            fingerHelper.startMatch("");
        } else {
            if (null == tipDialog) {
                tipDialog = new TipDialog(this, R.string.dialog_content_fp_error);
            } else {
                tipDialog.setContent(R.string.dialog_content_fp_error);
            }
            tipDialog.show();
        }
    }

    @Override
    public void openDeviceSuccess(int i) {
        Log.v(TAG, "开启成功" + count);
        runOnUiThread(() -> ToastUtils.toastShort(getBaseContext(), "开启成功,请按手指"));
        SpUtils.saveBoolean(SpConstant.FLAG_FINGER_OPEN, true);
        mTimer.cancel();
        handler.sendEmptyMessage(2);
        type = 2;
    }

    @Override
    public void openDeviceFail(int i) {

        SpUtils.saveBoolean(SpConstant.FLAG_FINGER_OPEN, false);
    }

    @Override
    public void usbFail(int i) {
        SpUtils.saveBoolean(SpConstant.FLAG_FINGER_OPEN, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fingerHelper.stopEncroll();
        handler.removeCallbacksAndMessages(null);
        //取消定时任务
        if (mTimer != null) {
            mTimer.cancel();
            //关闭指纹设备
        }
        fingerHelper.close();
        ZA_finger fppower = new ZA_finger();
        fppower.finger_power_off();
        AdbShellUtils.runCMD("echo  >" + AppConstant.FINGER_POWER_PATCH);
        tag = false;
    }


    /**
     * DownLoadFingerView回调接口重写
     */
    @Override
    public void getSucess(List<ResUploadInfoBean> list) {
        try {
            if (DbUtils.hasUser())
                DbUtils.clearAll();
        }catch (Exception e){
            e.printStackTrace();
        }

        for (ResUploadInfoBean ls : list) {
            UserInfo info = new UserInfo();
            info.cardNum = ls.cardNum;
            info.fingerOne = ls.finger1;
            info.fingerTwo = ls.finger2;
            info.phone = ls.mobile;
            info.userType = ls.mobile;
            DbUtils.insert(info);
            Log.e("tag", "  info " + info.phone);
            //List<UserInfo> m = DbUtils.QueryAll(UserInfo.class);
        }
        list.clear();
    }

    @Override
    public void error(String str) {

    }
}
