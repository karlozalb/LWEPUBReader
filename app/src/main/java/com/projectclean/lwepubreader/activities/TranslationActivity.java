package com.projectclean.lwepubreader.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.projectclean.lwepubreader.R;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Carlos Albaladejo Pérez on 19/01/2016.
 */
public class TranslationActivity extends AppCompatActivity{

    private WebView mWebView;

    public static final String TRANSLATION_CONTENT = "T_CONTENT";

    private String mHtmlContent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);

        mWebView = (WebView)findViewById(R.id.translation_webview);

        Bundle b = getIntent().getExtras();

        if (b != null){
            mHtmlContent = b.getString(TRANSLATION_CONTENT);
        }

        String htmlWebSitePart1 = "",htmlWebSitePart2 = "";
        try {
            htmlWebSitePart1 = IOUtils.toString(new InputStreamReader(getAssets().open("translation_page_skeleton_1.html")));
            htmlWebSitePart2 = IOUtils.toString(new InputStreamReader(getAssets().open("translation_page_skeleton_2.html")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String fullWebSite = htmlWebSitePart1 + mHtmlContent + htmlWebSitePart2;

        mWebView.loadDataWithBaseURL("file:///android_asset/", fullWebSite, "text/html", "UTF-8", null);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowContentAccess(true);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setVerticalScrollBarEnabled(false);

        /*mWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                if (mHtmlContent != null && mHtmlContent.length() > 0) mWebView.loadUrl("javascript:populateMain('hola');");
            }
        });*/

    }
}
