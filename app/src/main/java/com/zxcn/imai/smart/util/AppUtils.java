package com.zxcn.imai.smart.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ZXCN1 on 2017/9/13.
 */

public class AppUtils {
    //返回值 -1：没有网络  1：WIFI网络2：wap网络3：net网络
    public static int getNetType(Context context){
        int netType = -1;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo==null){
            return netType;
        }
        int nType = networkInfo.getType();
        if(nType==ConnectivityManager.TYPE_MOBILE)
        {
            if(networkInfo.getExtraInfo().toLowerCase().equals("cmnet")){
                netType = 3;
            } else {
                netType = 2;
            }
        } else if(nType==ConnectivityManager.TYPE_WIFI) {
            netType = 1;
        }
        return netType;
    }
}
