package com.zxcn.imai.smart.core.finger;

/**
 * Created by ZXCN1 on 2017/9/9.
 */

public interface FingerOpenListenerBak {
    void openDeviceSuccess(int var1);

    void openDeviceFail(int var1);

    void usbFail(int var1);
}
