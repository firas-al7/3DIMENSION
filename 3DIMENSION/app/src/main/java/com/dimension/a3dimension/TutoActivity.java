package com.dimension.a3dimension;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.Objects;

import cn.jzvd.*;

public class TutoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuto);
        Toolbar toolbar =  findViewById(R.id.appToolbarTuto);
        TextView toolbarTitle = findViewById(R.id.toolbarTitleTuto);
        setSupportActionBar(toolbar);

        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowHomeEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            toolbarTitle.setText("Tutorial");
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        VideoView videoView = findViewById(R.id.videoView);

        videoView.setVideoPath("android.resource://"+getPackageName()+"/"+ R.raw.demo);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.start();

    }

  public void onConfigurationChanged(@NonNull Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
      Objects.requireNonNull(getSupportActionBar()).hide();
    } else {
      Objects.requireNonNull(getSupportActionBar()).show();
    }
  }

}
