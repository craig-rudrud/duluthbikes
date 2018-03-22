package com.example.sam.duluthbikes.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sam.duluthbikes.R;
import com.example.sam.duluthbikes.UnitConverter;

import org.w3c.dom.Text;

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
    Button eraseAllRides;

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

        totalDist.setText(df.format(totDistance.doubleValue()/1000).toString() + " km");
        totalTime.setText(converter.convertHoursMinSecToString(totTime));

        retrieveRideData();

        if(numberOfRides != 0) {
            avgDist.setText(df.format((totDistance.doubleValue() / 1000) / numberOfRides).toString() + " km");
            avgTime.setText(converter.convertHoursMinSecToString(totTime/numberOfRides));
        }
        else {
            avgDist.setText("0 km");
            avgTime.setText(converter.convertHoursMinSecToString(totTime));
        }

        eraseAllRides = makeEraseAllRidesButton();

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

    private void eraseAllRides() {

        SharedPreferences totalStats = getActivity()
                .getSharedPreferences(getString(R.string.lifetimeStats_file_key), 0);
        totalStats.edit().clear().commit();
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    private Button makeEraseAllRidesButton() {

        Button button = (Button)myView.findViewById(R.id.button_eraseAllRides);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogBox = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choice) {
                        switch (choice) {
                            case DialogInterface.BUTTON_POSITIVE:
                                eraseAllRides();
                                Toast.makeText(getActivity().getApplicationContext(),
                                        getString(R.string.eraseAllRidesToast), Toast.LENGTH_SHORT)
                                        .show();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(myView.getContext());
                builder.setMessage(getString(R.string.eraseAllRidesPopup))
                        .setPositiveButton("Yes", dialogBox)
                        .setNegativeButton("No", dialogBox).show();
            }
        });

        return button;
    }
}
