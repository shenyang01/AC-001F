package com.zxcn.imai.smart.bean;

/**
 * Created by xtich on 2017/10/17.
 */

public class FingerBindBean {

    /**
     * mobile : 13986185060
     * deviceId : 132
     * imaiToken : zxcn.com
     * orgCode : abc
     */

    private String mobile;   //手机号
    private String deviceId;
    private String imaiToken;
    private String orgCode;
    private String cardNum;
    private String type;  //1  血压 2  血糖
    private String finger1;     //指纹1

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

    public String getImaiToken() {
        return imaiToken;
    }

    public void setImaiToken(String imaiToken) {
        this.imaiToken = imaiToken;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getFinger1() {
        return finger1;
    }

    public void setFinger1(String finger1) {
        this.finger1 = finger1;
    }

    public String getFinger2() {
        return finger2;
    }

    public void setFinger2(String finger2) {
        this.finger2 = finger2;
    }

    private String finger2;      //指纹2


}
