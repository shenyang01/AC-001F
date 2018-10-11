/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zxcn.imai.smart.zxing.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.zxing.Result;
import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.activity.common.FunctionChoseActivity;
import com.zxcn.imai.smart.activity.pressure.MeasureTipActivity;
import com.zxcn.imai.smart.base.BaseActivity;
import com.zxcn.imai.smart.base.SpConstant;
import com.zxcn.imai.smart.ui.HeaderView;
import com.zxcn.imai.smart.util.SpUtils;
import com.zxcn.imai.smart.util.ToastUtils;
import com.zxcn.imai.smart.zxing.camera.CameraManager;
import com.zxcn.imai.smart.zxing.decode.DecodeThread;
import com.zxcn.imai.smart.zxing.utils.BeepManager;
import com.zxcn.imai.smart.zxing.utils.CaptureActivityHandler;
import com.zxcn.imai.smart.zxing.utils.InactivityTimer;

import java.lang.reflect.Field;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 二维码扫描界面
 */
public final class CaptureActivity extends BaseActivity implements SurfaceHolder.Callback {

    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;
    private SurfaceView scanPreview = null;
    private RelativeLayout scanContainer;
    private RelativeLayout scanCropView;
    private Rect mCropRect = null;
    private boolean isHasSurface = false;
    private ScheduledExecutorService executorService = null; //超时返回主界面
    private boolean first;
    private HeaderView headView;


    @Override
    protected View setView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_capture, null);
    }

    @Override
    protected void setEvent() {
        headView = findViewById(R.id.headView);
        scanPreview = findViewById(R.id.capture_preview);
        scanContainer = findViewById(R.id.capture_container);
        scanCropView = findViewById(R.id.capture_crop_view);
        ImageView scanLine = findViewById(R.id.capture_scan_line);
        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.9f);
        animation.setDuration(4500);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        scanLine.startAnimation(animation);
        checkBluetoothPermission();
        headView.getLeftTV().setOnClickListener(view -> finish());
    }

    @Override
    protected void getData() {

    }


    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }


    @Override
    protected void onResume() {
        super.onResume();
        inactivityTimer.onResume();
        if (first && cameraManager == null) {
            cameraManager = new CameraManager(getApplication());
            initCamera(scanPreview.getHolder());
        }
        executorService = Executors.newSingleThreadScheduledExecutor();
        Log.e("tag", "onResume()");
        overTime();
        first = true;
    }

    private void checkBluetoothPermission() {
        //Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(CaptureActivity.this,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.e("test", "CAMERA  " + " 权限请求中  " + Build.VERSION.SDK_INT);
                ActivityCompat.requestPermissions(CaptureActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        123);
            } else {
                cameraManager = new CameraManager(getApplication());
                Log.e("tag", " 有权限  ");
                handler = null;
                if (isHasSurface) {
                    initCamera(scanPreview.getHolder());
                } else {
                    scanPreview.getHolder().addCallback(this);
                }
            }
        }
    }

    /**
     * 权限请求完成
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("tag", " 权限请求完成  ");
        cameraManager = new CameraManager(getApplication());
        handler = null;
        if (!isHasSurface) {
            initCamera(scanPreview.getHolder());
        } else {
            scanPreview.getHolder().addCallback(this);
        }
    }

    public void setHeadView(String info) {
        headView.setTitle(info);
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        beepManager.close();
        if (cameraManager != null) {
            cameraManager.closeDriver();
            cameraManager = null;
        }
        if (!isHasSurface) {
            scanPreview.getHolder().removeCallback(this);
        }
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        first = false;
        scanPreview.getHolder().removeCallback(this);
        scanPreview = null;
        if (executorService != null) {
            executorService.shutdownNow();
            executorService = null;
        }
        inactivityTimer.shutdown();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
        }
        if (!isHasSurface) {
            isHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isHasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * 解析获取扫码后的数据
     */
    public void handleDecode(Result rawResult, Bundle bundle) {
        inactivityTimer.onActivity();
        executorService.shutdownNow();
        beepManager.playBeepSoundAndVibrate();
        String info = rawResult.getText();
        Log.e("060_CaptureActivity", info);

        if (info.contains("userid")) { //验证身份证格式
            try {
                Intent intent = new Intent();
                String string = info.split(":")[1];
                SpUtils.saveString(SpConstant.MOBILE_PHONE, info.split(":")[1].substring(1, 12));

                intent.setClass(this, FunctionChoseActivity.class);
                startActivity(intent);
                Log.e("060_zxing userid", string.substring(0, 11)+" string  " +
                        ""+string.substring(11));
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            ToastUtils.toastShort(this, getString(R.string.scanningErr));
        }
        finish();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            if (handler == null) {
                handler = new CaptureActivityHandler(CaptureActivity.this, cameraManager,
                        DecodeThread
                                .QRCODE_MODE); //QR码模式
            }
            initCrop();
        } catch (Exception e) {
            Log.e("wifi", "相机异常" + e.getMessage());
            e.printStackTrace();
            displayFrameworkBugMessageAndExit();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void displayFrameworkBugMessageAndExit() {
        ToastUtils.toastShort(getApplicationContext(), getString(R.string.cameraMessage));
        cameraManager.closeDriver();
        finish();

    }


    public Rect getCropRect() {
        return mCropRect;
    }

    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        int cameraWidth = cameraManager.getCameraResolution().y;
        int cameraHeight = cameraManager.getCameraResolution().x;
        Log.e("tag", "initCrop()  " + cameraWidth + "   " + cameraHeight);

        /* 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();

        /* 获取布局容器的宽高 */
        int containerWidth = scanContainer.getWidth();
        int containerHeight = scanContainer.getHeight();

        /* 计算最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * cameraWidth / containerWidth;
        /* 计算最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * cameraHeight / containerHeight;

        /* 计算最终截取的矩形的宽度 */
        int width = cropWidth * cameraWidth / containerWidth;
        /* 计算最终截取的矩形的高度 */
        int height = cropHeight * cameraHeight / containerHeight;

        /* 生成最终的截取的矩形 */
        mCropRect = new Rect(x, y, width + x, height + y);
        Log.e("tag", " mCropRect  " + mCropRect.toString());
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 扫码超时处理 30秒  返回主界面
     */
    public void overTime() {
        executorService.schedule(() -> {
            // ToastUtils.toastShort(getString(R.string.overTime));
            CaptureActivity.this.finish();
        }, 30, TimeUnit.SECONDS);
    }

}