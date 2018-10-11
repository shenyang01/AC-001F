package com.zxcn.imai.smart.activity.pressure;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.base.BaseActivity;
import com.zxcn.imai.smart.base.SmartApplication;
import com.zxcn.imai.smart.base.SpConstant;
import com.zxcn.imai.smart.ui.HeaderView;
import com.zxcn.imai.smart.util.SpUtils;

/**
 * Created by ZXCN1 on 2017/9/4.
 */

public class MeasureTipActivity extends BaseActivity {

    private HeaderView headerView;
    private RelativeLayout rl_nextTV;
    private TextView card_name, card_number;

    @Override
    protected View setView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_measure_tip, null);
    }

    @Override
    protected void setEvent() {
        SmartApplication.getInstance().addAcitivity(this);
        card_name = findViewById(R.id.card_name);
        card_number = findViewById(R.id.card_number);
        headerView = findViewById(R.id.headerView);
        rl_nextTV = findViewById(R.id.rl_nextTV);
        headerView.setLeftClickListener(view -> finish());
        rl_nextTV.setOnClickListener(view -> {
            MeasureActivity.newActivity(this);
            finish();
        });
        card_name.setVisibility(View.GONE);
        // card_number.setVisibility(View.GONE);
        String MOBILE_PHONE = SpUtils.getValue(SpConstant.MOBILE_PHONE, "");
        if (!TextUtils.isEmpty(MOBILE_PHONE)) { // 用户模式
            card_name.setVisibility(View.VISIBLE);
            // card_number.setVisibility(View.VISIBLE);
            Log.e("tag", "用户模式 ");
            card_name.setText(MOBILE_PHONE);
            //card_number.setText(SpUtils.getValue("card_number",""));
        }
    }

    @Override
    protected void getData() {

    }

    public static void newActivity(Context context) {
        context.startActivity(new Intent(context, MeasureTipActivity.class));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
