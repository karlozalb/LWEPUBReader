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
public class ConfigurationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Spinner mDefaultLanguage;
    private Spinner mDefaultColor;
    private CheckedTextView mShowClockCheckedTextView;
    private Toolbar mToolBar;

    private SharedPreferences mPrefs;

    public static String DEFAULT_LANGUAGE = "D_LANGUAGE", DEFAULT_COLOR = "D_COLOR", SHOW_CLOCK = "S_CLOCK";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_configuration_activity);

        mPrefs = getSharedPreferences("dropbox-swiftreader", MODE_PRIVATE);

        mDefaultLanguage = (Spinner)findViewById(R.id.main_configuration_languages_list);
        mDefaultColor = (Spinner)findViewById(R.id.default_color_list);
        mShowClockCheckedTextView = (CheckedTextView)findViewById(R.id.configuration_show_clock);

        mShowClockCheckedTextView.setChecked(mPrefs.getBoolean(SHOW_CLOCK, true));

        CustomSpinnerAdapter languageAdapter = new CustomSpinnerAdapter(this);
        languageAdapter.setItems(getResources().getStringArray(R.array.languages_list));
        mDefaultLanguage.setAdapter(languageAdapter);
        languageAdapter.notifyDataSetChanged();

        mToolBar = (Toolbar) findViewById(R.id.configuration_toolbar);
        setSupportActionBar(mToolBar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        mToolBar.setTitle(getString(R.string.action_settings));

        mDefaultLanguage.setSelection(mPrefs.getInt(DEFAULT_LANGUAGE, 0));

        mDefaultLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPrefs.edit().putInt(DEFAULT_LANGUAGE, position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        CustomSpinnerAdapter colorAdapter = new CustomSpinnerAdapter(this);
        colorAdapter.setItems(getResources().getStringArray(R.array.color_modes));
        mDefaultColor.setAdapter(colorAdapter);
        colorAdapter.notifyDataSetChanged();

        mDefaultColor.setSelection(mPrefs.getInt(DEFAULT_COLOR, 0));

        mDefaultColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPrefs.edit().putInt(DEFAULT_COLOR, position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mShowClockCheckedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowClockCheckedTextView.setChecked(!mShowClockCheckedTextView.isChecked());
                mPrefs.edit().putBoolean(SHOW_CLOCK, mShowClockCheckedTextView.isChecked()).apply();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (view == mDefaultLanguage){

        } else if (view == mDefaultColor){
            mPrefs.edit().putInt(DEFAULT_COLOR, position).apply();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
