package com.zxcn.imai.smart.activity.spo2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.activity.common.MainActivity;
import com.zxcn.imai.smart.activity.intr.IUpDataMesureDataView;
import com.zxcn.imai.smart.activity.intr.UpDataPressureDataPresenter;
import com.zxcn.imai.smart.base.BaseActivity;
import com.zxcn.imai.smart.base.SpConstant;
import com.zxcn.imai.smart.core.spo.BOxygenManager;
import com.zxcn.imai.smart.db.DbUtils;
import com.zxcn.imai.smart.db.module.Cache_UserInfo;
import com.zxcn.imai.smart.db.module.SpoData;
import com.zxcn.imai.smart.ui.HeaderView;
import com.zxcn.imai.smart.util.CustomCurveChart;
import com.zxcn.imai.smart.util.DateUtils;
import com.zxcn.imai.smart.util.LogUtils;
import com.zxcn.imai.smart.util.SharedPreferencesUtils;
import com.zxcn.imai.smart.util.SpUtils;
import com.zxcn.imai.smart.util.ToastUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author xtich
 * @date 2017/12/4
 * 血氧/脉率 测量页面
 */

public class Spo2MeasureActivity extends BaseActivity implements IUpDataMesureDataView {
    private String TAG = "060_Spo2MeasureActivity";

    private TextView tv1;
    private TextView tv2, tv_status;
    private TextView tv_time;
    private LinearLayout curveChart1, ll_one;
    private RelativeLayout line;
    private TextView tv_getdata, result_card_name;
    private HeaderView headerView;
    private int[] num;
    private Button MeasurementCompletion;
    private UpDataPressureDataPresenter pressureDataPresenter;
    private boolean isConnect = false;

    @Override
    protected View setView() {
        return LayoutInflater.from(this).inflate(R.layout.spo2_activity, null);
    }

    @Override
    protected void setEvent() {
        headerView = findViewById(R.id.headerView);
        tv_getdata = findViewById(R.id.tv_get_data);
        curveChart1 = findViewById(R.id.curveChart1);
        line = findViewById(R.id.line);
        ll_one = findViewById(R.id.ll_one);
        tv1 = findViewById(R.id.tv_1);
        tv_status = findViewById(R.id.tv_status);
        tv2 = findViewById(R.id.tv_2);
        tv_time = findViewById(R.id.tv_time);
        headerView.setLeftClickListener(view -> finish());
        // checkBluetoothPermission();
        MeasurementCompletion = findViewById(R.id.MeasurementCompletion);
        MeasurementCompletion.setOnClickListener(view -> {
            MainActivity.newActivity(this);
            finish();
        });
        result_card_name = findViewById(R.id.result_card_name);
        String MOBILE_PHONE = SpUtils.getValue(SpConstant.MOBILE_PHONE, "");
        if (!TextUtils.isEmpty(MOBILE_PHONE)) { // 用户模式
            result_card_name.setVisibility(View.VISIBLE);
            result_card_name.setText(MOBILE_PHONE);
        }
        if (inspectNet()) {
            isConnect = true;
        }
    }


    @Override
    protected void getData() {
        Intent intent = getIntent();
        int malv = intent.getIntExtra("malv", 0);
        int spo = intent.getIntExtra("spo", 0);
        List<Integer> list = intent.getIntegerArrayListExtra("list");

        setdata(malv, spo, list);
    }

    private void setdata(int malv, int spo, List<Integer> chat_list) {
        line.setVisibility(View.VISIBLE);
        ll_one.setVisibility(View.VISIBLE);
        tv_status.setVisibility(View.VISIBLE);

        tv_time.setText("测量时间：" + DateUtils.getDateStr(System.currentTimeMillis()));

        Log.e("chat_list", chat_list.toString());
        if (chat_list.size() > 0) {
            initCurveChart1(chat_list);
        } else {
            ToastUtils.toastShort("list为空");
        }
        if (spo <= 94) {
            tv_status.setText("血氧偏低，处于缺氧饱和");
        } else {
            tv_status.setText("血氧正常，处于正常氧饱和");
        }

        tv1.setText(malv + "");
        tv2.setText(spo + "%");
        String data15 = "6";
        String type = "6";
        String cardNum = SharedPreferencesUtils.getInstance().getString("cardNum");
        String phone = SpUtils.getValue(SpConstant.MOBILE_PHONE, "");
        if (SpUtils.getValue(SpConstant.IN_TYPE, " ").equals(SpConstant.SCAN) || SpUtils.getValue(SpConstant.IN_TYPE, " ").equals(SpConstant.FINGERPRITET)) { // 用户模式
            if (isConnect) {   //联网
                LogUtils.LogNet("is_connected");
                StringBuilder builder = new StringBuilder();
                if (num == null) return;
                for (int i = 0; i < num.length; i++) {
                    if (i < num.length - 1)
                        builder.append(num[i]).append(",");
                    else
                        builder.append(num[i]);
                }
                String data10 = builder.toString();
                Log.e("tag", "data10   " + builder.toString());
                upDataToserver(spo + "" + "", malv + "", "", data15, data10, DateUtils.getDateStr
                        (System.currentTimeMillis()), type, phone);
            } else {
                LogUtils.LogNet("un_connected");
                saveLoaction(spo + "%" + "", malv + "", type, phone, cardNum);
            }
        }
        BOxygenManager.getInstance().stop();
    }

    private void saveLoaction(String data1, String data2, String type, String phone, String cardNum) {
        SpoData info = new SpoData();
        info.cardNum = cardNum;
        info.phone = phone;
        info.data_type = type;
        info.data1 = data1;
        info.data2 = data2;
        info.time = DateUtils.getDateStr(System.currentTimeMillis());
        LogUtils.save(info.toString());
        DbUtils.insert(info);
    }

    private void checkBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //校验是否已具有模糊定位权限
            if (ContextCompat.checkSelfPermission(Spo2MeasureActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Spo2MeasureActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        123);
            }
        }
    }

    /**
     * 初始化曲线图数据
     */
    private void initCurveChart1(List<Integer> ls) {
        String[] xLabel = {"0", "", "", "", "5", "", "", "", "10", "", "", "", "15", "", "", "", "20", "", "", "", "25", "", "", "", "30s"};
        String[] yLabel = {"", "", "", "", "", "", "", "", "", "", "100"};
        if (ls.size() > 850) {
            num = sub23Bytes(ls, 25, 32);
        } else if (ls.size() > 800 && ls.size() < 850) {
            num = sub23Bytes(ls, 25, 30);
        } else {
            num = sub23Bytes(ls, 25, 2);
        }
        Log.e("num", "list_size" + ls.size() + ":" + Arrays.toString(num));
        List<int[]> data = new ArrayList<>();
        data.add(num);
        List<Integer> color = new ArrayList<>();
        color.add(R.color.color25);
        curveChart1.addView(new CustomCurveChart(this, xLabel, yLabel, data, color, false));
    }

    public int[] sub23Bytes(List<Integer> src, int count, int avg) {
        int[] num = new int[count];
        int s = 0;
        /**
         * 丢掉前面50个数据
         * 850个丢掉50 取出25个
         * */
        for (int a = 50; a < src.size(); a++) {
            if (a % avg == 0) {
                if (s < count) {
                    num[s] = src.get(a) / 4;
                    s++;
                }
            }
        }
        return num;
    }

    /**
     * 上传数据到服务器
     *
     * @param
     */
    private void upDataToserver(String data1, String data2, String data3, String data15, String
            data10, String dataTime, String type, String phone) {
        if (pressureDataPresenter == null) {
            pressureDataPresenter = new UpDataPressureDataPresenter(this);
            pressureDataPresenter.postPressureData(Spo2MeasureActivity.this, data1, data2,
                    data3, data15, data10,
                    dataTime, type, phone);
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // finish();
    }

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
