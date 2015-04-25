package com.uvi.googleapi;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Mark
 */
public interface LatitudeLongitudeFoundCallback {
    public void onLatitudeLongitudeFound(String name, LatLng latLng);
}
