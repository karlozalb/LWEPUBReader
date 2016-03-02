package com.projectclean.lwepubreader;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.dropbox.core.v2.files.FileMetadata;
import com.projectclean.lwepubreader.activities.BookDetailsActivity;
import com.projectclean.lwepubreader.activities.ConfigurationActivity;
import com.projectclean.lwepubreader.activities.EPUBActivity;
import com.projectclean.lwepubreader.fragments.FileChooserDialogFragment;
import com.projectclean.lwepubreader.fragments.GenericFragment;
import com.projectclean.lwepubreader.fragments.MyLibraryFragment;
import com.projectclean.lwepubreader.fragments.ProgressDialogFragment;
import com.projectclean.lwepubreader.fragments.SpinnerDialogFragment;
import com.projectclean.lwepubreader.fragments.TranslationDialogFragment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Carlos Albaladejo Pérez on 10/12/2015.
 */
public class Router {

    private AppCompatActivity mContext;

    public static String MY_LIBRARY_TAG = "T_MYLIBRARY";
    public static String TRANSLATION_DIALOG_TAG = "T_TRANSLATION_DIALOG_TAG";
    public static String FILE_CHOOSER_DIALOG_TAG = "T_FILE_CHOOSER_DIALOG_TAG";
    public static String PROGRESS_DIALOG_TAG = "T_PROGRESS_DIALOG_TAG";


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

    public static void showBookDetails(Activity pcontext,long pbookid){
        Intent intent = new Intent(pcontext, BookDetailsActivity.class);
        intent.putExtra(BookDetailsActivity.EXTRA_ID, pbookid);
        pcontext.startActivity(intent);
    }

    public static void showConfiguration(Activity pcontext){
        Intent intent = new Intent(pcontext, ConfigurationActivity.class);
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

        setFragment(pcontext, myLibrary, R.id.main_fragment_container, MY_LIBRARY_TAG);
    }

    public static void showTranslationFragmentDialog(Activity pcontext,String pfragmentcontent){
        TranslationDialogFragment tDialogFragment = new TranslationDialogFragment();

        Bundle arguments = new Bundle();
        arguments.putString(TranslationDialogFragment.TRANSLATION_DIALOG_CONTENT, pfragmentcontent);

        tDialogFragment.setArguments(arguments);

        tDialogFragment.show(((AppCompatActivity) pcontext).getSupportFragmentManager(), TRANSLATION_DIALOG_TAG);
    }

    public static void showFileChooserFragmentDialog(Activity pcontext,LinkedList<String> pepublist){
        FileChooserDialogFragment tDialogFragment = new FileChooserDialogFragment();

        Bundle arguments = new Bundle();

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.addAll(pepublist);
        arguments.putStringArrayList(FileChooserDialogFragment.EPUB_LIST, arrayList);
        arguments.putBoolean(FileChooserDialogFragment.IS_DROPBOX, false);
        tDialogFragment.setArguments(arguments);

        tDialogFragment.show(((AppCompatActivity) pcontext).getSupportFragmentManager(), FILE_CHOOSER_DIALOG_TAG);
    }

    public static void showDropboxFileChooserFragmentDialog(Activity pcontext,List<FileMetadata> pepublist){
        FileChooserDialogFragment tDialogFragment = new FileChooserDialogFragment();

        Bundle arguments = new Bundle();

        ArrayList<String> arrayList = new ArrayList<>();
        for (FileMetadata fm : pepublist){
            arrayList.add(fm.toJson(false));
        }
        arguments.putStringArrayList(FileChooserDialogFragment.EPUB_LIST, arrayList);
        arguments.putBoolean(FileChooserDialogFragment.IS_DROPBOX, true);
        tDialogFragment.setArguments(arguments);

        tDialogFragment.show(((AppCompatActivity)pcontext).getSupportFragmentManager(),FILE_CHOOSER_DIALOG_TAG);
    }

    public static ProgressDialogFragment showLoadingDialog(Activity pcontext){
        ProgressDialogFragment tDialogFragment = new ProgressDialogFragment();

        tDialogFragment.show(((AppCompatActivity)pcontext).getSupportFragmentManager(),PROGRESS_DIALOG_TAG);

        return tDialogFragment;
    }

    public static SpinnerDialogFragment showSpinnerLoadingDialog(Activity pcontext,String pmessage){
        SpinnerDialogFragment tDialogFragment = new SpinnerDialogFragment();

        Bundle b = new Bundle();
        b.putString(Intent.EXTRA_TEXT,pmessage);

        tDialogFragment.setArguments(b);

        tDialogFragment.show(((AppCompatActivity)pcontext).getSupportFragmentManager(),PROGRESS_DIALOG_TAG);

        return tDialogFragment;
    }

    private static void setFragment(Activity pcontext,Fragment pfragment,int pcontainerid,String pfragmenttag){
        FragmentManager fragmentManager = ((AppCompatActivity)pcontext).getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(pcontainerid, pfragment,pfragmenttag);
        transaction.commit();
    }
}
