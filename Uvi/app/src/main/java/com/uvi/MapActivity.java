package com.uvi;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.uvi.googleapi.LatitudeLongitudeFoundCallback;
import com.uvi.googleapi.LatitudeLongitudeRetrievalTask;

import java.util.ArrayList;

/**
 * Created by Mark on 18/04/2015.
 */
public class MapActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private ArrayList<Marker> markers = new ArrayList<Marker>();

    private String start;
    private String destination;
    private String startId;
    private String destinationId;
    private String riderStart;
    private String riderDestination;
    private String riderStartId;
    private String riderDestinationId;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        start = intent.getStringExtra("start");
        destination = intent.getStringExtra("destination");
        startId = intent.getStringExtra("start_id");
        destinationId = intent.getStringExtra("destination_id");
        riderStart = intent.getStringExtra("rider_start");
        riderDestination = intent.getStringExtra("rider_destination");
        riderStartId = intent.getStringExtra("rider_start_id");
        riderDestinationId = intent.getStringExtra("rider_destination_id");
        phone = intent.getStringExtra("phone");

        TextView driverStart = (TextView)findViewById(R.id.driver_start);
        TextView driverDestination = (TextView)findViewById(R.id.driver_destination);
        TextView phoneView = (TextView)findViewById(R.id.phone);
        driverStart.setText("Start: " + start);
        driverDestination.setText("Destination: " + destination);
        phoneView.setText("Phone: " + phone);

        LatitudeLongitudeRetrievalTask startTask = new LatitudeLongitudeRetrievalTask(new LatitudeLongitudeFoundCallback() {
            @Override
            public void onLatitudeLongitudeFound(String name, LatLng latLng) {
                if (latLng != null) {
                    Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(false).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    marker.setTitle("Driver Start");
                    markers.add(marker);
                    centreMap();
                }
            }
        }, start, startId);
        startTask.execute();
        LatitudeLongitudeRetrievalTask destinationTask = new LatitudeLongitudeRetrievalTask(new LatitudeLongitudeFoundCallback() {
            @Override
            public void onLatitudeLongitudeFound(String name, LatLng latLng) {
                if (latLng != null) {
                    Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(false).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    marker.setTitle("Driver End");
                    markers.add(marker);
                    centreMap();
                }
            }
        }, destination, destinationId);
        destinationTask.execute();
        LatitudeLongitudeRetrievalTask riderStartTask = new LatitudeLongitudeRetrievalTask(new LatitudeLongitudeFoundCallback() {
            @Override
            public void onLatitudeLongitudeFound(String name, LatLng latLng) {
                if (latLng != null) {
                    Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(false).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    marker.setTitle("Rider Start");
                    markers.add(marker);
                    centreMap();
                }
            }
        }, riderStart, riderStartId);
        riderStartTask.execute();
        LatitudeLongitudeRetrievalTask riderDestinationTask = new LatitudeLongitudeRetrievalTask(new LatitudeLongitudeFoundCallback() {
            @Override
            public void onLatitudeLongitudeFound(String name, LatLng latLng) {
                if (latLng != null) {
                    Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(false).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    marker.setTitle("Rider End");
                    markers.add(marker);
                    centreMap();
                }
            }
        }, riderDestination, riderDestinationId);
        riderDestinationTask.execute();

        setUpMapIfNeeded();
        //getLocation();//calling service

    }

    private void centreMap() {
        if (markers == null) {
            return;
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
    }

    //    public Location getLocation() {
//        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        if (locationManager != null) {
//            Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            if (lastKnownLocationGPS != null) {
//                return lastKnownLocationGPS;
//            } else {
//                Location loc =  locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
//                System.out.println("1::"+loc);//getting null over here
//                System.out.println("2::"+loc.getLatitude());
//                return loc;
//            }
//        } else {
//            return null;
//        }
//    }
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //  mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker").snippet("Snippet"));

        // Enable MyLocation Layer of Google Map
        mMap.setMyLocationEnabled(true);

        // Get LocationManager object from System Service LOCATION_SERVICE
        //LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Get the name of the best provider
        //String provider = locationManager.getBestProvider(criteria, true);

        // Get Current Location
        //Location myLocation = locationManager.getLastKnownLocation(provider);
        // nullLocation = null;
        //if (myLocation == nullLocation){
            //        double latitude = nullLocation.getLatitude();
            //      double longitude = nullLocation.getLongitude();
        //}
        // set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Get latitude of the current location
        //  double latitude = myLocation.getLatitude();

        // Get longitude of the current location
        //    double longitude = myLocation.getLongitude();

        // Create a LatLng object for the current location
        //  LatLng latLng = new LatLng(latitude, longitude);

        // Show the current location in Google Map
        //    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        //     mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("You are here!").snippet("Consider yourself located"));

    }
}
