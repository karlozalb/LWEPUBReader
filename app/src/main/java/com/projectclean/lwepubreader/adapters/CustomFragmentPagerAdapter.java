package com.projectclean.lwepubreader.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.projectclean.lwepubreader.R;
import com.projectclean.lwepubreader.fragments.GenericFragment;
import com.projectclean.lwepubreader.fragments.MyLibraryFragment;

/**
 * Created by Carlos Albaladejo Pérez on 27/01/2016.
 */
public class CustomFragmentPagerAdapter extends FragmentPagerAdapter {

    public static final int MOST_RECENT_BOOKS = 0,MY_LIBRARY_INDEX = 1, READ_BOOKS_INDEX = 2;

    private Activity mActivity;

    public CustomFragmentPagerAdapter(Activity pactivity,FragmentManager fm){
        super(fm);
        mActivity = pactivity;
    }

    @Override
    public Fragment getItem(int position) {

        if (position == MOST_RECENT_BOOKS){
            MyLibraryFragment myLibrary = new MyLibraryFragment();

            Bundle arguments = new Bundle();
            arguments.putString(GenericFragment.FRAGMENT_NAME,"MYLIBRARY");
            arguments.putString(GenericFragment.TOOLBAR_TITLE, mActivity.getString(R.string.mylibrary)); //<- strings file! different locales!!!
            arguments.putString(MyLibraryFragment.FRAGMENT_QUERY,"SELECT * FROM BOOK WHERE MOST_RECENT_ORDER>=0 AND MOST_RECENT_ORDER <="+MyLibraryFragment.MOST_RECENT_LIMIT+";");

            myLibrary.setArguments(arguments);

            return myLibrary;
        }else if (position == MY_LIBRARY_INDEX){
            MyLibraryFragment myLibrary = new MyLibraryFragment();

            Bundle arguments = new Bundle();
            arguments.putString(GenericFragment.FRAGMENT_NAME,"MYLIBRARY");
            arguments.putString(GenericFragment.TOOLBAR_TITLE, mActivity.getString(R.string.mylibrary)); //<- strings file! different locales!!!
            arguments.putString(MyLibraryFragment.FRAGMENT_QUERY,"SELECT * FROM BOOK WHERE READ = 0;");

            myLibrary.setArguments(arguments);

            return myLibrary;
        }else if (position == READ_BOOKS_INDEX){
            MyLibraryFragment myLibrary = new MyLibraryFragment();

            Bundle arguments = new Bundle();
            arguments.putString(GenericFragment.FRAGMENT_NAME,"MYLIBRARY");
            arguments.putString(GenericFragment.TOOLBAR_TITLE, mActivity.getString(R.string.mylibrary)); //<- strings file! different locales!!!
            arguments.putString(MyLibraryFragment.FRAGMENT_QUERY,"SELECT * FROM BOOK WHERE READ = 1;");

            myLibrary.setArguments(arguments);

            return myLibrary;
        }

        return null;
    }

    public CharSequence getPageTitle (int position){
        if (position == MY_LIBRARY_INDEX){
            return mActivity.getString(R.string.mylibrary);
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
