package com.projectclean.lwepubreader.model;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by Carlos Albaladejo Pérez on 22/12/2015.
 */
public class Book extends SugarRecord {

    //Data about the book, not changeable.
    String author, title;
    String bookState, bookPath, bookCover;

    String dateAdded, dateLastReading;
    float bookCompletion;

    String bookFileName;

    String locations;

    //Book's aestethics customized by the user.
    //Why are these variables strings?? because I send them to Javascript methods, and I make de function calls as strings -> String funcCall = "javascript:doSomething("+param1+","+param2+");" etc.
    String fontSize;
    String height;
    String width;

    //Data only for the app behaviour.
    //Is this book marked as read?
    boolean read;
    boolean deleted;
    int mostRecentOrder;
    int marginPercentage;

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

    public void setBookState(String pcurrentcfi,String pfontsize,String pwidth,String pheight) {
        bookState = pcurrentcfi;
        width = pwidth;
        height = pheight;
        fontSize = pfontsize;
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

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String pfontsize){
        fontSize = pfontsize;
    }

    public String getBookFileName() {
        return bookFileName;
    }

    public void setBookFileName(String bookFileName) {
        this.bookFileName = bookFileName;
    }

    public float getBookCompletion() {
        return bookCompletion;
    }

    public void setBookCompletion(float bookCompletion) {
        this.bookCompletion = bookCompletion;
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

    public String toString(){
        return "Author:"+author+" - Title:"+title;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getHeight() {
        return height;
    }

    public String getWidth() {
        return width;
    }

    public String getDateLastReading() {
        return dateLastReading;
    }

    public String getLocations(){
        return locations;
    }

    public void setLocations(String plocation){
        locations = plocation;
    }

    public void setMarginPercentage(int pmargin){
        marginPercentage = pmargin;
    }

    public int getMarginPercentage(){
        return marginPercentage;
    }

}
