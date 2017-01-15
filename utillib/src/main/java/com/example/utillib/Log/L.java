package com.example.utillib.Log;

import android.util.Log;

/**
 * Created by admin on 2017/1/15.
 */

public class L {

    public static String TAG = "taxi";


    public static boolean isNeedLog = true;


    public static void i(String content) {
        if (isNeedLog) {
            Log.i(TAG, content);
        }
    }

    public static void d(String content) {
        if (isNeedLog) {
            Log.d(TAG, content);

        }
    }

    public static void e(String content) {
        if (isNeedLog) {
            Log.e(TAG, content);
        }
    }

    public static void w(String content) {
        if (isNeedLog) {
            Log.w(TAG, content);
        }
    }

}
