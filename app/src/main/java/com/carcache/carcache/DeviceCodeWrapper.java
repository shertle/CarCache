package com.carcache.carcache;

/**
 * Created by Sherman on 10/3/15.
 *
 * DeviceCodeWrapper class that implements the singleton pattern allowing the user to get the device
 * code when they need it
 */
public class DeviceCodeWrapper {

    private static String macAddress = null;

    private DeviceCodeWrapper() {}

    public static String getMacAddress(){
        return macAddress;
    }

    public static void setMacAddress(String macAddress){

    }

}
