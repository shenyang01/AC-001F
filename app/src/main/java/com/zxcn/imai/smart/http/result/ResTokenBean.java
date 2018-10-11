package com.zxcn.imai.smart.http.result;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by xtich on 2017/10/25.
 */

public class ResTokenBean  implements Serializable {


    /**
     * imaiToken : 9d72e7154b7bf1ae1efbca26c11fbda8
     * org_code : zxcn.com
     */

    @SerializedName("imaiToken")
    public String imaiToken;
    @SerializedName("org_code")
    public String orgCode;

    @Override
    public String toString() {
        return "ResTokenBean{" +
                "imaiToken='" + imaiToken + '\'' +
                ", orgCode='" + orgCode + '\'' +
                '}';
    }
}
