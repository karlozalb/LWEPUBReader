package com.projectclean.lwepubreader.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.projectclean.lwepubreader.R;
import com.projectclean.lwepubreader.adapters.MyLibraryAdapter;
import com.projectclean.lwepubreader.io.FileUtils;

/**
 * Created by Carlos Albaladejo Pérez on 29/01/2016.
 */
public class MostRecentFragment extends MyLibraryFragment{

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setFragmentParams();

        View v = inflater.inflate(mLayoutID, container, false);

        myLibraryListView = (ListView)v.findViewById(R.id.most_recent_listview);
        mMyLibraryAdapter = new MyLibraryAdapter(getActivity());
        myLibraryListView.setAdapter(mMyLibraryAdapter);

        mFileUtils = FileUtils.getInstance(getActivity());

        loadCurrentLibrary();

        setListeners();

        myLibraryListView.setItemsCanFocus(true);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myLibraryListView.setEmptyView(getActivity().findViewById(R.id.most_recent_listview_empty_textview));
    }

}
