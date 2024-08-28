package com.dimension.a3dimension.graphics;

import kotlin.jvm.internal.Intrinsics;


public final class consts {
    private String BLUETOOTH_DEVICE_NAME = "HC-05";
    private int withInternet = 1;
    private String FingerPrint = "6d85195a41c41a7e";

    public final String getBLUETOOTH_DEVICE_NAME() {
        return this.BLUETOOTH_DEVICE_NAME;
    }

    public final void setBLUETOOTH_DEVICE_NAME(String str) {
        Intrinsics.checkParameterIsNotNull(str, "<set-?>");
        this.BLUETOOTH_DEVICE_NAME = str;
    }

    public final int getWithInternet() {
        return this.withInternet;
    }

    public final void setWithInternet(int i) {
        this.withInternet = i;
    }

    public final String getFingerPrint() {
        return this.FingerPrint;
    }

    public final void setFingerPrint(String str) {
        Intrinsics.checkParameterIsNotNull(str, "<set-?>");
        this.FingerPrint = str;
    }
}
