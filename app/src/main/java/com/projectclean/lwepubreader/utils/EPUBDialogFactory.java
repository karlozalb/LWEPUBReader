package com.projectclean.lwepubreader.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.projectclean.lwepubreader.R;

/**
 * Created by Carlos Albaladejo Pérez on 17/02/2016.
 */
public class EPUBDialogFactory {

    public  static void createAndShowAlertDialog(Activity pactivity,String ptitle,String pmessage,final OnEPUBDialogClickListener plistener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(pactivity);
        builder.setTitle(ptitle);
        builder.setMessage(pmessage);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                plistener.onPositiveButtonClick();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                plistener.onNegativeButtonClick();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(pactivity.getResources().getColor(R.color.pcg_orange));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(pactivity.getResources().getColor(R.color.pcg_orange));
    }

    public  static void createAndShowAlertDialogNoNegativeButton(Activity pactivity,String ptitle,String pmessage,final OnEPUBDialogClickListener plistener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(pactivity);
        builder.setTitle(ptitle);
        builder.setMessage(pmessage);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                plistener.onPositiveButtonClick();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(pactivity.getResources().getColor(R.color.pcg_orange));
    }

}
