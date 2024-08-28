package com.dimension.a3dimension.graphics.Activities.diagrams;

import android.Manifest;
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
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.dimension.a3dimension.MyProgressDialog;
import com.dimension.a3dimension.graphics.SharedPref;
import com.dimension.a3dimension.graphics.consts;
import com.dimension.a3dimension.R;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.DebugKt;


public class LinearDiagramActivity extends AppCompatActivity {
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "BluetoothConnectionServ";
    private static final String appName = "MYAPP";
    private final String BLUETOOTH_DEVICE_NAME = new consts().getBLUETOOTH_DEVICE_NAME();
    int ConvertedMessage;
    ImageView Dry_column;
    TextView OutPut;
    TextView SensitivityHelp;
    AnyChartView anyChartView;
    Button btn_balance;
    Button btn_sound;
    ImageView btn_return_linear;
    Cartesian cartesian;
    int current;
    private UUID deviceUUID;
    BluetoothDevice mBTDevice;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    Context mContext;
    private AcceptThread mInsecureAcceptThread;
    MyProgressDialog mProgressDialog;
    private BluetoothDevice mmDevice;
    int previous;
    SeekBar seekBar;
    Set set;
    ToneGenerator toneGen1;
    ImageView wet_column;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    boolean doubleBackToExitPressedOnce = false;
    int balanceNumber = -1;
    int changeValue = 1;
    int littleCounter = 1;
    String IntPattern = "-?\\d+";
    String sound = DebugKt.DEBUG_PROPERTY_VALUE_OFF;
    List<DataEntry> seriesData = new ArrayList();
    MakeTune makeTune = new MakeTune();


    SharedPref sp;

   // SharedPreferences.Editor editor;



    UsbDeviceConnection connection;

    //UsbSerialPort port;

    int usbProductId;
    int usbVendorId;
    //UsbDeviceConnection connection;
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
        setContentView(R.layout.activity_linear_diagram);
        binding();
        this.mContext = this;


        String bluetooth = SharedPref.getPreferences(LinearDiagramActivity.this).getStringData("Bluetooth", "none");


        if (Intrinsics.areEqual(bluetooth, "on")){
            if (ContextCompat.checkSelfPermission(LinearDiagramActivity.this,"android.permission.BLUETOOTH_CONNECT") != 0 || ContextCompat.checkSelfPermission(LinearDiagramActivity.this, "android.permission.BLUETOOTH_ADMIN") != 0 || ContextCompat.checkSelfPermission(LinearDiagramActivity.this, "android.permission.ACCESS_FINE_LOCATION") != 0 || ContextCompat.checkSelfPermission(LinearDiagramActivity.this, "android.permission.ACCESS_COARSE_LOCATION") != 0) {
                requestPermissions(new String[]{"android.permission.BLUETOOTH_CONNECT",  "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, 200);
                //return;
            }
        java.util.Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
        if (mBluetoothAdapter.isEnabled()) {
            if (bondedDevices != null) {
                for (BluetoothDevice bluetoothDevice : bondedDevices) {

                    if (Intrinsics.areEqual(bluetoothDevice.getName(), SharedPref.getPreferences(this).getStringData("Devicename", "None"))) {
                        Toast.makeText(this,"paired with"+ bluetoothDevice.getName(), Toast.LENGTH_LONG).show();
                        this.mBTDevice = (BluetoothDevice) bluetoothDevice;
                        start();
                        startClient(this.mBTDevice, MY_UUID_INSECURE);

                    }/*else {

                        Toast.makeText(mContext, "Please Choose A Bluetooth Device From Settings", Toast.LENGTH_LONG).show();
                        //return;
                    }*/
                }

            } else {
                Toast.makeText(this, "Please pair to HC-05 Bluetooth Module", Toast.LENGTH_LONG).show();
            }

         } else {
            Toast.makeText(this, "Please Turn On The Bluetooth", Toast.LENGTH_LONG).show();
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
                if (hasUsbPermission){

                    startUsbConnection(device);

                } else {
                    PendingIntent pi = PendingIntent.getBroadcast(LinearDiagramActivity.this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_MUTABLE);
                    LinearDiagramActivity.this.registerReceiver(usbReceiver, new IntentFilter(ACTION_USB_PERMISSION));
                    manager.requestPermission(device, pi);

                }


            } else {
                Toast.makeText(LinearDiagramActivity.this,"Please Connect an OTG Device",Toast.LENGTH_SHORT).show();

            }
        }
        createMainChart();
        this.makeTune.start();
        listeners();

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
                }
            } else {
                //Toast.makeText(LinearDiagramActivity.this, "SERIAL PORT IS NULL", Toast.LENGTH_SHORT).show();
                Log.d("SERIAL", "PORT IS NULL");
                //finish();
                runOnUiThread(new Runnable() {
                    @Override // java.lang.Runnable
                    public void run() {
                        Toast.makeText(LinearDiagramActivity.this, "USB Device Not Supported !", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

            }
        }
    }
    public class ReadUsbThread extends Thread {

        String incomingMessage;
        int counter = 0;
        int single_digit_counter = 0;


        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            ArrayList<Character> arrayList = new ArrayList();
            byte[] buffer = new byte[256];

            while (true) {


                int n = serialPort.syncRead(buffer, 0);
                if(n>0) {
                    byte[] received = new byte[n];
                    System.arraycopy(buffer, 0, received, 0, n);
                    String receivedStr = new String(received);
                    for (char c : receivedStr.toCharArray()) {
                        arrayList.add(Character.valueOf(c));
                        if (c == '\r') {
                            StringBuilder stringBuilder = new StringBuilder();

                            Iterator<Character> iterator = arrayList.iterator();
                            while (iterator.hasNext()) {
                                stringBuilder.append(((Character) iterator.next()).charValue());
                            }
                            try {
                                LinearDiagramActivity.this.ConvertedMessage = (int) ((Integer.valueOf(Integer.parseInt(String.valueOf(stringBuilder).trim()))) * 0.244f);
                                if (this.counter == 0 && LinearDiagramActivity.this.balanceNumber == -1) {
                                    LinearDiagramActivity.this.balanceNumber = LinearDiagramActivity.this.ConvertedMessage;
                                    Log.d("BluetoothConnectionServ", "BALANCE SET!!!!");
                                }
                                if (this.counter == 0) {
                                    LinearDiagramActivity.this.previous = LinearDiagramActivity.this.ConvertedMessage;
                                    LinearDiagramActivity.this.current = LinearDiagramActivity.this.ConvertedMessage + 1;
                                } else {
                                    LinearDiagramActivity.this.current = LinearDiagramActivity.this.ConvertedMessage;
                                }
                                this.counter++;
                                if (LinearDiagramActivity.this.ConvertedMessage >= LinearDiagramActivity.this.balanceNumber && Math.abs(LinearDiagramActivity.this.current - LinearDiagramActivity.this.previous) >= LinearDiagramActivity.this.changeValue) {
                                    (LinearDiagramActivity.this.Dry_column.getLayoutParams()).height = LinearDiagramActivity.this.ConvertedMessage - LinearDiagramActivity.this.balanceNumber;
                                    (LinearDiagramActivity.this.wet_column.getLayoutParams()).height = 0;
                                } else if (LinearDiagramActivity.this.ConvertedMessage < LinearDiagramActivity.this.balanceNumber && Math.abs(LinearDiagramActivity.this.current - LinearDiagramActivity.this.previous) >= LinearDiagramActivity.this.changeValue) {
                                    (LinearDiagramActivity.this.Dry_column.getLayoutParams()).height = 0;
                                    (LinearDiagramActivity.this.wet_column.getLayoutParams()).height = LinearDiagramActivity.this.balanceNumber - LinearDiagramActivity.this.ConvertedMessage;
                                }
                                if (this.counter % 2 == 0) {
                                    if (LinearDiagramActivity.this.seriesData.size() > 30)
                                        LinearDiagramActivity.this.seriesData.remove(2);
                                    LinearDiagramActivity linearDiagramActivity1 = LinearDiagramActivity.this;
                                    linearDiagramActivity1.littleCounter++;
                                    List<DataEntry> list = LinearDiagramActivity.this.seriesData;
                                    CustomDataEntry customDataEntry = new CustomDataEntry(String.valueOf(LinearDiagramActivity.this.littleCounter), Integer.valueOf(LinearDiagramActivity.this.ConvertedMessage));

                                    list.add(customDataEntry);
                                    LinearDiagramActivity.this.set.data(LinearDiagramActivity.this.seriesData);
                                }
                                LinearDiagramActivity linearDiagramActivity = LinearDiagramActivity.this;
                                Runnable runnable = new Runnable() {
                                    public void run() {
                                        LinearDiagramActivity.this.OutPut.setText(String.valueOf(LinearDiagramActivity.this.ConvertedMessage));
                                        LinearDiagramActivity.this.wet_column.requestLayout();
                                        LinearDiagramActivity.this.Dry_column.requestLayout();
                                        Button button = LinearDiagramActivity.this.btn_balance;
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append("BALANCE\n");
                                        stringBuilder.append(LinearDiagramActivity.this.balanceNumber);
                                        button.setText(stringBuilder.toString());
                                        LinearDiagramActivity.this.btn_balance.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View param3View) {
                                                LinearDiagramActivity.this.balanceNumber = LinearDiagramActivity.this.ConvertedMessage;
                                            }
                                        });
                                    }
                                };
                                //  super(this);
                                linearDiagramActivity.runOnUiThread(runnable);
                                if (Math.abs(LinearDiagramActivity.this.current - LinearDiagramActivity.this.previous) >= LinearDiagramActivity.this.changeValue) {
                                    LinearDiagramActivity.this.previous = LinearDiagramActivity.this.current;
                                    this.single_digit_counter = 0;
                                }
                            } catch (NumberFormatException numberFormatException) {
                            }
                            Log.d("result", stringBuilder.toString());
                            arrayList.clear();
                        }
                    }
                }
            }
        }
        public void cancel() {
            serialPort.close();
        }

    }

    private void listeners() {
        this.btn_return_linear.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                onBackPressed();
            }
        });
        this.btn_sound.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (LinearDiagramActivity.this.sound.equals(DebugKt.DEBUG_PROPERTY_VALUE_OFF)) {
                    LinearDiagramActivity.this.sound = DebugKt.DEBUG_PROPERTY_VALUE_ON;
                    LinearDiagramActivity.this.btn_sound.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sound_on, 0, 0, 0);
                } else if (LinearDiagramActivity.this.sound.equals(DebugKt.DEBUG_PROPERTY_VALUE_ON)) {
                    LinearDiagramActivity.this.sound = "red";
                    LinearDiagramActivity.this.btn_sound.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sound_red, 0, 0, 0);
                } else if (LinearDiagramActivity.this.sound.equals("red")) {
                    LinearDiagramActivity.this.sound = "blue";
                    LinearDiagramActivity.this.btn_sound.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sound_blue, 0, 0, 0);
                } else if (LinearDiagramActivity.this.sound.equals("blue")) {
                    LinearDiagramActivity.this.sound = DebugKt.DEBUG_PROPERTY_VALUE_OFF;
                    LinearDiagramActivity.this.btn_sound.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sound_off, 0, 0, 0);
                }
            }
        });
        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                LinearDiagramActivity.this.changeValue = i;
                TextView textView = LinearDiagramActivity.this.SensitivityHelp;
                textView.setText("Sensitivity : " + i);
            }
        });
    }


    @Override
    // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        this.sound = DebugKt.DEBUG_PROPERTY_VALUE_OFF;
        String bluetooth = SharedPref.getPreferences(LinearDiagramActivity.this).getStringData("Bluetooth","none");
        if (Intrinsics.areEqual(bluetooth, "on")){
            if(this.mConnectedThread!=null){
           this.mConnectedThread.cancel();
            }
        } else {
            if(serialPort!=null){
                serialPort.close();
            }
        }
    }

    @Override
    // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        this.sound = DebugKt.DEBUG_PROPERTY_VALUE_OFF;
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        if (this.doubleBackToExitPressedOnce) {
            super.onBackPressed();
            this.sound = DebugKt.DEBUG_PROPERTY_VALUE_OFF;
            this.btn_sound.setText(R.string.linearDiagram_btn_Sound_off);
            String bluetooth = SharedPref.getPreferences(LinearDiagramActivity.this).getStringData("Bluetooth","none");
            if (Intrinsics.areEqual(bluetooth, "on")){
                if(this.mConnectedThread!=null){
                    this.mConnectedThread.cancel();
                }
            }else{
                if(serialPort!=null){
                    serialPort.close();
                }
            }
            finish();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override // java.lang.Runnable
            public void run() {
                LinearDiagramActivity.this.doubleBackToExitPressedOnce = false;
            }
        }, 2000L);
    }

    public void createMainChart() {
        Cartesian line = AnyChart.line();
        this.cartesian = line;
        Double valueOf = Double.valueOf(5.0d);
        line.padding((Number) valueOf, (Number) valueOf, (Number) valueOf, (Number) valueOf);
        this.cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        this.seriesData.add(new CustomDataEntry("..", 0));
        this.seriesData.add(new CustomDataEntry(".", 1023));
        Set instantiate = Set.instantiate();
        this.set = instantiate;
        instantiate.data(this.seriesData);
        Line line2 = this.cartesian.line(this.set.mapAs("{ x: 'x', value: 'value' }"));
        line2.hovered().markers().enabled((Boolean) false);
        line2.hovered().markers().type(MarkerType.CIRCLE).size(Double.valueOf(4.0d));
        line2.tooltip().position("right").anchor(Anchor.LEFT_CENTER).offsetX(valueOf).offsetY(valueOf);
        line2.tooltip().enabled((Boolean) true);
        this.cartesian.legend().enabled((Boolean) false);
        this.anyChartView.setChart(this.cartesian);
    }


    public class CustomDataEntry extends ValueDataEntry {
        CustomDataEntry(String str, Number number) {
            super(str, number);
        }
    }

    private void binding() {
        this.manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        this.btn_return_linear =(ImageView)findViewById(R.id.btn_return_linear);
        this.anyChartView = (AnyChartView) findViewById(R.id.any_chart_view);
        this.wet_column = (ImageView) findViewById(R.id.wet_column);
        this.Dry_column = (ImageView) findViewById(R.id.Dry_column);
        this.OutPut = (TextView) findViewById(R.id.OutPut);
        this.SensitivityHelp = (TextView) findViewById(R.id.txtSensitivityHelp);
        this.btn_balance = (Button) findViewById(R.id.btn_balance);
        this.btn_sound = (Button) findViewById(R.id.btn_sound);
        this.seekBar = (SeekBar) findViewById(R.id.seekbar);
        this.toneGen1 = new ToneGenerator(3, 100);
        this.usbProductId= Integer.parseInt(SharedPref.getPreferences(getApplicationContext()).getStringData("UsbProductId","1234"));
        this.usbVendorId = Integer.parseInt(SharedPref.getPreferences(getApplicationContext()).getStringData("UsbVendorId","1234"));
    }


    public class AcceptThread extends Thread {
        private BluetoothServerSocket mmServerSocket =null;

        public AcceptThread() {
            BluetoothServerSocket bluetoothServerSocket = null;
            try {
                if ((ActivityCompat.checkSelfPermission(LinearDiagramActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) ||(ActivityCompat.checkSelfPermission(LinearDiagramActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)||(ActivityCompat.checkSelfPermission(LinearDiagramActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)||(ActivityCompat.checkSelfPermission(LinearDiagramActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)){

                    LinearDiagramActivity.this.requestPermissions(new String[]{"android.permission.BLUETOOTH_CONNECT", "android.permission.BLUETOOTH_SCAN", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, 200);

                    return;
                }
                bluetoothServerSocket = LinearDiagramActivity.this.mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(LinearDiagramActivity.appName, LinearDiagramActivity.MY_UUID_INSECURE);
                Log.d(LinearDiagramActivity.TAG, "AcceptThread: Setting up Server using: " + LinearDiagramActivity.MY_UUID_INSECURE);
            } catch (IOException e) {
                Log.e(LinearDiagramActivity.TAG, "AcceptThread: IOException: " + e.getMessage());
            }
            this.mmServerSocket = bluetoothServerSocket;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Log.d(LinearDiagramActivity.TAG, "run: AcceptThread Running.");
            BluetoothSocket bluetoothSocket = null;
            try {
                Log.d(LinearDiagramActivity.TAG, "run: RFCOM server socket start.....");
                bluetoothSocket = this.mmServerSocket.accept();
                Log.d(LinearDiagramActivity.TAG, "run: RFCOM server socket accepted connection.");
            } catch (IOException e) {
                Log.e(LinearDiagramActivity.TAG, "AcceptThread: IOException: " + e.getMessage());
            }
            if (bluetoothSocket != null) {
                LinearDiagramActivity linearDiagramActivity = LinearDiagramActivity.this;
                linearDiagramActivity.connected(bluetoothSocket, linearDiagramActivity.mmDevice);
            }
            Log.i(LinearDiagramActivity.TAG, "END mAcceptThread ");
        }

        public void cancel() {
            Log.d(LinearDiagramActivity.TAG, "cancel: Canceling AcceptThread.");
            try {
                this.mmServerSocket.close();
            } catch (IOException e) {
                Log.e(LinearDiagramActivity.TAG, "cancel: Close of AcceptThread ServerSocket failed. " + e.getMessage());
            }
        }
    }


    public class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice bluetoothDevice, UUID uuid) {
            Log.d(LinearDiagramActivity.TAG, "ConnectThread: started.");
            LinearDiagramActivity.this.mmDevice = bluetoothDevice;
            LinearDiagramActivity.this.deviceUUID = uuid;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            BluetoothSocket bluetoothSocket;
            Log.i(LinearDiagramActivity.TAG, "RUN mConnectThread ");
            try {
                Log.d(LinearDiagramActivity.TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: " + LinearDiagramActivity.MY_UUID_INSECURE);
                if ((ActivityCompat.checkSelfPermission(LinearDiagramActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) ||(ActivityCompat.checkSelfPermission(LinearDiagramActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)||(ActivityCompat.checkSelfPermission(LinearDiagramActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)||(ActivityCompat.checkSelfPermission(LinearDiagramActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)){

                    LinearDiagramActivity.this.requestPermissions(new String[]{"android.permission.BLUETOOTH_CONNECT", "android.permission.BLUETOOTH_SCAN", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, 200);

                    return;
                }
                bluetoothSocket = LinearDiagramActivity.this.mmDevice.createRfcommSocketToServiceRecord(LinearDiagramActivity.this.deviceUUID);
            } catch (IOException e) {
                Log.e(LinearDiagramActivity.TAG, "ConnectThread: Could not create InsecureRfcommSocket " + e.getMessage());
                bluetoothSocket = null;
            }
            this.mmSocket = bluetoothSocket;
            LinearDiagramActivity.this.mBluetoothAdapter.cancelDiscovery();
            try {
                this.mmSocket.connect();
                Log.d(LinearDiagramActivity.TAG, "run: ConnectThread connected.");
            } catch (IOException e2) {
                Log.e(LinearDiagramActivity.TAG, "mConnectThread: run: Unable to close connection in socket " + e2.getMessage());
                Log.d(LinearDiagramActivity.TAG, "run: ConnectThread: Could not connect to UUID: " + LinearDiagramActivity.MY_UUID_INSECURE);
                LinearDiagramActivity linearDiagramActivity = LinearDiagramActivity.this;
                linearDiagramActivity.connected(this.mmSocket, linearDiagramActivity.mmDevice);
            }
            LinearDiagramActivity linearDiagramActivity22 = LinearDiagramActivity.this;
            linearDiagramActivity22.connected(this.mmSocket, linearDiagramActivity22.mmDevice);
        }

        public void cancel() {
            try {
                Log.d(LinearDiagramActivity.TAG, "cancel: Closing Client Socket.");
                this.mmSocket.close();
            } catch (IOException e) {
                Log.e(LinearDiagramActivity.TAG, "cancel: close() of mmSocket in Connect thread failed. " + e.getMessage());
            }
        }
    }

    public synchronized void start() {
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
        //this.mProgressDialog = ProgressDialog.show(this.mContext, "Connecting Bluetooth", "Please Wait...", true);
        ConnectThread connectThread = new ConnectThread(bluetoothDevice, uuid);
        this.mConnectThread = connectThread;
        connectThread.start();
    }


    public class ConnectedThread extends Thread {
        String incomingMessage;
        String[] lines;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private final BluetoothSocket mmSocket;
        String prev = "";
        boolean has_junk = false;
        int counter = 0;
        int single_digit_counter = 0;

        public ConnectedThread(BluetoothSocket bluetoothSocket) {
            InputStream inputStream;
            Log.d(LinearDiagramActivity.TAG, "ConnectedThread: Starting.");
            this.mmSocket = bluetoothSocket;
            /*try {
                LinearDiagramActivity.this.mProgressDialog.dismiss();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }*/
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
            ArrayList<Character> arrayList = new ArrayList();
            byte[] arrayOfByte = new byte[1024];
            try {
                while (true) {

                    String str = new String(arrayOfByte,0,this.mmInStream.read(arrayOfByte));

                    this.incomingMessage = str;
                    for (char c : str.toCharArray()) {
                        arrayList.add(Character.valueOf(c));
                        if (c == '\r') {
                            StringBuilder stringBuilder = new StringBuilder();

                            Iterator<Character> iterator = arrayList.iterator();
                            while (iterator.hasNext()) {
                                stringBuilder.append(((Character) iterator.next()).charValue());
                            }
                            try {
                                LinearDiagramActivity.this.ConvertedMessage = (int) ((Integer.valueOf(Integer.parseInt(String.valueOf(stringBuilder).trim()))) * 0.244f);
                                if (this.counter == 0 && LinearDiagramActivity.this.balanceNumber == -1) {
                                    LinearDiagramActivity.this.balanceNumber = LinearDiagramActivity.this.ConvertedMessage;
                                    Log.d("BluetoothConnectionServ", "BALANCE SET!!!!");
                                }
                                if (this.counter == 0) {
                                    LinearDiagramActivity.this.previous = LinearDiagramActivity.this.ConvertedMessage;
                                    LinearDiagramActivity.this.current = LinearDiagramActivity.this.ConvertedMessage + 1;
                                } else {
                                    LinearDiagramActivity.this.current = LinearDiagramActivity.this.ConvertedMessage;
                                }
                                this.counter++;
                                if (LinearDiagramActivity.this.ConvertedMessage >= LinearDiagramActivity.this.balanceNumber && Math.abs(LinearDiagramActivity.this.current - LinearDiagramActivity.this.previous) >= LinearDiagramActivity.this.changeValue) {
                                    (LinearDiagramActivity.this.Dry_column.getLayoutParams()).height = LinearDiagramActivity.this.ConvertedMessage - LinearDiagramActivity.this.balanceNumber;
                                    (LinearDiagramActivity.this.wet_column.getLayoutParams()).height = 0;
                                } else if (LinearDiagramActivity.this.ConvertedMessage < LinearDiagramActivity.this.balanceNumber && Math.abs(LinearDiagramActivity.this.current - LinearDiagramActivity.this.previous) >= LinearDiagramActivity.this.changeValue) {
                                    (LinearDiagramActivity.this.Dry_column.getLayoutParams()).height = 0;
                                    (LinearDiagramActivity.this.wet_column.getLayoutParams()).height = LinearDiagramActivity.this.balanceNumber - LinearDiagramActivity.this.ConvertedMessage;
                                }
                                if (this.counter % 2 == 0) {
                                    if (LinearDiagramActivity.this.seriesData.size() > 30)
                                        LinearDiagramActivity.this.seriesData.remove(2);
                                    LinearDiagramActivity linearDiagramActivity1 = LinearDiagramActivity.this;
                                    linearDiagramActivity1.littleCounter++;
                                    List<DataEntry> list = LinearDiagramActivity.this.seriesData;
                                    LinearDiagramActivity.CustomDataEntry customDataEntry = new LinearDiagramActivity.CustomDataEntry(String.valueOf(LinearDiagramActivity.this.littleCounter), Integer.valueOf(LinearDiagramActivity.this.ConvertedMessage));

                                    list.add(customDataEntry);
                                    LinearDiagramActivity.this.set.data(LinearDiagramActivity.this.seriesData);
                                }
                                LinearDiagramActivity linearDiagramActivity = LinearDiagramActivity.this;
                                Runnable runnable = new Runnable() {
                                    public void run() {
                                        LinearDiagramActivity.this.OutPut.setText(String.valueOf(LinearDiagramActivity.this.ConvertedMessage));
                                        LinearDiagramActivity.this.wet_column.requestLayout();
                                        LinearDiagramActivity.this.Dry_column.requestLayout();
                                        Button button = LinearDiagramActivity.this.btn_balance;
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append("BALANCE\n");
                                        stringBuilder.append(LinearDiagramActivity.this.balanceNumber);
                                        button.setText(stringBuilder.toString());
                                        LinearDiagramActivity.this.btn_balance.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View param3View) {
                                                LinearDiagramActivity.this.balanceNumber = LinearDiagramActivity.this.ConvertedMessage;
                                            }
                                        });
                                    }
                                };
                                //super(this);
                                linearDiagramActivity.runOnUiThread(runnable);
                                if (Math.abs(LinearDiagramActivity.this.current - LinearDiagramActivity.this.previous) >= LinearDiagramActivity.this.changeValue) {
                                    LinearDiagramActivity.this.previous = LinearDiagramActivity.this.current;
                                    this.single_digit_counter = 0;
                                }
                            } catch (NumberFormatException numberFormatException) {}
                            Log.d("result", stringBuilder.toString());
                            arrayList.clear();
                        }
                    }
                }
            } catch (IOException iOException) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("write: Error reading Input Stream. ");
                stringBuilder.append(iOException.getMessage());
                Log.e("BluetoothConnectionServ", stringBuilder.toString());
            }
        }


        public void write(byte[] bArr) {
            String str = new String(bArr, Charset.defaultCharset());
            Log.d(LinearDiagramActivity.TAG, "write: Writing to outputstream: " + str);
            try {
                this.mmOutStream.write(bArr);
            } catch (IOException e) {
                Log.e(LinearDiagramActivity.TAG, "write: Error writing to output stream. " + e.getMessage());
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

    public void write(byte[] bArr) {
        Log.d(TAG, "write: Write Called.");
        this.mConnectedThread.write(bArr);
    }


    private class MakeTune extends Thread {
        int difference;
        boolean is_run;
        int sleepDuration;

        private MakeTune() {
            this.sleepDuration = 100;
            this.is_run = true;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            while (this.is_run) {
                try {
                    sleep(this.sleepDuration);
                } catch (InterruptedException e) {
                    Log.d(LinearDiagramActivity.TAG, e.toString());
                }
                if (!LinearDiagramActivity.this.sound.equals(DebugKt.DEBUG_PROPERTY_VALUE_OFF)) {
                    int abs = Math.abs(LinearDiagramActivity.this.ConvertedMessage - LinearDiagramActivity.this.balanceNumber);
                    this.difference = abs;
                    if (abs > 3) {
                        if (LinearDiagramActivity.this.sound.equals(DebugKt.DEBUG_PROPERTY_VALUE_ON)) {
                            if (LinearDiagramActivity.this.ConvertedMessage >= LinearDiagramActivity.this.balanceNumber) {
                                LinearDiagramActivity.this.toneGen1.startTone(12, 100);
                            } else {
                                LinearDiagramActivity.this.toneGen1.startTone(2, 100);
                            }
                        } else if (LinearDiagramActivity.this.sound.equals("red")) {
                            if (LinearDiagramActivity.this.ConvertedMessage >= LinearDiagramActivity.this.balanceNumber) {
                                LinearDiagramActivity.this.toneGen1.startTone(12, 100);
                            }
                        } else if (LinearDiagramActivity.this.sound.equals("blue") && LinearDiagramActivity.this.ConvertedMessage <= LinearDiagramActivity.this.balanceNumber) {
                            LinearDiagramActivity.this.toneGen1.startTone(2, 100);
                        }
                    }
                }
            }
        }

        public void cancel() {
            this.is_run = false;
        }
    }
}
