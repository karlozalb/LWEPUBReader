package com.projectclean.lwepubreader.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import com.projectclean.lwepubreader.adapters.MyLibraryAdapter;
import com.projectclean.lwepubreader.epub.EPUBImporter;
import com.projectclean.lwepubreader.epub.epubloader.EPUBLoaderHelper;
import com.projectclean.lwepubreader.io.FileUtils;
import com.projectclean.lwepubreader.model.Book;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Carlos Albaladejo Pérez on 16/12/2015.
 */
public class CoverThumbGenerator {

    private Activity mContext;
    private String mPrivateFilesDir;
    private Queue<CoverGenTaskData> mQueue;
    private EPUBImporter mEPUBImporter;
    private FileUtils mFileUtils;

    public CoverThumbGenerator(Activity pcontext,EPUBImporter pepubimporter){
        mContext = pcontext;
        mQueue = new ConcurrentLinkedQueue<CoverGenTaskData>();
        mEPUBImporter = pepubimporter;
        mFileUtils = FileUtils.getInstance(pcontext);
    }

    synchronized public void addTask(String pepubpath,Book pnewbook){
        CoverGenTaskData coverGenTask = new CoverGenTaskData();

        coverGenTask.EPUB_PATH = pepubpath;
        coverGenTask.BOOK = pnewbook;

        mQueue.add(coverGenTask);

        if (mQueue.size() > 0) nextTask();
    }

    private void nextTask(){
        if (mQueue.size() > 0){
            CoverGenTaskData task = mQueue.poll();
            generateCover(task);
        }
    }

    private void generateCover(CoverGenTaskData ptask){
        mPrivateFilesDir = mContext.getFilesDir().getAbsolutePath();
        String fileName = mFileUtils.getFileName(ptask.EPUB_PATH);
        prepareScreenshot(fileName,ptask);
    }

    public Bitmap loadBitmapFromView(View v) {
        if (v.getMeasuredHeight() <= 0) {
            //v.measure(720, 1280);
            Bitmap b = Bitmap.createBitmap(600, 800, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            //v.layout(0, 0, 720, 1280);
            v.draw(c);
            return b;
        }else {
            Bitmap b = Bitmap.createBitmap(v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
            v.draw(c);
            return b;
        }
    }

    public void prepareScreenshot(final String pfilename,final CoverGenTaskData ptask){

        final WebView webView = new WebView(mContext);
        webView.measure(600, 800);
        webView.layout(0, 0, 600, 800);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);

        String htmlWebSite = "<script src=\"file:///android_asset/epub.js\" type=\"text/javascript\"></script>";
        htmlWebSite += "<script src=\"file:///android_asset/zip.min.js\" type=\"text/javascript\"></script>";
        htmlWebSite += "<div id=\"area\"></div>";

        webView.loadDataWithBaseURL("file:///android_asset/", htmlWebSite, "text/html", "UTF-8", null);

        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                String loadEpub = "javascript:" +
                        "var Book = ePub(\"file:///" + ptask.EPUB_PATH + "\");" +
                        "Book.renderTo(\"area\");";
                webView.loadUrl(loadEpub);

                Log.i("LWEPUB", "Carga WebView finalizada.");

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        createBitmap(webView, pfilename);
                        ptask.BOOK.setBookCover(pfilename+".jpg");
                        ptask.BOOK.save();
                        mEPUBImporter.addProgress();
                    }
                }, 5000);
            }
        });
    }

    public void createBitmap(View pview,String pfilename){
        Bitmap result = loadBitmapFromView(pview);

        FileOutputStream coverOstream = null;
        try {
            coverOstream = mContext.openFileOutput(pfilename+".jpg",Context.MODE_PRIVATE);
            result.compress(Bitmap.CompressFormat.JPEG, 100, coverOstream);
        } catch (FileNotFoundException e) {
            Log.e("LWEPUB","There was an error saving the EPUB cover");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (coverOstream != null) {
                    coverOstream.close();
                    nextTask();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class CoverGenTaskData{

        public String EPUB_PATH;
        public Book BOOK;

        public EPUBLoaderHelper EPUB;
        public ImageView IMAGE;

        public MyLibraryAdapter.MyLibraryBookListHolder BOOK_HOLDER;
    }

}
