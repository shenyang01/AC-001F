package com.zxcn.imai.smart.activity.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.cjt2325.cameralibrary.JCameraView;
import com.cjt2325.cameralibrary.lisenter.ErrorLisenter;
import com.cjt2325.cameralibrary.lisenter.JCameraLisenter;
import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.base.SmartApplication;
import com.zxcn.imai.smart.util.PhotoUtils;

/**
 * Created by ZXCN1 on 2017/9/28.
 */

public class CameraActivity extends FragmentActivity {

    private JCameraView jCameraView;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SmartApplication.getInstance().addAcitivity(this);
        initDecorView();
        setContentView(R.layout.activity_camera);
        path = getIntent().getStringExtra("path");
        jCameraView = findViewById(R.id.jcameraview);
        jCameraView.setFeatures(JCameraView.BUTTON_STATE_ONLY_CAPTURE);

        setEvent();
    }

    private void initDecorView() {
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }

    private void setEvent(){
        //JCameraView监听
        jCameraView.setErrorLisenter(new ErrorLisenter() {
            @Override
            public void onError() {
                //打开Camera失败回调
                Log.i("CJT", "open camera error");
            }
            @Override
            public void AudioPermissionError() {
                //没有录取权限回调
                Log.i("CJT", "AudioPermissionError");
            }
        });

        jCameraView.setJCameraLisenter(new JCameraLisenter() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                if (PhotoUtils.saveBitmapAsFile(bitmap, path)){
                    setResult(RESULT_OK);
                    finish();
                }
                //获取图片bitmap
                Log.i("JCameraView", "bitmap = " + bitmap.getWidth());
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {

            }
            @Override
            public void quit() {
                //退出按钮
                CameraActivity.this.finish();
            }
        });
    }

    public static void newActivity(Context context){
        context.startActivity(new Intent(context, CameraActivity.class));
    }

    public static void newActivity(Activity context, String path){
        Intent intent = new Intent(context, CameraActivity.class);
        intent.putExtra("path", path);
        context.startActivityForResult(intent, 1);
    }
}
