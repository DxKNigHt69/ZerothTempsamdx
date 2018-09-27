package com.whereismytransport.sdktemplateapp;

import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public final class TimeFormattingHelper {

    public static String formatDate(Date dateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        dateFormat.setTimeZone(TimeZone.getDefault());
        return dateFormat.format(dateTime);
    }

    public static String formatDuration(int durationSeconds) {
        long durationMillis = durationSeconds * DateUtils.SECOND_IN_MILLIS;

        if (durationMillis >= DateUtils.HOUR_IN_MILLIS) {
            return durationMillis / DateUtils.HOUR_IN_MILLIS + " hrs";
        } else if (durationMillis >= DateUtils.MINUTE_IN_MILLIS) {
            return durationMillis / DateUtils.MINUTE_IN_MILLIS + " min";
        } else if (durationMillis >= DateUtils.SECOND_IN_MILLIS) {
            // Intentionally using 1 minute as the most granular duration of time.
            return durationSeconds + "1 min";
        }

        return null;
    }
}
