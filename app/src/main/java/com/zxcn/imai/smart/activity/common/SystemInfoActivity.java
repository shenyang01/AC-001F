package com.zxcn.imai.smart.activity.common;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.base.BaseActivity;
import com.zxcn.imai.smart.ui.HeaderView;

/**
 * @ data  2018/7/9 11:43
 * @ author  zxcg
 */
public class SystemInfoActivity extends BaseActivity {
    @Override
    protected View setView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_system_info,null);
    }

    @Override
    protected void setEvent() {
        HeaderView headerView = findViewById(R.id.systemInfo_headerView);
        headerView.getLeftTV().setOnClickListener(v -> finish());

        TextView systemInfo = findViewById(R.id.systemInfo);
        if(null!= Build.DISPLAY )
        systemInfo.setText(Build.DISPLAY );

    }

    @Override
    protected void getData() {

    }
}
