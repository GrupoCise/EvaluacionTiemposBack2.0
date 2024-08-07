package com.web.back.utils;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private DateUtil(){}

    public static String toStringYYYYMMDD(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return date.format(formatter);
    }

    public static String timeToString(Time time){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        return sdf.format(time);
    }

    public static String clearSymbols(String dateString){
        return dateString.replace("-", "")
                .replace(":", "")
                .replace(" ", "");
    }
}
