package com.zxcn.imai.smart.core.bglucose;

/**
 * Created by qqq on 2017/8/15.
 */

public interface IglucoseCallback {

    void onHelp(int helpMsgId, String buffer, String helpString );
    void sendResult(int helpMsgId, String buffer, String helpString ,String result);
    void onException(int helpMsgId, String buffer, String helpString );
}
