package com.dimension.a3dimension;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.dimension.a3dimension.graphics.SharedPref;
import com.dimension.a3dimension.models.UsbInfo;
import java.util.ArrayList;


public class UsbDevicesAdapter extends ArrayAdapter<UsbInfo> {

    private final Context mContext;
    private final ArrayList<UsbInfo> dataSet;

    SharedPref pref;

    public UsbDevicesAdapter(@NonNull Context context, @NonNull ArrayList<UsbInfo> data) {
        super(context, R.layout.usb_device_layout, data);
        this.dataSet = data;
        this.mContext = context;
    }
    public UsbInfo getItem(int i) {
        return this.dataSet.get(i);
    }

    private class ViewHolder {

        ImageView Status;
        TextView UsbName;

        TextView txtUsbProductId;
        TextView txtUsbVendorId;

        TextView UsbProductId;
        TextView UsbVendorId;

        LinearLayout usbContainer;
        public ViewHolder(View view) {
            this.Status= (ImageView) view.findViewById(R.id.usbStatus);
            this.UsbName =(TextView) view.findViewById(R.id.usbDeviceName);
            this.txtUsbProductId = (TextView) view.findViewById(R.id.txtusbProductId);
            this.txtUsbVendorId = (TextView) view.findViewById(R.id.txtusbVendorId);
            this.UsbProductId = (TextView) view.findViewById(R.id.usbProductId);
            this.UsbVendorId = (TextView) view.findViewById(R.id.usbVendorId);
            this.usbContainer =(LinearLayout) view.findViewById(R.id.linearLayoutUsbInfo);
        }
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {

            view = LayoutInflater.from(this.mContext).inflate(R.layout.usb_device_layout, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (UsbDevicesAdapter.ViewHolder) view.getTag();
        }
        viewHolder.UsbName.setText(((UsbInfo) getItem(i)).getUsbName());
        viewHolder.UsbProductId.setText(((UsbInfo) getItem(i)).getUsbProductId());
        viewHolder.UsbVendorId.setText(((UsbInfo) getItem(i)).getUsbVendorId());
        /*viewHolder.usbContainer.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                final int green = ContextCompat.getColor(mContext, R.color.vivid_green);
                viewHolder.Status.setBackgroundColor(green);
                String pId = (String) view2.UsbProductId.getText();
                String vId = (String) viewHolder.UsbVendorId.getText();
                pref.getPreferences(view2.setStringData("VendorID",vId);
                pref.getPreferences(mContext.getApplicationContext()).setStringData("ProductID",pId);
                Toast.makeText(mContext,"Vendor ID "+pref.getStringData("VendorID","none"),Toast.LENGTH_SHORT).show();
            }
        });*/
        return view;
    }


}
