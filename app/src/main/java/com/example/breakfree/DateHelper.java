package com.example.breakfree;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateHelper {

    private static final long MILLIS_PER_DAY = 24L * 60L * 60L * 1000L;

    // Returns the given time moved back to 00:00 of the same day.
    public static long startOfDay(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    public static long today() {
        return startOfDay(System.currentTimeMillis());
    }

    // Number of full calendar days between two moments (never negative).
    public static int daysBetween(long startMillis, long endMillis) {
        long diff = startOfDay(endMillis) - startOfDay(startMillis);
        int days = (int) Math.round(diff / (double) MILLIS_PER_DAY);
        return Math.max(days, 0);
    }

    public static String format(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(new Date(millis));
    }
}
