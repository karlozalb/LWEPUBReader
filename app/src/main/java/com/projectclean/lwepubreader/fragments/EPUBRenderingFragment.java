package com.projectclean.lwepubreader.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.projectclean.lwepubreader.R;
import com.projectclean.lwepubreader.activities.EPUBActivity;
import com.projectclean.lwepubreader.model.Book;
import com.projectclean.lwepubreader.utils.JavascriptEPUBInterface;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Carlos Albaladejo Pérez on 04/02/2016.
 */
public class EPUBRenderingFragment extends GenericFragment{

    WebView mWebView;
    Book mBook;
    EPUBActivity mParentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //setFragmentParams();

        View v = inflater.inflate(R.layout.layout_fragment_epub_rendering_fragment, container, false);

        mWebView = (WebView)v;
        mParentActivity = (EPUBActivity)getActivity();
        mBook = mParentActivity.getCurrentBook();

        String htmlSite = "";

        try {
            htmlSite = IOUtils.toString(new InputStreamReader(mParentActivity.getAssets().open("epub_page_skeleton.html")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mWebView.loadDataWithBaseURL("file:///android_asset/", htmlSite, "text/html", "UTF-8", null);
        setWebViewConfiguration();

        return v;
    }

    private void setWebViewConfiguration(){

        final WebView webView = mWebView;

        WebView.setWebContentsDebuggingEnabled(true);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);

        webView.setWebViewClient(new WebViewClient(){
                                     public void onPageFinished(WebView view, String url) {

                                         if (mBook.getWidth() == null || mBook.getHeight() ==  null){
                                             int[] virtualSizes = mParentActivity.updateMargins();
                                             webView.loadUrl("javascript:loadBook('file:///"+mParentActivity.getCurrentBookPath()+"',"+mBook.getFontSize()+",'"+mBook.getBookState()+"',"+virtualSizes[0]+","+virtualSizes[1] + ");");
                                         }else{
                                             webView.loadUrl("javascript:loadBook('file:///"+mParentActivity.getCurrentBookPath()+"',"+mBook.getFontSize()+",'"+mBook.getBookState()+"',"+mBook.getWidth()+","+mBook.getHeight()+");");
                                         }

                                     }}
        );

        webView.addJavascriptInterface(new JavascriptEPUBInterface(mParentActivity, mBook), "Android");
    }

}
