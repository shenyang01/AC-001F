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

public class TipDialog extends AlertDialog{

    private String content;
    private TextView contentTV;

    private View.OnClickListener onClickListener;

    public TipDialog(Context context, int contentID) {
        this(context);
        content = context.getString(contentID);
    }

    protected TipDialog(Context context) {
        super(context);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_fp);
        contentTV = findViewById(R.id.contentTV);
        TextView backTV = findViewById(R.id.backTV);

        contentTV.setText(content);

        backTV.setOnClickListener(view -> {
            dismiss();
            if(null != onClickListener) {
                onClickListener.onClick(view);
            }
        });
        setCancelable(true);
    }

    public void setContent(String str){
        if (null != contentTV) {
            contentTV.setText(str);
        }
    }

    public void setContent(int resID){
        if (null != contentTV) {
            contentTV.setText(resID);
        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
