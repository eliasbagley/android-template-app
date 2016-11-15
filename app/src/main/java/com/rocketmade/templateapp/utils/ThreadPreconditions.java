package com.rocketmade.templateapp.utils;

import android.os.Looper;

import com.rocketmade.templateapp.BuildConfig;

/**
 * Created by eliasbagley on 2/9/15.
 */

public class ThreadPreconditions {
    public static void checkOnMainThread() {
        if (BuildConfig.DEBUG) {
            if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
                throw new IllegalStateException("This method should be called from the Main Thread");
            }
        }
    }
}

