package com.projectclean.lwepubreader.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.Spinner;

import com.projectclean.lwepubreader.R;
import com.projectclean.lwepubreader.adapters.CustomSpinnerAdapter;

/**
 * Created by Carlos Albaladejo Pérez on 02/03/2016.
 */
public class AboutUsActivity extends AppCompatActivity{

    private Toolbar mToolBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_about_us_activity);

        mToolBar = (Toolbar) findViewById(R.id.configuration_toolbar);
        setSupportActionBar(mToolBar);
        mToolBar.setTitle(getString(R.string.about_us));
    }


}
