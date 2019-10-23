package com.example.pato.customclass;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.example.pato.BoardModifyActivity;
import com.example.pato.R;

public class LoadingDialog {

    public static AlertDialog loading_Diaglog(Activity activity){

        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_loading,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final AlertDialog Optiondialog = builder.create();
        Optiondialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Optiondialog.setView(view);
        Optiondialog.setCancelable(false);
        Optiondialog.show();

        return Optiondialog;
    }
}
