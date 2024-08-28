package com.dimension.a3dimension.models;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.dimension.a3dimension.R;


   public class Alerts {
  public static void show_alert(Context context, String title, String message)
  {
    new AlertDialog.Builder(context, 0)
      .setTitle(title)
      .setMessage(message)
      .setNeutralButton("Ok", new DialogInterface.OnClickListener()
      {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
          dialog.dismiss();
        }
      })
            //.setView(R.drawable.startup_screen_card_background)
      .setCancelable(false)
      .create()
      .show();
  }
}
