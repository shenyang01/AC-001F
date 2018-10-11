package com.zxcn.imai.smart.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zxcn.imai.smart.R;

/**
 * Created by ZXCN1 on 2017/8/16.
 */

public class GetPicDialog extends AlertDialog{

    private View.OnClickListener onCameraClick;
    private View.OnClickListener onPicClick;

    public GetPicDialog(Context context) {
        super(context);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_get_pic);
        TextView cameraTV = findViewById(R.id.cameraTV);
        TextView pictureTV = findViewById(R.id.pictureTV);

        cameraTV.setOnClickListener(view -> {
            dismiss();
            if(null != onCameraClick) {
                onCameraClick.onClick(view);
            }
        });
        pictureTV.setOnClickListener(view -> {
            dismiss();
            if(null != onPicClick){
                onPicClick.onClick(view);
            }
        });
        setCancelable(true);
    }

    public void setOnCameraClick(View.OnClickListener onCameraClick) {
        this.onCameraClick = onCameraClick;
    }

    public void setOnPicClick(View.OnClickListener onPicClick) {
        this.onPicClick = onPicClick;
    }
}
