package com.zxcn.imai.smart.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.util.SizeUtils;

/**
 * Created by ZXCN1 on 2017/8/9.
 */

public class BpBoardView extends View {

    private float radius; //表盘内圆半径
    private int record;     //表盘最大值

    private int now;        //表盘现在所在的刻度

    private int degreeColor;    //刻度初始颜色
    private int degreePointerColor;    //刻度走过的颜色    高压

    private int defaultRecord = 300;   //表盘最大值默认260
    private int defaultInPointerSize = 30;  //默认普通刻度长度
    private int defaultOutPointerSize = 42; //默认准点刻度长度

    private Paint mPaint; //画笔

    private CountDownTimer timer;

    private static final int TIME_ALL = 30000;      //表盘走完一圈的总时间
    private static final int TIME_INTERVAL = 500;   //绘制表盘间隔时间


    private int recordSecond;

    private boolean isRun = false;


    public BpBoardView(Context context) {
        this(context, null);
    }

    public BpBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //获取自定义的属性
        obtainStyledAttrs(attrs);
        //初始化画笔
        initPaint();
        //初始化Timer
        initTimer();
    }

    private void obtainStyledAttrs(AttributeSet attrs) {
        TypedArray array = null;
        try {
            array = getContext().obtainStyledAttributes(attrs, R.styleable.BpBoard);
            radius = array.getDimension(R.styleable.BpBoard_bb_radius, SizeUtils.dip2px(getContext(), 76));
            record = array.getInteger(R.styleable.BpBoard_bb_record, defaultRecord);

            degreeColor = array.getColor(R.styleable.BpBoard_bb_degree_color, Color.WHITE);
            degreePointerColor = array.getColor(R.styleable.BpBoard_bb_degree_pointer_color, getContext().getResources().getColor(R.color.point));

        } catch (Exception e) {
            radius = SizeUtils.dip2px(getContext(), 76);
            record = defaultRecord;
            degreeColor = Color.WHITE;
            degreePointerColor = Color.YELLOW;
        } finally {
            if (null != array) {
                array.recycle();
            }
        }
    }

    //画笔初始化
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
    }

    int step = 0;

    private void initTimer() {
        recordSecond = record * TIME_INTERVAL / TIME_ALL;
        timer = new CountDownTimer(TIME_ALL, TIME_INTERVAL) {
            @Override
            public void onTick(long l) {
                now = recordSecond * step;
                if (now <= record) {
                    step++;
                    postInvalidate();
                }
            }

            @Override
            public void onFinish() {
                isRun = false;
            }
        };
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);
        //绘制刻度
        paintScale(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = 1000; //设定一个最小值


        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);


        if ((widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) && (heightMeasureSpec == MeasureSpec.AT_MOST || heightMeasureSpec == MeasureSpec.UNSPECIFIED)) {
//            try {
//                throw new NoDetermineSizeException("宽度高度至少有一个确定的值,不能同时为wrap_content");
//            } catch (NoDetermineSizeException e) {
//                e.printStackTrace();
//            }
        } else { //至少有一个为确定值,要获取其中的最小值
            if (widthMode == MeasureSpec.EXACTLY) {
                width = Math.min(widthSize, width);
            }
            if (heightMode == MeasureSpec.EXACTLY) {
                width = Math.min(heightSize, width);
            }
        }

        setMeasuredDimension(width, width);
    }

    //绘制刻度
    private void paintScale(Canvas canvas) {
        mPaint.setStrokeWidth(SizeUtils.dip2px(getContext(), 1));
        int lineWidth = 0;
        for (int i = 0; i < 60; i++) {
            if (i % 15 == 0) { //整点
                mPaint.setStrokeWidth(SizeUtils.dip2px(getContext(), 2f));
                lineWidth = defaultOutPointerSize;
            } else { //非整点
                lineWidth = defaultInPointerSize;
                mPaint.setStrokeWidth(SizeUtils.dip2px(getContext(), 1));
            }
            if ((float) i / 60 < (float) now / record) {
                mPaint.setColor(degreePointerColor);
            } else {
                //说明血压未到这个刻度，颜色为初始颜色
                mPaint.setColor(degreeColor);
            }
            canvas.drawLine(0, -radius + SizeUtils.dip2px(getContext(), 10), 0, -radius + SizeUtils.dip2px(getContext(), 10) - lineWidth, mPaint);
            canvas.rotate(6);
        }
        canvas.restore();
    }

    public void start() {
        if (null != timer && !isRun) {
            step = 0;
            isRun = true;
            timer.start();
        }
    }

    public void stop() {
        if (null != timer && isRun) {
            isRun = false;
            timer.cancel();
        }
    }
}
