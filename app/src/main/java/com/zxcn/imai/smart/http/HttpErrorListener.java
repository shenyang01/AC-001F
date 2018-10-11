package com.zxcn.imai.smart.http;

/**
 * 成功回调处理
 * Created by WZG on 2016/7/16.
 */
public interface HttpErrorListener<T> {
    void onError(Throwable e);
}
