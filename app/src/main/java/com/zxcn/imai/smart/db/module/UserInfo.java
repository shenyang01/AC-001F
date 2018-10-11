package com.zxcn.imai.smart.db.module;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.annotation.Unique;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

/**
 * Created by ZXCN1 on 2017/9/11.
 */
@Table("tb_finger")
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1425889039277875917L;

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;

    @Column("finger1")
    public String fingerOne;

    @Column("finger2")
    public String fingerTwo;

    @Column("phone")
    public String phone;

    @Unique
    @Column("user_type")
    public String userType;

    @Column("user_name")
    public String userName;

    @Column("org_Code")
    public String org_Code;

    @Column("cardNum")
    public String cardNum;


}
