package com.zxcn.imai.smart.activity.pressure;

import android.os.Bundle;

/**
 * IECGCallback interface
 *
 * @author huangyuan
 * @data 2017/12/20
 */
public interface IBPCallback {
    void onHelp(int helpMsgId, String helpString);
    void onMeasure(Bundle info);
    void onEnd(Bundle info);

    /**
     * test
     */
    void onTestMeasure(int i, String msg, int info);
}
