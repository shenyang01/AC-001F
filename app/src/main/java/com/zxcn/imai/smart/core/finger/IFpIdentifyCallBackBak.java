package com.zxcn.imai.smart.core.finger;

import java.util.Map;

/**
 * Created by ZXCN1 on 2017/8/17.
 */

public interface IFpIdentifyCallBackBak {

    String FINGER_IDENTIFY_SUCCESS = "success";
    String FINGER_IDENTIFY_ERROR = "error";

    /**
     * @param errMsgId      错误信息码
     * @param errString     错误信息
     */
    void onAuthenticationError(int errMsgId, CharSequence errString);

    /**
     * @param helpMsgId      辅助信息码
     * @param helpString     辅助信息
     */
    void onAuthenticationHelp(int helpMsgId, CharSequence helpString);

    /**
     * 获取成功
     * @param result  指纹信息
     */
    void onAuthenticationSucceeded(Map<String, String> result);

    /**
     * 获取失败
     */
    void onAuthenticationFailed();
}
