package com.dimension.a3dimension;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.dimension.a3dimension.users.AccountActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;

public class SettingActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_setting);
        Toolbar toolbar =  findViewById(R.id.appToolbarSetting);
        TextView toolbarTitle = findViewById(R.id.toolbarTitleSetting);
        setSupportActionBar(toolbar);

        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowHomeEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            toolbarTitle.setText("Settings");
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        ConstraintLayout bluetooth = findViewById(R.id.bluetooth);
        ((FloatingActionButton) _$_findCachedViewById(R.id.setting_return)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });
        bluetooth.setOnClickListener(v -> {
                    Intent intent = new Intent(SettingActivity.this, BluetoothSettingActivity.class);
                    startActivity(intent);
                }


        );
        ConstraintLayout otg = findViewById(R.id.otg);
        otg.setOnClickListener(v -> {
                    Intent intent = new Intent(SettingActivity.this,OtgSettingActivity.class);
                    startActivity(intent);
                }


        );
        ConstraintLayout user = findViewById(R.id.user_account);
        user.setOnClickListener(v -> {
                    Intent intent = new Intent(SettingActivity.this, AccountActivity.class);
                    startActivity(intent);
                }


        );
    }
}