package com.carcache.carcache;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.util.Log;
import java.util.Date;
import java.util.ArrayList;
import android.location.Criteria;
import android.location.LocationManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;

public class MapsActivity extends FragmentActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback {

    public static final String MAP_LOGGER = "MAP_LOGGER";
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;

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


        mMap = googleMap;


        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location myLoc = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(myLoc.getLatitude(), myLoc.getLongitude())));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.addMarker(new MarkerOptions().position(new LatLng(myLoc.getLatitude(), myLoc.getLongitude())).title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        /*
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */
    }

    /**
     * Button to get current Location. This demonstrates how to get the current Location as required
     * without needing to register a LocationListener
     */
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
            Log.v("Location", msg1);

            Location loc1 = new Location(loc);
            ArrayList<CCuser> markers = new ArrayList<>();
            loc1.setLatitude(loc.getLatitude() + 10);
            loc1.setLongitude(loc.getLongitude() + 10);
            markers.add(new CCuser(new Date(), loc1));

            Location loc2 = new Location(loc);
            loc2.setLatitude(loc.getLatitude() + 20);
            loc2.setLongitude(loc.getLongitude() + 20);
            markers.add(new CCuser(new Date(),loc2));


            displayMarker(markers);

        }
    }

    public void refresh(View view)
    {
        finish();
        startActivity(getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
    }

    /**
     * Recieves array of markers of nearby CCusers and pin point the location of
     * such users on the map
     */
    public void displayMarker(ArrayList<CCuser> markers)
    {
        for(CCuser m : markers)
        {
            Location l = m.getLocation();
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(l.getLatitude(), l.getLongitude())));
        }
    }




    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        // Do nothing
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        LocationServices.FusedLocationApi.requestLocationUpdates (
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
}
