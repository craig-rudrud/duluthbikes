package com.example.sam.duluthbikes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.concurrent.ExecutionException;

//import retrofit2.http.HTTP;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//import retrofit2.http.HTTP;

/**
 * Created by Sam on 3/26/2017.
 */

public class Model
        implements ModelViewPresenterComponents.Model,
            GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener,
            LocationListener{

    private ModelViewPresenterComponents.PresenterContract mPresenter;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private GoogleApiClient mGoogleApi;
    private Context mContext;
    private FragmentActivity mActivity;
    private int mRequestCode;
    private boolean mode;
    private String serverAddress="http://10.0.2.2:23405";//23405


    public Model(){}

    @SuppressLint("RestrictedApi")
    public Model(Context context, Presenter presenter){
        mContext = context;
        //mGSO = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        if(mGoogleApiClient==null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    public Model(Context context, FragmentActivity activity,Presenter presenter){
        mContext = context;
        mActivity = activity;
        mPresenter = presenter;
        mode = false;

        mGoogleApiClient = mPresenter.getOurClient();
        // Create an instance of GoogleAPIClient
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mPresenter.setOurClient(mGoogleApiClient);
            mGoogleApiClient.connect();
        }else{
            mGoogleApiClient.disconnect();
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mPresenter.setOurClient(mGoogleApiClient);
            mGoogleApiClient.connect();
        }
        createLocationRequest();
    }

    /**
     * Method to stop location services called by Presenters pauseRideButton method
     */
    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        disconnectApiOnFinish();
    }


    public void disconnectApiOnFinish() {
        if(mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    public void connectApiOnResume() {
        mGoogleApiClient.connect();
    }


    /**
     * @param finishRoute JSON object containing ride info
     * @param list ___
     * Sends route data to database
     */
    @Override
    public void notifyFinishRoute(JSONArray finishRoute,JSONArray list){
        if(finishRoute.length()>10) {
            JSONObject fullRide = null;
            try {
                fullRide = new JSONObject();
                fullRide.put("ride", finishRoute);
                fullRide.put("heat", list);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new HTTPAsyncTask().execute("http://ukko.d.umn.edu:23405/postfinish", "POST", fullRide.toString());
        }
    }

    /**
     *
     * @param input String to be hashed
     * @return string that has been hashed for sha256
     * @throws NoSuchAlgorithmException
     */
    public String sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA256");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    /**
     * @param user username
     * @param pass password
     */
    @Override
    public boolean loginAttempt(String user, String pass) {
        JSONObject profile = null;
        try{
            profile = new JSONObject();
            profile.put("name",user);
            profile.put("pass", sha256(pass+user));
        } catch (Exception e){
            e.printStackTrace();
        }
        mode = true;
        new HTTPAsyncTask().execute("http://ukko.d.umn.edu:23405/loginAttempt", "POST", profile.toString());
//        return getLoginStatus();
        return true;
    }

    @Override
    public boolean logoutAttempt() {
        String status = null;
        try {
            status = new HTTPAsyncTask().execute("http://ukko.d.umn.edu:23405/logout", "GET").get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return status.equals("logout");
    }

    public boolean getLoginStatus() {
        String loginStatus;
        try {
            loginStatus = new HTTPAsyncTask().execute("http://ukko.d.umn.edu:23405/isLoggedIn", "GET").get();
            return loginStatus.equals("true");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void newAccount(String user, String pass, String email){
        JSONObject profile = null;
        try{
            profile = new JSONObject();
            profile.put("name",user);
            profile.put("pass", sha256(pass+user));
            profile.put("email", email);
        } catch (Exception e){
            e.printStackTrace();
        }
        mode = true;
        new HTTPAsyncTask().execute("http://ukko.d.umn.edu:23405/newAccount","POST",profile.toString());
    }

    /**
     *
     * @param location ___
     * @param description ___ a description for the image
     * @param encodedImage ___ the image as a string
     * Sends a picture to be inserted into the database
     */
    @Override
    public void sendPicture(String location, String description, String encodedImage) {
        JSONObject pictureObj = null;
        try{
            pictureObj = new JSONObject();
            pictureObj.put("loc",location);   // pictureObj.put("loc",getLocation());
            pictureObj.put("description", description);
            pictureObj.put("picture",encodedImage);
        }catch (JSONException e){
            e.printStackTrace();
        }
        new HTTPAsyncTask().execute("http://ukko.d.umn.edu:23405/postpicture","POST",pictureObj.toString());
        //mGoogleApiClient.disconnect();
    }

    public String getPicture(String description) {
        String data = null;

        try {
            data = new HTTPAsyncTask().execute("http://ukko.d.umn.edu:23405/getpicture", "GET", description).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return data;
    }

    @Override
    public void sendOneClick(String placeName, String clickTimes) {
        JSONObject clicksObj = null;
        try {
            clicksObj = new JSONObject();
            clicksObj.put("placeName",placeName);   // pictureObj.put("loc",getLocation());
            clicksObj.put("clickTimes", clickTimes);
            new HTTPAsyncTask().execute(serverAddress+"/postClickPlaces","POST", clicksObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void deleteOneClick(String placeName) {
        try {
            new HTTPAsyncTask().execute(serverAddress+"/deleteClicks/"+placeName,"GET");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public JSONArray getClicks() {
        String data="";
        JSONArray jsonArray=null;
        try {
            data = new HTTPAsyncTask().execute(serverAddress + "/clickPlaces" ,"GET").get();
            jsonArray = new JSONArray(data);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;

    }

    @Override
    public void sendToLocalLeaderboard(JSONArray data) {

        try {
            new HTTPAsyncTask().execute("http://ukko.d.umn.edu:23405/postlocalleaderboard","POST", data.get(0).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendToGlobalLeaderboard(JSONArray data) {

        try {
            new HTTPAsyncTask().execute("http://ukko.d.umn.edu:23405/postgloballeaderboard","POST", data.get(0).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public JSONArray getLocalLeaderboard() {
        String data = null;
        JSONArray result = null;

        try {
            data = new HTTPAsyncTask().execute("http://ukko.d.umn.edu:23405/localleaderboard","GET").get();
        } catch (Exception e) {
            Log.d("DEBUG GET REQUEST",
                    "Timed out waiting for response from http://ukko.d.umn.edu:23405/localleaderboard");
        }

        try {
            result = new JSONArray(data);
        } catch (JSONException e) {
            Log.d("JSON EXCEP",
                    "parse fails or doesn't yield a JSONObject.");
        }

        return result;
    }

    @Override
    public JSONArray getGlobalLeaderboard() {
        String data = null;
        JSONArray result = null;

        try {
            data = new HTTPAsyncTask().execute("http://ukko.d.umn.edu:23405/globalleaderboard","GET").get();
        } catch (Exception e) {
            Log.d("DEBUG GET REQUEST",
                    "Timed out waiting for response from http://ukko.d.umn.edu:23405/localleaderboard");
        }

        try {
            result = new JSONArray(data);
        } catch (JSONException e) {
            Log.d("JSON EXCEP",
                    "parse fails or doesn't yield a JSONObject.");
        }

        return result;
    }

    public JSONArray getUsernames() {
        String data;
        JSONArray result = null;
        try {
            data = new HTTPAsyncTask().execute("http://ukko.d.umn.edu:23405/usernames", "GET").get();
            result = new JSONArray(data);
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public JSONArray getFriends(String user) {
        JSONObject object;
        String data;
        JSONArray result = null;

        try {
            object = new JSONObject();
            object.put("name", user);
            data = new HTTPAsyncTask().execute("http://ukko.d.umn.edu:23405/friends", "POST", object.toString()).get();
            result = new JSONArray(data);
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean addFriend(String name) {
        try {
            JSONObject object = new JSONObject();
            object.put("name", name);
            new HTTPAsyncTask().execute("http://ukko.d.umn.edu:23405/addFriend", "POST", object.toString());
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeFriend(String name) {
        try {
            JSONObject object = new JSONObject();
            object.put("name", name);
            new HTTPAsyncTask().execute("http://ukko.d.umn.edu:23405/removeFriend", "POST", object.toString());
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressLint("RestrictedApi")
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setSmallestDisplacement(2.0f);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Getter and Setter methods for Location
     */
    public Location getLocation() { return mLastLocation; }
    public void setLocation(Location curr) { mLastLocation = curr; }

    @Override
    public GoogleApiClient getGoogleApi() {
        return mGoogleApi;
    }

    @Override
    public void setGoogleApi(GoogleApiClient mGoogleApiClient) {
        mGoogleApi = mGoogleApiClient;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //check if the app is allowed to access location
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // request permission to access location
            ActivityCompat.requestPermissions(
                    mActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    mRequestCode);
            return;
        }
        // get the last location
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location){
        setLocation(location);
        mPresenter.updateMapLocation();
    }

    /*
     * Async task for GET PUT POST to send data to our server
     */
    private class HTTPAsyncTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params){

            HttpURLConnection serverConnection = null;
            InputStream inputStream = null;

            try{
                /* The Java URL class is used to hold the URI */
                URL url = new URL(params[0]);

                /* We can open a connection to this URL now */
                serverConnection = (HttpURLConnection) url.openConnection();

                /* The second parameter, params[1] contains the TYPE of the HTTP
                 * request. It can be GET, POST, PUT or DELETE.
                 */
                serverConnection.setRequestMethod(params[1]);

                /* If the TYPE is POST, PUT or DELETE then we need to take
                 * the third parameter params[2] which contains the JSON data
                 * we need to place in the body of the HTTP message, and write
                 * that JSON data as a string to the network connection to the
                 * HTTP server.
                 */
                if (params[1].equals("POST") ||
                        params[1].equals("PUT") ||
                        params[1].equals("DELETE")) {
                    Log.d("DEBUG POST/PUT/DELETE:", "In post: params[0]=" + params[0]
                            + ", params[1]=" + params[1] + ", params[2]=" + params[2]);

                    /* Various server parameters need to set on HTTP connections that indicate the type
                     * of data that will be sent. In our case, we are sending JSON as output so need to
                     * set the Content-Type header attribute.
                     */
                    serverConnection.setDoOutput(true);
                    serverConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                    /* Since params[2] contains the JSON String to send, we must also calculate the
                     * byte length of this data and set the Content-Length header attribute as well.
                     */
                    serverConnection.setRequestProperty("Content-Length", "" +
                            Integer.toString(params[2].toString().getBytes().length));

                    /* Finally, the JSON data can be written out to the server by using
                     * a DataOutputStream that is created with the server's output stream.
                     */
                    DataOutputStream out = new DataOutputStream(serverConnection.getOutputStream());
                    /* Write the json string data to the network */
                    out.writeBytes(params[2].toString());

                    /* flush and close the output stream buffer */
                    out.flush();
                    out.close();
                }

                /* ************************
                 * HTTP RESPONSE Section
                 * All requests are followed by a response with HTTP
                 *
                 * Get the response code and determine what to do.
                 */
                int responseCode = serverConnection.getResponseCode();

                Log.d("Debug: ", "HTTP Response Code : " + responseCode);

                /* Get the input stream (what's coming from our server to the Android client)
                 * process the JSON data that's contained with it.
                 */
                inputStream = serverConnection.getInputStream();

                /* This implementation is built so that ALL Responses send back a JSON data, as either
                 * the data you want from a GET Request or as confirmation of receiving the data
                 * on a POST, PUT, or DELETE Request.
                 */
                StringBuilder sb = new StringBuilder();
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                /* At this point, the StringBuilder sb contains all the data that was in the
                 * body of the Response. Since we expect JSON to be in this, the string hopefully
                 * contains valid JSON data.  We need to return this string out of this
                 * function and the onPostExecute function will process it.
                 */
                return sb.toString();

            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                serverConnection.disconnect();
            }
            return "Shouldn't ever get here ";
        }
        protected void onPostExecute(String user){
           /* if(mode){
                if(user=="good")mPresenter.returnLogin("good");
                else if(user=="bad")mPresenter.returnLogin("bad");
                else mPresenter.returnLogin("error");
                mode =false;
            }*/
        }
    }
}
