package com.zxcn.imai.smart.activity.intr.present;

/**
 * Created by Administrator on 2016/5/5.
 */
public interface PresenterFactory<T extends Presenter> {
    T create();
}
