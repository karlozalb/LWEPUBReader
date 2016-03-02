package com.projectclean.lwepubreader.dropbox;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.core.DbxException;
import com.dropbox.core.android.Auth;
import com.dropbox.core.json.JsonReadException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.SearchArg;
import com.dropbox.core.v2.files.SearchMatch;
import com.dropbox.core.v2.files.SearchMode;
import com.dropbox.core.v2.files.SearchResult;
import com.projectclean.lwepubreader.R;
import com.projectclean.lwepubreader.Router;
import com.projectclean.lwepubreader.fragments.SpinnerDialogFragment;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Carlos Albaladejo Pérez on 23/02/2016.
 */
public class DropboxHelper {

    private Activity mActivity;
    private String mAccessToken;

    private final int MAX_PER_PAGE = 50;
    private DbxClientV2 mClient;

    private boolean mTaskInProcess;
    private AsyncTask<Object, Integer, LinkedList<FileMetadata>> mBackgroundTask;

    public DropboxHelper(Activity pactivity){
        mActivity = pactivity;
    }

    public void authenticate(){
        if (!hasToken()) {
            Auth.startOAuth2Authentication(mActivity, mActivity.getString(R.string.dropbox_app_key));
        }else{
            getToken();
        }
    }

    public DbxClientV2 getClient(){
        return mClient;
    }

    public void getToken(){
        SharedPreferences prefs = mActivity.getSharedPreferences("dropbox-swiftreader", mActivity.MODE_PRIVATE);
        mAccessToken = prefs.getString("access-token", null);
        if (mAccessToken == null) {
            mAccessToken = Auth.getOAuth2Token();
            if (mAccessToken != null) {
                prefs.edit().putString("access-token", mAccessToken).apply();
                Log.i("LWEPUB", "Dropbox token:" + mAccessToken);
            }
        } else {
            Log.i("LWEPUB", "Dropbox token:" + mAccessToken);
        }
    }

    public void initClient(SpinnerDialogFragment pdialog){
        initClient(mAccessToken, pdialog);
    }

    private void initClient(String accessToken,SpinnerDialogFragment pdialog){
        DropboxClientFactory.init(accessToken);
        mClient = DropboxClientFactory.getClient();
        searchForEpubs(pdialog);
    }

    public void searchForEpubs(final SpinnerDialogFragment pdialog){
        mBackgroundTask = new AsyncTask<Object, Integer, LinkedList<FileMetadata>>(){

            protected LinkedList<FileMetadata> doInBackground(Object... pargs) {
                mTaskInProcess = true;

                LinkedList<FileMetadata> dropboxFiles = new LinkedList<FileMetadata>();

                try {
                    SearchArg searchArg = new SearchArg("", "epub",0,MAX_PER_PAGE, SearchMode.FILENAME);
                    SearchResult result = mClient.files.search(searchArg);

                    boolean end = false;

                    while (!end && !isCancelled()) {
                        List<SearchMatch> matches = result.getMatches();

                        if (matches.size() > 0) {
                            for (SearchMatch sm : matches) {
                                try {
                                    if (sm.getMetadata().getName().toLowerCase().endsWith(".epub")) {
                                        FileMetadata fileFileMetadata = FileMetadata.fromJson(sm.getMetadata().toJson(false));

                                        dropboxFiles.add(fileFileMetadata);
                                    }
                                } catch (JsonReadException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (result.getMore()) {
                                searchArg = new SearchArg("", "epub", result.getStart(), MAX_PER_PAGE, SearchMode.FILENAME);
                                result = mClient.files.search(searchArg);
                            }else{
                                end = true;
                            }
                        }else{
                            end = true;
                        }
                    }

                } catch (DbxException e) {
                    e.printStackTrace();
                }

                return dropboxFiles;
            }

            protected void onProgressUpdate(Integer... progress) {

            }

            protected void onPostExecute(LinkedList<FileMetadata> presult) {
                if (!isCancelled()) {
                    pdialog.dismiss();
                    Router.showDropboxFileChooserFragmentDialog(mActivity, presult);
                }
                mTaskInProcess = false;
            }
        };
        mBackgroundTask.execute(0);
    }

    public boolean isThereATaskInProcess(){
        return mTaskInProcess;
    }

    public void cancelTask(){
        if (mBackgroundTask != null) {
            mBackgroundTask.cancel(true);
        }
    }

    public boolean hasToken(){
            SharedPreferences prefs = mActivity.getSharedPreferences("dropbox-swiftreader", mActivity.MODE_PRIVATE);
            String accessToken = prefs.getString("access-token", null);
            return accessToken != null;
    }

    /*
        API response example.

        ".tag": "file",
        "name": "epub30-test-0101-20140611.epub",
        "path_lower": "/epub test 1/epub30-test-0101-20140611.epub",
        "path_display": "/epub test 1/epub30-test-0101-20140611.epub",
        "id": "id:KG5AGJeiHTAAAAAAAAAAAQ",
        "client_modified": "2014-06-11T22:22:54Z",
        "server_modified": "2016-02-23T19:30:00Z",
        "rev": "f22c102b91464",
        "size": 13460
     */

    /*public class DropboxFile{
        public String NAME,PATH_LOWER,PATH_DISPLAY,ID,SIZE,REV;
        public Date CLIENT_MODIFIED,SERVER_MODIFIED;

        public DropboxFile(JSONObject pobject){
            try {
                NAME = pobject.getString("name");
                PATH_LOWER = pobject.getString("path_lower");
                PATH_DISPLAY = pobject.getString("path_display");
                ID = pobject.getString("id");
                SIZE = pobject.getString("size");
                REV = pobject.getString("rev");

                CLIENT_MODIFIED = pobject.getString()
                SERVER_MODIFIED = ;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }*/
}
