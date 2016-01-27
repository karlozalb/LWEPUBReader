package com.projectclean.lwepubreader.model;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by Carlos Albaladejo Pérez on 22/12/2015.
 */
public class Book extends SugarRecord {

    String author, title;
    String bookState, bookPath, bookCover;

    String dateAdded, dateLastReading;
    float bookCompletion;

    String bookFileName;

    //Book's aestethics
    int mFontSize;
    float mMargin;

    int mWidthAndHeight;

    //Is this book masked as read?
    boolean read;

    //Data only for the app behaviour.
    int mostRecentOrder;

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
        return dateLastReading;
    }

    public void setDateLastRead(String mDateLastRead) {
        this.dateLastReading = mDateLastRead;
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

    public float getBookCompletion() {
        return bookCompletion;
    }

    public void setBookCompletion(float bookCompletion) {
        this.bookCompletion = bookCompletion;
    }

    public String toString(){
        return "Author:"+author+" - Title:"+title+" - WidthAndHeight:"+mWidthAndHeight+" - Font size:"+mFontSize;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean mRead) {
        this.read = mRead;
    }

    public int getMostRecentOrder() {
        return mostRecentOrder;
    }

    public void setMostRecentOrder(int mostRecentOrder) {
        this.mostRecentOrder = mostRecentOrder;
    }

}
