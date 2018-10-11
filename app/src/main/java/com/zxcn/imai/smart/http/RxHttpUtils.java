package com.zxcn.imai.smart.http;

/**
 * Created by ${wangwen} on 2016/11/1 10:18.
 */

public class RxHttpUtils {
    public static void requestPost(BaseEntity baseEntity){
        HttpManager.getInstance().doHttpDeal(baseEntity);
    }
    public static String getToken(){
        return "9749f186-de37-4907-aaff-2446b78dc4db";
    }
}
