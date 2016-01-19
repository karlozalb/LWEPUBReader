package com.projectclean.lwepubreader.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SeekBar;
import android.widget.TextView;

import com.projectclean.lwepubreader.R;
import com.projectclean.lwepubreader.customviews.CustomWebView;
import com.projectclean.lwepubreader.model.Book;
import com.projectclean.lwepubreader.translation.ITranslationCallBack;
import com.projectclean.lwepubreader.translation.TranslationProvider;
import com.projectclean.lwepubreader.utils.JavascriptEPUBInterface;
import com.projectclean.lwepubreader.utils.JavascriptUtils;
import com.projectclean.lwepubreader.utils.ScreenUtils;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class EPUBActivity extends AppCompatActivity implements ITranslationCallBack {
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
    private int mMinWidthAndHeight;
    private int mMaxMarginSize,mMinMarginSize,mCurrentWidthAndHeight,mMaxWidthAndHeight;

    /* UI components */
    private SeekBar mFontChangeSeekBar,mMarginChangeSeekBar;
    private TextView mCurrentPageTextView;

    /* Sugar record model object */
    private Book mBook;

    /* Translation provider */
    private TranslationProvider mTranslationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_epub);

        mTranslationProvider = new TranslationProvider(this);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);

        mFontChangeSeekBar = (SeekBar)findViewById(R.id.seekbar_font_change);
        mMarginChangeSeekBar = (SeekBar)findViewById(R.id.seekbar_margin_change);
        mCurrentPageTextView = (TextView)findViewById(R.id.page_number_indicator);

        Bundle params = getIntent().getExtras();
        if (params != null){
            mEPUBPath = params.getString(EPUBPATHEXTRA);

            mBook = Book.find(Book.class, "BOOK_PATH = ?", mEPUBPath).get(0);

            String htmlWebSite = "";
            try {
                htmlWebSite = IOUtils.toString(new InputStreamReader(getAssets().open("epub_page_skeleton.html")));
            } catch (IOException e) {
                e.printStackTrace();
            }

            ((WebView) mContentView).loadDataWithBaseURL("file:///android_asset/", htmlWebSite, "text/html", "UTF-8", null);
            //((WebView) mContentView).loadUrl("http://www.google.es");
            setWebViewConfiguration();
        }

        mScreenWidth = ScreenUtils.getScreenWidth(this);

        setParameters();

        mFontChangeSeekBar.setMax(mMaxFontSize - mMinFontSize);
        mFontChangeSeekBar.setProgress(mCurrentFontSizePx - mMinFontSize);

        mMarginChangeSeekBar.setMax(mMaxWidthAndHeight - mMinWidthAndHeight);
        mMarginChangeSeekBar.setProgress(mCurrentWidthAndHeight -mMinWidthAndHeight);

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
                Log.i("LWEPUB", mBook.toString());
                mBook.save();
                ((WebView) mContentView).loadUrl(JavascriptUtils.getChangeFontSizeFuncPx(mCurrentFontSizePx));
            }
        });

        //                                  MARGIN SIZE
        //***********************************************************************************
        mMarginChangeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
                /*mCurrentMarginSizeEm = progress + mMinMarginSize;
                if (mCurrentMarginSizeEm > mMaxMarginSize) mCurrentMarginSizeEm = mMaxMarginSize;*/
                mCurrentWidthAndHeight = progress + mMinWidthAndHeight;
                if (mCurrentWidthAndHeight > mMaxWidthAndHeight) mCurrentWidthAndHeight = mMaxWidthAndHeight;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mBook.setWidthAndHeight(mCurrentWidthAndHeight);
                Log.i("LWEPUB", mBook.toString());
                mBook.save();
                ((WebView) mContentView).loadUrl(JavascriptUtils.getChangeWidthAndHeightFuncEm(mCurrentWidthAndHeight));
            }
        });
    }

    private void setParameters(){
        mMinFontSize = getResources().getInteger(R.integer.min_font_size_px);
        mMaxFontSize = getResources().getInteger(R.integer.max_font_size_px);

        mMinMarginSize = getResources().getInteger(R.integer.min_margin_size_px);
        mMaxMarginSize = getResources().getInteger(R.integer.max_margin_size_px);

        mMinWidthAndHeight = 75;
        mMaxWidthAndHeight = 95;
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
                String loadEpub = "javascript: var Book = ePub({ spread: \"none\" }); Book.open(\"file:///" + mEPUBPath + "\");";

                if (mBook.getBookState() != null && mBook.getBookState().length() > 0) {
                    loadEpub += "Book.gotoCfi('" + mBook.getBookState() + "');";
                }

                File file = new File(getFilesDir().getPath() + "/" + mBook.getBookFileName() + ".json");
                if (!file.exists()) {
                    loadEpub += "Book.pageListReady.then(function(pageList){" +
                            "Android.saveBookPagination(JSON.stringify(pageList));" +
                            "console.log('Pagination created correctly.');" +
                            "});";
                    loadEpub += "Book.ready.all.then(function(){ Book.generatePagination(); });";
                }

                loadEpub += "var rendered = Book.renderTo(\"area\");";

                webView.loadUrl(loadEpub);

                mCurrentWidthAndHeight = 80;
                if (mBook.getWidthAndHeight() != 0) {
                    mCurrentWidthAndHeight = mBook.getWidthAndHeight();
                }

                //webView.loadUrl(JavascriptUtils.getChangeWidthAndHeightFuncEm(mCurrentWidthAndHeight));
                webView.loadUrl(JavascriptUtils.getOnPageChangedFunc());
                webView.loadUrl(JavascriptUtils.getOnBookReadyFunc());

                if (file.exists()) {
                    try {
                        String loadPagination = "javascript: Book.loadPagination('" + IOUtils.toString(new FileInputStream(new File(getFilesDir().getPath() + "/" + mBook.getBookFileName() + ".json"))) + "');";
                        loadPagination += "Book.pageListReady.then(function(pageList){" +
                                "console.log('Pagination loaded correctly.');" +
                                "});";
                        webView.loadUrl(loadPagination);
                    } catch (IOException e) {
                        Log.e("LWEPUB", e.getMessage());
                    }
                }

                if (mBook.getFontSize() != 0) {
                    mCurrentFontSizePx = mBook.getFontSize();
                    mFontChangeSeekBar.setProgress(mCurrentFontSizePx - mMinFontSize);
                    ((WebView) mContentView).loadUrl(JavascriptUtils.getChangeFontSizeFuncPx(mCurrentFontSizePx));
                }

                if (mBook.getWidthAndHeight() != 0) {
                    mCurrentWidthAndHeight = mBook.getWidthAndHeight();
                    mMarginChangeSeekBar.setProgress(mCurrentWidthAndHeight - mMinWidthAndHeight);
                    ((WebView) mContentView).loadUrl(JavascriptUtils.getChangeWidthAndHeightFuncEm(mCurrentWidthAndHeight));
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

    //GUI Update methods
    public void setUIPageData(final String pcurrentpage,final String plastpage) {

        mCurrentPageTextView.post(new Runnable() {
            @Override
            public void run() {
                mCurrentPageTextView.setText(pcurrentpage + "/" + plastpage);
            }
        });
    }

    public void setSelectedText(String pselectedtext){
        try {
            mTranslationProvider.translateFromEnglishToSpanish(pselectedtext, this);
        }catch (IOException e){
            Log.e("LWEPUB",e.getMessage().toString());
        }
    }

    @Override
    public void setTranslationResponse(String pdata) {
        Intent intent = new Intent(this,TranslationActivity.class);
        intent.putExtra(TranslationActivity.TRANSLATION_CONTENT,pdata);
        startActivity(intent);
    }
}
