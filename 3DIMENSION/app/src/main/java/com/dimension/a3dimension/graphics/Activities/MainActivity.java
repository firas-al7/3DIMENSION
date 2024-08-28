package com.dimension.a3dimension.graphics.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.dimension.a3dimension.GroundTypesAdapter;
import com.dimension.a3dimension.LoginActivity;
import com.dimension.a3dimension.graphics.Activities.diagrams.SurfaceDiagramActivity;
import com.dimension.a3dimension.graphics.Activities.diagrams.WaterFallDiagramActivity;
import com.dimension.a3dimension.graphics.SharedPref;
import com.dimension.a3dimension.graphics.consts;
import com.dimension.a3dimension.MainActivity2;
import com.dimension.a3dimension.R;

import com.dimension.a3dimension.models.Alerts;
import com.dimension.a3dimension.models.GroundType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import kotlin.jvm.internal.Intrinsics;


public final class MainActivity extends AppCompatActivity {
    private HashMap<Integer, View> _$_findViewCache;
    private boolean doubleBackToExitPressedOnce;
    private boolean is_connected_to_HC;
    private boolean is_setting_on;
    private String whichBtn = "x";
    public static final int GROUNDTYPE_CLAY = 4;
    public static final int GROUNDTYPE_COAL = 12;
    public static final int GROUNDTYPE_CONCRETE = 1;
    public static final int GROUNDTYPE_DENSE_MINERAL = 6;
    public static final int GROUNDTYPE_FRESH_WATER = 8;
    public static final int GROUNDTYPE_FROZEN_SOIL = 11;
    public static final int GROUNDTYPE_GRANITE = 13;
    public static final int GROUNDTYPE_LIGHT_MINERAL = 5;
    public static final int GROUNDTYPE_MILE = 2;
    public static final int GROUNDTYPE_NEUTRAL = 0;
    public static final int GROUNDTYPE_ROCKY = 7;
    public static final int GROUNDTYPE_SALT = 14;
    public static final int GROUNDTYPE_SALT_WATER = 9;
    public static final int GROUNDTYPE_SANDY = 3;
    public static final int GROUNDTYPE_SNOW = 10;
    ArrayList<GroundType> groundType;
    TextView ground_type;
    ImageView ground_type_image;
    Drawable groundDrawable;
    String groundText = "Neutral";
    float groundCoef = 25.0f;
    SharedPref sp;
    //SharedPreferences sp;

    //SharedPreferences.Editor editor;
    private final String BLUETOOTH_DEVICE_NAME = new consts().getBLUETOOTH_DEVICE_NAME();

    public void _$_clearFindViewByIdCache() {
        HashMap<Integer, View> hashMap = this._$_findViewCache;
        if (hashMap != null) {
            hashMap.clear();
        }
    }

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

    public final String getWhichBtn() {
        return this.whichBtn;
    }

    public final void setWhichBtn(String str) {
        Intrinsics.checkParameterIsNotNull(str, "<set-?>");
        this.whichBtn = str;
    }

    public final boolean is_connected_to_HC() {
        return this.is_connected_to_HC;
    }

    public final void set_connected_to_HC(boolean z) {
        this.is_connected_to_HC = z;
    }

    public final boolean is_setting_on() {
        return this.is_setting_on;
    }

    public final void set_setting_on(boolean z) {
        this.is_setting_on = z;
    }

    public final boolean getDoubleBackToExitPressedOnce() {
        return this.doubleBackToExitPressedOnce;
    }

    public final void setDoubleBackToExitPressedOnce(boolean z) {
        this.doubleBackToExitPressedOnce = z;
    }


    @Override
    // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
         //String username = SharedPref.getPreferences(MainActivity.this).getStringData("username","user");
        String username = SharedPref.getPreferences(MainActivity.this).getStringData("username","user");
        String upperUsername = username.substring(0, 1).toUpperCase() + username.substring(1).toLowerCase();
        int i2= SharedPref.getPreferences(MainActivity.this).getIntData("firebase",0);
        if(i2==0){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

        int i= SharedPref.getPreferences(MainActivity.this).getIntData("autoSave",0);
        if(i==1){
            setContentView(R.layout.activity_main);
            TextView usernameTxt = findViewById(R.id.username_main);

            usernameTxt.setText(upperUsername);
        listeners();
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private final void listeners() {
         /*((ConstraintLayout) _$_findCachedViewById(R.id.btn_goto_linear_diagram)).setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.this.setWhichBtn("linear");
                MainActivity.this.checkBluetooth();
            }
        });*/
        ((FloatingActionButton) _$_findCachedViewById(R.id.logout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this, 0)
                        .setTitle("Logout!")
                        .setMessage("Are you sure ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPref.getPreferences(MainActivity.this).setIntData("autoSave",0);

                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        //.setView(R.drawable.startup_screen_card_background)
                        .setCancelable(false)
                        .create()
                        .show();
            }
        });
        ((ConstraintLayout) _$_findCachedViewById(R.id.btn_goto_waterfall_diagram)).setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                String bluetooth = sp.getPreferences(MainActivity.this).getStringData("Bluetooth","none");
                String otg = sp.getPreferences(MainActivity.this).getStringData("OTG","none");
                LinearLayout bluetooth_show = (LinearLayout) MainActivity.this._$_findCachedViewById(R.id.bluetooth_connection_waterFall);
                LinearLayout otg_show = (LinearLayout) MainActivity.this._$_findCachedViewById(R.id.otg_connection_waterFall);
                LinearLayout not_conneted = (LinearLayout) MainActivity.this._$_findCachedViewById(R.id.exclamtion_connection_waterFall);
                if (Intrinsics.areEqual(bluetooth, "on")){
                    bluetooth_show.setVisibility(View.VISIBLE);
                    otg_show.setVisibility(View.GONE);
                    not_conneted.setVisibility(View.GONE);
                } else if (Intrinsics.areEqual(otg, "on")){
                    otg_show.setVisibility(View.VISIBLE);
                    bluetooth_show.setVisibility(View.GONE);
                    not_conneted.setVisibility(View.GONE);
                } else{
                    not_conneted.setVisibility(View.VISIBLE);
                    otg_show.setVisibility(View.GONE);
                    bluetooth_show.setVisibility(View.GONE);

                }
                LinearLayout main = (LinearLayout) MainActivity.this._$_findCachedViewById(R.id.main);
                Intrinsics.checkExpressionValueIsNotNull(main, "main");
                main.setVisibility(View.GONE);
                LinearLayout WaterFallSetting = (LinearLayout) MainActivity.this._$_findCachedViewById(R.id.WaterFallSetting);
                Intrinsics.checkExpressionValueIsNotNull(WaterFallSetting, "WaterFallSetting");
                WaterFallSetting.setVisibility(View.VISIBLE);
                SeekBar PalsPerColumn = (SeekBar) MainActivity.this._$_findCachedViewById(R.id.PalsPerColumn);
                Intrinsics.checkExpressionValueIsNotNull(PalsPerColumn, "PalsPerColumn");
                RadioButton zigzag = (RadioButton) MainActivity.this._$_findCachedViewById(R.id.zigzag);
                Intrinsics.checkExpressionValueIsNotNull(zigzag, "zigzag");
                zigzag.setChecked(true);
                RadioButton fromFirst = (RadioButton) MainActivity.this._$_findCachedViewById(R.id.fromFirst);
                Intrinsics.checkExpressionValueIsNotNull(fromFirst, "fromFirst");
                fromFirst.setChecked(false);
                MainActivity.this.set_setting_on(true);
            }
        });
        ((ConstraintLayout) _$_findCachedViewById(R.id.btn_goto_surface_diagram)).setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
               String bluetooth = sp.getPreferences(MainActivity.this).getStringData("Bluetooth","off");
               String otg = sp.getPreferences(MainActivity.this).getStringData("OTG","off");
                LinearLayout bluetooth_show = (LinearLayout) MainActivity.this._$_findCachedViewById(R.id.bluetooth_connection);
                LinearLayout otg_show = (LinearLayout) MainActivity.this._$_findCachedViewById(R.id.otg_connection);
                LinearLayout not_conneted = (LinearLayout) MainActivity.this._$_findCachedViewById(R.id.exclamtion_connection);
                if (Intrinsics.areEqual(bluetooth, "on")){
                    bluetooth_show.setVisibility(View.VISIBLE);
                    otg_show.setVisibility(View.GONE);
                    not_conneted.setVisibility(View.GONE);
                } else if (Intrinsics.areEqual(otg, "on")){
                    otg_show.setVisibility(View.VISIBLE);
                    bluetooth_show.setVisibility(View.GONE);
                    not_conneted.setVisibility(View.GONE);
                } else{
                    not_conneted.setVisibility(View.VISIBLE);
                    otg_show.setVisibility(View.GONE);
                    bluetooth_show.setVisibility(View.GONE);

                }
                LinearLayout main = (LinearLayout) MainActivity.this._$_findCachedViewById(R.id.main);
                Intrinsics.checkExpressionValueIsNotNull(main, "main");
                main.setVisibility(View.GONE);
                LinearLayout setting = (LinearLayout) MainActivity.this._$_findCachedViewById(R.id.setting);
                Intrinsics.checkExpressionValueIsNotNull(setting, "setting");
                setting.setVisibility(View.VISIBLE);
                /*INITIAL VIEWS*/
                SeekBar SleepTime = (SeekBar) MainActivity.this._$_findCachedViewById(R.id.SleepTime);
                Intrinsics.checkExpressionValueIsNotNull(SleepTime, "SleepTime");
                SleepTime.setProgress(1);
                SeekBar PalsPerColumn = (SeekBar) MainActivity.this._$_findCachedViewById(R.id.PalsPerColumn);
                Intrinsics.checkExpressionValueIsNotNull(PalsPerColumn, "PalsPerColumn");
                PalsPerColumn.setProgress(2);
                SeekBar PalsPerRow = (SeekBar) MainActivity.this._$_findCachedViewById(R.id.PalsPerRow);
                Intrinsics.checkExpressionValueIsNotNull(PalsPerRow, "PalsPerRow");
                PalsPerRow.setProgress(2);
                RadioButton zigzag = (RadioButton) MainActivity.this._$_findCachedViewById(R.id.zigzag);
                Intrinsics.checkExpressionValueIsNotNull(zigzag, "zigzag");
                zigzag.setChecked(true);
                RadioButton fromFirst = (RadioButton) MainActivity.this._$_findCachedViewById(R.id.fromFirst);
                Intrinsics.checkExpressionValueIsNotNull(fromFirst, "fromFirst");
                fromFirst.setChecked(false);
                TextView errors = (TextView) MainActivity.this._$_findCachedViewById(R.id.errors);
                Intrinsics.checkExpressionValueIsNotNull(errors, "errors");
                errors.setVisibility(View.GONE);

                TextView receive_txt = (TextView) MainActivity.this._$_findCachedViewById(R.id.receive_txt);
                Intrinsics.checkExpressionValueIsNotNull(receive_txt, "receive_txt");
                receive_txt.setText("Receive time (s) : 1");
                TextView palsPer_txt = (TextView) MainActivity.this._$_findCachedViewById(R.id.palsPer_txt);
                Intrinsics.checkExpressionValueIsNotNull(palsPer_txt, "palsPer_txt");
                palsPer_txt.setText("Number of columns : 2");
                TextView palsPerRow_txt = (TextView) MainActivity.this._$_findCachedViewById(R.id.palsPerRow_txt);
                Intrinsics.checkExpressionValueIsNotNull(palsPerRow_txt, "palsPerRow_txt");
                palsPerRow_txt.setText("Number of rows : 2");
                MainActivity.this.set_setting_on(true);
            }
        });
        ((ConstraintLayout) _$_findCachedViewById(R.id.btn_goto_load_diagram)).setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, MainActivity2.class));
            }
        });
        ((Button) _$_findCachedViewById(R.id.btn_Submit)).setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SeekBar SleepTime = (SeekBar) MainActivity.this._$_findCachedViewById(R.id.SleepTime);
                String bluetooth = SharedPref.getPreferences(MainActivity.this).getStringData("Bluetooth","off");
                String otg = SharedPref.getPreferences(MainActivity.this).getStringData("OTG","off");
                Intrinsics.checkExpressionValueIsNotNull(SleepTime, "SleepTime");
                if (Intrinsics.areEqual(bluetooth, "on")||Intrinsics.areEqual(otg, "on")) {
                    if (!Intrinsics.areEqual(SleepTime.getProgress(), 0)) {
                        SeekBar PalsPerColumn = (SeekBar) MainActivity.this._$_findCachedViewById(R.id.PalsPerColumn);
                        Intrinsics.checkExpressionValueIsNotNull(PalsPerColumn, "PalsPerColumn");

                        SeekBar PalsPerRow = (SeekBar) MainActivity.this._$_findCachedViewById(R.id.PalsPerRow);
                        Intrinsics.checkExpressionValueIsNotNull(PalsPerRow, "PalsPerRow");
                        if ((!Intrinsics.areEqual(PalsPerColumn.getProgress(), 0)) && (!Intrinsics.areEqual(PalsPerRow.getProgress(), 0))) {
                            RadioGroup palsOrientation = (RadioGroup) MainActivity.this._$_findCachedViewById(R.id.palsOrientation);
                            Intrinsics.checkExpressionValueIsNotNull(palsOrientation, "palsOrientation");
                            RadioButton zigzag = (RadioButton) MainActivity.this._$_findCachedViewById(R.id.zigzag);
                            Intrinsics.checkExpressionValueIsNotNull(zigzag, "zigzag");

                            RadioButton fromFirst = (RadioButton) MainActivity.this._$_findCachedViewById(R.id.fromFirst);
                            Intrinsics.checkExpressionValueIsNotNull(fromFirst, "fromFirst");

                            if ((!Intrinsics.areEqual(fromFirst.isChecked(), false)) || (!Intrinsics.areEqual(zigzag.isChecked(), false))) {
                                MainActivity.this.setWhichBtn("surface");
                                MainActivity.this.checkBluetooth();
                                LinearLayout main = (LinearLayout) MainActivity.this._$_findCachedViewById(R.id.main);
                                Intrinsics.checkExpressionValueIsNotNull(main, "main");
                                main.setVisibility(View.VISIBLE);
                                LinearLayout setting = (LinearLayout) MainActivity.this._$_findCachedViewById(R.id.setting);
                                Intrinsics.checkExpressionValueIsNotNull(setting, "setting");
                                setting.setVisibility(View.GONE);
                                return;
                            }
                            TextView errors = (TextView) MainActivity.this._$_findCachedViewById(R.id.errors);
                            Intrinsics.checkExpressionValueIsNotNull(errors, "errors");
                            errors.setVisibility(View.VISIBLE);
                            TextView errors2 = (TextView) MainActivity.this._$_findCachedViewById(R.id.errors);
                            Intrinsics.checkExpressionValueIsNotNull(errors2, "errors");
                            errors2.setText("Please select one of Scan Orientations");

                            return;
                        }
                    }
                    TextView errors3 = (TextView) MainActivity.this._$_findCachedViewById(R.id.errors);
                    Intrinsics.checkExpressionValueIsNotNull(errors3, "errors");
                    errors3.setVisibility(View.VISIBLE);
                    TextView errors4 = (TextView) MainActivity.this._$_findCachedViewById(R.id.errors);
                    Intrinsics.checkExpressionValueIsNotNull(errors4, "errors");
                    errors4.setText("All fields must be completed");
                }else{
                    Alerts.show_alert(MainActivity.this,"Not Connected !","Please Choose A Connection Method From Settings");

                }
            }
        });
        ((Button) _$_findCachedViewById(R.id.btn_Cancel)).setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SeekBar SleepTime = (SeekBar) MainActivity.this._$_findCachedViewById(R.id.SleepTime);
                Intrinsics.checkExpressionValueIsNotNull(SleepTime, "SleepTime");
                SleepTime.setProgress(0);
                SeekBar PalsPerColumn = (SeekBar) MainActivity.this._$_findCachedViewById(R.id.PalsPerColumn);
                Intrinsics.checkExpressionValueIsNotNull(PalsPerColumn, "PalsPerColumn");
                PalsPerColumn.setProgress(0);
                RadioButton zigzag = (RadioButton) MainActivity.this._$_findCachedViewById(R.id.zigzag);
                Intrinsics.checkExpressionValueIsNotNull(zigzag, "zigzag");
                zigzag.setChecked(true);
                RadioButton fromFirst = (RadioButton) MainActivity.this._$_findCachedViewById(R.id.fromFirst);
                Intrinsics.checkExpressionValueIsNotNull(fromFirst, "fromFirst");
                fromFirst.setChecked(false);
                TextView errors = (TextView) MainActivity.this._$_findCachedViewById(R.id.errors);
                Intrinsics.checkExpressionValueIsNotNull(errors, "errors");
                errors.setVisibility(View.GONE);
                LinearLayout main = (LinearLayout) MainActivity.this._$_findCachedViewById(R.id.main);
                Intrinsics.checkExpressionValueIsNotNull(main, "main");
                main.setVisibility(View.VISIBLE);
                LinearLayout setting = (LinearLayout) MainActivity.this._$_findCachedViewById(R.id.setting);
                Intrinsics.checkExpressionValueIsNotNull(setting, "setting");
                setting.setVisibility(View.GONE);
            }
        });
        ((SeekBar) _$_findCachedViewById(R.id.SleepTime)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                TextView receive_txt = (TextView) MainActivity.this._$_findCachedViewById(R.id.receive_txt);
                Intrinsics.checkExpressionValueIsNotNull(receive_txt, "receive_txt");
                receive_txt.setText("Receive time (s) : " + i);
            }
        });
        ((SeekBar) _$_findCachedViewById(R.id.PalsPerColumn)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                TextView palsPer_txt = (TextView) MainActivity.this._$_findCachedViewById(R.id.palsPer_txt);
                Intrinsics.checkExpressionValueIsNotNull(palsPer_txt, "palsPer_txt");
                palsPer_txt.setText("Number of columns : " + i);
            }
        });
        ((SeekBar) _$_findCachedViewById(R.id.PalsPerRow)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                TextView palsPerRow_txt = (TextView) MainActivity.this._$_findCachedViewById(R.id.palsPerRow_txt);
                Intrinsics.checkExpressionValueIsNotNull(palsPerRow_txt, "palsPerRow_txt");
                //palsPerRow_txt.setText("Number of rows : " + (i * 2));
                palsPerRow_txt.setText("Number of rows : " + i);
            }
        });
        ((Button) _$_findCachedViewById(R.id.btn_WaterFall_Submit)).setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                String bluetooth = SharedPref.getPreferences(MainActivity.this).getStringData("Bluetooth","off");
                String otg = SharedPref.getPreferences(MainActivity.this).getStringData("OTG","off");
                SeekBar WaterFallSleepTime = (SeekBar) MainActivity.this._$_findCachedViewById(R.id.WaterFallSleepTime);
                Intrinsics.checkExpressionValueIsNotNull(WaterFallSleepTime, "WaterFallSleepTime");
                if (Intrinsics.areEqual(bluetooth, "on")||Intrinsics.areEqual(otg, "on")) {
                    if (!Intrinsics.areEqual(String.valueOf(WaterFallSleepTime.getProgress()), "")) {
                        MainActivity.this.setWhichBtn("waterfall");
                        MainActivity.this.checkBluetooth();
                        LinearLayout main = (LinearLayout) MainActivity.this._$_findCachedViewById(R.id.main);
                        Intrinsics.checkExpressionValueIsNotNull(main, "main");
                        main.setVisibility(View.VISIBLE);
                        LinearLayout WaterFallSetting = (LinearLayout) MainActivity.this._$_findCachedViewById(R.id.WaterFallSetting);
                        Intrinsics.checkExpressionValueIsNotNull(WaterFallSetting, "WaterFallSetting");
                        WaterFallSetting.setVisibility(View.GONE);
                        return;
                    }
                    TextView WaterFallErrors = (TextView) MainActivity.this._$_findCachedViewById(R.id.WaterFallErrors);
                    Intrinsics.checkExpressionValueIsNotNull(WaterFallErrors, "WaterFallErrors");
                    WaterFallErrors.setVisibility(View.VISIBLE);
                    TextView WaterFallErrors2 = (TextView) MainActivity.this._$_findCachedViewById(R.id.WaterFallErrors);
                    Intrinsics.checkExpressionValueIsNotNull(WaterFallErrors2, "WaterFallErrors");
                    WaterFallErrors2.setText("All fields must be completed");
                }else{
                    Alerts.show_alert(MainActivity.this,"Not Connected !","Please Choose A Connection Method From Settings");

                }
            }
        });
        ((Button) _$_findCachedViewById(R.id.btn_WaterFall_Cancel)).setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SeekBar WaterFallSleepTime = (SeekBar) MainActivity.this._$_findCachedViewById(R.id.WaterFallSleepTime);
                Intrinsics.checkExpressionValueIsNotNull(WaterFallSleepTime, "WaterFallSleepTime");
                WaterFallSleepTime.setProgress(1);
                TextView WaterFallErrors = (TextView) MainActivity.this._$_findCachedViewById(R.id.WaterFallErrors);
                Intrinsics.checkExpressionValueIsNotNull(WaterFallErrors, "WaterFallErrors");
                WaterFallErrors.setVisibility(View.GONE);
                LinearLayout main = (LinearLayout) MainActivity.this._$_findCachedViewById(R.id.main);
                Intrinsics.checkExpressionValueIsNotNull(main, "main");
                main.setVisibility(View.VISIBLE);
                LinearLayout WaterFallSetting = (LinearLayout) MainActivity.this._$_findCachedViewById(R.id.WaterFallSetting);
                Intrinsics.checkExpressionValueIsNotNull(WaterFallSetting, "WaterFallSetting");
                WaterFallSetting.setVisibility(View.GONE);
            }
        });
        ((SeekBar) _$_findCachedViewById(R.id.WaterFallSleepTime)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (i == 1) {
                    TextView WaterFallReceive_txt = (TextView) MainActivity.this._$_findCachedViewById(R.id.WaterFallReceive_txt);
                    Intrinsics.checkExpressionValueIsNotNull(WaterFallReceive_txt, "WaterFallReceive_txt");
                    WaterFallReceive_txt.setText("Receive Time (ms) : 100");
                    return;
                }
                TextView WaterFallReceive_txt2 = (TextView) MainActivity.this._$_findCachedViewById(R.id.WaterFallReceive_txt);
                Intrinsics.checkExpressionValueIsNotNull(WaterFallReceive_txt2, "WaterFallReceive_txt");
                StringBuilder sb = new StringBuilder();
                sb.append("Receive Time (ms) : ");
                SeekBar WaterFallSleepTime = (SeekBar) MainActivity.this._$_findCachedViewById(R.id.WaterFallSleepTime);
                Intrinsics.checkExpressionValueIsNotNull(WaterFallSleepTime, "WaterFallSleepTime");
                sb.append(WaterFallSleepTime.getProgress() * 100);
                WaterFallReceive_txt2.setText(sb.toString());
            }
        });
        ((ImageView) _$_findCachedViewById(R.id.ground_type_edit)).setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {


                groundType = new ArrayList<>();

                groundType.add(new GroundType("NEUTRAL", getDrawable(R.drawable.scale_neutral)));
                groundType.add(new GroundType("CONCRETE", getDrawable(R.drawable.scale_concrete)));
                groundType.add(new GroundType("SANDY", getDrawable(R.drawable.scale_sandy)));
                groundType.add(new GroundType("CLAY", getDrawable(R.drawable.scale_clay)));
                groundType.add(new GroundType("LIGHT MINERAL", getDrawable(R.drawable.scale_dense_mineral)));
                groundType.add(new GroundType("DENSE MINERAL", getDrawable(R.drawable.scale_dense_mineral)));
                groundType.add(new GroundType("ROCKY", getDrawable(R.drawable.scale_rocky)));
                groundType.add(new GroundType("FRESH WATER", getDrawable(R.drawable.scale_fresh_water)));
                groundType.add(new GroundType("SALT WATER", getDrawable(R.drawable.scale_salt_water)));
                groundType.add(new GroundType("SNOW", getDrawable(R.drawable.scale_snowy)));
                groundType.add(new GroundType("FROZEN SOIL", getDrawable(R.drawable.scale_frozen)));
                groundType.add(new GroundType("COAL", getDrawable(R.drawable.scale_coal)));
                groundType.add(new GroundType("GRANITE", getDrawable(R.drawable.scale_granite)));
                groundType.add(new GroundType("SALT", getDrawable(R.drawable.scale_salt)));
                GridView gridView = new GridView(MainActivity.this);
                GroundTypesAdapter groundListAdapter = new GroundTypesAdapter(groundType, getApplicationContext());
                gridView.setAdapter(groundListAdapter);
                gridView.setNumColumns(2);
                gridView.setGravity(Gravity.CENTER);

                gridView.setHorizontalSpacing(5);
                gridView.setVerticalSpacing(5);


                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Ground Type");
                // View title = new TextView(SurfaceDiagramActivity.this);
                //builder.setCustomTitle(title);


                builder.setView(gridView);
                final AlertDialog dialog = builder.show();

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        switch (position) {
                            case 0:
                                groundCoef = MainActivity.this.getCoefficient(GROUNDTYPE_NEUTRAL);
                                groundDrawable = getDrawable(R.drawable.scale_neutral);
                                groundText="Neutral";
                                break;
                            case 1:
                                groundCoef = MainActivity.this.getCoefficient(GROUNDTYPE_CONCRETE);
                                groundText= "Concrete";
                                groundDrawable =   getDrawable(R.drawable.scale_concrete);
                                break;

                            case 2:
                                groundCoef = MainActivity.this.getCoefficient(GROUNDTYPE_SANDY);
                                groundDrawable = getDrawable(R.drawable.scale_sandy);
                                groundText="Sandy";
                                break;
                            case 3:
                                groundCoef = MainActivity.this.getCoefficient(GROUNDTYPE_CLAY);
                                groundDrawable = getDrawable(R.drawable.scale_clay);
                                groundText="Clay";
                                break;
                            case 4:
                                groundCoef = MainActivity.this.getCoefficient(GROUNDTYPE_LIGHT_MINERAL);
                                groundDrawable = getDrawable(R.drawable.scale_dense_mineral);
                                groundText="Light Mineral";
                                break;
                            case 5:
                                groundCoef = MainActivity.this.getCoefficient(GROUNDTYPE_DENSE_MINERAL);
                                groundDrawable = getDrawable(R.drawable.scale_dense_mineral);
                                groundText="Dense Mineral";
                                break;
                            case 6:
                                groundCoef = MainActivity.this.getCoefficient(GROUNDTYPE_ROCKY);
                                groundDrawable = getDrawable(R.drawable.scale_rocky);
                                groundText="Rocky";
                                break;
                            case 7:
                                groundCoef = MainActivity.this.getCoefficient(GROUNDTYPE_FRESH_WATER);
                                groundDrawable = getDrawable(R.drawable.scale_fresh_water);
                                groundText="Fresh Water";
                                break;
                            case 8:
                                groundCoef = MainActivity.this.getCoefficient(GROUNDTYPE_SALT_WATER);
                                groundDrawable = getDrawable(R.drawable.scale_salt_water);
                                groundText="Salt Water";
                                break;
                            case 9:
                                groundCoef = MainActivity.this.getCoefficient(GROUNDTYPE_SNOW);
                                groundDrawable = getDrawable(R.drawable.scale_snowy);
                                groundText="Snowy";
                                break;
                            case 10:
                                groundCoef = MainActivity.this.getCoefficient(GROUNDTYPE_FROZEN_SOIL);
                                groundDrawable = getDrawable(R.drawable.scale_frozen);
                                groundText="Frozen Soil";
                                break;
                            case 11:
                                groundCoef = MainActivity.this.getCoefficient(GROUNDTYPE_COAL);
                                groundDrawable = getDrawable(R.drawable.scale_coal);
                                groundText="Coal";
                                break;
                            case 12:
                                groundCoef = MainActivity.this.getCoefficient(GROUNDTYPE_GRANITE);
                                groundDrawable = getDrawable(R.drawable.scale_granite);
                                groundText="Granite";
                                break;
                            case 13:
                                groundCoef = MainActivity.this.getCoefficient(GROUNDTYPE_SALT);
                                groundDrawable = getDrawable(R.drawable.scale_salt);
                                groundText="Salt";
                                break;
                        }

                        Log.d("groundCoef", " " + groundCoef);
                        dialog.dismiss();

                        TextView groundTypeText = (TextView) MainActivity.this._$_findCachedViewById(R.id.ground_type_text);
                        ImageView groundTypeImage = (ImageView) MainActivity.this._$_findCachedViewById(R.id.ground_type_image);
                        groundTypeText.setText(groundText);
                        groundTypeImage.setBackground(groundDrawable);

                    }
                });
            }
        });
    }



    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        if (!this.is_setting_on) {
            if (this.doubleBackToExitPressedOnce) {
                super.onBackPressed();
                finish();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override // java.lang.Runnable
                public final void run() {
                    MainActivity.this.setDoubleBackToExitPressedOnce(false);
                }
            }, 2000L);
            return;
        }
        LinearLayout main = (LinearLayout) _$_findCachedViewById(R.id.main);
        Intrinsics.checkExpressionValueIsNotNull(main, "main");
        main.setVisibility(View.VISIBLE);
        LinearLayout WaterFallSetting = (LinearLayout) _$_findCachedViewById(R.id.WaterFallSetting);
        Intrinsics.checkExpressionValueIsNotNull(WaterFallSetting, "WaterFallSetting");
        WaterFallSetting.setVisibility(View.GONE);
        LinearLayout setting = (LinearLayout) _$_findCachedViewById(R.id.setting);
        Intrinsics.checkExpressionValueIsNotNull(setting, "setting");
        setting.setVisibility(View.GONE);
        this.is_setting_on = false;
    }

    public float getCoefficient(int groundType) {
                switch (groundType) {
                    case GROUNDTYPE_CONCRETE:
                        return 2.0f;
                    // case GROUNDTYPE_MILE:
                    case GROUNDTYPE_ROCKY:
                        return 5.0f;
                    case GROUNDTYPE_SANDY:
                        return 3.0f;
                    case GROUNDTYPE_CLAY:
                        return 4.0f;
                    case GROUNDTYPE_LIGHT_MINERAL:
                        return 3.0f;
                    case GROUNDTYPE_DENSE_MINERAL:
                        return 1.0f;
                    case GROUNDTYPE_FRESH_WATER:
                        return 10.0f;
                    case GROUNDTYPE_SALT_WATER:
                        return 15.0f;
                    case GROUNDTYPE_SNOW:
                        return 8.0f;
                    case GROUNDTYPE_FROZEN_SOIL:
                        return 6.5f;
                    case GROUNDTYPE_COAL:
                        return 2.5f;
                    case GROUNDTYPE_GRANITE:
                        return 6.0f;
                    case GROUNDTYPE_SALT:
                        return 7.5f;
                    default:
                        return 25.0f;
                }
            }
    public float getGroundDrawable(int groundType) {
        switch (groundType) {
            case GROUNDTYPE_CONCRETE:
                return 2.0f;
            // case GROUNDTYPE_MILE:
            case GROUNDTYPE_ROCKY:
                return 5.0f;
            case GROUNDTYPE_SANDY:
                return 3.0f;
            case GROUNDTYPE_CLAY:
                return 4.0f;
            case GROUNDTYPE_LIGHT_MINERAL:
                return 3.0f;
            case GROUNDTYPE_DENSE_MINERAL:
                return 1.0f;
            case GROUNDTYPE_FRESH_WATER:
                return 10.0f;
            case GROUNDTYPE_SALT_WATER:
                return 15.0f;
            case GROUNDTYPE_SNOW:
                return 8.0f;
            case GROUNDTYPE_FROZEN_SOIL:
                return 6.5f;
            case GROUNDTYPE_COAL:
                return 2.5f;
            case GROUNDTYPE_GRANITE:
                return 6.0f;
            case GROUNDTYPE_SALT:
                return 7.5f;
            default:
                return 25.0f;
        }
    }

    /*public void checkBluetooth() {
        int progress;
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            if (defaultAdapter.isEnabled()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    requestPermissions(new String[]{"android.permission.BLUETOOTH", "android.permission.BLUETOOTH_ADMIN", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, 200);

                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Set<BluetoothDevice> bondedDevices = defaultAdapter.getBondedDevices();
                if (bondedDevices != null) {
                    for (BluetoothDevice bluetoothDevice : bondedDevices) {
                        if (Intrinsics.areEqual(bluetoothDevice.getName(), this.BLUETOOTH_DEVICE_NAME)) {
                            if (Intrinsics.areEqual(this.whichBtn, "surface")) {
                                Intent intent = new Intent(this, SurfaceDiagramActivity.class);
                                intent.putExtra("mBTDevice", bluetoothDevice);
                                SeekBar SleepTime = (SeekBar) _$_findCachedViewById(R.id.SleepTime);
                                Intrinsics.checkExpressionValueIsNotNull(SleepTime, "SleepTime");
                                intent.putExtra("SleepTime", String.valueOf(SleepTime.getProgress()));
                                SeekBar PalsPerColumn = (SeekBar) _$_findCachedViewById(R.id.PalsPerColumn);
                                Intrinsics.checkExpressionValueIsNotNull(PalsPerColumn, "PalsPerColumn");
                                intent.putExtra("PalsPerColumn", String.valueOf(PalsPerColumn.getProgress()));
                                SeekBar PalsPerRow = (SeekBar) _$_findCachedViewById(R.id.PalsPerRow);
                                Intrinsics.checkExpressionValueIsNotNull(PalsPerRow, "PalsPerRow");
                                intent.putExtra("PalsPerRow", String.valueOf(PalsPerRow.getProgress() * 2));
                                RadioGroup palsOrientation = (RadioGroup) _$_findCachedViewById(R.id.palsOrientation);
                                Intrinsics.checkExpressionValueIsNotNull(palsOrientation, "palsOrientation");
                                View findViewById = findViewById(palsOrientation.getCheckedRadioButtonId());
                                Intrinsics.checkExpressionValueIsNotNull(findViewById, "findViewById<RadioButton…ion.checkedRadioButtonId)");
                                intent.putExtra("palsOrientation", ((RadioButton) findViewById).getText());
                                startActivity(intent);
                            } else if (Intrinsics.areEqual(this.whichBtn, "linear")) {
                                Intent intent2 = new Intent(this, LinearDiagramActivity.class);
                                intent2.putExtra("mBTDevice", bluetoothDevice);
                                startActivity(intent2);
                            } else if (Intrinsics.areEqual(this.whichBtn, "waterfall")) {
                                SeekBar WaterFallSleepTime = (SeekBar) _$_findCachedViewById(R.id.WaterFallSleepTime);
                                Intrinsics.checkExpressionValueIsNotNull(WaterFallSleepTime, "WaterFallSleepTime");
                                if (WaterFallSleepTime.getProgress() == 0) {
                                    progress = 1;
                                } else {
                                    SeekBar WaterFallSleepTime2 = (SeekBar) _$_findCachedViewById(R.id.WaterFallSleepTime);
                                    Intrinsics.checkExpressionValueIsNotNull(WaterFallSleepTime2, "WaterFallSleepTime");
                                    progress = WaterFallSleepTime2.getProgress() * 100;
                                }
                                Intent intent3 = new Intent(this, WaterFallDiagramActivity.class);
                                intent3.putExtra("SleepTime", String.valueOf(progress));
                                intent3.putExtra("mBTDevice", bluetoothDevice);
                                startActivity(intent3);
                            }
                            this.is_connected_to_HC = true;
                        }
                    }
                }
                if (!this.is_connected_to_HC) {
                    Toast.makeText(this, "Please pair to sensors Bluetooth", Toast.LENGTH_LONG).show();
                }
                this.is_connected_to_HC = false;
                return;
            }
            Toast.makeText(this, "Please turn on the bluetooth", Toast.LENGTH_LONG).show();
        }
    }*/
        public void checkBluetooth() {
                               int progress;
                            if (Intrinsics.areEqual(this.whichBtn, "surface")) {

                                Intent intent = new Intent(this, SurfaceDiagramActivity.class);
                                //intent.putExtra("mBTDevice", "HC-05");
                                SeekBar SleepTime = (SeekBar) _$_findCachedViewById(R.id.SleepTime);
                                Intrinsics.checkExpressionValueIsNotNull(SleepTime, "SleepTime");
                                intent.putExtra("SleepTime", String.valueOf(SleepTime.getProgress()));
                                SeekBar PalsPerColumn = (SeekBar) _$_findCachedViewById(R.id.PalsPerColumn);
                                Intrinsics.checkExpressionValueIsNotNull(PalsPerColumn, "PalsPerColumn");
                                intent.putExtra("PalsPerColumn", String.valueOf(PalsPerColumn.getProgress()));
                                SeekBar PalsPerRow = (SeekBar) _$_findCachedViewById(R.id.PalsPerRow);
                                Intrinsics.checkExpressionValueIsNotNull(PalsPerRow, "PalsPerRow");
                                //intent.putExtra("PalsPerRow", String.valueOf(PalsPerRow.getProgress() * 2));
                                intent.putExtra("PalsPerRow", String.valueOf(PalsPerRow.getProgress()));
                                RadioGroup palsOrientation = (RadioGroup) _$_findCachedViewById(R.id.palsOrientation);
                                Intrinsics.checkExpressionValueIsNotNull(palsOrientation, "palsOrientation");
                                View findViewById = findViewById(palsOrientation.getCheckedRadioButtonId());
                                Intrinsics.checkExpressionValueIsNotNull(findViewById, "findViewById<RadioButton…ion.checkedRadioButtonId)");
                                intent.putExtra("palsOrientation", ((RadioButton) findViewById).getText());
                                intent.putExtra("groundCoef",groundCoef);
                                intent.putExtra("groundText",groundText);
                                startActivity(intent);

                            /*} else if (Intrinsics.areEqual(this.whichBtn, "linear")) {
                                String bluetooth = SharedPref.getPreferences(MainActivity.this).getStringData("Bluetooth","off");
                                String otg = SharedPref.getPreferences(MainActivity.this).getStringData("OTG","off");
                                if (Intrinsics.areEqual(bluetooth, "on")||Intrinsics.areEqual(otg, "on")) {
                                Intent intent2 = new Intent(this, LinearDiagramActivity.class);
                                //intent2.putExtra("mBTDevice", "HC-05");
                                startActivity(intent2);
                                }else{
                                    Alerts.show_alert(MainActivity.this,"Not Connected !","Please Choose A Connection Method From Settings");

                                }*/

                            } else if (Intrinsics.areEqual(this.whichBtn, "waterfall")) {
                                SeekBar WaterFallSleepTime = (SeekBar) _$_findCachedViewById(R.id.WaterFallSleepTime);
                                Intrinsics.checkExpressionValueIsNotNull(WaterFallSleepTime, "WaterFallSleepTime");

                                if (WaterFallSleepTime.getProgress() == 0) {
                                    progress = 1;
                                } else {
                                    SeekBar WaterFallSleepTime2 = (SeekBar) _$_findCachedViewById(R.id.WaterFallSleepTime);
                                    Intrinsics.checkExpressionValueIsNotNull(WaterFallSleepTime2, "WaterFallSleepTime");
                                    progress = WaterFallSleepTime2.getProgress() * 100;
                                }
                                Intent intent3 = new Intent(this, WaterFallDiagramActivity.class);
                                intent3.putExtra("SleepTime", String.valueOf(progress));
                                //intent3.putExtra("mBTDevice", "HC-05");
                                startActivity(intent3);
                            }

                                this.is_connected_to_HC = true;
                       }



}
