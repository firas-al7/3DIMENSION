package com.dimension.a3dimension.graphics.Activities.diagrams;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.dimension.a3dimension.MyProgressDialog;
import com.dimension.a3dimension.graphics.SharedPref;
import com.dimension.a3dimension.graphics.consts;
import com.dimension.a3dimension.R;
import com.dimension.a3dimension.graphics.SurfaceShape;
import com.dimension.a3dimension.models.Alerts;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import kotlin.jvm.internal.Intrinsics;


public class WaterFallDiagramActivity extends AppCompatActivity {
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final String BLUETOOTH_DEVICE_NAME = new consts().getBLUETOOTH_DEVICE_NAME();
    private static final String TAG = "BluetoothConnectionServ";
    private static final String appName = "MYAPP";
    int SleepTime;
    private final float sensor_factor = 0.244f;
    Button btn_startStop;
    ImageView btn_return_waterfall;
    private UUID deviceUUID;
    private GLSurfaceView glView;
    BluetoothDevice mBTDevice;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    Context mContext;
    private AcceptThread mInsecureAcceptThread;
    MyProgressDialog mProgressDialog;
    LinearLayout main_2d_surface;
    private BluetoothDevice mmDevice;
    MyGLRenderer renderer;
    float x;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    String IntPattern = "-?\\d+";
    boolean is_running = false;
    boolean ThreadRunning = false;
    boolean is_Finished = false;
    boolean doubleBackToExitPressedOnce = false;
    float z = 9.0f;
    float MinX = -0.0f;
    float zyIncrease = 0.5f;
    float minColor = -1.0f;
    float maxColor = -1.0f;
    float translationY = 7.6f;
    ArrayList<SurfaceShape> finalData = new ArrayList<>();
    ArrayList<float[]> data = new ArrayList<>();
    ArrayList<float[]> twoDdata = new ArrayList<>();
    ArrayList<float[]> colors = new ArrayList<>();

    SharedPref sp;

    SharedPreferences.Editor editor;

    //private UsbCommunicationThread mUsbCommunicationThread;

    int usbProductId;
    int usbVendorId;
    UsbDeviceConnection connection;
    UsbManager manager;
    UsbDevice device;
    boolean hasUsbPermission;
    UsbSerialDevice serialPort;
    //UsbSerialPort port;

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
                    }
                }
            }
        }
    };
    @Override
    // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_water_fall_diagram);
        this.btn_return_waterfall = (ImageView) findViewById(R.id.btn_return_waterfall);
        String bluetooth = SharedPref.getPreferences(WaterFallDiagramActivity.this).getStringData("Bluetooth","none");
        String otg = SharedPref.getPreferences(WaterFallDiagramActivity.this).getStringData("OTG","none");
        if (Intrinsics.areEqual(bluetooth, "on")||Intrinsics.areEqual(otg, "on")) {
            bindAndPrepare();
            Listeners();
        } else {
            this.btn_return_waterfall.setOnClickListener(new View.OnClickListener() {
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            Alerts.show_alert(WaterFallDiagramActivity.this,"Connection Error!","Please choose a connection Method");
        }
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        if (this.doubleBackToExitPressedOnce) {
            super.onBackPressed();
            String bluetooth = SharedPref.getPreferences(WaterFallDiagramActivity.this).getStringData("Bluetooth","none");
            if (Intrinsics.areEqual(bluetooth, "on")) {
                try {
                    this.mConnectThread.cancel();
                    this.mConnectedThread.cancel();
                } catch (NullPointerException unused) {
                }
            }else {
                try {
                   if(serialPort!=null){serialPort.close();}


                } catch (NullPointerException unused){}
            }
            this.is_Finished = true;
            this.is_running = false;
            this.ThreadRunning = false;
            finish();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override // java.lang.Runnable
            public void run() {
                WaterFallDiagramActivity.this.doubleBackToExitPressedOnce = false;
            }
        }, 2000L);
    }

    private void Listeners() {
        this.btn_return_waterfall.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                onBackPressed();
            }
        });
        this.btn_startStop.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                String bluetooth = SharedPref.getPreferences(WaterFallDiagramActivity.this).getStringData("Bluetooth","none");

                if (!WaterFallDiagramActivity.this.is_running) {
                    Log.d(WaterFallDiagramActivity.TAG, "Running");
                    WaterFallDiagramActivity.this.is_running = true;
                    WaterFallDiagramActivity.this.btn_startStop.setText("Pause");

                    WaterFallDiagramActivity waterFallDiagramActivity = WaterFallDiagramActivity.this;
                    if (Intrinsics.areEqual(bluetooth, "on")) {
                        if(waterFallDiagramActivity.mBTDevice !=null) {
                            if (WaterFallDiagramActivity.this.ThreadRunning) {
                                return;
                            }

                            WaterFallDiagramActivity.this.start();
                            waterFallDiagramActivity.startClient(waterFallDiagramActivity.mBTDevice, WaterFallDiagramActivity.MY_UUID_INSECURE);
                            return;
                        }else {
                            //WaterFallDiagramActivity.this.btn_startStop.setText("Start");
                            //Toast.makeText(mContext, "Please Choose A Bluetooth Device From Settings", Toast.LENGTH_LONG).show();
                            View view1 = findViewById(R.id.main_2d_surface);
                            Snackbar snackbar = Snackbar.make(view1, "Please Choose A Bluetooth Device From Settings", Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction("OK", new View.OnClickListener() {
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
                                PendingIntent pi = PendingIntent.getBroadcast(WaterFallDiagramActivity.this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_MUTABLE);
                                WaterFallDiagramActivity.this.registerReceiver(usbReceiver, new IntentFilter(ACTION_USB_PERMISSION));
                                manager.requestPermission(device, pi);
                                return;
                            }


                        } else {
                            //Toast.makeText(WaterFallDiagramActivity.this,"Please Connect an OTG Device",Toast.LENGTH_SHORT).show();
                                WaterFallDiagramActivity.this.btn_startStop.setText("Start");
                                View view1 = findViewById(R.id.main_2d_surface);
                                    Snackbar snackbar = Snackbar.make(view1, "Please Connect an USB Device", Snackbar.LENGTH_INDEFINITE);
                                    snackbar.setAction("OK", new View.OnClickListener() {
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
                    }
                }
                Log.d(WaterFallDiagramActivity.TAG, "Pause Running");
                WaterFallDiagramActivity.this.is_running = false;
                WaterFallDiagramActivity.this.ThreadRunning = false;
                WaterFallDiagramActivity.this.btn_startStop.setText("Start");
                if (Intrinsics.areEqual(bluetooth, "on")) {
                    if (mConnectedThread != null) {
                        WaterFallDiagramActivity.this.mConnectedThread.cancel();
                        Log.d(WaterFallDiagramActivity.TAG, "mConnectedThread Canceled");
                    }

                } else {

                    if (serialPort != null){
                        serialPort.close();
                        Log.d("USB", "Closing Serial Port");
                    }
                }

            }
        });
    }

    public void startUsbConnection(UsbDevice usbdevice) {
        Log.d("usbConnection", "startUsbConnection: Started.");
        //this.mProgressDialog = ProgressDialog.show(this.mContext, "Connecting Bluetooth", "Please Wait...", true);
        /*mProgressDialog = new MyProgressDialog(this.mContext);
        mProgressDialog.show();*/
        UsbConnectionThread usbConnectionThread = new UsbConnectionThread(usbdevice);
        usbConnectionThread.start();
    }
    public class UsbConnectionThread extends Thread  {

        public UsbConnectionThread(UsbDevice d){
            Log.d("usbConnection", "UsbConnectionThread Started ");
            Log.d("usbConnection", "UsbDevice d= "+d);
            Log.d("usbConnection", "manager= "+manager);
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

                            View view = findViewById(R.id.main_2d_surface);
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

                //finish();
                runOnUiThread(new Runnable() {
                    @Override // java.lang.Runnable
                    public void run() {

                        View view = findViewById(R.id.main_2d_surface);
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
            while(true) {
                try {
                int n = serialPort.syncRead(buffer, 0);
                if (n > 0) {
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
                                val_uT = (int) ((Integer.valueOf(Integer.parseInt(String.valueOf(sb).trim()))) *sensor_factor);
                                arrayList.add(val_uT);
                            } catch (NumberFormatException unused) {
                            }
                            Log.d("result", sb.toString());
                            arrayList2.clear();
                        }
                    }
                    double currentTimeMillis2 = System.currentTimeMillis();
                    Double.isNaN(currentTimeMillis2);
                    if (currentTimeMillis2 - currentTimeMillis >= WaterFallDiagramActivity.this.SleepTime) {
                        currentTimeMillis = System.currentTimeMillis();
                        WaterFallDiagramActivity.this.getAverage(arrayList);
                        arrayList.clear();
                    }

                }
            } catch (NullPointerException e) {
                    Log.e(WaterFallDiagramActivity.TAG, "write: Error reading Input Stream. " + e.getMessage());
                    String bluetooth = SharedPref.getPreferences(WaterFallDiagramActivity.this).getStringData("Bluetooth","none");
                    if(WaterFallDiagramActivity.this.ThreadRunning) {
                        runOnUiThread(new Runnable() {
                            @Override // java.lang.Runnable
                            public void run() {
                                //makeTune.cancel();
                                View view = findViewById(R.id.main_2d_surface);
                                Snackbar snackbar = Snackbar.make(view, "Please Check Your Connection", Snackbar.LENGTH_INDEFINITE);
                                snackbar.setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.d(WaterFallDiagramActivity.TAG, "Pause Running");
                                        WaterFallDiagramActivity.this.is_running = false;
                                        WaterFallDiagramActivity.this.ThreadRunning = false;
                                        WaterFallDiagramActivity.this.btn_startStop.setText("Start");
                                        if (Intrinsics.areEqual(bluetooth, "on")) {
                                            if (mConnectedThread != null) {
                                                WaterFallDiagramActivity.this.mConnectedThread.cancel();
                                                Log.d(WaterFallDiagramActivity.TAG, "mConnectedThread Canceled");
                                            }

                                        } else {

                                            if (serialPort != null) {
                                                WaterFallDiagramActivity.this.serialPort.close();
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
    private void bindAndPrepare() {
        this.mContext = this;
        this.manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        this.main_2d_surface = (LinearLayout) findViewById(R.id.main_2d_surface);
        //this.btn_return_waterfall = (ImageView) findViewById(R.id.btn_return_waterfall);
        this.btn_startStop = (Button) findViewById(R.id.btn_2d_surface_StartStop);
        this.usbProductId= Integer.parseInt(SharedPref.getPreferences(getApplicationContext()).getStringData("UsbProductId","1234"));
        this.usbVendorId = Integer.parseInt(SharedPref.getPreferences(getApplicationContext()).getStringData("UsbVendorId","1234"));
        Bundle extras = getIntent().getExtras();
        //this.mBTDevice = (BluetoothDevice) extras.get("mBTDevice");
        String bluetooth = SharedPref.getPreferences(WaterFallDiagramActivity.this).getStringData("Bluetooth", "none");


        if (Intrinsics.areEqual(bluetooth, "on")){
            if (ContextCompat.checkSelfPermission(WaterFallDiagramActivity.this,"android.permission.BLUETOOTH_CONNECT") != 0 || ContextCompat.checkSelfPermission(WaterFallDiagramActivity.this, "android.permission.BLUETOOTH_ADMIN") != 0 || ContextCompat.checkSelfPermission(WaterFallDiagramActivity.this, "android.permission.ACCESS_FINE_LOCATION") != 0 || ContextCompat.checkSelfPermission(WaterFallDiagramActivity.this, "android.permission.ACCESS_COARSE_LOCATION") != 0) {
                requestPermissions(new String[]{"android.permission.BLUETOOTH_CONNECT",  "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, 200);
                //return;
            }
        Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
        if (mBluetoothAdapter.isEnabled()) {
            if (bondedDevices != null) {
                Log.d(WaterFallDiagramActivity.TAG, "Shered preferences = "+ SharedPref.getPreferences(WaterFallDiagramActivity.this).getStringData("Devicename","none"));

                for (BluetoothDevice bluetoothDevice : bondedDevices) {
                    Log.d(WaterFallDiagramActivity.TAG, "bluetoothDevice = "+bluetoothDevice.getName());
                    if (Intrinsics.areEqual(bluetoothDevice.getName(), SharedPref.getPreferences(WaterFallDiagramActivity.this).getStringData("Devicename","none"))) {
                        Toast.makeText(this, "Paired with "+bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
                        this.mBTDevice = (BluetoothDevice) bluetoothDevice;

                    }
                }

            }else{
                Toast.makeText(this, "Please pair to a Bluetooth Device", Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(this, "Please Turn On The Bluetooth", Toast.LENGTH_LONG).show();
        }

        }
        this.renderer = new MyGLRenderer(this);
        MyGLSurfaceView myGLSurfaceView = new MyGLSurfaceView(this);
        this.glView = myGLSurfaceView;
        this.main_2d_surface.addView(this.glView);
        this.SleepTime = Integer.parseInt((String) extras.get("SleepTime"));
    }


    public void getAverage(ArrayList<Integer> arrayList) {
        Integer num = 0;
        if (arrayList.isEmpty()) {
            return;
        }
        Iterator<Integer> it = arrayList.iterator();
        while (it.hasNext()) {
            num = Integer.valueOf(num.intValue() + it.next().intValue());
        }
        final int intValue = num.intValue() / arrayList.size();
        Log.d(TAG, "Average :" + intValue);
        for (int i = 0; i < 1; i++) {
            float f = this.x;
            float f2 = intValue;
            float f3 = this.z;
            float f4 = this.zyIncrease;
            float[] fArr = {f, f2, f3, (3.0f * f4) + f, f2, f3, f, f2, f3, f + (3.0f * f4), f2, f3, f + (3.0f * f4), f2, f3 - (f4 / 2.0f), f, f2, f3 - (f4 / 2.0f), f + (3.0f * f4), f2, f3 - (f4 / 2.0f), f, f2, f3 - (f4 / 2.0f)};
            float[] fArr2 = {fArr[6], fArr[7], fArr[8], fArr[9], fArr[10], fArr[11], fArr[21], fArr[22], fArr[23], fArr[18], fArr[19], fArr[20], fArr[12], fArr[13], fArr[14], fArr[15], fArr[16], fArr[17], fArr[18], fArr[19], fArr[20], fArr[21], fArr[22], fArr[23], fArr[15], fArr[16], fArr[17], fArr[0], fArr[1], fArr[2], fArr[21], fArr[22], fArr[23], fArr[6], fArr[7], fArr[8], fArr[3], fArr[4], fArr[5], fArr[12], fArr[13], fArr[14], fArr[9], fArr[10], fArr[11], fArr[18], fArr[19], fArr[20], fArr[0], fArr[1], fArr[2], fArr[3], fArr[4], fArr[5], fArr[6], fArr[7], fArr[8], fArr[9], fArr[10], fArr[11], fArr[15], fArr[16], fArr[17], fArr[12], fArr[13], fArr[14], fArr[0], fArr[1], fArr[2], fArr[3], fArr[4], fArr[5]};
            float[] fArr3 = {fArr[6], 0.0f, fArr[8], fArr[9], 0.0f, fArr[11], fArr[21], 0.0f, fArr[23], fArr[18], 0.0f, fArr[20], fArr[12], 0.0f, fArr[14], fArr[15], 0.0f, fArr[17], fArr[18], 0.0f, fArr[20], fArr[21], 0.0f, fArr[23], fArr[15], 0.0f, fArr[17], fArr[0], 0.0f, fArr[2], fArr[21], 0.0f, fArr[23], fArr[6], 0.0f, fArr[8], fArr[3], 0.0f, fArr[5], fArr[12], 0.0f, fArr[14], fArr[9], 0.0f, fArr[11], fArr[18], 0.0f, fArr[20], fArr[0], 0.0f, fArr[2], fArr[3], 0.0f, fArr[5], fArr[6], 0.0f, fArr[8], fArr[9], 0.0f, fArr[11], fArr[15], 0.0f, fArr[17], fArr[12], 0.0f, fArr[14], fArr[0], 0.0f, fArr[2], fArr[3], 0.0f, fArr[5]};
            this.data.add(fArr2);
            this.twoDdata.add(fArr3);
            this.x += this.zyIncrease;
        }
        float f5 = this.translationY;
        float f6 = this.zyIncrease;
        this.translationY = f5 + (f6 / 2.0f);
        this.z += f6 / 2.0f;
        this.x = this.MinX;
        fromFirstBalancing();
        coloring();
        this.finalData.clear();
        for (int i2 = 0; i2 < this.data.size(); i2++) {
            this.finalData.add(new SurfaceShape(this.twoDdata.get(i2), this.colors.get(i2)));
        }
        runOnUiThread(new Runnable() {
            @Override // java.lang.Runnable
            public void run() {
                Button button = WaterFallDiagramActivity.this.btn_startStop;
                button.setText("Pause at " + intValue + " uT");
            }
        });
        if (this.twoDdata.size() > 8) {
            this.twoDdata.remove(0);
            this.data.remove(0);
            this.colors.remove(0);
        }
    }

    private void coloring() {
        this.colors.clear();
        for (int i = 0; i < this.data.size() - 1; i++) {
            if (this.minColor == -1.0f && this.maxColor == -1.0f) {
                this.minColor = this.data.get(i)[4];
                this.maxColor = this.data.get(i)[4];
            }
            if (this.minColor > this.data.get(i)[1]) {
                this.minColor = this.data.get(i)[1];
            }
            if (this.minColor > this.data.get(i)[4]) {
                this.minColor = this.data.get(i)[4];
            }
            if (this.minColor > this.data.get(i)[7]) {
                this.minColor = this.data.get(i)[7];
            }
            if (this.minColor > this.data.get(i)[10]) {
                this.minColor = this.data.get(i)[10];
            }
            if (this.maxColor < this.data.get(i)[1]) {
                this.maxColor = this.data.get(i)[1];
            }
            if (this.maxColor < this.data.get(i)[4]) {
                this.maxColor = this.data.get(i)[4];
            }
            if (this.maxColor < this.data.get(i)[7]) {
                this.maxColor = this.data.get(i)[7];
            }
            if (this.maxColor < this.data.get(i)[10]) {
                this.maxColor = this.data.get(i)[10];
            }
        }
        float f = (this.maxColor - this.minColor) / 10.0f;
        for (int i2 = 0; i2 < this.data.size(); i2++) {
            float[] fArr = new float[96];
            for (int i3 = 16; i3 <= 79; i3++) {
                fArr[i3] = 0.0f;
            }
            if (this.data.get(i2)[1] == this.minColor) {
                fArr[0] = 0.0f;
                fArr[1] = 0.0f;
                fArr[2] = 1.0f;
                fArr[3] = 1.0f;
                fArr[88] = 0.0f;
                fArr[89] = 0.0f;
                fArr[90] = 1.0f;
                fArr[91] = 1.0f;
            } else if (this.data.get(i2)[1] > this.minColor && this.data.get(i2)[1] < this.minColor + f) {
                fArr[0] = 0.0f;
                fArr[1] = 0.1f;
                fArr[2] = 0.9f;
                fArr[3] = 1.0f;
                fArr[88] = 0.0f;
                fArr[89] = 0.1f;
                fArr[90] = 0.9f;
                fArr[91] = 1.0f;
            } else if (this.data.get(i2)[1] == this.minColor + f) {
                fArr[0] = 0.0f;
                fArr[1] = 0.2f;
                fArr[2] = 0.8f;
                fArr[3] = 1.0f;
                fArr[88] = 0.0f;
                fArr[89] = 0.2f;
                fArr[90] = 0.8f;
                fArr[91] = 1.0f;
            } else if (this.data.get(i2)[1] <= this.minColor + f || this.data.get(i2)[1] >= this.minColor + (2.0f * f)) {
                float f2 = 2.0f * f;
                if (this.data.get(i2)[1] == this.minColor + f2) {
                    fArr[0] = 0.0f;
                    fArr[1] = 0.4f;
                    fArr[2] = 0.6f;
                    fArr[3] = 1.0f;
                    fArr[88] = 0.0f;
                    fArr[89] = 0.4f;
                    fArr[90] = 0.6f;
                    fArr[91] = 1.0f;
                } else if (this.data.get(i2)[1] <= this.minColor + f2 || this.data.get(i2)[1] >= this.minColor + (3.0f * f)) {
                    float f3 = 3.0f * f;
                    if (this.data.get(i2)[1] == this.minColor + f3) {
                        fArr[0] = 0.0f;
                        fArr[1] = 0.6f;
                        fArr[2] = 0.4f;
                        fArr[3] = 1.0f;
                        fArr[88] = 0.0f;
                        fArr[89] = 0.6f;
                        fArr[90] = 0.4f;
                        fArr[91] = 1.0f;
                    } else if (this.data.get(i2)[1] <= this.minColor + f3 || this.data.get(i2)[1] >= this.minColor + (4.0f * f)) {
                        float f4 = 4.0f * f;
                        if (this.data.get(i2)[1] == this.minColor + f4) {
                            fArr[0] = 0.0f;
                            fArr[1] = 0.8f;
                            fArr[2] = 0.2f;
                            fArr[3] = 1.0f;
                            fArr[88] = 0.0f;
                            fArr[89] = 0.8f;
                            fArr[90] = 0.2f;
                            fArr[91] = 1.0f;
                        } else if (this.data.get(i2)[1] <= this.minColor + f4 || this.data.get(i2)[1] >= this.minColor + (5.0f * f)) {
                            float f5 = 5.0f * f;
                            if (this.data.get(i2)[1] == this.minColor + f5) {
                                fArr[0] = 0.0f;
                                fArr[1] = 1.0f;
                                fArr[2] = 0.0f;
                                fArr[3] = 1.0f;
                                fArr[88] = 0.0f;
                                fArr[89] = 1.0f;
                                fArr[90] = 0.0f;
                                fArr[91] = 1.0f;
                            } else if (this.data.get(i2)[1] <= this.minColor + f5 || this.data.get(i2)[1] >= this.minColor + (6.0f * f)) {
                                float f6 = 6.0f * f;
                                if (this.data.get(i2)[1] == this.minColor + f6) {
                                    fArr[0] = 0.2f;
                                    fArr[1] = 0.8f;
                                    fArr[2] = 0.0f;
                                    fArr[3] = 1.0f;
                                    fArr[88] = 0.2f;
                                    fArr[89] = 0.8f;
                                    fArr[90] = 0.0f;
                                    fArr[91] = 1.0f;
                                } else if (this.data.get(i2)[1] <= this.minColor + f6 || this.data.get(i2)[1] >= this.minColor + (7.0f * f)) {
                                    float f7 = 7.0f * f;
                                    if (this.data.get(i2)[1] == this.minColor + f7) {
                                        fArr[0] = 0.4f;
                                        fArr[1] = 0.6f;
                                        fArr[2] = 0.0f;
                                        fArr[3] = 1.0f;
                                        fArr[88] = 0.4f;
                                        fArr[89] = 0.6f;
                                        fArr[90] = 0.0f;
                                        fArr[91] = 1.0f;
                                    } else if (this.data.get(i2)[1] <= this.minColor + f7 || this.data.get(i2)[1] >= this.minColor + (8.0f * f)) {
                                        float f8 = 8.0f * f;
                                        if (this.data.get(i2)[1] == this.minColor + f8) {
                                            fArr[0] = 0.6f;
                                            fArr[1] = 0.4f;
                                            fArr[2] = 0.0f;
                                            fArr[3] = 1.0f;
                                            fArr[88] = 0.6f;
                                            fArr[89] = 0.4f;
                                            fArr[90] = 0.0f;
                                            fArr[91] = 1.0f;
                                        } else if (this.data.get(i2)[1] <= this.minColor + f8 || this.data.get(i2)[1] >= this.minColor + (9.0f * f)) {
                                            float f9 = 9.0f * f;
                                            if (this.data.get(i2)[1] == this.minColor + f9) {
                                                fArr[0] = 0.8f;
                                                fArr[1] = 0.2f;
                                                fArr[2] = 0.0f;
                                                fArr[3] = 1.0f;
                                                fArr[88] = 0.8f;
                                                fArr[89] = 0.2f;
                                                fArr[90] = 0.0f;
                                                fArr[91] = 1.0f;
                                            } else if (this.data.get(i2)[1] > this.minColor + f9 && this.data.get(i2)[1] < this.maxColor) {
                                                fArr[0] = 0.9f;
                                                fArr[1] = 0.1f;
                                                fArr[2] = 0.0f;
                                                fArr[3] = 1.0f;
                                                fArr[88] = 0.9f;
                                                fArr[89] = 0.1f;
                                                fArr[90] = 0.0f;
                                                fArr[91] = 1.0f;
                                            } else if (this.data.get(i2)[1] == this.maxColor) {
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
            if (this.data.get(i2)[4] == this.minColor) {
                fArr[4] = 0.0f;
                fArr[5] = 0.0f;
                fArr[6] = 1.0f;
                fArr[7] = 1.0f;
                fArr[92] = 0.0f;
                fArr[93] = 0.0f;
                fArr[94] = 1.0f;
                fArr[95] = 1.0f;
            } else if (this.data.get(i2)[4] > this.minColor && this.data.get(i2)[4] < this.minColor + f) {
                fArr[4] = 0.0f;
                fArr[5] = 0.1f;
                fArr[6] = 0.9f;
                fArr[7] = 1.0f;
                fArr[92] = 0.0f;
                fArr[93] = 0.1f;
                fArr[94] = 0.9f;
                fArr[95] = 1.0f;
            } else if (this.data.get(i2)[4] == this.minColor + f) {
                fArr[4] = 0.0f;
                fArr[5] = 0.2f;
                fArr[6] = 0.8f;
                fArr[7] = 1.0f;
                fArr[92] = 0.0f;
                fArr[93] = 0.2f;
                fArr[94] = 0.8f;
                fArr[95] = 1.0f;
            } else if (this.data.get(i2)[4] <= this.minColor + f || this.data.get(i2)[4] >= this.minColor + (2.0f * f)) {
                float f10 = 2.0f * f;
                if (this.data.get(i2)[4] == this.minColor + f10) {
                    fArr[4] = 0.0f;
                    fArr[5] = 0.4f;
                    fArr[6] = 0.6f;
                    fArr[7] = 1.0f;
                    fArr[92] = 0.0f;
                    fArr[93] = 0.4f;
                    fArr[94] = 0.6f;
                    fArr[95] = 1.0f;
                } else if (this.data.get(i2)[4] <= this.minColor + f10 || this.data.get(i2)[4] >= this.minColor + (3.0f * f)) {
                    float f11 = 3.0f * f;
                    if (this.data.get(i2)[4] == this.minColor + f11) {
                        fArr[4] = 0.0f;
                        fArr[5] = 0.6f;
                        fArr[6] = 0.4f;
                        fArr[7] = 1.0f;
                        fArr[92] = 0.0f;
                        fArr[93] = 0.6f;
                        fArr[94] = 0.4f;
                        fArr[95] = 1.0f;
                    } else if (this.data.get(i2)[4] <= this.minColor + f11 || this.data.get(i2)[4] >= this.minColor + (4.0f * f)) {
                        float f12 = 4.0f * f;
                        if (this.data.get(i2)[4] == this.minColor + f12) {
                            fArr[4] = 0.0f;
                            fArr[5] = 0.8f;
                            fArr[6] = 0.2f;
                            fArr[7] = 1.0f;
                            fArr[92] = 0.0f;
                            fArr[93] = 0.8f;
                            fArr[94] = 0.2f;
                            fArr[95] = 1.0f;
                        } else if (this.data.get(i2)[4] <= this.minColor + f12 || this.data.get(i2)[4] >= this.minColor + (5.0f * f)) {
                            float f13 = 5.0f * f;
                            if (this.data.get(i2)[4] == this.minColor + f13) {
                                fArr[4] = 0.0f;
                                fArr[5] = 1.0f;
                                fArr[6] = 0.0f;
                                fArr[7] = 1.0f;
                                fArr[92] = 0.0f;
                                fArr[93] = 1.0f;
                                fArr[94] = 0.0f;
                                fArr[95] = 1.0f;
                            } else if (this.data.get(i2)[4] <= this.minColor + f13 || this.data.get(i2)[4] >= this.minColor + (6.0f * f)) {
                                float f14 = 6.0f * f;
                                if (this.data.get(i2)[4] == this.minColor + f14) {
                                    fArr[4] = 0.2f;
                                    fArr[5] = 0.8f;
                                    fArr[6] = 0.0f;
                                    fArr[7] = 1.0f;
                                    fArr[92] = 0.2f;
                                    fArr[93] = 0.8f;
                                    fArr[94] = 0.0f;
                                    fArr[95] = 1.0f;
                                } else if (this.data.get(i2)[4] <= this.minColor + f14 || this.data.get(i2)[4] >= this.minColor + (7.0f * f)) {
                                    float f15 = 7.0f * f;
                                    if (this.data.get(i2)[4] == this.minColor + f15) {
                                        fArr[4] = 0.4f;
                                        fArr[5] = 0.6f;
                                        fArr[6] = 0.0f;
                                        fArr[7] = 1.0f;
                                        fArr[92] = 0.4f;
                                        fArr[93] = 0.6f;
                                        fArr[94] = 0.0f;
                                        fArr[95] = 1.0f;
                                    } else if (this.data.get(i2)[4] <= this.minColor + f15 || this.data.get(i2)[4] >= this.minColor + (8.0f * f)) {
                                        float f16 = 8.0f * f;
                                        if (this.data.get(i2)[4] == this.minColor + f16) {
                                            fArr[4] = 0.6f;
                                            fArr[5] = 0.4f;
                                            fArr[6] = 0.0f;
                                            fArr[7] = 1.0f;
                                            fArr[92] = 0.6f;
                                            fArr[93] = 0.4f;
                                            fArr[94] = 0.0f;
                                            fArr[95] = 1.0f;
                                        } else if (this.data.get(i2)[4] <= this.minColor + f16 || this.data.get(i2)[4] >= this.minColor + (9.0f * f)) {
                                            float f17 = 9.0f * f;
                                            if (this.data.get(i2)[4] == this.minColor + f17) {
                                                fArr[4] = 0.8f;
                                                fArr[5] = 0.2f;
                                                fArr[6] = 0.0f;
                                                fArr[7] = 1.0f;
                                                fArr[92] = 0.8f;
                                                fArr[93] = 0.2f;
                                                fArr[94] = 0.0f;
                                                fArr[95] = 1.0f;
                                            } else if (this.data.get(i2)[4] > this.minColor + f17 && this.data.get(i2)[4] < this.maxColor) {
                                                fArr[4] = 0.9f;
                                                fArr[5] = 0.1f;
                                                fArr[6] = 0.0f;
                                                fArr[7] = 1.0f;
                                                fArr[92] = 0.9f;
                                                fArr[93] = 0.1f;
                                                fArr[94] = 0.0f;
                                                fArr[95] = 1.0f;
                                            } else if (this.data.get(i2)[4] == this.maxColor) {
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
            if (this.data.get(i2)[7] == this.minColor) {
                fArr[8] = 0.0f;
                fArr[9] = 0.0f;
                fArr[10] = 1.0f;
                fArr[11] = 1.0f;
                fArr[80] = 0.0f;
                fArr[81] = 0.0f;
                fArr[82] = 1.0f;
                fArr[83] = 1.0f;
            } else if (this.data.get(i2)[7] > this.minColor && this.data.get(i2)[7] < this.minColor + f) {
                fArr[8] = 0.0f;
                fArr[9] = 0.1f;
                fArr[10] = 0.9f;
                fArr[11] = 1.0f;
                fArr[80] = 0.0f;
                fArr[81] = 0.1f;
                fArr[82] = 0.9f;
                fArr[83] = 1.0f;
            } else if (this.data.get(i2)[7] == this.minColor + f) {
                fArr[8] = 0.0f;
                fArr[9] = 0.2f;
                fArr[10] = 0.8f;
                fArr[11] = 1.0f;
                fArr[80] = 0.0f;
                fArr[81] = 0.2f;
                fArr[82] = 0.8f;
                fArr[83] = 1.0f;
            } else if (this.data.get(i2)[7] <= this.minColor + f || this.data.get(i2)[7] >= this.minColor + (2.0f * f)) {
                float f18 = 2.0f * f;
                if (this.data.get(i2)[7] == this.minColor + f18) {
                    fArr[8] = 0.0f;
                    fArr[9] = 0.4f;
                    fArr[10] = 0.6f;
                    fArr[11] = 1.0f;
                    fArr[80] = 0.0f;
                    fArr[81] = 0.4f;
                    fArr[82] = 0.6f;
                    fArr[83] = 1.0f;
                } else if (this.data.get(i2)[7] <= this.minColor + f18 || this.data.get(i2)[7] >= this.minColor + (3.0f * f)) {
                    float f19 = 3.0f * f;
                    if (this.data.get(i2)[7] == this.minColor + f19) {
                        fArr[8] = 0.0f;
                        fArr[9] = 0.6f;
                        fArr[10] = 0.4f;
                        fArr[11] = 1.0f;
                        fArr[80] = 0.0f;
                        fArr[81] = 0.6f;
                        fArr[82] = 0.4f;
                        fArr[83] = 1.0f;
                    } else if (this.data.get(i2)[7] <= this.minColor + f19 || this.data.get(i2)[7] >= this.minColor + (4.0f * f)) {
                        float f20 = 4.0f * f;
                        if (this.data.get(i2)[7] == this.minColor + f20) {
                            fArr[8] = 0.0f;
                            fArr[9] = 0.8f;
                            fArr[10] = 0.2f;
                            fArr[11] = 1.0f;
                            fArr[80] = 0.0f;
                            fArr[81] = 0.8f;
                            fArr[82] = 0.2f;
                            fArr[83] = 1.0f;
                        } else if (this.data.get(i2)[7] <= this.minColor + f20 || this.data.get(i2)[7] >= this.minColor + (5.0f * f)) {
                            float f21 = 5.0f * f;
                            if (this.data.get(i2)[7] == this.minColor + f21) {
                                fArr[8] = 0.0f;
                                fArr[9] = 1.0f;
                                fArr[10] = 0.0f;
                                fArr[11] = 1.0f;
                                fArr[80] = 0.0f;
                                fArr[81] = 1.0f;
                                fArr[82] = 0.0f;
                                fArr[83] = 1.0f;
                            } else if (this.data.get(i2)[7] <= this.minColor + f21 || this.data.get(i2)[7] >= this.minColor + (6.0f * f)) {
                                float f22 = 6.0f * f;
                                if (this.data.get(i2)[7] == this.minColor + f22) {
                                    fArr[8] = 0.2f;
                                    fArr[9] = 0.8f;
                                    fArr[10] = 0.0f;
                                    fArr[11] = 1.0f;
                                    fArr[80] = 0.2f;
                                    fArr[81] = 0.8f;
                                    fArr[82] = 0.0f;
                                    fArr[83] = 1.0f;
                                } else if (this.data.get(i2)[7] <= this.minColor + f22 || this.data.get(i2)[7] >= this.minColor + (7.0f * f)) {
                                    float f23 = 7.0f * f;
                                    if (this.data.get(i2)[7] == this.minColor + f23) {
                                        fArr[8] = 0.4f;
                                        fArr[9] = 0.6f;
                                        fArr[10] = 0.0f;
                                        fArr[11] = 1.0f;
                                        fArr[80] = 0.4f;
                                        fArr[81] = 0.6f;
                                        fArr[82] = 0.0f;
                                        fArr[83] = 1.0f;
                                    } else if (this.data.get(i2)[7] <= this.minColor + f23 || this.data.get(i2)[7] >= this.minColor + (8.0f * f)) {
                                        float f24 = 8.0f * f;
                                        if (this.data.get(i2)[7] == this.minColor + f24) {
                                            fArr[8] = 0.6f;
                                            fArr[9] = 0.4f;
                                            fArr[10] = 0.0f;
                                            fArr[11] = 1.0f;
                                            fArr[80] = 0.6f;
                                            fArr[81] = 0.4f;
                                            fArr[82] = 0.0f;
                                            fArr[83] = 1.0f;
                                        } else if (this.data.get(i2)[7] <= this.minColor + f24 || this.data.get(i2)[7] >= this.minColor + (9.0f * f)) {
                                            float f25 = 9.0f * f;
                                            if (this.data.get(i2)[7] == this.minColor + f25) {
                                                fArr[8] = 0.8f;
                                                fArr[9] = 0.2f;
                                                fArr[10] = 0.0f;
                                                fArr[11] = 1.0f;
                                                fArr[80] = 0.8f;
                                                fArr[81] = 0.2f;
                                                fArr[82] = 0.0f;
                                                fArr[83] = 1.0f;
                                            } else if (this.data.get(i2)[7] > this.minColor + f25 && this.data.get(i2)[7] < this.maxColor) {
                                                fArr[8] = 0.9f;
                                                fArr[9] = 0.1f;
                                                fArr[10] = 0.0f;
                                                fArr[11] = 1.0f;
                                                fArr[80] = 0.9f;
                                                fArr[81] = 0.1f;
                                                fArr[82] = 0.0f;
                                                fArr[83] = 1.0f;
                                            } else if (this.data.get(i2)[7] == this.maxColor) {
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
            if (this.data.get(i2)[10] == this.minColor) {
                fArr[12] = 0.0f;
                fArr[13] = 0.0f;
                fArr[14] = 1.0f;
                fArr[15] = 1.0f;
                fArr[84] = 0.0f;
                fArr[85] = 0.0f;
                fArr[86] = 1.0f;
                fArr[87] = 1.0f;
            } else if (this.data.get(i2)[10] > this.minColor && this.data.get(i2)[10] < this.minColor + f) {
                fArr[12] = 0.0f;
                fArr[13] = 0.1f;
                fArr[14] = 0.9f;
                fArr[15] = 1.0f;
                fArr[84] = 0.0f;
                fArr[85] = 0.1f;
                fArr[86] = 0.9f;
                fArr[87] = 1.0f;
            } else if (this.data.get(i2)[10] == this.minColor + f) {
                fArr[12] = 0.0f;
                fArr[13] = 0.2f;
                fArr[14] = 0.8f;
                fArr[15] = 1.0f;
                fArr[84] = 0.0f;
                fArr[85] = 0.2f;
                fArr[86] = 0.8f;
                fArr[87] = 1.0f;
            } else if (this.data.get(i2)[10] <= this.minColor + f || this.data.get(i2)[10] >= this.minColor + (2.0f * f)) {
                float f26 = 2.0f * f;
                if (this.data.get(i2)[10] == this.minColor + f26) {
                    fArr[12] = 0.0f;
                    fArr[13] = 0.4f;
                    fArr[14] = 0.6f;
                    fArr[15] = 1.0f;
                    fArr[84] = 0.0f;
                    fArr[85] = 0.4f;
                    fArr[86] = 0.6f;
                    fArr[87] = 1.0f;
                } else if (this.data.get(i2)[10] <= this.minColor + f26 || this.data.get(i2)[10] >= this.minColor + (3.0f * f)) {
                    float f27 = 3.0f * f;
                    if (this.data.get(i2)[10] == this.minColor + f27) {
                        fArr[12] = 0.0f;
                        fArr[13] = 0.6f;
                        fArr[14] = 0.4f;
                        fArr[15] = 1.0f;
                        fArr[84] = 0.0f;
                        fArr[85] = 0.6f;
                        fArr[86] = 0.4f;
                        fArr[87] = 1.0f;
                    } else if (this.data.get(i2)[10] <= this.minColor + f27 || this.data.get(i2)[10] >= this.minColor + (4.0f * f)) {
                        float f28 = 4.0f * f;
                        if (this.data.get(i2)[10] == this.minColor + f28) {
                            fArr[12] = 0.0f;
                            fArr[13] = 0.8f;
                            fArr[14] = 0.2f;
                            fArr[15] = 1.0f;
                            fArr[84] = 0.0f;
                            fArr[85] = 0.8f;
                            fArr[86] = 0.2f;
                            fArr[87] = 1.0f;
                        } else if (this.data.get(i2)[10] <= this.minColor + f28 || this.data.get(i2)[10] >= this.minColor + (5.0f * f)) {
                            float f29 = 5.0f * f;
                            if (this.data.get(i2)[10] == this.minColor + f29) {
                                fArr[12] = 0.0f;
                                fArr[13] = 1.0f;
                                fArr[14] = 0.0f;
                                fArr[15] = 1.0f;
                                fArr[84] = 0.0f;
                                fArr[85] = 1.0f;
                                fArr[86] = 0.0f;
                                fArr[87] = 1.0f;
                            } else if (this.data.get(i2)[10] <= this.minColor + f29 || this.data.get(i2)[10] >= this.minColor + (6.0f * f)) {
                                float f30 = 6.0f * f;
                                if (this.data.get(i2)[10] == this.minColor + f30) {
                                    fArr[12] = 0.2f;
                                    fArr[13] = 0.8f;
                                    fArr[14] = 0.0f;
                                    fArr[15] = 1.0f;
                                    fArr[84] = 0.2f;
                                    fArr[85] = 0.8f;
                                    fArr[86] = 0.0f;
                                    fArr[87] = 1.0f;
                                } else if (this.data.get(i2)[10] <= this.minColor + f30 || this.data.get(i2)[10] >= this.minColor + (7.0f * f)) {
                                    float f31 = 7.0f * f;
                                    if (this.data.get(i2)[10] == this.minColor + f31) {
                                        fArr[12] = 0.4f;
                                        fArr[13] = 0.6f;
                                        fArr[14] = 0.0f;
                                        fArr[15] = 1.0f;
                                        fArr[84] = 0.4f;
                                        fArr[85] = 0.6f;
                                        fArr[86] = 0.0f;
                                        fArr[87] = 1.0f;
                                    } else if (this.data.get(i2)[10] <= this.minColor + f31 || this.data.get(i2)[10] >= this.minColor + (8.0f * f)) {
                                        float f32 = 8.0f * f;
                                        if (this.data.get(i2)[10] == this.minColor + f32) {
                                            fArr[12] = 0.6f;
                                            fArr[13] = 0.4f;
                                            fArr[14] = 0.0f;
                                            fArr[15] = 1.0f;
                                            fArr[84] = 0.6f;
                                            fArr[85] = 0.4f;
                                            fArr[86] = 0.0f;
                                            fArr[87] = 1.0f;
                                        } else if (this.data.get(i2)[10] <= this.minColor + f32 || this.data.get(i2)[10] >= this.minColor + (9.0f * f)) {
                                            float f33 = 9.0f * f;
                                            if (this.data.get(i2)[10] == this.minColor + f33) {
                                                fArr[12] = 0.8f;
                                                fArr[13] = 0.2f;
                                                fArr[14] = 0.0f;
                                                fArr[15] = 1.0f;
                                                fArr[84] = 0.8f;
                                                fArr[85] = 0.2f;
                                                fArr[86] = 0.0f;
                                                fArr[87] = 1.0f;
                                            } else if (this.data.get(i2)[10] > this.minColor + f33 && this.data.get(i2)[10] < this.maxColor) {
                                                fArr[12] = 0.9f;
                                                fArr[13] = 0.1f;
                                                fArr[14] = 0.0f;
                                                fArr[15] = 1.0f;
                                                fArr[84] = 0.9f;
                                                fArr[85] = 0.1f;
                                                fArr[86] = 0.0f;
                                                fArr[87] = 1.0f;
                                            } else if (this.data.get(i2)[10] == this.maxColor) {
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
    }

    private void fromFirstBalancing() {
        for (int i = 0; i < this.data.size(); i++) {
            if (i > 0) {
                int i2 = i - 1;
                float f = (this.data.get(i)[58] + this.data.get(i2)[58]) / 2.0f;
                this.data.get(i)[19] = f;
                this.data.get(i)[46] = f;
                this.data.get(i)[10] = f;
                this.data.get(i)[22] = f;
                this.data.get(i)[31] = f;
                this.data.get(i)[7] = f;
                this.data.get(i2)[4] = f;
                this.data.get(i2)[58] = f;
                this.data.get(i2)[43] = f;
                this.data.get(i2)[1] = f;
                this.data.get(i2)[34] = f;
                this.data.get(i2)[55] = f;
            }
        }
    }

    public class MyGLSurfaceView extends GLSurfaceView {
        public MyGLSurfaceView(Context context) {
            super(context);
            WaterFallDiagramActivity.this.renderer = new MyGLRenderer(context);
            setRenderer(WaterFallDiagramActivity.this.renderer);
            requestFocus();
            setFocusableInTouchMode(true);
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
            if (i == 0) {
                i2 = 1;
            }
            gl10.glViewport(0, 0, i, i2);
            // for a fixed camera, set the projection too
            float ratio = (float) i / i2;
            gl10.glMatrixMode(GL10.GL_PROJECTION);
            gl10.glLoadIdentity();
            gl10.glFrustumf(-ratio, ratio, -1, 1, 1.5f, 100);
            gl10.glMatrixMode(5888);
            gl10.glLoadIdentity();
           /* gl10.glViewport(0, 0, i, i2);
            gl10.glMatrixMode(5889);
            gl10.glLoadIdentity();
            GLU.gluPerspective(gl10, 45.0f, (float)(i / i2), 0.1f, 100.0f);
            gl10.glMatrixMode(5888);
            gl10.glLoadIdentity();
            gl10.glFrustumf(-(i / i2), (i / i2), -1, 1, 1, 10);*/
        }

        @Override // android.opengl.GLSurfaceView.Renderer
        public void onDrawFrame(GL10 gl10) {
            gl10.glClear(16640);
            if (WaterFallDiagramActivity.this.finalData.isEmpty()) {
                return;
            }
            try {
                Iterator<SurfaceShape> it = WaterFallDiagramActivity.this.finalData.iterator();
                while (it.hasNext()) {
                    SurfaceShape next = it.next();
                    gl10.glLoadIdentity();
                    gl10.glTranslatef(-0.6f, WaterFallDiagramActivity.this.translationY, -2.75f);
                    //gl10.glTranslatef(WaterFallDiagramActivity.this.translationY, -0.6f, -2.75f);
                    gl10.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
                    gl10.glRotatef(0.0f, 0.0f, 1.0f, 0.0f);
                    //gl10.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
                    //gl10.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                    //gl10.glRotatef(-180.0f, 1.0f, 0.0f, 0.0f);
                    //gl10.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                    try {

                        next.draw(gl10);
                    } catch (NullPointerException e) {
                        Log.e(WaterFallDiagramActivity.TAG, e.toString());
                    }
                }
            } catch (ConcurrentModificationException unused) {
            }
        }
    }

    public class AcceptThread extends Thread {
        private BluetoothServerSocket mmServerSocket =null;

        public AcceptThread() {
            BluetoothServerSocket bluetoothServerSocket = null;
            try {
                if (ContextCompat.checkSelfPermission(WaterFallDiagramActivity.this, "android.permission.BLUETOOTH_CONNECT") != 0  || ContextCompat.checkSelfPermission(WaterFallDiagramActivity.this, "android.permission.BLUETOOTH_SCAN") != 0 || ContextCompat.checkSelfPermission(WaterFallDiagramActivity.this, "android.permission.ACCESS_FINE_LOCATION") != 0 || ContextCompat.checkSelfPermission(WaterFallDiagramActivity.this, "android.permission.ACCESS_COARSE_LOCATION") != 0) {
                    WaterFallDiagramActivity.this.requestPermissions(new String[]{"android.permission.BLUETOOTH_CONNECT", "android.permission.BLUETOOTH_CONNECT", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, 200);
                    return;
                }
                bluetoothServerSocket = WaterFallDiagramActivity.this.mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(WaterFallDiagramActivity.appName, WaterFallDiagramActivity.MY_UUID_INSECURE);
                Log.d(WaterFallDiagramActivity.TAG, "AcceptThread: Setting up Server using: " + WaterFallDiagramActivity.MY_UUID_INSECURE);
            } catch (IOException e) {
                Log.e(WaterFallDiagramActivity.TAG, "AcceptThread: IOException: " + e.getMessage());
            }
            this.mmServerSocket = bluetoothServerSocket;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Log.d(WaterFallDiagramActivity.TAG, "run: AcceptThread Running.");
            BluetoothSocket bluetoothSocket = null;
            try {
                Log.d(WaterFallDiagramActivity.TAG, "run: RFCOM server socket start.....");
                bluetoothSocket = this.mmServerSocket.accept();
                Log.d(WaterFallDiagramActivity.TAG, "run: RFCOM server socket accepted connection.");
            } catch (IOException e) {
                Log.e(WaterFallDiagramActivity.TAG, "AcceptThread: IOException: " + e.getMessage());
            }
            if (bluetoothSocket != null) {
                WaterFallDiagramActivity waterFallDiagramActivity = WaterFallDiagramActivity.this;
                waterFallDiagramActivity.connected(bluetoothSocket, waterFallDiagramActivity.mmDevice);
            }
            Log.i(WaterFallDiagramActivity.TAG, "END mAcceptThread ");
        }

        public void cancel() {
            Log.d(WaterFallDiagramActivity.TAG, "cancel: Canceling AcceptThread.");
            try {
                this.mmServerSocket.close();
            } catch (IOException e) {
                Log.e(WaterFallDiagramActivity.TAG, "cancel: Close of AcceptThread ServerSocket failed. " + e.getMessage());
            }
        }
    }

    public class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice bluetoothDevice, UUID uuid) {
            Log.d(WaterFallDiagramActivity.TAG, "ConnectThread: started.");
            Log.d(WaterFallDiagramActivity.TAG, "bluetoothDevice = "+bluetoothDevice);
            WaterFallDiagramActivity.this.mmDevice = bluetoothDevice;
            WaterFallDiagramActivity.this.deviceUUID = uuid;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            BluetoothSocket bluetoothSocket;
            Log.i(WaterFallDiagramActivity.TAG, "RUN mConnectThread ");
            try {
                Log.d(WaterFallDiagramActivity.TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: " + WaterFallDiagramActivity.MY_UUID_INSECURE);
                if (ContextCompat.checkSelfPermission(WaterFallDiagramActivity.this, "android.permission.BLUETOOTH_CONNECT") != 0  || ContextCompat.checkSelfPermission(WaterFallDiagramActivity.this, "android.permission.BLUETOOTH_SCAN") != 0 || ContextCompat.checkSelfPermission(WaterFallDiagramActivity.this, "android.permission.ACCESS_FINE_LOCATION") != 0 || ContextCompat.checkSelfPermission(WaterFallDiagramActivity.this, "android.permission.ACCESS_COARSE_LOCATION") != 0) {
                    WaterFallDiagramActivity.this.requestPermissions(new String[]{"android.permission.BLUETOOTH_CONNECT", "android.permission.BLUETOOTH_CONNECT", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, 200);
                    return;
                }
                bluetoothSocket = WaterFallDiagramActivity.this.mmDevice.createRfcommSocketToServiceRecord(WaterFallDiagramActivity.this.deviceUUID);
            } catch (IOException e) {
                Log.e(WaterFallDiagramActivity.TAG, "ConnectThread: Could not create InsecureRfcommSocket " + e.getMessage());
                bluetoothSocket = null;
            }
            this.mmSocket = bluetoothSocket;
          /*  if (ContextCompat.checkSelfPermission(WaterFallDiagramActivity.this, "android.permission.BLUETOOTH_CONNECT") != 0  || ContextCompat.checkSelfPermission(WaterFallDiagramActivity.this, "android.permission.BLUETOOTH_SCAN") != 0 || ContextCompat.checkSelfPermission(WaterFallDiagramActivity.this, "android.permission.ACCESS_FINE_LOCATION") != 0 || ContextCompat.checkSelfPermission(WaterFallDiagramActivity.this, "android.permission.ACCESS_COARSE_LOCATION") != 0) {
                WaterFallDiagramActivity.this.requestPermissions(new String[]{"android.permission.BLUETOOTH_CONNECT", "android.permission.BLUETOOTH_CONNECT", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, 200);
                return;
            }*/
            WaterFallDiagramActivity.this.mBluetoothAdapter.cancelDiscovery();
            try {
                this.mmSocket.connect();
                Log.d(WaterFallDiagramActivity.TAG, "run: ConnectThread connected.");
            } catch (IOException e2) {
                Log.e(WaterFallDiagramActivity.TAG, "mConnectThread: run: Unable to close connection in socket " + e2.getMessage());
                Log.d(WaterFallDiagramActivity.TAG, "run: ConnectThread: Could not connect to UUID: " + WaterFallDiagramActivity.MY_UUID_INSECURE);
                WaterFallDiagramActivity waterFallDiagramActivity = WaterFallDiagramActivity.this;
                waterFallDiagramActivity.connected(this.mmSocket, waterFallDiagramActivity.mmDevice);
            }
            WaterFallDiagramActivity waterFallDiagramActivity22 = WaterFallDiagramActivity.this;
            waterFallDiagramActivity22.connected(this.mmSocket, waterFallDiagramActivity22.mmDevice);
        }

        public void cancel() {
            try {
                Log.d(WaterFallDiagramActivity.TAG, "cancel: Closing Client Socket.");
                this.mmSocket.close();
            } catch (IOException e) {
                Log.e(WaterFallDiagramActivity.TAG, "cancel: close() of mmSocket in Connectthread failed. " + e.getMessage());
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
        }
    }

    public void startClient(BluetoothDevice bluetoothDevice, UUID uuid) {
        Log.d(TAG, "startClient: Started.");
        mProgressDialog = new MyProgressDialog(this.mContext);
        mProgressDialog.show();
        ConnectThread connectThread = new ConnectThread(bluetoothDevice, uuid);
        this.mConnectThread = connectThread;
        connectThread.start();
    }

    public class ConnectedThread extends Thread {
        String incomingMessage;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private final BluetoothSocket mmSocket;

        public ConnectedThread(BluetoothSocket bluetoothSocket) {
            InputStream inputStream;
            Log.d(WaterFallDiagramActivity.TAG, "ConnectedThread: Starting.");
            this.mmSocket = bluetoothSocket;
            try {
                WaterFallDiagramActivity.this.mProgressDialog.dismiss();
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
            int val_uT;
            byte[] bArr = new byte[1024];
            double currentTimeMillis = System.currentTimeMillis();
            while (true) {
                try {
                    String str = new String(bArr, 0, this.mmInStream.read(bArr));
                    this.incomingMessage = str;
                    for (char c : str.toCharArray()) {
                        arrayList2.add(Character.valueOf(c));
                        if (c == '\r') {
                            StringBuilder sb = new StringBuilder();
                            Iterator it = arrayList2.iterator();
                            while (it.hasNext()) {
                                sb.append(((Character) it.next()).charValue());
                            }
                            try {
                                val_uT= (int) ((Integer.valueOf(Integer.parseInt(String.valueOf(sb).trim())))*sensor_factor);
                                arrayList.add(val_uT);
                            } catch (NumberFormatException unused) {
                            }
                            Log.d("result", sb.toString());
                            arrayList2.clear();
                        }
                    }
                    double currentTimeMillis2 = System.currentTimeMillis();
                    Double.isNaN(currentTimeMillis2);
                    if (currentTimeMillis2 - currentTimeMillis >= WaterFallDiagramActivity.this.SleepTime) {
                        currentTimeMillis = System.currentTimeMillis();
                        WaterFallDiagramActivity.this.getAverage(arrayList);
                        arrayList.clear();
                    }
                } catch (IOException e) {
                    Log.e(WaterFallDiagramActivity.TAG, "write: Error reading Input Stream. " + e.getMessage());
                    String bluetooth = SharedPref.getPreferences(WaterFallDiagramActivity.this).getStringData("Bluetooth","none");
                    if(WaterFallDiagramActivity.this.ThreadRunning) {
                        runOnUiThread(new Runnable() {
                            @Override // java.lang.Runnable
                            public void run() {
                                //makeTune.cancel();
                                View view = findViewById(R.id.main_2d_surface);
                                Snackbar snackbar = Snackbar.make(view, "Please Check Your Connection", Snackbar.LENGTH_INDEFINITE);
                                snackbar.setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.d(WaterFallDiagramActivity.TAG, "Pause Running");
                                        WaterFallDiagramActivity.this.is_running = false;
                                        WaterFallDiagramActivity.this.ThreadRunning = false;
                                        WaterFallDiagramActivity.this.btn_startStop.setText("Start");
                                        if (Intrinsics.areEqual(bluetooth, "on")) {
                                            if (mConnectedThread != null) {
                                                WaterFallDiagramActivity.this.mConnectedThread.cancel();
                                                Log.d(WaterFallDiagramActivity.TAG, "mConnectedThread Canceled");
                                            }

                                        } else {

                                            if (serialPort != null) {
                                                WaterFallDiagramActivity.this.serialPort.close();
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
            Log.d(WaterFallDiagramActivity.TAG, "write: Writing to outputstream: " + str);
            try {
                this.mmOutStream.write(bArr);
            } catch (IOException e) {
                Log.e(WaterFallDiagramActivity.TAG, "write: Error writing to output stream. " + e.getMessage());
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
}
