package com.zxcn.imai.smart.activity.intr;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.zxcn.imai.smart.base.SpConstant;
import com.zxcn.imai.smart.bean.TokenBean;
import com.zxcn.imai.smart.http.BaseData;
import com.zxcn.imai.smart.http.HttpOnNextListener;
import com.zxcn.imai.smart.http.ProgressSubscriber;
import com.zxcn.imai.smart.http.RxHttpUtils;
import com.zxcn.imai.smart.http.request.GetToken;
import com.zxcn.imai.smart.http.result.ResTokenBean;
import com.zxcn.imai.smart.util.SharedPreferencesUtils;

import java.util.HashMap;

/**
 * Created by xtich on 2017/10/30.
 */

/**
 * 设备认证
 * http://iots.zxcnsmart.com:8080/iss/rest/pub/imai/checkDevice
 */
public class GetTokenPresenter {
    private String tag = "060_GetTokenPresenter";
    private Context context;
    private IGetToken iGetToken;

    public GetTokenPresenter(IGetToken iGetToken) {
        this.iGetToken = iGetToken;
    }

    /**
     * 获取token
     */
    public void getToken(Context context) {
        Log.e(tag, "getToken");
        this.context = context;
        TokenBean requestBean = new TokenBean();
        requestBean.setDeviceId("dev10001");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("json", new Gson().toJson(requestBean));
//        LogUtils.LogNet( new Gson().toJson(requestBean));
        Log.e(tag, "getToken ：" + new Gson().toJson(requestBean));
        RxHttpUtils.requestPost(new GetToken(new ProgressSubscriber((Activity) context, getFindPassListener(), false), hashMap));

    }

    private HttpOnNextListener getFindPassListener() {
        return new HttpOnNextListener<BaseData<ResTokenBean>>() {
            @Override
            public void onNext(BaseData<ResTokenBean> data) {
                if (data.status.equals("1")) {
                    if (data.sysdata.pageInfo.list.size() > 0) {
                        ResTokenBean bean = data.sysdata.pageInfo.list.get(0);
                        SharedPreferencesUtils.getInstance().putString(SpConstant.ORG_CODE, bean.orgCode);
                        SharedPreferencesUtils.getInstance().putString(SpConstant.TOKEN, bean.imaiToken);
                        // iGetToken.getTokenSuccess();
                        Log.e(tag, "getToken back:  " + bean.toString());
                       // Toast.makeText(context.getApplicationContext(), "设备联网成功", Toast
                               // .LENGTH_SHORT).show();
                    } else {
                        //iGetToken.error("认证失败");
                        Log.e(tag, "认证失败");
                    }
                } else {
                    //iGetToken.error(data.message);
                    Log.e(tag, "error message" + data.message);
                }
            }
        };
    }
}
