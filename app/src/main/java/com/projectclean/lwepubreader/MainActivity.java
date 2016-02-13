package com.projectclean.lwepubreader;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

import com.projectclean.lwepubreader.adapters.CustomFragmentPagerAdapter;


public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private CustomFragmentPagerAdapter mCustomPageAdapter;
    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_main_viewpager);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);

        mCustomPageAdapter = new CustomFragmentPagerAdapter(this,getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mCustomPageAdapter);

        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabTextColors(R.color.skull_white,R.color.skull_white);

        //Removes action bar shadow!!
        //getSupportActionBar().setElevation(0);

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        //If you want to set your new ActionBar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        /*toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setLogo(R.mipmap.ic_launcher);*/

        Spannable spannable = (Spannable)((TextView)findViewById(R.id.toolbar_title_tv)).getText();

        StyleSpan boldSpanEPUB = new StyleSpan( Typeface.BOLD );
        ForegroundColorSpan colorSpanREADER = new ForegroundColorSpan(getResources().getColor(R.color.pcg_orange));

        spannable.setSpan(boldSpanEPUB, 0, 11, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        spannable.setSpan(colorSpanREADER, 5, 11, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
    }

    public void onResume(){
        super.onResume();
        updateLists();
    }

    public void updateLists(){
        mCustomPageAdapter.updateFragmentLists();
    }
}
