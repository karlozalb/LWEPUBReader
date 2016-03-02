package com.projectclean.lwepubreader.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.projectclean.lwepubreader.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by Carlos Albaladejo Pérez on 23/02/2016.
 */
public class TocListAdapter extends BaseAdapter {

    private LinkedList<TocNode> mTocNodes;
    private Activity mActivity;
    private LayoutInflater mLayoutInflater;

    public TocListAdapter(Activity pactivity){
        mTocNodes = new LinkedList<TocNode>();
        mActivity = pactivity;
        mLayoutInflater = pactivity.getLayoutInflater();
    }

    public void parseJSON(String pjson){
        try {
            JSONObject object = new JSONObject(pjson);
            JSONArray json_array = object.optJSONArray("_result");

            for (int i = 0; i < json_array.length(); i++) {
                mTocNodes.add(new TocNode(json_array.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return mTocNodes.size();
    }

    @Override
    public Object getItem(int position) {
        return mTocNodes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TocHolder holder;

        if (convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.item_simple_textview_list, null);
            holder = new TocHolder();
            holder.TEXT = (TextView)((ViewGroup) convertView).getChildAt(0);
            convertView.setTag(holder);
        }else{
            holder = (TocHolder)convertView.getTag();
        }

        holder.TEXT.setText(mTocNodes.get(position).LABEL);

        return convertView;
    }

    public class TocNode{
        public String ID,HREF,LABEL,CFI,SPINEPOS;

        public TocNode(JSONObject pjsonobject){
            try {
                ID = pjsonobject.getString("id");
                HREF = pjsonobject.getString("href");
                LABEL = pjsonobject.getString("label");
                CFI = pjsonobject.getString("cfi");
                SPINEPOS = pjsonobject.getString("spinePos");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class TocHolder{
        public TextView TEXT;
    }

}
