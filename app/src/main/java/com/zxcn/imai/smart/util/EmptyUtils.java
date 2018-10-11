package com.zxcn.imai.smart.util;

import java.util.List;

/**
 * Created by ZXCN1 on 2017/9/14.
 */

public class EmptyUtils {

    public static boolean listIsEmpty(List<?> data){
        if (null == data || data.size() == 0){
            return true;
        }  else {
            return false;
        }
    }
}
