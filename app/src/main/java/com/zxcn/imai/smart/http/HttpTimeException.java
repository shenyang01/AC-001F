package com.zxcn.imai.smart.http;


import com.zxcn.imai.smart.util.ToastUtils;

/**
 * 自定义错误信息，统一处理返回处理
 * Created by WZG on 2016/7/16.
 */
public class HttpTimeException extends RuntimeException {

    public HttpTimeException(String detailMessage) {

        ToastUtils.toastShort(detailMessage);
     //  ErrorUtils.showErrorMsg(detailMessage);
        //SnackBarUtils.show(context,detailMessage);
    }
}

