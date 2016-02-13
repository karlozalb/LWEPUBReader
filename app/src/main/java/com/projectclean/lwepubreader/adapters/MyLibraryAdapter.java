package com.projectclean.lwepubreader.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pcg.epubloader.EPUBLoaderHelper;
import com.pcg.epubspec.Manifest;
import com.pcg.epubspec.Spine;
import com.pcg.exceptions.EPUBException;
import com.projectclean.lwepubreader.listnodes.MyLibraryBookListNode;
import com.projectclean.lwepubreader.R;
import com.projectclean.lwepubreader.model.Book;
import com.projectclean.lwepubreader.utils.CoverThumbGenerator;
import com.projectclean.lwepubreader.utils.JavascriptUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Carlos Albaladejo Pérez on 05/12/2015.
 */
public class MyLibraryAdapter extends BaseAdapter {

    private Activity mActivity;
    private LayoutInflater mLayoutInflater;
    private LinkedList<Book> mBooks;
    private LinkedList<MyLibraryBookListHolder> mHolders;
    private String mPrivateFilesDir;

    private HashMap<Book,Integer> mIdMap;

    public MyLibraryAdapter(Activity pactivity){
        mActivity = pactivity;
        mLayoutInflater = pactivity.getLayoutInflater();
        mBooks = new LinkedList<Book>();
        mHolders = new LinkedList<MyLibraryBookListHolder>();
        mIdMap = new HashMap<Book,Integer>();

        mPrivateFilesDir = mActivity.getFilesDir().getAbsolutePath();
    }

    public void addItem(Book pnode){
        mBooks.add(pnode);
    }

    public void addItems(List<Book> pnodes){
        for (int i=0;i<pnodes.size();i++) {
            mIdMap.put(pnodes.get(i),i);
            mBooks.add(pnodes.get(i));
        }
    }

    public boolean hasStableIds(){
        return true;
    }

    public void addItemAtPosition(int position,Book pnode){
        mBooks.add(position, pnode);
    }

    public void clear(){
        mBooks.clear();
    }

    @Override
    public int getCount() {
        return mBooks.size();
    }

    @Override
    public Object getItem(int position) {
        return mBooks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mIdMap.get(mBooks.get(position));
    }

    public boolean isEmpty(){
        return mBooks.size() == 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Book currentBook = mBooks.get(position);
        final MyLibraryBookListHolder holder;

        if (convertView == null){ //Brand new view needed!
            convertView = mLayoutInflater.inflate(R.layout.item_mylibrary_book,null);

            holder = new MyLibraryBookListHolder();
            holder.AUTHOR = (TextView)convertView.findViewById(R.id.mylibrary_item_author);
            holder.TITLE = (TextView)convertView.findViewById(R.id.mylibrary_item_title);
            holder.COVER = (ImageView)convertView.findViewById(R.id.mylibrary_item_cover);
            holder.PROGRESS_BAR = (ProgressBar) convertView.findViewById(R.id.book_completion_progress_bar);

            mHolders.add(holder);

            convertView.setTag(holder);
        }else{ //Recycled view
            holder = (MyLibraryBookListHolder)convertView.getTag();
        }

        holder.AUTHOR.setText(currentBook.getAuthor());
        holder.TITLE.setText(currentBook.getTitle());
        Picasso.with(mActivity).load(new File(mPrivateFilesDir + "/" + currentBook.getBookCover())).resize(200, 266).into(holder.COVER);
        holder.PROGRESS_BAR.setProgress((int)(currentBook.getBookCompletion() * 100));
        holder.BOOK = currentBook;

        return convertView;
    }

    /**
     * Updates the book completion progress bar.
     */
    public void updateListUI(){
        for (MyLibraryBookListHolder holder : mHolders) {
            holder.PROGRESS_BAR.setProgress((int)(holder.BOOK.getBookCompletion() * 100));
        }
    }

    public class MyLibraryBookListHolder{
        //View items
        public TextView TITLE,AUTHOR;
        public ImageView COVER;
        public ProgressBar PROGRESS_BAR;

        public Book BOOK;

        //Additional data
        public String TITLE_STRING,AUTHOR_STRING,EPUB_PATH;

        //Helpers
        public AsyncTask<String, Integer, String[]> TASK;
        public EPUBLoaderHelper LOADER_HELPER;
        public CoverThumbGenerator THUMB_GENERATOR;
    }
}
