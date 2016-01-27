package com.projectclean.lwepubreader.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

import com.projectclean.lwepubreader.R;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Carlos Albaladejo Pérez on 20/01/2016.
 */
public class TranslationDialogFragment extends DialogFragment {

    public static String TRANSLATION_DIALOG_CONTENT = "T_DIALOG_CONTENT";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.layout_fragment_dialog_translation, null);

        if (getArguments() != null) initializeWebView((WebView)v,getArguments().getString(TRANSLATION_DIALOG_CONTENT));

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.translation_dialog_title)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }

    public void initializeWebView(WebView pwebview,String pcontent){
        String htmlWebSitePart1 = "",htmlWebSitePart2 = "";
        try {
            htmlWebSitePart1 = IOUtils.toString(new InputStreamReader(getActivity().getAssets().open("translation_page_skeleton_1.html")));
            htmlWebSitePart2 = IOUtils.toString(new InputStreamReader(getActivity().getAssets().open("translation_page_skeleton_2.html")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String fullWebSite = htmlWebSitePart1 + pcontent + htmlWebSitePart2;

        pwebview.loadDataWithBaseURL("file:///android_asset/", fullWebSite, "text/html", "UTF-8", null);

        pwebview.getSettings().setJavaScriptEnabled(true);
        pwebview.getSettings().setAllowFileAccess(true);
        pwebview.getSettings().setAllowContentAccess(true);
        pwebview.getSettings().setAllowFileAccessFromFileURLs(true);
        pwebview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        pwebview.setHorizontalScrollBarEnabled(false);
        pwebview.setVerticalScrollBarEnabled(false);
    }

}
