package com.rocketmade.templateapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import timber.log.Timber;

/**
 * Created by eliasbagley on 2/3/15.
 */
public class Utils {
    public static void snackbar(Activity activity, String message) {
        if (activity != null) {
            View v = activity.getWindow().getDecorView().getRootView();
            snackbar(v, message);
        } else {
            Timber.e("Trying to call snackbar on a null activity!");
        }
    }

    public static void snackbar(Fragment fragment, String message) {
        if (fragment != null) {
            snackbar(fragment.getActivity(), message);
        } else {
            Timber.e("Trying to call snackbar on a null fragment!");
        }
    }

    public static void snackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    public static void printKeyHash(Context context) {
        PackageInfo info;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Timber.e("This device is not supported");
                activity.finish();
            }
            return false;
        }
        return true;
    }

    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static float getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        float density  = context.getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels;
        float dpWidth  = outMetrics.widthPixels;

        return dpWidth;
    }

    public static float getScreenWidthDp(Context context) {
        int px = (int) getScreenWidth(context);
        return pxToDp(context, px);
    }

    public static float getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        float density  = context.getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels;
        float dpWidth  = outMetrics.widthPixels;

        return dpHeight;
    }

    public static int pxToDp(Context context, int px) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);
        float density = outMetrics.density;

        int dp = (int) Math.ceil(px / density);
        return dp;
    }

    public static int dpToPx(Context context, int dp) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);
        float density = outMetrics.density;

        int px = (int) Math.ceil(dp * density);
        return px;
    }

    public static Integer max(int[] arr) {
        if (arr.length == 0) {
            return null;
        }

        int max = arr[0];

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }

        return max;
    }

    public static Integer min(int[] arr) {
        if (arr.length == 0) {
            return null;
        }

        int min = arr[0];

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < min) {
                min = arr[i];
            }
        }

        return min;
    }


}
