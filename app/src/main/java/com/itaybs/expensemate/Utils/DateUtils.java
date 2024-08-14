package com.itaybs.expensemate.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    /**
     * Formats the given date to a human-readable format.
     * @param date The date to format.
     * @return A formatted date string.
     */
    public static String formatDate(Date date) {
        Calendar nowCal = Calendar.getInstance();
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);

        long diffInMillis = nowCal.getTimeInMillis() - dateCal.getTimeInMillis();
        long diffInHours = diffInMillis / (60 * 60 * 1000);
        long diffInMinutes = (diffInMillis % (60 * 60 * 1000)) / (60 * 1000);

        if (diffInHours < 1) {
            return diffInMinutes + " minutes ago";
        } else if (diffInHours == 1) {
            return "1 hour ago";
        } else if (diffInHours < 24) {
            return diffInHours + " hours ago";
        } else {
            SimpleDateFormat fullDateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
            return fullDateFormat.format(date) + ", " + new SimpleDateFormat("HH:mm a", Locale.getDefault()).format(date);
        }
    }
}