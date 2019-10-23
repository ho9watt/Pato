package com.example.pato.customclass;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class LongTypeGetTime {

    public static long getTime(){
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        df.setTimeZone(timeZone);
        Date date = new Date();
        String timeString = df.format(date);

        try {
            date = df.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long timeLong = date.getTime();

        return timeLong;
    }


}
