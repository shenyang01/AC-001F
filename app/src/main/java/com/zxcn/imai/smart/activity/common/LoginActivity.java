package com.zxcn.imai.smart.activity.common;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.activity.finger.FingerBindActivity;
import com.zxcn.imai.smart.base.BaseActivity;
import com.zxcn.imai.smart.base.SmartApplication;
import com.zxcn.imai.smart.ui.HeaderView;
import com.zxcn.imai.smart.util.RegardsUtil;
import com.zxcn.imai.smart.util.ToastUtils;

/**
 * Created by ZXCN1 on 2017/9/4.
 */

public class LoginActivity extends BaseActivity{

    private HeaderView headerView;
    private EditText phoneET;
    private EditText codeET;
    private TextView getCodeTV;
    private TextView nextTV;
    private String validCode;

    @Override
    protected View setView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_login,null);
    }

    @Override
    protected void setEvent() {
        SmartApplication.getInstance().addAcitivity(this);
        headerView = findViewById(R.id.headerView);
        phoneET = findViewById(R.id.phoneET);
        codeET = findViewById(R.id.codeET);
        getCodeTV = findViewById(R.id.getCodeTV);
        nextTV = findViewById(R.id.nextTV);
        headerView.setLeftClickListener(view -> finish());
        getCodeTV.setOnClickListener(view -> {
            String phone = phoneET.getText().toString();
            if(RegardsUtil.phoneValid(phone)) {
                getCodeTV.setEnabled(false);
                timer.start();
//                validCode = StringResUtils.getValidaCode();
//                RetrofitFactory.getInstance().getCode(phone, validCode, success -> {
//                    ToastUtils.toastShort(this, R.string.toast_send_code_success);
//                }, error -> {
//
//                });
            } else {
                ToastUtils.toastShort(this, R.string.toast_phone_error);
            }
        });
        nextTV.setOnClickListener(view -> {
            String phone = phoneET.getText().toString();
            String code = codeET.getText().toString();
            if(!RegardsUtil.phoneValid(phone)){
                ToastUtils.toastShort(this, R.string.toast_phone_error);
                return;
            }
            if(TextUtils.isEmpty(code)){
                ToastUtils.toastShort(this, R.string.toast_code_empty);
                return;
            }
//            if(!code.equals(validCode)) {
//                ToastUtils.toastShort(this, R.string.toast_code_error);
//                return;
//            }
//            RetrofitFactory.getInstance().login(phone, code, success -> {
//                //登录成功，保存当前角色的token
//                SpUtils.saveString(SpUtils.getValue(SpConstant.USER_TYPE, AppConstant.USER_GRANDPA), success.get("token"));
//                //登录成功，跳转到指纹绑定界面
//                FingerBindActivity.newActivity(this);
//            }, error -> {
//                //提示错误原因
//                if (error instanceof ResultException) {
//                    ToastUtils.toastShort(this, ((ResultException) error).getResultMessage());
//                }
//            });
            FingerBindActivity.newActivity(this);
        });
    }

    @Override
    protected void getData() {

    }

    public static void newActivity(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    private static final int TIME_ALL = 60000;      //一分钟后可以再次获取时间
    private static final int TIME_INTERVAL = 1000;   //绘制表盘间隔时间
    private CountDownTimer timer = new CountDownTimer(TIME_ALL, TIME_INTERVAL) {
        @Override
        public void onTick(long l) {
            getCodeTV.setText(l/1000 + "S");
        }

        @Override
        public void onFinish() {
            getCodeTV.setText(R.string.btn_get_code);
            getCodeTV.setEnabled(true);
        }
    };
}
