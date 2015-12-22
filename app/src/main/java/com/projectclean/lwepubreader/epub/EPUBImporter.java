package com.projectclean.lwepubreader.epub;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.pcg.epubloader.EPUBLoaderHelper;
import com.pcg.exceptions.EPUBException;
import com.projectclean.lwepubreader.model.Book;
import com.projectclean.lwepubreader.utils.CoverThumbGenerator;
import com.projectclean.lwepubreader.utils.DateTimeUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Carlos Albaladejo Pérez on 22/12/2015.
 */
public class EPUBImporter {

    private CoverThumbGenerator mThumbGenerator;
    private int mCurrentProgress,mGoalProgress;
    private IProgressListener mProgressListener;

    public EPUBImporter(Activity pactivity){
        mThumbGenerator = new CoverThumbGenerator(pactivity,this);
    }

    public void setProgressListener(Object plistener){
        mProgressListener = (IProgressListener)plistener;
    }

    public void importNewBooks(LinkedList<String> pepubpaths){
        final LinkedList<String> booksToAdd = new LinkedList<String>();

        for (int i=0;i<pepubpaths.size();i++){
            if (Book.find(Book.class, "BOOK_PATH = ?", pepubpaths.get(i)).size() == 0){
                booksToAdd.add(pepubpaths.get(i));
            }
        }

        if (booksToAdd.size() > 0){

            mGoalProgress = booksToAdd.size();

            AsyncTask<LinkedList<String>, Integer, LinkedList<EPUBLoaderHelper>> task = new AsyncTask<LinkedList<String>, Integer, LinkedList<EPUBLoaderHelper>>(){

                protected LinkedList<EPUBLoaderHelper> doInBackground(LinkedList<String>... ppath) {

                    LinkedList<EPUBLoaderHelper> loaderHelpers = new LinkedList<EPUBLoaderHelper>();

                    for (String epubFile : booksToAdd) {
                        loaderHelpers.add(new EPUBLoaderHelper(epubFile));
                    }

                    return loaderHelpers;
                }

                protected void onProgressUpdate(Integer... progress) {
                }

                protected void onPostExecute(LinkedList<EPUBLoaderHelper> presult) {
                    for (EPUBLoaderHelper epubLoader : presult) {
                        Book newBook = new Book();

                        newBook.setAuthor(processBookAuthor(epubLoader));

                        try {
                            newBook.setTitle(epubLoader.getPackage().getMetadata().getBookTitle());
                        } catch (EPUBException e) {
                            Log.e("LWEPUB","Cannot retrieve book title.");
                            e.printStackTrace();
                        }

                        newBook.setBookPath(epubLoader.getPath());
                        newBook.setDateAdded(DateTimeUtils.getCurrentDate());
                        mThumbGenerator.addTask(epubLoader.getPath(),newBook);
                    }
                }
            };
            task.execute(booksToAdd);
        }
    }

    public void addProgress(){
        mCurrentProgress++;
        Log.i("LWEPUB", "Creada captura de pantalla.");
        if (mCurrentProgress >= mGoalProgress){
            mProgressListener.onProgressFinished();
        }
    }

    private String processBookAuthor(EPUBLoaderHelper ploader) {
        String[] authors = new String[0];
        try {
            authors = ploader.getPackage().getMetadata().getBookAuthor();

            String result = "";

            if (authors.length == 0) {
                result = "Unknown author";
            } else if (authors.length == 1) {
                result = authors[0];
            } else {
                for (int i = 0; i < authors.length - 1; i++) {
                    result += authors[i] + " & ";
                }
                result += authors[authors.length - 1];
            }

            return result;
        } catch (EPUBException e) {
            Log.e("LWEPUB","Cannot retrieve book author(s).");
            e.printStackTrace();
        }

        return null;
    }

}
