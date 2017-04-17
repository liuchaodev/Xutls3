package com.welink.worker.utils;

import com.baidu.android.pushservice.PushManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by liuchao on 16/9/27.
 */
public class TimeUtil {

    private final static String DATE_FORMAT_HH_MM_SS = "HH:mm";
    private final static String DATE_FORMAT_YY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    private final static String DATE_FORMAT_YY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    //各个接口地址放到此处统一管理

    public static void main(String args[]) {

        System.out.println("利用日历获取时间：" + Calendar.getInstance().getTimeInMillis());
        System.out.println("当前时间戳：" + System.currentTimeMillis());
        System.out.println(getTodayOrYesterday("1476956091"));
        System.out.print((new Date()).getHours() + "  " + ((new Date()).getHours() + 24) % 24);
        isOverTime(5, "1476962397988", "1476962397939");
        //PushManager.setNoDisturbMode(this, (new Date()).getHours(), (new Date()).getMinutes(), 24, 59);

    }

    public static String getTodayOrYesterday(String date) {//date 是存储的时间戳
        long timeMills = 0;
        String strDate = "";
        if (null!=date) {
            try {
                if (date.length() == 10) {
                    timeMills = Long.parseLong(date) * 1000;
                } else {
                    timeMills = Long.parseLong(date);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //所在时区时8，系统初始时间是1970-01-01 80:00:00，注意是从八点开始，计算的时候要加回去
            try {
                int offSet = Calendar.getInstance().getTimeZone().getRawOffset();
                long today = (System.currentTimeMillis() + offSet) / 86400000;
                long start = (timeMills + offSet) / 86400000;
                long intervalTime = start - today;
                //-2:前天,-1：昨天,0：今天,1：明天,2：后天
                if (intervalTime == 0) {
                    strDate = "今天 " + timeStampToDate(timeMills, DATE_FORMAT_HH_MM_SS);
                } else if (intervalTime == -1) {
                    strDate = "昨天 " + timeStampToDate(timeMills, DATE_FORMAT_HH_MM_SS);
                } else {
                    //时间太久 昨天今天无法表示
                    strDate = timeStampToDate(timeMills, DATE_FORMAT_YY_MM_DD_HH_MM_SS);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }else {
            return "30分钟内上门";
        }
        return strDate;
    }


    public static String timeStampToDate(long mills, String dateFormat) {
        long timeMills;
        if (String.valueOf(mills).length() == 10) {
            timeMills = mills * 1000;
        } else {
            timeMills = mills;
        }
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Date date = new Date(timeMills);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static boolean isOverTime(int period, String currentTime, String orderTime) {
        long currentTimeMills = 0;
        long orderTimeMills = 0;
        try {
            currentTimeMills = Long.parseLong(currentTime);
            orderTimeMills = Long.parseLong(orderTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (currentTimeMills - orderTimeMills > period * 60) {
            System.out.println("时间差：" + (currentTimeMills - orderTimeMills));
            System.out.println("超过了" + period + "分钟");
            return true;
        }

        return false;
    }

    public static String getCurrentFormatDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_YY_MM_DD_HH_MM_SS);
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

}
