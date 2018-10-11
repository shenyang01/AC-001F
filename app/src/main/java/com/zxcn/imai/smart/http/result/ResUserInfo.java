package com.zxcn.imai.smart.http.result;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by xtich on 2017/10/26.
 */

public class ResUserInfo implements Serializable {

    /**
     * elderlyId : 199
     * elderlyName : 龚平测试11
     * elderlyCode : 22
     * cardNum : 420108198708301598
     * liveValue : 天赐楼-楼层2-房间202-床位20201
     * nursingLevel :
     * selfCareLevel :
     * picHead : http://zxcn-pic.oss-cn-hangzhou.aliyuncs.com/zhengqy%40zxcn.com/420108198708301598
     * orgCode : zxcn.com
     */

    @SerializedName("elderlyId")
    public int elderlyId;
    @SerializedName("elderlyName")
    public String elderlyName;
    @SerializedName("elderlyCode")
    public int elderlyCode;
    @SerializedName("cardNum")
    public String cardNum;
    @SerializedName("liveValue")
    public String liveValue;
    @SerializedName("nursingLevel")
    public String nursingLevel;
    @SerializedName("selfCareLevel")
    public String selfCareLevel;
    @SerializedName("picHead")
    public String picHead;
    @SerializedName("orgCode")
    public String orgCode;

    @Override
    public String toString() {
        return "ResUserInfo{" +
                "elderlyId=" + elderlyId +
                ", elderlyName='" + elderlyName + '\'' +
                ", elderlyCode=" + elderlyCode +
                ", cardNum='" + cardNum + '\'' +
                ", liveValue='" + liveValue + '\'' +
                ", nursingLevel='" + nursingLevel + '\'' +
                ", selfCareLevel='" + selfCareLevel + '\'' +
                ", picHead='" + picHead + '\'' +
                ", orgCode='" + orgCode + '\'' +
                '}';
    }

    public int getElderlyId() {
        return elderlyId;
    }

    public void setElderlyId(int elderlyId) {
        this.elderlyId = elderlyId;
    }

    public String getElderlyName() {
        return elderlyName;
    }

    public void setElderlyName(String elderlyName) {
        this.elderlyName = elderlyName;
    }

    public int getElderlyCode() {
        return elderlyCode;
    }

    public void setElderlyCode(int elderlyCode) {
        this.elderlyCode = elderlyCode;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public String getLiveValue() {
        return liveValue;
    }

    public void setLiveValue(String liveValue) {
        this.liveValue = liveValue;
    }

    public String getNursingLevel() {
        return nursingLevel;
    }

    public void setNursingLevel(String nursingLevel) {
        this.nursingLevel = nursingLevel;
    }

    public String getSelfCareLevel() {
        return selfCareLevel;
    }

    public void setSelfCareLevel(String selfCareLevel) {
        this.selfCareLevel = selfCareLevel;
    }

    public String getPicHead() {
        return picHead;
    }

    public void setPicHead(String picHead) {
        this.picHead = picHead;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
}
