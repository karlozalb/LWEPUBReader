package com.projectclean.lwepubreader.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;

import com.projectclean.lwepubreader.R;
import com.projectclean.lwepubreader.fragments.EPUBRenderingFragment;

import java.util.LinkedList;

/**
 * Created by Carlos Albaladejo Pérez on 04/02/2016.
 */
public class BookPageFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private int mCount;
    private EPUBRenderingFragment mFragmentPage1,mFragmentPage2,mFragmentPage3;
    private FragmentManager mFragmentManager;

    private LinkedList<FragmentNode> mFragments;

    private int mPreviousPosition = 0,mCurrentPosition = 0;

    public BookPageFragmentPagerAdapter(FragmentManager fm) {
        super(fm);

        mFragments = new LinkedList<FragmentNode>();

        mFragmentManager = fm;
        mCount = 3;

        mFragmentPage1 = new EPUBRenderingFragment();
        mFragmentPage2 = new EPUBRenderingFragment();
        mFragmentPage3 = new EPUBRenderingFragment();

        /*Bundle params = new Bundle();
        params.putInt(EPUBRenderingFragment.LAYOUT_ID, R.layout.layout_fragment_epub_rendering_fragment);

        mFragmentPage1.setArguments(params);
        mFragmentPage2.setArguments(params);
        mFragmentPage3.setArguments(params);*/

        mFragments.add(new FragmentNode(0,mFragmentPage1));
        mFragments.add(new FragmentNode(1,mFragmentPage2));
        mFragments.add(new FragmentNode(2,mFragmentPage3));
    }

    @Override
    public Fragment getItem(int position) {

        FragmentNode fragmentNode = getFarthest(position);

        fragmentNode.INDEX = position;

        return fragmentNode.FRAGMENT;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    public void addPage(int pcurrentpage){
        if (pcurrentpage >= mCount - 1) {
            mCount++;
            notifyDataSetChanged();
        }
    }

    public FragmentNode getFarthest(int pposition){
        FragmentNode currentFragment;

        currentFragment = getFreeNode();

        if (currentFragment == null){
            int distance = 0;
            currentFragment = null;
            for (FragmentNode f : mFragments) {
                if (Math.abs(f.INDEX - pposition) > distance) {
                    distance = Math.abs(f.INDEX - pposition);
                    currentFragment = f;

                    FragmentTransaction ft = mFragmentManager.beginTransaction();
                    ft.remove(f.FRAGMENT);
                    ft.commit();
                }
            }
        }

        return currentFragment;
    }

    public FragmentNode getFreeNode(){
        for (FragmentNode f : mFragments){
            if (f.FREE){
                f.FREE = false;
                return f;
            }
        }
        return null;
    }



    private class FragmentNode{
        public int INDEX;
        public EPUBRenderingFragment FRAGMENT;
        public boolean FREE;

        public FragmentNode(int pindex, EPUBRenderingFragment pfragment){
            INDEX = pindex;
            FRAGMENT = pfragment;
            FREE = true;
        }
    }
}
