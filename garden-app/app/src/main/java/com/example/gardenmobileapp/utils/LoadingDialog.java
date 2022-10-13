package com.example.gardenmobileapp.utils;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;

import com.example.gardenmobileapp.R;

public class LoadingDialog {

    private final Activity activity;
    private AlertDialog alertDialog;

    public LoadingDialog(Activity activity)
    {
        this.activity = activity;
    }

    public void startLoadingDialog()
    {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_dialog_loading,null));
        builder.setCancelable(false);


        alertDialog = builder.create();
        alertDialog.show();
    }


    public void dismissDialog()
    {
        alertDialog.dismiss();
    }
}