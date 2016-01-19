package com.projectclean.lwepubreader;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;

import com.projectclean.lwepubreader.translation.TranslationProvider;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Router.showMyLibrary(this);

        /*TranslationProvider tp = new TranslationProvider(this);
        try {
            tp.getTranslation("http://www.wordreference.com/es/translation.asp?tranword=hello",null);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

}
