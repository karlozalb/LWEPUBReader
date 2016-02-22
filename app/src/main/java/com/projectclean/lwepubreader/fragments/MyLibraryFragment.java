package com.projectclean.lwepubreader.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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
    protected ListView myLibraryListView;

    public static final int MOST_RECENT_LIMIT = 3;
    public static final String FRAGMENT_QUERY = "F_QUERY";
    public static final String FLOATING_BUTTON_VISIBILITY = "F_FLOATING_BUTTON";
    String mQuery;

    private int mCurrentLongClickSelectedItem;
    private EPUBImporter mEpubImporter;

    private TextView mEmptyTextView;

    protected int mEmptyViewId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setFragmentParams();

        mEpubImporter = new EPUBImporter(getActivity());

        View v = inflater.inflate(mLayoutID, container, false);

        myLibraryListView = (ListView)v.findViewById(R.id.mylibrary_listview);
        mMyLibraryAdapter = new MyLibraryAdapter(getActivity());

        mEmptyTextView = (TextView)getActivity().findViewById(R.id.mylibrary_empty_textview);

        myLibraryListView.setAdapter(mMyLibraryAdapter);

        mFileUtils = FileUtils.getInstance(getActivity());

        loadCurrentLibrary();

        setListeners();

        FloatingActionButton updateButton = (FloatingActionButton) v.findViewById(R.id.btn_update_library);
        updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateMyLibrary(Router.showSpinnerLoadingDialog(getActivity()));
                }
        });

        myLibraryListView.setItemsCanFocus(true);

        registerForContextMenu(myLibraryListView);

        mEmptyViewId = R.id.mylibrary_empty_textview;

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myLibraryListView.setEmptyView(getActivity().findViewById(mEmptyViewId));
    }

    public void setListeners(){
        myLibraryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Router.showEPUB(MyLibraryFragment.this.getActivity(), ((Book) mMyLibraryAdapter.getItem(position)).getBookPath());
            }
        });

        myLibraryListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentLongClickSelectedItem = position;
                return false;
            }
        });

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

        mMyLibraryAdapter.notifyDataSetChanged();

        //if (mEmptyTextView != null && mMyLibraryAdapter.getCount() <= 0) mEmptyTextView.setVisibility(View.GONE);
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

    public void updateMyLibrary(final SpinnerDialogFragment pspinner){
        mEpubImporter.setProgressListener(this);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                mEpubImporter.getNewBooks(mFileUtils.scanFileSystemForEPUB(),pspinner);
            }
        });
        t.start();
    }

    public void importSelectedBooks(LinkedList<String> pselectedbooks){
        mEpubImporter.importSelectedBooks(pselectedbooks);
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.listview_context_menu, menu);

        int position = myLibraryListView.getSelectedItemPosition();
    }

    public Book getSelectedItem(){
        return (Book)myLibraryListView.getItemAtPosition(mCurrentLongClickSelectedItem);
    }

}
