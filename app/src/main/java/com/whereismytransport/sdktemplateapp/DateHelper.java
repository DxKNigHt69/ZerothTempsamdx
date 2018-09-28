package com.whereismytransport.sdktemplateapp;

import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;

public final class DateHelper {
    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * Parses a string representation of a date.
     *
     * @param isoDateString A date string in the format {@link DateHelper#ISO_DATE_FORMAT}.
     * @return A {@link Date} instance in the UTC timezone.
     * @throws ParseException If the isoDateString is invalid.
     */
    public static Date parseIsoDateString(String isoDateString) throws ParseException {
        DateFormat isoDateFormat = new SimpleDateFormat(ISO_DATE_FORMAT, Locale.getDefault());
        isoDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        if (isoDateString.length() > 21) {
            isoDateString = isoDateString.substring(0, 19) + "Z";
        }

        return isoDateFormat.parse(isoDateString);
    }

    /**
     * Converts a date into a string representation.
     *
     * @param date The {@link Date} to convert.
     * @return A string representation in the format {@link DateHelper#ISO_DATE_FORMAT} in the UTC timezone.
     */
    public static String dateToIsoFormattedString(Date date) {
        DateFormat isoDateFormat = new SimpleDateFormat(ISO_DATE_FORMAT, Locale.getDefault());
        isoDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTime(date);
        Date time = calendar.getTime();

        return isoDateFormat.format(time);
    }

    /**
     * Calculate the difference in seconds between laterDate and earlierDate. Dates are expected to
     * be from the same timezone.
     *
     * @param laterDate   A {@link Date} that represents a later point in time relative to earlierDate.
     * @param earlierDate A {@link Date} that represents an earlier point in time relative to laterDate.
     * @return The difference in seconds between laterDate and earlierDate.
     */
    public static int getTimeDifferenceSeconds(@NonNull Date laterDate, @NonNull Date earlierDate) {
        long diff = laterDate.getTime() - earlierDate.getTime();
        return (int) (diff / DateUtils.SECOND_IN_MILLIS);
    }
}
