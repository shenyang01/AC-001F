package com.zxcn.imai.smart.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.util.SizeUtils;

/**
 * Created by ZXCN1 on 2017/9/5.
 */

public class RoundImageView extends android.support.v7.widget.AppCompatImageView{
    private float mWidth, mHeight;
    private float mCorner;

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        obtainStyledAttrs(attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (Build.VERSION.SDK_INT < 18) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    private void obtainStyledAttrs(AttributeSet attrs) {
        TypedArray array = null;
        try {
            array = getContext().obtainStyledAttributes(attrs, R.styleable.RoundImage);
            mCorner = array.getDimension(R.styleable.RoundImage_corner, SizeUtils.dip2px(getContext(), 2));

        } catch (Exception e) {
            mCorner = SizeUtils.dip2px(getContext(), 2);
        }finally {
            if (null != array) {
                array.recycle();
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mWidth > mCorner && mHeight > mCorner) {
            Path path = new Path();
            path.moveTo(mCorner, 0);
            path.lineTo(mWidth - mCorner, 0);
            path.quadTo(mWidth, 0, mWidth, mCorner);
            path.lineTo(mWidth, mHeight - mCorner);
            path.quadTo(mWidth, mHeight, mWidth - mCorner, mHeight);
            path.lineTo(mCorner, mHeight);
            path.quadTo(0, mHeight, 0, mHeight - mCorner);
            path.lineTo(0, mCorner);
            path.quadTo(0, 0, mCorner, 0);
            canvas.clipPath(path);
        }

        super.onDraw(canvas);
    }
}
