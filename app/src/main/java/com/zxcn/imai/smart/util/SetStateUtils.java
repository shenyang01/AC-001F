package com.zxcn.imai.smart.util;

import com.zxcn.imai.smart.base.BaseActivity;

/**
 * @ data  2018/5/8 11:16
 *
 */
public class SetStateUtils {
    private BaseActivity currentActivity; //  当前所处界面
    private boolean BtState;  //当前充电状态
    private SetStateUtils(){

    }
    /**
     *   单例 静态内部类
     */
    private static class SingletonHolder{
         static SetStateUtils instance = new SetStateUtils();
    }
    public static SetStateUtils newInstance(){
        return SingletonHolder.instance;
    }


    public BaseActivity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(BaseActivity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public boolean getBtState() {
        return BtState;
    }

    public void setBtState(boolean btState) {
        BtState = btState;
    }
}
