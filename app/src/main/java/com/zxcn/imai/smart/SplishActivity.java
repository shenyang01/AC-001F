package com.zxcn.imai.smart;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxcn.imai.smart.base.BaseActivity;
import com.zxcn.imai.smart.util.SetStateUtils;

/**
 * Created by xtich on 2017/12/1.
 */

public class SplishActivity extends BaseActivity {

    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected View setView() {
        return LayoutInflater.from(this).inflate(R.layout.sp, null);
    }

    @Override
    protected void setEvent() {
        ImageView iv_sp=findViewById(R.id.iv_sp);
        iv_sp.setOnClickListener(view -> finish());

        setBrightness(this,30);
        acquireWakeLock();

    }
    //获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
    private void acquireWakeLock()
    {
        if (null == wakeLock)
        {
            PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
            assert pm != null;
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, "PostLocationService");
            if (null != wakeLock)
            {
                wakeLock.acquire();
            }
        }
    }
    @Override
    public void onBackPressed() {
    }
    //释放设备电源锁
    private void releaseWakeLock()
    {
        if (null != wakeLock)
        {
            wakeLock.release();
            wakeLock = null;
        }
    }
    /**
     * 设置电池状态
     */
    public void setStateMessage() {
        TextView btState = findViewById(R.id.base_tv);
        if (SetStateUtils.newInstance().getBtState()) {
            btState.setVisibility(View.VISIBLE);
        } else {
            btState.setVisibility(View.GONE);
        }
        btState.setText(getString(R.string.state));
    }
    @Override
    protected void getData() {

    }


    /**
     * 设置当前Activity显示时的亮度
     * 屏幕亮度最大数值一般为255，各款手机有所不同
     * screenBrightness 的取值范围在[0,1]之间
     */
    public  void setBrightness(Activity activity, int brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        activity.getWindow().setAttributes(lp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setStateMessage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseWakeLock();

    }
}
