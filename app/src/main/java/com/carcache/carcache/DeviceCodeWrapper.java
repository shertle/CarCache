package com.carcache.carcache;

/**
 * Created by Sherman on 10/3/15.
 *
 * DeviceCodeWrapper class that implements the singleton pattern allowing the user to get the device
 * code when they need it
 */
public class DeviceCodeWrapper {
    private static DeviceCodeWrapper instance;

    private int deviceCode = 0;

    private DeviceCodeWrapper() {}

    public static DeviceCodeWrapper getInstance() {
        if (instance == null) {
            instance = new DeviceCodeWrapper();
        }
        return instance;
    }

    public int getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(int code) {
        this.deviceCode = code;
    }
}
