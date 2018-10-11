package com.zxcn.imai.smart.activity.intr;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.zxcn.imai.smart.base.SpConstant;
import com.zxcn.imai.smart.bean.FingerBindBean;
import com.zxcn.imai.smart.db.module.Cache_UserInfo;
import com.zxcn.imai.smart.http.BaseData;
import com.zxcn.imai.smart.http.HttpOnNextListener;
import com.zxcn.imai.smart.http.ProgressSubscriber;
import com.zxcn.imai.smart.http.RxHttpUtils;
import com.zxcn.imai.smart.http.request.FingerBind;
import com.zxcn.imai.smart.http.result.ResTokenBean;
import com.zxcn.imai.smart.util.LogUtils;
import com.zxcn.imai.smart.util.SharedPreferencesUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by xtich on 2017/10/31.
 */

public class BindFingerPresenter {
    private String tag = "060_BindFingerPresenter";

    private IBindFingerView listener;

    public BindFingerPresenter( ) {
    }
    public BindFingerPresenter(IBindFingerView bindFingerView) {
        this.listener=bindFingerView;
    }

    public void bindFingerToServer(Context context, String finger1, String finger2, String type, String phone, String card){
        FingerBindBean requestBean = new FingerBindBean();
        requestBean.setDeviceId(SharedPreferencesUtils.getInstance().getString(SpConstant.DEVICE_ID));
        requestBean.setMobile(phone);
        requestBean.setImaiToken(SharedPreferencesUtils.getInstance().getString(SpConstant.TOKEN));
        requestBean.setOrgCode(SharedPreferencesUtils.getInstance().getString(SpConstant.ORG_CODE));
        requestBean.setFinger1(finger1);
        requestBean.setFinger2(finger2);
        requestBean.setType(type);
        requestBean.setCardNum(card);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("json",new Gson().toJson(requestBean));
//        LogUtils.up(new Gson().toJson(requestBean));
        Log.e(tag, "bindFingerToServer ："+ new Gson().toJson(requestBean));
        RxHttpUtils.requestPost(new FingerBind(new ProgressSubscriber((Activity) context, getFindPassListener(), false), hashMap));

    }

    private HttpOnNextListener getFindPassListener() {
        return new HttpOnNextListener<BaseData<ResTokenBean>>() {
            @Override
            public void onNext(BaseData<ResTokenBean> data) {
                if (data.status.equals("1")) {
                    Log.e(tag, "getbindSucess ");
                    listener.getbindSucess();
                } else {
                    Log.e(tag, "binderror ");
                    listener.binderror("绑定失败");
                }
            }
        };
    }


    public void uploadFingerToServer(List<Cache_UserInfo> ls){
        int index=0;
        for (Cache_UserInfo info:ls  ) {
            FingerBindBean requestBean = new FingerBindBean();
            requestBean.setDeviceId(SharedPreferencesUtils.getInstance().getString(SpConstant.DEVICE_ID));
            requestBean.setImaiToken(SharedPreferencesUtils.getInstance().getString(SpConstant.TOKEN));
            requestBean.setOrgCode(SharedPreferencesUtils.getInstance().getString(SpConstant.ORG_CODE));
            requestBean.setFinger1(info.getFingerOne());
            requestBean.setFinger2(info.getFingerTwo());
            requestBean.setMobile(info.phone);
            requestBean.setType(info.userType);
            requestBean.setCardNum(info.cardNum);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("json",new Gson().toJson(requestBean));
            index++;
            LogUtils.up("size"+ls.size()+"index:"+index+new Gson().toJson(requestBean));
            RxHttpUtils.requestPost(new FingerBind(new ProgressSubscriber(uploadListener(ls.size(),index)), hashMap));
        }

    }

    private HttpOnNextListener uploadListener(int size, int index) {
        return new HttpOnNextListener<BaseData<ResTokenBean>>() {
            @Override
            public void onNext(BaseData<ResTokenBean> data) {
                if (data.status.equals("1")) {
                   if (size==index){
                       listener.getbindSucess();
                   }
                } else {
                    listener.binderror("error");
                }
            }
        };
    }



}
