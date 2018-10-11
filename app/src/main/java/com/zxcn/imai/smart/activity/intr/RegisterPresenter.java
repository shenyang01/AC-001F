package com.zxcn.imai.smart.activity.intr;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.zxcn.imai.smart.activity.intr.present.Presenter;
import com.zxcn.imai.smart.base.SpConstant;
import com.zxcn.imai.smart.bean.CodeBean;
import com.zxcn.imai.smart.http.BaseData;
import com.zxcn.imai.smart.http.HttpOnNextListener;
import com.zxcn.imai.smart.http.ProgressSubscriber;
import com.zxcn.imai.smart.http.RxHttpUtils;
import com.zxcn.imai.smart.http.request.CheckCode;
import com.zxcn.imai.smart.http.request.GetCode;
import com.zxcn.imai.smart.http.result.ResTokenBean;
import com.zxcn.imai.smart.util.RegardsUtil;
import com.zxcn.imai.smart.util.SharedPreferencesUtils;

import java.util.HashMap;

/**
 * @version V1.0
 * @Package com.zxcn.imai.smart.activity.intr
 * @Description: ${todo}
 * @author: huangyuan
 * @date: 2018/7/23 10:51
 * @Copyright: www.***.com Inc. All rights reserved.
 */
public class RegisterPresenter implements Presenter {
    private String tag = "060_RegisterPresenter";

    private IRegisterView registerView;
    public RegisterPresenter() {
    }

    public RegisterPresenter(IRegisterView registerView) {
        this.registerView = registerView;
    }

    @Override
    public void onViewAttached(View view) {

    }

    @Override
    public void onViewDetached() {

    }

    @Override
    public void onDestroyed() {
        //        registerView = null;
    }


    /**
     * 获取验证码
     *
     * @param phone 手机号
     */
    public void getCode(Context context, String phone) {
        Log.e(tag, " getCode ");

        if (RegardsUtil.phoneValid(phone)) {
            CodeBean requestBean = new CodeBean();
            requestBean.setDeviceId(SharedPreferencesUtils.getInstance().getString(SpConstant.DEVICE_ID));
            requestBean.setMobile(phone);
            requestBean.setImaiToken(SharedPreferencesUtils.getInstance().getString(SpConstant.TOKEN));
            requestBean.setOrgCode(SharedPreferencesUtils.getInstance().getString(SpConstant.ORG_CODE));
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("json", new Gson().toJson(requestBean));
//            Log.e(tag, new Gson().toJson(requestBean));
            Log.e(tag,"getCode ：" + new Gson().toJson(requestBean));
            RxHttpUtils.requestPost(new GetCode(new ProgressSubscriber((Activity) context, getFindPassListener(), false), hashMap));

        }
    }

    private HttpOnNextListener getFindPassListener() {
        return new HttpOnNextListener<BaseData<ResTokenBean>>() {
            @Override
            public void onNext(BaseData<ResTokenBean> data) {
                if (data.status.equals("1")) {
                    registerView.getCodeSuccess();
                } else {
                    registerView.error("发送失败");
                }
            }
        };
    }

    /**
     * 新用户注册
     *
     * @param phone 手机号
     * @param code  验证码
     */
    public void checkCode(Context context, String phone, String code) {
        if (RegardsUtil.phoneValid(phone) && !TextUtils.isEmpty(code)) {
            CodeBean requestBean = new CodeBean();
            requestBean.setDeviceId(SharedPreferencesUtils.getInstance().getString(SpConstant.DEVICE_ID));
            requestBean.setMobile(phone);
            requestBean.setImaiToken(SharedPreferencesUtils.getInstance().getString(SpConstant.TOKEN));
            requestBean.setOrgCode(SharedPreferencesUtils.getInstance().getString(SpConstant.ORG_CODE));
            requestBean.setCode(code);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("json", new Gson().toJson(requestBean));
            Log.e(tag, "checkCode >>>"+ new Gson().toJson(requestBean));
            RxHttpUtils.requestPost(new CheckCode(new ProgressSubscriber((Activity) context, getcheckCodeListener(phone), false), hashMap));
        }
    }

    private HttpOnNextListener getcheckCodeListener(String phone) {
        return new HttpOnNextListener<BaseData<ResTokenBean>>() {
            @Override
            public void onNext(BaseData<ResTokenBean> data) {
                if (data.status.equals("1")) {
                    Log.e(tag, "data.status.equals 1");
                    registerView.registerSuccess();
                    SharedPreferencesUtils.getInstance().putString(SpConstant.MOBILE_PHONE, phone);
                } else {
                    registerView.error("验证失败");
                }
            }
        };
    }
}
