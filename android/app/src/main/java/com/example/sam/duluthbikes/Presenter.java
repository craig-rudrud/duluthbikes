package com.example.sam.duluthbikes;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**

 * Created by Sam on 3/26/2017.
 */

public class Presenter implements ModelViewPresenterComponents.PresenterContract {

    private ModelViewPresenterComponents.Model mModel;
    private ModelViewPresenterComponents.View mView;
    private Context mContext;
    private FragmentActivity mActivity;

    public Presenter(){mModel = new Model();}


    public Presenter(Context context, FragmentActivity activity,ModelViewPresenterComponents.View view){
        mView = view;
        mContext = context;
        mActivity = activity;
    }

    public Presenter(Context context, FragmentActivity activity,ModelViewPresenterComponents.View view, boolean createModel){
        mView = view;
        mContext = context;
        mActivity = activity;
        if (createModel) {
            mModel = new Model();
        }
    }

    @Override
    public Location getLocationForCamera() {

        // NEED TO START GOOOGLE API CLIENT TO GET LOCATION
        ////// DOES NOT WORK BECAUSE MODEL HAS NOT STARTED. NO MAPS HACE STARTEd.
        //mModel = new Model(mContext,mActivity,this);
        Location loc = mModel.getLocation();
        return  loc;
    }

    @Override
    public void updateMapLocation(){
        mView.locationChanged(mModel.getLocation());
    }

    @Override
    public void clickStart(){
        mModel = new Model(mContext,mActivity,this);
    }

    @Override
    public void pauseRideButton() { mModel.stopLocationUpdates(); }

    @Override
    public void finishRideButton() { mModel.disconnectApiOnFinish(); }

    @Override
    public void connectApi() { mModel.connectApiOnResume(); }

    @Override
    public void notifyRoute(JSONArray route,JSONArray l){mModel.notifyFinishRoute(route,l);}

    @Override
    public void loginUser(String userName, String passWord) {
        mModel= new Model(mContext,mActivity,this);
        mModel.loginAttempt(userName,passWord);
    }

    public void newAccount(String userName, String passWord, String email) {
        mModel= new Model(mContext,mActivity,this);
        mModel.newAccount(userName,passWord, email);
    }

    @Override
    public void sendPictureToServer(String location, String description, String encodedImage) {
        //mModel = new Model(mContext);
        mModel.sendPicture(location, description, encodedImage);
    }

    @Override
    public void sendLeaderboardToServer(String type, JSONArray data) {

        switch (type) {
            case ModelViewPresenterComponents.LOCAL :
                mModel.sendToLocalLeaderboard(data);
                break;
            case ModelViewPresenterComponents.GLOBAL :
                mModel.sendToGlobalLeaderboard(data);
                break;
            default:
                System.out.println("Should never reach here.");
        }
    }

    @Override
    public JSONArray getLeaderboardFromServer(String type) {

        JSONArray data = null;

        switch (type) {
            case ModelViewPresenterComponents.GLOBAL :
                data = mModel.getGlobalLeaderboard();
                break;
            case ModelViewPresenterComponents.LOCAL :
                data = mModel.getLocalLeaderboard();
                break;
            default:
                System.out.println("Should never reach here.");
                break;
        }
        return data;
    }

    @Override
    public void returnLogin(String result){mView.userResults(result);}

    @Override
    public void setOurClient(GoogleApiClient googleApiClient) {
        mView.setClient(googleApiClient);
    }

    @Override
    public GoogleApiClient getOurClient() {
        return mView.getClient();
    }

    @Override
    public GoogleApiClient getClient() {
        return mModel.getGoogleApi();
    }

    @Override
    public void setClient(GoogleApiClient c) {
        mModel.setGoogleApi(c);
    }
}
