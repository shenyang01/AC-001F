package com.zxcn.imai.smart.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by ZXCN1 on 2017/8/10.
 */

public class DateUtils {
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    /**
     * 根据时间戳获取格式化日期
     * @param longTime
     * @return
     */
    public static String getDateStr(String longTime){
            long time = Long.parseLong(longTime);
            return getDateStr(time);
    }

    public static String getDateStr(long time){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
            Date date = new Date(time);
            return sdf.format(date);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取一周前的今天零点时间
     * @return
     */
    public static long getLastWeekTime() {
        return getBeforeDayTime(7);
//        long current = System.currentTimeMillis();
//        long zero=current/(1000*3600*24)*(1000*3600*24)- TimeZone.getDefault().getRawOffset();
//        long weekTime = 7*24*60*60*1000;
//
//        Date lastWeekDate = new Date(zero - weekTime);
//
//        return lastWeekDate.getTime();
    }

    /**
     * 获取某天零点时间
     * @param day
     * @return
     */
    public static long getBeforeDayTime(int day){
        long current = System.currentTimeMillis();
        //今天零点
        long zero=current/(1000*3600*24)*(1000*3600*24)- TimeZone.getDefault().getRawOffset();
        long weekTime = 24*60*60*1000*day;

        Date lastWeekDate = new Date(zero - weekTime);

        return lastWeekDate.getTime();
    }
}
