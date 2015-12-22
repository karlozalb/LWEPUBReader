package com.projectclean.lwepubreader.io;

import android.app.Activity;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedList;

/**
 * Created by Carlos Albaladejo Pérez on 09/12/2015.
 */
public class FileUtils {

    private File[] mExternalDirectores;
    private LinkedList<File> mRootExternalDirectores;

    private static FileUtils mInstance;

    public static FileUtils getInstance(Activity pcontext){
        if (mInstance == null) {
            mInstance = new FileUtils(pcontext);
        }
        return mInstance;
    }

    private FileUtils(Activity pcontext) {
        mExternalDirectores = ContextCompat.getExternalFilesDirs(pcontext, null);

        mRootExternalDirectores = new LinkedList<File>();

        for (File device : mExternalDirectores){
            mRootExternalDirectores.add(getRootOfInnerSdCardFolder(device));
        }
    }

    public LinkedList<String> scanFileSystemForEPUB(){
        FilenameFilter epubFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                Log.i("LWEPUB",filename);
                return filename.toUpperCase().endsWith(".EPUB");
            }
        };

        LinkedList<String> fileNames = new LinkedList<String>();

        if (mExternalDirectores != null && mExternalDirectores.length > 0){
            for (File device : mExternalDirectores){
                File rootPath = getRootOfInnerSdCardFolder(device);
                LinkedList<String> newEPUBSToAdd = getEPUBRecursive(rootPath);
                if (newEPUBSToAdd!=null && newEPUBSToAdd.size() > 0) fileNames.addAll(newEPUBSToAdd);
            }
        }

        return fileNames;
    }

    private LinkedList<String> getEPUBRecursive(File pdirectory){
        LinkedList<String> epubPaths = new LinkedList<String>();
        File[] files = pdirectory.listFiles();

        for (File file : files){
            if (file.isDirectory()){
                LinkedList<String> moreEPUBs = getEPUBRecursive(file);
                if (moreEPUBs!=null && moreEPUBs.size() > 0) epubPaths.addAll(moreEPUBs);
            }else{
                if (file.getName().toUpperCase().endsWith(".EPUB")){
                    epubPaths.add(file.getAbsolutePath());
                }
            }
        }

        return epubPaths;
    }

    private File getRootOfInnerSdCardFolder(File file)
    {
        if(file==null) return null;
        final long totalSpace=file.getTotalSpace();
        while(true)
        {
            final File parentFile=file.getParentFile();
            if(parentFile==null||parentFile.getTotalSpace()!=totalSpace) return file;
            file=parentFile;
        }
    }

    public File[] getExternalDirectories(){
        return mExternalDirectores;
    }

    public LinkedList<File> getRootExternalDirectories(){
        return mRootExternalDirectores;
    }
}
