package com.rocketmade.templateapp.utils;

/**
 * Created by eliasbagley on 8/7/14.
 */
public final class StringUtils {
    private StringUtils() {
        // No instances.
    }

    public static boolean isBlank(CharSequence string){
        return (string == null || string.toString().trim().length() == 0);
    }

    public static String valueOrDefault(String string, String defaultString) {
        return isBlank(string) ? defaultString : string;
    }

    public static String truncateAt(String string, int length) {
        return string.length() > length ? string.substring(0, length) : string;
    }

    public static String convertSnakeCaseToSpacedTitleCase(String string) {
        String[] arr = string.split("_");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < arr.length; i++) {
            sb.append(Character.toUpperCase(arr[i].charAt(0)))
                .append(arr[i].substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

}

