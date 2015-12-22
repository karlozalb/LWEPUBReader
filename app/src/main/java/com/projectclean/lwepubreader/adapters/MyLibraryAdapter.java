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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Carlos Albaladejo Pérez on 05/12/2015.
 */
public class MyLibraryAdapter extends BaseAdapter {

    private Activity mActivity;
    private LayoutInflater mLayoutInflater;
    private LinkedList<Book> mBooks;
    private String mPrivateFilesDir;

    public MyLibraryAdapter(Activity pactivity){
        mActivity = pactivity;
        mLayoutInflater = pactivity.getLayoutInflater();
        mBooks = new LinkedList<Book>();

        mPrivateFilesDir = mActivity.getFilesDir().getAbsolutePath();
    }

    public void addItem(Book pnode){
        mBooks.add(pnode);
    }

    public void addItems(List<Book> pnodes){
        mBooks.addAll(pnodes);
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
        return 0;
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

            convertView.setTag(holder);
        }else{ //Recycled view
            holder = (MyLibraryBookListHolder)convertView.getTag();
        }

        holder.AUTHOR.setText(currentBook.getAuthor());
        holder.TITLE.setText(currentBook.getTitle());
        Picasso.with(mActivity).load(new File(mPrivateFilesDir + "/" + currentBook.getBookCover())).resize(200, 266).into(holder.COVER);


        //if (holder.TASK != null) holder.TASK.cancel(true);

        /*final List<Book> book = Book.find(Book.class, "BOOK_PATH = ?", dataNode.getEbookPath());

        holder.TASK = new AsyncTask<String, Integer, String[]>(){

            protected String[] doInBackground(String... ppath) {
                String[] result = new String[3];

                if (book != null && book.size() > 0){
                    Log.i("LWEPUB", "Book stored in database.");
                    result[0] = book.get(0).getAuthor();
                    result[1] = book.get(0).getTitle();
                    result[2] = book.get(0).getBookPath();
                }else {
                    Log.i("LWEPUB", "New book!.");
                    holder.LOADER_HELPER = new EPUBLoaderHelper(ppath[0]);

                    try {
                        String[] authors = holder.LOADER_HELPER.getPackage().getMetadata().getBookAuthor();
                        result[0] = "";

                        if (authors.length == 0) {
                            result[0] = "Unknown author";
                        } else if (authors.length == 1) {
                            result[0] = authors[0];
                        } else {
                            for (int i = 0; i < authors.length - 1; i++) {
                                result[0] += authors[i] + " & ";
                            }
                            result[0] += authors[authors.length - 1];
                        }
                        result[1] = holder.LOADER_HELPER.getPackage().getMetadata().getBookTitle();
                        result[2] = ppath[0];
                    } catch (EPUBException e) {
                        e.printStackTrace();
                    }
                }

                return result;
            }

            protected void onProgressUpdate(Integer... progress) {
            }

            protected void onPostExecute(String[] presult) {
                if (!isCancelled()){
                    holder.AUTHOR_STRING = presult[0];
                    holder.TITLE_STRING = presult[1];
                    holder.EPUB_PATH = presult[2];

                    holder.AUTHOR.setText(presult[0]);
                    holder.TITLE.setText(presult[1]);
                    holder.THUMB_GENERATOR.addTask(holder);
                }
            }
        };
        holder.TASK.execute(dataNode.getEbookPath());*/

        return convertView;
    }

    public class MyLibraryBookListHolder{
        //View items
        public TextView TITLE,AUTHOR;
        public ImageView COVER;

        //Additional data
        public String TITLE_STRING,AUTHOR_STRING,EPUB_PATH;

        //Helpers
        public AsyncTask<String, Integer, String[]> TASK;
        public EPUBLoaderHelper LOADER_HELPER;
        public CoverThumbGenerator THUMB_GENERATOR;
    }
}
