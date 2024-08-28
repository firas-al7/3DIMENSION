package com.dimension.a3dimension.graphics.Activities.diagrams;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dimension.a3dimension.graphics.CustomAdapter;
import com.dimension.a3dimension.graphics.Item;
import com.dimension.a3dimension.graphics.TinyDB;
import com.dimension.a3dimension.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;


public class LoadDiagramActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_STORAGE = 1000;
    private static final int READ_REQUEST_FILE = 42;
    Button add_diagram;
    ArrayList<Item> arrayItem = new ArrayList<>();
    ListView list_diagrams;
    CustomAdapter mAdapter;


    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_load_diagram);
        Toolbar toolbar =  findViewById(R.id.appToolbar3);
        TextView toolbarTitle = findViewById(R.id.toolbarTitle3);
        setSupportActionBar(toolbar);

        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowHomeEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            toolbarTitle.setText("View Scan");
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        this.list_diagrams = (ListView) findViewById(R.id.list_diagrams);
        //this.add_diagram = (Button) findViewById(R.id.add_diagram);
        this.mAdapter = new CustomAdapter(this, this.arrayItem);
        TinyDB tinyDB = new TinyDB(this);
        //tinyDB.remove("*datetime_@scan1");
        Map<String, ?> all = tinyDB.getAll();
        String[] strArr = new String[all.size()];
        String datetime;
        int i = 0;
        for (String str : all.keySet()) {
            if (str.length() > 3 && (str.toCharArray()[0] != '*' || str.toCharArray()[1] != '_' || str.toCharArray()[2] != '@')) {
                if (!str.contains("numbersXYZ_") && !str.contains("realNumbers_") && !str.contains("loc_") && !str.contains("colorsString_") && !str.contains("currentLoc")&& !str.contains("*datetime_@")&& !str.contains("*groundCoef_@")&& !str.contains("*groundName_@")) {
                    strArr[i] = str;
                    datetime=tinyDB.getString("*datetime_@" + str);
                    this.arrayItem.add(new Item(str,datetime));
                    i++;
                }
            }
        }
        this.list_diagrams.setAdapter((ListAdapter) this.mAdapter);
       /* this.add_diagram.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (LoadDiagramActivity.this.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {

                    LoadDiagramActivity.this.performFileSearch();


                } else {
                    LoadDiagramActivity.this.requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 1000);
                }
                LoadDiagramActivity.this.performFileSearch();
            }
        });*/
    }

    private String readText(String str) {
        File file = new File(Environment.getExternalStorageDirectory(), str);
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                sb.append(readLine);
                sb.append("\n");
            }
            bufferedReader.close();
            Log.d("permission", sb.toString());
        } catch (IOException unused) {
        }
        return sb.toString();
    }


    public void performFileSearch() {

        Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT");
        intent.addCategory("android.intent.category.OPENABLE");
        intent.setType("text/*");
        startActivityForResult(intent, 42);
    }


    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        //Toast.makeText(LoadDiagramActivity.this, "inside onActivityResult", Toast.LENGTH_SHORT).show();
        if (i == 42 && i2 == -1 && intent != null) {
            String path = intent.getData().getPath();
            String substring = path.substring(path.indexOf(":") + 1);
            if (substring.contains("emulated")) {
                substring = substring.substring(substring.indexOf("0") + 1);
            }
            String readText = readText(substring);
            Log.d("myTag", readText);
            int i3 = 0;
            for (char c : readText(substring).toCharArray()) {
                if (c == '~') {
                    i3++;
                }
            }
            if (i3 == 3) {
                TinyDB tinyDB = new TinyDB(getApplicationContext());
                String[] split = substring.split("/");
                String str = split[split.length - 1] + " EXTERNAL";
                tinyDB.putString(str, readText.split("~")[0]);
                tinyDB.putString("*realNumbers_@" + str, readText.split("~")[1]);
                tinyDB.putString("*numbersXYZ_@" + str, readText.split("~")[2]);
                tinyDB.putString("*colorsString_@" + str, readText.split("~")[3]);

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                tinyDB.putString("*datetime_@" + str, dtf.format(now)); /*dtf.format(now)*/
                tinyDB.putFloat("*groundCoef_@" + str, 25.f);
                tinyDB.putString("*groundName_@" + str, "NEUTRAL");
                Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
                onRestart();
                return;
            }
            Toast.makeText(this, "corrupted file", Toast.LENGTH_LONG).show();
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity, androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 1000 && iArr[0] == 0) {
            Log.d("permission", "permission granted!!");
        }
    }

    @Override // android.app.Activity
    protected void onRestart() {
        super.onRestart();
        this.arrayItem.clear();
        TinyDB tinyDB = new TinyDB(this);
        Map<String, ?> all = tinyDB.getAll();
        String[] strArr = new String[all.size()];
        String datetime;
        int i = 0;
        for (String str : all.keySet()) {
            if (str.length() > 3 && (str.toCharArray()[0] != '*' || str.toCharArray()[1] != '_' || str.toCharArray()[2] != '@')) {
                if (!str.contains("numbersXYZ_") && !str.contains("realNumbers_") && !str.contains("loc_") && !str.contains("colorsString_") && !str.contains("currentLoc")&& !str.contains("*datetime_@")&& !str.contains("*groundCoef_@")&& !str.contains("*groundName_@")) {
                    strArr[i] = str;
                    datetime=tinyDB.getString("*datetime_@" + str);
                    this.arrayItem.add(new Item(str,datetime));
                    i++;
                }
            }
        }
        this.mAdapter.notifyDataSetChanged();
    }
}
