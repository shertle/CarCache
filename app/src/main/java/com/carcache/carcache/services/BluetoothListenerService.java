package com.carcache.carcache.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by zizhouzhai on 10/3/15.
 */
public class BluetoothListenerService extends Service {

    private static String TAG = "Bluetooth Change Handler";
    private String macAddress = "";
    public static final String PREFS_KEY_SAVEDDEVICE = "CarCache Saved Device";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate(){
        super.onCreate();

        Log.v(TAG, "Registering the Receiver");
        System.out.println("ASDf");
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter1);
        this.registerReceiver(mReceiver, filter2);
        this.registerReceiver(mReceiver, filter3);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        macAddress = preferences.getString(PREFS_KEY_SAVEDDEVICE,"");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Do not forget to unregister the receiver!!!
        this.unregisterReceiver(mReceiver);
    }


    @Override
    public int onStartCommand (Intent intent, int flags, int startId){

        return super.onStartCommand(intent,flags,startId);

    }


    //The BroadcastReceiver that listens for bluetooth broadcasts
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.v(TAG, "Revieved intent: " + action);

            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //Device found
                Log.v(TAG, "ACTION_FOUND");
                showSuccessfulBroadcast("ACTION_FOUND");
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                //Device is now connected
                Log.v(TAG, "ACTION_ACL_CONNECTED");
                showSuccessfulBroadcast("ACTION_ACL_CONNECTED");

                if(macAddress == device.getAddress()){
                    Log.v(TAG, "Connected to device with matching mac address: " + macAddress);
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //Done searching
                Log.v(TAG, "ACTION_DISCOVERY_FINISHED");
                showSuccessfulBroadcast("ACTION_DISCOVERY_FINISHED");
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                //Device is about to disconnect
                Log.v(TAG, "ACTION_ACL_DISCONNECT_REQUESTED" );
                showSuccessfulBroadcast("ACTION_ACL_DISCONNECT_REQUESTED");
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Device has disconnected
                Log.v(TAG, "ACTION_ACL_DISCONNECTED" );
                showSuccessfulBroadcast("ACTION_ACL_DISCONNECTED");
                if(macAddress == device.getAddress()){
                    Log.v(TAG, "Disconnected to device with matching mac address: " + macAddress);
                }

            }
        }
    };

    private void showSuccessfulBroadcast(String s) {
        Toast.makeText(this, "Broadcast: " + s, Toast.LENGTH_LONG)
                .show();
    }


}
