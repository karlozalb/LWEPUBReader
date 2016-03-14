package com.projectclean.lwepubreader.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;

import com.dropbox.core.DbxException;
import com.dropbox.core.json.JsonReadException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.projectclean.lwepubreader.dropbox.DropboxClientFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Carlos Albaladejo Pérez on 25/02/2016.
 */
public class DropboxDownloadService extends IntentService {

    public static final String ACTION_DropboxDownloadService_RESPONSE = "com.projectclean.lwepubreader.services.RESPONSE";
    public static final String ACTION_DropboxDownloadService_UPDATE = "com.projectclean.lwepubreader.services.UPDATE";

    public static final String PROGRESS = "SERVICE_PROGRESS";
    public static final String FILENAME = "SERVICE_FILENAME";
    public static final String RESPONSE = "SERVICE_RESPONSE";

    private Exception mException;
    private DbxClientV2 mDbxClient;

    public DropboxDownloadService(){
        super("DropboxDownloadService");
        System.out.println("Servicio creado.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("Servicio iniciado.");
        ArrayList<String> fileMetadataJSON = intent.getExtras().getStringArrayList(Intent.EXTRA_TEXT);
        LinkedList<FileMetadata> fileMetadata = new LinkedList<FileMetadata>();
        ArrayList<File> importedFileList = new ArrayList<File>();

        mDbxClient = DropboxClientFactory.getClient();

        for (String json : fileMetadataJSON){
            try {
                fileMetadata.add(FileMetadata.fromJson(json));
            } catch (JsonReadException e) {
                e.printStackTrace();
            }
        }

        int progress = 0;

        if (fileMetadata.size() > 0){
            for (FileMetadata m : fileMetadata) {
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
                    }

                    // Download the file.
                    OutputStream outputStream = new FileOutputStream(file);
                    mDbxClient.files.download(m.getPathLower(), m.getRev()).download(outputStream);
                    outputStream.close();

                    importedFileList.add(file);

                    //Progress broadcast.
                    Intent intentUpdate = new Intent();
                    intentUpdate.setAction(ACTION_DropboxDownloadService_UPDATE);
                    intentUpdate.addCategory(Intent.CATEGORY_DEFAULT);

                    int currentPercentageProgress = (int) (((float) progress / (float) fileMetadata.size()) * 100);

                    intentUpdate.putExtra(PROGRESS, currentPercentageProgress);
                    intentUpdate.putExtra(FILENAME, m.getName());

                    sendBroadcast(intentUpdate);

                    System.out.println("Progreso enviado: "+currentPercentageProgress);

                } catch (DbxException | IOException e) {
                    mException = e;
                }
                progress++;
            }
        }

        //Progress broadcast.
        Intent intentUpdate = new Intent();
        intentUpdate.setAction(ACTION_DropboxDownloadService_RESPONSE);
        intentUpdate.addCategory(Intent.CATEGORY_DEFAULT);

        ArrayList<String> filesAbsPath = new ArrayList<String>();
        for (File f : importedFileList){
            filesAbsPath.add(f.getAbsolutePath());
        }
        intentUpdate.putStringArrayListExtra(RESPONSE, filesAbsPath);
        sendBroadcast(intentUpdate);
    }
}
