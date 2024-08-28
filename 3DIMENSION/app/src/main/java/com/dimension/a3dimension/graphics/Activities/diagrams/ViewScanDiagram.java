package com.dimension.a3dimension.graphics.Activities.diagrams;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;

import com.dimension.a3dimension.GroundTypesAdapter;
import com.dimension.a3dimension.R;
import com.dimension.a3dimension.graphics.Activities.TexturedSurfaceShape;
import com.dimension.a3dimension.graphics.SharedPref;
import com.dimension.a3dimension.graphics.SurfaceShape;
import com.dimension.a3dimension.graphics.TinyDB;
import com.dimension.a3dimension.models.GroundType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Objects;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class ViewScanDiagram extends AppCompatActivity {

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
    ConstraintLayout Controls_load;
    ImageView Delete;
    ImageView Diagram_Loc;
    ImageView Edit;
    LinearLayout Edit_board;
    String Loc;
    float Mid;
    ImageView Share;

    Button btn_ground_type_change;
    Button btn_2d_depth_show_load;
    Button btn_2d_numbers_show_load;
    Button btn_3d_favorable_show_load;
    Button btn_Collaps_load;
    Button btn_Collaps_moving_load;
    Button btn_Edit_Save;
    Button btn_move_down_load;
    Button btn_move_left_load;
    Button btn_move_right_load;
    Button btn_move_up_load;
    ImageView btn_rotate;
    ImageView btn_return_view;
    String diagramName;
    private GLSurfaceView glView;
    TextView increase_unit;
    LinearLayout linearLayout;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private int mColorAccent;
    private int mColorBackground;
    private int mColorRectangle;
    TextView main_unit_txt;
    ConstraintLayout moving_Controls_load;
    TextView reduce_unit;
    MyGLRenderer renderer;
    TinyDB tinyDB;
    EditText txt_edit_saving_data;
    ImageView zoom_in;
    ImageView zoom_out;
    boolean doubleBackToExitPressedOnce = false;
   boolean userWantsDepth = false;
    boolean userWantsNumbers = false;
    boolean userWantsFavorable = true;
    boolean sure_delete = false;
    boolean controls_collapsed_load = false;
    boolean moving_controls_collapsed_load = false;
    boolean portrait = false;
    String path = "";
    float angleX = 90.0f;
    float angleY = -90.0f;
    float graphY = 0.0f;
    float graphX = 1.0f;
    float IncreaseReduceUnit = 1.0f;
    float speedX = 0.0f;
    float speedY = 0.0f;
    float FirstZ = -30.0f;
    float Max = -100.0f;
    float Min = 100.0f;
    ArrayList<SurfaceShape> finalData = new ArrayList<>();
    ArrayList<TexturedSurfaceShape> texturedSurfaceShapes = new ArrayList<>();
    ArrayList<TexturedSurfaceShape> texturedSurfaceShapes1 = new ArrayList<>();
    ArrayList<float[]> data = new ArrayList<>();
    ArrayList<float[]> favorableXYZs = new ArrayList<>();
    ArrayList<float[]> myXYZs = new ArrayList<>();
    ArrayList<Integer> realNumbers = new ArrayList<>();
    ArrayList<GroundType> groundType;
    Drawable groundDrawable;
    String groundText;

    float groundCoef = 25.0f;
    ArrayList<Float> depthValues = new ArrayList<>();
    ArrayList<float[]> colors = new ArrayList<>();
    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private Paint mPaint = new Paint(32);
    ArrayList<Bitmap> bitmaps1 = new ArrayList<>();

    private int calRealNumber(float f) {
        return (int) f;
    }

    SharedPref sp;


    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_view_scan_diagram);
        getWindow().addFlags(1024);
        this.increase_unit = (TextView) findViewById(R.id.increase_unit);
        this.reduce_unit = (TextView) findViewById(R.id.reduce_unit);
        this.main_unit_txt = (TextView) findViewById(R.id.mainUnit_txt);
        this.btn_Collaps_load = (Button) findViewById(R.id.btn_Collaps_main_load);
        this.btn_Collaps_moving_load = (Button) findViewById(R.id.btn_Collaps_moving_load);
        this.btn_move_right_load = (Button) findViewById(R.id.btn_move_right_load);
        this.btn_move_left_load = (Button) findViewById(R.id.btn_move_left_load);
        this.btn_move_up_load = (Button) findViewById(R.id.btn_move_up_load);
        this.btn_move_down_load = (Button) findViewById(R.id.btn_move_down_load);
        //this.btn_rotate = (ImageView) findViewById(R.id.btn_rotate);
        this.btn_return_view = (ImageView) findViewById(R.id.btn_return_view);
        this.linearLayout = (LinearLayout) findViewById(R.id.load_surfaceView);
        this.Controls_load = (ConstraintLayout) findViewById(R.id.Controls_load);
        this.moving_Controls_load = (ConstraintLayout) findViewById(R.id.moving_Controls_load);
        this.Edit_board = (LinearLayout) findViewById(R.id.Edit_board);
        this.zoom_in = (ImageView) findViewById(R.id.btn_zoom_in_load);
        this.zoom_out = (ImageView) findViewById(R.id.btn_zoom_out_load);
        this.btn_Edit_Save = (Button) findViewById(R.id.btn_Edit_Save);
        //this.Diagram_Loc = (ImageView) findViewById(R.id.Diagram_Loc);
        this.Edit = (ImageView) findViewById(R.id.Diagram_edit);
        this.Share = (ImageView) findViewById(R.id.Diagram_Share);
        this.Delete = (ImageView) findViewById(R.id.Diagram_delete);
        this.txt_edit_saving_data = (EditText) findViewById(R.id.txt_edit_saving_data);
        this.btn_ground_type_change = (Button) findViewById(R.id.btn_ground_type_change);
        this.btn_2d_depth_show_load = (Button) findViewById(R.id.btn_2d_depth_show_load);
        this.btn_2d_numbers_show_load = (Button) findViewById(R.id.btn_2d_numbers_show_load);
        this.btn_3d_favorable_show_load = (Button) findViewById(R.id.btn_3d_favorable_show_load);
        this.btn_2d_depth_show_load.setBackground(ActivityCompat.getDrawable(this,R.drawable.button_gradiant_1));
        this.btn_3d_favorable_show_load.setBackground(ActivityCompat.getDrawable(this,R.drawable.button_gradiant_1));
        this.btn_2d_numbers_show_load.setBackground(ActivityCompat.getDrawable(this,R.drawable.button_gradiant_1));

        this.mColorBackground = ResourcesCompat.getColor(getResources(), R.color.colorWhite, null);
        this.mColorRectangle = ResourcesCompat.getColor(getResources(), R.color.colorRectangle, null);
        this.mColorAccent = ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null);
        this.tinyDB = new TinyDB(this);
        String string = this.tinyDB.getString(this.diagramName);
        this.diagramName = ((Bundle) Objects.requireNonNull(getIntent().getExtras())).getString("data");
        Log.d("ON CREATE diagramName ", this.diagramName);
        String groundName = this.tinyDB.getString("*groundName_@" + this.diagramName);
        this.btn_ground_type_change.setText(groundName);
        this.renderer = new MyGLRenderer(this);
        MyGLSurfaceView myGLSurfaceView = new MyGLSurfaceView(this);
        this.glView = myGLSurfaceView;
        this.linearLayout.addView(myGLSurfaceView);
        load();
        load2();
        Listeners();
    }

    private void Listeners() {
        /*this.btn_rotate.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (ViewScanDiagram.this.portrait) {
                    ViewScanDiagram.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    ViewScanDiagram.this.portrait = false;
                    return;
                }
                ViewScanDiagram.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                ViewScanDiagram.this.portrait = true;
            }
        });*/
          this.btn_return_view.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                onBackPressed();
            }
        });
        this.increase_unit.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ViewScanDiagram.this.IncreaseReduceUnit += 1.0f;
                ViewScanDiagram.this.main_unit_txt.setText(String.valueOf(ViewScanDiagram.this.IncreaseReduceUnit));
            }
        });
        this.reduce_unit.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (ViewScanDiagram.this.IncreaseReduceUnit != 0.0f) {
                    ViewScanDiagram.this.IncreaseReduceUnit -= 1.0f;
                    ViewScanDiagram.this.main_unit_txt.setText(String.valueOf(ViewScanDiagram.this.IncreaseReduceUnit));
                }
            }
        });
        this.btn_Collaps_load.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (ViewScanDiagram.this.controls_collapsed_load) {
                    ViewScanDiagram.this.controls_collapsed_load = false;
                    ViewScanDiagram.this.btn_Collaps_load.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_arrow_drop_up_black_24dp, 0, 0);
                    ViewScanDiagram.this.Controls_load.setVisibility(View.VISIBLE);
                    return;
                }
                ViewScanDiagram.this.controls_collapsed_load = true;
                ViewScanDiagram.this.btn_Collaps_load.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_arrow_drop_down_black_24dp, 0, 0);
                ViewScanDiagram.this.Controls_load.setVisibility(View.INVISIBLE);
            }
        });
        this.btn_Collaps_moving_load.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (ViewScanDiagram.this.moving_controls_collapsed_load) {
                    ViewScanDiagram.this.moving_controls_collapsed_load = false;
                    ViewScanDiagram.this.btn_Collaps_moving_load.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_arrow_drop_up_black_24dp, 0, 0);
                    ViewScanDiagram.this.moving_Controls_load.setVisibility(View.VISIBLE);
                    return;
                }
                ViewScanDiagram.this.moving_controls_collapsed_load = true;
                ViewScanDiagram.this.btn_Collaps_moving_load.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_arrow_drop_down_black_24dp, 0, 0);
                ViewScanDiagram.this.moving_Controls_load.setVisibility(View.INVISIBLE);
            }
        });
        /*this.Diagram_Loc.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ViewScanDiagram.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://www.google.com/search?q=" + ViewScanDiagram.this.Loc)));
            }
        });*/
        this.Edit.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ViewScanDiagram.this.Edit_board.setVisibility(View.VISIBLE);
            }
        });
        this.btn_Edit_Save.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                String valueOf = String.valueOf(ViewScanDiagram.this.txt_edit_saving_data.getText());
                if (valueOf.contains("realNumbers") || valueOf.contains("numbersXYZ") || valueOf.contains("loc") || valueOf.contains("currentLoc")) {
                    Toast.makeText(ViewScanDiagram.this, "Name should not contain this word!", Toast.LENGTH_LONG).show();
                } else if (valueOf.length() <= 3) {
                    Toast.makeText(ViewScanDiagram.this, "Name length must be more than 3", Toast.LENGTH_LONG).show();
                } else {
                    String string = ViewScanDiagram.this.tinyDB.getString(ViewScanDiagram.this.diagramName);
                    TinyDB tinyDB = ViewScanDiagram.this.tinyDB;
                    String string2 = tinyDB.getString("*realNumbers_@" + ViewScanDiagram.this.diagramName);
                    TinyDB tinyDB2 = ViewScanDiagram.this.tinyDB;
                    String string3 = tinyDB2.getString("*numbersXYZ_@" + ViewScanDiagram.this.diagramName);
                    ViewScanDiagram.this.tinyDB.remove(ViewScanDiagram.this.diagramName);
                    TinyDB tinyDB6 = ViewScanDiagram.this.tinyDB;
                    String string6 = tinyDB6.getString("*colorsString_@" + ViewScanDiagram.this.diagramName);
                    TinyDB tinyDB7 = ViewScanDiagram.this.tinyDB;
                    String string7 = tinyDB7.getString("*datetime_@" + ViewScanDiagram.this.diagramName);
                    TinyDB tinyDB8 = ViewScanDiagram.this.tinyDB;
                    float  string8 = tinyDB8.getFloat("*groundCoef_@" + ViewScanDiagram.this.diagramName);
                    TinyDB tinyDB9 = ViewScanDiagram.this.tinyDB;
                    String string9 = tinyDB9.getString("*groundName_@" + ViewScanDiagram.this.diagramName);
                    TinyDB tinyDB10 = ViewScanDiagram.this.tinyDB;
                    String string10 = tinyDB10.getString("*loc_@" + ViewScanDiagram.this.diagramName);
                    TinyDB tinyDB3 = ViewScanDiagram.this.tinyDB;
                    tinyDB3.remove("*realNumbers_@" + ViewScanDiagram.this.diagramName);
                    TinyDB tinyDB4 = ViewScanDiagram.this.tinyDB;
                    tinyDB4.remove("*numbersXYZ_@" + ViewScanDiagram.this.diagramName);
                    TinyDB tinyDB11 = ViewScanDiagram.this.tinyDB;
                    tinyDB11.remove("*colorsString_@" + ViewScanDiagram.this.diagramName);
                    TinyDB tinyDB12 = ViewScanDiagram.this.tinyDB;
                    tinyDB12.remove("*datetime_@" + ViewScanDiagram.this.diagramName);
                    TinyDB tinyDB13 = ViewScanDiagram.this.tinyDB;
                    tinyDB13.remove("*groundCoef_@" + ViewScanDiagram.this.diagramName);
                    TinyDB tinyDB14 = ViewScanDiagram.this.tinyDB;
                    tinyDB14.remove("*groundName_@" + ViewScanDiagram.this.diagramName);
                    TinyDB tinyDB15 = ViewScanDiagram.this.tinyDB;
                    tinyDB15.remove("*loc_@" + ViewScanDiagram.this.diagramName);
                    TinyDB tinyDB5 = new TinyDB(ViewScanDiagram.this);
                    tinyDB5.putString(valueOf, string);
                    tinyDB5.putString("*realNumbers_@" + valueOf, string2);
                    tinyDB5.putString("*numbersXYZ_@" + valueOf, string3);
                    tinyDB5.putString("*colorsString_@" + valueOf, string6);
                    tinyDB5.putString("*datetime_@" + valueOf, string7);
                    tinyDB5.putFloat("*groundCoef_@" + valueOf, string8);
                    tinyDB5.putString("*groundName_@" + valueOf, string9);
                    tinyDB5.putString("*loc_@" + valueOf, string10);
                    ViewScanDiagram.this.Edit_board.setVisibility(View.GONE);
                    Toast.makeText(ViewScanDiagram.this, "DONE", Toast.LENGTH_SHORT).show();
                    ViewScanDiagram.this.finish();
                }
            }
        });
        this.Delete.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (ViewScanDiagram.this.sure_delete) {
                    TinyDB tinyDB = new TinyDB(ViewScanDiagram.this);
                    tinyDB.remove(ViewScanDiagram.this.diagramName);
                    tinyDB.remove("*realNumbers_@" + ViewScanDiagram.this.diagramName);
                    tinyDB.remove("*numbersXYZ_@" + ViewScanDiagram.this.diagramName);
                    Toast.makeText(ViewScanDiagram.this, "DELETED !!", Toast.LENGTH_SHORT).show();
                    ViewScanDiagram.this.finish();
                    return;
                }
                ViewScanDiagram.this.sure_delete = true;
                Toast.makeText(ViewScanDiagram.this, "click again to DELETE!!", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override // java.lang.Runnable
                    public void run() {
                        ViewScanDiagram.this.sure_delete = false;
                    }
                }, 2000L);
            }
        });
        this.Share.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if ( (ViewScanDiagram.this.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED)||(ViewScanDiagram.this.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) ) {
                    ViewScanDiagram.this.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"}, 2);
                }
                if ( ViewScanDiagram.this.path.length() == 0) {
                    Intent intent2 = new Intent("android.intent.action.OPEN_DOCUMENT_TREE");
                    Uri uriForDocument = DocumentsContract.buildDocumentUri(getApplicationContext().getPackageName() + ".documentsProvider",Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
                    intent2.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uriForDocument);
                    //startActivity(Intent.createChooser(intent2, "Save your graph !!"));

                    /*final Intent chooserIntent = new Intent(this, ViewScanDiagram.class);

                    final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                            .newDirectoryName("DirChooserSample")
                            .allowReadOnlyDirectory(true)
                            .allowNewDirectoryNameModification(true)
                            .build();

                    chooserIntent.putExtra(DirectoryChooserActivity.EXTRA_CONFIG, config);*/

                    ViewScanDiagram.this.startActivityForResult(Intent.createChooser(intent2, "Choose directory"), 9999);
                } else {
                    ViewScanDiagram.this.path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                    ViewScanDiagram.this.share();
                }
            }
        });
        this.btn_ground_type_change.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {



                groundType = new ArrayList<>();

                groundType.add(new GroundType("NEUTRAL", ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_neutral)));
                groundType.add(new GroundType("CONCRETE", ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_concrete)));
                groundType.add(new GroundType("SANDY", ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_sandy)));
                groundType.add(new GroundType("CLAY", ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_clay)));
                groundType.add(new GroundType("LIGHT MINERAL", ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_dense_mineral)));
                groundType.add(new GroundType("DENSE MINERAL", ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_dense_mineral)));
                groundType.add(new GroundType("ROCKY", ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_rocky)));
                groundType.add(new GroundType("FRESH WATER", ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_fresh_water)));
                groundType.add(new GroundType("SALT WATER", ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_salt_water)));
                groundType.add(new GroundType("SNOW", ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_snowy)));
                groundType.add(new GroundType("FROZEN SOIL", ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_frozen)));
                groundType.add(new GroundType("COAL", ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_coal)));
                groundType.add(new GroundType("GRANITE", ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_granite)));
                groundType.add(new GroundType("SALT", ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_salt)));
                GridView gridView = new GridView(ViewScanDiagram.this);
                GroundTypesAdapter groundListAdapter = new GroundTypesAdapter(groundType, getApplicationContext());
                gridView.setAdapter(groundListAdapter);
                gridView.setNumColumns(3);
                gridView.setGravity(Gravity.CENTER);

                gridView.setHorizontalSpacing(3);
                gridView.setVerticalSpacing(5);


                AlertDialog.Builder builder = new AlertDialog.Builder(ViewScanDiagram.this);
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
                                groundCoef = ViewScanDiagram.this.getCoefficient(GROUNDTYPE_NEUTRAL);
                                groundDrawable = ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_neutral);
                                groundText="NEUTRAL";
                                break;
                            case 1:
                                groundCoef = ViewScanDiagram.this.getCoefficient(GROUNDTYPE_CONCRETE);
                                groundText= "CONCRETE";
                                groundDrawable =   ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_concrete);
                                break;

                            case 2:
                                groundCoef = ViewScanDiagram.this.getCoefficient(GROUNDTYPE_SANDY);
                                groundDrawable = ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_sandy);
                                groundText="SANDY";
                                break;
                            case 3:
                                groundCoef = ViewScanDiagram.this.getCoefficient(GROUNDTYPE_CLAY);
                                groundDrawable = ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_clay);
                                groundText="CLAY";
                                break;
                            case 4:
                                groundCoef = ViewScanDiagram.this.getCoefficient(GROUNDTYPE_LIGHT_MINERAL);
                                groundDrawable = ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_dense_mineral);
                                groundText="Light Mineral";
                                break;
                            case 5:
                                groundCoef = ViewScanDiagram.this.getCoefficient(GROUNDTYPE_DENSE_MINERAL);
                                groundDrawable = ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_dense_mineral);
                                groundText="Dense Mineral";
                                break;
                            case 6:
                                groundCoef = ViewScanDiagram.this.getCoefficient(GROUNDTYPE_ROCKY);
                                groundDrawable = ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_rocky);
                                groundText="Rocky";
                                break;
                            case 7:
                                groundCoef = ViewScanDiagram.this.getCoefficient(GROUNDTYPE_FRESH_WATER);
                                groundDrawable = ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_fresh_water);
                                groundText="Fresh Water";
                                break;
                            case 8:
                                groundCoef = ViewScanDiagram.this.getCoefficient(GROUNDTYPE_SALT_WATER);
                                groundDrawable = ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_salt_water);
                                groundText="Salt Water";
                                break;
                            case 9:
                                groundCoef = ViewScanDiagram.this.getCoefficient(GROUNDTYPE_SNOW);
                                groundDrawable = ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_snowy);
                                groundText="Snowy";
                                break;

                            case 10:
                                groundCoef = ViewScanDiagram.this.getCoefficient(GROUNDTYPE_FROZEN_SOIL);
                                groundDrawable = ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_frozen);
                                groundText="Frozen Soil";
                                break;
                            case 11:
                                groundCoef = ViewScanDiagram.this.getCoefficient(GROUNDTYPE_COAL);
                                groundDrawable =ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_coal);
                                groundText="Coal";
                                break;
                            case 12:
                                groundCoef = ViewScanDiagram.this.getCoefficient(GROUNDTYPE_GRANITE);
                                groundDrawable = ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_granite);
                                groundText="Granite";
                                break;
                            case 13:
                                groundCoef = ViewScanDiagram.this.getCoefficient(GROUNDTYPE_SALT);
                                groundDrawable = ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.scale_salt);
                                groundText="Salt";
                                break;
                        }

                        Log.d("groundCoef", " " + groundCoef);
                        dialog.dismiss();
                        Button ground_type = (Button) findViewById(R.id.btn_ground_type_change);
                        ground_type.setText(groundText);

                        //String string = ViewScanDiagram.this.tinyDB.getString(ViewScanDiagram.this.diagramName);
                        //Log.d("diagramName", " " + string);
                        //TinyDB tinyDB = new TinyDB(ViewScanDiagram.this);
                        TinyDB tinyDB2 = ViewScanDiagram.this.tinyDB;
                        tinyDB2.putFloat("*groundCoef_@" + diagramName, groundCoef);
                        tinyDB2.putString("*groundName_@" + diagramName, groundText);
                        ViewScanDiagram.this.userWantsDepth = true;
                        ViewScanDiagram.this.userWantsFavorable = false;
                        ViewScanDiagram.this.userWantsNumbers = false;
                        ViewScanDiagram.this.bitmaps1.clear();
                        ViewScanDiagram.this.texturedSurfaceShapes1.clear();
                        ViewScanDiagram.this.loadDepth();
                        ViewScanDiagram.this.glView.requestRender();
                        finish();
                        startActivity(getIntent());
                    }
                });
                ViewScanDiagram.this.btn_2d_depth_show_load.setBackground(ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.button_gradiant_2));
                ViewScanDiagram.this.btn_2d_numbers_show_load.setBackground(ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.button_gradiant_1));
                ViewScanDiagram.this.btn_3d_favorable_show_load.setBackground(ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.button_gradiant_1));
            }
        });
        this.btn_2d_depth_show_load.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (ViewScanDiagram.this.userWantsDepth) {
                    ViewScanDiagram.this.userWantsDepth = false;
                    ViewScanDiagram.this.btn_2d_depth_show_load.setBackground(ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.button_gradiant_1));
                    ViewScanDiagram.this.btn_2d_numbers_show_load.setBackground(ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.button_gradiant_1));
                    ViewScanDiagram.this.btn_3d_favorable_show_load.setBackground(ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.button_gradiant_1));
                    ViewScanDiagram.this.angleX = 90.0f;
                    ViewScanDiagram.this.angleY = -90.0f;
                    return;
                }
                ViewScanDiagram.this.userWantsDepth = true;
                ViewScanDiagram.this.userWantsFavorable = false;
                ViewScanDiagram.this.userWantsNumbers = false;
                String string = ViewScanDiagram.this.tinyDB.getString(ViewScanDiagram.this.diagramName);
                Float groundCoef = ViewScanDiagram.this.tinyDB.getFloat("*groundCoef_@" + ViewScanDiagram.this.diagramName);
                depthValues.clear();
                ViewScanDiagram.this.depthValues = calculate_depth(realNumbers,groundCoef);
                int size1 = ViewScanDiagram.this.depthValues.size();
                Log.d("groundCoef","groundCoef from database =" +groundCoef);
                Log.d("depthValues.size","depthValues.size =" +size1);
                Log.d("depthValues","depthValue = " + depthValues);

                ViewScanDiagram.this.loadDepth();

                ViewScanDiagram.this.glView.requestRender();
                ViewScanDiagram.this.btn_2d_depth_show_load.setBackground(ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.button_gradiant_2));
                ViewScanDiagram.this.btn_2d_numbers_show_load.setBackground(ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.button_gradiant_1));
                ViewScanDiagram.this.btn_3d_favorable_show_load.setBackground(ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.button_gradiant_1));
            }
        });
        this.btn_2d_numbers_show_load.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (ViewScanDiagram.this.userWantsNumbers) {
                    ViewScanDiagram.this.userWantsNumbers = false;
                    ViewScanDiagram.this.btn_2d_depth_show_load.setBackground(ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.button_gradiant_1));
                    ViewScanDiagram.this.btn_2d_numbers_show_load.setBackground(ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.button_gradiant_1));
                    ViewScanDiagram.this.btn_3d_favorable_show_load.setBackground(ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.button_gradiant_1));
                    ViewScanDiagram.this.angleX = 90.0f;
                    ViewScanDiagram.this.angleY = -90.0f;
                    return;
                }
                ViewScanDiagram.this.userWantsNumbers = true;
                ViewScanDiagram.this.userWantsFavorable = false;
                ViewScanDiagram.this.userWantsDepth = false;
                if (ViewScanDiagram.this.texturedSurfaceShapes.isEmpty()) {
                    ViewScanDiagram.this.loadNumbers();
                }
                ViewScanDiagram.this.glView.requestRender();
                ViewScanDiagram.this.btn_2d_depth_show_load.setBackground(ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.button_gradiant_1));
                ViewScanDiagram.this.btn_2d_numbers_show_load.setBackground(ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.button_gradiant_2));
                ViewScanDiagram.this.btn_3d_favorable_show_load.setBackground(ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.button_gradiant_1));
            }
        });
        this.btn_3d_favorable_show_load.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (ViewScanDiagram.this.userWantsFavorable) {
                    ViewScanDiagram.this.userWantsNumbers = false;
                    ViewScanDiagram.this.userWantsFavorable = false;
                    ViewScanDiagram.this.btn_3d_favorable_show_load.setBackground(ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.button_gradiant_1));
                    ViewScanDiagram.this.btn_2d_numbers_show_load.setBackground(ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.button_gradiant_1));
                    ViewScanDiagram.this.btn_2d_depth_show_load.setBackground(ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.button_gradiant_1));
                    ViewScanDiagram.this.load();
                    return;
                }
                ViewScanDiagram.this.userWantsNumbers = false;
                ViewScanDiagram.this.userWantsDepth = false;
                ViewScanDiagram.this.userWantsFavorable = true;
                ViewScanDiagram.this.load2();
                ViewScanDiagram.this.btn_3d_favorable_show_load.setBackground(ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.button_gradiant_2));
                ViewScanDiagram.this.btn_2d_numbers_show_load.setBackground(ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.button_gradiant_1));
                ViewScanDiagram.this.btn_2d_depth_show_load.setBackground(ActivityCompat.getDrawable(ViewScanDiagram.this,R.drawable.button_gradiant_1));
            }
        });
    }


    public void share()  {
        //sp = getSharedPreferences("userSettings", Context.MODE_PRIVATE);
        String string = this.tinyDB.getString(this.diagramName);
        TinyDB tinyDB = this.tinyDB;
        String string2 = tinyDB.getString("*realNumbers_@" + this.diagramName);
        TinyDB tinyDB2 = this.tinyDB;
        String string3 = tinyDB2.getString("*numbersXYZ_@" + this.diagramName);
        TinyDB tinyDB3 = this.tinyDB;
        String string4 = tinyDB3.getString("*colorsString_@" + this.diagramName);
        this.diagramName = this.diagramName.replace(' ', '_');
        //File externalFilesDir = new ContextWrapper(getApplicationContext()).getExternalFilesDir(null);
        //File internalFilesDir = new ContextWrapper(getApplicationContext()).getFilesDir();
        // File file = new File(Environment.getExternalStorageDirectory(), sp.getPreferences(this).getStringData("Devicename", "None").replace('-', '_').replace(' ', '_'));
        //File file = new File(Environment.getExternalStorageDirectory(), "Scans2");
        //final File file = new File(externalFilesDir.getAbsolutePath() + "/Scans/");
        final File file = new File(this.path +"/Scans/");
        Log.d("File", "File = "+file);
        if (!file.exists()) {
            file.mkdir();
        }
        //File file2 = new File(Environment.getExternalStorageDirectory() + "/" + File.separator + this.diagramName + ".txt");
        File file2 = new File(file, this.diagramName + ".txt");
        Log.d("File2", "File2 = "+file2);
        try {
            file2.createNewFile();
            System.out.println(file2.getPath() + " created successfully...");
        } catch (IOException e){
            e.printStackTrace();
            return;
        }

        try {

            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            fileOutputStream.write(string.getBytes());
            fileOutputStream.write("~".getBytes());
            fileOutputStream.write(string2.getBytes());
            fileOutputStream.write("~".getBytes());
            fileOutputStream.write(string3.getBytes());
            fileOutputStream.write("~".getBytes());
            fileOutputStream.write(string4.getBytes());
            fileOutputStream.close();
            try {
                Intent intent = new Intent("android.intent.action.SEND");
                Uri uriForFile = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file2);
                intent.setType("*/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra("android.intent.extra.STREAM", uriForFile);
                startActivity(Intent.createChooser(intent, "Share your graph !!"));
            } catch (Exception e) {
                Toast.makeText(this, "There is a problem with your storage!\nI can't find the file.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } catch (IOException e2) {
            Toast.makeText(this, "There is a problem with your storage!\nI can't find the Directory.", Toast.LENGTH_SHORT).show();
            e2.printStackTrace();
        }
    }


    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 9999) {
            ViewScanDiagram.this.path = intent.getData().getPath();
            share();
            Log.i("Test", "Result URI " + intent.getData());

        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity, androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i != 2) {

            Toast.makeText(this, "Permission denied to access your Storage.", Toast.LENGTH_LONG).show();
            return;
        }else {

            Toast.makeText(this, "Permission accepted to access your Storage.", Toast.LENGTH_LONG).show();}
        if (iArr.length > 0 && iArr[0] == 0 && iArr[1] == 0) {
            return;
        }

        //Toast.makeText(this, "Permission denied to access your Storage.", Toast.LENGTH_LONG).show();
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
    public void loadNumbers() {
        int size = this.realNumbers.size();
        for (int i = 0; i < size; i++) {
            drawSomething(String.valueOf(this.realNumbers.get(i)));
            try {
                this.texturedSurfaceShapes.add(new TexturedSurfaceShape(this.myXYZs.get(i), this.bitmaps.get(i)));
            } catch (IndexOutOfBoundsException e) {
                Log.d("IndexTexture", e.toString());
            }
        }
    }
    public void loadDepth() {
        int size = this.depthValues.size();
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        for (int i = 0; i < size; i++) {
            drawSomething1(String.valueOf(df.format(this.depthValues.get(i))));
            try {
                this.texturedSurfaceShapes1.add(new TexturedSurfaceShape(this.myXYZs.get(i), this.bitmaps1.get(i)));
            } catch (IndexOutOfBoundsException e) {
                Log.d("IndexTexture", e.toString());
            }
        }
    }
    public float getRealMaxValue(ArrayList<Integer> array) {
        float maxValue = 0.0f;
        for (int i = 0; i < array.size(); i++) {

            if (array.get(i) != Float.MIN_VALUE && array.get(i) > maxValue) {
                maxValue = array.get(i);
            }

        }
        return maxValue;
    }

    public float getRealTotalValue(ArrayList<Integer> array) {
        float totalValue = 0.0f;
        for (int i = 0; i < array.size(); i++) {

            if (array.get(i) != Float.MIN_VALUE) {
                totalValue += array.get(i);
            }

        }
        return totalValue;
    }

    public float getRealMinValue(ArrayList<Integer> array) {
        float minValue = Float.MAX_VALUE;
        for (int i = 0; i < array.size(); i++) {

            if (array.get(i) != Float.MIN_VALUE && array.get(i) < minValue) {
                minValue = array.get(i);
            }

        }
        if (minValue == Float.MAX_VALUE) {
            return 0.0f;
        }
        return minValue;
    }
    public ArrayList<Float> calculate_depth(ArrayList<Integer> array,Float coef){
        int i;
        int n = array.size();
        //depthValues = (float[]) ArrayList.(n);
        ArrayList<Float> depthArray;
        float diff = getRealMaxValue(array)-getRealMinValue(array);
        float total = getRealTotalValue(array);
        float avg = (float) Math.floor(total / array.size());
        for( i = 0;i<array.size(); i++){
            depthValues.add(((Math.abs(array.get(i) - avg) * coef) / diff));
        }

        return depthValues ;

    }
    public void drawSomething1(String str) {
        this.mBitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        final int green = ContextCompat.getColor(getApplicationContext(), R.color.vivid_green);
        this.mCanvas = new Canvas(this.mBitmap);
        if (this.texturedSurfaceShapes1.size() % 2 == 0) {
            this.mCanvas.drawColor(green);
        } else {
            this.mCanvas.drawColor(this.mColorRectangle);
        }
        if (this.texturedSurfaceShapes1.size() % 2 == 0) {
            this.mPaint.setColor(this.mColorRectangle);
        } else {
            this.mPaint.setColor(this.mColorBackground);
        }
       /* if (str.length() == 4) {
            this.mPaint.setTextSize(93.0f);
        } else if (str.length() == 3) {
            this.mPaint.setTextSize(140.0f);
        } else if (str.length() == 2) {
            this.mPaint.setTextSize(185.0f);
        } else {
            this.mPaint.setTextSize(220.0f);
        }*/
        /*if (str.length() == 8) {
            this.mPaint.setTextSize(53.0f);
        } else if (str.length() == 7) {
            this.mPaint.setTextSize(63.0f);
        }
        else if (str.length() == 6) {
            this.mPaint.setTextSize(73.0f);
        }
        else if (str.length() == 5) {
            this.mPaint.setTextSize(83.0f);
        }
        else if (str.length() == 4) {
            this.mPaint.setTextSize(93.0f);
        } else if (str.length() == 3) {
            this.mPaint.setTextSize(140.0f);
        } else if (str.length() == 2) {
            this.mPaint.setTextSize(185.0f);
        } else {
            this.mPaint.setTextSize(220.0f);
        }*/
        this.mPaint.setTextSize(100.0f);
        //this.mCanvas.drawText(str, 100.0f, 230.0f, this.mPaint);
        this.mCanvas.rotate(-90.0f,100.0f, 230.0f);
        this.mCanvas.drawText(str, 50.0f, 400.0f, this.mPaint);
        this.bitmaps1.add(this.mBitmap);
    }

    public void load() {
        String string = this.tinyDB.getString(this.diagramName);
        String string2 = this.tinyDB.getString("*realNumbers_@" + this.diagramName);
        String string3 = this.tinyDB.getString("*colorsString_@" + this.diagramName);
        String string4 = this.tinyDB.getString("*numbersXYZ_@" + this.diagramName);
        this.Loc = this.tinyDB.getString("*loc_@" + this.diagramName);
        String[] split = string.split("/");
        this.data.clear();
        for (String str : split) {
            float[] fArr = new float[72];
            int i = 0;
            for (String str2 : str.split(",")) {
                try {
                    fArr[i] = Float.parseFloat(str2);
                    i++;
                } catch (NumberFormatException e) {
                    Log.d("myTag", e.toString());
                }
            }
            this.data.add(fArr);
        }
        this.myXYZs.clear();
        for (String str3 : string4.split("/")) {
            float[] fArr2 = new float[72];
            int i2 = 0;
            for (String str4 : str3.split(",")) {
                try {
                    fArr2[i2] = Float.parseFloat(str4);
                    i2++;
                } catch (NumberFormatException unused) {
                }
            }
            this.myXYZs.add(fArr2);
        }
        for (String str5 : string3.split("/")) {
            float[] fArr3 = new float[96];
            int i3 = 0;
            for (String str6 : str5.split(",")) {
                try {
                    fArr3[i3] = Float.parseFloat(str6);
                    i3++;
                } catch (ArrayIndexOutOfBoundsException | NumberFormatException unused2) {
                }
            }
            this.colors.add(fArr3);
        }
        if (this.realNumbers.isEmpty()) {
            int i4 = 0;
            for (String str7 : string2.split(",")) {
                try {
                    i4 = Integer.valueOf(Integer.parseInt(str7));
                } catch (NumberFormatException unused3) {
                }
                this.realNumbers.add(i4);
            }
        }
        this.finalData.clear();
        for (int i5 = 0; i5 < this.data.size(); i5++) {
            try {
                this.finalData.add(new SurfaceShape(this.data.get(i5), this.colors.get(i5)));
            } catch (IndexOutOfBoundsException unused4) {
            }
        }
    }

    public void load2() {
        String string = this.tinyDB.getString(this.diagramName);
        String string2 = this.tinyDB.getString("*realNumbers_@" + this.diagramName);
        String string3 = this.tinyDB.getString("*numbersXYZ_@" + this.diagramName);
        String[] split = string.split("/");
        this.data.clear();
        for (String str : split) {
            float[] fArr = new float[72];
            int i = 0;
            for (String str2 : str.split(",")) {
                try {
                    fArr[i] = Float.parseFloat(str2);
                    i++;
                } catch (NumberFormatException unused) {
                }
            }
            this.data.add(fArr);
        }
        this.myXYZs.clear();
        for (String str3 : string3.split("/")) {
            float[] fArr2 = new float[72];
            int i2 = 0;
            for (String str4 : str3.split(",")) {
                try {
                    fArr2[i2] = Float.parseFloat(str4);
                    i2++;
                } catch (NumberFormatException unused2) {
                }
            }
            this.myXYZs.add(fArr2);
        }
        if (this.realNumbers.isEmpty()) {
            int i3 = 0;
            for (String str5 : string2.split(",")) {
                try {
                    i3 = Integer.valueOf(Integer.parseInt(str5));
                } catch (NumberFormatException unused3) {
                }
                this.realNumbers.add(i3);
            }
        }
        Iterator<float[]> it = this.data.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            float[] next = it.next();
            for (int i4 = 1; i4 < next.length; i4 += 3) {
                if (this.Max < next[i4]) {
                    this.Max = next[i4];
                }
                if (this.Min > next[i4]) {
                    this.Min = next[i4];
                }
            }
        }
        this.Mid = (Math.abs(this.Max) - Math.abs(this.Min)) / 2.0f;
        this.favorableXYZs = this.data;
        for (int i5 = 0; i5 < this.favorableXYZs.size(); i5++) {
            for (int i6 = 1; i6 < this.favorableXYZs.get(i5).length; i6 += 3) {
                if (this.favorableXYZs.get(i5)[i6] > this.Mid) {
                    this.favorableXYZs.get(i5)[i6] = this.Mid - (this.favorableXYZs.get(i5)[i6] - this.Mid);
                }
            }
        }
        try {
            this.finalData.clear();
            for (int i7 = 0; i7 < this.data.size(); i7++) {
                this.finalData.add(new SurfaceShape(this.data.get(i7), this.colors.get(i7)));
            }
        } catch (Exception e) {
            Log.d("ignored", e.toString());
        }
    }

    public void drawSomething(String str) {
        this.mBitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        final int green = ContextCompat.getColor(getApplicationContext(), R.color.vivid_green);
        this.mCanvas = new Canvas(this.mBitmap);
        if (this.texturedSurfaceShapes.size() % 2 == 0) {
            this.mCanvas.drawColor(green);
        } else {
            this.mCanvas.drawColor(this.mColorRectangle);
        }
        if (this.texturedSurfaceShapes.size() % 2 == 0) {
            this.mPaint.setColor(this.mColorRectangle);
        } else {
            this.mPaint.setColor(this.mColorBackground);
        }
        if (str.length() == 4) {
            this.mPaint.setTextSize(93.0f);
        } else if (str.length() == 3) {
            this.mPaint.setTextSize(140.0f);
        } else if (str.length() == 2) {
            this.mPaint.setTextSize(185.0f);
        } else {
            this.mPaint.setTextSize(220.0f);
        }
        //this.mCanvas.drawText(str, 100.0f, 230.0f, this.mPaint);
        this.mCanvas.rotate(-90.0f,100.0f, 230.0f);
        this.mCanvas.drawText(str, 50.0f, 400.0f, this.mPaint);
        this.bitmaps.add(this.mBitmap);
    }


    class MyGLSurfaceView extends GLSurfaceView {
        private final float TOUCH_SCALE_FACTOR;
        private float previousX;
        private float previousY;

        public MyGLSurfaceView(Context context) {
            super(context);
            this.TOUCH_SCALE_FACTOR = 0.5625f;
            ViewScanDiagram.this.renderer = new MyGLRenderer(context);
            setRenderer(ViewScanDiagram.this.renderer);
            requestFocus();
            setFocusableInTouchMode(true);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            if (motionEvent.getAction() == 2) {
                ViewScanDiagram.this.angleX += (y - this.previousY) * 0.5625f;
                ViewScanDiagram.this.angleY += (x - this.previousX) * 0.5625f;
            }
            ViewScanDiagram.this.zoom_in.setOnClickListener(new View.OnClickListener() {
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    ViewScanDiagram.this.FirstZ += ViewScanDiagram.this.IncreaseReduceUnit;
                    ViewScanDiagram.this.glView.requestRender();
                }
            });
            ViewScanDiagram.this.zoom_out.setOnClickListener(new View.OnClickListener() {
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    ViewScanDiagram.this.FirstZ -= ViewScanDiagram.this.IncreaseReduceUnit;
                    ViewScanDiagram.this.glView.requestRender();
                }
            });
            ViewScanDiagram.this.btn_move_right_load.setOnClickListener(new View.OnClickListener() {
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    ViewScanDiagram.this.graphX += 1.0f;
                    ViewScanDiagram.this.glView.requestRender();
                }
            });
            ViewScanDiagram.this.btn_move_left_load.setOnClickListener(new View.OnClickListener() {
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    ViewScanDiagram.this.graphX -= 1.0f;
                    ViewScanDiagram.this.glView.requestRender();
                }
            });
            ViewScanDiagram.this.btn_move_up_load.setOnClickListener(new View.OnClickListener() {
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    ViewScanDiagram.this.graphY += 1.0f;
                    ViewScanDiagram.this.glView.requestRender();
                }
            });
            ViewScanDiagram.this.btn_move_down_load.setOnClickListener(new View.OnClickListener() {
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    ViewScanDiagram.this.graphY -= 1.0f;
                    ViewScanDiagram.this.glView.requestRender();
                }
            });
            this.previousX = x;
            this.previousY = y;
            return true;
        }
    }


    class MyGLRenderer implements GLSurfaceView.Renderer {
        private Context context;

        public MyGLRenderer(Context context) {
            this.context = context;
        }

        @Override // android.opengl.GLSurfaceView.Renderer
        public void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig) {
            gl10.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            gl10.glClearDepthf(1.0f);
            gl10.glEnable(2929);
            gl10.glDepthFunc(515);
            gl10.glHint(3152, 4354);
            gl10.glShadeModel(7425);
            gl10.glDisable(3024);
            gl10.glEnable(3553);
        }

        @Override // android.opengl.GLSurfaceView.Renderer
        public void onSurfaceChanged(GL10 gl10, int i, int i2) {
            if (i2 == 0) {
                i2 = 1;
            }
            gl10.glViewport(0, 0, i, i2);
            gl10.glMatrixMode(5889);
            gl10.glLoadIdentity();
            GLU.gluPerspective(gl10, 45.0f, i / i2, 0.1f, 100.0f);
            gl10.glMatrixMode(5888);
            gl10.glLoadIdentity();
        }

        @Override // android.opengl.GLSurfaceView.Renderer
        public void onDrawFrame(GL10 gl10) {
            gl10.glClear(16640);
            if (!ViewScanDiagram.this.finalData.isEmpty() || !ViewScanDiagram.this.texturedSurfaceShapes.isEmpty()) {
                try {
                    if (ViewScanDiagram.this.userWantsNumbers) {
                        Iterator<TexturedSurfaceShape> it = ViewScanDiagram.this.texturedSurfaceShapes.iterator();
                        while (it.hasNext()) {
                            TexturedSurfaceShape next = it.next();
                            gl10.glLoadIdentity();
                            gl10.glTranslatef(ViewScanDiagram.this.graphX, ViewScanDiagram.this.graphY, ViewScanDiagram.this.FirstZ);
                            gl10.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
                            gl10.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
                            try {
                                next.loadTexture(gl10, ViewScanDiagram.this);
                                next.draw(gl10);
                            } catch (NullPointerException e) {
                                Log.e("LoadDiagram", e.toString());
                            }
                        }
                    }
                    else if (ViewScanDiagram.this.userWantsDepth) {
                        Iterator<TexturedSurfaceShape> it3 = ViewScanDiagram.this.texturedSurfaceShapes1.iterator();
                        while (it3.hasNext()) {
                            TexturedSurfaceShape next3 = it3.next();
                            gl10.glLoadIdentity();
                            gl10.glTranslatef(ViewScanDiagram.this.graphX, ViewScanDiagram.this.graphY, ViewScanDiagram.this.FirstZ);
                            gl10.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
                            gl10.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
                            try {
                                next3.loadTexture(gl10, ViewScanDiagram.this);
                                next3.draw(gl10);
                            } catch (NullPointerException e) {
                                Log.e("LoadDiagram", e.toString());
                            }
                        }
                    }
                    else {
                        Iterator<SurfaceShape> it2 = ViewScanDiagram.this.finalData.iterator();
                        while (it2.hasNext()) {
                            SurfaceShape next2 = it2.next();
                            gl10.glLoadIdentity();
                            gl10.glTranslatef(ViewScanDiagram.this.graphX, ViewScanDiagram.this.graphY, ViewScanDiagram.this.FirstZ);
                            gl10.glRotatef(ViewScanDiagram.this.angleX, 1.0f, 0.0f, 0.0f);
                            gl10.glRotatef(ViewScanDiagram.this.angleY, 0.0f, 1.0f, 0.0f);
                            try {
                                next2.draw(gl10);
                            } catch (NullPointerException e2) {
                                Log.e("LoadDiagram", e2.toString());
                            }
                        }
                    }
                } catch (ConcurrentModificationException unused) {
                }
            }
            ViewScanDiagram.this.angleX += ViewScanDiagram.this.speedX;
            ViewScanDiagram.this.angleY += ViewScanDiagram.this.speedY;
        }
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        if (this.doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override // java.lang.Runnable
            public void run() {
                ViewScanDiagram.this.doubleBackToExitPressedOnce = false;
            }
        }, 2000L);
    }
}
