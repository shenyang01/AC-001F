package com.zxcn.imai.smart.http.request;


import com.zxcn.imai.smart.http.BaseEntity;
import com.zxcn.imai.smart.http.HttpService;

import java.util.Map;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by xtich on 2017/10/16.
 */

public class CheckCode extends BaseEntity {

    private final Map<String, String> hashMap;
    private final Subscriber subscriber;

    public CheckCode(Subscriber getTopMovieOnNext, Map<String,String> hashMap) {
        subscriber =getTopMovieOnNext;
        this.hashMap=hashMap;
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.checkCode(hashMap);
    }

    @Override
    public Subscriber getSubscirber() {
        return subscriber;
    }

}
