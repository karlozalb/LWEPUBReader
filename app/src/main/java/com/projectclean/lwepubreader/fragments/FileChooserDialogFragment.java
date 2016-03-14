package com.projectclean.lwepubreader.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.dropbox.core.json.JsonReadException;
import com.dropbox.core.v2.files.FileMetadata;
import com.projectclean.lwepubreader.MainActivity;
import com.projectclean.lwepubreader.R;
import com.projectclean.lwepubreader.adapters.FileChooserListAdapter;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Carlos Albaladejo Pérez on 16/02/2016.
 */
public class FileChooserDialogFragment extends DialogFragment {

    public static final String EPUB_LIST = "F_EPUB_LIST";
    public static final String IS_DROPBOX = "F_IS_DROPBOX";
    private FileChooserListAdapter mFileChooserListAdapter;

    private int FILESYSTEM = 1,DROPBOX = 2, GDRIVE = 3;

    private int mCurrentProvider;

    public AlertDialog DIALOG;

    private View mView;
    private TextView mEmptyViewId;

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getActivity().getResources().getColor(R.color.pcg_orange));
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getActivity().getResources().getColor(R.color.pcg_orange));

        mEmptyViewId = (TextView)mView.findViewById(R.id.filechooser_empty_textview);
        ((ListView)mView.findViewById(R.id.file_chooser_list_view)).setEmptyView(mEmptyViewId);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.layout_fragment_dialog_file_chooser, null);

        mView = v;

        if (getArguments() != null){
            List<String> epubList = getArguments().getStringArrayList(EPUB_LIST);
            ListView listView = (ListView)v.findViewById(R.id.file_chooser_list_view);
            listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
            mFileChooserListAdapter = new FileChooserListAdapter(getActivity());

            if (getArguments().getBoolean(IS_DROPBOX)){
                mCurrentProvider = DROPBOX;
                LinkedList<FileMetadata> fileMetadataList = new LinkedList<FileMetadata>();
                for (String filemeta : epubList){
                    try {
                        fileMetadataList.add(FileMetadata.fromJson(filemeta));
                    } catch (JsonReadException e) {
                        e.printStackTrace();
                    }
                }
                mFileChooserListAdapter.addFileMetadataItems(fileMetadataList);
            }else{
                mCurrentProvider = FILESYSTEM;
                mFileChooserListAdapter.addItems(epubList);
            }

            listView.setAdapter(mFileChooserListAdapter);
        }

        Button selectAllButton = (Button)v.findViewById(R.id.file_chooser_select_all_button);
        selectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFileChooserListAdapter.selectAll();
            }
        });

        Button selectNoneButton = (Button)v.findViewById(R.id.file_chooser_select_none_button);
        selectNoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFileChooserListAdapter.selectNone();
            }
        });


        if (mCurrentProvider == FILESYSTEM){
            AlertDialog.Builder builder = new  AlertDialog.Builder(getActivity());

            builder.setView(v)
                    .setTitle(R.string.file_chooser_dialog_title)
                    .setPositiveButton(R.string.button_add, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((MainActivity) getActivity()).importSelectedBooks(mFileChooserListAdapter.getSelectedItems());
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null);

            DIALOG = builder.create();

            /*dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getActivity().getResources().getColor(R.color.pcg_orange));
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getActivity().getResources().getColor(R.color.pcg_orange));*/

            return DIALOG;
        }else if (mCurrentProvider == DROPBOX){
            AlertDialog.Builder builder = new  AlertDialog.Builder(new ContextThemeWrapper(getActivity(),R.style.AlertDialogCustom));

            builder.setView(v)
                    .setTitle(R.string.file_chooser_dialog_title)
                    .setPositiveButton(R.string.button_add, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((MainActivity) getActivity()).importSelectedBooksFromDropbox(mFileChooserListAdapter.getSelectedItemsFileMetadata());
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null);

            DIALOG = builder.create();

            /*dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getActivity().getResources().getColor(R.color.pcg_orange));
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getActivity().getResources().getColor(R.color.pcg_orange));*/

            return DIALOG;
        }else{
            return null;
        }

    }

}
