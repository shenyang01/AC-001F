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
import com.zxcn.imai.smart.base.SpConstant;

/**
 *
 * @author ZXCN1
 * @date 2017/9/25
 */

public class RoleChosePopup extends PopupWindow{


    private Context context;

    private View mPopView;

    private TextView grandpaTV;
    private TextView grandmaTV;
    private TextView fatherTV;
    private TextView matherTV;

    private RoleItemClickListener listener;

    public RoleChosePopup(Context context) {
        super(context);
        this.context = context;

        initView();
        setEvent();
        setPopup();
    }

    private void initView() {
        mPopView = LayoutInflater.from(context).inflate(R.layout.dialog_role_chose, null);
        grandpaTV = mPopView.findViewById(R.id.grandpaTV);
        grandmaTV = mPopView.findViewById(R.id.grandmaTV);
        fatherTV = mPopView.findViewById(R.id.fatherTV);
        matherTV = mPopView.findViewById(R.id.matherTV);


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
        grandpaTV.setOnClickListener(view -> {
            if (null != listener) {
                listener.onItemClickListener(AppConstant.USER_GRANDPA, grandpaTV.getText().toString());
            }
        });
        grandmaTV.setOnClickListener(view -> {
            if (null != listener) {
                listener.onItemClickListener(AppConstant.USER_GRANDMA, grandmaTV.getText().toString());
            }
        });
        fatherTV.setOnClickListener(view -> {
            if (null != listener) {
                listener.onItemClickListener(AppConstant.USER_FATHER, fatherTV.getText().toString());
            }
        });
        matherTV.setOnClickListener(view -> {
            if (null != listener) {
                listener.onItemClickListener(AppConstant.USER_MATHER, matherTV.getText().toString());
            }
        });
    }

    public void setOnItemClick(RoleItemClickListener roleItemClickListener){
        this.listener = roleItemClickListener;
    }

    public interface RoleItemClickListener{
        void onItemClickListener(String userType, String roleName);
    }
}
