package com.projectclean.lwepubreader.utils;

import android.app.Activity;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;

/**
 * Created by Carlos Albaladejo Pérez on 14/12/2015.
 */
public class ScreenUtils {

    public static int getScreenWidth(Activity pcontext) {
        Display display = pcontext.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static int getScreenHeight(Activity pcontext) {
        Display display = pcontext.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    public static int getPixelsFromDp(Activity pcontext,int pdps){
        DisplayMetrics displayMetrics =  pcontext.getResources().getDisplayMetrics();
        return (int)((pdps * displayMetrics.density) + 0.5);
    }

    public static int getDpFromPixels(Activity pcontext,int ppixels){
        DisplayMetrics displayMetrics =  pcontext.getResources().getDisplayMetrics();
        return (int)((ppixels - 0.5) / displayMetrics.density);
    }

}
