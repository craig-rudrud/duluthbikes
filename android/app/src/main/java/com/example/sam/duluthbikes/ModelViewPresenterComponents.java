package com.example.sam.duluthbikes;

import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Sam on 3/26/2017.
 */

public interface ModelViewPresenterComponents {

    static String GLOBAL = "GLOBAL";
    static String LOCAL = "LOCAL";

    interface View {

        void locationChanged(Location location);

        void userResults(String results);

        void setClient(GoogleApiClient googleApiClient);

        GoogleApiClient getClient();
    }

    interface PresenterContract {

        void setClient(GoogleApiClient c);

        Location getLocationForCamera();

        void updateMapLocation();

        void clickStart();

        void pauseRideButton();

        void finishRideButton();

        void connectApi();

        void notifyRoute(JSONArray fullRide,JSONArray l);

        void loginUser(String userName,String passWord);

        void sendPictureToServer(String loc, String description, String encodedImage);

        void sendLeaderboardToServer(String type, JSONObject data);

        JSONObject getLeaderboardFromServer(String type);

        void returnLogin(String result);

        void setOurClient(GoogleApiClient googleApiClient);

        GoogleApiClient getOurClient();

        GoogleApiClient getClient();
    }

    interface Model {

        void setGoogleApi(GoogleApiClient mGoogleApiClient);

        //Set Location
        void setLocation(Location curr);

        //Get Location
        Location getLocation();

        void sendToLocalLeaderboard(JSONObject data);

        void sendToGlobalLeaderboard(JSONObject data);

        JSONObject getLocalLeaderboard();

        JSONObject getGlobalLeaderboard();

        void stopLocationUpdates();

        void disconnectApiOnFinish();

        void connectApiOnResume();

        void notifyFinishRoute(JSONArray r,JSONArray l);

        void loginAttempt(String user,String pass);

        void sendPicture(String loc, String description, String encodedImage);

        GoogleApiClient getGoogleApi();
    }

}

