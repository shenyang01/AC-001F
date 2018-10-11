package com.zxcn.imai.smart.activity.common;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.base.BaseActivity;
import com.zxcn.imai.smart.ui.HeaderView;
import com.zxcn.imai.smart.util.SpUtils;
import com.zxcn.imai.smart.util.ToastUtils;
import com.zxcn.imai.smart.zxing.activity.CaptureActivity;

import java.util.Hashtable;

/**
 * @author ZXCN1
 * @date 2017/9/1
 */
public class ErweimaActivity extends BaseActivity {
    String phone;
    String id;
    private String content_shenfen;

    HeaderView headerView;
    ImageView merweima;
    Button mok;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected View setView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        return LayoutInflater.from(this).inflate(R.layout.activity_erweima, null);
    }

    @Override
    protected void setEvent() {
        headerView = findViewById(R.id.headerView);
//        headerView.setLeftClickListener(view -> finish());
        headerView.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                MainActivity.newActivity(getApplicationContext());
            }
        });
        merweima = findViewById(R.id.merweima);
        mok = findViewById(R.id.btn_ok);
        mok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                MainActivity.newActivity(getApplicationContext());
            }
        });
    }

    @Override
    protected void getData() {
        Intent i = getIntent();
        phone = i.getStringExtra("phone");
        String card_name = i.getStringExtra("card_name");
        SpUtils.saveBoolean("card_data", true);
        SpUtils.saveString("card_name", card_name);
        content_shenfen =  "{"+"\"userid\"" + ":" + "\"" + phone + "\""+"}";
        Log.e("060_ErweimaActivity", "content_shenfen : " + content_shenfen);


        try {
            Bitmap mBitmap = Create2DCode(content_shenfen);
            merweima.setImageBitmap(mBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private Bitmap Create2DCode(String str) throws WriterException {
        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 400, 400, hints);

        int width = matrix.getWidth();
        int height = matrix.getHeight();
        // 二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //UI显示结果
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
