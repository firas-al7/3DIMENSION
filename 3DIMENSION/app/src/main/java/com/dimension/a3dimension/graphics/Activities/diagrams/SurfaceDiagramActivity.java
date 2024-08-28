package com.dimension.a3dimension.graphics.Activities.diagrams;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.media.ToneGenerator;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.dimension.a3dimension.MyProgressDialog;
import com.dimension.a3dimension.graphics.SharedPref;
import com.dimension.a3dimension.graphics.TinyDB;
import com.dimension.a3dimension.graphics.consts;
import com.dimension.a3dimension.R;
import com.dimension.a3dimension.graphics.Activities.TexturedSurfaceShape;
import com.dimension.a3dimension.graphics.SurfaceShape;
import com.dimension.a3dimension.models.Alerts;
import com.dimension.a3dimension.models.GroundType;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
//import com.hoho.android.usbserial.driver.FtdiSerialDriver;
//import com.hoho.android.usbserial.driver.ProbeTable;
//import com.hoho.android.usbserial.driver.UsbSerialDriver;
//import com.hoho.android.usbserial.driver.UsbSerialPort;
//import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import kotlin.jvm.internal.Intrinsics;


public class SurfaceDiagramActivity extends AppCompatActivity {
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int OFFSET = 120;
    private final String BLUETOOTH_DEVICE_NAME = new consts().getBLUETOOTH_DEVICE_NAME();
    private static final String TAG = "BluetoothConnectionServ";
    private static final String appName = "MYAPP";

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
    int[] CoefficientCounterLTR;
    int[] CoefficientCounterRTL;
    ConstraintLayout Controls;
    float MaxX;
    float Mid;
    float MinX;
    TextView OutPut;
    int PalsPerColumn;
    int PalsPerRow;
    int SleepTime;
    long StartTime;

    Button btn_2d_depth_show;
    Button btn_2d_numbers_show;
    Button btn_3d_favorable_show;
    Button btn_Collaps;
    Button btn_Collaps_moving;
    Button btn_StartAndStop;
    Button btn_finish;
    Button btn_move_down;
    Button btn_move_left;
    Button btn_move_right;
    Button btn_move_up;
    ImageView btn_rotate;
    ImageView btn_return;
    ImageView ground_type_image;
    Button btn_save;
    private UUID deviceUUID;
    ArrayList<float[]> favorableXYZs;
    FusedLocationProviderClient fusedLocationClient;
    private GLSurfaceView glView;

    TextView ground_type;
    TextView increase_unit;
    LinearLayout linearLayout;
    BluetoothDevice mBTDevice;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private int mColorAccent;
    private int mColorBackground;
    private int mColorRectangle;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;


    Context mContext;
    private AcceptThread mInsecureAcceptThread;
    MyProgressDialog mProgressDialog;
    TextView main_unit_txt;
    private BluetoothDevice mmDevice;
    private final float sensor_factor = 0.244f;
    ConstraintLayout moving_Controls;
    int palsOrientation;
    TextView reduce_unit;
    MyGLRenderer renderer;
    ToneGenerator toneGen1;
    EditText txt_saving_name;
    float x;
    float z;
    ImageView zoom_in;
    ImageView zoom_out;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    int zigzagRound = 0;
    boolean is_running = false;
    boolean ThreadRunning = false;
    boolean controls_collapsed = false;
    boolean moving_controls_collapsed = false;
    boolean is_Finished = false;
    boolean is_save_pressed = false;

    boolean userWantsDepth = false;
    boolean userWantsNumbers = false;
    boolean userWantsFavorable = true;
    boolean doubleBackToExitPressedOnce = false;
    boolean is_strings_maked = false;
    boolean portrait = false;
    float y = -1.0f;
    float zyIncrease = 1.0f;
    ArrayList<Integer> BottomBalance = new ArrayList<>();
    ArrayList<Integer> TopBalance = new ArrayList<>();
    ArrayList<Integer> RightBalance = new ArrayList<>();
    ArrayList<Integer> LeftBalance = new ArrayList<>();
    ArrayList<Integer> realNumbers = new ArrayList<>();
    ArrayList<Float> depthValues = new ArrayList<>();
    ArrayList<SurfaceShape> finalData = new ArrayList<>();
    ArrayList<float[]> myXYZs = new ArrayList<>();
    ArrayList<TexturedSurfaceShape> texturedSurfaceShapes = new ArrayList<>();
    ArrayList<TexturedSurfaceShape> texturedSurfaceShapes1 = new ArrayList<>();
    ArrayList<SurfaceShape> finaltwoDdata = new ArrayList<>();
    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    ArrayList<Bitmap> bitmaps1 = new ArrayList<>();
    ArrayList<float[]> data = new ArrayList<>();
    ArrayList<float[]> twoDdata = new ArrayList<>();
    ArrayList<float[]> colors = new ArrayList<>();
    StringBuilder allText = new StringBuilder();
    StringBuilder numbersXYZ = new StringBuilder();
    StringBuilder colorsString = new StringBuilder();
    StringBuilder numbers_to_Save = new StringBuilder();

    StringBuilder favorableXYZsString = new StringBuilder();
    ArrayList<GroundType> groundType;
    String groundName ="Neutral";
    private Paint mPaint = new Paint(32);
    float angleX = 90.0f;
    float angleY = -90.0f;
    float graphY = 0.0f;
    float graphX = 1.0f;
    float IncreaseReduceUnit = 1.0f;
    float speedX = 0.0f;
    float speedY = 0.0f;
    float FirstZ = -20.0f;
    float FirstZfar = -20.0f;
    int minColor = -1;
    int maxColor = -1;
    float Max = -100.0f;
    float Min = 100.0f;

    float groundCoef = 25.0f;
    SharedPref sp;
    //SharedPreferences sp;

    //SharedPreferences.Editor editor;

    Alerts alert;
    int usbProductId;
    int usbVendorId;
    //private UsbCommunicationThread mUsbCommunicationThread;

    private ReadUsbThread mUsbReadThread;
    UsbDeviceConnection connection;
    //UsbSerialDriver driver;
    //UsbSerialPort port;
    UsbManager manager;
    UsbDevice device;
    boolean hasUsbPermission;

    //UsbInterface usbInterface;
    //UsbEndpoint usbEndpointIN, usbEndpointOUT;
    //UsbDeviceConnection usbDeviceConnection;
    //UsbDevice deviceFound = null;
    //USB USB;

    //ArrayList<String> listInterface;
    //ArrayList<UsbInterface> listUsbInterface;
    //ArrayList<String> listEndPoint;
    //ArrayList<UsbEndpoint> listUsbEndpoint;

    UsbSerialDevice serialPort;

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            Integer val_uT;
            String incomingMessage;
            byte[] bArr = new byte[1024];
            double currentTimeMillis = System.currentTimeMillis();
                Log.d("UsbCommunication"," UsbThread = Started");
                String str = new String(arg0, 0, arg0.length);
                Log.d("UsbCommunication","message recieved : str = " +str);
                incomingMessage = str;
                if(!str.equals("")) {
                    for (char c : incomingMessage.toCharArray()) {
                        arrayList2.add(Character.valueOf(c));
                        if (c == '\r') {
                            StringBuilder sb = new StringBuilder();
                            Iterator it = arrayList2.iterator();
                            while (it.hasNext()) {
                                sb.append(((Character) it.next()).charValue());
                            }
                            try {
                                val_uT = (int) ((Integer.valueOf(Integer.parseInt(String.valueOf(sb).trim()))) * 0.244f);
                                arrayList.add(val_uT);
                            } catch (NumberFormatException unused) {
                            }
                            Log.d("result", sb.toString());
                            arrayList2.clear();
                        }
                    }
                    double currentTimeMillis2 = System.currentTimeMillis();
                    Double.isNaN(currentTimeMillis2);
                    if (currentTimeMillis2 - currentTimeMillis >= SurfaceDiagramActivity.this.SleepTime) {
                        currentTimeMillis = System.currentTimeMillis();
                        SurfaceDiagramActivity.this.getAverage(arrayList);
                        arrayList.clear();
                    }
                }else{

                    Toast.makeText(SurfaceDiagramActivity.this,"DATA RECIEVED IS NULL",Toast.LENGTH_SHORT).show();
                }

        }
    };
    private static final String ACTION_USB_PERMISSION = "com.example.usbserial.USB_PERMISSION";
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    //UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(device != null){
                            startUsbConnection(device);

                        }
                    }
                    else {
                        Log.d("UsbDevice", "permission denied for device " + device);
                        runOnUiThread(new Runnable() {
                            @Override // java.lang.Runnable
                            public void run() {

                                View view = findViewById(R.id.Controls);
                                Snackbar snackbar = Snackbar.make(view, "Permission Denied For Device : "+ device, Snackbar.LENGTH_INDEFINITE);
                                snackbar.setAction("Refresh", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        finish();
                                        startActivity(getIntent());
                                    }
                                });
                                snackbar.setBackgroundTint(R.drawable.startup_screen_card_background);
                                snackbar.show();
                            }
                        });
                    }
                }
            }
        }
    };

    private float calDividedNumber(float f) {
        return f / 10.0f;
    }

    private int calRealNumber(float f) {
        return (int) f;
    }


    @Override
    // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_surface_diagram);
        String bluetooth = SharedPref.getPreferences(SurfaceDiagramActivity.this).getStringData("Bluetooth","none");
        String otg = SharedPref.getPreferences(SurfaceDiagramActivity.this).getStringData("OTG","none");
        if (Intrinsics.areEqual(bluetooth, "on")||Intrinsics.areEqual(otg, "on")) {
            bindAndPrepare();
            listeners();
        } else {
            Alerts.show_alert(SurfaceDiagramActivity.this,"Connection Error!","Please choose a connection Method");
        }
    }

    private void listeners() {

        this.btn_return.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                onBackPressed();
            }
        });
        this.increase_unit.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                SurfaceDiagramActivity.this.IncreaseReduceUnit += 1.0f;
                SurfaceDiagramActivity.this.main_unit_txt.setText(String.valueOf(SurfaceDiagramActivity.this.IncreaseReduceUnit));
            }
        });
        this.reduce_unit.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (SurfaceDiagramActivity.this.IncreaseReduceUnit != 0.0f) {
                    SurfaceDiagramActivity.this.IncreaseReduceUnit -= 1.0f;
                    SurfaceDiagramActivity.this.main_unit_txt.setText(String.valueOf(SurfaceDiagramActivity.this.IncreaseReduceUnit));
                }
            }
        });
        this.btn_Collaps.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (SurfaceDiagramActivity.this.controls_collapsed) {
                    SurfaceDiagramActivity.this.controls_collapsed = false;
                    SurfaceDiagramActivity.this.btn_Collaps.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_arrow_drop_up_black_24dp, 0, 0);
                    SurfaceDiagramActivity.this.Controls.setVisibility(View.VISIBLE);
                    return;
                }
                SurfaceDiagramActivity.this.controls_collapsed = true;
                SurfaceDiagramActivity.this.btn_Collaps.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_arrow_drop_down_black_24dp, 0, 0);
                SurfaceDiagramActivity.this.Controls.setVisibility(View.INVISIBLE);
            }
        });
        this.btn_Collaps_moving.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (SurfaceDiagramActivity.this.moving_controls_collapsed) {
                    SurfaceDiagramActivity.this.moving_controls_collapsed = false;
                    SurfaceDiagramActivity.this.btn_Collaps_moving.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_arrow_drop_up_black_24dp, 0, 0);
                    SurfaceDiagramActivity.this.moving_Controls.setVisibility(View.VISIBLE);
                    return;
                }
                SurfaceDiagramActivity.this.moving_controls_collapsed = true;
                SurfaceDiagramActivity.this.btn_Collaps_moving.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_arrow_drop_down_black_24dp, 0, 0);
                SurfaceDiagramActivity.this.moving_Controls.setVisibility(View.INVISIBLE);
            }
        });
        this.btn_StartAndStop.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                String bluetooth = SharedPref.getPreferences(SurfaceDiagramActivity.this).getStringData("Bluetooth","none");
                String otg = SharedPref.getPreferences(SurfaceDiagramActivity.this).getStringData("OTG","none");
                if (!SurfaceDiagramActivity.this.is_running) {
                    if (SurfaceDiagramActivity.this.is_Finished) {
                        try {
                            if (Intrinsics.areEqual(bluetooth, "on")) {

                                SurfaceDiagramActivity.this.mConnectedThread.cancel();
                                SurfaceDiagramActivity.this.mConnectThread.cancel();

                            }
                            if (Intrinsics.areEqual(otg, "on")){

                                if (serialPort != null){
                                    serialPort.close();
                                }
                            }
                        } catch (Exception e) {
                            Log.d("MyTag", e.toString());
                        }
                        SurfaceDiagramActivity.this.finalData.clear();
                        SurfaceDiagramActivity.this.data.clear();
                        SurfaceDiagramActivity.this.colors.clear();
                        SurfaceDiagramActivity.this.twoDdata.clear();
                        SurfaceDiagramActivity.this.BottomBalance.clear();
                        SurfaceDiagramActivity.this.TopBalance.clear();
                        SurfaceDiagramActivity.this.LeftBalance.clear();
                        SurfaceDiagramActivity.this.RightBalance.clear();
                        SurfaceDiagramActivity.this.is_Finished = false;
                        try {
                            SurfaceDiagramActivity.this.startActivity(SurfaceDiagramActivity.this.getIntent());
                            SurfaceDiagramActivity.this.finish();
                            return;
                        } catch (Exception e2) {
                            Log.d("MyTag", e2.toString());
                            return;
                        }
                    }

                    SurfaceDiagramActivity.this.btn_save.setVisibility(View.GONE);
                    SurfaceDiagramActivity.this.StartTime = System.currentTimeMillis() / 1000;
                    SurfaceDiagramActivity.this.is_running = true;
                    SurfaceDiagramActivity.this.btn_StartAndStop.setText("Pause");
                    SurfaceDiagramActivity.this.btn_finish.setVisibility(View.GONE);

                    SurfaceDiagramActivity surfaceDiagramActivity = SurfaceDiagramActivity.this;
                    //surfaceDiagramActivity.start();
                    if (Intrinsics.areEqual(bluetooth, "on")) {
                        if(surfaceDiagramActivity.mBTDevice !=null) {
                            Log.d(SurfaceDiagramActivity.TAG, "Running");
                            if (SurfaceDiagramActivity.this.ThreadRunning) {
                                return;
                            }

                            surfaceDiagramActivity.start();
                            Log.d("bluetoothDevice", "" + surfaceDiagramActivity.mBTDevice);
                            surfaceDiagramActivity.startClient(surfaceDiagramActivity.mBTDevice, SurfaceDiagramActivity.MY_UUID_INSECURE);
                            return;
                        } else {
                            SurfaceDiagramActivity.this.btn_StartAndStop.setText("Start");
                            //Toast.makeText(mContext, "Please Choose A Bluetooth Device From Settings", Toast.LENGTH_LONG).show();
                            View view1 = findViewById(R.id.Controls);
                            Snackbar snackbar = Snackbar.make(view1, "Please Choose A Bluetooth Device From Settings", Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //finish();
                                    //startActivity(getIntent());
                                }
                            });
                            snackbar.setBackgroundTint(R.drawable.startup_screen_card_background);
                            snackbar.show();
                            return;
                        }
                    } else {
                        Log.d("connection", "Starting USB Connection...");
                        Log.d("usbProductId","USB Product Id = "+ String.valueOf(usbProductId));
                        Log.d("usbVendorId", "USB Vendor Id = "+String.valueOf(usbVendorId));

                        usbProductId= Integer.parseInt(SharedPref.getPreferences(getApplicationContext()).getStringData("UsbProductId","1234"));
                        usbVendorId = Integer.parseInt(SharedPref.getPreferences(getApplicationContext()).getStringData("UsbVendorId","1234"));

                        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
                        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
                        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
                        if(!deviceList.isEmpty()){
                            while (deviceIterator.hasNext()) {
                                UsbDevice deviceIt = deviceIterator.next();

                                if(deviceIt.getVendorId()==usbVendorId) {
                                    if(deviceIt.getProductId()==usbProductId) {
                                        device = deviceIt;
                                        Log.d("USB Device", "USB Device = "+String.valueOf(device));


                                    }

                                }
                            }
                            hasUsbPermission = manager.hasPermission(device);
                            Log.d("hasUsbPermission" ,"hasUsbPermission : " + hasUsbPermission);
                            if(hasUsbPermission){

                                startUsbConnection(device);
                                return;
                            } else {
                                PendingIntent pi = PendingIntent.getBroadcast(SurfaceDiagramActivity.this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_MUTABLE);
                                SurfaceDiagramActivity.this.registerReceiver(usbReceiver, new IntentFilter(ACTION_USB_PERMISSION));
                                manager.requestPermission(device, pi);
                                return;
                            }


                        } else {
                            //Toast.makeText(SurfaceDiagramActivity.this,"Please Connect an OTG Device",Toast.LENGTH_SHORT).show();
                            View view1 = findViewById(R.id.Controls);
                            Snackbar snackbar = Snackbar.make(view1, "Please Connect An USB Device", Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //finish();
                                    //startActivity(getIntent());
                                }
                            });
                            snackbar.setBackgroundTint(R.drawable.startup_screen_card_background);
                            snackbar.show();

                        }


                    }
                }
                Log.d(SurfaceDiagramActivity.TAG, "Pause Running");
                SurfaceDiagramActivity.this.is_running = false;
                SurfaceDiagramActivity.this.ThreadRunning = false;
                SurfaceDiagramActivity.this.btn_StartAndStop.setText("Start");

                if (Intrinsics.areEqual(bluetooth, "on")) {
                    if (mConnectedThread != null) {
                        SurfaceDiagramActivity.this.mConnectedThread.cancel();
                        Log.d(SurfaceDiagramActivity.TAG, "mConnectedThread Canceled");
                    }
                }
                if (Intrinsics.areEqual(otg, "on")) {
                    if (serialPort != null){
                        serialPort.close();
                        Log.d("USB", "Closing Serial Port");
                    }
                }
            }
        });
        this.btn_save.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (SurfaceDiagramActivity.this.is_save_pressed) {
                    if (ContextCompat.checkSelfPermission(SurfaceDiagramActivity.this, "android.permission.BLUETOOTH") != 0 || ContextCompat.checkSelfPermission(SurfaceDiagramActivity.this, "android.permission.BLUETOOTH_ADMIN") != 0 || ContextCompat.checkSelfPermission(SurfaceDiagramActivity.this, "android.permission.ACCESS_FINE_LOCATION") != 0 || ContextCompat.checkSelfPermission(SurfaceDiagramActivity.this, "android.permission.ACCESS_COARSE_LOCATION") != 0) {
                        SurfaceDiagramActivity.this.requestPermissions(new String[]{"android.permission.BLUETOOTH", "android.permission.BLUETOOTH_ADMIN", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, 200);
                        return;
                    }
                    String obj = SurfaceDiagramActivity.this.txt_saving_name.getText().toString();
                    if (obj.contains("realNumbers") || obj.contains("colorsString") || obj.contains("numbersXYZ") || obj.contains("loc") || obj.contains("currentLoc")) {
                        Toast.makeText(SurfaceDiagramActivity.this, "Name should not contain this word!", Toast.LENGTH_LONG).show();
                        return;
                    } else if (obj.length() > 3) {
                        SurfaceDiagramActivity.this.saveGraph();
                        return;
                    } else {
                        Toast.makeText(SurfaceDiagramActivity.this, "Name length must be more than 3", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                SurfaceDiagramActivity.this.txt_saving_name.setVisibility(View.VISIBLE);
                SurfaceDiagramActivity.this.is_save_pressed = true;
                SurfaceDiagramActivity.this.btn_save.setText("DONE");
            }
        });
        this.btn_2d_depth_show.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (SurfaceDiagramActivity.this.userWantsDepth) {
                    SurfaceDiagramActivity.this.userWantsDepth = false;
                    SurfaceDiagramActivity.this.angleX = 90.0f;
                    SurfaceDiagramActivity.this.angleY = -90.0f;
                    SurfaceDiagramActivity.this.btn_2d_depth_show.setBackground(ActivityCompat.getDrawable(SurfaceDiagramActivity.this,R.drawable.button_gradiant_1));
                    SurfaceDiagramActivity.this.btn_3d_favorable_show.setBackground(ActivityCompat.getDrawable(SurfaceDiagramActivity.this,R.drawable.button_gradiant_1));
                    SurfaceDiagramActivity.this.btn_2d_numbers_show.setBackground(ActivityCompat.getDrawable(SurfaceDiagramActivity.this,R.drawable.button_gradiant_1));
                    return;
                }


                       Log.d("groundCoef", " "+groundCoef);
                       depthValues.clear();
                       SurfaceDiagramActivity.this.userWantsDepth = true;
                       SurfaceDiagramActivity.this.userWantsFavorable = false;
                       SurfaceDiagramActivity.this.userWantsNumbers = false;
                       int size = SurfaceDiagramActivity.this.realNumbers.size();
                       SurfaceDiagramActivity.this.depthValues = calculate_depth(realNumbers,groundCoef);
                       int size1 = SurfaceDiagramActivity.this.depthValues.size();
                       Log.d("depthValues.size","depthValues.size =" +size1);
                       Log.d("depthValues","depthValue = " + depthValues);
                       SurfaceDiagramActivity surfaceDiagramActivity = SurfaceDiagramActivity.this;
                       surfaceDiagramActivity.x = surfaceDiagramActivity.MinX;
                       SurfaceDiagramActivity surfaceDiagramActivity4 = SurfaceDiagramActivity.this;
                       surfaceDiagramActivity4.z = surfaceDiagramActivity4.MinX;
                       DecimalFormat df = new DecimalFormat();
                       df.setMaximumFractionDigits(2);
                       //texturedSurfaceShapes.clear();
                       texturedSurfaceShapes1.clear();
                       bitmaps.clear();
                       if (SurfaceDiagramActivity.this.texturedSurfaceShapes1.isEmpty()) {
                           SurfaceDiagramActivity.this.makeNumbersXYZ();
                           for (int i = 0; i < size1; i++) {
                               SurfaceDiagramActivity surfaceDiagramActivity5 = SurfaceDiagramActivity.this;
                               surfaceDiagramActivity5.drawSomething1(String.valueOf(df.format(surfaceDiagramActivity5.depthValues.get(i))));
                               SurfaceDiagramActivity.this.texturedSurfaceShapes1.add(new TexturedSurfaceShape(SurfaceDiagramActivity.this.myXYZs.get(i), SurfaceDiagramActivity.this.bitmaps1.get(i)));
                           }

                       }
                       SurfaceDiagramActivity.this.btn_2d_numbers_show.setBackground(ActivityCompat.getDrawable(SurfaceDiagramActivity.this,R.drawable.button_gradiant_1));
                       SurfaceDiagramActivity.this.btn_3d_favorable_show.setBackground(ActivityCompat.getDrawable(SurfaceDiagramActivity.this,R.drawable.button_gradiant_1));
                       SurfaceDiagramActivity.this.btn_2d_depth_show.setBackground(ActivityCompat.getDrawable(SurfaceDiagramActivity.this,R.drawable.button_gradiant_2));


            }
        });
        this.btn_2d_numbers_show.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (SurfaceDiagramActivity.this.userWantsNumbers) {
                    SurfaceDiagramActivity.this.userWantsNumbers = false;
                    SurfaceDiagramActivity.this.angleX = 90.0f;
                    SurfaceDiagramActivity.this.angleY = -90.0f;
                    SurfaceDiagramActivity.this.btn_2d_depth_show.setBackground(ActivityCompat.getDrawable(SurfaceDiagramActivity.this,R.drawable.button_gradiant_1));
                    SurfaceDiagramActivity.this.btn_3d_favorable_show.setBackground(ActivityCompat.getDrawable(SurfaceDiagramActivity.this,R.drawable.button_gradiant_1));
                    SurfaceDiagramActivity.this.btn_2d_numbers_show.setBackground(ActivityCompat.getDrawable(SurfaceDiagramActivity.this,R.drawable.button_gradiant_1));
                    return;
                }
                SurfaceDiagramActivity.this.userWantsNumbers = true;
                SurfaceDiagramActivity.this.userWantsFavorable = false;
                SurfaceDiagramActivity.this.userWantsDepth = false;
                int size = SurfaceDiagramActivity.this.realNumbers.size();
                SurfaceDiagramActivity surfaceDiagramActivity = SurfaceDiagramActivity.this;
                surfaceDiagramActivity.x = surfaceDiagramActivity.MinX;
                SurfaceDiagramActivity surfaceDiagramActivity2 = SurfaceDiagramActivity.this;
                surfaceDiagramActivity2.z = surfaceDiagramActivity2.MinX;
                if (SurfaceDiagramActivity.this.texturedSurfaceShapes.isEmpty()) {
                    SurfaceDiagramActivity.this.makeNumbersXYZ();
                    for (int i = 0; i < size; i++) {
                        SurfaceDiagramActivity surfaceDiagramActivity3 = SurfaceDiagramActivity.this;
                        surfaceDiagramActivity3.drawSomething(String.valueOf(surfaceDiagramActivity3.realNumbers.get(i)));
                        SurfaceDiagramActivity.this.texturedSurfaceShapes.add(new TexturedSurfaceShape(SurfaceDiagramActivity.this.myXYZs.get(i), SurfaceDiagramActivity.this.bitmaps.get(i)));
                    }
                }
                SurfaceDiagramActivity.this.btn_2d_depth_show.setBackground(ActivityCompat.getDrawable(SurfaceDiagramActivity.this,R.drawable.button_gradiant_1));
                SurfaceDiagramActivity.this.btn_3d_favorable_show.setBackground(ActivityCompat.getDrawable(SurfaceDiagramActivity.this,R.drawable.button_gradiant_1));
                SurfaceDiagramActivity.this.btn_2d_numbers_show.setBackground(ActivityCompat.getDrawable(SurfaceDiagramActivity.this,R.drawable.button_gradiant_2));
            }
        });
        this.btn_3d_favorable_show.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (SurfaceDiagramActivity.this.userWantsFavorable) {
                    SurfaceDiagramActivity.this.userWantsFavorable = false;
                    SurfaceDiagramActivity.this.userWantsNumbers = false;
                    SurfaceDiagramActivity.this.load();
                    SurfaceDiagramActivity.this.btn_3d_favorable_show.setBackground(ActivityCompat.getDrawable(SurfaceDiagramActivity.this,R.drawable.button_gradiant_1));
                    SurfaceDiagramActivity.this.btn_2d_numbers_show.setBackground(ActivityCompat.getDrawable(SurfaceDiagramActivity.this,R.drawable.button_gradiant_1));
                    return;
                }
                SurfaceDiagramActivity.this.userWantsFavorable = true;
                SurfaceDiagramActivity.this.userWantsNumbers = false;
                SurfaceDiagramActivity.this.userWantsDepth = false;
                SurfaceDiagramActivity.this.makeStrings();
                SurfaceDiagramActivity.this.load2();
                SurfaceDiagramActivity.this.btn_2d_depth_show.setBackground(ActivityCompat.getDrawable(SurfaceDiagramActivity.this,R.drawable.button_gradiant_1));
                SurfaceDiagramActivity.this.btn_3d_favorable_show.setBackground(ActivityCompat.getDrawable(SurfaceDiagramActivity.this,R.drawable.button_gradiant_2));
                SurfaceDiagramActivity.this.btn_2d_numbers_show.setBackground(ActivityCompat.getDrawable(SurfaceDiagramActivity.this,R.drawable.button_gradiant_1));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //startUsbConnection(device);
    }

    @Override
    public void onPause() {
        super.onPause();
        /*if(serialPort!=null){
            serialPort.close();
        }*/
    }
    public void startUsbConnection(UsbDevice usbdevice) {
        Log.d("usbConnection", "startUsbConnection: Started.");
        //this.mProgressDialog = ProgressDialog.show(this.mContext, "Connecting Bluetooth", "Please Wait...", true);
        /* mProgressDialog = new MyProgressDialog(this.mContext);
        mProgressDialog.show(); */
        UsbConnectionThread usbConnectionThread = new UsbConnectionThread(usbdevice);
        usbConnectionThread.start();
    }
    public class UsbConnectionThread extends Thread  {

        public UsbConnectionThread(UsbDevice d){
            Log.d("usbConnection", "UsbConnectionThread Started ");
            connection = manager.openDevice(d);
            device =d;
        }
        @Override
        public void run() {
            //UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
            Log.d("usbConnection", "UsbConnectionThread : run() ");

            Log.d("usbConnection", "Connection = " + connection);
            serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
            Log.d("usbConnection", "serialPort = " + serialPort);
            if (serialPort != null) {
                if (serialPort.syncOpen()) { //Set Serial Connection Parameters.

                    serialPort.setBaudRate(9600);
                    serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                    serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                    serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                    serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                    //serialPort.read(mCallback); //
                    //tvAppend(textView,"Serial Connection Opened!\n");
                    new ReadUsbThread().start();

                } else {
                    //Toast.makeText(SurfaceDiagramActivity.this, "SERIAL PORT CAN NOT BE OPENED", Toast.LENGTH_SHORT).show();
                    Log.d("SERIAL", "PORT NOT OPEN");
                    runOnUiThread(new Runnable() {
                        @Override // java.lang.Runnable
                        public void run() {

                            View view = findViewById(R.id.Controls);
                            Snackbar snackbar = Snackbar.make(view, "Serial Port Can Not Be Opened", Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction("Refresh", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    finish();
                                    startActivity(getIntent());
                                }
                            });
                            snackbar.setBackgroundTint(R.drawable.startup_screen_card_background);
                            snackbar.show();
                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override // java.lang.Runnable
                    public void run() {

                        View view = findViewById(R.id.Controls);
                        Snackbar snackbar = Snackbar.make(view, "USB Devise Not Supported", Snackbar.LENGTH_INDEFINITE);
                        snackbar.setAction("Refresh", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                                startActivity(getIntent());
                            }
                        });
                        snackbar.setBackgroundTint(R.drawable.startup_screen_card_background);
                        snackbar.show();
                    }
                });
                //Toast.makeText(SurfaceDiagramActivity.this, "SERIAL PORT IS NULL", Toast.LENGTH_SHORT).show();
                Log.d("SERIAL", "PORT IS NULL");
            }
        }
    }
    public class ReadUsbThread extends Thread {

        @Override
        public void run() {
            Log.d("usbConnection", "ReadUsbThread Started : run() ");
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            Integer val_uT;
            byte[] buffer = new byte[1024];
            double currentTimeMillis = System.currentTimeMillis();
            while(true){
                try {
                int n = serialPort.syncRead(buffer, 0);
                if(n > 0) {
                    byte[] received = new byte[n];
                    System.arraycopy(buffer, 0, received, 0, n);
                    String receivedStr = new String(received);
                    for (char c : receivedStr.toCharArray()) {
                        arrayList2.add(Character.valueOf(c));
                        if (c == '\r') {
                            StringBuilder sb = new StringBuilder();
                            Iterator it = arrayList2.iterator();
                            while (it.hasNext()) {
                                sb.append(((Character) it.next()).charValue());
                            }
                            try {
                                val_uT = (int) ((Integer.valueOf(Integer.parseInt(String.valueOf(sb).trim())))*sensor_factor);
                                arrayList.add(val_uT);
                            } catch (NumberFormatException unused) {
                            }
                            Log.d("result", sb.toString());
                            arrayList2.clear();
                        }
                    }
                    double currentTimeMillis2 = System.currentTimeMillis();
                    Double.isNaN(currentTimeMillis2);
                    if (currentTimeMillis2 - currentTimeMillis >= SurfaceDiagramActivity.this.SleepTime) {
                        currentTimeMillis = System.currentTimeMillis();
                        SurfaceDiagramActivity.this.getAverage(arrayList);
                        arrayList.clear();
                    }

                }
            } catch (NullPointerException e){
                    Log.e(SurfaceDiagramActivity.TAG, "write: Error reading Input Stream. " + e.getMessage());
                    String bluetooth = SharedPref.getPreferences(SurfaceDiagramActivity.this).getStringData("Bluetooth","none");
                    String otg = SharedPref.getPreferences(SurfaceDiagramActivity.this).getStringData("OTG","none");

                    if((SurfaceDiagramActivity.this.ThreadRunning)&&(SurfaceDiagramActivity.this.PalsPerRow * SurfaceDiagramActivity.this.PalsPerColumn != SurfaceDiagramActivity.this.realNumbers.size())) {
                        runOnUiThread(new Runnable() {
                            @Override // java.lang.Runnable
                            public void run() {

                                View view = findViewById(R.id.Controls);
                                Snackbar snackbar = Snackbar.make(view, "Please Check Your Connection And Press Start", Snackbar.LENGTH_INDEFINITE);
                                snackbar.setAction("Refresh", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //finish();
                                        //startActivity(getIntent());
                                        Log.d(SurfaceDiagramActivity.TAG, "Pause Running");
                                        SurfaceDiagramActivity.this.is_running = false;
                                        SurfaceDiagramActivity.this.ThreadRunning = false;
                                        SurfaceDiagramActivity.this.btn_StartAndStop.setText("Start");

                                        if (Intrinsics.areEqual(bluetooth, "on")) {
                                            if (mConnectedThread != null) {
                                                SurfaceDiagramActivity.this.mConnectedThread.cancel();
                                                Log.d(SurfaceDiagramActivity.TAG, "mConnectedThread Canceled");
                                            }
                                        }
                                        if (Intrinsics.areEqual(otg, "on")){
                                            if (serialPort != null) {
                                                serialPort.close();
                                                Log.d("USB", "Closing Serial Port");
                                            }
                                        }
                                    }
                                });
                                snackbar.setBackgroundTint(R.drawable.startup_screen_card_background);
                                snackbar.show();
                            }
                        });
                    }
                    return;
                }
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
        final int trans = Color.argb(0,1,1,1);
        this.mCanvas = new Canvas(this.mBitmap);
        if (this.texturedSurfaceShapes1.size() % 2 == 0) {
            this.mCanvas.drawColor(green);
        } else {
            this.mCanvas.drawColor(this.mColorRectangle);
            //this.mCanvas.drawColor(trans);
        }
        if (this.texturedSurfaceShapes1.size() % 2 == 0) {
            this.mPaint.setColor(this.mColorRectangle);
        } else {
            this.mPaint.setColor(this.mColorBackground);
        }
        /*if (str.length() == 4) {
            this.mPaint.setTextSize(93.0f);
        } else if (str.length() == 3) {
            this.mPaint.setTextSize(140.0f);
        } else if (str.length() == 2) {
            this.mPaint.setTextSize(185.0f);
        } else {
            this.mPaint.setTextSize(220.0f);
        }*/
        //this.mPaint.setAlpha(0);
        this.mPaint.setTextSize(100.0f);
        //this.mCanvas.rotate(90.0f);
        //this.mCanvas.drawText(str, 100.0f, 230.0f, this.mPaint);
        this.mCanvas.rotate(-90.0f,100.0f, 230.0f);
        this.mCanvas.drawText(str, 50.0f, 400.0f, this.mPaint);
        this.bitmaps1.add(this.mBitmap);
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

    public void allow_to_finish() {
        try {
            String bluetooth = SharedPref.getPreferences(SurfaceDiagramActivity.this).getStringData("Bluetooth","none");
            String otg = SharedPref.getPreferences(SurfaceDiagramActivity.this).getStringData("OTG","none");
            if (Intrinsics.areEqual(bluetooth, "on")) {
                if (this.mConnectThread != null) {
                    this.mConnectThread.cancel();
                    this.mConnectThread = null;
                    Log.d("MyTag", "mConnectThread = null");
                }
                //this.mConnectThread.cancel();
                this.mConnectedThread.cancel();
                Log.d("MyTag", "mConnectedThread.cancel()");
            }
            if (Intrinsics.areEqual(otg, "on")) {
                if (serialPort != null) {
                    serialPort.close();
                }
            }
        } catch (Exception e) {
            Log.d("MyTag", e.toString());
        }
        this.is_Finished = true;
        this.is_running = false;
        this.ThreadRunning = false;
        balancing();
        Log.d(TAG, "Stop Running");
        this.btn_StartAndStop.setText("Start");
        this.btn_finish.setVisibility(View.GONE);
        this.btn_save.setVisibility(View.VISIBLE);
        this.btn_return.setVisibility(View.VISIBLE);
        this.btn_3d_favorable_show.setVisibility(View.VISIBLE);
        this.btn_2d_numbers_show.setVisibility(View.VISIBLE);
        this.btn_2d_depth_show.setVisibility(View.VISIBLE);
        this.btn_2d_depth_show.setBackground(ActivityCompat.getDrawable(this,R.drawable.button_gradiant_1));
        this.btn_2d_numbers_show.setBackground(ActivityCompat.getDrawable(this,R.drawable.button_gradiant_1));
        this.btn_3d_favorable_show.setBackground(ActivityCompat.getDrawable(this,R.drawable.button_gradiant_2));
        this.BottomBalance.clear();
        this.TopBalance.clear();
        this.LeftBalance.clear();
        this.RightBalance.clear();
        float f = this.MinX;
        this.x = f;
        this.z = f;
        bindAndPrepare();
        makeStrings();
        load2();
    }


    public void saveGraph() {
        String obj = this.txt_saving_name.getText().toString();
        makeNumbersXYZ();

        TinyDB tinyDB = new TinyDB(this);
        DateTimeFormatter  dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime  now = LocalDateTime.now();
        makeStrings();
        Log.d("numbersXYZtoString", String.valueOf(this.numbersXYZ));
        tinyDB.putString(obj, this.allText.toString());
        tinyDB.putString("*realNumbers_@" + obj, this.numbers_to_Save.toString());
        tinyDB.putString("*numbersXYZ_@" + obj, this.numbersXYZ.toString());
        tinyDB.putString("*colorsString_@" + obj, this.colorsString.toString());
        tinyDB.putString("*loc_@" + obj, tinyDB.getString("currentLoc"));
        tinyDB.putString("*datetime_@" + obj, dtf.format(now)); /*dtf.format(now)*/
        tinyDB.putFloat("*groundCoef_@" + obj, this.groundCoef);
        tinyDB.putString("*groundName_@" + obj, this.groundName);
        Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
        this.txt_saving_name.setText("");
        this.txt_saving_name.setVisibility(View.GONE);
        this.btn_save.setEnabled(false);
        this.is_save_pressed = false;
        this.btn_save.setText("SAVED!!");
    }


    public void makeNumbersXYZ() {
        int size = this.realNumbers.size();
        if (this.myXYZs.size() == 0) {
            for (int i = 0; i < size; i++) {
                float f = this.x;
                float f2 = this.z;
                float f3 = this.zyIncrease;
                float[] fArr = {f, 0.0f, f2, f + f3, 0.0f, f2, f, 0.0f, f2, f + f3, 0.0f, f2, f + f3, 0.0f, f2 - f3, f, 0.0f, f2 - f3, f + f3, 0.0f, f2 - f3, f, 0.0f, f2 - f3};
                float[] fArr2 = {fArr[6], fArr[7], fArr[8], fArr[9], fArr[10], fArr[11], fArr[21], fArr[22], fArr[23], fArr[18], fArr[19], fArr[20], fArr[12], fArr[13], fArr[14], fArr[15], fArr[16], fArr[17], fArr[18], fArr[19], fArr[20], fArr[21], fArr[22], fArr[23], fArr[15], fArr[16], fArr[17], fArr[0], fArr[1], fArr[2], fArr[21], fArr[22], fArr[23], fArr[6], fArr[7], fArr[8], fArr[3], fArr[4], fArr[5], fArr[12], fArr[13], fArr[14], fArr[9], fArr[10], fArr[11], fArr[18], fArr[19], fArr[20], fArr[0], fArr[1], fArr[2], fArr[3], fArr[4], fArr[5], fArr[6], fArr[7], fArr[8], fArr[9], fArr[10], fArr[11], fArr[15], fArr[16], fArr[17], fArr[12], fArr[13], fArr[14], fArr[0], fArr[1], fArr[2], fArr[3], fArr[4], fArr[5]};
                if (this.palsOrientation == 1) {
                    if (this.PalsPerColumn % 2 == 0) {
                        int i2 = this.zigzagRound;
                        if (i2 % 2 == 0) {
                            float f4 = this.MaxX;
                            if (f == f4 - f3) {
                                this.z = f2 + f3;
                                this.zigzagRound = i2 + 1;
                            } else if (f < f4) {
                                this.x = f + f3;
                            }
                        } else {
                            float f5 = this.MinX;
                            if (f == f5) {
                                this.z = f2 + f3;
                                this.zigzagRound = i2 + 1;
                            } else if (f > f5) {
                                this.x = f - f3;
                            }
                        }
                    } else {
                        int i3 = this.zigzagRound;
                        if (i3 % 2 == 0 && f == this.MaxX) {
                            this.z = f2 + f3;
                            this.zigzagRound = i3 + 1;
                        } else {
                            if (this.zigzagRound % 2 == 0) {
                                float f6 = this.x;
                                if (f6 < this.MaxX) {
                                    this.x = f6 + this.zyIncrease;
                                }
                            }
                            int i4 = this.zigzagRound;
                            if (i4 % 2 != 0 && this.x == this.MinX) {
                                this.z += this.zyIncrease;
                                this.zigzagRound = i4 + 1;
                            } else if (this.zigzagRound % 2 != 0) {
                                float f7 = this.x;
                                if (f7 > this.MinX) {
                                    this.x = f7 - this.zyIncrease;
                                }
                            }
                        }
                    }
                } else {
                    float f8 = f + f3;
                    this.x = f8;
                    if (this.PalsPerColumn % 2 == 0) {
                        if (f8 == this.MaxX) {
                            this.z = f2 + f3;
                            this.x = this.MinX;
                        }
                    } else if (f8 > this.MaxX) {
                        this.z = f2 + f3;
                        this.x = this.MinX;
                    }
                }
                this.myXYZs.add(fArr2);
            }
        }
        Log.d("myXYZs", String.valueOf(this.myXYZs));
    }


    public void makeStrings() {
        float[] next;
        float[] next2;
        float[] next3;
        makeNumbersXYZ();
        if (this.is_strings_maked) {
            return;
        }
        if (this.allText.length() == 0) {
            this.is_strings_maked = true;
            Iterator<float[]> it = this.data.iterator();
            while (it.hasNext()) {
                for (float f : it.next()) {
                    StringBuilder sb = this.allText;
                    sb.append(f);
                    sb.append("f, ");
                }
                this.allText.append(" / ");
            }
        }
        Iterator<float[]> it2 = this.myXYZs.iterator();
        while (it2.hasNext()) {
            for (float f2 : it2.next()) {
                StringBuilder sb2 = this.numbersXYZ;
                sb2.append(f2);
                sb2.append("f,");
            }
            this.numbersXYZ.append("/");
        }
        Iterator<float[]> it3 = this.colors.iterator();
        while (it3.hasNext()) {
            for (float f3 : it3.next()) {
                StringBuilder sb3 = this.colorsString;
                sb3.append(f3);
                sb3.append("f,");
            }
            this.colorsString.append("/");
        }
        if (this.numbers_to_Save.length() == 0) {
            Iterator<Integer> it4 = this.realNumbers.iterator();
            while (it4.hasNext()) {
                StringBuilder sb4 = this.numbers_to_Save;
                sb4.append(it4.next());
                sb4.append(",");
            }
        }


        Log.d("myXYZs", String.valueOf(this.myXYZs));
        Log.d("myXYZs", String.valueOf((this.myXYZs).size()));
        Log.d("myXYZsString = numbersXYZ", String.valueOf(this.numbersXYZ));
        Log.d("myXYZsString = numbersXYZ", String.valueOf((this.numbersXYZ).length()));

        Log.d("data", String.valueOf(this.data));
        Log.d("data", String.valueOf((this.data).size()));
        Log.d("dataString = allText", String.valueOf(this.allText));
        Log.d("dataString = allText", String.valueOf((this.allText).length()));

        Log.d("colors", String.valueOf(this.colors));
        Log.d("colors", String.valueOf((this.colors).size()));
        Log.d("colorsString = colorsString", String.valueOf(this.colorsString));
        Log.d("colorsString = colorsString", String.valueOf((this.colorsString).length()));


        Log.d("realNumbers", String.valueOf(this.realNumbers));
        Log.d("realNumbers", String.valueOf((this.realNumbers).size()));
        Log.d("realNumbersString = numbers_to_Save", String.valueOf(this.numbers_to_Save));
        Log.d("realNumbersString = numbers_to_Save", String.valueOf((this.numbers_to_Save).length()));

        Log.d("finaltwoDdata", String.valueOf(this.finaltwoDdata));
        Log.d("finaltwoDdata", String.valueOf((this.finaltwoDdata).size()));


    }


    public void load() {
        String[] split = this.allText.toString().split("/");
        String[] split1 = this.favorableXYZsString.toString().split("/");
        this.data.clear();
        this.favorableXYZs.clear();
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

        for (String str : split1) {
            float[] fArr1 = new float[72];
            int i = 0;
            for (String str2 : str.split(",")) {
                try {
                    fArr1[i] = Float.parseFloat(str2);
                    i++;
                } catch (NumberFormatException unused) {
                }
            }
            this.favorableXYZs.add(fArr1);
        }
        try {
            this.finalData.clear();
            for (int i2 = 0; i2 < this.data.size(); i2++) {
                this.finalData.add(new SurfaceShape(this.data.get(i2), this.colors.get(i2)));
                //this.finalData.add(new SurfaceShape(this.favorableXYZs.get(i2), this.colors.get(i2)));
            }
        } catch (IndexOutOfBoundsException unused2) {
        }
    }


    public void load2() {
        String[] split = this.allText.toString().split("/");
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
        Iterator<float[]> it = this.data.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            float[] next = it.next();
            for (int i2 = 1; i2 < next.length; i2 += 3) {
                if (this.Max < next[i2]) {
                    this.Max = next[i2];
                }
                if (this.Min > next[i2]) {
                    this.Min = next[i2];
                }
            }
        }
        this.Mid = (Math.abs(this.Max) - Math.abs(this.Min)) / 2.0f;
        this.favorableXYZs = this.data;
        for (int i3 = 0; i3 < this.favorableXYZs.size(); i3++) {
            for (int i4 = 1; i4 < this.favorableXYZs.get(i3).length; i4 += 3) {
                if (this.favorableXYZs.get(i3)[i4] > this.Mid) {
                    this.favorableXYZs.get(i3)[i4] = this.Mid - (this.favorableXYZs.get(i3)[i4] - this.Mid);
                }
            }
        }
        Iterator<float[]> it5 = this.favorableXYZs.iterator();
        while (it5.hasNext()) {
            for (float f5 : it5.next()) {
                StringBuilder sb5 = this.favorableXYZsString;
                sb5.append(f5);
                sb5.append("f,");
            }
            this.favorableXYZsString.append("/");
        }
       // Log.d("favorableXYZs", String.valueOf(this.favorableXYZs));
        //Log.d("favorableXYZs", String.valueOf((this.favorableXYZs).size()));
        try {
            this.finalData.clear();
            for (int i5 = 0; i5 < this.data.size(); i5++) {
                this.finalData.add(new SurfaceShape(this.data.get(i5), this.colors.get(i5)));
                //this.finalData.add(new SurfaceShape(this.favorableXYZs.get(i5), this.colors.get(i5)));
            }
        } catch (IndexOutOfBoundsException unused2) {
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
        if (str.length() == 8) {
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
        }


        //this.mCanvas.drawText(str, 100.0f, 230.0f, this.mPaint);
        this.mCanvas.rotate(-90.0f,100.0f, 230.0f);
       this.mCanvas.drawText(str, 50.0f, 400.0f, this.mPaint);

        this.bitmaps.add(this.mBitmap);
    }

    private void bindAndPrepare() {
        char c;
        int i;
        this.manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient((Activity) this);
        getWindow().addFlags(1024);
        this.mContext = this;
        this.ground_type = (TextView) findViewById(R.id.ground_type_text);
        this.ground_type_image = (ImageView) findViewById(R.id.ground_type_image);
        this.increase_unit = (TextView) findViewById(R.id.surface_increase_unit);
        this.reduce_unit = (TextView) findViewById(R.id.surface_reduce_unit);
        this.main_unit_txt = (TextView) findViewById(R.id.surface_mainUnit_txt);
        this.OutPut = (TextView) findViewById(R.id.OutPut);
        this.txt_saving_name = (EditText) findViewById(R.id.txt_saving_data);
        this.btn_StartAndStop = (Button) findViewById(R.id.btn_StartAndStop);
        this.btn_2d_depth_show = (Button) findViewById(R.id.btn_2d_depth_show);
        this.btn_2d_numbers_show = (Button) findViewById(R.id.btn_2d_numbers_show);
        this.btn_3d_favorable_show = (Button) findViewById(R.id.btn_2d_favorable_show);
        this.btn_finish = (Button) findViewById(R.id.btn_Finish);
        this.btn_Collaps = (Button) findViewById(R.id.btn_Collaps_main);
        this.btn_Collaps_moving = (Button) findViewById(R.id.btn_Collaps_moving);
        this.zoom_out = (ImageView) findViewById(R.id.btn_zoom_out);
        this.zoom_in = (ImageView) findViewById(R.id.btn_zoom_in);
        this.btn_move_right = (Button) findViewById(R.id.btn_move_right);
        this.btn_move_left = (Button) findViewById(R.id.btn_move_left);
        this.btn_move_up = (Button) findViewById(R.id.btn_move_up);
        this.btn_move_down = (Button) findViewById(R.id.btn_move_down);
        this.btn_save = (Button) findViewById(R.id.btn_Save);
        this.usbProductId= Integer.parseInt(SharedPref.getPreferences(getApplicationContext()).getStringData("UsbProductId","1234"));
        this.usbVendorId = Integer.parseInt(SharedPref.getPreferences(getApplicationContext()).getStringData("UsbVendorId","1234"));
        //SharedPref.getPreferences(getApplicationContext()).getStringData("UsbVendorId","None");
        //this.btn_rotate = (ImageView) findViewById(R.id.btn_rotate);
        this.btn_return = (ImageView) findViewById(R.id.btn_return);
        Bundle extras = getIntent().getExtras();
        //this.mBTDevice = (BluetoothDevice) extras.get("mBTDevice");
        //sp = getSharedPreferences("userSettings", Context.MODE_PRIVATE);
        String bluetooth = SharedPref.getPreferences(SurfaceDiagramActivity.this).getStringData("Bluetooth", "none");
        String otg = SharedPref.getPreferences(SurfaceDiagramActivity.this).getStringData("OTG", "none");
        Log.d("BluetoothDevice", SharedPref.getPreferences(SurfaceDiagramActivity.this).getStringData("Devicename", "None"));
        Log.d("Bluetooth",SharedPref.getPreferences(SurfaceDiagramActivity.this).getStringData("Bluetooth", "none"));

        if (Intrinsics.areEqual(bluetooth, "on")){
            if (ContextCompat.checkSelfPermission(SurfaceDiagramActivity.this, "android.permission.BLUETOOTH_CONNECT") != 0 || ContextCompat.checkSelfPermission(SurfaceDiagramActivity.this, "android.permission.BLUETOOTH_ADMIN") != 0 || ContextCompat.checkSelfPermission(SurfaceDiagramActivity.this, "android.permission.ACCESS_FINE_LOCATION") != 0 || ContextCompat.checkSelfPermission(SurfaceDiagramActivity.this, "android.permission.ACCESS_COARSE_LOCATION") != 0 ) {
                SurfaceDiagramActivity.this.requestPermissions(new String[]{"android.permission.BLUETOOTH_CONNECT","android.permission.BLUETOOTH_ADMIN", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, 200);
                //return;
            }
            Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
        if (mBluetoothAdapter.isEnabled()) {
            if (bondedDevices != null) {
                for (BluetoothDevice bluetoothDevice : bondedDevices) {
                    if (Intrinsics.areEqual(bluetoothDevice.getName(), SharedPref.getPreferences(SurfaceDiagramActivity.this).getStringData("Devicename", "None"))) {
                        this.mBTDevice = (BluetoothDevice) bluetoothDevice;
                        Log.d("BluetoothDeviceinside",""+bluetoothDevice.getName());
                        Toast.makeText(this, "Paired with " + bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
                    }
                }

            } else {
                Toast.makeText(this, "Please pair to a Bluetooth Device", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(this, "Please Turn On The Bluetooth", Toast.LENGTH_LONG).show();
        }
    }



        this.SleepTime = Integer.parseInt(String.valueOf(extras.get("SleepTime"))) * 1000;
        this.renderer = new MyGLRenderer(this);
        this.glView = new MyGLSurfaceView(this);
        this.linearLayout = (LinearLayout) findViewById(R.id.surfaceView);
        this.Controls = (ConstraintLayout) findViewById(R.id.Controls);
        this.moving_Controls = (ConstraintLayout) findViewById(R.id.moving_Controls);
        this.linearLayout.addView(this.glView);
        this.toneGen1 = new ToneGenerator(3, 100);
        this.groundCoef = extras.getFloat("groundCoef");
        this.groundName = extras.getString("groundText");
        Log.d("groundCoef", String.valueOf(this.groundCoef));
        Log.d("groundName", String.valueOf(this.groundName));
        this.PalsPerColumn = Integer.parseInt(String.valueOf(extras.get("PalsPerColumn")));
        this.PalsPerRow = Integer.parseInt(String.valueOf(extras.get("PalsPerRow")));
        Log.d("PalsPerColumn", String.valueOf(this.PalsPerColumn));
        String valueOf = String.valueOf(extras.get("palsOrientation"));

        /*******A VERIFIER******/

        int hashCode = valueOf.hashCode();

        if (valueOf.equals("Parallel")) {
            c = 1;
        } else if (valueOf.equals("Zigzag")) {
            c = 0;
        } else c = 65535;

      /*******A VERIFIER******/
        if (c == 0) {
            this.palsOrientation = 2;/*1*/
        } else if (c == 1) {
            this.palsOrientation = 2;
        } else {
            this.palsOrientation = 0;
        }
        /*******A VERIFIER******/
        int i2 = this.PalsPerColumn;
        this.FirstZ = (i2 * (-1)) - 10;
        this.FirstZfar = (i2 * (-1)) - 30;
        this.zyIncrease = 1.0f;
        float f = ((float) i2 / 2) * (-1.0f);
        this.MinX = f;
        this.MaxX = (float) i2 / 2;
        this.x = f;
        this.z = f;
        this.CoefficientCounterLTR = new int[i2];
        this.CoefficientCounterRTL = new int[i2];
        int i3 = 0;
        int i4 = 1;
        while (true) {
            i = this.PalsPerColumn;
            if (i3 >= i) {
                break;
            }
            this.CoefficientCounterRTL[i3] = i4;
            i4 += 2;
            i3++;
        }
        int i5 = (i + i) - 1;
        for (int i6 = 0; i6 < this.PalsPerColumn; i6++) {
            this.CoefficientCounterLTR[i6] = i5;
            i5 -= 2;
        }
        Log.d("CoefficientCounterRTL", Arrays.toString(this.CoefficientCounterRTL));
        Log.d("CoefficientCounterLTR", Arrays.toString(this.CoefficientCounterLTR));
        this.mColorBackground = ResourcesCompat.getColor(getResources(), R.color.colorWhite, null);
        this.mColorRectangle = ResourcesCompat.getColor(getResources(), R.color.colorRectangle, null);
        this.mColorAccent = ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null);
    }


    public class AcceptThread extends Thread {
        private BluetoothServerSocket mmServerSocket = null;

        public AcceptThread() {
            BluetoothServerSocket bluetoothServerSocket = null;
            try {

                if ((ActivityCompat.checkSelfPermission(SurfaceDiagramActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) ||(ActivityCompat.checkSelfPermission(SurfaceDiagramActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)||(ActivityCompat.checkSelfPermission(SurfaceDiagramActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)||(ActivityCompat.checkSelfPermission(SurfaceDiagramActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)){

                    requestPermissions(new String[]{"android.permission.BLUETOOTH_CONNECT", "android.permission.BLUETOOTH_SCAN", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, 200);

                    //return;
                }
                bluetoothServerSocket = SurfaceDiagramActivity.this.mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(SurfaceDiagramActivity.appName, SurfaceDiagramActivity.MY_UUID_INSECURE);
                Log.d(SurfaceDiagramActivity.TAG, "AcceptThread: Setting up Server using: " + SurfaceDiagramActivity.MY_UUID_INSECURE);
                //FirebaseCrashlytics.getInstance().log("AcceptThread: Setting up Server using: " + SurfaceDiagramActivity.MY_UUID_INSECURE);
            } catch (IOException e) {
                Log.e(SurfaceDiagramActivity.TAG, "AcceptThread: IOException: " + e.getMessage());
            }
            this.mmServerSocket = bluetoothServerSocket;
           // Log.e(SurfaceDiagramActivity.TAG, "bluetoothServerSocket: " + this.mmServerSocket);
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Log.d(SurfaceDiagramActivity.TAG, "run: AcceptThread Running.");
            BluetoothSocket bluetoothSocket = null;
            try {
                Log.d(SurfaceDiagramActivity.TAG, "run: RFCOM server socket start.....");
                bluetoothSocket = this.mmServerSocket.accept();
                Log.d(SurfaceDiagramActivity.TAG, "run: RFCOM server socket accepted connection.");
            } catch (IOException e) {
                Log.e(SurfaceDiagramActivity.TAG, "AcceptThread: IOException: " + e.getMessage());
            }
            if (bluetoothSocket != null) {
                SurfaceDiagramActivity surfaceDiagramActivity = SurfaceDiagramActivity.this;
                surfaceDiagramActivity.connected(bluetoothSocket, surfaceDiagramActivity.mmDevice);
            }
            Log.i(SurfaceDiagramActivity.TAG, "END mAcceptThread ");
        }

        public void cancel() {
            Log.d(SurfaceDiagramActivity.TAG, "cancel: Canceling AcceptThread.");
            try {
                this.mmServerSocket.close();
            } catch (IOException e) {
                Log.e(SurfaceDiagramActivity.TAG, "cancel: Close of AcceptThread ServerSocket failed. " + e.getMessage());
            }
        }
    }


    public class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice bluetoothDevice, UUID uuid) {
            Log.d(SurfaceDiagramActivity.TAG, "ConnectThread: started.");
            SurfaceDiagramActivity.this.mmDevice = bluetoothDevice;
            SurfaceDiagramActivity.this.deviceUUID = uuid;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            BluetoothSocket bluetoothSocket;
            Log.i(SurfaceDiagramActivity.TAG, "RUN mConnectThread ");

            try {
                Log.d(SurfaceDiagramActivity.TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: " + SurfaceDiagramActivity.MY_UUID_INSECURE);

                if ((ActivityCompat.checkSelfPermission(SurfaceDiagramActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) ||(ActivityCompat.checkSelfPermission(SurfaceDiagramActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)||(ActivityCompat.checkSelfPermission(SurfaceDiagramActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)||(ActivityCompat.checkSelfPermission(SurfaceDiagramActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)){

                    SurfaceDiagramActivity.this.requestPermissions(new String[]{"android.permission.BLUETOOTH_CONNECT", "android.permission.BLUETOOTH_SCAN", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, 200);

                    //return;
                }
                Log.d("BluetoothDevice", ""+SurfaceDiagramActivity.this.mmDevice);
                bluetoothSocket = SurfaceDiagramActivity.this.mmDevice.createRfcommSocketToServiceRecord(SurfaceDiagramActivity.this.deviceUUID);
            } catch (IOException e) {
                Log.e(SurfaceDiagramActivity.TAG, "ConnectThread: Could not create InsecureRfcommSocket " + e.getMessage());
                bluetoothSocket = null;
            }
            this.mmSocket = bluetoothSocket;
            SurfaceDiagramActivity.this.mBluetoothAdapter.cancelDiscovery();
            try {
                this.mmSocket.connect();
                Log.d(SurfaceDiagramActivity.TAG, "run: ConnectThread connected.");
            } catch (IOException e2) {
                Log.e(SurfaceDiagramActivity.TAG, "mConnectThread: run: Unable to close connection in socket " + e2.getMessage());
                Log.d(SurfaceDiagramActivity.TAG, "run: ConnectThread: Could not connect to UUID: " + SurfaceDiagramActivity.MY_UUID_INSECURE);
                SurfaceDiagramActivity surfaceDiagramActivity = SurfaceDiagramActivity.this;
                surfaceDiagramActivity.connected(this.mmSocket, surfaceDiagramActivity.mmDevice);
            }
            SurfaceDiagramActivity surfaceDiagramActivity22 = SurfaceDiagramActivity.this;
            surfaceDiagramActivity22.connected(this.mmSocket, surfaceDiagramActivity22.mmDevice);
        }

        public void cancel() {
            try {
                Log.d(SurfaceDiagramActivity.TAG, "cancel: Closing Client Socket.");
                if(this.mmSocket!=null){
                this.mmSocket.close();
                }
            } catch (IOException e) {
                Log.e(SurfaceDiagramActivity.TAG, "cancel: close() of mmSocket in Connectthread failed. " + e.getMessage());
            }
        }
    }

    public synchronized void start() {
        this.ThreadRunning = true;
        Log.d(TAG, "start");
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mInsecureAcceptThread == null) {
            AcceptThread acceptThread = new AcceptThread();
            this.mInsecureAcceptThread = acceptThread;
            acceptThread.start();
            Log.d("Inside the if","Inside the if");
        }
    }

    public void startClient(BluetoothDevice bluetoothDevice, UUID uuid) {
        Log.d(TAG, "startClient: Started.");
        //this.mProgressDialog = ProgressDialog.show(this.mContext, "Connecting Bluetooth", "Please Wait...", true);
        mProgressDialog = new MyProgressDialog(this.mContext);
        mProgressDialog.show();
        ConnectThread connectThread = new ConnectThread(bluetoothDevice, uuid);
        this.mConnectThread = connectThread;
        connectThread.start();
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        if (this.doubleBackToExitPressedOnce) {
            super.onBackPressed();
            this.finalData.clear();
            this.data.clear();
            this.colors.clear();
            this.twoDdata.clear();
            this.BottomBalance.clear();
            this.TopBalance.clear();
            this.LeftBalance.clear();
            this.RightBalance.clear();
            String bluetooth = SharedPref.getPreferences(SurfaceDiagramActivity.this).getStringData("Bluetooth","none");
            if (Intrinsics.areEqual(bluetooth, "on")) {
                try {
                    this.mConnectThread.cancel();
                    this.mConnectedThread.cancel();
                } catch (NullPointerException unused) {
                }
            }else {
                if (serialPort != null) {
                    try {

                        this.serialPort.close();

                    } catch (NullPointerException unused) {
                    }
                }
            }
            finish();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() { // from class: com.pourmami.hc05.Activities.diagrams.SurfaceDiagramActivity.11
            @Override // java.lang.Runnable
            public void run() {
                SurfaceDiagramActivity.this.doubleBackToExitPressedOnce = false;
            }
        }, 2000L);
    }


    public class ConnectedThread extends Thread {
        String incomingMessage;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private final BluetoothSocket mmSocket;

        public ConnectedThread(BluetoothSocket bluetoothSocket) {
            InputStream inputStream;
            Log.d(SurfaceDiagramActivity.TAG, "ConnectedThread: Starting.");
            this.mmSocket = bluetoothSocket;
           try {
               if(SurfaceDiagramActivity.this.mProgressDialog!=null){
                SurfaceDiagramActivity.this.mProgressDialog.dismiss();
               }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            OutputStream outputStream = null;
            try {
                inputStream = this.mmSocket.getInputStream();
                try {
                    outputStream = this.mmSocket.getOutputStream();
                } catch (IOException e2) {

                    e2.printStackTrace();

                }
            } catch (IOException e3) {
                e3.printStackTrace();
                inputStream = null;
            }
            this.mmInStream = inputStream;
            this.mmOutStream = outputStream;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            char[] charArray;
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            Integer val_uT;
            byte[] bArr = new byte[1024];
            double currentTimeMillis = System.currentTimeMillis();
            while (true) {
                try {
                    String str = new String(bArr, 0, this.mmInStream.read(bArr));
                    this.incomingMessage = str;
                    if(!str.equals("")) {
                        for (char c : str.toCharArray()) {
                            arrayList2.add(Character.valueOf(c));
                            if (c == '\r') {
                                StringBuilder sb = new StringBuilder();
                                Iterator it = arrayList2.iterator();
                                while (it.hasNext()) {
                                    sb.append(((Character) it.next()).charValue());
                                }
                                try {
                                    val_uT = (int) ((Integer.valueOf(Integer.parseInt(String.valueOf(sb).trim()))) * sensor_factor);
                                    arrayList.add(val_uT);
                                } catch (NumberFormatException unused) {
                                }
                                Log.d("result", sb.toString());
                                arrayList2.clear();
                            }
                        }
                        double currentTimeMillis2 = System.currentTimeMillis();
                        Double.isNaN(currentTimeMillis2);
                        if (currentTimeMillis2 - currentTimeMillis >= SurfaceDiagramActivity.this.SleepTime) {
                            currentTimeMillis = System.currentTimeMillis();
                            SurfaceDiagramActivity.this.getAverage(arrayList);
                            arrayList.clear();
                        }
                    }else{
                        Alerts.show_alert(SurfaceDiagramActivity.this,"Error!","Connected to "+ sp.getStringData("Devicename","None")+" but there is no Data");

                    }
                } catch (IOException e) {
                    Log.e(SurfaceDiagramActivity.TAG, "write: Error reading Input Stream. " + e.getMessage());
                    String bluetooth = SharedPref.getPreferences(SurfaceDiagramActivity.this).getStringData("Bluetooth","none");
    if((SurfaceDiagramActivity.this.ThreadRunning)&&(SurfaceDiagramActivity.this.PalsPerRow * SurfaceDiagramActivity.this.PalsPerColumn != SurfaceDiagramActivity.this.realNumbers.size())) {
      runOnUiThread(new Runnable() {
        @Override // java.lang.Runnable
        public void run() {

            View view = findViewById(R.id.Controls);
            Snackbar snackbar = Snackbar.make(view, "Please Check Your Connection And Press Start", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Refresh", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //finish();
                    //startActivity(getIntent());
                    Log.d(SurfaceDiagramActivity.TAG, "Pause Running");
                    SurfaceDiagramActivity.this.is_running = false;
                    SurfaceDiagramActivity.this.ThreadRunning = false;
                    SurfaceDiagramActivity.this.btn_StartAndStop.setText("Start");

                    if (Intrinsics.areEqual(bluetooth, "on")) {
                        if (mConnectedThread != null) {
                            SurfaceDiagramActivity.this.mConnectedThread.cancel();
                            Log.d(SurfaceDiagramActivity.TAG, "mConnectedThread Canceled");
                        }
                    } else {
                        if (serialPort != null) {
                            SurfaceDiagramActivity.this.serialPort.close();
                            Log.d("USB", "Closing Serial Port");
                        }
                    }
                }
            });
            snackbar.setBackgroundTint(R.drawable.startup_screen_card_background);
            snackbar.show();
        }
    });
}
                    return;
                }
            }
        }

        public void write(byte[] bArr) {
            String str = new String(bArr, Charset.defaultCharset());
            Log.d(SurfaceDiagramActivity.TAG, "write: Writing to outputstream: " + str);
            try {
                this.mmOutStream.write(bArr);
            } catch (IOException e) {
                Log.e(SurfaceDiagramActivity.TAG, "write: Error writing to output stream. " + e.getMessage());
            }
        }

        public void cancel() {
            try {
                this.mmSocket.close();
            } catch (IOException unused) {
            }
        }
    }


    public void connected(BluetoothSocket bluetoothSocket, BluetoothDevice bluetoothDevice) {
        Log.d(TAG, "connected: Starting.");
        ConnectedThread connectedThread = new ConnectedThread(bluetoothSocket);
        this.mConnectedThread = connectedThread;
        connectedThread.start();
    }


    public void getAverage(ArrayList<Integer> arrayList) {
        float f;
        Iterator<Integer> it = arrayList.iterator();
         int i = 0;
        while (it.hasNext()) {
            int intValue = it.next().intValue();
            int i2 = 0;
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                if (arrayList.get(i3).intValue() == intValue) {
                    i2++;
                }
            }
            if (i2 > 0) {
                i = intValue;
            }
        }
        this.realNumbers.add(i);
        Log.d(TAG, "result :" + i);
        float f2 = (float) i;
        this.toneGen1.startTone(12, 100);
        float f3 = this.x;
        while (f3 < this.x + this.zyIncrease) {
            float f4 = this.z;

            while (true) {
                float f5 = this.z;
                f = this.zyIncrease;
                if (f4 < f5 + f) {
                    float[] fArr = {f3, f2, f4, (f / 2.0f) + f3, f2, f4, f3, f2, f4, f3 + (f / 2.0f), f2, f4, f3 + (f / 2.0f), f2, f4 - (f / 2.0f), f3, f2, f4 - (f / 2.0f), f3 + (f / 2.0f), f2, f4 - (f / 2.0f), f3, f2, f4 - (f / 2.0f)};
                    float[] fArr2 = {fArr[6], fArr[7], fArr[8], fArr[9], fArr[10], fArr[11], fArr[21], fArr[22], fArr[23], fArr[18], fArr[19], fArr[20], fArr[12], fArr[13], fArr[14], fArr[15], fArr[16], fArr[17], fArr[18], fArr[19], fArr[20], fArr[21], fArr[22], fArr[23], fArr[15], fArr[16], fArr[17], fArr[0], fArr[1], fArr[2], fArr[21], fArr[22], fArr[23], fArr[6], fArr[7], fArr[8], fArr[3], fArr[4], fArr[5], fArr[12], fArr[13], fArr[14], fArr[9], fArr[10], fArr[11], fArr[18], fArr[19], fArr[20], fArr[0], fArr[1], fArr[2], fArr[3], fArr[4], fArr[5], fArr[6], fArr[7], fArr[8], fArr[9], fArr[10], fArr[11], fArr[15], fArr[16], fArr[17], fArr[12], fArr[13], fArr[14], fArr[0], fArr[1], fArr[2], fArr[3], fArr[4], fArr[5]};
                    float[] fArr3 = {fArr[6], 0.0f, fArr[8], fArr[9], 0.0f, fArr[11], fArr[21], 0.0f, fArr[23], fArr[18], 0.0f, fArr[20], fArr[12], 0.0f, fArr[14], fArr[15], 0.0f, fArr[17], fArr[18], 0.0f, fArr[20], fArr[21], 0.0f, fArr[23], fArr[15], 0.0f, fArr[17], fArr[0], 0.0f, fArr[2], fArr[21], 0.0f, fArr[23], fArr[6], 0.0f, fArr[8], fArr[3], 0.0f, fArr[5], fArr[12], 0.0f, fArr[14], fArr[9], 0.0f, fArr[11], fArr[18], 0.0f, fArr[20], fArr[0], 0.0f, fArr[2], fArr[3], 0.0f, fArr[5], fArr[6], 0.0f, fArr[8], fArr[9], 0.0f, fArr[11], fArr[15], 0.0f, fArr[17], fArr[12], 0.0f, fArr[14], fArr[0], 0.0f, fArr[2], fArr[3], 0.0f, fArr[5]};
                    this.data.add(fArr2);
                    this.twoDdata.add(fArr3);

                }else{
                    break;
                }
                f4 += this.zyIncrease / 2.0f;
            }
           f3 += f / 2.0f;
        }
        balancing();
        coloring();
        this.finalData.clear();
        for (int i4 = 0; i4 < this.data.size(); i4++) {
            this.finalData.add(new SurfaceShape(this.twoDdata.get(i4), this.colors.get(i4)));
            //this.finaltwoDdata.add(new SurfaceShape(this.twoDdata.get(i4), this.colors.get(i4)));
        }
        if (this.palsOrientation == 1) {
            if (this.PalsPerColumn % 2 == 0) {
                int i5 = this.zigzagRound;
                if (i5 % 2 == 0) {
                    float f6 = this.x;
                    float f7 = this.MaxX;
                    float f8 = this.zyIncrease;
                    if (f6 == f7 - f8) {
                        this.z += f8;
                        this.zigzagRound = i5 + 1;
                    } else if (f6 < f7) {
                        this.x = f6 + f8;
                    }
                } else {
                    float f9 = this.x;
                    float f10 = this.MinX;
                    if (f9 == f10) {
                        this.z += this.zyIncrease;
                        this.zigzagRound = i5 + 1;
                    } else if (f9 > f10) {
                        this.x = f9 - this.zyIncrease;
                    }
                }
            } else {
                int i6 = this.zigzagRound;
                if (i6 % 2 == 0 && this.x == this.MaxX) {
                    this.z += this.zyIncrease;
                    this.zigzagRound = i6 + 1;
                } else {
                    if (this.zigzagRound % 2 == 0) {
                        float f11 = this.x;
                        if (f11 < this.MaxX) {
                            this.x = f11 + this.zyIncrease;
                        }
                    }
                    int i7 = this.zigzagRound;
                    if (i7 % 2 != 0 && this.x == this.MinX) {
                        this.z += this.zyIncrease;
                        this.zigzagRound = i7 + 1;
                    } else if (this.zigzagRound % 2 != 0) {
                        float f12 = this.x;
                        if (f12 > this.MinX) {
                            this.x = f12 - this.zyIncrease;
                        }
                    }
                }
            }
        } else {
            float f13 = this.x;
            float f14 = this.zyIncrease;
            float f15 = f13 + f14;
            this.x = f15;
            if (this.PalsPerColumn % 2 == 0) {
                if (f15 == this.MaxX) {
                    this.z += f14;
                    this.x = this.MinX;
                }
            } else if (f15 > this.MaxX) {
                this.z += f14;
                this.x = this.MinX;
            }
        }
        int finalI = i;
        runOnUiThread(new Runnable() {
            @Override // java.lang.Runnable
            public void run() {
                SurfaceDiagramActivity.this.OutPut.setText(String.valueOf(finalI)+" uT");
                if (SurfaceDiagramActivity.this.PalsPerRow * SurfaceDiagramActivity.this.PalsPerColumn == SurfaceDiagramActivity.this.realNumbers.size()) {
                    SurfaceDiagramActivity.this.allow_to_finish();
                }
            }
        });
    }

    private void balancing() {
        if (this.palsOrientation == 1) {
            zigzagBalancing();
        } else {
            fromFirstBalancing();
        }
        coloring();
    }

    private void coloring() {
        this.colors.clear();
        for (int i = 0; i < this.data.size() - 1; i++) {
            if (this.minColor == -1 && this.maxColor == -1) {
                this.minColor = calRealNumber(this.data.get(i)[4]);
                this.maxColor = calRealNumber(this.data.get(i)[4]);
            }
            if (this.minColor > calRealNumber(this.data.get(i)[1])) {
                this.minColor = calRealNumber(this.data.get(i)[1]);
            }
            if (this.minColor > calRealNumber(this.data.get(i)[4])) {
                this.minColor = calRealNumber(this.data.get(i)[4]);
            }
            if (this.minColor > calRealNumber(this.data.get(i)[7])) {
                this.minColor = calRealNumber(this.data.get(i)[7]);
            }
            if (this.minColor > calRealNumber(this.data.get(i)[10])) {
                this.minColor = calRealNumber(this.data.get(i)[10]);
            }
            if (this.maxColor < calRealNumber(this.data.get(i)[1])) {
                this.maxColor = calRealNumber(this.data.get(i)[1]);
            }
            if (this.maxColor < calRealNumber(this.data.get(i)[4])) {
                this.maxColor = calRealNumber(this.data.get(i)[4]);
            }
            if (this.maxColor < calRealNumber(this.data.get(i)[7])) {
                this.maxColor = calRealNumber(this.data.get(i)[7]);
            }
            if (this.maxColor < calRealNumber(this.data.get(i)[10])) {
                this.maxColor = calRealNumber(this.data.get(i)[10]);
            }
        }
        float f = (this.maxColor - this.minColor) / 10.0f;
        for (int i2 = 0; i2 < this.data.size(); i2++) {
            float[] fArr = new float[96];
            for (int i3 = 16; i3 <= 79; i3++) {
                fArr[i3] = 0.0f;
            }
            if (calRealNumber(this.data.get(i2)[1]) == this.minColor) {
                fArr[0] = 0.0f;
                fArr[1] = 0.0f;
                fArr[2] = 1.0f;
                fArr[3] = 1.0f;
                fArr[88] = 0.0f;
                fArr[89] = 0.0f;
                fArr[90] = 1.0f;
                fArr[91] = 1.0f;
            } else if (calRealNumber(this.data.get(i2)[1]) > this.minColor && calRealNumber(this.data.get(i2)[1]) < this.minColor + f) {
                fArr[0] = 0.0f;
                fArr[1] = 0.1f;
                fArr[2] = 0.9f;
                fArr[3] = 1.0f;
                fArr[88] = 0.0f;
                fArr[89] = 0.1f;
                fArr[90] = 0.9f;
                fArr[91] = 1.0f;
            } else if (calRealNumber(this.data.get(i2)[1]) == this.minColor + f) {
                fArr[0] = 0.0f;
                fArr[1] = 0.2f;
                fArr[2] = 0.8f;
                fArr[3] = 1.0f;
                fArr[88] = 0.0f;
                fArr[89] = 0.2f;
                fArr[90] = 0.8f;
                fArr[91] = 1.0f;
            } else if (calRealNumber(this.data.get(i2)[1]) <= this.minColor + f || calRealNumber(this.data.get(i2)[1]) >= this.minColor + (2.0f * f)) {
                float f2 = 2.0f * f;
                if (calRealNumber(this.data.get(i2)[1]) == this.minColor + f2) {
                    fArr[0] = 0.0f;
                    fArr[1] = 0.4f;
                    fArr[2] = 0.6f;
                    fArr[3] = 1.0f;
                    fArr[88] = 0.0f;
                    fArr[89] = 0.4f;
                    fArr[90] = 0.6f;
                    fArr[91] = 1.0f;
                } else if (calRealNumber(this.data.get(i2)[1]) <= this.minColor + f2 || calRealNumber(this.data.get(i2)[1]) >= this.minColor + (3.0f * f)) {
                    float f3 = 3.0f * f;
                    if (calRealNumber(this.data.get(i2)[1]) == this.minColor + f3) {
                        fArr[0] = 0.0f;
                        fArr[1] = 0.6f;
                        fArr[2] = 0.4f;
                        fArr[3] = 1.0f;
                        fArr[88] = 0.0f;
                        fArr[89] = 0.6f;
                        fArr[90] = 0.4f;
                        fArr[91] = 1.0f;
                    } else if (calRealNumber(this.data.get(i2)[1]) <= this.minColor + f3 || calRealNumber(this.data.get(i2)[1]) >= this.minColor + (4.0f * f)) {
                        float f4 = 4.0f * f;
                        if (calRealNumber(this.data.get(i2)[1]) == this.minColor + f4) {
                            fArr[0] = 0.0f;
                            fArr[1] = 0.8f;
                            fArr[2] = 0.2f;
                            fArr[3] = 1.0f;
                            fArr[88] = 0.0f;
                            fArr[89] = 0.8f;
                            fArr[90] = 0.2f;
                            fArr[91] = 1.0f;
                        } else if (calRealNumber(this.data.get(i2)[1]) <= this.minColor + f4 || calRealNumber(this.data.get(i2)[1]) >= this.minColor + (5.0f * f)) {
                            float f5 = 5.0f * f;
                            if (calRealNumber(this.data.get(i2)[1]) == this.minColor + f5) {
                                fArr[0] = 0.0f;
                                fArr[1] = 1.0f;
                                fArr[2] = 0.0f;
                                fArr[3] = 1.0f;
                                fArr[88] = 0.0f;
                                fArr[89] = 1.0f;
                                fArr[90] = 0.0f;
                                fArr[91] = 1.0f;
                            } else if (calRealNumber(this.data.get(i2)[1]) <= this.minColor + f5 || calRealNumber(this.data.get(i2)[1]) >= this.minColor + (6.0f * f)) {
                                float f6 = 6.0f * f;
                                if (calRealNumber(this.data.get(i2)[1]) == this.minColor + f6) {
                                    fArr[0] = 0.2f;
                                    fArr[1] = 0.8f;
                                    fArr[2] = 0.0f;
                                    fArr[3] = 1.0f;
                                    fArr[88] = 0.2f;
                                    fArr[89] = 0.8f;
                                    fArr[90] = 0.0f;
                                    fArr[91] = 1.0f;
                                } else if (calRealNumber(this.data.get(i2)[1]) <= this.minColor + f6 || calRealNumber(this.data.get(i2)[1]) >= this.minColor + (7.0f * f)) {
                                    float f7 = 7.0f * f;
                                    if (calRealNumber(this.data.get(i2)[1]) == this.minColor + f7) {
                                        fArr[0] = 0.4f;
                                        fArr[1] = 0.6f;
                                        fArr[2] = 0.0f;
                                        fArr[3] = 1.0f;
                                        fArr[88] = 0.4f;
                                        fArr[89] = 0.6f;
                                        fArr[90] = 0.0f;
                                        fArr[91] = 1.0f;
                                    } else if (calRealNumber(this.data.get(i2)[1]) <= this.minColor + f7 || calRealNumber(this.data.get(i2)[1]) >= this.minColor + (8.0f * f)) {
                                        float f8 = 8.0f * f;
                                        if (calRealNumber(this.data.get(i2)[1]) == this.minColor + f8) {
                                            fArr[0] = 0.6f;
                                            fArr[1] = 0.4f;
                                            fArr[2] = 0.0f;
                                            fArr[3] = 1.0f;
                                            fArr[88] = 0.6f;
                                            fArr[89] = 0.4f;
                                            fArr[90] = 0.0f;
                                            fArr[91] = 1.0f;
                                        } else if (calRealNumber(this.data.get(i2)[1]) <= this.minColor + f8 || calRealNumber(this.data.get(i2)[1]) >= this.minColor + (9.0f * f)) {
                                            float f9 = 9.0f * f;
                                            if (calRealNumber(this.data.get(i2)[1]) == this.minColor + f9) {
                                                fArr[0] = 0.8f;
                                                fArr[1] = 0.2f;
                                                fArr[2] = 0.0f;
                                                fArr[3] = 1.0f;
                                                fArr[88] = 0.8f;
                                                fArr[89] = 0.2f;
                                                fArr[90] = 0.0f;
                                                fArr[91] = 1.0f;
                                            } else if (calRealNumber(this.data.get(i2)[1]) > this.minColor + f9 && calRealNumber(this.data.get(i2)[1]) < this.maxColor) {
                                                fArr[0] = 0.9f;
                                                fArr[1] = 0.1f;
                                                fArr[2] = 0.0f;
                                                fArr[3] = 1.0f;
                                                fArr[88] = 0.9f;
                                                fArr[89] = 0.1f;
                                                fArr[90] = 0.0f;
                                                fArr[91] = 1.0f;
                                            } else if (calRealNumber(this.data.get(i2)[1]) == this.maxColor) {
                                                fArr[0] = 1.0f;
                                                fArr[1] = 0.0f;
                                                fArr[2] = 0.0f;
                                                fArr[3] = 1.0f;
                                                fArr[88] = 1.0f;
                                                fArr[89] = 0.0f;
                                                fArr[90] = 0.0f;
                                                fArr[91] = 1.0f;
                                            }
                                        } else {
                                            fArr[0] = 0.7f;
                                            fArr[1] = 0.3f;
                                            fArr[2] = 0.0f;
                                            fArr[3] = 1.0f;
                                            fArr[88] = 0.7f;
                                            fArr[89] = 0.3f;
                                            fArr[90] = 0.0f;
                                            fArr[91] = 1.0f;
                                        }
                                    } else {
                                        fArr[0] = 0.5f;
                                        fArr[1] = 0.5f;
                                        fArr[2] = 0.0f;
                                        fArr[3] = 1.0f;
                                        fArr[88] = 0.5f;
                                        fArr[89] = 0.5f;
                                        fArr[90] = 0.0f;
                                        fArr[91] = 1.0f;
                                    }
                                } else {
                                    fArr[0] = 0.3f;
                                    fArr[1] = 0.7f;
                                    fArr[2] = 0.0f;
                                    fArr[3] = 1.0f;
                                    fArr[88] = 0.3f;
                                    fArr[89] = 0.7f;
                                    fArr[90] = 0.0f;
                                    fArr[91] = 1.0f;
                                }
                            } else {
                                fArr[0] = 0.1f;
                                fArr[1] = 0.9f;
                                fArr[2] = 0.0f;
                                fArr[3] = 1.0f;
                                fArr[88] = 0.1f;
                                fArr[89] = 0.9f;
                                fArr[90] = 0.0f;
                                fArr[91] = 1.0f;
                            }
                        } else {
                            fArr[0] = 0.0f;
                            fArr[1] = 0.9f;
                            fArr[2] = 0.1f;
                            fArr[3] = 1.0f;
                            fArr[88] = 0.0f;
                            fArr[89] = 0.9f;
                            fArr[90] = 0.1f;
                            fArr[91] = 1.0f;
                        }
                    } else {
                        fArr[0] = 0.0f;
                        fArr[1] = 0.7f;
                        fArr[2] = 0.3f;
                        fArr[3] = 1.0f;
                        fArr[88] = 0.0f;
                        fArr[89] = 0.7f;
                        fArr[90] = 0.3f;
                        fArr[91] = 1.0f;
                    }
                } else {
                    fArr[0] = 0.0f;
                    fArr[1] = 0.5f;
                    fArr[2] = 0.5f;
                    fArr[3] = 1.0f;
                    fArr[88] = 0.0f;
                    fArr[89] = 0.5f;
                    fArr[90] = 0.5f;
                    fArr[91] = 1.0f;
                }
            } else {
                fArr[0] = 0.0f;
                fArr[1] = 0.3f;
                fArr[2] = 0.7f;
                fArr[3] = 1.0f;
                fArr[88] = 0.0f;
                fArr[89] = 0.3f;
                fArr[90] = 0.7f;
                fArr[91] = 1.0f;
            }
            if (calRealNumber(this.data.get(i2)[4]) == this.minColor) {
                fArr[4] = 0.0f;
                fArr[5] = 0.0f;
                fArr[6] = 1.0f;
                fArr[7] = 1.0f;
                fArr[92] = 0.0f;
                fArr[93] = 0.0f;
                fArr[94] = 1.0f;
                fArr[95] = 1.0f;
            } else if (calRealNumber(this.data.get(i2)[4]) > this.minColor && calRealNumber(this.data.get(i2)[4]) < this.minColor + f) {
                fArr[4] = 0.0f;
                fArr[5] = 0.1f;
                fArr[6] = 0.9f;
                fArr[7] = 1.0f;
                fArr[92] = 0.0f;
                fArr[93] = 0.1f;
                fArr[94] = 0.9f;
                fArr[95] = 1.0f;
            } else if (calRealNumber(this.data.get(i2)[4]) == this.minColor + f) {
                fArr[4] = 0.0f;
                fArr[5] = 0.2f;
                fArr[6] = 0.8f;
                fArr[7] = 1.0f;
                fArr[92] = 0.0f;
                fArr[93] = 0.2f;
                fArr[94] = 0.8f;
                fArr[95] = 1.0f;
            } else if (calRealNumber(this.data.get(i2)[4]) <= this.minColor + f || calRealNumber(this.data.get(i2)[4]) >= this.minColor + (2.0f * f)) {
                float f10 = 2.0f * f;
                if (calRealNumber(this.data.get(i2)[4]) == this.minColor + f10) {
                    fArr[4] = 0.0f;
                    fArr[5] = 0.4f;
                    fArr[6] = 0.6f;
                    fArr[7] = 1.0f;
                    fArr[92] = 0.0f;
                    fArr[93] = 0.4f;
                    fArr[94] = 0.6f;
                    fArr[95] = 1.0f;
                } else if (calRealNumber(this.data.get(i2)[4]) <= this.minColor + f10 || calRealNumber(this.data.get(i2)[4]) >= this.minColor + (3.0f * f)) {
                    float f11 = 3.0f * f;
                    if (calRealNumber(this.data.get(i2)[4]) == this.minColor + f11) {
                        fArr[4] = 0.0f;
                        fArr[5] = 0.6f;
                        fArr[6] = 0.4f;
                        fArr[7] = 1.0f;
                        fArr[92] = 0.0f;
                        fArr[93] = 0.6f;
                        fArr[94] = 0.4f;
                        fArr[95] = 1.0f;
                    } else if (calRealNumber(this.data.get(i2)[4]) <= this.minColor + f11 || calRealNumber(this.data.get(i2)[4]) >= this.minColor + (4.0f * f)) {
                        float f12 = 4.0f * f;
                        if (calRealNumber(this.data.get(i2)[4]) == this.minColor + f12) {
                            fArr[4] = 0.0f;
                            fArr[5] = 0.8f;
                            fArr[6] = 0.2f;
                            fArr[7] = 1.0f;
                            fArr[92] = 0.0f;
                            fArr[93] = 0.8f;
                            fArr[94] = 0.2f;
                            fArr[95] = 1.0f;
                        } else if (calRealNumber(this.data.get(i2)[4]) <= this.minColor + f12 || calRealNumber(this.data.get(i2)[4]) >= this.minColor + (5.0f * f)) {
                            float f13 = 5.0f * f;
                            if (calRealNumber(this.data.get(i2)[4]) == this.minColor + f13) {
                                fArr[4] = 0.0f;
                                fArr[5] = 1.0f;
                                fArr[6] = 0.0f;
                                fArr[7] = 1.0f;
                                fArr[92] = 0.0f;
                                fArr[93] = 1.0f;
                                fArr[94] = 0.0f;
                                fArr[95] = 1.0f;
                            } else if (calRealNumber(this.data.get(i2)[4]) <= this.minColor + f13 || calRealNumber(this.data.get(i2)[4]) >= this.minColor + (6.0f * f)) {
                                float f14 = 6.0f * f;
                                if (calRealNumber(this.data.get(i2)[4]) == this.minColor + f14) {
                                    fArr[4] = 0.2f;
                                    fArr[5] = 0.8f;
                                    fArr[6] = 0.0f;
                                    fArr[7] = 1.0f;
                                    fArr[92] = 0.2f;
                                    fArr[93] = 0.8f;
                                    fArr[94] = 0.0f;
                                    fArr[95] = 1.0f;
                                } else if (calRealNumber(this.data.get(i2)[4]) <= this.minColor + f14 || calRealNumber(this.data.get(i2)[4]) >= this.minColor + (7.0f * f)) {
                                    float f15 = 7.0f * f;
                                    if (calRealNumber(this.data.get(i2)[4]) == this.minColor + f15) {
                                        fArr[4] = 0.4f;
                                        fArr[5] = 0.6f;
                                        fArr[6] = 0.0f;
                                        fArr[7] = 1.0f;
                                        fArr[92] = 0.4f;
                                        fArr[93] = 0.6f;
                                        fArr[94] = 0.0f;
                                        fArr[95] = 1.0f;
                                    } else if (calRealNumber(this.data.get(i2)[4]) <= this.minColor + f15 || calRealNumber(this.data.get(i2)[4]) >= this.minColor + (8.0f * f)) {
                                        float f16 = 8.0f * f;
                                        if (calRealNumber(this.data.get(i2)[4]) == this.minColor + f16) {
                                            fArr[4] = 0.6f;
                                            fArr[5] = 0.4f;
                                            fArr[6] = 0.0f;
                                            fArr[7] = 1.0f;
                                            fArr[92] = 0.6f;
                                            fArr[93] = 0.4f;
                                            fArr[94] = 0.0f;
                                            fArr[95] = 1.0f;
                                        } else if (calRealNumber(this.data.get(i2)[4]) <= this.minColor + f16 || calRealNumber(this.data.get(i2)[4]) >= this.minColor + (9.0f * f)) {
                                            float f17 = 9.0f * f;
                                            if (calRealNumber(this.data.get(i2)[4]) == this.minColor + f17) {
                                                fArr[4] = 0.8f;
                                                fArr[5] = 0.2f;
                                                fArr[6] = 0.0f;
                                                fArr[7] = 1.0f;
                                                fArr[92] = 0.8f;
                                                fArr[93] = 0.2f;
                                                fArr[94] = 0.0f;
                                                fArr[95] = 1.0f;
                                            } else if (calRealNumber(this.data.get(i2)[4]) > this.minColor + f17 && calRealNumber(this.data.get(i2)[4]) < this.maxColor) {
                                                fArr[4] = 0.9f;
                                                fArr[5] = 0.1f;
                                                fArr[6] = 0.0f;
                                                fArr[7] = 1.0f;
                                                fArr[92] = 0.9f;
                                                fArr[93] = 0.1f;
                                                fArr[94] = 0.0f;
                                                fArr[95] = 1.0f;
                                            } else if (calRealNumber(this.data.get(i2)[4]) == this.maxColor) {
                                                fArr[4] = 1.0f;
                                                fArr[5] = 0.0f;
                                                fArr[6] = 0.0f;
                                                fArr[7] = 1.0f;
                                                fArr[92] = 1.0f;
                                                fArr[93] = 0.0f;
                                                fArr[94] = 0.0f;
                                                fArr[95] = 1.0f;
                                            }
                                        } else {
                                            fArr[4] = 0.7f;
                                            fArr[5] = 0.3f;
                                            fArr[6] = 0.0f;
                                            fArr[7] = 1.0f;
                                            fArr[92] = 0.7f;
                                            fArr[93] = 0.3f;
                                            fArr[94] = 0.0f;
                                            fArr[95] = 1.0f;
                                        }
                                    } else {
                                        fArr[4] = 0.5f;
                                        fArr[5] = 0.5f;
                                        fArr[6] = 0.0f;
                                        fArr[7] = 1.0f;
                                        fArr[92] = 0.5f;
                                        fArr[93] = 0.5f;
                                        fArr[94] = 0.0f;
                                        fArr[95] = 1.0f;
                                    }
                                } else {
                                    fArr[4] = 0.3f;
                                    fArr[5] = 0.7f;
                                    fArr[6] = 0.0f;
                                    fArr[7] = 1.0f;
                                    fArr[92] = 0.3f;
                                    fArr[93] = 0.7f;
                                    fArr[94] = 0.0f;
                                    fArr[95] = 1.0f;
                                }
                            } else {
                                fArr[4] = 0.1f;
                                fArr[5] = 0.9f;
                                fArr[6] = 0.0f;
                                fArr[7] = 1.0f;
                                fArr[92] = 0.1f;
                                fArr[93] = 0.9f;
                                fArr[94] = 0.0f;
                                fArr[95] = 1.0f;
                            }
                        } else {
                            fArr[4] = 0.0f;
                            fArr[5] = 0.9f;
                            fArr[6] = 0.1f;
                            fArr[7] = 1.0f;
                            fArr[92] = 0.0f;
                            fArr[93] = 0.9f;
                            fArr[94] = 0.1f;
                            fArr[95] = 1.0f;
                        }
                    } else {
                        fArr[4] = 0.0f;
                        fArr[5] = 0.7f;
                        fArr[6] = 0.3f;
                        fArr[7] = 1.0f;
                        fArr[92] = 0.0f;
                        fArr[93] = 0.7f;
                        fArr[94] = 0.3f;
                        fArr[95] = 1.0f;
                    }
                } else {
                    fArr[4] = 0.0f;
                    fArr[5] = 0.5f;
                    fArr[6] = 0.5f;
                    fArr[7] = 1.0f;
                    fArr[92] = 0.0f;
                    fArr[93] = 0.5f;
                    fArr[94] = 0.5f;
                    fArr[95] = 1.0f;
                }
            } else {
                fArr[4] = 0.0f;
                fArr[5] = 0.3f;
                fArr[6] = 0.7f;
                fArr[7] = 1.0f;
                fArr[92] = 0.0f;
                fArr[93] = 0.3f;
                fArr[94] = 0.7f;
                fArr[95] = 1.0f;
            }
            if (calRealNumber(this.data.get(i2)[7]) == this.minColor) {
                fArr[8] = 0.0f;
                fArr[9] = 0.0f;
                fArr[10] = 1.0f;
                fArr[11] = 1.0f;
                fArr[80] = 0.0f;
                fArr[81] = 0.0f;
                fArr[82] = 1.0f;
                fArr[83] = 1.0f;
            } else if (calRealNumber(this.data.get(i2)[7]) > this.minColor && calRealNumber(this.data.get(i2)[7]) < this.minColor + f) {
                fArr[8] = 0.0f;
                fArr[9] = 0.1f;
                fArr[10] = 0.9f;
                fArr[11] = 1.0f;
                fArr[80] = 0.0f;
                fArr[81] = 0.1f;
                fArr[82] = 0.9f;
                fArr[83] = 1.0f;
            } else if (calRealNumber(this.data.get(i2)[7]) == this.minColor + f) {
                fArr[8] = 0.0f;
                fArr[9] = 0.2f;
                fArr[10] = 0.8f;
                fArr[11] = 1.0f;
                fArr[80] = 0.0f;
                fArr[81] = 0.2f;
                fArr[82] = 0.8f;
                fArr[83] = 1.0f;
            } else if (calRealNumber(this.data.get(i2)[7]) <= this.minColor + f || calRealNumber(this.data.get(i2)[7]) >= this.minColor + (2.0f * f)) {
                float f18 = 2.0f * f;
                if (calRealNumber(this.data.get(i2)[7]) == this.minColor + f18) {
                    fArr[8] = 0.0f;
                    fArr[9] = 0.4f;
                    fArr[10] = 0.6f;
                    fArr[11] = 1.0f;
                    fArr[80] = 0.0f;
                    fArr[81] = 0.4f;
                    fArr[82] = 0.6f;
                    fArr[83] = 1.0f;
                } else if (calRealNumber(this.data.get(i2)[7]) <= this.minColor + f18 || calRealNumber(this.data.get(i2)[7]) >= this.minColor + (3.0f * f)) {
                    float f19 = 3.0f * f;
                    if (calRealNumber(this.data.get(i2)[7]) == this.minColor + f19) {
                        fArr[8] = 0.0f;
                        fArr[9] = 0.6f;
                        fArr[10] = 0.4f;
                        fArr[11] = 1.0f;
                        fArr[80] = 0.0f;
                        fArr[81] = 0.6f;
                        fArr[82] = 0.4f;
                        fArr[83] = 1.0f;
                    } else if (calRealNumber(this.data.get(i2)[7]) <= this.minColor + f19 || calRealNumber(this.data.get(i2)[7]) >= this.minColor + (4.0f * f)) {
                        float f20 = 4.0f * f;
                        if (calRealNumber(this.data.get(i2)[7]) == this.minColor + f20) {
                            fArr[8] = 0.0f;
                            fArr[9] = 0.8f;
                            fArr[10] = 0.2f;
                            fArr[11] = 1.0f;
                            fArr[80] = 0.0f;
                            fArr[81] = 0.8f;
                            fArr[82] = 0.2f;
                            fArr[83] = 1.0f;
                        } else if (calRealNumber(this.data.get(i2)[7]) <= this.minColor + f20 || calRealNumber(this.data.get(i2)[7]) >= this.minColor + (5.0f * f)) {
                            float f21 = 5.0f * f;
                            if (calRealNumber(this.data.get(i2)[7]) == this.minColor + f21) {
                                fArr[8] = 0.0f;
                                fArr[9] = 1.0f;
                                fArr[10] = 0.0f;
                                fArr[11] = 1.0f;
                                fArr[80] = 0.0f;
                                fArr[81] = 1.0f;
                                fArr[82] = 0.0f;
                                fArr[83] = 1.0f;
                            } else if (calRealNumber(this.data.get(i2)[7]) <= this.minColor + f21 || calRealNumber(this.data.get(i2)[7]) >= this.minColor + (6.0f * f)) {
                                float f22 = 6.0f * f;
                                if (calRealNumber(this.data.get(i2)[7]) == this.minColor + f22) {
                                    fArr[8] = 0.2f;
                                    fArr[9] = 0.8f;
                                    fArr[10] = 0.0f;
                                    fArr[11] = 1.0f;
                                    fArr[80] = 0.2f;
                                    fArr[81] = 0.8f;
                                    fArr[82] = 0.0f;
                                    fArr[83] = 1.0f;
                                } else if (calRealNumber(this.data.get(i2)[7]) <= this.minColor + f22 || calRealNumber(this.data.get(i2)[7]) >= this.minColor + (7.0f * f)) {
                                    float f23 = 7.0f * f;
                                    if (calRealNumber(this.data.get(i2)[7]) == this.minColor + f23) {
                                        fArr[8] = 0.4f;
                                        fArr[9] = 0.6f;
                                        fArr[10] = 0.0f;
                                        fArr[11] = 1.0f;
                                        fArr[80] = 0.4f;
                                        fArr[81] = 0.6f;
                                        fArr[82] = 0.0f;
                                        fArr[83] = 1.0f;
                                    } else if (calRealNumber(this.data.get(i2)[7]) <= this.minColor + f23 || calRealNumber(this.data.get(i2)[7]) >= this.minColor + (8.0f * f)) {
                                        float f24 = 8.0f * f;
                                        if (calRealNumber(this.data.get(i2)[7]) == this.minColor + f24) {
                                            fArr[8] = 0.6f;
                                            fArr[9] = 0.4f;
                                            fArr[10] = 0.0f;
                                            fArr[11] = 1.0f;
                                            fArr[80] = 0.6f;
                                            fArr[81] = 0.4f;
                                            fArr[82] = 0.0f;
                                            fArr[83] = 1.0f;
                                        } else if (calRealNumber(this.data.get(i2)[7]) <= this.minColor + f24 || calRealNumber(this.data.get(i2)[7]) >= this.minColor + (9.0f * f)) {
                                            float f25 = 9.0f * f;
                                            if (calRealNumber(this.data.get(i2)[7]) == this.minColor + f25) {
                                                fArr[8] = 0.8f;
                                                fArr[9] = 0.2f;
                                                fArr[10] = 0.0f;
                                                fArr[11] = 1.0f;
                                                fArr[80] = 0.8f;
                                                fArr[81] = 0.2f;
                                                fArr[82] = 0.0f;
                                                fArr[83] = 1.0f;
                                            } else if (calRealNumber(this.data.get(i2)[7]) > this.minColor + f25 && calRealNumber(this.data.get(i2)[7]) < this.maxColor) {
                                                fArr[8] = 0.9f;
                                                fArr[9] = 0.1f;
                                                fArr[10] = 0.0f;
                                                fArr[11] = 1.0f;
                                                fArr[80] = 0.9f;
                                                fArr[81] = 0.1f;
                                                fArr[82] = 0.0f;
                                                fArr[83] = 1.0f;
                                            } else if (calRealNumber(this.data.get(i2)[7]) == this.maxColor) {
                                                fArr[8] = 1.0f;
                                                fArr[9] = 0.0f;
                                                fArr[10] = 0.0f;
                                                fArr[11] = 1.0f;
                                                fArr[80] = 1.0f;
                                                fArr[81] = 0.0f;
                                                fArr[82] = 0.0f;
                                                fArr[83] = 1.0f;
                                            }
                                        } else {
                                            fArr[8] = 0.7f;
                                            fArr[9] = 0.3f;
                                            fArr[10] = 0.0f;
                                            fArr[11] = 1.0f;
                                            fArr[80] = 0.7f;
                                            fArr[81] = 0.3f;
                                            fArr[82] = 0.0f;
                                            fArr[83] = 1.0f;
                                        }
                                    } else {
                                        fArr[8] = 0.5f;
                                        fArr[9] = 0.5f;
                                        fArr[10] = 0.0f;
                                        fArr[11] = 1.0f;
                                        fArr[80] = 0.5f;
                                        fArr[81] = 0.5f;
                                        fArr[82] = 0.0f;
                                        fArr[83] = 1.0f;
                                    }
                                } else {
                                    fArr[8] = 0.3f;
                                    fArr[9] = 0.7f;
                                    fArr[10] = 0.0f;
                                    fArr[11] = 1.0f;
                                    fArr[80] = 0.3f;
                                    fArr[81] = 0.7f;
                                    fArr[82] = 0.0f;
                                    fArr[83] = 1.0f;
                                }
                            } else {
                                fArr[8] = 0.1f;
                                fArr[9] = 0.9f;
                                fArr[10] = 0.0f;
                                fArr[11] = 1.0f;
                                fArr[80] = 0.1f;
                                fArr[81] = 0.9f;
                                fArr[82] = 0.0f;
                                fArr[83] = 1.0f;
                            }
                        } else {
                            fArr[8] = 0.0f;
                            fArr[9] = 0.9f;
                            fArr[10] = 0.1f;
                            fArr[11] = 1.0f;
                            fArr[80] = 0.0f;
                            fArr[81] = 0.9f;
                            fArr[82] = 0.1f;
                            fArr[83] = 1.0f;
                        }
                    } else {
                        fArr[8] = 0.0f;
                        fArr[9] = 0.7f;
                        fArr[10] = 0.3f;
                        fArr[11] = 1.0f;
                        fArr[80] = 0.0f;
                        fArr[81] = 0.7f;
                        fArr[82] = 0.3f;
                        fArr[83] = 1.0f;
                    }
                } else {
                    fArr[8] = 0.0f;
                    fArr[9] = 0.5f;
                    fArr[10] = 0.5f;
                    fArr[11] = 1.0f;
                    fArr[80] = 0.0f;
                    fArr[81] = 0.5f;
                    fArr[82] = 0.5f;
                    fArr[83] = 1.0f;
                }
            } else {
                fArr[8] = 0.0f;
                fArr[9] = 0.3f;
                fArr[10] = 0.7f;
                fArr[11] = 1.0f;
                fArr[80] = 0.0f;
                fArr[81] = 0.3f;
                fArr[82] = 0.7f;
                fArr[83] = 1.0f;
            }
            if (calRealNumber(this.data.get(i2)[10]) == this.minColor) {
                fArr[12] = 0.0f;
                fArr[13] = 0.0f;
                fArr[14] = 1.0f;
                fArr[15] = 1.0f;
                fArr[84] = 0.0f;
                fArr[85] = 0.0f;
                fArr[86] = 1.0f;
                fArr[87] = 1.0f;
            } else if (calRealNumber(this.data.get(i2)[10]) > this.minColor && calRealNumber(this.data.get(i2)[10]) < this.minColor + f) {
                fArr[12] = 0.0f;
                fArr[13] = 0.1f;
                fArr[14] = 0.9f;
                fArr[15] = 1.0f;
                fArr[84] = 0.0f;
                fArr[85] = 0.1f;
                fArr[86] = 0.9f;
                fArr[87] = 1.0f;
            } else if (calRealNumber(this.data.get(i2)[10]) == this.minColor + f) {
                fArr[12] = 0.0f;
                fArr[13] = 0.2f;
                fArr[14] = 0.8f;
                fArr[15] = 1.0f;
                fArr[84] = 0.0f;
                fArr[85] = 0.2f;
                fArr[86] = 0.8f;
                fArr[87] = 1.0f;
            } else if (calRealNumber(this.data.get(i2)[10]) <= this.minColor + f || calRealNumber(this.data.get(i2)[10]) >= this.minColor + (2.0f * f)) {
                float f26 = 2.0f * f;
                if (calRealNumber(this.data.get(i2)[10]) == this.minColor + f26) {
                    fArr[12] = 0.0f;
                    fArr[13] = 0.4f;
                    fArr[14] = 0.6f;
                    fArr[15] = 1.0f;
                    fArr[84] = 0.0f;
                    fArr[85] = 0.4f;
                    fArr[86] = 0.6f;
                    fArr[87] = 1.0f;
                } else if (calRealNumber(this.data.get(i2)[10]) <= this.minColor + f26 || calRealNumber(this.data.get(i2)[10]) >= this.minColor + (3.0f * f)) {
                    float f27 = 3.0f * f;
                    if (calRealNumber(this.data.get(i2)[10]) == this.minColor + f27) {
                        fArr[12] = 0.0f;
                        fArr[13] = 0.6f;
                        fArr[14] = 0.4f;
                        fArr[15] = 1.0f;
                        fArr[84] = 0.0f;
                        fArr[85] = 0.6f;
                        fArr[86] = 0.4f;
                        fArr[87] = 1.0f;
                    } else if (calRealNumber(this.data.get(i2)[10]) <= this.minColor + f27 || calRealNumber(this.data.get(i2)[10]) >= this.minColor + (4.0f * f)) {
                        float f28 = 4.0f * f;
                        if (calRealNumber(this.data.get(i2)[10]) == this.minColor + f28) {
                            fArr[12] = 0.0f;
                            fArr[13] = 0.8f;
                            fArr[14] = 0.2f;
                            fArr[15] = 1.0f;
                            fArr[84] = 0.0f;
                            fArr[85] = 0.8f;
                            fArr[86] = 0.2f;
                            fArr[87] = 1.0f;
                        } else if (calRealNumber(this.data.get(i2)[10]) <= this.minColor + f28 || calRealNumber(this.data.get(i2)[10]) >= this.minColor + (5.0f * f)) {
                            float f29 = 5.0f * f;
                            if (calRealNumber(this.data.get(i2)[10]) == this.minColor + f29) {
                                fArr[12] = 0.0f;
                                fArr[13] = 1.0f;
                                fArr[14] = 0.0f;
                                fArr[15] = 1.0f;
                                fArr[84] = 0.0f;
                                fArr[85] = 1.0f;
                                fArr[86] = 0.0f;
                                fArr[87] = 1.0f;
                            } else if (calRealNumber(this.data.get(i2)[10]) <= this.minColor + f29 || calRealNumber(this.data.get(i2)[10]) >= this.minColor + (6.0f * f)) {
                                float f30 = 6.0f * f;
                                if (calRealNumber(this.data.get(i2)[10]) == this.minColor + f30) {
                                    fArr[12] = 0.2f;
                                    fArr[13] = 0.8f;
                                    fArr[14] = 0.0f;
                                    fArr[15] = 1.0f;
                                    fArr[84] = 0.2f;
                                    fArr[85] = 0.8f;
                                    fArr[86] = 0.0f;
                                    fArr[87] = 1.0f;
                                } else if (calRealNumber(this.data.get(i2)[10]) <= this.minColor + f30 || calRealNumber(this.data.get(i2)[10]) >= this.minColor + (7.0f * f)) {
                                    float f31 = 7.0f * f;
                                    if (calRealNumber(this.data.get(i2)[10]) == this.minColor + f31) {
                                        fArr[12] = 0.4f;
                                        fArr[13] = 0.6f;
                                        fArr[14] = 0.0f;
                                        fArr[15] = 1.0f;
                                        fArr[84] = 0.4f;
                                        fArr[85] = 0.6f;
                                        fArr[86] = 0.0f;
                                        fArr[87] = 1.0f;
                                    } else if (calRealNumber(this.data.get(i2)[10]) <= this.minColor + f31 || calRealNumber(this.data.get(i2)[10]) >= this.minColor + (8.0f * f)) {
                                        float f32 = 8.0f * f;
                                        if (calRealNumber(this.data.get(i2)[10]) == this.minColor + f32) {
                                            fArr[12] = 0.6f;
                                            fArr[13] = 0.4f;
                                            fArr[14] = 0.0f;
                                            fArr[15] = 1.0f;
                                            fArr[84] = 0.6f;
                                            fArr[85] = 0.4f;
                                            fArr[86] = 0.0f;
                                            fArr[87] = 1.0f;
                                        } else if (calRealNumber(this.data.get(i2)[10]) <= this.minColor + f32 || calRealNumber(this.data.get(i2)[10]) >= this.minColor + (9.0f * f)) {
                                            float f33 = 9.0f * f;
                                            if (calRealNumber(this.data.get(i2)[10]) == this.minColor + f33) {
                                                fArr[12] = 0.8f;
                                                fArr[13] = 0.2f;
                                                fArr[14] = 0.0f;
                                                fArr[15] = 1.0f;
                                                fArr[84] = 0.8f;
                                                fArr[85] = 0.2f;
                                                fArr[86] = 0.0f;
                                                fArr[87] = 1.0f;
                                            } else if (calRealNumber(this.data.get(i2)[10]) > this.minColor + f33 && calRealNumber(this.data.get(i2)[10]) < this.maxColor) {
                                                fArr[12] = 0.9f;
                                                fArr[13] = 0.1f;
                                                fArr[14] = 0.0f;
                                                fArr[15] = 1.0f;
                                                fArr[84] = 0.9f;
                                                fArr[85] = 0.1f;
                                                fArr[86] = 0.0f;
                                                fArr[87] = 1.0f;
                                            } else if (calRealNumber(this.data.get(i2)[10]) == this.maxColor) {
                                                fArr[12] = 1.0f;
                                                fArr[13] = 0.0f;
                                                fArr[14] = 0.0f;
                                                fArr[15] = 1.0f;
                                                fArr[84] = 1.0f;
                                                fArr[85] = 0.0f;
                                                fArr[86] = 0.0f;
                                                fArr[87] = 1.0f;
                                            }
                                        } else {
                                            fArr[12] = 0.7f;
                                            fArr[13] = 0.3f;
                                            fArr[14] = 0.0f;
                                            fArr[15] = 1.0f;
                                            fArr[84] = 0.7f;
                                            fArr[85] = 0.3f;
                                            fArr[86] = 0.0f;
                                            fArr[87] = 1.0f;
                                        }
                                    } else {
                                        fArr[12] = 0.5f;
                                        fArr[13] = 0.5f;
                                        fArr[14] = 0.0f;
                                        fArr[15] = 1.0f;
                                        fArr[84] = 0.5f;
                                        fArr[85] = 0.5f;
                                        fArr[86] = 0.0f;
                                        fArr[87] = 1.0f;
                                    }
                                } else {
                                    fArr[12] = 0.3f;
                                    fArr[13] = 0.7f;
                                    fArr[14] = 0.0f;
                                    fArr[15] = 1.0f;
                                    fArr[84] = 0.3f;
                                    fArr[85] = 0.7f;
                                    fArr[86] = 0.0f;
                                    fArr[87] = 1.0f;
                                }
                            } else {
                                fArr[12] = 0.1f;
                                fArr[13] = 0.9f;
                                fArr[14] = 0.0f;
                                fArr[15] = 1.0f;
                                fArr[84] = 0.1f;
                                fArr[85] = 0.9f;
                                fArr[86] = 0.0f;
                                fArr[87] = 1.0f;
                            }
                        } else {
                            fArr[12] = 0.0f;
                            fArr[13] = 0.9f;
                            fArr[14] = 0.1f;
                            fArr[15] = 1.0f;
                            fArr[84] = 0.0f;
                            fArr[85] = 0.9f;
                            fArr[86] = 0.1f;
                            fArr[87] = 1.0f;
                        }
                    } else {
                        fArr[12] = 0.0f;
                        fArr[13] = 0.7f;
                        fArr[14] = 0.3f;
                        fArr[15] = 1.0f;
                        fArr[84] = 0.0f;
                        fArr[85] = 0.7f;
                        fArr[86] = 0.3f;
                        fArr[87] = 1.0f;
                    }
                } else {
                    fArr[12] = 0.0f;
                    fArr[13] = 0.5f;
                    fArr[14] = 0.5f;
                    fArr[15] = 1.0f;
                    fArr[84] = 0.0f;
                    fArr[85] = 0.5f;
                    fArr[86] = 0.5f;
                    fArr[87] = 1.0f;
                }
            } else {
                fArr[12] = 0.0f;
                fArr[13] = 0.3f;
                fArr[14] = 0.7f;
                fArr[15] = 1.0f;
                fArr[84] = 0.0f;
                fArr[85] = 0.3f;
                fArr[86] = 0.7f;
                fArr[87] = 1.0f;
            }
            this.colors.add(fArr);
        }
        if (this.is_Finished) {
            divideHeight();
        }
    }

    private void divideHeight() {
        for (int i = 0; i < this.data.size(); i++) {
            this.data.get(i)[1] = calDividedNumber(this.data.get(i)[1]);
            this.data.get(i)[4] = calDividedNumber(this.data.get(i)[4]);
            this.data.get(i)[7] = calDividedNumber(this.data.get(i)[7]);
            this.data.get(i)[10] = calDividedNumber(this.data.get(i)[10]);
            this.data.get(i)[13] = calDividedNumber(this.data.get(i)[13]);
            this.data.get(i)[16] = calDividedNumber(this.data.get(i)[16]);
            this.data.get(i)[19] = calDividedNumber(this.data.get(i)[19]);
            this.data.get(i)[22] = calDividedNumber(this.data.get(i)[22]);
            this.data.get(i)[25] = calDividedNumber(this.data.get(i)[25]);
            this.data.get(i)[28] = calDividedNumber(this.data.get(i)[28]);
            this.data.get(i)[31] = calDividedNumber(this.data.get(i)[31]);
            this.data.get(i)[34] = calDividedNumber(this.data.get(i)[34]);
            this.data.get(i)[37] = calDividedNumber(this.data.get(i)[37]);
            this.data.get(i)[40] = calDividedNumber(this.data.get(i)[40]);
            this.data.get(i)[43] = calDividedNumber(this.data.get(i)[43]);
            this.data.get(i)[46] = calDividedNumber(this.data.get(i)[46]);
            this.data.get(i)[49] = calDividedNumber(this.data.get(i)[49]);
            this.data.get(i)[52] = calDividedNumber(this.data.get(i)[52]);
            this.data.get(i)[55] = calDividedNumber(this.data.get(i)[55]);
            this.data.get(i)[58] = calDividedNumber(this.data.get(i)[58]);
            this.data.get(i)[61] = calDividedNumber(this.data.get(i)[61]);
            this.data.get(i)[64] = calDividedNumber(this.data.get(i)[64]);
            this.data.get(i)[67] = calDividedNumber(this.data.get(i)[67]);
            this.data.get(i)[70] = calDividedNumber(this.data.get(i)[70]);
        }
        this.finalData.clear();
        for (int i2 = 0; i2 < this.data.size(); i2++) {
            this.finalData.add(new SurfaceShape(this.data.get(i2), this.colors.get(i2)));
        }
    }


    /*private void zigzagBalancing() {
        int i;
        int i2;
        int i3 = 0;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8 = 0;
        int i9 = 0;
        int i10 = 0;
        int i11;
        int i12 = 0;
        int i13 = 0;
        int i14;
        ArrayList arrayList = new ArrayList();
        int i15 = 0;
        int i16 = 0;
        for (int i17 = 0; i17 < this.data.size(); i17 += 4) {
            i16++;
            if (i15 % 2 == 0) {
                int i18 = i17 - 4;
                if (i18 >= 0 && i17 - 1 != this.PalsPerColumn * 4 && !this.LeftBalance.contains(Integer.valueOf(i17))) {
                    this.LeftBalance.add(Integer.valueOf(i17));
                    float f = (this.data.get(i17)[58] + this.data.get(i18)[58]) / 2.0f;
                    this.data.get(i17)[55] = f;
                    this.data.get(i17)[34] = f;
                    this.data.get(i17)[1] = f;
                    this.data.get(i17)[22] = f;
                    this.data.get(i17)[31] = f;
                    this.data.get(i17)[7] = f;
                    int i19 = i17 + 1;
                    this.data.get(i19)[55] = f;
                    this.data.get(i19)[34] = f;
                    this.data.get(i19)[1] = f;
                    this.data.get(i19)[22] = f;
                    this.data.get(i19)[31] = f;
                    this.data.get(i19)[7] = f;
                    this.data.get(i17)[61] = this.data.get(i17)[7];
                    this.data.get(i17)[67] = this.data.get(i17)[1];
                    this.data.get(i17)[49] = this.data.get(i17)[1];
                    this.data.get(i17)[25] = this.data.get(i17)[7];
                    this.data.get(i17)[28] = this.data.get(i17)[1];
                    this.data.get(i17)[16] = this.data.get(i17)[7];
                    this.data.get(i19)[61] = this.data.get(i19)[7];
                    this.data.get(i19)[67] = this.data.get(i19)[1];
                    this.data.get(i19)[49] = this.data.get(i19)[1];
                    this.data.get(i19)[25] = this.data.get(i19)[7];
                    this.data.get(i19)[28] = this.data.get(i19)[1];
                    this.data.get(i19)[16] = this.data.get(i19)[7];
                }
                int i20 = i17 + 4;
                if (i20 < this.data.size() && (i14 = i17 + 3) != this.PalsPerColumn * 4 && !this.RightBalance.contains(Integer.valueOf(i17))) {
                    this.RightBalance.add(Integer.valueOf(i17));
                    float f2 = (this.data.get(i17)[58] + this.data.get(i20)[58]) / 2.0f;
                    int i21 = i17 + 2;
                    this.data.get(i21)[58] = f2;
                    this.data.get(i21)[43] = f2;
                    this.data.get(i21)[4] = f2;
                    this.data.get(i21)[19] = f2;
                    this.data.get(i21)[46] = f2;
                    this.data.get(i21)[10] = f2;
                    this.data.get(i14)[58] = f2;
                    this.data.get(i14)[43] = f2;
                    this.data.get(i14)[4] = f2;
                    this.data.get(i14)[19] = f2;
                    this.data.get(i14)[46] = f2;
                    this.data.get(i14)[10] = f2;
                    this.data.get(i21)[64] = this.data.get(i21)[10];
                    this.data.get(i21)[70] = this.data.get(i21)[4];
                    this.data.get(i21)[52] = this.data.get(i21)[4];
                    this.data.get(i21)[37] = this.data.get(i21)[4];
                    this.data.get(i21)[40] = this.data.get(i21)[10];
                    this.data.get(i21)[13] = this.data.get(i21)[10];
                    this.data.get(i14)[64] = this.data.get(i14)[10];
                    this.data.get(i14)[70] = this.data.get(i14)[4];
                    this.data.get(i14)[52] = this.data.get(i14)[4];
                    this.data.get(i14)[37] = this.data.get(i14)[4];
                    this.data.get(i14)[40] = this.data.get(i14)[10];
                    this.data.get(i14)[13] = this.data.get(i14)[10];
                }
                int i22 = i15 + 1;
                int i23 = this.PalsPerColumn;
                if (i16 - this.CoefficientCounterRTL[(i16 - ((i22 * i23) - i23)) - 1] > 0 && (i12 * 4) - 4 < this.data.size() && !this.TopBalance.contains(Integer.valueOf(i17))) {
                    this.TopBalance.add(Integer.valueOf(i17));
                    float f3 = (this.data.get(i17)[58] + this.data.get(i13)[58]) / 2.0f;
                    this.data.get(i17)[19] = f3;
                    this.data.get(i17)[46] = f3;
                    this.data.get(i17)[10] = f3;
                    this.data.get(i17)[22] = f3;
                    this.data.get(i17)[31] = f3;
                    this.data.get(i17)[7] = f3;
                    int i24 = i17 + 2;
                    this.data.get(i24)[19] = f3;
                    this.data.get(i24)[46] = f3;
                    this.data.get(i24)[10] = f3;
                    this.data.get(i24)[22] = f3;
                    this.data.get(i24)[31] = f3;
                    this.data.get(i24)[7] = f3;
                    this.data.get(i17)[61] = this.data.get(i17)[7];
                    this.data.get(i17)[64] = this.data.get(i17)[10];
                    this.data.get(i17)[40] = this.data.get(i17)[10];
                    this.data.get(i17)[25] = this.data.get(i17)[7];
                    this.data.get(i17)[13] = this.data.get(i17)[10];
                    this.data.get(i17)[16] = this.data.get(i17)[7];
                    this.data.get(i24)[61] = this.data.get(i24)[7];
                    this.data.get(i24)[64] = this.data.get(i24)[10];
                    this.data.get(i24)[40] = this.data.get(i24)[10];
                    this.data.get(i24)[25] = this.data.get(i24)[7];
                    this.data.get(i24)[13] = this.data.get(i24)[10];
                    this.data.get(i24)[16] = this.data.get(i24)[7];
                }
                int i25 = this.PalsPerColumn;
                int i26 = this.CoefficientCounterLTR[(i16 - ((i22 * i25) - i25)) - 1] + i16;
                int i27 = (i26 * 4) - 4;
                if (i27 < this.data.size() && !this.BottomBalance.contains(Integer.valueOf(i17))) {
                    Log.d("TAG", i16 + " -- " + i26);
                    this.BottomBalance.add(Integer.valueOf(i17));
                    float f4 = (this.data.get(i17)[58] + this.data.get(i27)[58]) / 2.0f;
                    int i28 = i17 + 1;
                    this.data.get(i28)[55] = f4;
                    this.data.get(i28)[34] = f4;
                    this.data.get(i28)[1] = f4;
                    this.data.get(i28)[58] = f4;
                    this.data.get(i28)[43] = f4;
                    this.data.get(i28)[4] = f4;
                    int i29 = i17 + 3;
                    this.data.get(i29)[55] = f4;
                    this.data.get(i29)[34] = f4;
                    this.data.get(i29)[1] = f4;
                    this.data.get(i29)[58] = f4;
                    this.data.get(i29)[43] = f4;
                    this.data.get(i29)[4] = f4;
                    this.data.get(i28)[67] = this.data.get(i28)[1];
                    this.data.get(i28)[70] = this.data.get(i28)[4];
                    this.data.get(i28)[49] = this.data.get(i28)[1];
                    this.data.get(i28)[52] = this.data.get(i28)[4];
                    this.data.get(i28)[37] = this.data.get(i28)[4];
                    this.data.get(i28)[28] = this.data.get(i28)[1];
                    this.data.get(i29)[67] = this.data.get(i29)[1];
                    this.data.get(i29)[70] = this.data.get(i29)[4];
                    this.data.get(i29)[49] = this.data.get(i29)[1];
                    this.data.get(i29)[52] = this.data.get(i29)[4];
                    this.data.get(i29)[37] = this.data.get(i29)[4];
                    this.data.get(i29)[28] = this.data.get(i29)[1];
                }
            } else {
                if (i16 % 10 != 0 && (i11 = i17 + 4) < this.data.size() && !this.LeftBalance.contains(Integer.valueOf(i17))) {
                    this.LeftBalance.add(Integer.valueOf(i17));
                    float f5 = (this.data.get(i17)[58] + this.data.get(i11)[58]) / 2.0f;
                    this.data.get(i17)[55] = f5;
                    this.data.get(i17)[34] = f5;
                    this.data.get(i17)[1] = f5;
                    this.data.get(i17)[22] = f5;
                    this.data.get(i17)[31] = f5;
                    this.data.get(i17)[7] = f5;
                    int i30 = i17 + 1;
                    this.data.get(i30)[55] = f5;
                    this.data.get(i30)[34] = f5;
                    this.data.get(i30)[1] = f5;
                    this.data.get(i30)[22] = f5;
                    this.data.get(i30)[31] = f5;
                    this.data.get(i30)[7] = f5;
                    this.data.get(i17)[61] = f5;
                    this.data.get(i17)[67] = f5;
                    this.data.get(i17)[49] = f5;
                    this.data.get(i17)[25] = f5;
                    this.data.get(i17)[28] = f5;
                    this.data.get(i17)[16] = f5;
                    this.data.get(i30)[61] = f5;
                    this.data.get(i30)[67] = f5;
                    this.data.get(i30)[49] = f5;
                    this.data.get(i30)[25] = f5;
                    this.data.get(i30)[28] = f5;
                    this.data.get(i30)[16] = f5;
                }
                if ((i16 - 1) % 10 != 0 && !this.RightBalance.contains(Integer.valueOf(i17))) {
                    this.RightBalance.add(Integer.valueOf(i17));
                    float f6 = (this.data.get(i17)[58] + this.data.get(i17 - 4)[58]) / 2.0f;
                    int i31 = i17 + 2;
                    this.data.get(i31)[58] = f6;
                    this.data.get(i31)[43] = f6;
                    this.data.get(i31)[4] = f6;
                    this.data.get(i31)[19] = f6;
                    this.data.get(i31)[46] = f6;
                    this.data.get(i31)[10] = f6;
                    int i32 = i17 + 3;
                    this.data.get(i32)[58] = f6;
                    this.data.get(i32)[43] = f6;
                    this.data.get(i32)[4] = f6;
                    this.data.get(i32)[19] = f6;
                    this.data.get(i32)[46] = f6;
                    this.data.get(i32)[10] = f6;
                    this.data.get(i31)[64] = f6;
                    this.data.get(i31)[70] = f6;
                    this.data.get(i31)[52] = f6;
                    this.data.get(i31)[37] = f6;
                    this.data.get(i31)[40] = f6;
                    this.data.get(i31)[13] = f6;
                    this.data.get(i32)[64] = f6;
                    this.data.get(i32)[70] = f6;
                    this.data.get(i32)[52] = f6;
                    this.data.get(i32)[37] = f6;
                    this.data.get(i32)[40] = f6;
                    this.data.get(i32)[13] = f6;
                }
                int i33 = i15 + 1;
                int i34 = this.PalsPerColumn;
                if (i16 - this.CoefficientCounterRTL[(i16 - ((i33 * i34) - i34)) - 1] >= 0 && (i8 * 4) - 4 < this.data.size() && !this.TopBalance.contains(Integer.valueOf(i17))) {
                    this.TopBalance.add(Integer.valueOf(i17));
                    float f7 = (this.data.get(i17)[58] + this.data.get(i10)[58]) / 2.0f;
                    this.data.get(i17)[19] = f7;
                    this.data.get(i17)[46] = f7;
                    this.data.get(i17)[10] = f7;
                    this.data.get(i17)[22] = f7;
                    this.data.get(i17)[31] = f7;
                    this.data.get(i17)[7] = f7;
                    int i35 = i17 + 2;
                    this.data.get(i35)[19] = f7;
                    this.data.get(i35)[46] = f7;
                    this.data.get(i35)[10] = f7;
                    this.data.get(i35)[22] = f7;
                    this.data.get(i35)[31] = f7;
                    this.data.get(i35)[7] = f7;
                    this.data.get(i17)[61] = this.data.get(i17)[7];
                    this.data.get(i17)[64] = this.data.get(i17)[10];
                    this.data.get(i17)[40] = this.data.get(i17)[10];
                    this.data.get(i17)[25] = this.data.get(i17)[7];
                    this.data.get(i17)[13] = this.data.get(i17)[10];
                    this.data.get(i17)[16] = this.data.get(i17)[7];
                    this.data.get(i35)[61] = this.data.get(i35)[7];
                    this.data.get(i35)[64] = this.data.get(i35)[10];
                    this.data.get(i35)[40] = this.data.get(i35)[10];
                    this.data.get(i35)[25] = this.data.get(i35)[7];
                    this.data.get(i35)[13] = this.data.get(i35)[10];
                    this.data.get(i35)[16] = this.data.get(i35)[7];
                }
                int i36 = this.PalsPerColumn;
                if (((this.CoefficientCounterLTR[(i16 - ((i33 * i36) - i36)) - 1] + i16) - 1) * 4 < this.data.size() && !this.BottomBalance.contains(Integer.valueOf(i17))) {
                    this.BottomBalance.add(Integer.valueOf(i17));
                    float f8 = (this.data.get(i17)[58] + this.data.get((i9 * 4) - 4)[58]) / 2.0f;
                    int i37 = i17 + 1;
                    this.data.get(i37)[55] = f8;
                    this.data.get(i37)[34] = f8;
                    this.data.get(i37)[1] = f8;
                    this.data.get(i37)[58] = f8;
                    this.data.get(i37)[43] = f8;
                    this.data.get(i37)[4] = f8;
                    int i38 = i17 + 3;
                    this.data.get(i38)[55] = f8;
                    this.data.get(i38)[34] = f8;
                    this.data.get(i38)[1] = f8;
                    this.data.get(i38)[58] = f8;
                    this.data.get(i38)[43] = f8;
                    this.data.get(i38)[4] = f8;
                    this.data.get(i37)[67] = this.data.get(i37)[1];
                    this.data.get(i37)[70] = this.data.get(i37)[4];
                    this.data.get(i37)[49] = this.data.get(i37)[1];
                    this.data.get(i37)[52] = this.data.get(i37)[4];
                    this.data.get(i37)[37] = this.data.get(i37)[4];
                    this.data.get(i37)[28] = this.data.get(i37)[1];
                    this.data.get(i38)[67] = this.data.get(i38)[1];
                    this.data.get(i38)[70] = this.data.get(i38)[4];
                    this.data.get(i38)[49] = this.data.get(i38)[1];
                    this.data.get(i38)[52] = this.data.get(i38)[4];
                    this.data.get(i38)[37] = this.data.get(i38)[4];
                    this.data.get(i38)[28] = this.data.get(i38)[1];
                }
            }
            if (i16 % this.PalsPerColumn == 0) {
                i15++;
            }
        }
        int i39 = 0;
        int i40 = 0;
        for (int i41 = 0; i41 < this.data.size(); i41 += 4) {
            arrayList.clear();
            i40++;
            arrayList.add(Float.valueOf(this.data.get(i41)[7]));
            int i42 = i39 % 2;
            if (i42 == 0) {
                int i43 = i41 - 2;
                if (i43 > 0 && (i40 - 1) % this.PalsPerColumn != 0) {
                    arrayList.add(Float.valueOf(this.data.get(i43)[10]));
                }
            } else {
                int i44 = i41 + 6;
                if (i44 < this.data.size()) {
                    arrayList.add(Float.valueOf(this.data.get(i44)[10]));
                }
            }
            if (i42 == 0) {
                int i45 = this.PalsPerColumn;
                i = this.CoefficientCounterRTL[(i40 - (((i39 + 1) * i45) - i45)) - 1];
            } else {
                int i46 = this.PalsPerColumn;
                i = this.CoefficientCounterRTL[(i40 - (((i39 + 1) * i46) - i46)) - 1];
            }
            int i47 = i40 - i;
            if (i47 > 0 && (i7 = ((i47 * 4) - 4) + 1) < this.data.size()) {
                arrayList.add(Float.valueOf(this.data.get(i7)[1]));
            }
            if (i42 == 0) {
                if (i47 > 0 && (i6 = ((i47 * 4) - 4) + 7) < this.data.size()) {
                    arrayList.add(Float.valueOf(this.data.get(i6)[4]));
                }
            } else if (i47 > 0 && (i2 = ((i47 * 4) - 4) - 1) > 0) {
                arrayList.add(Float.valueOf(this.data.get(i2)[4]));
            }
            if (arrayList.size() > 1) {
                float f9 = 0.0f;
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    f9 += ((Float) it.next()).floatValue();
                }
                float size = f9 / arrayList.size();
                if (i42 == 0) {
                    int i48 = i41 - 2;
                    if (i48 > 0 && (i40 - 1) % this.PalsPerColumn != 0) {
                        this.data.get(i48)[46] = size;
                        this.data.get(i48)[10] = size;
                        this.data.get(i48)[19] = size;
                        this.data.get(i48)[64] = size;
                        this.data.get(i48)[40] = size;
                        this.data.get(i48)[13] = size;
                      *//* if (i47 <= 0 && (i5 = ((i47 * 4) - 4) + 1) < this.data.size()) {
                            this.data.get(i5)[1] = size;
                            this.data.get(i5)[34] = size;
                            this.data.get(i5)[55] = size;
                            this.data.get(i5)[67] = size;
                            this.data.get(i5)[49] = size;
                            this.data.get(i5)[28] = size;
                        }*//*
                        if (i47 > 0 && (i4 = ((i47 * 4) - 4) + 7) < this.data.size()) {
                            if (i42 != 0) {
                                this.data.get(i4)[4] = size;
                                this.data.get(i4)[43] = size;
                                this.data.get(i4)[58] = size;
                                this.data.get(i4)[70] = size;
                                this.data.get(i4)[37] = size;
                                this.data.get(i4)[52] = size;
                            } else {
                                int i49 = i3 - 1;
                                if (i49 >= 0) {
                                    this.data.get(i49)[4] = size;
                                    this.data.get(i49)[43] = size;
                                    this.data.get(i49)[58] = size;
                                    this.data.get(i49)[70] = size;
                                    this.data.get(i49)[37] = size;
                                    this.data.get(i49)[52] = size;
                                }
                            }
                        }
                        this.data.get(i41)[7] = size;
                        this.data.get(i41)[22] = size;
                        this.data.get(i41)[31] = size;
                        this.data.get(i41)[61] = size;
                        this.data.get(i41)[25] = size;
                        this.data.get(i41)[16] = size;
                    }
                } else if (i41 + 4 < this.data.size()) {
                    int i50 = i41 + 6;
                    this.data.get(i50)[46] = size;
                    this.data.get(i50)[10] = size;
                    this.data.get(i50)[19] = size;
                    this.data.get(i50)[64] = size;
                    this.data.get(i50)[40] = size;
                    this.data.get(i50)[13] = size;
                    if (i47 <= 0) {
                    }
                    if (i47 > 0) {
                        if (i42 != 0) {
                        }
                    }
                    this.data.get(i41)[7] = size;
                    this.data.get(i41)[22] = size;
                    this.data.get(i41)[31] = size;
                    this.data.get(i41)[61] = size;
                    this.data.get(i41)[25] = size;
                    this.data.get(i41)[16] = size;
                }
                if (i47 <= 0) {
                }
                if (i47 > 0) {
                }
                this.data.get(i41)[7] = size;
                this.data.get(i41)[22] = size;
                this.data.get(i41)[31] = size;
                this.data.get(i41)[61] = size;
                this.data.get(i41)[25] = size;
                this.data.get(i41)[16] = size;
            }
            if (i40 % this.PalsPerColumn == 0) {
                i39++;
            }
        }
    }*/

    private void fromFirstBalancing() {
        char c;
        int i;
        ArrayList arrayList = new ArrayList();
        int i2 = 0;
        while (true) {
            c = '+';
            if (i2 >= this.data.size()) {
                break;
            }
            int i3 = i2 - 4;
            if (i3 >= 0 && i2 - 1 != this.PalsPerColumn * 4 && !this.LeftBalance.contains(Integer.valueOf(i2))) {
                this.LeftBalance.add(Integer.valueOf(i2));
                float f = (this.data.get(i2)[58] + this.data.get(i3)[58]) / 2.0f;
                this.data.get(i2)[55] = f;
                this.data.get(i2)[34] = f;
                this.data.get(i2)[1] = f;
                this.data.get(i2)[22] = f;
                this.data.get(i2)[31] = f;
                this.data.get(i2)[7] = f;
                int i4 = i2 + 1;
                this.data.get(i4)[55] = f;
                this.data.get(i4)[34] = f;
                this.data.get(i4)[1] = f;
                this.data.get(i4)[22] = f;
                this.data.get(i4)[31] = f;
                this.data.get(i4)[7] = f;
                this.data.get(i2)[61] = this.data.get(i2)[7];
                this.data.get(i2)[67] = this.data.get(i2)[1];
                this.data.get(i2)[49] = this.data.get(i2)[1];
                this.data.get(i2)[25] = this.data.get(i2)[7];
                this.data.get(i2)[28] = this.data.get(i2)[1];
                this.data.get(i2)[16] = this.data.get(i2)[7];
                this.data.get(i4)[61] = this.data.get(i4)[7];
                this.data.get(i4)[67] = this.data.get(i4)[1];
                this.data.get(i4)[49] = this.data.get(i4)[1];
                this.data.get(i4)[25] = this.data.get(i4)[7];
                this.data.get(i4)[28] = this.data.get(i4)[1];
                this.data.get(i4)[16] = this.data.get(i4)[7];
            }
            int i5 = i2 + 4;
            if (i5 < this.data.size() && (i = i2 + 3) != this.PalsPerColumn * 4 && !this.RightBalance.contains(Integer.valueOf(i2))) {
                this.RightBalance.add(Integer.valueOf(i2));
                float f2 = (this.data.get(i2)[58] + this.data.get(i5)[58]) / 2.0f;
                int i6 = i2 + 2;
                this.data.get(i6)[58] = f2;
                this.data.get(i6)[43] = f2;
                this.data.get(i6)[4] = f2;
                this.data.get(i6)[19] = f2;
                this.data.get(i6)[46] = f2;
                this.data.get(i6)[10] = f2;
                this.data.get(i)[58] = f2;
                this.data.get(i)[43] = f2;
                this.data.get(i)[4] = f2;
                this.data.get(i)[19] = f2;
                this.data.get(i)[46] = f2;
                this.data.get(i)[10] = f2;
                this.data.get(i6)[64] = this.data.get(i6)[10];
                this.data.get(i6)[70] = this.data.get(i6)[4];
                this.data.get(i6)[52] = this.data.get(i6)[4];
                this.data.get(i6)[37] = this.data.get(i6)[4];
                this.data.get(i6)[40] = this.data.get(i6)[10];
                this.data.get(i6)[13] = this.data.get(i6)[10];
                this.data.get(i)[64] = this.data.get(i)[10];
                this.data.get(i)[70] = this.data.get(i)[4];
                this.data.get(i)[52] = this.data.get(i)[4];
                this.data.get(i)[37] = this.data.get(i)[4];
                this.data.get(i)[40] = this.data.get(i)[10];
                this.data.get(i)[13] = this.data.get(i)[10];
            }
            if (i2 - (this.PalsPerColumn * 4) >= 0 && !this.TopBalance.contains(Integer.valueOf(i2))) {
                this.TopBalance.add(Integer.valueOf(i2));
                float f3 = (this.data.get(i2)[58] + this.data.get(i2 - (this.PalsPerColumn * 4))[58]) / 2.0f;
                this.data.get(i2)[19] = f3;
                this.data.get(i2)[46] = f3;
                this.data.get(i2)[10] = f3;
                this.data.get(i2)[22] = f3;
                this.data.get(i2)[31] = f3;
                this.data.get(i2)[7] = f3;
                int i7 = i2 + 2;
                this.data.get(i7)[19] = f3;
                this.data.get(i7)[46] = f3;
                this.data.get(i7)[10] = f3;
                this.data.get(i7)[22] = f3;
                this.data.get(i7)[31] = f3;
                this.data.get(i7)[7] = f3;
                this.data.get(i2)[61] = this.data.get(i2)[7];
                this.data.get(i2)[64] = this.data.get(i2)[10];
                this.data.get(i2)[40] = this.data.get(i2)[10];
                this.data.get(i2)[25] = this.data.get(i2)[7];
                this.data.get(i2)[13] = this.data.get(i2)[10];
                this.data.get(i2)[16] = this.data.get(i2)[7];
                this.data.get(i7)[61] = this.data.get(i7)[7];
                this.data.get(i7)[64] = this.data.get(i7)[10];
                this.data.get(i7)[40] = this.data.get(i7)[10];
                this.data.get(i7)[25] = this.data.get(i7)[7];
                this.data.get(i7)[13] = this.data.get(i7)[10];
                this.data.get(i7)[16] = this.data.get(i7)[7];
            }
            if ((this.PalsPerColumn * 4) + i2 < this.data.size() && !this.BottomBalance.contains(Integer.valueOf(i2))) {
                this.BottomBalance.add(Integer.valueOf(i2));
                float f4 = (this.data.get(i2)[58] + this.data.get((this.PalsPerColumn * 4) + i2)[58]) / 2.0f;
                int i8 = i2 + 1;
                this.data.get(i8)[55] = f4;
                this.data.get(i8)[34] = f4;
                this.data.get(i8)[1] = f4;
                this.data.get(i8)[58] = f4;
                this.data.get(i8)[43] = f4;
                this.data.get(i8)[4] = f4;
                int i9 = i2 + 3;
                this.data.get(i9)[55] = f4;
                this.data.get(i9)[34] = f4;
                this.data.get(i9)[1] = f4;
                this.data.get(i9)[58] = f4;
                this.data.get(i9)[43] = f4;
                this.data.get(i9)[4] = f4;
                this.data.get(i8)[67] = this.data.get(i8)[1];
                this.data.get(i8)[70] = this.data.get(i8)[4];
                this.data.get(i8)[49] = this.data.get(i8)[1];
                this.data.get(i8)[52] = this.data.get(i8)[4];
                this.data.get(i8)[37] = this.data.get(i8)[4];
                this.data.get(i8)[28] = this.data.get(i8)[1];
                this.data.get(i9)[67] = this.data.get(i9)[1];
                this.data.get(i9)[70] = this.data.get(i9)[4];
                this.data.get(i9)[49] = this.data.get(i9)[1];
                this.data.get(i9)[52] = this.data.get(i9)[4];
                this.data.get(i9)[37] = this.data.get(i9)[4];
                this.data.get(i9)[28] = this.data.get(i9)[1];
            }
            i2 = i5;
        }
        int i10 = 0;
        while (i10 < this.data.size()) {
            arrayList.clear();
            arrayList.add(Float.valueOf(this.data.get(i10)[7]));
            int i11 = i10 - 2;
            if (i11 > 0) {
                arrayList.add(Float.valueOf(this.data.get(i11)[10]));
            }
            int i12 = this.PalsPerColumn;
            if ((i10 - (i12 * 4)) + 1 > 0) {
                arrayList.add(Float.valueOf(this.data.get((i10 - (i12 * 4)) + 1)[1]));
            }
            int i13 = this.PalsPerColumn;
            if ((i10 - (i13 * 4)) - 1 > 0) {
                arrayList.add(Float.valueOf(this.data.get((i10 - (i13 * 4)) + 1)[4]));
            }
            if (arrayList.size() > 1) {
                float f5 = 0.0f;
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    f5 += ((Float) it.next()).floatValue();
                }
                float size = f5 / arrayList.size();
                if (i11 > 0) {
                    this.data.get(i11)[46] = size;
                    this.data.get(i11)[10] = size;
                    this.data.get(i11)[19] = size;
                    this.data.get(i11)[64] = size;
                    this.data.get(i11)[40] = size;
                    this.data.get(i11)[13] = size;
                }
                int i14 = this.PalsPerColumn;
                if ((i10 - (i14 * 4)) + 1 > 0) {
                    this.data.get((i10 - (i14 * 4)) + 1)[1] = size;
                    this.data.get((i10 - (this.PalsPerColumn * 4)) + 1)[34] = size;
                    this.data.get((i10 - (this.PalsPerColumn * 4)) + 1)[55] = size;
                    this.data.get((i10 - (this.PalsPerColumn * 4)) + 1)[67] = size;
                    this.data.get((i10 - (this.PalsPerColumn * 4)) + 1)[49] = size;
                    this.data.get((i10 - (this.PalsPerColumn * 4)) + 1)[28] = size;
                }
                int i15 = this.PalsPerColumn;
                if ((i10 - (i15 * 4)) - 1 > 0) {
                    this.data.get((i10 - (i15 * 4)) - 1)[4] = size;
                    this.data.get((i10 - (this.PalsPerColumn * 4)) - 1)[c] = size;
                    this.data.get((i10 - (this.PalsPerColumn * 4)) - 1)[58] = size;
                    this.data.get((i10 - (this.PalsPerColumn * 4)) - 1)[70] = size;
                    this.data.get((i10 - (this.PalsPerColumn * 4)) - 1)[37] = size;
                    this.data.get((i10 - (this.PalsPerColumn * 4)) - 1)[52] = size;
                }
                this.data.get(i10)[7] = size;
                this.data.get(i10)[22] = size;
                this.data.get(i10)[31] = size;
                this.data.get(i10)[61] = size;
                this.data.get(i10)[25] = size;
                this.data.get(i10)[16] = size;
            }
            i10 += 4;
            c = '+';
        }
    }

    private void zigzagBalancing() {
        ArrayList<Float> arrayList = new ArrayList();
        int i = 0;
        int j = 0;
        byte b = 0;
        while (b < this.data.size()) {
            int k = j + 1;
            if (i % 2 == 0) {
                j = b - 4;
                if (j >= 0 && b - 1 != this.PalsPerColumn * 4 && !this.LeftBalance.contains(Integer.valueOf(b))) {
                    this.LeftBalance.add(Integer.valueOf(b));
                    float f = (((float[])this.data.get(b))[58] + ((float[])this.data.get(j))[58]) / 2.0F;
                    ((float[])this.data.get(b))[55] = f;
                    ((float[])this.data.get(b))[34] = f;
                    ((float[])this.data.get(b))[1] = f;
                    ((float[])this.data.get(b))[22] = f;
                    ((float[])this.data.get(b))[31] = f;
                    ((float[])this.data.get(b))[7] = f;
                    ArrayList<float[]> arrayList1 = this.data;
                    j = b + 1;
                    ((float[])arrayList1.get(j))[55] = f;
                    ((float[])this.data.get(j))[34] = f;
                    ((float[])this.data.get(j))[1] = f;
                    ((float[])this.data.get(j))[22] = f;
                    ((float[])this.data.get(j))[31] = f;
                    ((float[])this.data.get(j))[7] = f;
                    ((float[])this.data.get(b))[61] = ((float[])this.data.get(b))[7];
                    ((float[])this.data.get(b))[67] = ((float[])this.data.get(b))[1];
                    ((float[])this.data.get(b))[49] = ((float[])this.data.get(b))[1];
                    ((float[])this.data.get(b))[25] = ((float[])this.data.get(b))[7];
                    ((float[])this.data.get(b))[28] = ((float[])this.data.get(b))[1];
                    ((float[])this.data.get(b))[16] = ((float[])this.data.get(b))[7];
                    ((float[])this.data.get(j))[61] = ((float[])this.data.get(j))[7];
                    ((float[])this.data.get(j))[67] = ((float[])this.data.get(j))[1];
                    ((float[])this.data.get(j))[49] = ((float[])this.data.get(j))[1];
                    ((float[])this.data.get(j))[25] = ((float[])this.data.get(j))[7];
                    ((float[])this.data.get(j))[28] = ((float[])this.data.get(j))[1];
                    ((float[])this.data.get(j))[16] = ((float[])this.data.get(j))[7];
                }
                int m = b + 4;
                if (m < this.data.size()) {
                    j = b + 3;
                    if (j != this.PalsPerColumn * 4 && !this.RightBalance.contains(Integer.valueOf(b))) {
                        this.RightBalance.add(Integer.valueOf(b));
                        float f = (((float[])this.data.get(b))[58] + ((float[])this.data.get(m))[58]) / 2.0F;
                        ArrayList<float[]> arrayList1 = this.data;
                        m = b + 2;
                        ((float[])arrayList1.get(m))[58] = f;
                        ((float[])this.data.get(m))[43] = f;
                        ((float[])this.data.get(m))[4] = f;
                        ((float[])this.data.get(m))[19] = f;
                        ((float[])this.data.get(m))[46] = f;
                        ((float[])this.data.get(m))[10] = f;
                        ((float[])this.data.get(j))[58] = f;
                        ((float[])this.data.get(j))[43] = f;
                        ((float[])this.data.get(j))[4] = f;
                        ((float[])this.data.get(j))[19] = f;
                        ((float[])this.data.get(j))[46] = f;
                        ((float[])this.data.get(j))[10] = f;
                        ((float[])this.data.get(m))[64] = ((float[])this.data.get(m))[10];
                        ((float[])this.data.get(m))[70] = ((float[])this.data.get(m))[4];
                        ((float[])this.data.get(m))[52] = ((float[])this.data.get(m))[4];
                        ((float[])this.data.get(m))[37] = ((float[])this.data.get(m))[4];
                        ((float[])this.data.get(m))[40] = ((float[])this.data.get(m))[10];
                        ((float[])this.data.get(m))[13] = ((float[])this.data.get(m))[10];
                        ((float[])this.data.get(j))[64] = ((float[])this.data.get(j))[10];
                        ((float[])this.data.get(j))[70] = ((float[])this.data.get(j))[4];
                        ((float[])this.data.get(j))[52] = ((float[])this.data.get(j))[4];
                        ((float[])this.data.get(j))[37] = ((float[])this.data.get(j))[4];
                        ((float[])this.data.get(j))[40] = ((float[])this.data.get(j))[10];
                        ((float[])this.data.get(j))[13] = ((float[])this.data.get(j))[10];
                    }
                }
                j = i + 1;
                m = this.PalsPerColumn;
                m = k - this.CoefficientCounterRTL[k - j * m - m - 1];
                if (m > 0) {
                    m = m * 4 - 4;
                    if (m < this.data.size() && !this.TopBalance.contains(Integer.valueOf(b))) {
                        this.TopBalance.add(Integer.valueOf(b));
                        float f = (((float[])this.data.get(b))[58] + ((float[])this.data.get(m))[58]) / 2.0F;
                        ((float[])this.data.get(b))[19] = f;
                        ((float[])this.data.get(b))[46] = f;
                        ((float[])this.data.get(b))[10] = f;
                        ((float[])this.data.get(b))[22] = f;
                        ((float[])this.data.get(b))[31] = f;
                        ((float[])this.data.get(b))[7] = f;
                        ArrayList<float[]> arrayList1 = this.data;
                        m = b + 2;
                        ((float[])arrayList1.get(m))[19] = f;
                        ((float[])this.data.get(m))[46] = f;
                        ((float[])this.data.get(m))[10] = f;
                        ((float[])this.data.get(m))[22] = f;
                        ((float[])this.data.get(m))[31] = f;
                        ((float[])this.data.get(m))[7] = f;
                        ((float[])this.data.get(b))[61] = ((float[])this.data.get(b))[7];
                        ((float[])this.data.get(b))[64] = ((float[])this.data.get(b))[10];
                        ((float[])this.data.get(b))[40] = ((float[])this.data.get(b))[10];
                        ((float[])this.data.get(b))[25] = ((float[])this.data.get(b))[7];
                        ((float[])this.data.get(b))[13] = ((float[])this.data.get(b))[10];
                        ((float[])this.data.get(b))[16] = ((float[])this.data.get(b))[7];
                        ((float[])this.data.get(m))[61] = ((float[])this.data.get(m))[7];
                        ((float[])this.data.get(m))[64] = ((float[])this.data.get(m))[10];
                        ((float[])this.data.get(m))[40] = ((float[])this.data.get(m))[10];
                        ((float[])this.data.get(m))[25] = ((float[])this.data.get(m))[7];
                        ((float[])this.data.get(m))[13] = ((float[])this.data.get(m))[10];
                        ((float[])this.data.get(m))[16] = ((float[])this.data.get(m))[7];
                    }
                }
                m = this.PalsPerColumn;
                j = this.CoefficientCounterLTR[k - j * m - m - 1] + k;
                m = j * 4 - 4;
                if (m < this.data.size() && !this.BottomBalance.contains(Integer.valueOf(b))) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(k);
                    stringBuilder.append(" -- ");
                    stringBuilder.append(j);
                    Log.d("TAG", stringBuilder.toString());
                    this.BottomBalance.add(Integer.valueOf(b));
                    float f = (((float[])this.data.get(b))[58] + ((float[])this.data.get(m))[58]) / 2.0F;
                    ArrayList<float[]> arrayList1 = this.data;
                    m = b + 1;
                    ((float[])arrayList1.get(m))[55] = f;
                    ((float[])this.data.get(m))[34] = f;
                    ((float[])this.data.get(m))[1] = f;
                    ((float[])this.data.get(m))[58] = f;
                    ((float[])this.data.get(m))[43] = f;
                    ((float[])this.data.get(m))[4] = f;
                    arrayList1 = this.data;
                    j = b + 3;
                    ((float[])arrayList1.get(j))[55] = f;
                    ((float[])this.data.get(j))[34] = f;
                    ((float[])this.data.get(j))[1] = f;
                    ((float[])this.data.get(j))[58] = f;
                    ((float[])this.data.get(j))[43] = f;
                    ((float[])this.data.get(j))[4] = f;
                    ((float[])this.data.get(m))[67] = ((float[])this.data.get(m))[1];
                    ((float[])this.data.get(m))[70] = ((float[])this.data.get(m))[4];
                    ((float[])this.data.get(m))[49] = ((float[])this.data.get(m))[1];
                    ((float[])this.data.get(m))[52] = ((float[])this.data.get(m))[4];
                    ((float[])this.data.get(m))[37] = ((float[])this.data.get(m))[4];
                    ((float[])this.data.get(m))[28] = ((float[])this.data.get(m))[1];
                    ((float[])this.data.get(j))[67] = ((float[])this.data.get(j))[1];
                    ((float[])this.data.get(j))[70] = ((float[])this.data.get(j))[4];
                    ((float[])this.data.get(j))[49] = ((float[])this.data.get(j))[1];
                    ((float[])this.data.get(j))[52] = ((float[])this.data.get(j))[4];
                    ((float[])this.data.get(j))[37] = ((float[])this.data.get(j))[4];
                    ((float[])this.data.get(j))[28] = ((float[])this.data.get(j))[1];
                }
            } else {
                if (k % 10 != 0) {
                    j = b + 4;
                    if (j < this.data.size() && !this.LeftBalance.contains(Integer.valueOf(b))) {
                        this.LeftBalance.add(Integer.valueOf(b));
                        float f = (((float[])this.data.get(b))[58] + ((float[])this.data.get(j))[58]) / 2.0F;
                        ((float[])this.data.get(b))[55] = f;
                        ((float[])this.data.get(b))[34] = f;
                        ((float[])this.data.get(b))[1] = f;
                        ((float[])this.data.get(b))[22] = f;
                        ((float[])this.data.get(b))[31] = f;
                        ((float[])this.data.get(b))[7] = f;
                        ArrayList<float[]> arrayList1 = this.data;
                        j = b + 1;
                        ((float[])arrayList1.get(j))[55] = f;
                        ((float[])this.data.get(j))[34] = f;
                        ((float[])this.data.get(j))[1] = f;
                        ((float[])this.data.get(j))[22] = f;
                        ((float[])this.data.get(j))[31] = f;
                        ((float[])this.data.get(j))[7] = f;
                        ((float[])this.data.get(b))[61] = f;
                        ((float[])this.data.get(b))[67] = f;
                        ((float[])this.data.get(b))[49] = f;
                        ((float[])this.data.get(b))[25] = f;
                        ((float[])this.data.get(b))[28] = f;
                        ((float[])this.data.get(b))[16] = f;
                        ((float[])this.data.get(j))[61] = f;
                        ((float[])this.data.get(j))[67] = f;
                        ((float[])this.data.get(j))[49] = f;
                        ((float[])this.data.get(j))[25] = f;
                        ((float[])this.data.get(j))[28] = f;
                        ((float[])this.data.get(j))[16] = f;
                    }
                }
                if ((k - 1) % 10 != 0 && !this.RightBalance.contains(Integer.valueOf(b))) {
                    this.RightBalance.add(Integer.valueOf(b));
                    float f = (((float[])this.data.get(b))[58] + ((float[])this.data.get(b - 4))[58]) / 2.0F;
                    ArrayList<float[]> arrayList1 = this.data;
                    j = b + 2;
                    ((float[])arrayList1.get(j))[58] = f;
                    ((float[])this.data.get(j))[43] = f;
                    ((float[])this.data.get(j))[4] = f;
                    ((float[])this.data.get(j))[19] = f;
                    ((float[])this.data.get(j))[46] = f;
                    ((float[])this.data.get(j))[10] = f;
                    arrayList1 = this.data;
                    int n = b + 3;
                    ((float[])arrayList1.get(n))[58] = f;
                    ((float[])this.data.get(n))[43] = f;
                    ((float[])this.data.get(n))[4] = f;
                    ((float[])this.data.get(n))[19] = f;
                    ((float[])this.data.get(n))[46] = f;
                    ((float[])this.data.get(n))[10] = f;
                    ((float[])this.data.get(j))[64] = f;
                    ((float[])this.data.get(j))[70] = f;
                    ((float[])this.data.get(j))[52] = f;
                    ((float[])this.data.get(j))[37] = f;
                    ((float[])this.data.get(j))[40] = f;
                    ((float[])this.data.get(j))[13] = f;
                    ((float[])this.data.get(n))[64] = f;
                    ((float[])this.data.get(n))[70] = f;
                    ((float[])this.data.get(n))[52] = f;
                    ((float[])this.data.get(n))[37] = f;
                    ((float[])this.data.get(n))[40] = f;
                    ((float[])this.data.get(n))[13] = f;
                }
                j = i + 1;
                int m = this.PalsPerColumn;
                m = k - this.CoefficientCounterRTL[k - j * m - m - 1];
                if (m >= 0) {
                    m = m * 4 - 4;
                    if (m < this.data.size() && !this.TopBalance.contains(Integer.valueOf(b))) {
                        this.TopBalance.add(Integer.valueOf(b));
                        float f = (((float[])this.data.get(b))[58] + ((float[])this.data.get(m))[58]) / 2.0F;
                        ((float[])this.data.get(b))[19] = f;
                        ((float[])this.data.get(b))[46] = f;
                        ((float[])this.data.get(b))[10] = f;
                        ((float[])this.data.get(b))[22] = f;
                        ((float[])this.data.get(b))[31] = f;
                        ((float[])this.data.get(b))[7] = f;
                        ArrayList<float[]> arrayList1 = this.data;
                        m = b + 2;
                        ((float[])arrayList1.get(m))[19] = f;
                        ((float[])this.data.get(m))[46] = f;
                        ((float[])this.data.get(m))[10] = f;
                        ((float[])this.data.get(m))[22] = f;
                        ((float[])this.data.get(m))[31] = f;
                        ((float[])this.data.get(m))[7] = f;
                        ((float[])this.data.get(b))[61] = ((float[])this.data.get(b))[7];
                        ((float[])this.data.get(b))[64] = ((float[])this.data.get(b))[10];
                        ((float[])this.data.get(b))[40] = ((float[])this.data.get(b))[10];
                        ((float[])this.data.get(b))[25] = ((float[])this.data.get(b))[7];
                        ((float[])this.data.get(b))[13] = ((float[])this.data.get(b))[10];
                        ((float[])this.data.get(b))[16] = ((float[])this.data.get(b))[7];
                        ((float[])this.data.get(m))[61] = ((float[])this.data.get(m))[7];
                        ((float[])this.data.get(m))[64] = ((float[])this.data.get(m))[10];
                        ((float[])this.data.get(m))[40] = ((float[])this.data.get(m))[10];
                        ((float[])this.data.get(m))[25] = ((float[])this.data.get(m))[7];
                        ((float[])this.data.get(m))[13] = ((float[])this.data.get(m))[10];
                        ((float[])this.data.get(m))[16] = ((float[])this.data.get(m))[7];
                    }
                }
                m = this.PalsPerColumn;
                j = this.CoefficientCounterLTR[k - j * m - m - 1] + k;
                if ((j - 1) * 4 < this.data.size() && !this.BottomBalance.contains(Integer.valueOf(b))) {
                    this.BottomBalance.add(Integer.valueOf(b));
                    float f = (((float[])this.data.get(b))[58] + ((float[])this.data.get(j * 4 - 4))[58]) / 2.0F;
                    ArrayList<float[]> arrayList1 = this.data;
                    m = b + 1;
                    ((float[])arrayList1.get(m))[55] = f;
                    ((float[])this.data.get(m))[34] = f;
                    ((float[])this.data.get(m))[1] = f;
                    ((float[])this.data.get(m))[58] = f;
                    ((float[])this.data.get(m))[43] = f;
                    ((float[])this.data.get(m))[4] = f;
                    arrayList1 = this.data;
                    j = b + 3;
                    ((float[])arrayList1.get(j))[55] = f;
                    ((float[])this.data.get(j))[34] = f;
                    ((float[])this.data.get(j))[1] = f;
                    ((float[])this.data.get(j))[58] = f;
                    ((float[])this.data.get(j))[43] = f;
                    ((float[])this.data.get(j))[4] = f;
                    ((float[])this.data.get(m))[67] = ((float[])this.data.get(m))[1];
                    ((float[])this.data.get(m))[70] = ((float[])this.data.get(m))[4];
                    ((float[])this.data.get(m))[49] = ((float[])this.data.get(m))[1];
                    ((float[])this.data.get(m))[52] = ((float[])this.data.get(m))[4];
                    ((float[])this.data.get(m))[37] = ((float[])this.data.get(m))[4];
                    ((float[])this.data.get(m))[28] = ((float[])this.data.get(m))[1];
                    ((float[])this.data.get(j))[67] = ((float[])this.data.get(j))[1];
                    ((float[])this.data.get(j))[70] = ((float[])this.data.get(j))[4];
                    ((float[])this.data.get(j))[49] = ((float[])this.data.get(j))[1];
                    ((float[])this.data.get(j))[52] = ((float[])this.data.get(j))[4];
                    ((float[])this.data.get(j))[37] = ((float[])this.data.get(j))[4];
                    ((float[])this.data.get(j))[28] = ((float[])this.data.get(j))[1];
                }
            }
            j = i;
            if (k % this.PalsPerColumn == 0)
                j = i + 1;
            b += 4;
            i = j;
            j = k;
        }
        i = 0;
        j = 0;
        b = 0;
        while (b < this.data.size()) {
            arrayList.clear();
            int k = j + 1;
            arrayList.add(Float.valueOf(((float[])this.data.get(b))[7]));
            int m = i % 2;
            if (m == 0) {
                j = b - 2;
                if (j > 0 && (k - 1) % this.PalsPerColumn != 0)
                    arrayList.add(Float.valueOf(((float[])this.data.get(j))[10]));
            } else {
                j = b + 6;
                if (j < this.data.size())
                    arrayList.add(Float.valueOf(((float[])this.data.get(j))[10]));
            }
            if (m == 0) {
                j = this.PalsPerColumn;
                j = this.CoefficientCounterRTL[k - (i + 1) * j - j - 1];
            } else {
                j = this.PalsPerColumn;
                j = this.CoefficientCounterRTL[k - (i + 1) * j - j - 1];
            }
            j = k - j;
            if (j > 0) {
                int n = j * 4 - 4 + 1;
                if (n < this.data.size())
                    arrayList.add(Float.valueOf(((float[])this.data.get(n))[1]));
            }
            if (m == 0) {
                if (j > 0) {
                    int n = j * 4 - 4 + 7;
                    if (n < this.data.size())
                        arrayList.add(Float.valueOf(((float[])this.data.get(n))[4]));
                }
            } else if (j > 0) {
                int n = j * 4 - 4 - 1;
                if (n > 0)
                    arrayList.add(Float.valueOf(((float[])this.data.get(n))[4]));
            }
            if (arrayList.size() > 1) {
                float f = 0.0F;
                Iterator<Float> iterator = arrayList.iterator();
                while (iterator.hasNext())
                    f += ((Float)iterator.next()).floatValue();
                f /= arrayList.size();
                if (m == 0) {
                    int n = b - 2;
                    if (n > 0 && (k - 1) % this.PalsPerColumn != 0) {
                        ((float[])this.data.get(n))[46] = f;
                        ((float[])this.data.get(n))[10] = f;
                        ((float[])this.data.get(n))[19] = f;
                        ((float[])this.data.get(n))[64] = f;
                        ((float[])this.data.get(n))[40] = f;
                        ((float[])this.data.get(n))[13] = f;
                    }
                } else if (b + 4 < this.data.size()) {
                    ArrayList<float[]> arrayList1 = this.data;
                    int n = b + 6;
                    ((float[])arrayList1.get(n))[46] = f;
                    ((float[])this.data.get(n))[10] = f;
                    ((float[])this.data.get(n))[19] = f;
                    ((float[])this.data.get(n))[64] = f;
                    ((float[])this.data.get(n))[40] = f;
                    ((float[])this.data.get(n))[13] = f;
                }
                if (j > 0) {
                    int n = j * 4 - 4 + 1;
                    if (n < this.data.size()) {
                        ((float[])this.data.get(n))[1] = f;
                        ((float[])this.data.get(n))[34] = f;
                        ((float[])this.data.get(n))[55] = f;
                        ((float[])this.data.get(n))[67] = f;
                        ((float[])this.data.get(n))[49] = f;
                        ((float[])this.data.get(n))[28] = f;
                    }
                }
                if (j > 0) {
                    int n = j * 4 - 4;
                    j = n + 7;
                    if (j < this.data.size())
                        if (m == 0) {
                            ((float[])this.data.get(j))[4] = f;
                            ((float[])this.data.get(j))[43] = f;
                            ((float[])this.data.get(j))[58] = f;
                            ((float[])this.data.get(j))[70] = f;
                            ((float[])this.data.get(j))[37] = f;
                            ((float[])this.data.get(j))[52] = f;
                        } else {
                            j = n - 1;
                            if (j >= 0) {
                                ((float[])this.data.get(j))[4] = f;
                                ((float[])this.data.get(j))[43] = f;
                                ((float[])this.data.get(j))[58] = f;
                                ((float[])this.data.get(j))[70] = f;
                                ((float[])this.data.get(j))[37] = f;
                                ((float[])this.data.get(j))[52] = f;
                            }
                        }
                }
                ((float[])this.data.get(b))[7] = f;
                ((float[])this.data.get(b))[22] = f;
                ((float[])this.data.get(b))[31] = f;
                ((float[])this.data.get(b))[61] = f;
                ((float[])this.data.get(b))[25] = f;
                ((float[])this.data.get(b))[16] = f;
            }
            j = i;
            if (k % this.PalsPerColumn == 0)
                j = i + 1;
            b += 4;
            i = j;
            j = k;
        }
    }
    public class MyGLSurfaceView extends GLSurfaceView {
        private final float TOUCH_SCALE_FACTOR;
        private float previousX;
        private float previousY;

        public MyGLSurfaceView(Context context) {
            super(context);
            this.TOUCH_SCALE_FACTOR = 0.5625f;
            SurfaceDiagramActivity.this.renderer = new MyGLRenderer(context);
            setRenderer(SurfaceDiagramActivity.this.renderer);
            requestFocus();
            setFocusableInTouchMode(true);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            if (motionEvent.getAction() == 2) {
                SurfaceDiagramActivity.this.angleX += (y - this.previousY) * 0.5625f;
                SurfaceDiagramActivity.this.angleY += (x - this.previousX) * 0.5625f;
            }
            SurfaceDiagramActivity.this.zoom_in.setOnClickListener(new View.OnClickListener() { // from class: com.pourmami.hc05.Activities.diagrams.SurfaceDiagramActivity.MyGLSurfaceView.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    SurfaceDiagramActivity.this.FirstZfar += SurfaceDiagramActivity.this.IncreaseReduceUnit;
                    SurfaceDiagramActivity.this.glView.requestRender();
                }
            });
            SurfaceDiagramActivity.this.zoom_out.setOnClickListener(new View.OnClickListener() { // from class: com.pourmami.hc05.Activities.diagrams.SurfaceDiagramActivity.MyGLSurfaceView.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    SurfaceDiagramActivity.this.FirstZfar -= SurfaceDiagramActivity.this.IncreaseReduceUnit;
                    SurfaceDiagramActivity.this.glView.requestRender();
                }
            });
            SurfaceDiagramActivity.this.btn_move_right.setOnClickListener(new View.OnClickListener() { // from class: com.pourmami.hc05.Activities.diagrams.SurfaceDiagramActivity.MyGLSurfaceView.3
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    SurfaceDiagramActivity.this.graphX += 1.0f;
                    SurfaceDiagramActivity.this.glView.requestRender();
                }
            });
            SurfaceDiagramActivity.this.btn_move_left.setOnClickListener(new View.OnClickListener() { // from class: com.pourmami.hc05.Activities.diagrams.SurfaceDiagramActivity.MyGLSurfaceView.4
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    SurfaceDiagramActivity.this.graphX -= 1.0f;
                    SurfaceDiagramActivity.this.glView.requestRender();
                }
            });
            SurfaceDiagramActivity.this.btn_move_up.setOnClickListener(new View.OnClickListener() { // from class: com.pourmami.hc05.Activities.diagrams.SurfaceDiagramActivity.MyGLSurfaceView.5
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    SurfaceDiagramActivity.this.graphY += 1.0f;
                    SurfaceDiagramActivity.this.glView.requestRender();
                }
            });
            SurfaceDiagramActivity.this.btn_move_down.setOnClickListener(new View.OnClickListener() { // from class: com.pourmami.hc05.Activities.diagrams.SurfaceDiagramActivity.MyGLSurfaceView.6
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    SurfaceDiagramActivity.this.graphY -= 1.0f;
                    SurfaceDiagramActivity.this.glView.requestRender();
                }
            });
            this.previousX = x;
            this.previousY = y;
            return true;
        }
    }


    public class MyGLRenderer implements GLSurfaceView.Renderer {
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
            GLU.gluPerspective(gl10, 45.0f, (float)(i / i2), 0.1f, 100.0f);
            //gl10.glFrustumf(-(float)(i / i2), (float)(i / i2), -1, 1, 1.5f, 100);
            gl10.glMatrixMode(5888);
            gl10.glLoadIdentity();
        }

        @Override // android.opengl.GLSurfaceView.Renderer
        public void onDrawFrame(GL10 gl10) {
            gl10.glClear(16640);
            if (!SurfaceDiagramActivity.this.finalData.isEmpty() || !SurfaceDiagramActivity.this.texturedSurfaceShapes.isEmpty()|| !SurfaceDiagramActivity.this.texturedSurfaceShapes1.isEmpty()) {
                try {
                    if (SurfaceDiagramActivity.this.userWantsNumbers) {
                        Iterator<TexturedSurfaceShape> it = SurfaceDiagramActivity.this.texturedSurfaceShapes.iterator();
                        while (it.hasNext()) {
                            TexturedSurfaceShape next = it.next();
                            gl10.glLoadIdentity();
                            gl10.glTranslatef(SurfaceDiagramActivity.this.graphX, SurfaceDiagramActivity.this.graphY, SurfaceDiagramActivity.this.FirstZ);
                            gl10.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
                            gl10.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
                            try {
                                next.loadTexture(gl10, SurfaceDiagramActivity.this);
                                next.draw(gl10);
                            } catch (NullPointerException e) {
                                Log.e(SurfaceDiagramActivity.TAG, e.toString());
                            }
                        }
                    } else if(SurfaceDiagramActivity.this.userWantsDepth){
                        Iterator<TexturedSurfaceShape> it3 = SurfaceDiagramActivity.this.texturedSurfaceShapes1.iterator();
                       /* Iterator<SurfaceShape> it4 = SurfaceDiagramActivity.this.finaltwoDdata.iterator();
                        while (it4.hasNext()) {
                            //TexturedSurfaceShape next3 = it3.next();
                            SurfaceShape next4 = it4.next();
                            gl10.glLoadIdentity();
                            gl10.glTranslatef(SurfaceDiagramActivity.this.graphX, SurfaceDiagramActivity.this.graphY, SurfaceDiagramActivity.this.FirstZ);
                            gl10.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
                            gl10.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
                            try {
                                next4.draw(gl10);
                                //next3.loadTexture(gl10, SurfaceDiagramActivity.this);
                                //next3.draw(gl10);

                            } catch (NullPointerException e) {
                                Log.e(SurfaceDiagramActivity.TAG, e.toString());
                            }
                        }*/
                        while (it3.hasNext()) {
                            TexturedSurfaceShape next3 = it3.next();
                            //SurfaceShape next4 = it4.next();
                            gl10.glLoadIdentity();
                            gl10.glTranslatef(SurfaceDiagramActivity.this.graphX, SurfaceDiagramActivity.this.graphY, SurfaceDiagramActivity.this.FirstZ);
                            gl10.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
                            gl10.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
                            try {
                                //next4.draw(gl10);
                                next3.loadTexture(gl10, SurfaceDiagramActivity.this);
                                next3.draw(gl10);

                            } catch (NullPointerException e) {
                                Log.e(SurfaceDiagramActivity.TAG, e.toString());
                            }
                        }
                    } else {
                        Iterator<SurfaceShape> it2 = SurfaceDiagramActivity.this.finalData.iterator();
                        while (it2.hasNext()) {
                            SurfaceShape next2 = it2.next();
                            gl10.glLoadIdentity();
                            if (!SurfaceDiagramActivity.this.is_Finished) {
                                gl10.glTranslatef(SurfaceDiagramActivity.this.graphX, SurfaceDiagramActivity.this.graphY, SurfaceDiagramActivity.this.FirstZ);
                                gl10.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
                                gl10.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
                            } else {
                                gl10.glTranslatef(SurfaceDiagramActivity.this.graphX, SurfaceDiagramActivity.this.graphY, SurfaceDiagramActivity.this.FirstZfar);
                                gl10.glRotatef(SurfaceDiagramActivity.this.angleX, 1.0f, 0.0f, 0.0f);
                                gl10.glRotatef(SurfaceDiagramActivity.this.angleY, 0.0f, 1.0f, 0.0f);
                            }
                            try {
                                next2.draw(gl10);
                            } catch (NullPointerException e2) {
                                Log.e(SurfaceDiagramActivity.TAG, e2.toString());
                            }
                        }
                    }
                } catch (ConcurrentModificationException unused) {
                }
            }
            SurfaceDiagramActivity.this.angleX += SurfaceDiagramActivity.this.speedX;
            SurfaceDiagramActivity.this.angleY += SurfaceDiagramActivity.this.speedY;
        }
    }
}
