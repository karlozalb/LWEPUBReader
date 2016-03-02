package com.projectclean.lwepubreader.epub;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.pcg.epubloader.EPUBLoaderHelper;
import com.pcg.exceptions.EPUBException;
import com.projectclean.lwepubreader.Router;
import com.projectclean.lwepubreader.activities.ConfigurationActivity;
import com.projectclean.lwepubreader.fragments.MyLibraryFragment;
import com.projectclean.lwepubreader.fragments.ProgressDialogFragment;
import com.projectclean.lwepubreader.fragments.SpinnerDialogFragment;
import com.projectclean.lwepubreader.io.FileUtils;
import com.projectclean.lwepubreader.model.Book;
import com.projectclean.lwepubreader.utils.CoverThumbGenerator;
import com.projectclean.lwepubreader.utils.DateTimeUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Carlos Albaladejo Pérez on 22/12/2015.
 */
public class EPUBImporter {

    private CoverThumbGenerator mThumbGenerator;
    private int mCurrentProgress,mGoalProgress;
    private IProgressListener mProgressListener;
    private FileUtils mFileUtils;
    private Activity mActivity;
    private String mCurrentBookName;
    private SharedPreferences mPrefs;

    public EPUBImporter(Activity pactivity){
        mThumbGenerator = new CoverThumbGenerator(pactivity,this);
        mFileUtils = FileUtils.getInstance(pactivity);
        mActivity = pactivity;
        mPrefs = pactivity.getSharedPreferences("dropbox-swiftreader", pactivity.MODE_PRIVATE);
    }

    public void setProgressListener(Object plistener){
        mProgressListener = (IProgressListener)plistener;
    }

    public void getNewBooks(LinkedList<String> pepubpaths,SpinnerDialogFragment pspinner){
        final LinkedList<String> booksToAdd = new LinkedList<String>();

        for (int i=0;i<pepubpaths.size();i++){
            if (Book.find(Book.class, "BOOK_PATH = ?", pepubpaths.get(i)).size() == 0){
                booksToAdd.add(pepubpaths.get(i));
            }
        }

        pspinner.dismiss();
        Router.showFileChooserFragmentDialog(mActivity,booksToAdd);
    }

    public void importSelectedBooks(final ArrayList<String> pselectedbooks){
        if (pselectedbooks.size() > 0){

            mGoalProgress = pselectedbooks.size();

            final ProgressDialogFragment progressDialog = Router.showLoadingDialog(mActivity);

            AsyncTask<ArrayList<String>, Integer, LinkedList<EPUBLoaderHelper>> task = new AsyncTask<ArrayList<String>, Integer, LinkedList<EPUBLoaderHelper>>(){

                protected LinkedList<EPUBLoaderHelper> doInBackground(ArrayList<String>... ppath) {

                    LinkedList<EPUBLoaderHelper> loaderHelpers = new LinkedList<EPUBLoaderHelper>();

                    for (String epubFile : pselectedbooks) {
                        try {
                            loaderHelpers.add(new EPUBLoaderHelper(epubFile));
                        }catch (IOException e){
                            Log.e("LWEPUB","Invalid zip file "+epubFile+" -> "+e.getMessage());
                        }
                    }

                    int i=0;

                    if (loaderHelpers.size() > 0){
                        mCurrentBookName = mFileUtils.getFileName(loaderHelpers.get(0).getPath());
                        publishProgress(0);
                    }

                    for (EPUBLoaderHelper epubLoader : loaderHelpers) {
                        Book newBook = new Book();

                        newBook.setAuthor(processBookAuthor(epubLoader));

                        try {
                            newBook.setTitle(epubLoader.getPackage().getMetadata().getBookTitle());
                        } catch (EPUBException e) {
                            Log.e("LWEPUB","Cannot retrieve book title.");
                            e.printStackTrace();
                        }

                        newBook.setBookPath(epubLoader.getPath());
                        newBook.setBookFileName(mFileUtils.getFileName(newBook.getBookPath()));
                        newBook.setDateAdded(DateTimeUtils.getCurrentDate());
                        newBook.setMostRecentOrder(-1);
                        newBook.setMarginPercentage(85);
                        newBook.setFontSize("14");
                        newBook.setTranslationConfiguration(mPrefs.getInt(ConfigurationActivity.DEFAULT_LANGUAGE, 0));
                        newBook.setColorMode(mPrefs.getInt(ConfigurationActivity.DEFAULT_COLOR,0));

                        InputStream coverStream = epubLoader.getBookCover();
                        if (coverStream == null) {
                            try {
                                coverStream = mActivity.getAssets().open("epub-placeholder.png");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        mFileUtils.saveImageToInternalStorageFile(newBook.getBookFileName() + ".jpg", coverStream);
                        newBook.setBookCover(newBook.getBookFileName() + ".jpg");
                        newBook.save();

                        i++;
                        mCurrentBookName = newBook.getBookFileName();

                        publishProgress((int)(((float)i / (float)loaderHelpers.size())*100));
                    }

                    return loaderHelpers;
                }

                protected void onProgressUpdate(Integer... progress) {
                    progressDialog.getProgressBar().setProgress(progress[0]);
                    progressDialog.getCurrentBookTextView().setText(mCurrentBookName);
                }

                protected void onPostExecute(LinkedList<EPUBLoaderHelper> presult) {
                    progressDialog.dismiss();
                    mProgressListener.onProgressFinished();
                }
            };
            task.execute(pselectedbooks);
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
