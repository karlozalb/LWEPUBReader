package com.projectclean.lwepubreader.translation;

import android.app.Activity;
import android.os.AsyncTask;

import com.projectclean.lwepubreader.io.FileUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Carlos Albaladejo Pérez on 14/01/2016.
 */
public class TranslationProvider {

    OkHttpClient client = new OkHttpClient();
    FileUtils mFileUtils;
    Activity mActivity;

    private static final String ns = null;

    public TranslationProvider(Activity pactivity){
        mActivity = pactivity;
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

                    Elements div = doc.select("#articleWRD");

                    if (pcallback != null) pcallback.setTranslationResponse(div.toString());
                }
            }
        };
        task.execute(purl);
    }

     public void translateFromEnglishToSpanish(String ptext,ITranslationCallBack pcallback) throws IOException{
         getTranslation("http://www.wordreference.com/es/translation.asp?tranword="+ptext, pcallback);
     }

}
