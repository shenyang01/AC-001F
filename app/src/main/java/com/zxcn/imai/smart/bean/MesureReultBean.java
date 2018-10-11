package com.zxcn.imai.smart.bean;

/**
 * Created by xtich on 2017/10/17.
 */

public class MesureReultBean {

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
    private String checkTime;    //时间
    private String dataType;  //1  血压 2  血糖
    private String data1;     //高压

    public String getData10() {
        return data10;
    }

    public void setData10(String data10) {
        this.data10 = data10;
    }

    private String data10;     //容积波数组

    @Override
    public String toString() {
        return "MesureReultBean{" +
                "mobile='" + mobile + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", imaiToken='" + imaiToken + '\'' +
                ", orgCode='" + orgCode + '\'' +
                ", cardNum='" + cardNum + '\'' +
                ", checkTime='" + checkTime + '\'' +
                ", dataType='" + dataType + '\'' +
                ", data1='" + data1 + '\'' +
                ", data2='" + data2 + '\'' +
                ", data3='" + data3 + '\'' +
                ", data15='" + data15 + '\'' +
                '}';
    }

    private String data2;      //低压
    private String data3;

    public String getData15() {
        return data15;
    }

    public void setData15(String data15) {
        this.data15 = data15;
    }

    private String data15;

    //心率

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

    public String getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(String checkTime) {
        this.checkTime = checkTime;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getData1() {
        return data1;
    }

    public void setData1(String data1) {
        this.data1 = data1;
    }

    public String getData2() {
        return data2;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }

    public String getData3() {
        return data3;
    }

    public void setData3(String data3) {
        this.data3 = data3;
    }
}
