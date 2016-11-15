package com.rocketmade.templateapp.utils;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Created by eliasbagley on 2/2/15.
 */

public class AsyncMainThreadBus extends Bus {
    private final Handler _mainThread = new Handler(Looper.getMainLooper());

    @Override
    public void post(final Object event) {
        _mainThread.post(new Runnable() {
            @Override
            public void run() {
                AsyncMainThreadBus.super.post(event);
            }
        });
    }
}

