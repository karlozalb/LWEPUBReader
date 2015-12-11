package com.projectclean.lwepubreader.adapters;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcg.epubloader.EPUBLoaderHelper;
import com.pcg.exceptions.EPUBException;
import com.projectclean.lwepubreader.listnodes.MyLibraryBookListNode;
import com.projectclean.lwepubreader.R;

import java.util.LinkedList;

/**
 * Created by Carlos Albaladejo Pérez on 05/12/2015.
 */
public class MyLibraryAdapter extends BaseAdapter {

    private Activity mActivity;
    private LayoutInflater mLayoutInflater;
    private LinkedList<MyLibraryBookListNode> mBooks;

    public MyLibraryAdapter(Activity pactivity){
        mActivity = pactivity;
        mLayoutInflater = pactivity.getLayoutInflater();
        mBooks = new LinkedList<MyLibraryBookListNode>();
    }

    public void addItem(MyLibraryBookListNode pnode){
        mBooks.add(pnode);
    }

    public void addItems(LinkedList<MyLibraryBookListNode> pnodes){
        mBooks.addAll(pnodes);
    }

    public void addItemAtPosition(int position,MyLibraryBookListNode pnode){
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
    public View getView(int position, View convertView, ViewGroup parent) {

        MyLibraryBookListNode dataNode = mBooks.get(position);
        final MyLibraryBookListHolder holder;

        if (convertView == null){ //Brand new view needed!
            convertView = mLayoutInflater.inflate(R.layout.item_mylibrary_book,null);

            holder = new MyLibraryBookListHolder();
            holder.AUTHOR = (TextView)convertView.findViewById(R.id.mylibrary_item_author);
            holder.TITLE = (TextView)convertView.findViewById(R.id.mylibrary_item_title);
            holder.IMAGE = (ImageView)convertView.findViewById(R.id.mylibrary_item_cover);

            convertView.setTag(holder);
        }else{ //Recycled view
            holder = (MyLibraryBookListHolder)convertView.getTag();
        }

        if (holder.TASK != null) holder.TASK.cancel(true);
        holder.TASK = new AsyncTask<String, Integer, String[]>(){

            protected String[] doInBackground(String... ppath) {
                EPUBLoaderHelper loaderHelper = new EPUBLoaderHelper(ppath[0]);

                String[] result = new String[2];

                try {
                    String[] authors = loaderHelper.getPackage().getMetadata().getBookAuthor();
                    for (String author : authors){
                        result[0] += author + " & ";
                    }
                    result[1] = loaderHelper.getPackage().getMetadata().getBookTitle();
                } catch (EPUBException e) {
                    e.printStackTrace();
                }

                return result;
            }

            protected void onProgressUpdate(Integer... progress) {
            }

            protected void onPostExecute(String[] presult) {
                if (!isCancelled()){
                    holder.AUTHOR.setText(presult[0]);
                    holder.TITLE.setText(presult[1]);
                }
            }
        };

        holder.TASK.execute(dataNode.getEbookPath());

        return convertView;
    }

    public class MyLibraryBookListHolder{

        public TextView TITLE,AUTHOR;
        public ImageView IMAGE;
        public AsyncTask<String, Integer, String[]> TASK;
    }
}
