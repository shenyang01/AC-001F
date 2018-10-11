package com.zxcn.imai.smart.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xtich on 2017/10/25.
 */

public class UploadDataBean {

    /**
     * time :
     * deviceId : dev10001
     * imaiToken : beee239c38534d3d0735fbbae7396933
     * orgCode : zxcn.com
     */

    @SerializedName("time")
    public String time;
    @SerializedName("deviceId")
    public String deviceId;
    @SerializedName("imaiToken")
    public String imaiToken;
    @SerializedName("orgCode")
    public String orgCode;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
}
