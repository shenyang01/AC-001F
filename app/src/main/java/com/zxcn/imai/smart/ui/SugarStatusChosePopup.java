package com.zxcn.imai.smart.ui;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zxcn.imai.smart.R;
import com.zxcn.imai.smart.base.AppConstant;

/**
 * @author ZXCN1
 * @date 2017/9/25
 */

public class SugarStatusChosePopup extends PopupWindow {

    private Context context;

    private View mPopView;

    private TextView launch_beffor;
    private TextView launch_after;


    private RoleItemClickListener listener;

    public SugarStatusChosePopup(Context context) {
        super(context);
        this.context = context;

        initView();
        setEvent();
        setPopup();
    }

    private void initView() {
        mPopView = LayoutInflater.from(context).inflate(R.layout.dialog_sugar_chose, null);
        launch_beffor = mPopView.findViewById(R.id.launch_beffor);
        launch_after = mPopView.findViewById(R.id.launch_after);
    }

    private void setPopup() {

        this.setContentView(mPopView);// 设置View
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);// 设置弹出窗口的宽
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);// 设置弹出窗口的高
        this.setFocusable(true);// 设置弹出窗口可
        this.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transport)));// 设置背景透明
        mPopView.setOnTouchListener(new View.OnTouchListener() {// 如果触摸位置在窗口外面则销毁

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                int height = mPopView.getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    private void setEvent() {
        launch_beffor.setOnClickListener(view -> {
            if (null != listener) {
                listener.onItemClickListener(launch_beffor.getText().toString());
            }
        });
        launch_after.setOnClickListener(view -> {
            if (null != listener) {
                listener.onItemClickListener(launch_after.getText().toString());
            }
        });

    }

    public void setOnItemClick(RoleItemClickListener roleItemClickListener) {
        this.listener = roleItemClickListener;
    }

    public interface RoleItemClickListener {
        void onItemClickListener(String roleName);
    }
}
