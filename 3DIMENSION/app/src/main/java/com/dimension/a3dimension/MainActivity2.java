package com.dimension.a3dimension;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dimension.a3dimension.graphics.Activities.MainActivity;
import com.dimension.a3dimension.graphics.Activities.diagrams.LoadDiagramActivity;
import com.dimension.a3dimension.graphics.SharedPref;
import com.dimension.a3dimension.users.UserActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;

import kotlin.jvm.internal.Intrinsics;

public class MainActivity2 extends AppCompatActivity {

    private HashMap<Integer, View> _$_findViewCache;
    public View _$_findCachedViewById(int i) {
        if (this._$_findViewCache == null) {
            this._$_findViewCache = new HashMap<Integer, View>();
        }
        View view = this._$_findViewCache.get(i);
        if (view == null) {
            View findViewById = findViewById(i);
            this._$_findViewCache.put(i, findViewById);
            return findViewById;
        }
        return view;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar =  findViewById(R.id.appToolbar2);
        TextView toolbarTitle = findViewById(R.id.toolbarTitle2);
        String role = SharedPref.getPreferences(MainActivity2.this).getStringData("role","none");
        setSupportActionBar(toolbar);

        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowHomeEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            toolbarTitle.setText("Other");
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        ((FloatingActionButton) _$_findCachedViewById(R.id.other_return)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ConstraintLayout view = findViewById(R.id.viewScan);
        ConstraintLayout tutoriel = findViewById(R.id.tutoriel);
        ConstraintLayout setting = findViewById(R.id.setting);
        ConstraintLayout users = findViewById(R.id.users);
        view.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity2.this, LoadDiagramActivity.class);
                    startActivity(intent);
                }


        );
        tutoriel.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity2.this, HowToUseActivity.class);
                    startActivity(intent);
                }


        );
        setting.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity2.this, SettingActivity.class);
                    startActivity(intent);
                }


        );
              if(Intrinsics.areEqual(role,"admin")) {
                  users.setVisibility(View.VISIBLE);
                  users.setOnClickListener(v -> {
                              Intent intent = new Intent(MainActivity2.this, UserActivity.class);
                              startActivity(intent);
                          }


                  );
              }

    }
}