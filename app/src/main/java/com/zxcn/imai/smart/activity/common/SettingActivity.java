package com.zxcn.imai.smart.activity.common;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.activity.intr.GetTokenPresenter;
import com.zxcn.imai.smart.activity.intr.IGetToken;
import com.zxcn.imai.smart.base.BaseActivity;
import com.zxcn.imai.smart.base.SpConstant;
import com.zxcn.imai.smart.ui.HeaderView;
import com.zxcn.imai.smart.util.SharedPreferencesUtils;
import com.zxcn.imai.smart.util.ToastUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * @author ZXCN1
 * @ date 2017/9/1
 */

public class SettingActivity extends BaseActivity implements IGetToken {

    private String tag = "060_SettingActivity";
    private GetTokenPresenter presenter;

    @BindView(R.id.headerView)
    HeaderView headerView;
    @BindView(R.id.recyclerview)
    RecyclerView mwifi;

    private List<String> mDatas;
    SettingAdapter mAdapter;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected View setView() {

        return LayoutInflater.from(this).inflate(R.layout.activity_setting, null);
    }

//    @OnClick({R.id.mwifi})
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.mwifi:
//                startActivity(new Intent(SettingActivity.this, WifiConnActivity.class));
//                break;
//            default:
//                break;
//        }
//    }

    private void initDatas() {
        mDatas = new ArrayList<String>(Arrays.asList("WIFI",
                "时间设置", "设备ID号", "电池电量", "软件版本",
                "内部存储", "工程模式", "系统信息", "蓝牙"));
    }

    private void overrideCallback() {
        mAdapter.setOnItemClickLitener(new SettingAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.e(tag, "点击了 ：" + position);
//                Toast.makeText(SettingActivity.this, position+"", Toast.LENGTH_SHORT).show();

                switch (position) {
                    case 0:
                        Intent intent0 = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        startActivityForResult(intent0, 0);
//                        startActivity(new Intent(SettingActivity.this, WifiConnActivity.class));
                        break;
                    case 1:
                        Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
                        startActivityForResult(intent, 0);
                        break;
                    case 2:
                        startActivity(new Intent(SettingActivity.this, DeviceIDActivity.class));
                        break;
                    case 3:
                        Intent intent3 = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
                        startActivityForResult(intent3, 0);
                        break;
                    case 4:
                        Intent intent4 = new Intent(Settings.ACTION_DEVICE_INFO_SETTINGS);
                        startActivityForResult(intent4, 0);
                        break;
                    case 5:
                        Intent intent5 = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
                        startActivityForResult(intent5, 0);
                        break;
                    case 6:
                        if (isAvilible(SettingActivity.this, "com.zxcn.imai.smart.test")) {
                            method1();
                        } else {
                            Toast.makeText(SettingActivity.this, "请先安装工程测试软件", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 7:
                        startActivity(new Intent(SettingActivity.this, SystemInfoActivity.class));
                        break;
                    case 8:
                        Intent intent6 = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                        startActivityForResult(intent6, 0);
                        break;

                }
            }
        });

    }

    @Override
    protected void setEvent() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        //绑定初始化ButterKnife
        ButterKnife.bind(this);
        headerView = findViewById(R.id.headerView);
        headerView.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initDatas();
        mwifi = (RecyclerView) findViewById(R.id.recyclerview);
        mwifi.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new SettingAdapter(this, mDatas);
        mwifi.setAdapter(mAdapter);

        //override click callback
        overrideCallback();
        presenter = new GetTokenPresenter(this);
        presenter.getToken(SettingActivity.this);
        /**
         * 2.1.9	设备认证
         *
         * 送检注释掉。
         * */
        testData();
    }

    private void testData() {
        SharedPreferencesUtils.getInstance().putString(SpConstant.DEVICE_ID, "dev10001");
        SharedPreferencesUtils.getInstance().putString(SpConstant.ORG_CODE, "zxcn.com");
        SharedPreferencesUtils.getInstance().putString(SpConstant.TOKEN, "f324d6526d02a110c0aa2d11668f0b09");
    }

    @Override
    protected void getData() {
//        startService(new Intent(SettingActivity.this, ScreenService.class));
        //checkPermiss();
    }

    private void checkPermiss() {
        //检查权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //进入到这里代表没有权限.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 111);
        }
    }


    private boolean isAvilible(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (pinfo.get(i).packageName.equalsIgnoreCase(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static void newActivity(Context context) {
        context.startActivity(new Intent(context, SettingActivity.class));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public void getTokenSuccess() {
        //   ToastUtils.toastShort(MainActivity.this,"认证成功");
    }

    @Override
    public void error(String error) {
        ToastUtils.toastShort(SettingActivity.this, error);
    }

    private CountDownTimer timer = new CountDownTimer(30000, 1000) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            SettingActivity.newActivity(getApplicationContext());
        }
    };

    public void method1() {
        Intent intent = new Intent();
        intent.setData(Uri.parse("csd://pull.csd.demo/cyn?type=110"));
        intent.putExtra("", "");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
