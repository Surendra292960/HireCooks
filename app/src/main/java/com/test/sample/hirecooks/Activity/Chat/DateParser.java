package com.test.sample.hirecooks.Activity.Chat;

import com.test.sample.hirecooks.Models.Chat.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DateParser {
    static SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
    static SimpleDateFormat chat_group_date_format = new SimpleDateFormat("dd-MM-yyyy");
    static DateFormat date = new SimpleDateFormat("dd MMMM yyyy");
    static DateFormat chat_group_date = new SimpleDateFormat("dd-MM-yyyy");
    static DateFormat time = new SimpleDateFormat("HH:mm a");

    private static Date getDateTime(String sentat) {
        Date date= null;
        try{
            date = format.parse(sentat);
        }catch (Exception e){
            e.printStackTrace();
        }
        return date;
    }

    private static Date getChatGroupDateF(String sentat) {
        Date date= null;
        try{
            date = chat_group_date_format.parse(sentat);
        }catch (Exception e){
            e.printStackTrace();
        }
        return date;
    }
    public static String convertDateToString(String sentat) {
        return sentat;
    }

    public static String getChatGroupDate(String sentat) {
        return chat_group_date.format( getDateTime(sentat));
    }
    public static String getDate(String sentat) {
        return date.format( getChatGroupDateF(sentat));
    }
    public static String getTime(String sentat) {
        return time.format( getDateTime(sentat));
    }
}
