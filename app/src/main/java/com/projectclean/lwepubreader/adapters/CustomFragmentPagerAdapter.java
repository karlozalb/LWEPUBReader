package com.projectclean.lwepubreader.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.projectclean.lwepubreader.R;
import com.projectclean.lwepubreader.fragments.GenericFragment;
import com.projectclean.lwepubreader.fragments.MostRecentFragment;
import com.projectclean.lwepubreader.fragments.MyLibraryFragment;
import com.projectclean.lwepubreader.fragments.ReadFragment;

import java.util.LinkedList;

/**
 * Created by Carlos Albaladejo Pérez on 27/01/2016.
 */
public class CustomFragmentPagerAdapter extends FragmentPagerAdapter {

    public static final int MOST_RECENT_BOOKS = 0,MY_LIBRARY_INDEX = 1, READ_BOOKS_INDEX = 2;

    private LinkedList<MyLibraryFragment> mTabbedFragments;

    private Activity mActivity;

    private int mCurrentTab;

    public CustomFragmentPagerAdapter(Activity pactivity,FragmentManager fm){
        super(fm);
        mActivity = pactivity;

        mTabbedFragments = new LinkedList<MyLibraryFragment>();
    }

    @Override
    public Fragment getItem(int position) {

        MyLibraryFragment myLibrary = null;

        if (position == MOST_RECENT_BOOKS){
            myLibrary = new MostRecentFragment();

            Bundle arguments = new Bundle();
            arguments.putString(GenericFragment.FRAGMENT_NAME,"MOSTRECENT");
            arguments.putString(GenericFragment.TOOLBAR_TITLE, mActivity.getString(R.string.most_recent_tab_title)); //<- strings file! different locales!!!
            arguments.putString(MyLibraryFragment.FRAGMENT_QUERY, "SELECT * FROM BOOK WHERE MOST_RECENT_ORDER >= 0 ORDER BY MOST_RECENT_ORDER AND DELETED = 0 ASC LIMIT "+MyLibraryFragment.MOST_RECENT_LIMIT+";");
            arguments.putInt(GenericFragment.LAYOUT_ID,R.layout.layout_fragment_most_recent);

            myLibrary.setArguments(arguments);
        }else if (position == MY_LIBRARY_INDEX){
            myLibrary = new MyLibraryFragment();

            Bundle arguments = new Bundle();
            arguments.putString(GenericFragment.FRAGMENT_NAME,"MYLIBRARY");
            arguments.putString(GenericFragment.TOOLBAR_TITLE, mActivity.getString(R.string.mylibrary_tab_title)); //<- strings file! different locales!!!
            arguments.putString(MyLibraryFragment.FRAGMENT_QUERY,"SELECT * FROM BOOK WHERE READ = 0 AND DELETED = 0 ;");
            arguments.putInt(GenericFragment.LAYOUT_ID, R.layout.layout_fragment_mylibrary);

            myLibrary.setArguments(arguments);
        }else if (position == READ_BOOKS_INDEX){
            myLibrary = new ReadFragment();

            Bundle arguments = new Bundle();
            arguments.putString(GenericFragment.FRAGMENT_NAME,"READBOOKS");
            arguments.putString(GenericFragment.TOOLBAR_TITLE, mActivity.getString(R.string.read_tab_title)); //<- strings file! different locales!!!
            arguments.putString(MyLibraryFragment.FRAGMENT_QUERY,"SELECT * FROM BOOK WHERE READ = 1 AND DELETED = 0 ;");
            arguments.putInt(GenericFragment.LAYOUT_ID, R.layout.layout_fragment_read);

            myLibrary.setArguments(arguments);
        }

        mTabbedFragments.add(myLibrary);

        return myLibrary;
    }

    public void setActiveTab(int pposition){
        mCurrentTab = pposition;
    }

    public MyLibraryFragment getActiveFragment(){
        return mTabbedFragments.get(mCurrentTab);
    }

    public MyLibraryFragment getMyLibraryFragment(){
        return mTabbedFragments.get(1);
    }

    public void updateFragmentLists(){
        for (MyLibraryFragment f : mTabbedFragments){
            f.loadCurrentLibrary();
        }
    }

    public CharSequence getPageTitle (int position){
        if (position == MY_LIBRARY_INDEX){
            return mActivity.getString(R.string.mylibrary_tab_title);
        }else if (position == READ_BOOKS_INDEX){
            return mActivity.getString(R.string.read_tab_title);
        }else if (position == MOST_RECENT_BOOKS){
            return mActivity.getString(R.string.most_recent_tab_title);
        }

        return "noname";
    }

    @Override
    public int getCount() {
        return 3;
    }
}
