package com.itaybs.expensemate.Utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class StringUtils {

    /**
     * Formats a number with commas as thousands separators.
     *
     * @param number The number to be formatted.
     * @return A string representation of the formatted number.
     */
    public static String formatNumberWithCommas(int number) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        return numberFormat.format(number);
    }

    /**
     * Formats a number with commas and two decimal places.
     *
     * @param number The number to be formatted.
     * @return A string representation of the formatted number.
     */
    public static String formatNumberWithCommas(double number) {
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.getDefault());
        decimalFormat.setMinimumFractionDigits(2);
        decimalFormat.setMaximumFractionDigits(2);
        if (Math.abs(number) < 0.009) {
            number = 0;
        }
        return decimalFormat.format(number);
    }

    /**
     * Formats a number as a dollar amount with commas and two decimal places.
     *
     * @param number The number to be formatted.
     * @return A string representation of the formatted dollar amount.
     */
    public static String formatDollars(double number) {
        return "$" + formatNumberWithCommas(number);
    }

    public static String getEmailAsName(String email) {
        return email.substring(0, email.indexOf('@'));
    }
}
