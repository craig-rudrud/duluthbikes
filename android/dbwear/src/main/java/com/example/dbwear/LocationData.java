package com.example.dbwear;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by pfan on 11/16/17.
 */

public class LocationData {

    private Context mContext;
    @SuppressLint("StaticFieldLeak")
    private static LocationData ourInstance;
    private GoogleApiClient googleApiClient;
    private double distance;
    private Location lastLocation;
    private PolylineOptions mPolylineOptions;
    private LatLngBounds.Builder mBuilder;
    private Long startTime;

    private LocationData(Context context) {
        mContext = context;
        mPolylineOptions = getPoints();
        mBuilder = getBuilder();
        lastLocation = getLastLocation();
        distance = getDistance();
        //trip = getTrip();
        //latlng = getLatlng();
        startTime = getStartTime();
        googleApiClient = getGoogleApiClient();
    }

    public void dataReset() {
        ourInstance = null;
    }

    public static LocationData getOurInstance(Context context) {
        if(ourInstance == null) {
            ourInstance = new LocationData(context);
        }
        return ourInstance;
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public void setGoogleApiClient(GoogleApiClient g) {
        googleApiClient = g;
    }

    public Location getLastLocation() {
        if (lastLocation == null) distance = -1;
        return lastLocation;
    }

    public double getDistance() {
        if(distance == -1) distance = 0;
        return distance;
    }

    public PolylineOptions getPoints() {
        if (mPolylineOptions == null) {
            mPolylineOptions = new PolylineOptions().width(15).color(Color.GREEN);
        }
        return mPolylineOptions;
    }

    public LatLngBounds.Builder getBuilder() {
        if (mBuilder == null) {
            mBuilder = new LatLngBounds.Builder();
        }
        return mBuilder;
    }

    public Long getStartTime() {
        if (startTime == null) {
            Date date = new Date();
            startTime = date.getTime();
        }
        return startTime;
    }

    public void addPoint(LatLng latLng, Location location) {
        if (location.getAccuracy() < 25) {
            mPolylineOptions.add(latLng);
            /*JSONObject arr = new JSONObject();
            try {
                arr.put("lat", location.getLatitude());
                arr.put("lng", location.getLongitude());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            trip.put(arr);
            latlng.put(latLng);*/
            if (getLastLocation() != null) distance += getLastLocation().distanceTo(location);
        }
        mBuilder.include(latLng);
        lastLocation = location;
    }

    /*public JSONArray getTrip(){
        if(trip==null)trip = new JSONArray();
        return trip;
    }

    public JSONArray getLatlng(){
        if(latlng==null)latlng = new JSONArray();
        return latlng;
    }*/




}
