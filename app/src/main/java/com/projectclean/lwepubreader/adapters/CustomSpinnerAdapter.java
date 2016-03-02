package com.projectclean.lwepubreader.adapters;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.projectclean.lwepubreader.R;

/**
 * Created by Carlos Albaladejo Pérez on 29/02/2016.
 */
public class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter{

    private String[] mItems;
    private Activity mActivity;
    private LayoutInflater mInflater;

    public CustomSpinnerAdapter(Activity pactivity){
        mActivity = pactivity;
        mInflater = mActivity.getLayoutInflater();
    }

    public void setItems(String[] pitems){
        mItems = pitems;
    }

    @Override
    public int getCount() {
        return mItems.length;
    }

    @Override
    public Object getItem(int position) {
        return mItems[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextViewHolder holder;

        if (convertView == null){
            convertView = mInflater.inflate(R.layout.item_simple_textview_list, null);

            holder = new TextViewHolder();
            holder.TV_HOLDER = (TextView)((ViewGroup)convertView).getChildAt(0);
            holder.TV_HOLDER.setGravity(Gravity.CENTER_HORIZONTAL);

            convertView.setTag(holder);
        }else{
            holder = (TextViewHolder)convertView.getTag();
        }

        holder.TV_HOLDER.setText(mItems[position]);

        return convertView;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View row = mInflater.inflate(R.layout.item_simple_textview_list, parent,false);
        TextView v = (TextView)(((ViewGroup)row).getChildAt(0));
        v.setText(mItems[position]);
        v.setGravity(Gravity.CENTER_HORIZONTAL);
        v.setBackgroundColor(mActivity.getResources().getColor(R.color.colorPrimary));
        v.setPadding(0,15,0,15);
        return row;
    }

    public class TextViewHolder{
        public TextView TV_HOLDER;
    }
}
