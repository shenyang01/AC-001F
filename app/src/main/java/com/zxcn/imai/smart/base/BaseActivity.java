package com.zxcn.imai.smart.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.bugtags.library.Bugtags;
import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.ui.StatusBarUtils;
import com.zxcn.imai.smart.util.NetUtil;
import com.zxcn.imai.smart.util.SetStateUtils;

/**
 * Created by ZXCN1 on 2017/8/3.
 */

public abstract class BaseActivity extends FragmentActivity {
    public final int LOADER_ID = 1;
    /**
     * 网络类型
     */
    private int netMobile;
//    private NetBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);
        LinearLayout base_ll = findViewById(R.id.base_ll);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        base_ll.addView(setView(),layoutParams);
        StatusBarUtils.setColor(this, getResources().getColor(R.color.bg_title_bar));
        setEvent();
        getData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bugtags.onResume(this);
        SetStateUtils.newInstance().setCurrentActivity(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        Bugtags.onPause(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //注：回调 3
        Bugtags.onDispatchTouchEvent(this, event);
        return SetStateUtils.newInstance().getBtState() || super.dispatchTouchEvent(event);
    }

    protected abstract View setView();

    protected abstract void setEvent();

    protected abstract void getData();

    public void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.showSoftInput(view,InputMethodManager.SHOW_FORCED);
        if(imm!=null)
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN); //强制隐藏键盘
    }

    /**
     * 判断有无网络 。
     *
     * @return true 有网, false 没有网络.
     */
    public boolean isNetConnect() {
        if (netMobile == 1) {
            return true;
        } else if (netMobile == 0) {
            return true;
        } else if (netMobile == -1) {
            return false;
        }
        return false;
    }

    /**
     * 初始化时判断有没有网络
     */

    public boolean inspectNet() {
        this.netMobile = NetUtil.getNetWorkState(BaseActivity.this);
        return isNetConnect();
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
