package com.projectclean.lwepubreader;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.appodeal.ads.Appodeal;
import com.dropbox.core.v2.files.FileMetadata;
import com.orm.SugarContext;
import com.projectclean.lwepubreader.adapters.CustomFragmentPagerAdapter;
import com.projectclean.lwepubreader.dropbox.DropboxHelper;
import com.projectclean.lwepubreader.fragments.MostRecentFragment;
import com.projectclean.lwepubreader.fragments.MyLibraryFragment;
import com.projectclean.lwepubreader.model.Book;
import com.projectclean.lwepubreader.utils.DateTimeUtils;
import com.projectclean.lwepubreader.utils.EPUBDialogFactory;
import com.projectclean.lwepubreader.utils.NetworkUtils;
import com.projectclean.lwepubreader.utils.OnEPUBDialogClickListener;

import java.util.ArrayList;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    private ViewPager mViewPager;
    private CustomFragmentPagerAdapter mCustomPageAdapter;
    private Toolbar mToolBar;
    private DropboxHelper mDropboxHelper;

    private boolean mTokenNeeded;
    private int mCurrentSelectedIndex;
    private Book mCurrentSelectedBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SugarContext.init(this);

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

        mDropboxHelper = new DropboxHelper(this);
    }

    public void onStart(){
        super.onStart();

    }

    public void onResume(){
        super.onResume();
        updateLists();

        Appodeal.onResume(this, Appodeal.BANNER);

        if (mTokenNeeded) {
            mTokenNeeded = false;
            initClient();
        }
    }

    public void onPause(){
        super.onPause();
        if (mDropboxHelper.isThereATaskInProcess()){
            mDropboxHelper.cancelTask();
        }
    }

    public void onTerminate(){
        SugarContext.terminate();
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
        }else if (id == R.id.action_dropbox) {
            if (NetworkUtils.isNetworkAvailable(this)) {
                if (mDropboxHelper.hasToken()) {
                    initClient();
                } else {
                    EPUBDialogFactory.createAndShowAlertDialog(this, getString(R.string.import_from_dropbox_dialog_title), getString(R.string.dropbox_terms_message), new OnEPUBDialogClickListener() {
                        @Override
                        public void onPositiveButtonClick() {
                            if (mDropboxHelper.hasToken()) {
                                initClient();
                            } else {
                                mDropboxHelper.authenticate();
                                mTokenNeeded = true;
                            }
                        }

                        @Override
                        public void onNegativeButtonClick() {

                        }
                    });
                }
            }else {
                EPUBDialogFactory.createAndShowAlertDialogNoNegativeButton(this, "Oops...", getString(R.string.no_internet_error), new OnEPUBDialogClickListener() {
                    @Override
                    public void onPositiveButtonClick() {

                    }

                    @Override
                    public void onNegativeButtonClick() {

                    }
                });
            }
        }else if (id == R.id.action_settings){
            Router.showConfiguration(this);
        }else if (id == R.id.about_us){
            Router.showAboutUsDetails(this);
        }


        return super.onOptionsItemSelected(item);
    }

    public void importSelectedBooks(ArrayList<String> pepubs){
        mCustomPageAdapter.getMyLibraryFragment().importSelectedBooks(pepubs);
    }

    public void importSelectedBooksFromDropbox(LinkedList<FileMetadata> pepubs){
        mCustomPageAdapter.getMyLibraryFragment().importSelectedBooksFromDropbox(pepubs, mDropboxHelper);
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
                showSnackBarMessage(getString(R.string.book_moved_to_read), Snackbar.LENGTH_SHORT);
                return true;
            case R.id.context_menu_delete:
                b.delete();
                updateLists();
                showSnackBarMessage(getString(R.string.book_deleted), Snackbar.LENGTH_SHORT);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu_view_details:
                Router.showBookDetails(this, mCurrentSelectedBook.getId());
                return true;
            case R.id.context_menu_mask_as_read:
                mCurrentSelectedBook.setRead(true);
                mCurrentSelectedBook.save();
                updateLists();
                showSnackBarMessage(getString(R.string.book_moved_to_read), Snackbar.LENGTH_SHORT);
                return true;
            case R.id.context_menu_delete:
                mCurrentSelectedBook.delete();
                updateLists();
                showSnackBarMessage(getString(R.string.book_deleted), Snackbar.LENGTH_SHORT);
                return true;
            default:
                return false;
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

    public void initClient(){
        mDropboxHelper.getToken();
        mDropboxHelper.initClient(Router.showSpinnerLoadingDialog(MainActivity.this, getString(R.string.import_from_dropbox_dialog_title)));
    }

    public void showSnackBarMessage(String pmessage,int pduration){
        Snackbar snackbar = Snackbar.make(findViewById(R.id.tab_layout), pmessage, pduration);

        View snackbarView = snackbar.getView();

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)snackbarView.getLayoutParams();
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        snackbarView.setLayoutParams(params);

        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(getResources().getColor(R.color.pcg_orange));
        snackbar.show();
    }

    public void showPopupMenu(int pindex,Book pbook,View panchorview){
        mCurrentSelectedIndex = pindex;
        mCurrentSelectedBook = pbook;

        PopupMenu popup = new PopupMenu(this, panchorview);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.listview_context_menu, popup.getMenu());
        popup.show();
    }

}
