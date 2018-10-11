package com.zxcn.imai.smart.activity.common;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.base.BaseActivity;
import com.zxcn.imai.smart.ui.HeaderView;
import com.zxcn.imai.smart.util.AdbShellUtils;

/**
 * @ data  2018/7/9 11:17
 * @ author  zxcg
 */
public class DeviceIDActivity extends BaseActivity {
    @Override
    protected View setView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_deviceid, null);
    }

    @Override
    protected void setEvent() {
        HeaderView headerView = findViewById(R.id.deviceID_headerView);
        headerView.getLeftTV().setOnClickListener(v -> finish());
        TextView deviceID = findViewById(R.id.deviceID);
        String DeviceID = AdbShellUtils.runCMD("cat " +
                "/sys/bus/platform/drivers/zxcn_signal/zxcn_version");
        Log.e("tag","  DeviceID "+DeviceID);
        if (null != DeviceID)
            deviceID.setText(DeviceID);

    }

    @Override
    protected void getData() {

    }
}
