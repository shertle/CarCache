package com.carcache.carcache;

/*
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
*/

import android.app.FragmentManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.location.Criteria;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.carcache.carcache.connectors.WebServiceConnector;
import com.carcache.carcache.models.CCuser;
import com.carcache.carcache.services.BluetoothListenerService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class MapsActivity extends FragmentActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        BluetoothDeviceListFragment.OnFragmentInteractionListener {

    public static final String MAP_LOGGER = "MAP_LOGGER";
    public static final String CARCACHE_PREFS = "CarCachePrefs";
    public static final String PREFS_KEY_FIRSTLAUNCH = "FIRST_LAUNCH_KEY";


    private int timediff=5;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private ArrayList<CCuser> allUsers = new ArrayList<>();
    private ArrayList<Marker> allMarkers = new ArrayList<>();
    private CCuser mainUser;

    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)          // 5 seconds
            .setFastestInterval(16)     // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        boolean firstLaunch = settings.getBoolean(PREFS_KEY_FIRSTLAUNCH, true);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        /*
        List<String> s = new ArrayList<String>();
        for (BluetoothDevice bt : pairedDevices) {
            s.add(bt.getName());
        }
        for (String st : s) {
            Log.v(MAP_LOGGER, st);
        }
        */



        // On the first launch, ask the user for the bluetooth device
        if (firstLaunch) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Fragment fragment = new BluetoothDeviceListFragment();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();
        }
        else{
            startService(new Intent(this, BluetoothListenerService.class));

        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {



        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(this);

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                CCuser newUser = new CCuser();
                Location newLocation = new Location("");
                newLocation.setLatitude(latLng.latitude);
                newLocation.setLongitude(latLng.longitude);

                newUser.setLocation(newLocation);
                newUser.setDate(new Date());

                new WebServiceConnector().sendLocation(newUser);

            }
        });


        mMap = googleMap;

        //obtain location of the user
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location myLoc = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));

        //move the camera to be on the user
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(myLoc.getLatitude(), myLoc.getLongitude())));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mainUser = new CCuser(new Date(), myLoc);
        /*
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */

    }

    /**
     * Button to get current Location. This demonstrates how to get the current Location as required
     * without needing to register a LocationListener
     *
    public void showMyLocation(View view) {
        Log.v(MAP_LOGGER, "is connected: " + mGoogleApiClient.isConnected());
        if (mGoogleApiClient.isConnected()) {
            Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            double lat = loc.getLatitude();
            double lon = loc.getLongitude();
            String msg = "Longitude: " + lon + " Latitude: " + lat;
            //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
            Log.v(MAP_LOGGER, msg);
            CCuser test1 = new CCuser(new Date(),loc);

            String msg2 = "Date: "+ test1.getDate().toString();
            Log.v("Date", msg2);

            double lat1 = test1.getLocation().getLatitude();
            double lon1 = test1.getLocation().getLongitude();
            String msg1 = "Longitude: " + lon1 + " Latitude: " + lat1;

            Log.v("Location",msg1);

            Log.v("WebService Connect","Sending new location to web service.");
            WebServiceConnector connector = new WebServiceConnector();
            connector.sendLocation(test1);
            mainUser = test1;

        }

    }*/


    /**
     * delete old markers, request for new markers
     */
    public void refresh(View view)
    {

        Date curTime = new Date();
        if(!allUsers.isEmpty())
        {
            for(CCuser user:allUsers)
            {
                if(timeComp(curTime,user.getDate())<timediff)
                {
                    int position = allUsers.indexOf(user);
                    Marker m = allMarkers.get(position);
                    m.remove();
                    allMarkers.remove(m);
                    allUsers.remove(user);
                }
            }
        }
        WebServiceConnector connector = new WebServiceConnector();
        ArrayList<CCuser> newUser = connector.findPoints(mainUser);
        if(!newUser.isEmpty())
        {
            displayMarker(newUser);
        }

    }


    /*
    helper method to find difference in seconds of the two times
     */
    private int timeComp(Date d1, Date d2)
    {
        return (int)(d1.getTime()/1000-d2.getTime()/1000);
    }


    /**
     * Recieves array of markers of nearby CCusers and pin point the location of
     * such users on the map
     */
    public void displayMarker(ArrayList<CCuser> markers)
    {
        Date nowDate = new Date();

        for(CCuser m : markers) {
            Location l = m.getLocation();
            Date date = m.getDate();
            long difference = nowDate.getTime() - date.getTime();

            Marker mark = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(l.getLatitude(), l.getLongitude()))
                    .title(difference/1000/60 + " Mins Ago"));
            allUsers.add(m);
            allMarkers.add(mark);
        }
    }




    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        // Do nothing
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                REQUEST,
                this);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // Do nothing
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do nothing
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }


    public void onFragmentInteraction(String id) {
        // Do nothing
    }
}
