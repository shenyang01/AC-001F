package com.zxcn.imai.smart.http;

import com.google.gson.Gson;
import com.zxcn.imai.smart.util.LogUtils;


import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * 请求数据统一封装类
 */
public abstract class BaseEntity<T> implements Func1<BaseData<T>, BaseData<T>> {
    /**
     * 设置参数
     *
     * @param methods
     * @return
     */
    public abstract Observable getObservable(HttpService methods);

    /**
     * 设置回调sub
     *
     * @return
     */
    public abstract Subscriber getSubscirber();


    @Override
    public BaseData<T> call(BaseData<T> httpResult) {

        LogUtils.LogNet("------------返回数据-------"+getGson().toJson(httpResult));
        if (httpResult.status.equals("0")) {
            LogUtils.LogNet("------------数据异常---------------------");
            throw new HttpTimeException(httpResult.message);
        }

        return httpResult;
    }
    public Gson gson ;
    public Gson getGson(){
        if (gson==null){
            gson = new Gson();
        }
        return gson;
    }
}
