package com.zxcn.imai.smart.db.module;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

/**
 * @ data  2018/8/3 14:14
 * @ author  zxcg
 * 无网络时会员血氧数据缓存
 */
public class SpoData implements Serializable {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;

    @Column("phone")
    public String phone;
    @Column("user_type")
    public String userType;
    @Column("cardNum")
    public String cardNum;
    @Column("data_type")
    public String data_type;
    @Column("data1")
    public String data1;
    @Column("data2")
    public String data2;
    @Column("time")
    public String time;
}
