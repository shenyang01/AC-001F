package com.zxcn.imai.smart.activity.spo2;

import android.app.Dialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.SmartApplication;
import com.zxcn.imai.smart.base.BaseActivity;
import com.zxcn.imai.smart.base.SpConstant;
import com.zxcn.imai.smart.core.spo.BOxygenManager;
import com.zxcn.imai.smart.core.spo.IBOxygenCallback;
import com.zxcn.imai.smart.ui.HeaderView;
import com.zxcn.imai.smart.util.LoadingDialog;
import com.zxcn.imai.smart.util.SpUtils;
import com.zxcn.imai.smart.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xtich
 * @date 2017/12/19
 */

public class Spo2TipActivity extends BaseActivity implements IBOxygenCallback {
    private final int HAS_FINGER = 0x11;
    private final int NO_FINGER = 0x12;
    private final int FINGER_OUT = 0X13;
    private final int HAS_DEVICE = 0X14;
    private final int NO_DEVICE = 0X15;
    BOxygenManager mBOxygenManager = new BOxygenManager(this);
    private String TAG = "060_Spo2TipActivity";
    private boolean is_hase_finger = false;
    private List<Integer> list = new ArrayList<>();
    private List<Integer> malv_list = new ArrayList<>();
    private List<Integer> spo_list = new ArrayList<>();
    private Dialog dialog;
    private TextView tv_f_status;
    private boolean start_count = false;
    private boolean has_deviece = false;
    private int count = 0;

    private TextView card_name;

    @Override
    protected View setView() {
        return LayoutInflater.from(this).inflate(R.layout.spo_tip, null);

    }

    @Override
    protected void setEvent() {
        if (dialog == null) {
            dialog = LoadingDialog.createLoadingDialog(Spo2TipActivity.this, "测量中....");
        }
        tv_f_status = findViewById(R.id.tv_f_status);
        card_name = findViewById(R.id.card_name);
        HeaderView headerView = findViewById(R.id.headerView);
        headerView.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_hase_finger) {
                    dialog.show();
                    timer.start();
                    start_count = true;
                } else {
                    if (!start_count) {
                        ToastUtils.toastShort("请插入手指");
                        tv_f_status.setText("请插入手指");
                    }
                }
            }
        });
        String MOBILE_PHONE = SpUtils.getValue(SpConstant.MOBILE_PHONE, "");
        if (!TextUtils.isEmpty(MOBILE_PHONE)) { // 用户模式
            card_name.setVisibility(View.VISIBLE);
            card_name.setText(MOBILE_PHONE);
        }

    }

    /**
     * 定时器
     */
    private CountDownTimer timer = new CountDownTimer(30000, 1000) {
        @Override
        public void onTick(long l) {
            LoadingDialog.getTipTextView().setText("倒计时：" + l / 1000 + "s");
            count = (int) l / 1000 - 25;
        }

        @Override
        public void onFinish() {
            int malv = getAverage(malv_list);
            int spo = getAverage(spo_list);
            Intent intent = new Intent(Spo2TipActivity.this, Spo2MeasureActivity.class);
            intent.putExtra("malv", malv);
            intent.putExtra("spo", spo);
            intent.putIntegerArrayListExtra("list", (ArrayList<Integer>) list);
            startActivity(intent);
            finish();
            mBOxygenManager.stop();
            Log.e("tag", "list  " + list.size());
        }
    };

    public int getAverage(List<Integer> list) {
        int sum = 0;

        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                sum += list.get(i);
            }
            return sum / list.size();
        }
        return 0;
    }

    @Override
    protected void getData() {
        SmartApplication mApplication = SmartApplication.getInstance();
        Log.v("spo", "mApplication is ： " + mApplication);
        //启动ReadThread。把当前application作为参数传入。
        mBOxygenManager.start(mApplication);
    }

    @Override
    public void sendResult(int resultMsgId, String resultInfo, int result) {
        Log.v(TAG, "sendPRResult resultMsgId is : " + resultMsgId + "; resultInfo is :" + resultInfo + "; result is :" + result);
        if (resultMsgId == 101) {

            if (result == 0) {   //插入手指
                is_hase_finger = true;
                mHandler.sendEmptyMessage(HAS_FINGER);
            }
            if (result == 1) {
                is_hase_finger = false;
                mHandler.sendEmptyMessage(NO_FINGER); //没检测到
            }
            if (result == 2) {  //拔出手指
                is_hase_finger = false;
                mHandler.sendEmptyMessage(FINGER_OUT);
            }
        } else if (resultMsgId == 102) { //  脉率
            if (start_count && result > 0 && result < 255) {
                malv_list.add(result);
            }
        } else if (resultMsgId == 103) {// 血氧
            if (start_count) {
                if (result != 0 && result < 100) {
                    spo_list.add(result);
                }
            }
        } else if (resultMsgId == 104) { //容积波
            if (start_count) {
                if (result != 0 && result < 127) { //过滤掉0
                    list.add(result);
                }
            }
        } else if (resultMsgId == 107) {
            if (result == 0) {
                has_deviece = true;
                mHandler.sendEmptyMessage(HAS_DEVICE);
            } else {
                has_deviece = false;
                mHandler.sendEmptyMessage(NO_DEVICE);
            }
        }
        mHandler.sendEmptyMessage(resultMsgId);
    }

    /**
     * UI显示结果
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NO_DEVICE:
                    if (!start_count) {
                        tv_f_status.setText("请插入探头");
                    }
                    break;
                case HAS_FINGER:
                    if (!start_count) {
                        tv_f_status.setText("手指插入，点击开始测量按钮");
                    }
                    break;
                case NO_FINGER:
                    if (!start_count) {
                        tv_f_status.setText("请插入手指");
                    }
                    break;
                /**
                 * 已经开始测试后，如果手指拔出就提示重新测
                 * */
                case FINGER_OUT:
                    if (start_count) {
                        mBOxygenManager.stop();
                        dialog.dismiss();
                        timer.cancel();
                        tv_f_status.setText("手指拔出请返回上一页，重新测量");
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
        }
        if (timer != null) {
            timer.cancel();
        }
        if (mBOxygenManager != null) {
            mBOxygenManager.stop();
        }
    }
}
