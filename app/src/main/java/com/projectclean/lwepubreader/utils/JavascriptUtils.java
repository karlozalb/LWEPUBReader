package com.projectclean.lwepubreader.utils;

/**
 * Created by Carlos Albaladejo Pérez on 14/12/2015.
 */
public class JavascriptUtils {

    private static final String PREV_PAGE = "javascript:Book.prevPage();";
    private static final String NEXT_PAGE = "javascript:Book.nextPage();";

    public static final int EM = 1;
    public static final int PX = 2;

    public static String getNextPageFunc(){
        return NEXT_PAGE;
    }

    public static String getPrevPageFunc(){
        return PREV_PAGE;
    }

    public static String getIncreaseFontFuncEm(float pem){
        String func = "javascript:Book.setStyle(\"font-size\",\""+pem+"em\");";
        return func;
    }

    public static String getChangeFontSizeFuncPx(int ppx){
        String func = "javascript:Book.setStyle(\"font-size\",\""+ppx+"px\");";
        return func;
    }

    public static String getChangeMarginFuncPx(int ppx){
        String func = "javascript:Book.setStyle(\"margin\",\""+ppx+"px\");";
        return func;
    }

    public static String getChangeMarginFuncEm(float pem){
        String func = "javascript:Book.setStyle(\"margin\",\""+pem+"em\");";
        return func;
    }

    public static String getOnPageChangedFunc(){
        //return "javascript:Book.on('book:locationChanged', function(location){ Android.showCurrentPageData(location.anchorPage, location.pageRange, location.percentage); });";

        return "javascript:Book.on('renderer:locationChanged', function(locationCfi){"+
            "Android.saveCurrentPageData(locationCfi);"+
        "});";

        //return "javascript:Book.on('book:pageChanged', function(location){ console.log(\"ñksdjgakjlsdgfaklaskgja\"); });";
    }

    public static String getOnBookReadyFunc(){
        //return "javascript:Book.on('book:ready', function(location){ Android.showCurrentPageData(location.anchorPage, location.pageRange, location.percentage); });";

        return "javascript:Book.on('renderer:chapterDisplayed', function(){"+
                "console.log(\"libro ready\");"+
                "});";

        //return "javascript:Book.on('book:pageChanged', function(location){ console.log(\"ñksdjgakjlsdgfaklaskgja\"); });";
    }

}
