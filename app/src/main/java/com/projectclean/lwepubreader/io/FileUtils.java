package com.projectclean.lwepubreader.io;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

/**
 * Created by Carlos Albaladejo Pérez on 09/12/2015.
 */
public class FileUtils {

    private File[] mExternalDirectores;
    private LinkedList<File> mRootExternalDirectores;
    private Activity mContext;

    private static FileUtils mInstance;

    public static FileUtils getInstance(Activity pcontext){
        if (mInstance == null) {
            mInstance = new FileUtils(pcontext);
        }
        return mInstance;
    }

    private FileUtils(Activity pcontext) {
        mExternalDirectores = ContextCompat.getExternalFilesDirs(pcontext, null);
        mContext = pcontext;

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

    public String getFileName(String pfilepath){
        String[] tokens = pfilepath.split("/");
        return tokens[tokens.length-1];
    }

    public void saveStringToInternalStorageFile(String pfilename,String pcontent){
        FileOutputStream outputStream;

        try {
            outputStream = mContext.openFileOutput(pfilename, Context.MODE_PRIVATE);
            outputStream.write(pcontent.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveImageToInternalStorageFile(String pfilename,InputStream pstream){
        FileOutputStream outputStream;

        Bitmap bmp = BitmapFactory.decodeStream(pstream);

        try {
            outputStream = mContext.openFileOutput(pfilename, Context.MODE_PRIVATE);
            bmp.compress(Bitmap.CompressFormat.JPEG, 75, outputStream);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public InputStream getInputStreamFromInternalStorage(String pfilename)throws IOException{
        return mContext.openFileInput(pfilename);
    }
}
