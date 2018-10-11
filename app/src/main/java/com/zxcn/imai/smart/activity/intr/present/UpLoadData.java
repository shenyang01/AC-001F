package com.zxcn.imai.smart.activity.intr.present;


import com.zxcn.imai.smart.http.BaseEntity;
import com.zxcn.imai.smart.http.HttpService;

import java.util.Map;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by xtich on 2017/10/16.
 */

public class UpLoadData extends BaseEntity {

    private final Map<String, String> hashMap;
    private final Subscriber subscriber;

    public UpLoadData(Subscriber getTopMovieOnNext, Map<String,String> hashMap) {
        subscriber =getTopMovieOnNext;
        this.hashMap=hashMap;
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.syncData(hashMap);
    }

    @Override
    public Subscriber getSubscirber() {
        return subscriber;
    }

}
