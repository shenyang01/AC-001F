package com.zxcn.imai.smart.activity.pressure;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.activity.common.MainActivity;
import com.zxcn.imai.smart.activity.intr.IUpDataMesureDataView;
import com.zxcn.imai.smart.activity.intr.UpDataPressureDataPresenter;
import com.zxcn.imai.smart.base.BaseActivity;
import com.zxcn.imai.smart.base.SmartApplication;
import com.zxcn.imai.smart.base.SpConstant;
import com.zxcn.imai.smart.db.DbUtils;
import com.zxcn.imai.smart.db.module.Cache_UserInfo;
import com.zxcn.imai.smart.ui.HeaderView;
import com.zxcn.imai.smart.util.DateUtils;
import com.zxcn.imai.smart.util.LogUtils;
import com.zxcn.imai.smart.util.SharedPreferencesUtils;
import com.zxcn.imai.smart.util.SpUtils;

/**
 * Created by ZXCN1 on 2017/9/4.
 */

public class MeasureResultActivity extends BaseActivity implements IUpDataMesureDataView {

    private String tag = "060_MeasureResultActivity";
    private final int TOUCH_MSG = 1;

    private HeaderView headerView;
    private TextView highBpTV;
    private TextView lowBpTV;
    private TextView pulseTV;
    private TextView timeTV, result_card_name, result_card_number, resultstatus;
    private Button MeasurementCompletion;

    private UpDataPressureDataPresenter pressureDataPresenter;
    private boolean isConnect = false;

    @Override
    protected View setView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_measure_result, null);
    }

    @Override
    protected void setEvent() {
        SmartApplication.getInstance().addAcitivity(this);
        headerView = findViewById(R.id.headerView);
        highBpTV = findViewById(R.id.highBpTV);
        lowBpTV = findViewById(R.id.lowBpTV);
        pulseTV = findViewById(R.id.pulseTV);
        timeTV = findViewById(R.id.timeTV);
        MeasurementCompletion = findViewById(R.id.MeasurementCompletion);
        result_card_name = findViewById(R.id.result_card_name);
        result_card_number = findViewById(R.id.result_card_number);
        resultstatus = findViewById(R.id.resultstatus);

        String MOBILE_PHONE = SpUtils.getValue(SpConstant.MOBILE_PHONE, "");
        if (!TextUtils.isEmpty(MOBILE_PHONE)) { // 用户模式
            result_card_name.setVisibility(View.VISIBLE);
            // result_card_number.setVisibility(View.VISIBLE);
            result_card_name.setText(MOBILE_PHONE);
        }
        headerView.setLeftClickListener(view -> onBackPressed());
        headerView.hideLeft();
        MeasurementCompletion.setOnClickListener(view -> MainActivity.newActivity(this));


        if (SpUtils.getValue(SpConstant.IN_TYPE, " ").equals(SpConstant.SCAN)) { // 用户模式
            result_card_name.setVisibility(View.VISIBLE);
            result_card_name.setText("用户：" + SpUtils.getValue(SpConstant.MOBILE_PHONE, " "));
        } else if (SpUtils.getValue(SpConstant.IN_TYPE, " ").equals(SpConstant.FINGERPRITET)) {
            result_card_name.setVisibility(View.VISIBLE);
            result_card_name.setText("用户：" + SpUtils.getValue(SpConstant.MOBILE_PHONE, " "));
        }

        if (inspectNet()) {
            isConnect = true;
        }
    }

    @Override
    protected void getData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BPManager.getInstance().stop();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String data1 = String.valueOf(getIntent().getIntExtra("highBp", 0));
        String data2 = String.valueOf(getIntent().getIntExtra("lowBp", 0));
        String data3 = String.valueOf(getIntent().getIntExtra("pulse", 0));
        String data15 = "1";
        String type = "1";
        String phone = SpUtils.getValue(SpConstant.MOBILE_PHONE, "");
        Log.e(tag, " onResume  phone " + phone);

        String cardNum = SharedPreferencesUtils.getInstance().getString("cardNum");

        Log.e("tag", " onResume   " + data1 + "   " + data2 + "   " + data3);
        highBpTV.setText(data1);
        lowBpTV.setText(data2);
        pulseTV.setText(data3);
        timeTV.setText(DateUtils.getDateStr(System.currentTimeMillis()));

        if (SpUtils.getValue(SpConstant.IN_TYPE, " ").equals(SpConstant.SCAN) || SpUtils.getValue(SpConstant.IN_TYPE, " ").equals(SpConstant.FINGERPRITET)) { // 用户模式
            if (isConnect) {   //联网
                LogUtils.LogNet("is_connected");
                upDataToserver(data1, data2, data3, data15, "", DateUtils.getDateStr(System
                        .currentTimeMillis()), type, phone);
            } else {
                LogUtils.LogNet("un_connected");
                saveLoaction(data1, data2, data3, type, phone, cardNum);
            }
        }
    }

    private void saveLoaction(String data1, String data2, String data3, String type, String phone, String cardNum) {
        Cache_UserInfo info = new Cache_UserInfo();
        info.cardNum = cardNum;
        info.phone = phone;
        info.data_type = type;
        info.data1 = data1;
        info.data2 = data2;
        info.data3 = data3;
        info.time = DateUtils.getDateStr(System.currentTimeMillis());
        LogUtils.save(info.toString());
        DbUtils.insert(info);
    }

    /**
     * 上传数据到服务器
     *
     * @param data1
     * @param data2
     * @param data3
     * @param dataTime
     * @param type
     * @param phone
     * @param
     */
    private void upDataToserver(String data1, String data2, String data3, String data15, String
            data10, String dataTime, String type, String phone) {
        if (pressureDataPresenter == null) {
            pressureDataPresenter = new UpDataPressureDataPresenter(this);
            pressureDataPresenter.postPressureData(MeasureResultActivity.this, data1, data2,
                    data3, data15, data10, dataTime, type, phone);
        }
    }

    private Long firstBackTime = 0l;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SmartApplication.getInstance().finshActivity();
        //取消订阅
        handler.removeMessages(TOUCH_MSG);
        MainActivity.newActivity(this);
        finish();
    }

    private long lastTouchTime = -1l;       //最后一次触屏时间

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        lastTouchTime = System.currentTimeMillis();
        return super.onTouchEvent(event);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e("MeasureResult", (System.currentTimeMillis() - lastTouchTime) + "");
            if (lastTouchTime < 0) {
                //如果为初始状态，则赋值为当前时间
                lastTouchTime = System.currentTimeMillis();
                handler.sendEmptyMessageDelayed(TOUCH_MSG, 1000);
            } else if (System.currentTimeMillis() - lastTouchTime > 30000) {
                //如果两次触屏时间超过30s，则执行超时操作
                MainActivity.newActivity(MeasureResultActivity.this);
            } else {
                handler.sendEmptyMessageDelayed(TOUCH_MSG, 1000);
            }
        }
    };


    @Override
    public void getSucess() {
        Log.e("tag", "数据上传成功");
    }

    @Override
    public void upLoadSucess() {

    }

    @Override
    public void error(String str) {
        Log.e("tag", "数据上传失败");
    }
}
