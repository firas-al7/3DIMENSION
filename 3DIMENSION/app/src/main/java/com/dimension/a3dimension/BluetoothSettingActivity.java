package com.dimension.a3dimension;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dimension.a3dimension.R;
import com.dimension.a3dimension.models.DeviceInfo;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothSettingActivity extends AppCompatActivity {

    ArrayList<DeviceInfo> deviceInfo;
    ListView deviceList;

    ImageView bluetoothStatus;
    private static final int MY_PERMISSIONS_REQUEST_CODE = 200;
    SwitchMaterial bluetoothSwitch;
    SharedPreferences sp;
    TextView NoDevice;
    SharedPreferences.Editor editor;

    MyProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_setting);
        Toolbar toolbar = findViewById(R.id.appToolbarBluetoothSetting);
        TextView toolbarTitle = findViewById(R.id.toolbarTitleBluetoothSetting);
        NoDevice = findViewById(R.id.txtnoBluetoothDevices);
        bluetoothSwitch =findViewById(R.id.bluetoothSwitch);

        setSupportActionBar(toolbar);

        final ActionBar supportActionBar = getSupportActionBar();

        if (supportActionBar != null) {
            supportActionBar.setDisplayShowHomeEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            toolbarTitle.setText("Bluetooth Settings");
            toolbarTitle.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        deviceList = findViewById(R.id.bluetoothDevicesListView);

        sp = getSharedPreferences("userSettings", Context.MODE_PRIVATE);

        updateView();

        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                editor = sp.edit();

                if (isChecked){


                    editor.putString("OTG","off");
                            //editor.commit();
                    editor.putString("Bluetooth","on");
                    deviceList.setVisibility(View.VISIBLE);
                    NoDevice.setVisibility(View.GONE);

                    /*mProgressDialog = new MyProgressDialog(BluetoothSettingActivity.this);

                    mProgressDialog.show();

                    mProgressDialog.dismiss();*/
                }else {

                    editor.putString("Bluetooth","off");
                    finish();
                    startActivity(getIntent());
                }
                  editor.commit();

                showBluetoothDevices();
                //finish();
                //startActivity(getIntent());

            }
        });



    }
    private void updateView()
    {
          editor =sp.edit();

        if(sp.getString("Bluetooth","off").equals("on"))
        {
            showBluetoothDevices();
            bluetoothSwitch.setChecked(true);
        }
        else
        {
            deviceList.setVisibility(View.GONE);
            NoDevice.setVisibility(View.VISIBLE);
            bluetoothSwitch.setChecked(false);
        }

    }
    public void showBluetoothDevices(){
        //Toast.makeText(getApplicationContext(),"Bluetooth "+sp.getString("Bluetooth","None")+" OTG "+sp.getString("OTG","None"),Toast.LENGTH_SHORT).show();
        // Bluetooth Setup
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ||ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED||ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED||ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{"android.permission.BLUETOOTH_CONNECT","android.permission.BLUETOOTH_ADMIN","android.permission.BLUETOOTH_SCAN", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, 200);
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        deviceInfo = new ArrayList<>();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {

                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                DeviceInfo deviceInfoModel = new DeviceInfo(deviceName, deviceHardwareAddress);
                deviceInfo.add(deviceInfoModel);
            }

            BluetoothDevicesAdapter deviceListAdapter = new BluetoothDevicesAdapter(deviceInfo, getApplicationContext());

            deviceList.setAdapter(deviceListAdapter);


            deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final int green = ContextCompat.getColor(getApplicationContext(), R.color.vivid_green);
                    editor = sp.edit();
                    TextView textname= view.findViewById(R.id.bluetoothDeviceName);
                    TextView textadress= view.findViewById(R.id.bluetoothDeviceAddress);
                    String name = (String) textname.getText();
                    String address =(String) textadress.getText();
                    editor.putString("Devicename",name);
                    editor.putString("Deviceaddress",address);
                    editor.commit();
                    bluetoothStatus = view.findViewById(R.id.bluetoothStatus);
                    bluetoothStatus.setBackgroundColor(green);
                    Toast.makeText(getApplicationContext(),"Paired With "+sp.getString("Devicename","None"),Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(),name,Toast.LENGTH_LONG).show();
                }
            });

        } else {

            deviceList.setVisibility(View.GONE);
            NoDevice.setVisibility(View.VISIBLE);
            bluetoothSwitch.setChecked(false);
            editor.putString("Bluetooth","off");
            editor.commit();
            //View view = findViewById(R.id.bluetoothDevicesListView);
            Snackbar snackbar = Snackbar.make(deviceList, "Activate Your Phone Bluetooth", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) { }
            });
            snackbar.setBackgroundTint(R.drawable.startup_screen_card_background);
            snackbar.show();
        }
    }
}