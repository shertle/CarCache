package com.carcache.carcache;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Sherman on 10/3/15.
 */
public class DeviceListItem {
    //public String id;
    //public String content;
    public BluetoothDevice device;

    public DeviceListItem(BluetoothDevice bd) {
        this.device = bd;
        //this.id = id;
        //this.content = content;
    }

    public BluetoothDevice getDevice() {
        return this.device;
    }

    @Override
    public String toString() {
        return device.getName();
    }
}
