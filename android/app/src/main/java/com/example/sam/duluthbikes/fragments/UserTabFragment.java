package com.example.sam.duluthbikes.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sam.duluthbikes.R;
import com.example.sam.duluthbikes.UnitConverter;

import java.text.DecimalFormat;

/**
 * Created by Sam on 4/20/2017.
 */

public class UserTabFragment extends Fragment {

    View myView;
    TextView totalDist;
    TextView avgDist;
    TextView totalTime;
    TextView avgTime;
    TextView rideData;
    Float totDistance;
    Float avgDistance;
    Long totTime;
    Long averageTime;
    int numberOfRides;
    UnitConverter converter;
    DecimalFormat df;

    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_user_tab, container, false);

        converter = new UnitConverter();

        totalDist = (TextView)myView.findViewById(R.id.homeTotalDistance);
        avgDist = (TextView)myView.findViewById(R.id.homeAvgDistance);
        totalTime = (TextView)myView.findViewById(R.id.homeTotalTime);
        avgTime = (TextView)myView.findViewById(R.id.homeAvgTime);
        rideData = (TextView)myView.findViewById(R.id.allRideDataGoesHere);

        df = new DecimalFormat("#.##");

        initializeTotals();

        totalTime.setText(converter.convertHoursMinSecToString(totTime));

        retrieveRideData();

        if(numberOfRides != 0) {
            totalDist.setText(df.format(totDistance.doubleValue()/1000).toString());
            avgDist.setText(df.format((totDistance.doubleValue() / 1000) / numberOfRides).toString());
            avgTime.setText(converter.convertHoursMinSecToString(totTime/numberOfRides));
        }
        else {
            totalDist.setText("0.00");
            avgDist.setText("0.00");
            avgTime.setText(converter.convertHoursMinSecToString(totTime));
        }

        return myView;
    }


    private void initializeTotals(){

        SharedPreferences totalstats = getActivity().getSharedPreferences(getString(R.string.lifetimeStats_file_key), 0);
        totDistance = totalstats.getFloat(getString(R.string.lifetimeStats_totDist), 0);
        avgDistance = totalstats.getFloat(getString(R.string.lifetimeStats_avgDist), 0);
        totTime = totalstats.getLong(getString(R.string.lifetimeStats_totTime), 0);
        averageTime = totalstats.getLong(getString(R.string.lifetimeStats_avgDist), 0);
        numberOfRides = totalstats.getInt(getString(R.string.lifetimeStats_rideNumber), 0);

    }

    private void retrieveRideData(){

        SharedPreferences totalstats = getActivity().getSharedPreferences(getString(R.string.lifetimeStats_file_key), 0);

        if(numberOfRides == 0) {
            rideData.append("No rides!");
        }

        for (int num = numberOfRides; num >= 1;  num--){
            String rideTime = "ride" + num + "Time";
            String rideDist = "ride" + num + "Distance";

            Float rideD = totalstats.getFloat(rideDist, 0);
            Long rideT = totalstats.getLong(rideTime, 0);

            rideData.append("Ride " + num + ": \n" +
                    "Distance: " +
                    df.format(converter.getDistInKm(rideD.doubleValue())).toString() + " km" + "\n" +
                    "Time: " + converter.convertHoursMinSecToString(rideT) + "\n\n");
        }

    }
}
