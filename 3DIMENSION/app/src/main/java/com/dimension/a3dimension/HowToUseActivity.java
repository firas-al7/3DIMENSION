package com.dimension.a3dimension;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.MediaController;

import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import android.widget.VideoView;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;




public class HowToUseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_use);
        Toolbar toolbar =  findViewById(R.id.appToolbarHow);
        TextView toolbarTitle = findViewById(R.id.toolbarTitleHow);
        setSupportActionBar(toolbar);

        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowHomeEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            toolbarTitle.setText("How To Use");
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Button next = findViewById(R.id.tutorial_next_button);
        next.setOnClickListener(v -> {  //onBackPressed();

                    Intent intent = new Intent(HowToUseActivity.this,TutoActivity.class);
                    startActivity(intent);

        }
        );
        Button quick = findViewById(R.id.quick_manual_button);
        quick.setOnClickListener(v -> {  //onBackPressed();

                    Intent intent = new Intent(HowToUseActivity.this,QuickManualActivity.class);
                    startActivity(intent);

                }
        );
        Button detailed = findViewById(R.id.detailed_manual_button);
        detailed.setOnClickListener(v -> {  //onBackPressed();

                    Intent intent = new Intent(HowToUseActivity.this,DetailedManualActivity.class);
                    startActivity(intent);

                }
        );
    }


}