package com.loslink.myview.utils;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;


public class GbLog {

    private static final String TAG = "GbLog";
    private static final boolean openLog = true;

    public static void i(Object o) {
        if (openLog) {
            Log.i(TAG, String.valueOf(o));
        }
    }

    public static void e(Object o) {
        if (openLog) {
            Log.e(TAG, String.valueOf(o));
        }
    }

    public static void e(String tag, Object e) {
        if (openLog) {
        Log.e(TAG, tag + " error:" + String.valueOf(e));
        }
    }

    public static void e(String o, Throwable t) {
        if (openLog) {
            Log.e(TAG, o + "\n" + printError(t));
        }
    }

    public static void w(Object o) {
        if (openLog) {
            Log.w(TAG, String.valueOf(o));
        }
    }

    public static void w(Object o, Throwable t) {
        if (openLog) {
            Log.w(TAG, String.valueOf(o) + "\n" + printError(t));
        }
    }

    public static void v(Object o) {
        Log.v(TAG, String.valueOf(o));
    }

    public static void d(Object... os) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object o: os) {
            if (o instanceof Object[]) {
                stringBuilder.append(Arrays.toString((Object[]) o));
            } else {
                stringBuilder.append(String.valueOf(o));
            }
            stringBuilder.append(" ");
        }
        Log.d(TAG, stringBuilder.toString());
    }

    public static void i(String tag, Object s) {
        Log.i(TAG, tag + " " + String.valueOf(s));
    }

    private static String printError(Throwable throwable) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        return writer.toString();
    }
}
