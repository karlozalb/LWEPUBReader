package com.projectclean.lwepubreader.activities;

import android.annotation.SuppressLint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SeekBar;

import com.projectclean.lwepubreader.R;
import com.projectclean.lwepubreader.model.Book;
import com.projectclean.lwepubreader.utils.JavascriptEPUBInterface;
import com.projectclean.lwepubreader.utils.JavascriptUtils;
import com.projectclean.lwepubreader.utils.ScreenUtils;

import java.io.File;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class EPUBActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            /*ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }*/
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    public static final String EPUBPATHEXTRA = "EPUBPATHEXTRA";
    private String mEPUBPath;
    private int mScreenWidth;

    /* Font and margin values */
    private int mCurrentFontSizePx = 12;
    private float mCurrentFontSizeEm = 0.75f;

    private int mCurrentMarginSizePx = 12;
    private float mCurrentMarginSizeEm = 1;

    private int mMaxFontSize,mMinFontSize;
    private int mMaxMarginSize,mMinMarginSize;

    /* UI components */
    private SeekBar mFontChangeSeekBar,mMarginChangeSeekBar;
    private Book mBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_epub);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        mFontChangeSeekBar = (SeekBar)findViewById(R.id.seekbar_font_change);
        mMarginChangeSeekBar = (SeekBar)findViewById(R.id.seekbar_margin_change);

        Bundle params = getIntent().getExtras();
        if (params != null){
            mEPUBPath = params.getString(EPUBPATHEXTRA);

            mBook = Book.find(Book.class, "BOOK_PATH = ?", mEPUBPath).get(0);

            String htmlWebSite = "";
            htmlWebSite += "<script src=\"file:///android_asset/epub.js\" type=\"text/javascript\"></script>";
            htmlWebSite += "<script src=\"file:///android_asset/zip.min.js\" type=\"text/javascript\"></script>";
            htmlWebSite += "<div id=\"area\"></div>";

            ((WebView)mContentView).loadDataWithBaseURL("file:///android_asset/",htmlWebSite, "text/html", "UTF-8",null);
            setWebViewConfiguration();
        }

        mScreenWidth = ScreenUtils.getScreenWidth(this);

        setParameters();

        mFontChangeSeekBar.setMax(mMaxFontSize - mMinFontSize);
        mFontChangeSeekBar.setProgress(mCurrentFontSizePx - mMinFontSize);

        mMarginChangeSeekBar.setMax(mMaxMarginSize - mMinMarginSize);
        mMarginChangeSeekBar.setProgress((int)mCurrentMarginSizeEm - mMinMarginSize);

        initializeGUIComponentListeners();
    }

    private void initializeGUIComponentListeners() {

        //                                  FONT SIZE
        //***********************************************************************************
        mFontChangeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
                mCurrentFontSizePx = progress + mMinFontSize;
                if (mCurrentFontSizePx > mMaxFontSize) mCurrentFontSizePx = mMaxFontSize;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mBook.setFontSize(mCurrentFontSizePx);
                ((WebView)mContentView).loadUrl(JavascriptUtils.getChangeFontSizeFuncPx(mCurrentFontSizePx));
            }
        });

        //                                  MARGIN SIZE
        //***********************************************************************************
        mMarginChangeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
                mCurrentMarginSizeEm = progress + mMinMarginSize;
                if (mCurrentMarginSizeEm > mMaxMarginSize) mCurrentMarginSizeEm = mMaxMarginSize;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mBook.setMargin(mCurrentMarginSizeEm);
                ((WebView)mContentView).loadUrl(JavascriptUtils.getChangeMarginFuncEm(mCurrentMarginSizeEm));
            }
        });
    }

    private void setParameters(){
        mMinFontSize = getResources().getInteger(R.integer.min_font_size_px);
        mMaxFontSize = getResources().getInteger(R.integer.max_font_size_px);

        mMinMarginSize = getResources().getInteger(R.integer.min_margin_size_px);
        mMaxMarginSize = getResources().getInteger(R.integer.max_margin_size_px);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void setWebViewConfiguration(){

        final WebView webView = (WebView)mContentView;

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);

        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                String loadEpub = "javascript: var Book = ePub(\"file:///" + mEPUBPath + "\");";

                if (mBook.getBookState() != null && mBook.getBookState().length() > 0) {
                    loadEpub += "Book.gotoCfi('" + mBook.getBookState() + "');";
                }

                File file = new File(getFilesDir().getPath() + "/" + mBook.getBookFileName() + ".json");
                if (!file.exists()) {
                    loadEpub += "Book.pageListReady.then(function(pageList){" +
                            "Android.saveBookPagination(JSON.stringify(pageList));" +
                            "});";
                    loadEpub += "Book.ready.all.then(function(){ Book.generatePagination(); });";
                }


                loadEpub += "var rendered = Book.renderTo(\"area\");";

                webView.loadUrl(loadEpub);
                webView.loadUrl(JavascriptUtils.getOnPageChangedFunc());
                webView.loadUrl(JavascriptUtils.getOnBookReadyFunc());

                if (file.exists()) {
                    String loadPagination = "javascript: EPUBJS.core.request(\"file://" + mBook.getBookPath() + ".json\").then(function(storedPageList){pageList = storedPageList;console.log('ano palpitante');Book.loadPagination(pageList);});";
                    loadPagination += "Book.pageListReady.then(function(pageList){" +
                            "console.log(JSON.stringify(pageList));" +
                            "});";
                    webView.loadUrl(loadPagination);
                }

                if (mBook.getFontSize() != 0) {
                    mCurrentFontSizePx = mBook.getFontSize();
                    ((WebView) mContentView).loadUrl(JavascriptUtils.getChangeFontSizeFuncPx(mCurrentFontSizePx));
                }

                if (mBook.getMargin() != 0) {
                    mCurrentMarginSizeEm = mBook.getMargin();
                    ((WebView) mContentView).loadUrl(JavascriptUtils.getChangeMarginFuncEm(mCurrentMarginSizeEm));
                }
            }
        });

        webView.addJavascriptInterface(new JavascriptEPUBInterface(this, mBook), "Android");

        webView.setOnTouchListener(new View.OnTouchListener() {

            private float mStartX, mEndX;
            private float mStartY, mEndY;

            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mStartX = event.getX();
                    mStartY = event.getY();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mEndX = event.getX();
                    mEndY = event.getY();

                    float offsetX = mStartX - mEndX;
                    float offsetXAbs = Math.abs(offsetX);

                    float offsetY = mStartY - mEndY;
                    float offsetYAbs = Math.abs(offsetY);

                    if (offsetXAbs < 20 && offsetYAbs < 20) {
                        toggle();
                        mDelayHideTouchListener.onTouch(v, event);
                    } else {
                        if (offsetXAbs >= offsetYAbs) {
                            if (offsetXAbs > 50) {
                                if (offsetX < 0) {
                                    webView.loadUrl(JavascriptUtils.getPrevPageFunc());
                                } else if (offsetX > 0) {
                                    webView.loadUrl(JavascriptUtils.getNextPageFunc());
                                }
                            }
                        } else {
                            if (offsetYAbs > 50) {
                                if (offsetY < 0) {
                                    mCurrentFontSizePx -= 2;
                                } else if (offsetY > 0) {
                                    mCurrentFontSizePx += 2;
                                }
                                webView.loadUrl(JavascriptUtils.getChangeFontSizeFuncPx(mCurrentFontSizePx));
                            }
                        }
                    }
                }

                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
    }
}
