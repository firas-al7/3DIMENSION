package com.dimension.a3dimension.models;

public class UsbInfo {
    private String usbName, usbProductId,usbVendorId;

    public UsbInfo(String usbName, String usbProductId, String usbVendorId) {
        this.usbName = usbName;
        this.usbProductId = usbProductId;
        this.usbVendorId = usbVendorId;
    }

    public String getUsbName() {
        return usbName;
    }

    public String getUsbProductId() {
        return usbProductId;
    }

    public String getUsbVendorId() {
        return usbVendorId;
    }
}
