package com.projectclean.lwepubreader.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by Carlos Albaladejo Pérez on 05/12/2015.
 */
public class GenericFragment extends Fragment {

    protected String mFragmentName;
    protected String mToolbarTitle;
    protected int mLayoutID;

    //Fragment params TAGS
    public static String FRAGMENT_NAME = "F_NAME";
    public static String TOOLBAR_TITLE = "F_TOOLBAR_TITLE";
    public static String LAYOUT_ID = "F_LAYOUT_ID";

    protected void setFragmentParams(){
        Bundle b = getArguments();

        if (b != null){
            mFragmentName = b.getString(FRAGMENT_NAME);
            mToolbarTitle = b.getString(TOOLBAR_TITLE);
            mLayoutID = b.getInt(LAYOUT_ID);
        }
    }

}
