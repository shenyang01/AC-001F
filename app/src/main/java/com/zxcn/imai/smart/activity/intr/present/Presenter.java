package com.zxcn.imai.smart.activity.intr.present;

import android.view.View;

/**
 * Created by Administrator on 2016/5/5.
 */
public interface Presenter {
    void onViewAttached(View view);
    void onViewDetached();
    void onDestroyed();
}
