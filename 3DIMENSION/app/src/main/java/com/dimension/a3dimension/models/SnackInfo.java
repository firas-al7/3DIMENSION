package com.dimension.a3dimension.models;

import android.app.Activity;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class SnackInfo {

    Activity activity;

    public static void SnackInfo (View view,String title,String message){

        Snackbar snackbar = Snackbar.make(view, "Please Check Your Connection", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Refresh", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //nish();
                //artActivity(getIntent());
            }
        });
        snackbar.show();
    }
}
