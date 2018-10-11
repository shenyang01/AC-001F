package com.zxcn.imai.smart.http.request;



import com.zxcn.imai.smart.http.BaseEntity;
import com.zxcn.imai.smart.http.HttpService;

import java.util.Map;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by xtich on 2017/10/16.
 */

public class GetCode extends BaseEntity {

    private final Map<String, String> hashMap;
    private final Subscriber subscriber;

    public GetCode(Subscriber getTopMovieOnNext, Map<String,String> hashMap) {
        subscriber =getTopMovieOnNext;
        this.hashMap=hashMap;
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.getCode(hashMap);
    }

    @Override
    public Subscriber getSubscirber() {
        return subscriber;
    }

}
