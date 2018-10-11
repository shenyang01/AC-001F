package com.zxcn.imai.smart.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZXCN1 on 2017/8/8.
 */

public class RegardsUtil {

    public static boolean phoneValid(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        Pattern pattern = Pattern.compile("1[0-9]{10}");
        Matcher m = pattern.matcher(phone);
        return m.matches();
    }

}
