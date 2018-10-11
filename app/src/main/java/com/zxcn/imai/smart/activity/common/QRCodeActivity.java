package com.zxcn.imai.smart.activity.common;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.base.BaseActivity;
import com.zxcn.imai.smart.ui.HeaderView;
import com.zxcn.imai.smart.util.LogUtil;
import com.zxcn.imai.smart.zxing.utils.ZxingUtils;

import java.text.BreakIterator;

/**
 * @ data  2018/7/23 12:31
 * @ author  zxcg
 */
public class QRCodeActivity extends BaseActivity implements View.OnClickListener {

    private ImageView qr_code;

    @Override
    protected View setView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_qrcode, null);
    }

    @Override
    protected void setEvent() {
        qr_code = findViewById(R.id.QR_code);
        HeaderView headerView = findViewById(R.id.QR_headerView);
        headerView.setLeftClickListener(view -> finish());
        findViewById(R.id.sendPhone).setOnClickListener(this);
    }

    @Override
    protected void getData() {
        Intent intent = getIntent();
        try {
            if (intent != null) {
                String name = intent.getStringExtra("QR_name");
                String cardName = intent.getStringExtra("QR_phone");
                String phoneNumber = intent.getStringExtra("QR_card");
                Log.e("tag", name + cardName + phoneNumber);
                Bitmap bitmap = ZxingUtils.createBitmap(name.concat(":").concat(cardName)
                        .concat(":")
                        .concat(phoneNumber));
                qr_code.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendPhone:
                break;
        }
    }
}
