package com.zxcn.imai.smart.http;

/**
 * Created by gvesryb on 2016/11/10.
 */

public class EventBusUtils {
    private String mMsg;
    private String msg_flag;
    public int message;
    public String flag;
    public String getmMsg() {
        return mMsg;
    }

    public String getMsg_flag() {
        return msg_flag;
    }

    public EventBusUtils(String msg) {
        mMsg = msg;
    }

    public String getMsg() {
        return mMsg;
    }


    public String getFlag() {
        return flag;
    }

    public EventBusUtils(int message) {
        this.message = message;
    }

    public EventBusUtils(int message, String flag) {
        this.message = message;
        this.flag = flag;
    }

    public EventBusUtils(String msg, String flag) {
        this.msg_flag = msg;
        this.flag = flag;
    }
}
