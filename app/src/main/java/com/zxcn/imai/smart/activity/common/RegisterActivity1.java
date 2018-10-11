package com.zxcn.imai.smart.activity.common;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.za.finger.ZA_finger;
import com.zxcn.finger.FingerOpenListener;
import com.zxcn.finger.IFpIdentifyCallBack;
import com.zxcn.finger.ZXFingerprintHelper;
import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.activity.intr.BindFingerPresenter;
import com.zxcn.imai.smart.activity.intr.IBindFingerView;
import com.zxcn.imai.smart.activity.intr.IRegisterView;
import com.zxcn.imai.smart.activity.intr.RegisterPresenter;
import com.zxcn.imai.smart.base.AppConstant;
import com.zxcn.imai.smart.base.BaseActivity;
import com.zxcn.imai.smart.base.SmartApplication;
import com.zxcn.imai.smart.base.SpConstant;
import com.zxcn.imai.smart.core.finger.FingerHelper;
import com.zxcn.imai.smart.db.DbUtils;
import com.zxcn.imai.smart.db.module.Cache_UserInfo;
import com.zxcn.imai.smart.db.module.UserInfo;
import com.zxcn.imai.smart.ui.HeaderView;
import com.zxcn.imai.smart.ui.TipDialog;
import com.zxcn.imai.smart.util.AdbShellUtils;
import com.zxcn.imai.smart.util.InputCheckUtil;
import com.zxcn.imai.smart.util.LogUtils;
import com.zxcn.imai.smart.util.SharedPreferencesUtils;
import com.zxcn.imai.smart.util.SpUtils;
import com.zxcn.imai.smart.util.StringResUtils;
import com.zxcn.imai.smart.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ZXCN1 on 2017/9/4.
 */

public class RegisterActivity1 extends BaseActivity implements IBindFingerView, IRegisterView, IFpIdentifyCallBack, FingerOpenListener {

    private String TAG = "060_FingerBindActivity";

    private ImageView firstFingerIV;
    private ImageView secondFingerIV;
    private TextView tipTV;
    private FingerHelper fingerHelper;
    private UserInfo userInfo = new UserInfo();
    private Timer mTimer;
    //    private EditText finger_bindName;
    private Button finger_bt;

    private Spinner spinner;
    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;
    private EditText mname, mid, mphone, myanzheng;
    private Button mmgetCode, mnextTV;

    private boolean isConnect = false;
    private boolean hasregister = false;
    private String fp1;
    private String fp2;
    private BindFingerPresenter bindFingerPresenter = new BindFingerPresenter(this);
    private RegisterPresenter registerPresenter = new RegisterPresenter(this);

    String name;
    String phone;

    @Override
    protected View setView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_finger_bind1, null);
    }

    @Override
    protected void setEvent() {
        SmartApplication.getInstance().addAcitivity(this);
        AdbShellUtils.runCMD("echo 1 >" + AppConstant.FINGER_POWER_PATCH);
        mTimer = new Timer();
        HeaderView headerView = findViewById(R.id.headerView);
        firstFingerIV = findViewById(R.id.firstFingerIV);
        secondFingerIV = findViewById(R.id.secondFingerIV);
        finger_bt = findViewById(R.id.finger_bt);
        tipTV = findViewById(R.id.tipTV);
        firstFingerIV.setEnabled(false);
        secondFingerIV.setEnabled(false);
        fingerHelper = FingerHelper.getInstance(this);
        fingerHelper.initZXFingerprintWithoutCallBack();
        fingerHelper.setFingerOpernListener(this);
        fingerHelper.setCallBack(this);
        headerView.setLeftClickListener(view -> finish());

        /**
         * 是否联网
         * */
        if (this.inspectNet()) {
            isConnect = true;
        }

        finger_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if (secondFingerIV.isEnabled()) {
                //   Log.e(TAG, "secondFingerIV isEnabled");
                try {
                    if (!InputCheckUtil.isMobileNO(mphone
                            .getText().toString().trim()) || !InputCheckUtil.IDCardValidate(mid
                            .getText()
                            .toString().trim())) {
                        ToastUtils.toastShort(getApplicationContext(), "请输入正确的身份证号和正确的手机号");
                    } else {
//                        saveFingerToDb();
//                        saveFingerPrinter();
                        if (isConnect) {
                            Log.e(TAG, "isConnect");
                            bindFingerToServer();
                        } else {
                            save_loation_cache();
                        }
                        if (!hasregister) {
                            Intent intent = new Intent();
                            SpUtils.saveBoolean("card_data", true);
                            SpUtils.saveString("card_name", mname.getText().toString().trim());
                            intent.putExtra("type", "1");
                            intent.putExtra("card_name", mname.getText().toString().trim());
                            intent.putExtra("phone", mphone.getText().toString());
                            intent.setClass(RegisterActivity1.this, ErweimaActivity.class);
                            startActivity(intent);
                            finish();
                            hasregister = false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // }
        });


        mname = findViewById(R.id.mname);
        mname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //手机验证码验证
                //                registerPresenter.checkCode(RegisterActivity1.this, mphone.getText().toString(), mmgetCode.getText().toString());
                name = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mid = findViewById(R.id.mid);
        mphone = findViewById(R.id.mphone);
        mphone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //手机验证码验证
                //                registerPresenter.checkCode(RegisterActivity1.this, mphone.getText().toString(), mmgetCode.getText().toString());
                phone = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        myanzheng = findViewById(R.id.myanzheng);
        myanzheng.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //手机验证码验证
                //                registerPresenter.checkCode(RegisterActivity1.this, mphone.getText().toString(), mmgetCode.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mmgetCode = findViewById(R.id.mgetCode);
        mmgetCode.setOnClickListener(view -> {
            registerPresenter.getCode(RegisterActivity1.this, mphone.getText().toString());
        });

        spinner = findViewById(R.id.spinner);
        //数据
        data_list = new ArrayList<String>();
        data_list.add("是");
        data_list.add("否");
        //适配器
        arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(arr_adapter);
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {//选择item的选择点击监听事件
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                //                if (TextUtils.isEmpty(mname.getText().toString()) || TextUtils.isEmpty(mphone.getText().toString())) {
                ////                    ToastUtils.toastShort(RegisterActivity2.this, getString(R.string.userName));
                //                }
                //                else if (TextUtils.isEmpty(mphone.getText().toString())) {
                //                    ToastUtils.toastShort(RegisterActivity.this, getString(R.string.userPhone));
                //                }
                //                else {
                switch (arg2) {
                    case 0:
                        Log.e(TAG, "onItemSelected");
                        openFinge();
                        spinner.setEnabled(false);
                        break;
                    case 1:
                        break;
                    default:

                }

                //                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        spinner.setSelection(1, true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        hideSoftInput(mname);
        return super.onTouchEvent(event);
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
                runOnUiThread(() -> {
                    //                    finger_bt.setEnabled(false);
                });
                handler.sendEmptyMessageDelayed(0, 200);
            }
        }, 200, 2000/* 表示2000毫秒之後，每隔2000毫秒執行一次 */);

    }

    private void setUserType(String userType, String roleName) {
        SpUtils.saveString(SpConstant.USER_TYPE, userType);
        userInfo.userType = userType;
        startEncroll();
    }

    private void saveFingerPrinter() {
        SpUtils.saveString(SpConstant.USERNAME, mname.getText().toString());
        SpUtils.saveString(SpConstant.MOBILE_PHONE, mphone.getText().toString());
        userInfo.userName = name;
        userInfo.phone = phone;
        Log.e(TAG, "name :" + name + ", phone :" + phone);
        DbUtils.save(userInfo);

        /**
         *
         * **********************2018.7.26 调试********************
         * */
//        ArrayList samephone = new ArrayList();
//        try {
//            samephone = DbUtils.queryUserInfo("phone", phone);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        if (samephone != null) {
//            if (samephone.size() > 0) {
//                Log.e(TAG, "samephone  size:" + samephone.size());
//                hasregister = true;
//                ToastUtils.toastShort("该手机号已经注册了，请换一个手机号");
//                finish();
//                startActivity(new Intent(RegisterActivity1.this,RegisterActivity1.class));
//            }
//        } else {
//            DbUtils.save(userInfo);
//        }

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
                case 20:
                    if (mTimer != null)
                        mTimer.cancel();
                    if (fingerHelper != null) {
                        fingerHelper.stopEncroll();
                        ZA_finger fppower = new ZA_finger();
                        fppower.finger_power_off();
                        AdbShellUtils.runCMD("echo 0 >" + AppConstant.FINGER_POWER_PATCH);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null)
            mTimer.cancel();
        if (fingerHelper != null) {
            fingerHelper.stopEncroll();
            ZA_finger fppower = new ZA_finger();
            fppower.finger_power_off();
            AdbShellUtils.runCMD("echo 0 >" + AppConstant.FINGER_POWER_PATCH);
        }
    }


    public static void newActivity(Context context) {
        context.startActivity(new Intent(context, RegisterActivity1.class));
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
            fp1 = result.get("fingerinfo");
            userInfo.fingerOne = result.get("fingerinfo");
            tipTV.setText(R.string.toast_fp_collect_success_one);
            fingerHelper.setStatus(ZXFingerprintHelper.STATUS_ENROLL_START);
        } else if (!secondFingerIV.isEnabled()) {
            secondFingerIV.setEnabled(true);
            fp2 = result.get("fingerinfo");
            userInfo.fingerTwo = result.get("fingerinfo");
            tipTV.setText(R.string.toast_fp_collect_success);
            fingerHelper.stopEncroll();
            saveFingerPrinter();
            //            Intent intent = new Intent(RegisterActivity1.this, FingerIdentifyActivity.class);
            //            Intent intent = new Intent(RegisterActivity1.this, ErweimaActivity.class);
            //            SpUtils.saveBoolean("card_data", true);
            ////            SpUtils.saveString("card_name", finger_bindName.getText().toString().trim());
            //            intent.putExtra("type", "1");
            //            intent.putExtra("phone",mphone.getText().toString());
            //            startActivity(intent);
            //            finish();

            handler.sendEmptyMessageDelayed(20, 500);
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

    /**
     * 数据存本地数据库
     */
    private void saveFingerToDb() {
        //        DbUtils.insert(userInfo);
        SpUtils.saveString(SpConstant.USERNAME, mname.getText().toString());
        SpUtils.saveString(SpConstant.MOBILE_PHONE, mphone.getText().toString());
    }

    private void bindFingerToServer() {
        String phone = mphone.getText().toString();
        Log.e(TAG, "BindFingerPresenter fp2 :" + fp2 + ", fp1 : " + fp1);

        if (fp1 != null && fp2 != null) {
            if (bindFingerPresenter == null) {
                Log.e(TAG, "BindFingerPresenter1...................");
                bindFingerPresenter = new BindFingerPresenter(this);
                bindFingerPresenter.bindFingerToServer(RegisterActivity1.this, fp1, fp2, "0", phone, "");
            } else {
                Log.e(TAG, "BindFingerPresenter2...................");
                bindFingerPresenter.bindFingerToServer(RegisterActivity1.this, fp1, fp2, "0", phone, "");
            }
        }
    }

    private void save_loation_cache() {
        Cache_UserInfo cache_userInfo = new Cache_UserInfo();
        cache_userInfo.data_type = "1";
        cache_userInfo.fingerOne = fp1;
        cache_userInfo.fingerTwo = fp2;
        cache_userInfo.phone = SharedPreferencesUtils.getInstance().getString(SpConstant.MOBILE_PHONE);
        LogUtils.save(cache_userInfo.toString());
        DbUtils.insert(cache_userInfo);
    }


    /**
     * IRegisterView重写（获取验证码、验证码验证、错误回调接口）
     */
    @Override
    public void getCodeSuccess() {
        mmgetCode.setEnabled(false);
        timer.start();
    }

    private static final int TIME_ALL = 120000;      //一分钟后可以再次获取时间
    private static final int TIME_INTERVAL = 1000;   //绘制表盘间隔时间
    private CountDownTimer timer = new CountDownTimer(TIME_ALL, TIME_INTERVAL) {
        @Override
        public void onTick(long l) {
            mmgetCode.setText(l / 1000 + "S");
        }

        @Override
        public void onFinish() {
            mmgetCode.setText(R.string.btn_get_code);
            mmgetCode.setEnabled(true);
        }
    };

    @Override
    public void registerSuccess() {
    }

    @Override
    public void error(String error) {
        ToastUtils.toastShort(RegisterActivity1.this, error);
    }


    /**
     * IBindFingerView重写（上传指纹到后台回调，上传成功、上传失败）
     */
    @Override
    public void getbindSucess() {
/*
        Log.e(TAG, " getbindSucess ");
        Intent intent = new Intent();
        SpUtils.saveBoolean("card_data", true);
        SpUtils.saveString("card_name", mname.getText().toString().trim());
        intent.putExtra("type", "1");
        intent.putExtra("phone", mphone.getText().toString());
        intent.setClass(RegisterActivity1.this, ErweimaActivity.class);

        startActivity(intent);
        finish();*/
    }

    @Override
    public void binderror(String str) {
    }
}
