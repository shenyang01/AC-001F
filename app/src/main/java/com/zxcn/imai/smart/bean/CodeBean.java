package com.zxcn.imai.smart.bean;

/**
 * Created by ZXCN1 on 2017/9/18.
 */

public class CodeBean {
//    private String type = "SMS";
//    private String remindType = "短信提醒";
//    private String fromUserid = "1212121";
//    public String toUserids;
//    private String smsTemplateCode = "SMS_78645033";
////    public String  content;
//    public Map<String, String> content;
//    private String returns = "1212121回传给调用者的信息";
//    private String terminal = "pc";
//    private String currentAuth = "zxcn";


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getImaiToken() {
        return imaiToken;
    }

    public void setImaiToken(String imaiToken) {
        this.imaiToken = imaiToken;
    }

    public String mobile;
    public String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String   deviceId;
    public String orgCode  ;
    public String     imaiToken ;
}
