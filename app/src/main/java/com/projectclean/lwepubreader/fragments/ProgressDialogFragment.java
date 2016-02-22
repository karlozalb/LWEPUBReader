package com.projectclean.lwepubreader.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.projectclean.lwepubreader.MainActivity;
import com.projectclean.lwepubreader.R;
import com.projectclean.lwepubreader.adapters.FileChooserListAdapter;

import java.util.List;

/**
 * Created by Carlos Albaladejo Pérez on 16/02/2016.
 */
public class ProgressDialogFragment extends DialogFragment {

    public static final String EPUB_LIST = "F_EPUB_LIST";
    private FileChooserListAdapter mFileChooserListAdapter;
    private ProgressBar mProgressBar;
    private TextView mCurrentBook;

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.layout_fragment_dialog_loading, null);

        mProgressBar = (ProgressBar)v.findViewById(R.id.loading_dialog_progress);
        mCurrentBook = (TextView)v.findViewById(R.id.loading_dialog_tv_current_book);

        return new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setView(v)
                .setTitle(R.string.loading_dialog_title)
                .create();
    }

    public ProgressBar getProgressBar(){
        return mProgressBar;
    }

    public TextView getCurrentBookTextView(){
        return mCurrentBook;
    }
}
