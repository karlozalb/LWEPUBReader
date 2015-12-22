package com.projectclean.lwepubreader.model;

import com.orm.SugarRecord;

/**
 * Created by Carlos Albaladejo Pérez on 22/12/2015.
 */
public class Book extends SugarRecord {

    String author, title;
    String bookState, bookPath, bookCover;
    String dateAdded, dateLastRead;

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
}
