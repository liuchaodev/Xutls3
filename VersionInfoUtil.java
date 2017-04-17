package com.welink.worker.utils;

import android.content.Context;

/**
 * Created by liuchao on 16/11/23.
 */
public class VersionInfoUtil {
    public static String getAppInfo(Context context) {
        try {
            String pkName = context.getPackageName();
            String versionName = context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            int versionCode = context.getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode;
            return pkName + "  " + versionName + " " + versionCode;
        } catch (Exception e) {
        }
        return null;
    }

    public static int getVersionCode(Context context) {
        try {
            String pkName = context.getPackageName();
            int versionCode = context.getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode;
            return versionCode;
        } catch (Exception e) {
        }
        return -1;
    }

    public static String getVersionName(Context context) {
        try {
            String pkName = context.getPackageName();
            String versionName = context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            return versionName;
        } catch (Exception e) {
        }
        return null;
    }

    public static String getPackageName(Context context){
        try {
            String pkName = context.getPackageName();
            return pkName;
        } catch (Exception e) {
        }
        return null;
    }

}
