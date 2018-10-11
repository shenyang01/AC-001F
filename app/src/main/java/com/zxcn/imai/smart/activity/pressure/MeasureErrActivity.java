package com.zxcn.imai.smart.activity.pressure;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.activity.common.MainActivity;
import com.zxcn.imai.smart.base.BaseActivity;
import com.zxcn.imai.smart.base.SmartApplication;
import com.zxcn.imai.smart.util.DateUtils;

/**
 * @ data  2018/7/30 9:22
 * @ author  zxcg
 */
public class MeasureErrActivity extends BaseActivity {
    private TextView result_errTime;

    @Override
    protected View setView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_measure_err, null);
    }

    @Override
    protected void setEvent() {
        result_errTime = findViewById(R.id.result_errTime);
        findViewById(R.id.headerView).setOnClickListener(view -> {
            SmartApplication.getInstance().finshActivity();
            MainActivity.newActivity(this);
            finish();
        });
        findViewById(R.id.result_errStart).setOnClickListener(view -> {
            startActivity(new Intent(MeasureErrActivity.this, MeasureActivity.class));
            finish();
        });
    }

    @Override
    protected void getData() {
        result_errTime.setText(getString(R.string.label_measure_time).concat(DateUtils.getDateStr(System
                .currentTimeMillis
                        ())));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

}
