package com.zxcn.imai.smart.http;

import java.io.Serializable;

/**
 * 回调信息统一封装类
 * Created by WZG on 2016/7/16.
 */
public class BaseData<T> implements Serializable {
    public MapData<T> sysdata;
    public String status;
    public String message;
    public String messageCode;
    public String index;

}
