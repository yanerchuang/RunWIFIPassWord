package com.wifi.android.runwifipassword.util;

import android.util.Log;

import com.wifi.android.runwifipassword.BuildConfig;

/**
 * Created by chuangguo.qi on 2016/11/7.
 */

public class LogUtil {

    private static boolean isShowLog=BuildConfig.DEBUG;
    private static String tag="chuangguo.qi";
    public static void i(String messager){
        if (isShowLog) {
            Log.e(tag, messager);
        }
    }
}
