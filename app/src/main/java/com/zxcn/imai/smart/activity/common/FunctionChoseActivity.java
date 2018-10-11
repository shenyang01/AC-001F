package com.zxcn.imai.smart.activity.common;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.activity.pressure.MeasureTipActivity;
import com.zxcn.imai.smart.activity.spo2.Spo2TipActivity;
import com.zxcn.imai.smart.base.BaseActivity;
import com.zxcn.imai.smart.base.SmartApplication;
import com.zxcn.imai.smart.ui.HeaderView;

/**
 *
 * @author xtich
 * @date 2017/10/26
 */

public class FunctionChoseActivity extends BaseActivity implements View.OnClickListener {
    private HeaderView headerView;
    private ImageView iv_pressure,iv_spo;
    @Override
    protected View setView() {

        return LayoutInflater.from(this).inflate(R.layout.function_chose_activity, null);

    }

    @Override
    protected void setEvent() {
        SmartApplication.getInstance().addAcitivity(this);
        headerView = findViewById(R.id.headerView);
        iv_pressure = findViewById(R.id.iv_pressure);
        iv_spo = findViewById(R.id.iv_spo);
        headerView.setLeftClickListener(view -> finish());
        iv_pressure.setOnClickListener(this);
        iv_spo.setOnClickListener(this);
    }

    @Override
    protected void getData() {

    }


    @Override
    public void onClick(View view) {
        Intent intent=new Intent();
        switch (view.getId()){
            case R.id.iv_pressure:   //血压
               intent.setClass(FunctionChoseActivity.this,MeasureTipActivity.class) ;
                startActivity(intent);
                break;
            case R.id.iv_spo:    //血氧
                intent.setClass(FunctionChoseActivity.this,Spo2TipActivity.class) ;
                startActivity(intent);
                break;
        }
    }
}
