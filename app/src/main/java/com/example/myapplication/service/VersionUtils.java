package com.example.myapplication.service;

import android.os.Build;

public class VersionUtils {

    public static boolean isAndroid10_11() {
        int mV = Build.VERSION.SDK_INT;
        return mV >= 29;
    }

    public static boolean isAndroid8_9() {
        int mV = Build.VERSION.SDK_INT;
        return mV >= 26 && mV <= 28;
    }

    public static boolean isAndroid5_7() {
        int mV = Build.VERSION.SDK_INT;
        return mV >= 21 && mV <= 25;
    }
}
