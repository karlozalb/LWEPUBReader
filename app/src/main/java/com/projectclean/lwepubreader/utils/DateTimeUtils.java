package com.projectclean.lwepubreader.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Carlos Albaladejo Pérez on 22/12/2015.
 */
public class DateTimeUtils {

    public static String getCurrentDate(){
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        return df.toString();
    }

}
