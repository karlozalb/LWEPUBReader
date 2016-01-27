package com.projectclean.lwepubreader.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.projectclean.lwepubreader.Router;
import com.projectclean.lwepubreader.adapters.MyLibraryAdapter;
import com.projectclean.lwepubreader.R;
import com.projectclean.lwepubreader.epub.EPUBImporter;
import com.projectclean.lwepubreader.epub.IProgressListener;
import com.projectclean.lwepubreader.io.FileUtils;
import com.projectclean.lwepubreader.listnodes.MyLibraryBookListNode;
import com.projectclean.lwepubreader.model.Book;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by Carlos Albaladejo Pérez on 05/12/2015.
 */
public class MyLibraryFragment extends GenericFragment implements IProgressListener {

    MyLibraryAdapter mMyLibraryAdapter;
    FileUtils mFileUtils;
    ListView myLibraryListView;

    public static final int MOST_RECENT_LIMIT = 3;
    public static final String FRAGMENT_QUERY = "F_QUERY";
    String mQuery;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_fragment_mylibrary,container,false);

        setFragmentParams();

        myLibraryListView = (ListView)v.findViewById(R.id.mylibrary_listview);
        mMyLibraryAdapter = new MyLibraryAdapter(getActivity());
        myLibraryListView.setAdapter(mMyLibraryAdapter);

        mFileUtils = FileUtils.getInstance(getActivity());

        loadCurrentLibrary();

        myLibraryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Router.showEPUB(MyLibraryFragment.this.getActivity(), ((Book) mMyLibraryAdapter.getItem(position)).getBookPath());
            }
        });

        FloatingActionButton updateButton = (FloatingActionButton)v.findViewById(R.id.btn_update_library);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMyLibrary();
            }
        });

        myLibraryListView.setItemsCanFocus(true);

        return v;
    }

    public void setFragmentParams(){

        Bundle b = getArguments();

        if (b!=null) {
            super.setFragmentParams();
            mQuery = b.getString(FRAGMENT_QUERY);
        }
    }

    public void onResume(){
        super.onResume();
        mMyLibraryAdapter.updateListUI();
    }

    public void loadCurrentLibrary(){
        try {
            List<Book> books = Book.findWithQuery(Book.class, mQuery);

            mMyLibraryAdapter.clear();

            if (books.size() > 0){
                mMyLibraryAdapter.addItems(books);
            }

            mMyLibraryAdapter.notifyDataSetChanged();
        }catch(ExceptionInInitializerError e){
            //Database doesn't exist yet.
        }
    }

    public void updateMyLibrary(){
        EPUBImporter epubImporter = new EPUBImporter(getActivity());
        epubImporter.setProgressListener(this);
        epubImporter.importNewBooks(mFileUtils.scanFileSystemForEPUB());
    }


    private LinkedList<MyLibraryBookListNode> createMyLibraryDataNodes(LinkedList<String> pfilepaths){
        LinkedList<MyLibraryBookListNode> dataNodes = new LinkedList<MyLibraryBookListNode>();

        for (String filePath : pfilepaths) {
            MyLibraryBookListNode node = new MyLibraryBookListNode();
            node.setEbookPath(filePath);
            dataNodes.add(node);
        }

        return dataNodes;
    }

    @Override
    public void onProgressFinished() {
        Log.i("LWEPUB", "Proceso de importación de ebooks finalizado, cargando librería.");
        loadCurrentLibrary();
    }

    @Override
    public void onProgressChanged(int pprogressdelta) {

    }
}
