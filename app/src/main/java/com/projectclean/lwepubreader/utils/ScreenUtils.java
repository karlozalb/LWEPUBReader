package com.projectclean.lwepubreader.utils;

import android.app.Activity;
import android.graphics.Point;
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

}
