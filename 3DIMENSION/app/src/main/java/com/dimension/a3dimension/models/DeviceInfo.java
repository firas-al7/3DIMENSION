package com.dimension.a3dimension.models;

public class DeviceInfo {
    private String deviceName, deviceMacAddress;

    public DeviceInfo(){
    }

    public DeviceInfo(String deviceName, String deviceHardwareAddress){
        this.deviceName = deviceName;
        this.deviceMacAddress = deviceHardwareAddress;

    }

    public String getDeviceName(){return deviceName;}

    public String getDeviceMacAddress(){return deviceMacAddress;}

}
