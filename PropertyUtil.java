package com.welink.worker.utils;

import android.content.Context;
import android.util.Log;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by liuchao on 16/9/27.
 */
public class PropertyUtil {

    public static String getStringProperties(Context context,String key) {
        String myAddress;
        Properties pro = new Properties();
        InputStream is = null;
        try {
            is = context.getAssets().open("config.properties");
            pro.load(is);
            myAddress = pro.getProperty(key);
            return myAddress;
        } catch (Exception e) {
            LogUtil.e("errorInfo:" + "在PropertyUtil中数据发生异常-异常信息：" + e.getMessage());
            return "";
        }
    }

    public static boolean getBooleanProperties(Context context,String key) {
        boolean param;
        Properties pro = new Properties();
        InputStream is = null;
        try {
            is = context.getAssets().open("config.properties");
            pro.load(is);
            param = Boolean.parseBoolean(pro.getProperty(key));
            return param;
        } catch (Exception e) {
            LogUtil.e("errorInfo:" + "在PropertyUtil中数据发生异常-异常信息：" + e.getMessage());
            return true;
        }
    }
}
