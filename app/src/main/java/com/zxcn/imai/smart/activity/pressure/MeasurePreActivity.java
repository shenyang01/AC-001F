package com.zxcn.imai.smart.activity.pressure;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.activity.common.LoginActivity;
import com.zxcn.imai.smart.base.BaseActivity;
import com.zxcn.imai.smart.base.SmartApplication;
import com.zxcn.imai.smart.base.SpConstant;
import com.zxcn.imai.smart.db.DbUtils;
import com.zxcn.imai.smart.ui.HeaderView;
import com.zxcn.imai.smart.util.SpUtils;
import com.zxcn.imai.smart.util.ToastUtils;

/**
 * Created by ZXCN1 on 2017/9/4.
 */

public class MeasurePreActivity extends BaseActivity{

    private HeaderView headerView;
    private TextView measureTV;
    private TextView bindTV;

    @Override
    protected View setView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_measure_pre,null);
    }

    @Override
    protected void setEvent() {
        headerView = findViewById(R.id.headerView);
        measureTV = findViewById(R.id.measureTV);
        bindTV = findViewById(R.id.bindTV);
        SmartApplication.getInstance().addAcitivity(this);
        headerView.setLeftClickListener(view -> finish());
        measureTV.setOnClickListener(view -> {
            if(TextUtils.isEmpty(SpUtils.getValue(SpConstant.USER_TYPE, ""))
                    || null == DbUtils.getUserInfo()){
                ToastUtils.toastShort(this, R.string.toast_login_first);
            } else {
                MeasureTipActivity.newActivity(this);
            }
        });
        bindTV.setOnClickListener(view -> LoginActivity.newActivity(this));
    }

    @Override
    protected void getData() {

    }

    public static void newActivity(Context context) {
        context.startActivity(new Intent(context, MeasurePreActivity.class));
    }
}
