package com.projectclean.lwepubreader.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
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
import java.util.LinkedList;

/**
 * Created by Carlos Albaladejo Pérez on 08/02/2016.
 */
public class BookPagePagerAdapter extends PagerAdapter {

    private int mCount;
    private EPUBActivity mParentActivity;
    private WebView mView1,mView2,mView3;
    private LinkedList<ViewNode> mViews;
    private Book mBook;


    public BookPagePagerAdapter(Context pcontext){
        mParentActivity = (EPUBActivity)pcontext;
        mCount = 3;
        mViews = new LinkedList<ViewNode>();

        mView1 = (WebView)LayoutInflater.from(mParentActivity).inflate(R.layout.layout_epub_rendering_view,null);
        mView2 = (WebView)LayoutInflater.from(mParentActivity).inflate(R.layout.layout_epub_rendering_view,null);
        mView3 = (WebView)LayoutInflater.from(mParentActivity).inflate(R.layout.layout_epub_rendering_view,null);

        mViews.add(new ViewNode(0, mView1));
        mViews.add(new ViewNode(1, mView2));
        mViews.add(new ViewNode(2, mView3));
    }

    public void initializeWebViews(){
        mBook = mParentActivity.getCurrentBook();

        String htmlSite = "";

        try {
            htmlSite = IOUtils.toString(new InputStreamReader(mParentActivity.getAssets().open("epub_page_skeleton.html")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mView1.loadDataWithBaseURL("file:///android_asset/", htmlSite, "text/html", "UTF-8", null);
        setWebViewConfiguration(mView1);
        mView2.loadDataWithBaseURL("file:///android_asset/", htmlSite, "text/html", "UTF-8", null);
        setWebViewConfiguration(mView2);
        mView3.loadDataWithBaseURL("file:///android_asset/", htmlSite, "text/html", "UTF-8", null);
        setWebViewConfiguration(mView3);
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        //collection.removeView((View) view);
    }

    public void addPage(int pcurrentpage){
        if (pcurrentpage >= mCount - 1) {
            mCount++;
            notifyDataSetChanged();
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        ViewNode viewNode = getFarthest(position);

        if (viewNode.RECYCLED){
            ViewGroup parent = (ViewGroup)viewNode.VIEW.getParent();
            if (parent != null) parent.removeView(viewNode.VIEW);
        }

        viewNode.INDEX = position;
        container.addView(viewNode.VIEW);

        return viewNode.VIEW;
    }

    public ViewNode getFarthest(int pposition){
        ViewNode currentView;

        currentView = getFreeNode();

        if (currentView == null){
            int distance = 0;
            currentView = null;
            for (ViewNode f : mViews) {
                if (Math.abs(f.INDEX - pposition) > distance) {
                    distance = Math.abs(f.INDEX - pposition);
                    currentView = f;
                    f.RECYCLED = true;
                }
            }
        }

        return currentView;
    }

    public ViewNode getFreeNode(){
        for (ViewNode f : mViews){
            if (f.FREE){
                f.FREE = false;
                return f;
            }
        }
        return null;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private void setWebViewConfiguration(WebView pwebview){

        final WebView webView = pwebview;

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

    private class ViewNode{
        public int INDEX;
        public View VIEW;
        public boolean FREE,RECYCLED;

        public ViewNode(int pindex, View pfragment){
            INDEX = pindex;
            VIEW = pfragment;
            FREE = true;
            RECYCLED = false;
        }
    }
}
