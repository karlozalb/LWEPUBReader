package com.projectclean.lwepubreader.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;

import com.projectclean.lwepubreader.MainActivity;
import com.projectclean.lwepubreader.R;
import com.projectclean.lwepubreader.adapters.FileChooserListAdapter;
import com.projectclean.lwepubreader.io.FileUtils;

import java.util.List;

/**
 * Created by Carlos Albaladejo Pérez on 16/02/2016.
 */
public class FileChooserDialogFragment extends DialogFragment {

    public static final String EPUB_LIST = "F_EPUB_LIST";
    private FileChooserListAdapter mFileChooserListAdapter;

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.layout_fragment_dialog_file_chooser, null);

        if (getArguments() != null){
            List<String> epubList = getArguments().getStringArrayList(EPUB_LIST);
            ListView listView = (ListView)v.findViewById(R.id.file_chooser_list_view);
            listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
            mFileChooserListAdapter = new FileChooserListAdapter(getActivity());
            mFileChooserListAdapter.addItems(epubList);
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

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.translation_dialog_title)
                .setPositiveButton(R.string.button_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity) getActivity()).importSelectedBooks(mFileChooserListAdapter.getSelectedItems());
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

}
