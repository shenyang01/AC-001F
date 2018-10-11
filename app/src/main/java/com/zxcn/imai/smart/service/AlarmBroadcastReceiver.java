package com.zxcn.imai.smart.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.zxcn.imai.smart.base.SpConstant;

import com.zxcn.imai.smart.db.DbUtils;
import com.zxcn.imai.smart.db.module.SmartBean;
import com.zxcn.imai.smart.util.EmptyUtils;
import com.zxcn.imai.smart.util.SpUtils;

import java.util.List;

/**
 * Created by ZXCN1 on 2017/9/14.
 */

public class AlarmBroadcastReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, "同步", Toast.LENGTH_SHORT).show();
    }

    private void synchronizeData(){
        List<SmartBean> smartBeanList = DbUtils.getUploadData();
        if (!EmptyUtils.listIsEmpty(smartBeanList)) {
//            RetrofitFactory.getInstance().upLoadData(smartBeanList, success -> {
//                //上传成功，保存本次上传的时间
//                SpUtils.saveString(SpConstant.UPLOAD_TIME, String.valueOf(System.currentTimeMillis()));
//            } , error -> {
//
//            });
        }
    }
}
