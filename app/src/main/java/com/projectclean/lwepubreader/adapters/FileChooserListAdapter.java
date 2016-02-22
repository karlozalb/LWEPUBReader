package com.projectclean.lwepubreader.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.projectclean.lwepubreader.R;
import com.projectclean.lwepubreader.io.FileUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Carlos Albaladejo Pérez on 16/02/2016.
 */
public class FileChooserListAdapter  extends BaseAdapter {

    private LinkedList<EPUBNode> mFiles;
    private Activity mActivity;
    private FileUtils mFileUtils;


    public FileChooserListAdapter(Activity pactivity){
        mFiles = new LinkedList<EPUBNode>();
        mActivity = pactivity;
        mFileUtils = FileUtils.getInstance(pactivity);
    }

    @Override
    public int getCount() {
        return mFiles.size();
    }

    @Override
    public Object getItem(int position) {
        return mFiles.get(position);
    }

    public void addItems(List<String> pitems){
        for (String filename : pitems) {
            EPUBNode node = new EPUBNode();
            node.FILENAME = filename;
            mFiles.add(node);
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final FileChooserViewHolder holder;

        if (convertView == null){
            convertView = mActivity.getLayoutInflater().inflate(R.layout.item_file_chooser,null);

            holder = new FileChooserViewHolder();
            holder.CHECKED_TEXTVIEW = (CheckedTextView)((ViewGroup)convertView).getChildAt(0);

            holder.CHECKED_TEXTVIEW.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFiles.get(position).CHECKED = !mFiles.get(position).CHECKED;
                        holder.CHECKED_TEXTVIEW.setChecked(mFiles.get(position).CHECKED);
                    }
                }
            );

            convertView.setTag(holder);
        }else{
            holder = (FileChooserViewHolder)convertView.getTag();
        }

        holder.CHECKED_TEXTVIEW.setChecked(mFiles.get(position).CHECKED);
        holder.CHECKED_TEXTVIEW.setText(mFileUtils.getFileName(mFiles.get(position).FILENAME));

        return convertView;
    }

    public void selectAll(){
        for (EPUBNode node : mFiles){
            node.CHECKED = true;
        }
        notifyDataSetChanged();
    }

    public void selectNone(){
        for (EPUBNode node : mFiles){
            node.CHECKED = false;
        }
        notifyDataSetChanged();
    }

    public LinkedList<String> getSelectedItems(){
        LinkedList<String> epubs = new LinkedList<String>();

        for (EPUBNode node : mFiles){
            if (node.CHECKED) epubs.add(node.FILENAME);
        }

        return epubs;
    }

    private class FileChooserViewHolder{
        public CheckedTextView CHECKED_TEXTVIEW;
    }

    public class EPUBNode{
        public String FILENAME;
        public boolean CHECKED;
    }
}
