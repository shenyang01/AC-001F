package com.zxcn.imai.smart.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zxcn.imai.smart.R;

/**
 * Created by ZXCN1 on 2017/8/4.
 */

public class HeaderView extends RelativeLayout {

    private TextView leftTV;
    private TextView titleTV;


    private String titleText;
    private int textColor;

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_header,this,true);

        TypedArray a=context.obtainStyledAttributes(attrs, R.styleable.header);

        titleText = a.getString(R.styleable.header_titleText);
        textColor = a.getColor(R.styleable.header_textColor, Color.WHITE);

        a.recycle();
    }


    public TextView getLeftTV() {
        return leftTV;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        leftTV = findViewById(R.id.leftTV);
        titleTV = findViewById(R.id.titleTV);

        if (!TextUtils.isEmpty(titleText)) {
            titleTV.setText(titleText);
        }
        titleTV.setTextColor(textColor);
        leftTV.setTextColor(textColor);
    }


    public void setLeftClickListener(OnClickListener listener) {
        leftTV.setOnClickListener(view -> {
            if (null != listener) {
                listener.onClick(view);
            }
        });
    }

    public void setTitle(int resID) {
        titleTV.setText(resID);
    }

    public void setTitle(String title) {
        titleTV.setText(title);
    }

    public void hideLeft() {
        leftTV.setVisibility(View.GONE);
    }
    public void setLeftTV(String resource){
        leftTV.setText(resource);
    }

}
