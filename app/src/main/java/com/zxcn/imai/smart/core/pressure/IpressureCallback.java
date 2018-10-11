package com.zxcn.imai.smart.core.pressure;

import android.os.Bundle;

/**
 * Created by qqq on 2017/8/15.
 */

public interface IpressureCallback {

    void onMeasure(Bundle info);
    void onEnd(Bundle info);

    void onSuccess(int sucMsgId, CharSequence successString);
    void onFail(int failMsgId, CharSequence failString);
    void onHelp(int helpMsgId, CharSequence helpString);
}
