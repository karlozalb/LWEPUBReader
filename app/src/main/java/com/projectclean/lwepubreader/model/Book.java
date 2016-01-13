package com.projectclean.lwepubreader.model;

import com.orm.SugarRecord;

/**
 * Created by Carlos Albaladejo Pérez on 22/12/2015.
 */
public class Book extends SugarRecord {

    String author, title;
    String bookState, bookPath, bookCover;
    String dateAdded, dateLastRead;

    String bookFileName;

    int mFontSize;
    float mMargin;

    int mWidthAndHeight;

    public Book(){

    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String mAuthor) {
        this.author = mAuthor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String mTitle) {
        this.title = mTitle;
    }

    public String getBookState() {
        return bookState;
    }

    public void setBookState(String mBookState) {
        this.bookState = mBookState;
    }

    public String getBookPath() {
        return bookPath;
    }

    public void setBookPath(String mBookPath) {
        this.bookPath = mBookPath;
    }

    public String getBookCover() {
        return bookCover;
    }

    public void setBookCover(String mBookCover) {
        this.bookCover = mBookCover;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String mDateAdded) {
        this.dateAdded = mDateAdded;
    }

    public String getDateLastRead() {
        return dateLastRead;
    }

    public void setDateLastRead(String mDateLastRead) {
        this.dateLastRead = mDateLastRead;
    }

    public int getFontSize() {
        return mFontSize;
    }

    public void setFontSize(int mFontSize) {
        this.mFontSize = mFontSize;
    }

    public float getMargin() {
        return mMargin;
    }

    public void setMargin(float mMargin) {
        this.mMargin = mMargin;
    }

    public String getBookFileName() {
        return bookFileName;
    }

    public void setBookFileName(String bookFileName) {
        this.bookFileName = bookFileName;
    }

    public int getWidthAndHeight() {
        return mWidthAndHeight;
    }

    public void setWidthAndHeight(int pwidthAndHeight) {
        this.mWidthAndHeight = pwidthAndHeight;
    }

    public String toString(){
        return "Author:"+author+" - Title:"+title+" - WidthAndHeight:"+mWidthAndHeight+" - Font size:"+mFontSize;
    }


}
