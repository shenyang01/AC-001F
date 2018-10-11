package com.zxcn.imai.smart.db.module;

import android.text.TextUtils;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

/**
 * Created by ZXCN1 on 2017/8/4.
 */
@Table("tb_cache_finger")
public class Cache_UserInfo implements Serializable {
    private static final long serialVersionUID = 5739544339414949L;
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;
    @Column("finger1")
    public String fingerOne;
    @Column("finger2")
    public String fingerTwo;
    @Column("phone")
    public String phone;
    @Column("user_type")
    public String userType;
    @Column("cardNum")
    public String cardNum;
    @Column("org_Code")
    public String orgCode;
    @Column("data1")
    public String data1;
    @Column("data2")
    public String data2;
    @Column("data3")
    public String data3;
    @Column("data4")
    public String data4;
    @Column("data_type")
    public String data_type;
    @Column("time")
    public String time;

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", fingerOne='" + fingerOne + '\'' +
                ", fingerTwo='" + fingerTwo + '\'' +
                ", phone='" + phone + '\'' +
                ", cardNum='" + cardNum + '\'' +
                ", orgCode='" + orgCode + '\'' +
                ", data1='" + data1 + '\'' +
                ", data2='" + data2 + '\'' +
                ", data3='" + data3 + '\'' +
                ", data4='" + data4 + '\'' +
                ", data4='" + time + '\'' +
                ", data_type='" + data_type + '\'' +
                '}';
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

    public String getData4() {
        return data4;
    }

    public void setData4(String data4) {
        this.data4 = data4;
    }

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getFingerOne() {
        return fingerOne;
    }

    public void setFingerOne(String fingerOne) {
        this.fingerOne = fingerOne;
    }

    public String getFingerTwo() {
        return fingerTwo;
    }

    public void setFingerTwo(String fingerTwo) {
        this.fingerTwo = fingerTwo;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
    public String getCardNum() {
        return TextUtils.isEmpty(cardNum) ? "" : cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }



}
