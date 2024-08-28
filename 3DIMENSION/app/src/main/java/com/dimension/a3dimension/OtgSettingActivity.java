package com.dimension.a3dimension;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dimension.a3dimension.graphics.SharedPref;
import com.dimension.a3dimension.models.UsbInfo;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class OtgSettingActivity extends AppCompatActivity {
    ArrayList<UsbInfo> usbInfoArrayList;
    SwitchMaterial otgSwitch;
    SharedPreferences sp;
    ListView usbList;
    TextView NoDevice;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otg_setting);
        Toolbar toolbar = findViewById(R.id.appToolbarOtgSetting);
        TextView toolbarTitle = findViewById(R.id.toolbarTitleOtgSetting);
        NoDevice = findViewById(R.id.txtnoUsbDevices);
        otgSwitch = findViewById(R.id.otgSwitch);

        setSupportActionBar(toolbar);

        final ActionBar supportActionBar = getSupportActionBar();

        if (supportActionBar != null) {
            supportActionBar.setDisplayShowHomeEnabled(true);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            toolbarTitle.setText("OTG Settings");
            toolbarTitle.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        usbList = findViewById(R.id.usbDevicesListView);

        sp = getSharedPreferences("userSettings", Context.MODE_PRIVATE);

        updateView();

        otgSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor = sp.edit();

                if (isChecked) {

                    editor.putString("Bluetooth", "off");
                    editor.putString("OTG", "on");
                    usbList.setVisibility(View.VISIBLE);
                    NoDevice.setVisibility(View.GONE);

                } else {
                    editor.putString("OTG", "off");
                    finish();
                    startActivity(getIntent());

                }

                editor.commit();
                showUsbDevices();
            }


        });
        //updateView();


    }

    private void updateView()
    {
        editor =sp.edit();

        if(sp.getString("OTG","off").equals("on"))
        {
            showUsbDevices();
            otgSwitch.setChecked(true);
        }
        else
        {
            usbList.setVisibility(View.GONE);
            NoDevice.setVisibility(View.VISIBLE);
           otgSwitch.setChecked(false);

        }

    }
    public void showUsbDevices(){
        Toast.makeText(getApplicationContext(), "Bluetooth " + sp.getString("Bluetooth", "None") + " OTG " + sp.getString("OTG", "None"), Toast.LENGTH_SHORT).show();

        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        usbInfoArrayList = new ArrayList<>();

        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            String deviceName = device.getManufacturerName();
            int deviceVendorId = device.getVendorId();
            int deviceProductId = device.getProductId();
            String txtdeviceVendorId = ""+deviceVendorId+"";
            String txtdeviceProductId = ""+deviceProductId+"";
            UsbInfo usb= new UsbInfo(deviceName,txtdeviceProductId,txtdeviceVendorId);
            usbInfoArrayList.add(usb);

        }
        if (!deviceList.isEmpty()){
            UsbDevicesAdapter usbListAdapter = new UsbDevicesAdapter(getApplicationContext(),usbInfoArrayList);
            usbList.setAdapter(usbListAdapter);
            usbList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    final int green = ContextCompat.getColor(getApplicationContext(), R.color.vivid_green);

                    TextView  UsbProductId = (TextView) view.findViewById(R.id.usbProductId);
                    TextView UsbVendorId = (TextView) view.findViewById(R.id.usbVendorId);
                    String pID = (String) UsbProductId.getText();
                    String vID =(String) UsbVendorId.getText();

                    SharedPref.getPreferences(getApplicationContext()).setStringData("UsbProductId",pID);
                    SharedPref.getPreferences(getApplicationContext()).setStringData("UsbVendorId",vID);
                    ImageView usbStatus = view.findViewById(R.id.usbStatus);
                    usbStatus.setBackgroundColor(green);
                    Toast.makeText(getApplicationContext(),"UsbVendorId  "+SharedPref.getPreferences(getApplicationContext()).getStringData("UsbVendorId","None"),Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(),name,Toast.LENGTH_LONG).show();
                }
            });

        } else {

            usbList.setVisibility(View.GONE);
            NoDevice.setVisibility(View.VISIBLE);

            //View view = findViewById(R.id.usbDevicesListView);
            Snackbar snackbar = Snackbar.make(usbList, "Please Check Your OTG Connection", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) { }
            });
            snackbar.setBackgroundTint(R.drawable.startup_screen_card_background);
            snackbar.show();

        }
    }
}