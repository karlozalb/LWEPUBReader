package com.projectclean.lwepubreader.dropbox;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.projectclean.lwepubreader.fragments.ProgressDialogFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Task to download a file from Dropbox and put it in the Downloads folder
 */
public class DownloadFileTask extends AsyncTask<List<FileMetadata>, Integer, List<File>> {

    private final Context mContext;
    private final DbxClientV2 mDbxClient;
    private final DropboxDownloadCallback mCallback;
    private Exception mException;
    private ProgressDialogFragment mProgressDialog;

    public interface DropboxDownloadCallback {
        void onDownloadComplete(List<File> result);
        void onError(Exception e);
    }

    public DownloadFileTask(Context context, DbxClientV2 dbxClient,ProgressDialogFragment pdialog, DropboxDownloadCallback callback) {
        mContext = context;
        mDbxClient = dbxClient;
        mCallback = callback;
        mProgressDialog = pdialog;
    }

    @Override
    protected void onPostExecute(List<File> result) {
        super.onPostExecute(result);
        mProgressDialog.dismiss();
        if (mException != null) {
            mCallback.onError(mException);
        } else {
            mCallback.onDownloadComplete(result);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        System.out.println("progress: " + values[0]);
        mProgressDialog.getProgressBar().setProgress(values[0]);
    }

    @Override
    protected List<File> doInBackground(List<FileMetadata>... params) {
        List<FileMetadata> metadata = params[0];
        List<File> importedFileList = new LinkedList<File>();

        int progress = 0;

        if (metadata.size() > 0){
            for (FileMetadata m : metadata) {
                try {
                    File path = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS);
                    File file = new File(path, m.getName());

                    // Make sure the Downloads directory exists.
                    if (!path.exists()) {
                        if (!path.mkdirs()) {
                            mException = new RuntimeException("Unable to create directory: " + path);
                        }
                    } else if (!path.isDirectory()) {
                        mException = new IllegalStateException("Download path is not a directory: " + path);
                        return null;
                    }

                    // Download the file.
                    OutputStream outputStream = new FileOutputStream(file);
                    mDbxClient.files.download(m.getPathLower(), m.getRev()).download(outputStream);
                    outputStream.close();

                    importedFileList.add(file);

                    publishProgress((int)(((float)progress / (float) metadata.size())*100));
                } catch (DbxException | IOException e) {
                    mException = e;
                }
                progress++;
            }
        }

        return importedFileList;
    }
}
