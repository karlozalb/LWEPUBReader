package com.projectclean.lwepubreader.io;

import android.os.Environment;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Carlos Albaladejo Pérez on 09/12/2015.
 */
public class FileUtils {

    private String mRootPath;
    private File mExternalDirectory;

    public FileUtils() {
        mExternalDirectory = Environment.getExternalStorageDirectory();
        mRootPath = mExternalDirectory.getAbsolutePath();
    }

    public String[] scanFileSystemForEPUB(){
        FilenameFilter epubFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.toUpperCase().endsWith(".EPUB");
            }
        };

        String[] fileNames = mExternalDirectory.list(epubFilter);

        for (int i=0;i<fileNames.length;i++){
            fileNames[i] = mExternalDirectory + "/" + fileNames[i];
        }

        return fileNames;
    }

}
