package com.zxcn.imai.smart.activity.pressure;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.base.AppConstant;
import com.zxcn.imai.smart.base.BaseActivity;
import com.zxcn.imai.smart.base.SmartApplication;
import com.zxcn.imai.smart.base.SpConstant;
import com.zxcn.imai.smart.db.DbUtils;
import com.zxcn.imai.smart.db.module.SmartBean;
import com.zxcn.imai.smart.ui.BpBoardView;
import com.zxcn.imai.smart.ui.HeaderView;
import com.zxcn.imai.smart.util.AdbShellUtils;
import com.zxcn.imai.smart.util.SpUtils;
import com.zxcn.imai.smart.util.ToastUtils;


/**
 * Created by ZXCN1 on 2017/9/4.
 */

public class MeasureActivity extends BaseActivity implements IBPCallback {
    String TAG = "060_MeasureActivity";

    private HeaderView headerView;
    private BpBoardView bpBoardView;
    private TextView pressureTV;
    //    private PressureManager pressureManager;
    private boolean is_open = false;
    private int inf;
    private boolean open = false;

    protected SmartApplication mApplication;
    private BPManager pm;


    @Override
    protected View setView() {
//        SmartApplication.getInstance().addAcitivity(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return LayoutInflater.from(this).inflate(R.layout.activity_measure, null);
    }

    private void initSerialPort() {
        pm = BPManager.getInstance();
        BPManager.setCallback(this);
        mApplication = SmartApplication.getInstance();
        pm.start(mApplication);
    }

    @Override
    protected void setEvent() {
        headerView = findViewById(R.id.headerView);
        bpBoardView = findViewById(R.id.boardView);
        pressureTV = findViewById(R.id.pressureTV);
        initSerialPort();
        headerView.setLeftClickListener(view -> finish());
    }


    @Override
    protected void getData() {
        //打开设备启动测量
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            new Thread(() -> {
                AdbShellUtils.runCMD("echo 1 >" + AppConstant.BLOOD_POWER_PATCH);
                //打开设备启动测量
                pm.openBP();
                pm.openBP();
                pm.openBP();
                pm.openBP();
                pm.openBP();
                pm.openBP();
                pm.openBP();
                pm.openBP();
                pm.openBP();
                pm.startBP();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pm.startBP();
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            bpBoardView.stop();
            pm.stopBP();
            pm.closeBP();
            BPManager.getInstance().stop();
            AdbShellUtils.runCMD("echo 0 >" + AppConstant.BLOOD_POWER_PATCH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("tag", "onDestroy");
    }

    public static void newActivity(Context context) {
        context.startActivity(new Intent(context, MeasureActivity.class));
    }

    @Override
    public void onMeasure(Bundle bundle) {
        runOnUiThread(() -> {
            bpBoardView.start();
            pressureTV.setText(String.valueOf(bundle.getIntArray(BPManager.PRESSURE)[1]));
        });
    }

    @Override
    public void onEnd(Bundle bundle) {
        Log.e("tag", "onEnd");
        bpBoardView.stop();
        pm.stopBP();
        pm.closeBP();
        try {
            postData(bundle);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.toastShort(this, R.string.toast_measure_exception);
            startActivity(new Intent(MeasureActivity.this, MeasureErrActivity.class));
            finish();
        }


    }

    private void postData(Bundle bundle) {
        int[] array = bundle.getIntArray("pressure");

        Log.e("tag", "postData   " + array[0] + "  " + array[1] + "   " + array[2]);
        //        if (AppUtils.getNetType(this) != -1) {
        //            RetrofitFactory.getInstance().postData(SpUtils.getValue(SpConstant.USER_TYPE, AppConstant.USER_GRANDPA),
        //                    String.valueOf(array[0]),
        //                    String.valueOf(array[1]),
        //                    String.valueOf(array[2]),
        //                    String.valueOf(System.currentTimeMillis()),
        //                    success -> {
        //                        MeasureSugarResultActivity.newActivity(this, array[0], array[1], array[2]);
        //                    }, error -> {
        //                        //上传失败，数据本地保存
        //                        saveData(array);
        //                        MeasureSugarResultActivity.newActivity(this, array[0], array[1], array[2]);
        //                        if (error instanceof ResultException) {
        //                            ToastUtils.toastShort(this, ((ResultException) error).getResultMessage());
        //                        }
        //                    });
        //        } else {
        //没有网络，数据本地保存
        if (array[0] == 0 || array[1] == 0 || array[2] == 0) {
            startActivity(new Intent(MeasureActivity.this, MeasureErrActivity.class));
            finish();
        } else {
            startResultActivity(array[0], array[1], array[2]);
            saveData(array);
        }
    }

    private void startResultActivity(int h, int l, int p) {
        Log.e("tag", "startActivity  " + h + "  " + l + "    " + p);
        Intent intent = new Intent(MeasureActivity.this, MeasureResultActivity.class);
        intent.putExtra("highBp", h);
        intent.putExtra("lowBp", l);
        intent.putExtra("pulse", p);
        startActivity(intent);
        finish();
    }

    private void saveData(int[] array) {
        SmartBean smartBean = new SmartBean();
        smartBean.userId = SpUtils.getValue(SpUtils.getValue(SpConstant.USER_TYPE, AppConstant.USER_GRANDPA), "");
        smartBean.dataType = AppConstant.TYPE_BP;
        smartBean.dataTime = String.valueOf(System.currentTimeMillis());
        smartBean.orgCode = SpUtils.getValue(SpConstant.ORG_CODE, "");
        smartBean.data1 = String.valueOf(array[0]);
        smartBean.data2 = String.valueOf(array[1]);
        smartBean.data3 = String.valueOf(array[2]);
        DbUtils.save(smartBean);
    }

    @Override
    public void onHelp(int helpMsgId, String helpString) {
        Log.e("xy", "onHelp");
    }

    @Override
    public void onTestMeasure(int i, String msg, int info) {

    }

    @Override
    public void onBackPressed() {

    }

}
