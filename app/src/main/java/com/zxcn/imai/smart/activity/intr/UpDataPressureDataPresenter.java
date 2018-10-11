package com.zxcn.imai.smart.activity.intr;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.zxcn.imai.smart.base.SpConstant;
import com.zxcn.imai.smart.bean.MesureReultBean;
import com.zxcn.imai.smart.http.BaseData;
import com.zxcn.imai.smart.http.HttpOnNextListener;
import com.zxcn.imai.smart.http.ProgressSubscriber;
import com.zxcn.imai.smart.http.RxHttpUtils;
import com.zxcn.imai.smart.http.request.PostResult;
import com.zxcn.imai.smart.http.result.ResTokenBean;
import com.zxcn.imai.smart.util.SharedPreferencesUtils;

import java.util.HashMap;


/**
 * Created by xtich on 2017/10/31.
 */

public class UpDataPressureDataPresenter {
    private String tag = "060_UpDataPressureDataPresenter";
    private final IUpDataMesureDataView listener;

    public UpDataPressureDataPresenter(IUpDataMesureDataView bindFingerView) {
        this.listener = bindFingerView;
    }

    /**
     * 发送数据到后台
     *
     * @param
     * @param data1
     * @param data2
     * @param data3
     * @param dataTime
     * @param type
     * @param phone
     * @param
     */
    public void postPressureData(Context context, String data1, String data2, String data3,
                                 String data15, String data10, String dataTime, String type, String phone) {
        MesureReultBean requestBean = new MesureReultBean();
        requestBean.setDeviceId(SharedPreferencesUtils.getInstance().getString(SpConstant.DEVICE_ID));
        requestBean.setMobile(phone);
        requestBean.setImaiToken(SharedPreferencesUtils.getInstance().getString(SpConstant.TOKEN));
        requestBean.setOrgCode(SharedPreferencesUtils.getInstance().getString(SpConstant.ORG_CODE));
        requestBean.setCheckTime(dataTime);
        requestBean.setCardNum("");
        requestBean.setDataType(type);
        requestBean.setData1(data1);
        requestBean.setData2(data2);
        requestBean.setData3(data3);
        requestBean.setData15(data15);
        requestBean.setData10(data10);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("json", new Gson().toJson(requestBean));
//        LogUtils.LogNet( new Gson().toJson(requestBean));
        Log.e(tag, "postPressureData :" + new Gson().toJson(requestBean));
        RxHttpUtils.requestPost(new PostResult(new ProgressSubscriber((Activity) context, getFindPassListener(), false), hashMap));
    }


    public void postSugerData(Context context, String data1, String data15, String dataTime, String type, String phone) {
        MesureReultBean requestBean = new MesureReultBean();
        requestBean.setDeviceId(SharedPreferencesUtils.getInstance().getString(SpConstant.DEVICE_ID));
        requestBean.setMobile(phone);
        requestBean.setImaiToken(SharedPreferencesUtils.getInstance().getString(SpConstant.TOKEN));
        requestBean.setOrgCode(SharedPreferencesUtils.getInstance().getString(SpConstant.ORG_CODE));
        requestBean.setCheckTime(dataTime);
        requestBean.setCardNum("");
        requestBean.setDataType(type);
        requestBean.setData1(data1);
        requestBean.setData15(data15);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("json", new Gson().toJson(requestBean));
//        LogUtils.LogNet( new Gson().toJson(requestBean));
        Log.e(tag, "postSugerData :" + new Gson().toJson(requestBean));
        RxHttpUtils.requestPost(new PostResult(new ProgressSubscriber((Activity) context, getFindPassListener(), false), hashMap));

    }

    private HttpOnNextListener getFindPassListener() {
        return new HttpOnNextListener<BaseData<ResTokenBean>>() {
            @Override
            public void onNext(BaseData<ResTokenBean> data) {
                Log.e(tag, "onNext");

                if (data.sysdata.pageInfo.list.size() > 0) {
                    ResTokenBean bean = data.sysdata.pageInfo.list.get(0);
                    Log.e(tag, "getFindPassListener back:  " + bean.toString());
                }

                if (data.status.equals("1")) {
                    Log.e(tag, "getSucess");
                    listener.getSucess();
                } else {
                    Log.e(tag, "上传失败");
                    listener.error("上传失败");
                }
            }
        };
    }


}