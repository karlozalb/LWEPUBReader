package com.projectclean.lwepubreader.listnodes;

/**
 * Created by Carlos Albaladejo Pérez on 05/12/2015.
 */
public class MyLibraryBookListNode {

    private String mTitle,mAuthor;
    private String mEbookPath;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String ptitle) {
        this.mTitle = mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String pauthor) {
        this.mAuthor = mAuthor;
    }

    public String getEbookPath(){
        return mEbookPath;
    }

    public void setEbookPath(String ppath){
        mEbookPath = ppath;
    }
}
