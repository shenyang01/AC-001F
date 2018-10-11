package com.zxcn.imai.smart.util;

import android.content.Context;
import android.widget.Toast;

import com.zxcn.finger.FingerOpenListener;
import com.zxcn.imai.smart.base.SmartApplication;

/**
 * Created by ZXCN1 on 2017/9/4.
 */

public class ToastUtils {
    private static Toast toast= null;

    public static void toastLong(Context context, String toast){
        Toast.makeText(context, toast, Toast.LENGTH_LONG).show();
    }

    public static void toastLong(Context context, int stringId){
        toastLong(context, context.getString(stringId));
    }

    public static void toastShort(Context context, int toast) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }
    public static void toastShort(Context context, String string) {
        if(toast== null){
            toast = Toast.makeText(context,string,Toast.LENGTH_SHORT);
            toast.show();
        }else {
            toast.setText(string);
            toast.show();
        }
    }


    public static void toastShort( String toast) {
        Toast.makeText(SmartApplication.getContext(), toast, Toast.LENGTH_SHORT).show();
    }
}
