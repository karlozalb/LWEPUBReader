package com.projectclean.lwepubreader.translation;

import android.app.Activity;
import android.os.AsyncTask;

import com.projectclean.lwepubreader.io.FileUtils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by Carlos Albaladejo Pérez on 14/01/2016.
 */
public class TranslationProvider {

    OkHttpClient client = new OkHttpClient();
    FileUtils mFileUtils;
    Activity mActivity;

    private static final String ns = null;

    private int mConfig;

    public TranslationProvider(Activity pactivity){
        mActivity = pactivity;
    }

    public void setConfiguration(int pconfig){
        mConfig = pconfig;
    }

    private void getTranslation(String purl,final ITranslationCallBack pcallback) throws IOException {

        AsyncTask<String, Integer, String> task = new AsyncTask<String, Integer, String>(){

            protected String doInBackground(String... purl) {
                Request request = new Request.Builder()
                       .url(purl[0])
                       .build();

                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    return response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            protected void onProgressUpdate(Integer... progress) {
            }

            protected void onPostExecute(String presult) {
                if (presult != null) {
                    Document doc = null;
                    mFileUtils = FileUtils.getInstance(mActivity);

                    doc = Jsoup.parse(presult);

                    Elements articleWRDdiv = doc.select("#articleWRD");
                    Elements articleDiv = doc.select("#article");

                    if (pcallback != null) pcallback.setTranslationResponse(articleWRDdiv.toString()+articleDiv.toString());
                }
            }
        };
        task.execute(purl);
    }

     public void translate(String ptext, ITranslationCallBack pcallback) throws IOException{

         ptext = ptext.replace(" ","%20");
         ptext = ptext.replace("\t","%20");
         ptext = ptext.replace("\n","%20");

         switch(mConfig){
             case 0:
                 getTranslation("http://www.wordreference.com/es/en/translation.asp?spen="+ptext, pcallback);
                 break;
             case 1:
                 getTranslation("http://www.wordreference.com/esfr/"+ptext, pcallback);
                 break;
             case 2:
                 getTranslation("http://www.wordreference.com/espt/"+ptext, pcallback);
                 break;
             case 3:
                 getTranslation("http://www.wordreference.com/es/translation.asp?tranword="+ptext, pcallback);
                 break;
             case 4:
                 getTranslation("http://www.wordreference.com/enfr/"+ptext, pcallback);
                 break;
             case 5:
                 getTranslation("http://www.wordreference.com/enit/"+ptext, pcallback);
                 break;
             case 6:
                 getTranslation("http://www.wordreference.com/enpt/"+ptext, pcallback);
                 break;
             case 7:
                 getTranslation("http://www.wordreference.com/ende/"+ptext, pcallback);
                 break;
             case 8:
                 getTranslation("http://www.wordreference.com/fres/"+ptext, pcallback);
                 break;
             case 9:
                 getTranslation("http://www.wordreference.com/fren/"+ptext, pcallback);
                 break;
             case 10:
                 getTranslation("http://www.wordreference.com/iten/"+ptext, pcallback);
                 break;
             case 11:
                 getTranslation("http://www.wordreference.com/deen/"+ptext, pcallback);
                 break;
             case 12:
                 getTranslation("http://www.wordreference.com/pten/"+ptext, pcallback);
                 break;
         }
     }

}
