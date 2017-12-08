package com.example.sam.duluthbikes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;


/**
 * End of the Ride activity
 * Displays statistics of the ride and allows to return to the home screen
 */

public class EndRideActivity extends AppCompatActivity implements ModelViewPresenterComponents.View{

    Bundle data;
    Float totDistance;
    Long totTime;
    private Presenter mPresenter;
    String theRideDate;
    String name = "in-app POST test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_ride);

        mPresenter = new Presenter(this.getBaseContext(), this, this, true);

        UnitConverter converter = new UnitConverter();

        TextView rideDate = (TextView)findViewById(R.id.dateLabel);
        TextView dist = (TextView)findViewById(R.id.distance);
        TextView timeLapsed = (TextView) findViewById(R.id.timeLapsed);
        TextView avSpeed = (TextView)findViewById(R.id.averageSpeed);
        TextView startTime = (TextView)findViewById(R.id.startTime);
        TextView endTime = (TextView)findViewById(R.id.endTime);
        TextView totalDist = (TextView)findViewById(R.id.totalDistance);
        TextView totalTime = (TextView)findViewById(R.id.totalTime);

        data = getIntent().getExtras();
        Long sTime =  data.getLong("startTime");
        Long fTime = data.getLong("endTime");
        Double distance = data.getDouble("dis");

        //data format definitions
        //SimpleDateFormat timef = new SimpleDateFormat("HH:mm:ss"); //military time
        SimpleDateFormat timef = new SimpleDateFormat("hh:mm:ss a");
        SimpleDateFormat datef = new SimpleDateFormat("MM-dd-yyyy");
        DecimalFormat df = new DecimalFormat("#.##");

        Long timelapse = fTime - sTime;

        theRideDate = datef.format(fTime);

        initializeTotals();

        //format data entries
        Double distKM = Double.valueOf(df.format(converter.getDistInKm(distance)));
        Double averKmH = Double.valueOf(df.format(converter.getKmPerHour(distance, timelapse)));
        String dateOfRide = datef.format(fTime);
        String timeFinish = timef.format(fTime);
        String timeStart = timef.format(sTime);

        rideDate.setText(dateOfRide);
        dist.setText(Double.toString(distKM));
        timeLapsed.setText(converter.convertHoursMinSecToString(timelapse));
        avSpeed.setText(Double.toString(averKmH));
        startTime.setText(timeStart);
        endTime.setText(timeFinish);
        totalDist.setText(df.format(converter.getDistInKm(totDistance.doubleValue())).toString() + " km");
        totalTime.setText(converter.convertHoursMinSecToString(totTime));

        updateLocalLeaderboard();
    }


    private void initializeTotals(){

        SharedPreferences totalstats = getSharedPreferences(getString(R.string.lifetimeStats_file_key), 0);
        totDistance = totalstats.getFloat(getString(R.string.lifetimeStats_totDist), 0);
        totTime = totalstats.getLong(getString(R.string.lifetimeStats_totTime), 0);

    }


    public void doneWithRide(View view){
        Intent menu = new Intent(this.getApplicationContext(), MenuActivity.class);
        startActivity(menu);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    /**
     * Determine wether the user has beat a leaderbboard score.
     * If the user has beaten their own local leaderboard, update the local leaderboard.
     * Same with the global leaderboard.
     *
     * NOTE: This isnt really safe code as it will try to compare leaderboard stats even if its empty.
     * In a testing enviroment, pre fill the leaderboard or improve this code!
     */
    private void updateLocalLeaderboard() {
        UnitConverter converter = new UnitConverter();
        double justFinishedDistance = converter.getDistInKm(totDistance.doubleValue());
        String justFinishedDistanceText = converter.convertHoursMinSecToString(totTime);
        String justFinishedTime = converter.convertHoursMinSecToString(totTime);

        JSONArray data = mPresenter.getLeaderboardFromServer(ModelViewPresenterComponents.LOCAL);
        JSONArray thisRideData = new JSONArray();
        JSONObject values = new JSONObject();

        try {
            values.put("date",theRideDate);
            values.put("distance",justFinishedDistanceText);
            values.put("time",justFinishedTime);
            values.put("name",name);
        } catch (JSONException e) {
            System.out.println("JSONException trying to put values into JSONObject in updateLeaderboard()!");
        }

        try {
            JSONObject jsonDataFirstRank = data.getJSONObject(0);
            JSONObject jsonDataSecondRank = data.getJSONObject(1);
            JSONObject jsonDataThirdRank = data.getJSONObject(2);

            if (justFinishedDistance > Double.parseDouble(jsonDataFirstRank.get("distance").toString())) {
                values.put("pos", 1);
                thisRideData.put(data);
                mPresenter.sendLeaderboardToServer(ModelViewPresenterComponents.LOCAL, thisRideData);
            }
            else if (justFinishedDistance > Double.parseDouble(jsonDataSecondRank.get("distance").toString())) {
                values.put("pos", 2);
                thisRideData.put(data);
                mPresenter.sendLeaderboardToServer(ModelViewPresenterComponents.LOCAL, thisRideData);
            }
            else if (justFinishedDistance > Double.parseDouble(jsonDataThirdRank.get("distance").toString())) {
                values.put("pos", 3);
                thisRideData.put(data);
                mPresenter.sendLeaderboardToServer(ModelViewPresenterComponents.LOCAL, thisRideData);
            }

            // TESTING
            System.out.println("sending dummy update.");
            values.put("pos", 1);
            thisRideData.put(values);
            try{
                System.out.println("******data: " + thisRideData.get(0).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mPresenter.sendLeaderboardToServer(ModelViewPresenterComponents.LOCAL, thisRideData);


        } catch (JSONException e) {
            System.out.println("BAD JSON CALL IN updateLeaderboard()");
        }

    }

    /*
    These below functions are required to implement to consider this class a View, as defined in
    ModelViewPresenterComponents
     */

    @Override
    public void locationChanged(Location location) {

    }

    @Override
    public void userResults(String results) {

    }

    @Override
    public void setClient(GoogleApiClient googleApiClient) {

    }

    @Override
    public GoogleApiClient getClient() {
        return null;
    }
}

