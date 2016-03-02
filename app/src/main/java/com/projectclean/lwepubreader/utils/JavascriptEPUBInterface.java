package com.projectclean.lwepubreader.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.projectclean.lwepubreader.activities.EPUBActivity;
import com.projectclean.lwepubreader.io.FileUtils;
import com.projectclean.lwepubreader.model.Book;

/**
 * Created by Carlos Albaladejo Pérez on 14/12/2015.
 */
public class JavascriptEPUBInterface {

    private Context mContext;
    private Book mBook;
    private FileUtils mFileUtils;

    public JavascriptEPUBInterface(Activity pcontext,Book pbook){
        mContext = pcontext;
        mBook = pbook;
        mFileUtils = FileUtils.getInstance(pcontext);
    }

    @JavascriptInterface
    public void showCurrentPageData(String panchorpage,String ppagerange,String ppercentage){
        Log.i("LWEPUB", "anchorPage: " + panchorpage + " - pageRange: " + ppagerange + " - percentage:" + ppercentage);
        Toast.makeText(mContext, "ASFASFAF", Toast.LENGTH_SHORT);
    }

    @JavascriptInterface
    public void saveBookState(String pcurrentcfi,String pfontsize,String pwidth,String pheight){
        Log.i("LWEPUB","CFI: "+pcurrentcfi+" FONTSIZE: "+pfontsize+" HEIGHT: "+pheight+" WIDTH: "+pwidth);
        mBook.setBookState(pcurrentcfi, pfontsize, pwidth, pheight);
        mBook.save();
    }

    @JavascriptInterface
    public void saveBookLocations(String plocations){
        mBook.setLocations(plocations);
        mBook.save();
    }

    @JavascriptInterface
    public void setUIPageData(String pcurrentpage,String plastpage){
        ((EPUBActivity)mContext).setUIPageData(pcurrentpage, plastpage);
    }

    @JavascriptInterface
    public void saveBookPagination(String pepubpagination){
        mFileUtils.saveStringToInternalStorageFile(mBook.getBookFileName() + ".json", pepubpagination);
    }

    @JavascriptInterface
    public void setSelectedText(String pselectedtext){
        Log.i("LWEPUB","pselectedtext:"+pselectedtext);
        ((EPUBActivity)mContext).setSelectedText(pselectedtext);
    }

    @JavascriptInterface
    public void setSelectedTextBookAndAuthor(String pselectedtext,String pauthor,String ptitle){
        Log.i("LWEPUB","pselectedtext:"+pselectedtext);
        ((EPUBActivity)mContext).setSelectedTextForSharing(pselectedtext,pauthor,ptitle);
    }

    @JavascriptInterface
    public void setBookCompletion(String pbookcompletion){
        mBook.setBookCompletion(Float.parseFloat(pbookcompletion)/ 100f);
        ((EPUBActivity)mContext).setCurrentPageProgressBar((int)(Float.parseFloat(pbookcompletion)));
    }

    @JavascriptInterface
    public void setBookToc(String ptoc){
        ((EPUBActivity)mContext).setToc(ptoc);
    }

}
