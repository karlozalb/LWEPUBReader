package com.projectclean.lwepubreader.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.projectclean.lwepubreader.adapters.MyLibraryAdapter;
import com.projectclean.lwepubreader.R;
import com.projectclean.lwepubreader.io.FileUtils;
import com.projectclean.lwepubreader.listnodes.MyLibraryBookListNode;

import java.util.LinkedList;


/**
 * Created by Carlos Albaladejo Pérez on 05/12/2015.
 */
public class MyLibraryFragment extends GenericFragment{

    MyLibraryAdapter mMyLibraryAdapter;
    FileUtils mFileUtils;
    ListView myLibraryListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mylibrary_layout,container,false);

        setFragmentParams();

        myLibraryListView = (ListView)v.findViewById(R.id.mylibrary_listview);
        mMyLibraryAdapter = new MyLibraryAdapter(getActivity());
        myLibraryListView.setAdapter(mMyLibraryAdapter);

        mFileUtils = new FileUtils();

        updateMyLibrary();

        return v;
    }

    public void updateMyLibrary(){
        LinkedList<MyLibraryBookListNode> dataNodes = createMyLibraryDataNodes(mFileUtils.scanFileSystemForEPUB());

        mMyLibraryAdapter.clear();

        if (dataNodes.size() > 0) {
            mMyLibraryAdapter.addItems(dataNodes);
        }

        mMyLibraryAdapter.notifyDataSetChanged();
    }


    private LinkedList<MyLibraryBookListNode> createMyLibraryDataNodes(String[] pfilepaths){
        LinkedList<MyLibraryBookListNode> dataNodes = new LinkedList<MyLibraryBookListNode>();

        for (String pfilepath : pfilepaths) {
            MyLibraryBookListNode node = new MyLibraryBookListNode();
            node.setEbookPath(pfilepath);
            dataNodes.add(node);
        }

        return dataNodes;
    }
}
