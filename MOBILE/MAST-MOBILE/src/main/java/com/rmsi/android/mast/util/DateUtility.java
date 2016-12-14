package com.rmsi.android.mast.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Date and time utilities
 */
public class DateUtility {

    /**
     * Formats provided date string into yyyy-mm-dd
     * @param dateString Date string to format
     * @return
     */
    public static String formatDateString(String dateString){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calender = Calendar.getInstance();
        String result = "";

        if (!StringUtility.isEmpty(dateString)) {
            try {
                calender.setTime(sdf.parse(dateString));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            int day = calender.get(Calendar.DAY_OF_MONTH);
            String strday = day < 10 ? "0" + String.valueOf(day) : String.valueOf(day);
            int month = calender.get(Calendar.MONTH) + 1; // adding 1 as month starts with 0
            String strmonth = month < 10 ? "0" + String.valueOf(month) : String.valueOf(month);
            int year = calender.get(Calendar.YEAR);

            result = year + "-" + strmonth + "-" + strday;
        }
        return result;
    }
}
