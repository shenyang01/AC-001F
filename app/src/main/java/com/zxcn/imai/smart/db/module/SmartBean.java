package com.zxcn.imai.smart.db.module;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.annotation.Unique;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

/**
 * Created by ZXCN1 on 2017/9/14.
 */
@Table("tb_data")
public class SmartBean implements Serializable {
    private static final long serialVersionUID = 7105233119761004646L;

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;

    @NotNull
    @Column("user_id")
    public String userId;

    @NotNull
    @Column("org_code")
    public String orgCode;

    @NotNull
    @Column("data_type")
    public String dataType;

    @NotNull
    @Column("data_time")
    public String dataTime;

    @Column("data1")
    public String data1;

    @Column("data2")
    public String data2;

    @Column("data3")
    public String data3;

    @Column("data4")
    public String data4;

    @Column("data5")
    public String data5;
}
