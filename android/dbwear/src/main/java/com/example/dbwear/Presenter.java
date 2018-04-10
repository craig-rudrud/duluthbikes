package com.example.dbwear;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.support.wearable.activity.WearableActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by pfan on 11/6/17.
 */

public class Presenter implements MVP.Presenter {

    private MVP.View mView;
    private MVP.Model mModel;
    private Context mContext;
    private WearableActivity mActivity;

    public Presenter() {
        mModel = new Model();
    }

    public Presenter(Context context, WearableActivity activity, MVP.View view) {
        mView = view;
        mActivity = activity;
        mContext = context;
    }

    @Override
    public Location getLocationForCamera() {

        // NEED TO START GOOOGLE API CLIENT TO GET LOCATION
        ////// DOES NOT WORK BECAUSE MODEL HAS NOT STARTED. NO MAPS HACE STARTEd.
        //mModel = new Model(mContext,mActivity,this);
        Location loc = mModel.getLocation();
        return loc;
    }

    @Override
    public void updateMapLocation(){
        mView.locationChanged(mModel.getLocation());
    }

    @Override
    public void clickStart(){
        mModel = new Model(mContext, mActivity,this);
    }

    @Override
    public void pauseRideButton() { mModel.stopLocationUpdates(); }

    @Override
    public void finishRideButton() { mModel.disconnectApiOnFinish(); }

    @Override
    public void connectApi() { mModel.connectApiOnResume(); }

    /*@Override
    public void notifyRoute(JSONArray route,JSONArray l){mModel.notifyFinishRoute(route,l);}*/

    /*@Override
    public void loginUser(String userName, String passWord) {
        mModel= new Model(mContext,mActivity,this);
        mModel.loginAttempt(userName,passWord);
    }*/

    /*@Override
    public void sendPictureToServer(String location, String description, String encodedImage) {
        //mModel = new Model(mContext);
        mModel.sendPicture(location, description, encodedImage);
    }*/

    //@Override
    //public void returnLogin(String result){mView.userResults(result);}

    @Override
    public void setOurClient(GoogleApiClient googleApiClient) {
        mView.setClient(googleApiClient);
    }

    @Override
    public GoogleApiClient getOurClient() {
        return mView.getClient();

    }

}
