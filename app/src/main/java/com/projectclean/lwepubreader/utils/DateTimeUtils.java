package com.projectclean.lwepubreader.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Carlos Albaladejo Pérez on 22/12/2015.
 */
public class DateTimeUtils {

    public static String getCurrentDate(){

        String locale = Locale.getDefault().getDisplayLanguage();
        SimpleDateFormat df;

        if (locale.equalsIgnoreCase("es")){
            df = new SimpleDateFormat("dd/MM/yyyy");
        }else{
            df = new SimpleDateFormat("MM/dd/yyyy");
        }

        Calendar cal = Calendar.getInstance();

        Log.i("LWEPUB", "date: " + df.format(cal.getTime()));

        return df.format(cal.getTime());
    }

}
