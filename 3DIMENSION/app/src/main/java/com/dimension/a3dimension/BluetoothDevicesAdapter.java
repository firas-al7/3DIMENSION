package com.dimension.a3dimension;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.dimension.a3dimension.models.DeviceInfo;

import java.util.ArrayList;
public class BluetoothDevicesAdapter extends ArrayAdapter<DeviceInfo> implements View.OnClickListener {

    private ArrayList<DeviceInfo> dataSet;
    Context mContext;
    public static int scanImage;

    SharedPreferences sp;

    SharedPreferences.Editor editor;
    ImageView bluetoothStatus;
    // View lookup cache


    private static class ViewHolder {
        TextView txtName;
        TextView txtAddress;

        LinearLayout linearLayout;



    }

    public BluetoothDevicesAdapter(ArrayList<DeviceInfo> data, Context context) {
        super(context, R.layout.bluetooth_device_layout, data);
        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Object object = getItem(position);
        DeviceInfo dataModel = (DeviceInfo) object;



    }


    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DeviceInfo dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.bluetooth_device_layout, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.bluetoothDeviceName);
            viewHolder.txtAddress = (TextView) convertView.findViewById(R.id.bluetoothDeviceAddress);
           // viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.linearLayoutDeviceInfo);



            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtName.setText(dataModel.getDeviceName());
        viewHolder.txtAddress.setText(dataModel.getDeviceMacAddress());
        /*result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int green = ContextCompat.getColor(getContext(), R.color.vivid_green);
                editor = sp.edit();
                String name = dataModel.getDeviceName();
                String address = dataModel.getDeviceMacAddress();
                editor.putString("Devicename",name);
                editor.putString("Deviceaddress",address);
                bluetoothStatus = v.findViewById(R.id.bluetoothStatus);
                bluetoothStatus.setBackgroundColor(green);
                //Toast.makeText(getContext(),sp.getString("Devicename","None"),Toast.LENGTH_LONG);
                Toast.makeText(getContext(),name,Toast.LENGTH_LONG);
            }
        });*/

        // Return the completed view to render on screen
        return convertView;
    }

}
