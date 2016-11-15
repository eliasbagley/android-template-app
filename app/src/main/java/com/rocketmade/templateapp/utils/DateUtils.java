package com.rocketmade.templateapp.utils;

import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import timber.log.Timber;

import static org.threeten.bp.temporal.ChronoUnit.DAYS;
import static org.threeten.bp.temporal.ChronoUnit.HOURS;
import static org.threeten.bp.temporal.ChronoUnit.MINUTES;
import static org.threeten.bp.temporal.ChronoUnit.MONTHS;
import static org.threeten.bp.temporal.ChronoUnit.SECONDS;
import static org.threeten.bp.temporal.ChronoUnit.YEARS;

/**
 * Created by eliasbagley on 3/3/15.
 */
public class DateUtils {
    public static Date iso8601DateFromString(String string) {
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        df1.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date = df1.parse(string);
            return date;
        } catch (ParseException e) {
           Timber.d("Error while parsing iso8601 date!");
           return null;
        }
    }

    public static String counterStringFromDate(OffsetDateTime fromDateTime) {
        // API is ignoring timezone
        OffsetDateTime toDateTime = OffsetDateTime.now(ZoneOffset.UTC);

        OffsetDateTime tempDateTime = OffsetDateTime.from(fromDateTime);

        long years = tempDateTime.until( toDateTime, YEARS);
        tempDateTime = tempDateTime.plusYears(years);

        long months = tempDateTime.until( toDateTime, MONTHS);
        tempDateTime = tempDateTime.plusMonths(months);

        long days = tempDateTime.until( toDateTime, DAYS);
        tempDateTime = tempDateTime.plusDays(days);


        long hours = tempDateTime.until( toDateTime, HOURS);
        tempDateTime = tempDateTime.plusHours(hours);

        long minutes = tempDateTime.until( toDateTime, MINUTES);
        tempDateTime = tempDateTime.plusMinutes( minutes );

        long seconds = tempDateTime.until( toDateTime, SECONDS);

        String dayStr = String.format("%03d", days);
        String hrsStr = String.format("%02d", hours);
        String minStr = String.format("%02d", minutes);
        String secStr = String.format("%02d", seconds);
        return dayStr + ":" + hrsStr + ":" + minStr + ":" + secStr;
    }


    public static String convertToLongFormDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM dd");
        return formatter.format(date);
    }

    // returns the format +0700
    public static String getCurrentTimeZoneString() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("Z");
        return sdf.format(d);
    }

    // returns the format +07:00
    public static String getCurrentTimeZoneAsOffsetString() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("Z");
        String timeZoneString = sdf.format(d);
        timeZoneString = timeZoneString.substring(0, 3) + ":" + timeZoneString.substring(3, timeZoneString.length());
        return timeZoneString;
    }

}
