package com.zxcn.imai.smart.activity.common;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zxcn.imai.smart.R;

import java.util.List;

/**
 * @version V1.0
 * @Package com.zxcn.imai.smart.activity.common
 * @Description: ${todo}
 * @author: huangyuan
 * @date: 2018/7/2 10:25
 * @Copyright: www.***.com Inc. All rights reserved.
 */
public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.ViewHolder> {

    String tag = "060_GalleryAdapter";

    private LayoutInflater mInflater;
    private List<String> mDatas;

    public SettingAdapter(Context context, List<String> datats) {
        mInflater = LayoutInflater.from(context);
        mDatas = datats;
    }

    /**
     * 创建ViewHolder，每个ViewHolder管理一个item。因为ViewHolder会重复利用，
     * 所以如果Recyclerview有12个item，可能只创建7个ViewHolder
     */
    int count_create = 0;
    int count_bind = 0;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        count_create++;
        Log.e(tag, "onCreateViewHolder 被调用了 : " + count_create + "次");

        View view = mInflater.inflate(R.layout.item_setting, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.mTxt = (TextView) view.findViewById(R.id.id_index_gallery_item_text);
        return viewHolder;
    }


    /**
     * 绑定ViewHolder到Recyclerview
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        count_bind++;
        Log.e(tag, "onBindViewHolder 被调用了 : " + count_bind + "次");

        viewHolder.mTxt.setText(mDatas.get(i) + "");


        //设置点击事件对应的回调
        if (mOnItemClickLitener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickLitener.onItemClick(viewHolder.itemView, i);
                }
            });

        }
    }

    /**
     * ************************* 定义ViewHolder ******************************
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View arg0) {
            super(arg0);
        }
        TextView mTxt;

    }

    /**
     * *************************  重写Recyclerview Adapter的接口 ******************************
     */
    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    /**
     * *************************  Recyclerview item对应点击事件******************************
     */

    /**
     * RecyclerView Item 的点击事件回调接口
     */
    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    /**
     * RecyclerView Item 的滚动事件回调接口
     */
    public interface OnScrollListener {
        // 状态为0时：当前屏幕停止滚动；
        // 状态为1时：屏幕在滚动 且 用户仍在触碰或手指还在屏幕上；
        // 状态为2时：随用户的操作，屏幕上产生的惯性滑动；
        //        void onScrollStateChanged(RecyclerView recyclerView, int newState);
        void onScrolled(RecyclerView recyclerView, int dx, int dy);
    }

    private OnScrollListener mOnScrollListener;

    public void setOnScrollListener(OnScrollListener mOnScrollListener) {
        this.mOnScrollListener = mOnScrollListener;
    }

}
