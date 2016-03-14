package com.projectclean.lwepubreader.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.ShareActionProvider;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;
import com.google.common.io.CharStreams;
import com.projectclean.lwepubreader.R;
import com.projectclean.lwepubreader.Router;
import com.projectclean.lwepubreader.adapters.BookPagePagerAdapter;
import com.projectclean.lwepubreader.adapters.TocListAdapter;
import com.projectclean.lwepubreader.io.FileUtils;
import com.projectclean.lwepubreader.model.Book;
import com.projectclean.lwepubreader.translation.ITranslationCallBack;
import com.projectclean.lwepubreader.translation.TranslationProvider;
import com.projectclean.lwepubreader.utils.DateTimeUtils;
import com.projectclean.lwepubreader.utils.EPUBDialogFactory;
import com.projectclean.lwepubreader.utils.JavascriptEPUBInterface;
import com.projectclean.lwepubreader.utils.JavascriptUtils;
import com.projectclean.lwepubreader.utils.NetworkUtils;
import com.projectclean.lwepubreader.utils.OnEPUBDialogClickListener;
import com.projectclean.lwepubreader.utils.ScreenUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    private View mControlsView,mTopControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            /*ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }*/
            mControlsView.setVisibility(View.VISIBLE);
            mTopControlsView.setVisibility(View.VISIBLE);
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
    private SeekBar mPercentageSelectorSlider;
    private TextView mCurrentPageTextView;
    private ViewPager mPager;
    //private BookPageFragmentPagerAdapter mPagerAdapter;
    private BookPagePagerAdapter mPagerAdapter;

    /* Sugar record model object */
    private Book mBook;

    /* Translation provider */
    private TranslationProvider mTranslationProvider;

    private float mCurrentPagePercentage;

    private ActionMode mActionMode;
    private ShareActionProvider mShareActionProvider;
    private Intent mShareIntent;

    /* Buttons */
    private ImageButton mNormalModeButton,mSepiaModeButton,mNightModeButton;

    /* Drawer list */
    private ListView mDrawerList;

    /* Current clock broadcastreceiver */
    BroadcastReceiver mTimeBroadcastReceiver;
    private final SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm");
    private TextView mTimeTextView;

    //Flag to avoid page change when selecting text on the WebView.
    private boolean mEnableOverEPUBControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_activity_epub);

        mTranslationProvider = new TranslationProvider(this);

        mContentView =  findViewById(R.id.fullscreen_content);
        /*mPagerAdapter = new BookPagePagerAdapter(this);
        mPager.setAdapter(mPagerAdapter);*/

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mTopControlsView =  findViewById(R.id.fullscreen_top_content_controls);
        //mContentView = findViewById(R.id.fullscreen_content);

        mFontChangeSeekBar = (SeekBar)findViewById(R.id.seekbar_font_change);
        mMarginChangeSeekBar = (SeekBar)findViewById(R.id.seekbar_margin_change);
        mPercentageSelectorSlider = (SeekBar)findViewById(R.id.percentage_selector_slider);

        mCurrentPageTextView = (TextView)findViewById(R.id.page_number_indicator);
        mTimeTextView = (TextView)findViewById(R.id.current_time_indicator);
        Calendar c = Calendar.getInstance();
        mTimeTextView.setText(mTimeFormat.format(c.getTime()));

        mTimeTextView.setVisibility((getSharedPreferences("dropbox-swiftreader",MODE_PRIVATE).getBoolean(ConfigurationActivity.SHOW_CLOCK,true))?View.VISIBLE:View.GONE);

        mNormalModeButton = (ImageButton)findViewById(R.id.button_style_normal_mode);
        mSepiaModeButton = (ImageButton)findViewById(R.id.button_style_sepia_mode);
        mNightModeButton = (ImageButton)findViewById(R.id.button_style_night_mode);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        TocListAdapter adapter = new TocListAdapter(this);
        mDrawerList.setAdapter(adapter);

        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TocListAdapter.TocNode node = (TocListAdapter.TocNode)mDrawerList.getAdapter().getItem(position);
                if (node != null){
                    ((WebView)mContentView).loadUrl("javascript: gotoHref('"+node.HREF+"');");
                }
            }
        });

        Bundle params = getIntent().getExtras();
        if (params != null){
            mEPUBPath = params.getString(EPUBPATHEXTRA);

            mBook = Book.find(Book.class, "BOOK_PATH = ?", mEPUBPath).get(0);

            mTranslationProvider.setConfiguration(mBook.getTranslationConfiguration());

            mBook.setDateLastRead(DateTimeUtils.getCurrentDate());
            if (mBook.getMostRecentOrder() != 1) {
                mBook.setMostRecentOrder(1);
                mBook.save();

                List<Book> books = Book.find(Book.class, "BOOK_PATH != ?", mBook.getBookPath());
                for (Book b : books) {
                    if (b.getMostRecentOrder() >= 0) {
                        b.setMostRecentOrder(b.getMostRecentOrder() + 1);
                        b.save();
                    }
                }
            }

            String htmlSite = "";

            try {
                //htmlSite = IOUtils.toString(new InputStreamReader(getAssets().open("epub_page_skeleton.html")));
                htmlSite = CharStreams.toString(new InputStreamReader(getAssets().open("epub_page_skeleton.html"),"UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            setWebViewConfiguration();

            ((WebView) mContentView).loadDataWithBaseURL("file:///android_asset/", htmlSite, "text/html", "UTF-8", null);
            //((WebView) mContentView).loadUrl("http://www.google.es");
        }

        mScreenWidth = ScreenUtils.getScreenWidth(this);

        setParameters();

        mFontChangeSeekBar.setMax((mMaxFontSize - mMinFontSize) / 2);
        mFontChangeSeekBar.setProgress((ScreenUtils.getDpFromPixels(this,Integer.parseInt(mBook.getFontSize())) - mMinFontSize) / 2);

        mMarginChangeSeekBar.setMax(mMaxWidthAndHeight - mMinWidthAndHeight);
        mMarginChangeSeekBar.setProgress(mBook.getMarginPercentage() - mMinWidthAndHeight);

        mPercentageSelectorSlider.setMax(100);

        initializeGUIComponentListeners();

        mTimeBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0)
                    mTimeTextView.setText(mTimeFormat.format(new Date()));
            }
        };

        mEnableOverEPUBControl = true;

        if (Math.random() <= 0.4f) {
            Appodeal.show(this, Appodeal.INTERSTITIAL);
        }
    }

    public void onStop(){
        super.onStop();
        if (mTimeBroadcastReceiver != null) unregisterReceiver(mTimeBroadcastReceiver);
    }

    private void initializeGUIComponentListeners() {

        //                                  FONT SIZE
        //***********************************************************************************
        mFontChangeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
                mCurrentFontSizePx = progress * 2 + mMinFontSize;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("LWEPUB", mBook.toString());
                //mBook.save();
                ((WebView) mContentView).loadUrl("javascript: setFontSize("+ScreenUtils.getPixelsFromDp(EPUBActivity.this,mCurrentFontSizePx)+");");
            }
        });

        //                                  MARGIN SIZE
        //***********************************************************************************
        mMarginChangeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
                mCurrentWidthAndHeight = progress + mMinWidthAndHeight;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("LWEPUB", "mCurrentWidthAndHeight: "+mCurrentWidthAndHeight);

                mBook.setMarginPercentage(mCurrentWidthAndHeight);
                mBook.save();
                int[] sizes = updateMargins();

                ((WebView) mContentView).loadUrl("javascript: setBookWidth("+sizes[0]+");setBookHeight("+sizes[1]+");");
            }
        });

        mPercentageSelectorSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);

                mCurrentPagePercentage = (float) progress / 100f;
                if (mCurrentPagePercentage <= 0) mCurrentPagePercentage = 0.001f;
                if (mCurrentPagePercentage >= 100) mCurrentPagePercentage = 1f;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("LWEPUB","mCurrentPagePercentage:"+mCurrentPagePercentage);
                ((WebView) mContentView).loadUrl("javascript: gotoPercentage(" + mCurrentPagePercentage + ");");
            }
        });

        mNormalModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((WebView)mContentView).loadUrl("javascript: setNormalMode();");
                mBook.setColorMode(Book.NORMAL_MODE);
                mBook.save();
            }
        });

        mSepiaModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((WebView)mContentView).loadUrl("javascript: setSepiaMode();");
                mBook.setColorMode(Book.SEPIA_MODE);
                mBook.save();
            }
        });

        mNightModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((WebView) mContentView).loadUrl("javascript: setNightMode();");
                mBook.setColorMode(Book.NIGHT_MODE);
                mBook.save();
            }
        });
    }

    private void setParameters(){
        mMinFontSize = getResources().getInteger(R.integer.min_font_size_px);
        mMaxFontSize = 24; //Originally 48

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
        //delayedHide(100);
        hide();
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
        mTopControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

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

        WebView.setWebContentsDebuggingEnabled(true);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);

        webView.setWebChromeClient(new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage pmessage) {
                Log.i("LWEPUB", pmessage.message());
                return true;
            }
        });

        webView.addJavascriptInterface(new JavascriptEPUBInterface(this, mBook), "Android");

        webView.setWebViewClient(new WebViewClient() {

                                     public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                         Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                         startActivity(i);
                                         return true;
                                     }

                                     public void onPageFinished(WebView view, String url) {
                                         webView.loadUrl("javascript:test();");
                                         int[] virtualSizes = updateMargins();
                                         if (mBook.getWidth() == null || mBook.getHeight() == null) {
                                             webView.loadUrl("javascript:loadBook('file:///" + mEPUBPath + "'," + mBook.getFontSize() + ",'" + mBook.getBookState() + "'," + virtualSizes[0] + "," + virtualSizes[1] + "," + mBook.getColorMode() + ");");
                                         } else {
                                             webView.loadUrl("javascript:setLocations('" + mBook.getLocations() + "');");
                                             try {
                                                 String pagination = CharStreams.toString(new InputStreamReader(FileUtils.getInstance(EPUBActivity.this).getInputStreamFromInternalStorage(mBook.getBookFileName() + ".json")));

                                                 //webView.loadUrl("javascript:setPagination('" + IOUtils.toString(FileUtils.getInstance(EPUBActivity.this).getInputStreamFromInternalStorage(mBook.getBookFileName() + ".json")) + "');");
                                                 webView.loadUrl("javascript:setPagination('" + pagination + "');");

                                             } catch (IOException e) {
                                                 e.printStackTrace();
                                             }
                                             webView.loadUrl("javascript:loadBook('file:///" + mEPUBPath + "'," + mBook.getFontSize() + ",'" + mBook.getBookState() + "'," + virtualSizes[0] + "," + virtualSizes[1] + "," + mBook.getColorMode() + ");");
                                         }
                                     }
                                 }
        );

        webView.setOnTouchListener(new View.OnTouchListener() {

            private float mStartX, mEndX;
            private float mStartY, mEndY;

            public boolean onTouch(View v, MotionEvent event) {

                if (mEnableOverEPUBControl) {

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
                            } /*else {
                            if (offsetYAbs > 50) {
                                if (offsetY < 0) {
                                    mCurrentFontSizePx -= 2;
                                } else if (offsetY > 0) {
                                    mCurrentFontSizePx += 2;
                                }
                                webView.loadUrl(JavascriptUtils.getChangeFontSizeFuncPx(mCurrentFontSizePx));
                            }
                        }*/
                        }
                    }
                }

                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
    }

    //GUI Update methods
    public void setUIPageData(final String pcurrentpage,final String plastpage) {
        try {
            int currentPage = Integer.parseInt(pcurrentpage);

            if (currentPage == -1) return;

            int lastpage = Integer.parseInt(plastpage);

            mBook.setBookCompletion((float)currentPage/(float)lastpage);
            mBook.save();

            mCurrentPageTextView.post(new Runnable() {
                @Override
                public void run() {
                    mCurrentPageTextView.setText(pcurrentpage + "/" + plastpage);
                }
            });

        }catch (NumberFormatException exception){
            //Undefined detected somewhere!
        }
    }

    public void setSelectedText(String pselectedtext){
        try {
            mTranslationProvider.translate(pselectedtext, this);

            /*mTranslationProvider.setConfiguration(0);
            mTranslationProvider.translate("hola", this);
            mTranslationProvider.setConfiguration(1);
            mTranslationProvider.translate("hola", this);
            mTranslationProvider.setConfiguration(2);
            mTranslationProvider.translate("hola", this);
            mTranslationProvider.setConfiguration(3);
            mTranslationProvider.translate("hello", this);
            mTranslationProvider.setConfiguration(4);
            mTranslationProvider.translate("hello", this);
            mTranslationProvider.setConfiguration(5);
            mTranslationProvider.translate("hello", this);
            mTranslationProvider.setConfiguration(6);
            mTranslationProvider.translate("hello", this);
            mTranslationProvider.setConfiguration(7);
            mTranslationProvider.translate("hello", this);
            mTranslationProvider.setConfiguration(8);
            mTranslationProvider.translate("bonjour", this);
            mTranslationProvider.setConfiguration(9);
            mTranslationProvider.translate("bonjour", this);
            mTranslationProvider.setConfiguration(10);
            mTranslationProvider.translate("prego", this);
            mTranslationProvider.setConfiguration(11);
            mTranslationProvider.translate("danke", this);
            mTranslationProvider.setConfiguration(12);
            mTranslationProvider.translate("olá", this);*/

        }catch (IOException e){
            Log.e("LWEPUB",e.getMessage().toString());
        }
    }

    public void setSelectedTextForSharing(String pselectedtext,String ptitle,String pauthor){
        if (mShareActionProvider != null) {
            mShareIntent = new Intent(Intent.ACTION_SEND);
            mShareIntent.setType("text/plain");
            mShareIntent.putExtra(Intent.EXTRA_TEXT, "\"" + pselectedtext + "\" - " + ptitle);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mShareActionProvider.setShareIntent(mShareIntent);
                }
            });
        }
    }

    public void onResume(){
        super.onResume();
        registerReceiver(mTimeBroadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void setTranslationResponse(String pdata) {
        Log.i("LWEPUB", pdata);

        Router.showTranslationFragmentDialog(this, pdata);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int sizes[] = updateMargins();
        ((WebView) mContentView).loadUrl("javascript: setBookWidthAndHeight(" + sizes[0] + "," + sizes[1] + ");");
        //((WebView) mContentView).loadUrl("javascript: setBookWidth(" + sizes[0] + ");setBookHeight(" + sizes[1] + ");");

        // Checks the orientation of the screen
        /*if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Toast.makeText(this, ScreenUtils.getScreenWidth(this)+"x"+ScreenUtils.getScreenHeight(this), Toast.LENGTH_SHORT).show();
            ((WebView) mContentView).loadUrl("javascript: setMultiColumn();");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            //Toast.makeText(this, ScreenUtils.getScreenWidth(this)+"x"+ScreenUtils.getScreenHeight(this), Toast.LENGTH_SHORT).show();
            ((WebView) mContentView).loadUrl("javascript: setOneColumn();");
        }*/
    }

    public int[] updateMargins(){
        int margin = mBook.getMarginPercentage();

        int screenWidth =  ScreenUtils.getScreenWidth(this);
        int screenHeight =  ScreenUtils.getScreenHeight(this);

        int virtualWidth = (int)((margin / 100f) * screenWidth);
        int virtualHeight = (int)((margin / 100f) * screenHeight);

        if (virtualWidth % 2 != 0) virtualWidth++;
        if (virtualHeight % 2 != 0) virtualHeight++;

        int[] ret = new int[2];
        ret[0] = virtualWidth;
        ret[1] = virtualHeight;

        return ret;
    }

    public Book getCurrentBook(){
        return mBook;
    }

    public String getCurrentBookPath(){
        return mEPUBPath;
    }

    public void onSupportActionModeStarted(ActionMode mode){
        Menu menu = mode.getMenu();
        menu.clear();

        mEnableOverEPUBControl = false;

        getMenuInflater().inflate(R.menu.webview_contextual_action_menu, menu);

        SupportMenuItem shareItem = (SupportMenuItem)menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider)MenuItemCompat.getActionProvider(shareItem);

        mShareIntent = new Intent(Intent.ACTION_SEND);
        mShareIntent.setType("text/plain");
        mShareIntent.putExtra(Intent.EXTRA_TEXT, "");

        mShareActionProvider.setShareIntent(mShareIntent);

        menu.findItem(R.id.translate_action_btn).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (NetworkUtils.isNetworkAvailable(EPUBActivity.this)){
                    ((WebView) mContentView).loadUrl("javascript: getSelectedText();");
                }else{
                    EPUBDialogFactory.createAndShowAlertDialogNoNegativeButton(EPUBActivity.this, "Oops...", getString(R.string.no_internet_error), new OnEPUBDialogClickListener() {
                        @Override
                        public void onPositiveButtonClick() {

                        }

                        @Override
                        public void onNegativeButtonClick() {

                        }
                    });
                }
                return true;
            }
        });
    }

    public void onSupportActionModeFinished(ActionMode mode){
        super.onSupportActionModeFinished(mode);

        mEnableOverEPUBControl = true;
    }

    public void setCurrentPageProgressBar(float pcurrentprogress){
        Log.i("LWEPUB","setCurrentPageProgressBar "+pcurrentprogress);
        mPercentageSelectorSlider.setProgress((int)pcurrentprogress);
    }

    public void setToc(final String ptoc){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TocListAdapter adapter = new TocListAdapter(EPUBActivity.this);
                adapter.parseJSON(ptoc);

                mDrawerList.setAdapter(adapter);
            }
        });
    }

}
