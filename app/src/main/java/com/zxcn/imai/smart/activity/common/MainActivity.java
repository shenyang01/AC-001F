package com.zxcn.imai.smart.activity.common;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.zxcn.finger.FingerOpenListener;
import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.activity.finger.FingerIdentifyActivity1;
import com.zxcn.imai.smart.activity.intr.GetTokenPresenter;
import com.zxcn.imai.smart.activity.intr.IGetToken;
import com.zxcn.imai.smart.base.BaseActivity;
import com.zxcn.imai.smart.base.SpConstant;
import com.zxcn.imai.smart.db.DbUtils;
import com.zxcn.imai.smart.db.module.Cache_UserInfo;
import com.zxcn.imai.smart.db.module.UserInfo;
import com.zxcn.imai.smart.ui.HeaderView;
import com.zxcn.imai.smart.util.LogUtils;
import com.zxcn.imai.smart.util.SharedPreferencesUtils;
import com.zxcn.imai.smart.util.SpUtils;
import com.zxcn.imai.smart.util.ToastUtils;
import com.zxcn.imai.smart.zxing.activity.CaptureActivity;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;


/**
 * Created by ZXCN1 on 2017/9/1.
 */

public class MainActivity extends BaseActivity implements FingerOpenListener, IGetToken, View.OnClickListener {

    private GetTokenPresenter presenter;
    private String tag = "060_MainActivity";
    public final static int REQUEST_READ_PHONE_STATE = 1;


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected View setView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_main, null);
    }

    @Override
    protected void setEvent() {
        firstgetTokenSuc = false;
        TextView non_Registration, scanning, finger_tv, register_tv;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        HeaderView headerView = findViewById(R.id.headerView);
        non_Registration = findViewById(R.id.non_Registration);
        scanning = findViewById(R.id.scanning);
        finger_tv = findViewById(R.id.finger_tv);
//        setting_tv = findViewById(R.id.setting_tv);
        register_tv = findViewById(R.id.register_tv);
        non_Registration.setOnClickListener(this);
        scanning.setOnClickListener(this);
        finger_tv.setOnClickListener(this);
//        setting_tv.setOnClickListener(this);
        register_tv.setOnClickListener(this);

        headerView.getLeftTV().setCompoundDrawables(null, null, null, null);
        headerView.getLeftTV().setTextColor(getResources().getColor(R.color.transport));


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.non_Registration: //非注册用户
                SpUtils.saveString(SpConstant.MOBILE_PHONE, "");
                startActivity(new Intent(MainActivity.this, FunctionChoseActivity.class));
                SpUtils.saveString(SpConstant.IN_TYPE, SpConstant.NOREGISTER);
                break;
            case R.id.scanning:  // 扫码测量
                startActivity(new Intent(MainActivity.this, CaptureActivity.class));
                SpUtils.saveString(SpConstant.IN_TYPE, SpConstant.SCAN);
                break;
            case R.id.finger_tv:  //指纹测量
                //if (DbUtils.hasUser())
                if (inspectNet()) {
                    startActivity(new Intent(MainActivity.this, FingerIdentifyActivity1.class));
                } else {
                    ToastUtils.toastShort("请连接WIFI");
                }
                 /* else {
                    //没有数据进绑定界面
                    ToastUtils.toastShort(this, R.string.toast_fp_empty);
                    startActivity(new Intent(MainActivity.this, RegisterActivity1.class));
                }*/
                SpUtils.saveString(SpConstant.IN_TYPE, SpConstant.FINGERPRITET);
                break;
            case R.id.register_tv:  //注册
                startActivity(new Intent(MainActivity.this, RegisterActivity1.class));
                break;
//            case R.id.setting_tv:  //设置
//                startActivity(new Intent(MainActivity.this, SettingActivity.class));
//                break;

        }
    }

    @Override
    protected void getData() {

        //启动时判断网络状态
        boolean netConnect = this.isNetConnect();
        if (netConnect) {
            //            net_textView.setVisibility(View.GONE);
        } else {
            //            net_textView.setVisibility(View.VISIBLE);
        }

        presenter = new GetTokenPresenter(this);
        presenter.getToken(MainActivity.this);


        //        TestData();

        getTokenForAnHour();

        //        test();

    }

    public static void test() {
        UserInfo m1 = new UserInfo();
        m1.phone = "13720291008";
        m1.userName = "hhh";
        m1.userType = "01";
        m1.cardNum = "430201122";
        m1.org_Code = "zxcn.com";
        m1.fingerOne = "10478965365453356633";
        m1.fingerTwo = "124579624554aagagagwgeg";
        DbUtils.insert(m1);

        UserInfo m2 = new UserInfo();
        m1.phone = "13111112222";
        m1.userName = "yyy";
        m1.userType = "02";
        m1.cardNum = "1201233";
        m1.org_Code = "zxcn.com";
        m1.fingerOne = "10478965365453356633";
        m1.fingerTwo = "124579624554aagagagwgeg";
        DbUtils.insert(m2);

        List<UserInfo> m = DbUtils.QueryAll(UserInfo.class);
        for (int i = 0; i < m.size(); i++) {
            Log.e("060_1", m.get(i).toString());
        }
    }


    final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    /**
     * 定时从后台获取设备token
     */
    public void getTokenForAnHour() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {       //没有权限 去申请
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_WIFI_STATE}, REQUEST_READ_PHONE_STATE);
        } else {            //有权限
            TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = telephonyManager.getDeviceId();
            if (imei != null) {
                LogUtils.LogFinger(imei);
            }
        }

        SharedPreferencesUtils.getInstance().putString(SpConstant.DEVICE_ID, "dev10001");

        final Runnable beeper = new Runnable() {
            @Override
            public void run() {
                presenter = new GetTokenPresenter(MainActivity.this);
                presenter.getToken(MainActivity.this);
            }
        };
        final ScheduledFuture<?> beeperHandle =
                scheduler.scheduleAtFixedRate(beeper, 10, 1 * 60, SECONDS);
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                beeperHandle.cancel(true);
            }
        }, 60 * 60, SECONDS);
    }


    public static void newActivity(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    public void onBackPressed() {

    }


    @Override
    public void openDeviceSuccess(int i) {
        SpUtils.saveBoolean(SpConstant.FLAG_FINGER_OPEN, true);

    }

    @Override
    public void openDeviceFail(int i) {
        SpUtils.saveBoolean(SpConstant.FLAG_FINGER_OPEN, false);
        runOnUiThread(() -> ToastUtils.toastShort(this, R.string.toast_fp_start_error));
    }

    @Override
    public void usbFail(int i) {
        runOnUiThread(() -> {
            SpUtils.saveBoolean(SpConstant.FLAG_FINGER_OPEN, false);
            ToastUtils.toastShort(this, R.string.toast_fp_start_error);
        });
    }

    private boolean firstgetTokenSuc = false;

    @Override
    public void getTokenSuccess() {
        Log.e(tag, "getToken getTokenSuccess back");
        if (!firstgetTokenSuc) {
            Toast.makeText(MainActivity.this, "设备联网成功", Toast.LENGTH_SHORT).show();
            firstgetTokenSuc = true;
        }
    }

    @Override
    public void error(String error) {
        ToastUtils.toastShort(MainActivity.this, error);
    }


    private void TestData() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {       //没有权限 去申请
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_WIFI_STATE}, REQUEST_READ_PHONE_STATE);
        } else {            //有权限
            TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = telephonyManager.getDeviceId();
            if (imei != null) {
                LogUtils.LogFinger(imei);
            }
        }

        SharedPreferencesUtils.getInstance().putString(SpConstant.DEVICE_ID, "dev10001");
        //        String [] str={"420102198302180354"};
        //        List<VipInfo> ls= DbUtils.getQueryByWhere(VipInfo.class,"cardNum",str);
        //        if (ls!=null&&ls.size()>0){
        //            LogUtils.LogNet(ls.get(0).liveValue);
        //        }

        queryCacheData();
    }

    private void queryCacheData() {
        List<Cache_UserInfo> list = DbUtils.QueryAll(Cache_UserInfo.class);
        if (list != null && list.size() > 0) {
            for (Cache_UserInfo ls : list) {
                LogUtils.LogSugar(ls.toString());
            }
        }
    }


}
