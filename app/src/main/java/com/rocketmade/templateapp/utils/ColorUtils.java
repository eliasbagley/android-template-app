package com.rocketmade.templateapp.utils;

import android.graphics.Color;

import java.util.Random;

/**
 * Created by eliasbagley on 4/8/15.
 */
public class ColorUtils {
    public static int randomColor() {
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        return color;
    }
}
