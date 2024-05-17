package com.web.back.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    private DateUtil(){}

    public static String toStringYYYYMMDD(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        return sdf.format(date);
    }

    public static String clearHyphens(String dateString){
        return dateString.replace("-", "");
    }
}
