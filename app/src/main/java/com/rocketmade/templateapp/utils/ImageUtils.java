package com.rocketmade.templateapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import timber.log.Timber;

/**
 * Created by eliasbagley on 2/10/15.
 */
public class ImageUtils {

    //Note: this method is pretty slow compared to fileToBase64
    public static String bitmapToBase64String(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArr = stream.toByteArray();

        return Base64.encodeToString(byteArr, Base64.DEFAULT);
    }


    public static String fileToBase64(String filename) {
        InputStream inputStream = null;//You can get an inputStream using any IO API
        try {
            inputStream = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            Timber.d("no file found at path!");
        }

        byte[] bytes;
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        bytes = output.toByteArray();
        String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
        return encodedString;
    }


    public static void overlayImageViewWithColor(int color, ImageView imageView, Context context) {
        imageView.setColorFilter(context.getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
    }


    public static Bitmap cropToSquare(Bitmap bitmap){
        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width)? height - ( height - width) : height;
        int crop = (width - height) / 2;
        crop = (crop < 0)? 0: crop;
        Bitmap cropImg = Bitmap.createBitmap(bitmap, crop, 0, newWidth, newHeight);

        return cropImg;
    }
}
