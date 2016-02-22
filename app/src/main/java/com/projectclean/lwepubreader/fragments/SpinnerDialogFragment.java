package com.projectclean.lwepubreader.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.projectclean.lwepubreader.R;

/**
 * Created by Carlos Albaladejo Pérez on 16/02/2016.
 */
public class SpinnerDialogFragment extends DialogFragment{

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.layout_fragment_dialog_spinner, null);

        return new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setView(v)
                .setTitle(R.string.loading_dialog_title)
                .create();
    }

}
