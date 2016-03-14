package com.projectclean.lwepubreader.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.dropbox.core.v2.files.FileMetadata;
import com.projectclean.lwepubreader.MainActivity;
import com.projectclean.lwepubreader.R;
import com.projectclean.lwepubreader.Router;
import com.projectclean.lwepubreader.adapters.MyLibraryAdapter;
import com.projectclean.lwepubreader.dropbox.DropboxHelper;
import com.projectclean.lwepubreader.epub.EPUBImporter;
import com.projectclean.lwepubreader.epub.IProgressListener;
import com.projectclean.lwepubreader.io.FileUtils;
import com.projectclean.lwepubreader.listnodes.MyLibraryBookListNode;
import com.projectclean.lwepubreader.model.Book;
import com.projectclean.lwepubreader.services.DropboxDownloadService;

import java.util.ArrayList;
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

    private Activity mActivity;
    private ProgressDialogBroadcastReceiver mProgressBroadcastReceiver;
    private DownloadTaskServiceResponseReceiver mResponseBroadcastReceiver;

    private boolean mReceiversRegistered,mPaused;

    private View mCurrentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setFragmentParams();

        mActivity = getActivity();

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
                    updateMyLibrary(Router.showSpinnerLoadingDialog(mActivity,"Escaneando epubs en su dispositivo..."));
                }
        });

        myLibraryListView.setItemsCanFocus(true);

        registerForContextMenu(myLibraryListView);

        mEmptyViewId = R.id.mylibrary_empty_textview;

        mCurrentView = v;

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
        mPaused = false;

        if (mResponseBroadcastReceiver != null) {
            mResponseBroadcastReceiver.importIfNeeded();
        }

        mMyLibraryAdapter.updateListUI();

        mMyLibraryAdapter.notifyDataSetChanged();
    }

    public void onPause(){
        super.onPause();
        mPaused = true;
    }

    public void onDestroy() {
        super.onDestroy();

        //un-register BroadcastReceiver
        if (mResponseBroadcastReceiver != null && mProgressBroadcastReceiver != null) {
            getActivity().unregisterReceiver(mResponseBroadcastReceiver);
            getActivity().unregisterReceiver(mProgressBroadcastReceiver);
        }
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

    public void importSelectedBooks(ArrayList<String> pselectedbooks){
        mEpubImporter.importSelectedBooks(pselectedbooks);
    }

    public void importSelectedBooksFromDropbox(LinkedList<FileMetadata> pselectedbooks,DropboxHelper pdropboxhelper){

        if (mProgressBroadcastReceiver == null && mResponseBroadcastReceiver == null) {
            ProgressDialogFragment progressDialog = Router.showLoadingDialog(getActivity());
            progressDialog.setInitialBook(pselectedbooks.get(0).getName());

            mProgressBroadcastReceiver = new ProgressDialogBroadcastReceiver(progressDialog);
            mResponseBroadcastReceiver = new DownloadTaskServiceResponseReceiver(progressDialog);

            IntentFilter intentFilter = new IntentFilter(DropboxDownloadService.ACTION_DropboxDownloadService_UPDATE);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            getActivity().registerReceiver(mProgressBroadcastReceiver, intentFilter);

            IntentFilter intentFilterResponse = new IntentFilter(DropboxDownloadService.ACTION_DropboxDownloadService_RESPONSE);
            intentFilterResponse.addCategory(Intent.CATEGORY_DEFAULT);
            getActivity().registerReceiver(mResponseBroadcastReceiver, intentFilterResponse);
        }else{
            ProgressDialogFragment progressDialog = Router.showLoadingDialog(getActivity());

            mProgressBroadcastReceiver.setDialog(progressDialog);
            mResponseBroadcastReceiver.setDialog(progressDialog);
        }

        Intent intent = new Intent(getActivity(), DropboxDownloadService.class);

        ArrayList<String> filesJSON = new ArrayList<String>();
        for (FileMetadata fm : pselectedbooks){
            filesJSON.add(fm.toJson(true));
        }
        intent.putStringArrayListExtra(Intent.EXTRA_TEXT, filesJSON);

        getActivity().startService(intent);

        /*DownloadFileTask downloadTask = new DownloadFileTask(getActivity(),pdropboxhelper.getClient(),Router.showLoadingDialog(getActivity()),new DownloadFileTask.DropboxDownloadCallback(){
            @Override
            public void onDownloadComplete(List<File> result) {
                LinkedList<String> dropboxBooksPaths = new LinkedList<String>();
                for (File f : result) {
                    dropboxBooksPaths.add(f.getAbsolutePath());
                }
                mEpubImporter.importSelectedBooks(dropboxBooksPaths);

            }

            @Override
            public void onError(Exception e) {

            }
        });
        downloadTask.execute(pselectedbooks);*/
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
        ((MainActivity)getActivity()).showSnackBarMessage(getActivity().getString(R.string.books_already_imported),Snackbar.LENGTH_SHORT);
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

    public class ProgressDialogBroadcastReceiver extends BroadcastReceiver {

        private ProgressDialogFragment mDialog;

        public ProgressDialogBroadcastReceiver(ProgressDialogFragment pdialog){
            mDialog = pdialog;
        }

        public void setDialog(ProgressDialogFragment pdialog){
            mDialog = pdialog;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra(DropboxDownloadService.PROGRESS,0);
            String filename = intent.getStringExtra(DropboxDownloadService.FILENAME);
            System.out.println("Progreso recibido: "+progress);
            mDialog.getProgressBar().setProgress(progress);
            mDialog.getCurrentBookTextView().setText(filename);
        }
    }

    public class DownloadTaskServiceResponseReceiver extends BroadcastReceiver {

        private ProgressDialogFragment mDialog;

        private ArrayList<String> storedFiles;
        private boolean delayedLibraryImport;

        public DownloadTaskServiceResponseReceiver(ProgressDialogFragment pdialog){
            mDialog = pdialog;
        }

        public void setDialog(ProgressDialogFragment pdialog){
            mDialog = pdialog;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<String> files = intent.getStringArrayListExtra(DropboxDownloadService.RESPONSE);
            if (mPaused){
                storedFiles = files;
                delayedLibraryImport = true;
            }else {
                mDialog.dismiss();
                if (files != null && files.size() > 0) {
                    mEpubImporter.setProgressListener(MyLibraryFragment.this);
                    mEpubImporter.importSelectedBooks(files);
                }
            }
        }

        public void importIfNeeded(){
            if (delayedLibraryImport){
                delayedLibraryImport = false;
                mDialog.dismiss();
                if (storedFiles != null && storedFiles.size() > 0) {
                    mEpubImporter.setProgressListener(MyLibraryFragment.this);
                    mEpubImporter.importSelectedBooks(storedFiles);
                }
            }
        }
    }
}
