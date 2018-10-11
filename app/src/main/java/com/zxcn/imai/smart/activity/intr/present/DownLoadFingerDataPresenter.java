package com.zxcn.imai.smart.activity.intr.present;

import android.util.Log;

import com.google.gson.Gson;
import com.zxcn.imai.smart.base.SpConstant;
import com.zxcn.imai.smart.bean.UploadDataBean;
import com.zxcn.imai.smart.http.BaseData;
import com.zxcn.imai.smart.http.HttpOnNextListener;
import com.zxcn.imai.smart.http.ProgressSubscriber;
import com.zxcn.imai.smart.http.RxHttpUtils;
import com.zxcn.imai.smart.http.request.UpLoadData;
import com.zxcn.imai.smart.http.result.ResUploadInfoBean;
import com.zxcn.imai.smart.util.DateUtils;
import com.zxcn.imai.smart.util.LogUtils;
import com.zxcn.imai.smart.util.SharedPreferencesUtils;

import java.util.HashMap;

/**
 * Created by xtich on 2017/10/31.
 */

public class DownLoadFingerDataPresenter {
    private String TAG = "060_DownLoadFingerDataPresenter";

    private final DownLoadFingerView listener;

    public DownLoadFingerDataPresenter(DownLoadFingerView bindFingerView) {
        this.listener=bindFingerView;

    }
    /**
     * 从服务器拉取所有数据
     */
    public void uploadData() {
        UploadDataBean requestBean = new UploadDataBean();
        requestBean.setDeviceId("dev10001");
        requestBean.setOrgCode(SharedPreferencesUtils.getInstance().getString(SpConstant.ORG_CODE));
        requestBean.setImaiToken(SharedPreferencesUtils.getInstance().getString(SpConstant.TOKEN));
        requestBean.setTime(SharedPreferencesUtils.getInstance().getString(SpConstant.UPDATA_TIME));
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("json", new Gson().toJson(requestBean));
        LogUtils.downLoad( new Gson().toJson(requestBean));
        Log.e(TAG, "uploadData >>" + new Gson().toJson(requestBean));
        RxHttpUtils.requestPost(new UpLoadData(new ProgressSubscriber( getuploadDataListener()), hashMap));

    }

    private HttpOnNextListener getuploadDataListener() {
        return new HttpOnNextListener<BaseData<ResUploadInfoBean>>() {
            @Override
            public void onNext(BaseData<ResUploadInfoBean> data) {
                if (data.status.equals("1")) {
                    SharedPreferencesUtils.getInstance().putString(SpConstant.UPDATA_FINGER_TIME, DateUtils.getDateStr(System.currentTimeMillis()));
//                    SharedPreferencesUtils.getInstance().putString(SpConstant.UPDATA_TIME,"");
                    if (data.sysdata.pageInfo.list.size() > 0) {
                        Log.e(TAG, "list.size() : " + data.sysdata.pageInfo.list.size());
                        listener.getSucess(data.sysdata.pageInfo.list);
                    } else {
                        Log.e(TAG, "listener.error" );
                        listener.error(data.message);
                    }
                } else {
                    listener.error(data.message);

                }
            }
        };
    }
}
