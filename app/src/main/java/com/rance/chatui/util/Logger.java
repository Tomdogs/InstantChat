package com.rance.chatui.util;

import android.util.Log;

/**
 * log統一管理类
 */

public class Logger {

    private Logger()
    {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isDebug = true;// 是否需要打印bug，可以在application的onCreate函数里面初始化
    private static final String TAG = "tag";

    // 下面四个是默认tag的函数
    public static void i(String msg)
    {
        if (isDebug)
            Log.i(TAG, msg);
    }

    public static void d(String msg)
    {
        if (isDebug)
            Log.d(TAG, msg);
    }

    public static void e(String msg)
    {
        if (isDebug)
            Log.e(TAG, msg);
    }

    public static void v(String msg)
    {
        if (isDebug)
            Log.v(TAG, msg);
    }

    // 下面是传入自定义tag的函数
    public static void i(Class<?> clazz, String msg)
    {
        if (isDebug)
            Log.i(clazz.getSimpleName(), msg);
    }

    public static void d(Class<?> clazz, String msg)
    {
        if (isDebug)
            Log.i(clazz.getSimpleName(), msg);
    }

    public static void e(Class<?> clazz, String msg)
    {
        if (isDebug)
            Log.i(clazz.getSimpleName(), msg);
    }

    public static void v(Class<?> clazz, String msg)
    {
        if (isDebug)
            Log.i(clazz.getSimpleName(), msg);
    }
}
