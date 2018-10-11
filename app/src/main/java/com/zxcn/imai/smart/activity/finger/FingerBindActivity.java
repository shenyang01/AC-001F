package com.zxcn.imai.smart.activity.finger;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.za.finger.ZA_finger;
import com.zxcn.finger.FingerOpenListener;
import com.zxcn.finger.IFpIdentifyCallBack;
import com.zxcn.finger.ZXFingerprintHelper;
import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.activity.common.QRCodeActivity;
import com.zxcn.imai.smart.base.AppConstant;
import com.zxcn.imai.smart.base.BaseActivity;
import com.zxcn.imai.smart.base.SmartApplication;
import com.zxcn.imai.smart.base.SpConstant;
import com.zxcn.imai.smart.core.finger.FingerHelper;
import com.zxcn.imai.smart.db.DbUtils;
import com.zxcn.imai.smart.db.module.UserInfo;
import com.zxcn.imai.smart.ui.HeaderView;
import com.zxcn.imai.smart.ui.TipDialog;
import com.zxcn.imai.smart.util.AdbShellUtils;
import com.zxcn.imai.smart.util.SpUtils;
import com.zxcn.imai.smart.util.StringResUtils;
import com.zxcn.imai.smart.util.ToastUtils;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by ZXCN1 on 2017/9/4.
 */

public class FingerBindActivity extends BaseActivity implements IFpIdentifyCallBack, FingerOpenListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private String TAG = "FingerBindActivity";

    private TextView firstFingerIV, secondFingerIV;
    private TextView tipTV;
    private FingerHelper fingerHelper;
    private UserInfo userInfo;
    private Timer mTimer;
    private Button registrationComplete,mgetCode;
    private Spinner spinner;
    private EditText mid,mname,mphone,myanzheng;

    @Override
    protected View setView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_finger_bind2, null);
    }

    @Override
    protected void setEvent() {
        SmartApplication.getInstance().addAcitivity(this);
        AdbShellUtils.runCMD("echo 1 >" + AppConstant.FINGER_POWER_PATCH);
        mTimer = new Timer();
        HeaderView headerView = findViewById(R.id.headerView);
        firstFingerIV = findViewById(R.id.firstFingerIV);
        secondFingerIV = findViewById(R.id.secondFingerIV);
        spinner = findViewById(R.id.finger_spinner);
        spinner.setOnItemSelectedListener(this);
        registrationComplete = findViewById(R.id.registrationComplete);
        mgetCode = findViewById(R.id.mgetCode);
        mname = findViewById(R.id.mname);
        mid = findViewById(R.id.mid);
        mphone = findViewById(R.id.mphone);
        myanzheng = findViewById(R.id.myanzheng);
        tipTV = findViewById(R.id.tipTV);
        fingerHelper = FingerHelper.getInstance(this);
        fingerHelper.initZXFingerprintWithoutCallBack();
        fingerHelper.setFingerOpernListener(this);
        fingerHelper.setCallBack(this);
        mgetCode.setOnClickListener(this);
        registrationComplete.setOnClickListener(this);
        firstFingerIV.setEnabled(false);
        secondFingerIV.setEnabled(false);
        headerView.setLeftClickListener(view -> finish());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registrationComplete: //注册完成
                registrationComplete();
                break;
            case R.id.mgetCode: //指纹录入
                break;

        }
    }


    @Override
    public void openDeviceSuccess(int i) {
        Log.v("060", "openDeviceSuccess");
        SpUtils.saveBoolean(SpConstant.FLAG_FINGER_OPEN, true);
        handler.sendEmptyMessageDelayed(10, 100);

    }

    @Override
    public void openDeviceFail(int i) {
        Log.v("060", "openDeviceFail");
        SpUtils.saveBoolean(SpConstant.FLAG_FINGER_OPEN, false);
        runOnUiThread(() -> ToastUtils.toastShort(this, R.string.finger_starting));
    }

    @Override
    public void usbFail(int i) {
        Log.v("060", "usbFail");

        runOnUiThread(() -> {
            SpUtils.saveBoolean(SpConstant.FLAG_FINGER_OPEN, false);
            ToastUtils.toastShort(this, R.string.finger_starting);
        });
    }

    private void openFinge() {
        setTimerTask();
    }

    private void setTimerTask() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessageDelayed(0, 200);
            }
        }, 200, 2000/* 表示2000毫秒之後，每隔2000毫秒執行一次 */);

    }

    private void setUserType(String userType, String roleName) {
        SpUtils.saveString(SpConstant.USER_TYPE, userType);
        userInfo = new UserInfo();
        userInfo.userType = userType;
        startEncroll();
    }

    private void saveFingerPrinter() {
        DbUtils.save(userInfo);
    }

    @Override
    protected void getData() {
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
                case 10:
                    mTimer.cancel();
                    setUserType(mname.getText().toString(), "");
                    ToastUtils.toastShort(getBaseContext(), "指纹模块开启成功");
                    break;
            }
        }
    };

    /**
     * 关闭指纹
     */
    private void closeFingerprints() {
        if (fingerHelper != null) {
            fingerHelper.stopEncroll();
            ZA_finger fppower = new ZA_finger();
            fppower.finger_power_off();
            AdbShellUtils.runCMD("echo 0 >" + AppConstant.FINGER_POWER_PATCH);
        }
    }

    /***
     * 注册完成
     */
    private void registrationComplete() {
        String name = mname.getText().toString().trim();
        String cardName = mid.getText().toString().trim();
        String phoneNumber = mphone.getText().toString().trim();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(cardName) || TextUtils.isEmpty(phoneNumber)) {
            ToastUtils.toastShort(this, getString(R.string.isEmpty));
        } else {
            Intent intent = new Intent();
            intent.putExtra("QR_name", name);
            intent.putExtra("QR_phone", cardName);
            intent.putExtra("QR_card", phoneNumber);
            intent.setClass(this, QRCodeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null)
            mTimer.cancel();
        closeFingerprints();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        hideSoftInput(mid);
        return super.onTouchEvent(event);
    }

    public static void newActivity(Context context) {
        context.startActivity(new Intent(context, FingerBindActivity.class));
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        //        StringResUtils.showFpInfo(errMsgId);
        tipTV.setText(errString);
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        if (TextUtils.isEmpty(helpString)) {
            StringResUtils.showFpInfo(helpMsgId);
        } else {
            tipTV.setText(helpString);
        }
    }

    @Override
    public void onAuthenticationSucceeded(int helpMsgId, Map<String, String> result) {
        if (!firstFingerIV.isEnabled()) {
            firstFingerIV.setEnabled(true);
            userInfo.fingerOne = result.get("fingerinfo");
            tipTV.setText(R.string.toast_fp_collect_success_one);
            fingerHelper.setStatus(ZXFingerprintHelper.STATUS_ENROLL_START);
        } else if (!secondFingerIV.isEnabled()) {
            secondFingerIV.setEnabled(true);
            userInfo.fingerTwo = result.get("fingerinfo");
            tipTV.setText(R.string.toast_fp_collect_success);
            fingerHelper.stopEncroll();
            saveFingerPrinter();
            // Intent intent = new Intent(FingerBindActivity.this, FingerIdentifyActivity.class);
            //intent.putExtra("type", "1");
            //startActivity(intent);
            //finish();
        }
    }

    @Override
    public void onAuthenticationFailed(int helpMsgId) {
        ToastUtils.toastShort(this, R.string.toast_fp_collect_error);
    }

    private TipDialog tipDialog;

    private void startEncroll() {
        if (SpUtils.getValue(SpConstant.FLAG_FINGER_OPEN, false)) {
            fingerHelper.startEncroll();
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
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.e("tag", position + "   position");
        if (position == 0) {
            openFinge();
        } else {
            closeFingerprints();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

 /* private void clearUserInfo() {
        if (null != userInfo) {
            userInfo.userType = "0";
            userInfo.fingerOne = "";
            userInfo.fingerTwo = "";
        } else {
            userInfo = new UserInfo();
        }
        firstFingerIV.setEnabled(false);
        secondFingerIV.setEnabled(false);
    }*/


}
