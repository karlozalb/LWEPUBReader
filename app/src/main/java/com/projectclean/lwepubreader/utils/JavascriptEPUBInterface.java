package com.projectclean.lwepubreader.utils;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by Carlos Albaladejo Pérez on 14/12/2015.
 */
public class JavascriptEPUBInterface {

    private Context mContext;

    public JavascriptEPUBInterface(Context pcontext){
        mContext = pcontext;
    }

    @JavascriptInterface
    public void showCurrentPageData(String panchorpage,String ppagerange,String ppercentage){
        Log.i("LWEPUB","anchorPage: "+panchorpage+" - pageRange: "+ppagerange+" - percentage:"+ppercentage);
        Toast.makeText(mContext,"ASFASFAF",Toast.LENGTH_SHORT);
    }

}
