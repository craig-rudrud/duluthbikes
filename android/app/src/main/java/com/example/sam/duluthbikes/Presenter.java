package com.example.sam.duluthbikes;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;

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
    public boolean loginUser(String username, String password) {
        mModel= new Model(mContext,mActivity,this);
        return mModel.loginAttempt(username, password);
    }

    @Override
    public boolean logoutUser() {
        return mModel.logoutAttempt();
    }

    public boolean getLoginStatus() {
        return mModel.getLoginStatus();
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

    public JSONArray getUsernames() {
        return mModel.getUsernames();
    }

    public JSONArray getFriends(String user) {
        return mModel.getFriends(user);
    }

    @Override
    public void returnLogin(String result){mView.userResults(result);}

    @Override
    public void setOurClient(GoogleApiClient googleApiClient) {
        mView.setClient(googleApiClient);
    }

    @Override
    public void sendOneClickToServer(String placeName, String clickTimes) {
        mModel.sendOneClick(placeName,clickTimes);
    }

    @Override
    public void deleteOneClickToServer(String placeName) {
        mModel.deleteOneClick(placeName);
    }

    @Override
    public JSONArray getClicksToServer() {
        return  mModel.getClicks();
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
    public boolean addFriend(String name) {
        return mModel.addFriend(name);
    }

    @Override
    public boolean addFriendByUser(String uid, String name) {
        return mModel.addFriendByUser(uid, name);
    }

    @Override
    public boolean removeFriend(String name) {
        return mModel.removeFriend(name);
    }

    @Override
    public boolean removeFriendByUser(String uid, String name) {
        return mModel.removeFriendByUser(uid, name);
    }

    @Override
    public void setClient(GoogleApiClient c) {
        mModel.setGoogleApi(c);
    }

    public void sendPicture(String location, String description, String encodedImage) {
        mModel.sendPicture(location, description, encodedImage);
    }
}
