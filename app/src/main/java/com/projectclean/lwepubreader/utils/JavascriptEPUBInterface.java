package com.projectclean.lwepubreader.utils;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.projectclean.lwepubreader.model.Book;

/**
 * Created by Carlos Albaladejo Pérez on 14/12/2015.
 */
public class JavascriptEPUBInterface {

    private Context mContext;
    private Book mBook;

    public JavascriptEPUBInterface(Context pcontext,Book pbook){
        mContext = pcontext;
        mBook = pbook;
    }

    @JavascriptInterface
    public void showCurrentPageData(String panchorpage,String ppagerange,String ppercentage){
        Log.i("LWEPUB","anchorPage: "+panchorpage+" - pageRange: "+ppagerange+" - percentage:"+ppercentage);
        Toast.makeText(mContext,"ASFASFAF",Toast.LENGTH_SHORT);
    }

    @JavascriptInterface
    public void saveCurrentPageData(String pcurrentpageconfig){
        Log.i("LWEPUB","pcurrentpageconfig: "+pcurrentpageconfig);
        mBook.setBookState(pcurrentpageconfig);
        mBook.save();
    }

}
