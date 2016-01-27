package com.projectclean.lwepubreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;

import com.projectclean.lwepubreader.activities.EPUBActivity;
import com.projectclean.lwepubreader.fragments.GenericFragment;
import com.projectclean.lwepubreader.fragments.MyLibraryFragment;
import com.projectclean.lwepubreader.fragments.TranslationDialogFragment;

/**
 * Created by Carlos Albaladejo Pérez on 10/12/2015.
 */
public class Router {

    private AppCompatActivity mContext;

    public static String MY_LIBRARY_TAG = "T_MYLIBRARY";
    public static String TRANSLATION_DIALOG_TAG = "T_TRANSLATION_DIALOG_TAG";

    public Router(AppCompatActivity pcontext){
        mContext = pcontext;
    }

     /* ***************************************************************
                                ACTIVITIES
    *************************************************************** */

    public static void showEPUB(Activity pcontext,String pepubpath){
        Intent intent = new Intent(pcontext, EPUBActivity.class);
        intent.putExtra(EPUBActivity.EPUBPATHEXTRA,pepubpath);
        pcontext.startActivity(intent);
    }

    /* ***************************************************************
                                FRAGMENTS
    *************************************************************** */

    public static void showMyLibrary(Activity pcontext){
        MyLibraryFragment myLibrary = new MyLibraryFragment();

        Bundle arguments = new Bundle();
        arguments.putString(GenericFragment.FRAGMENT_NAME,"MYLIBRARY");
        arguments.putString(GenericFragment.TOOLBAR_TITLE,pcontext.getString(R.string.mylibrary)); //<- strings file! different locales!!!

        myLibrary.setArguments(arguments);

        setFragment(pcontext,myLibrary,R.id.main_fragment_container,MY_LIBRARY_TAG);
    }

    public static void showTranslationFragmentDialog(Activity pcontext,String pfragmentcontent){
        TranslationDialogFragment tDialogFragment = new TranslationDialogFragment();

        Bundle arguments = new Bundle();
        arguments.putString(TranslationDialogFragment.TRANSLATION_DIALOG_CONTENT, pfragmentcontent);

        tDialogFragment.setArguments(arguments);

        tDialogFragment.show(((AppCompatActivity)pcontext).getSupportFragmentManager(),TRANSLATION_DIALOG_TAG);
    }

    private static void setFragment(Activity pcontext,Fragment pfragment,int pcontainerid,String pfragmenttag){
        FragmentManager fragmentManager = ((AppCompatActivity)pcontext).getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(pcontainerid, pfragment,pfragmenttag);
        transaction.commit();
    }
}
