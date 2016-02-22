package com.projectclean.lwepubreader;

import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.appodeal.ads.Appodeal;
import com.projectclean.lwepubreader.adapters.CustomFragmentPagerAdapter;
import com.projectclean.lwepubreader.fragments.MyLibraryFragment;
import com.projectclean.lwepubreader.model.Book;
import com.projectclean.lwepubreader.utils.DateTimeUtils;
import com.projectclean.lwepubreader.utils.EPUBDialogFactory;
import com.projectclean.lwepubreader.utils.OnEPUBDialogClickListener;

import java.util.LinkedList;


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
        tabLayout.setTabTextColors(R.color.skull_white, R.color.skull_white);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i("LWEPUB","position:"+position);
                mCustomPageAdapter.setActiveTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        //If you want to set your new ActionBar
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        DateTimeUtils.getCurrentDate();

        initializeAppodeal();
    }

    public void onResume(){
        super.onResume();
        updateLists();

        Appodeal.onResume(this, Appodeal.BANNER);
    }

    public void updateLists(){
        mCustomPageAdapter.updateFragmentLists();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_deleteall) {
            EPUBDialogFactory.createAndShowAlertDialog(this, getString(R.string.delete_all_dialog_title), getString(R.string.delete_all_dialog_message), new OnEPUBDialogClickListener() {
                @Override
                public void onPositiveButtonClick() {
                    Book.deleteAll(Book.class);
                    updateLists();
                }

                @Override
                public void onNegativeButtonClick() {

                }
            });

        }
        return super.onOptionsItemSelected(item);
    }

    public void importSelectedBooks(LinkedList<String> pepubs){
        mCustomPageAdapter.getMyLibraryFragment().importSelectedBooks(pepubs);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        MyLibraryFragment fragment = mCustomPageAdapter.getActiveFragment();

        Book b = fragment.getSelectedItem();

        switch (item.getItemId()) {
            case R.id.context_menu_view_details:
                Router.showBookDetails(this,b.getId());
                return true;
            case R.id.context_menu_mask_as_read:
                b.setRead(true);
                b.save();
                updateLists();
                return true;
            case R.id.context_menu_delete:
                //Log.i("LWEPUB","mCurrentLongClickSelectedItem:"+mCurrentLongClickSelectedItem);
                b.delete();
                //Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.main_fragment_container), "Welcome to AndroidHive", Snackbar.LENGTH_LONG);

                //snackbar.show();
                //b.setDeleted(true);
                //b.save();
                updateLists();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void initializeAppodeal(){
        String appKey = "9c21799df265533d00f21aadda35dd30707f69227d5503a2";
        Appodeal.disableLocationPermissionCheck();
        //Appodeal.setTesting(true);
        Appodeal.setBannerViewId(R.id.appodealBannerView);
        Appodeal.initialize(this, appKey, Appodeal.INTERSTITIAL | Appodeal.BANNER);
        Appodeal.show(this, Appodeal.BANNER_VIEW);
    }
}
