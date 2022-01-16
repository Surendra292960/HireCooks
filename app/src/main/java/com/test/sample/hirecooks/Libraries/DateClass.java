package com.test.sample.hirecooks.Libraries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateClass {
    static SimpleDateFormat format = new SimpleDateFormat( "yyy-dd-MM HH:mm:ss" );
    static SimpleDateFormat format2 = new SimpleDateFormat( "yyy-dd-MM" );
    static DateFormat dates = new SimpleDateFormat( "dd MMM yyyy" );
    static DateFormat times = new SimpleDateFormat( "HH:mm a" );

    private static String getTime(String sentat) {
        Date date = null;
        try {
            date = format.parse( sentat );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return times.format(date);
    }

    private static String getDate(String toString) {
        Date date = null;
        try {
            date = format2.parse( toString );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dates.format(date);
    }

}
