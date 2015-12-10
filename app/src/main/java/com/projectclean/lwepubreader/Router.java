package com.projectclean.lwepubreader;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import com.projectclean.lwepubreader.fragments.GenericFragment;
import com.projectclean.lwepubreader.fragments.MyLibraryFragment;

/**
 * Created by Carlos Albaladejo Pérez on 10/12/2015.
 */
public class Router {

    private ActionBarActivity mContext;

    public Router(ActionBarActivity pcontext){
        mContext = pcontext;
    }

    public void showMyLibrary(){
        MyLibraryFragment myLibrary = new MyLibraryFragment();

        Bundle arguments = new Bundle();
        arguments.putString(GenericFragment.FRAGMENT_NAME,"MYLIBRARY");
        arguments.putString(GenericFragment.TOOLBAR_TITLE,mContext.getString(R.string.mylibrary)); //<- strings file! different locales!!!

        myLibrary.setArguments(arguments);

        setFragment(myLibrary);
    }

    private void setFragment(Fragment pfragment){
        FragmentManager fragmentManager = mContext.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.main_fragment_container, pfragment);
        transaction.commit();
    }
}
