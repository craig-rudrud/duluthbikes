package com.example.sam.duluthbikes;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.LinkedList;

/**
 * Created by jacob on 11/18/17.
 */

public class LeaderboardTab extends Fragment {

    View myView;
    Button updateLeaderboardButton;
    String type;
    JSONArray data;
    TextView totDist1;
    TextView totDist2;
    TextView totDist3;
    TextView totTime1;
    TextView totTime2;
    TextView totTime3;
    TextView name1;
    TextView name2;
    TextView name3;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //set variables
        myView = inflater.inflate(R.layout.activity_leaderboard_tab, container, false);

        totDist1 = (TextView) myView.findViewById(R.id.homeTotalDistance1);
        totDist2 = (TextView) myView.findViewById(R.id.homeTotalDistance2);
        totDist3 = (TextView) myView.findViewById(R.id.homeTotalDistance3);

        totTime1 = (TextView) myView.findViewById(R.id.homeTotalTime1);
        totTime2 = (TextView) myView.findViewById(R.id.homeTotalTime2);
        totTime3 = (TextView) myView.findViewById(R.id.homeTotalTime3);

        name1 = (TextView) myView.findViewById(R.id.nameValue1);
        name2 = (TextView) myView.findViewById(R.id.nameValue2);
        name3 = (TextView) myView.findViewById(R.id.nameValue3);

        updateLeaderboardButton = (Button) myView.findViewById(R.id.refreshLeaderboard);

        //set the initial values in the tab
        if (type.equals(ModelViewPresenterComponents.LOCAL)) {
            data = ((MenuActivity) getActivity()).GetLocalLeaderboard();
        }
        else if (type.equals(ModelViewPresenterComponents.GLOBAL)) {
            data = ((MenuActivity) getActivity()).GetGlobalLeaderboard();
        }

        //set the listener for the button
        updateLeaderboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals(ModelViewPresenterComponents.LOCAL)) {
                    data = ((MenuActivity) getActivity()).GetLocalLeaderboard();
                    updateTextViews(data);
                }
                else if (type.equals(ModelViewPresenterComponents.GLOBAL)) {
                    data = ((MenuActivity) getActivity()).GetGlobalLeaderboard();
                    updateTextViews(data);
                }
            }
        });

        updateTextViews(data);

        return myView;
    }

    public void setType(String type) {
        this.type = type;
    }

    private void updateTextViews(JSONArray data) {

        try {
            JSONObject jsonDataFirstRank = data.getJSONObject(0);
            JSONObject jsonDataSecondRank = data.getJSONObject(1);
            JSONObject jsonDataThirdRank = data.getJSONObject(2);

            totDist1.setText(jsonDataFirstRank.get("distance").toString());
            totTime1.setText(jsonDataFirstRank.get("time").toString());
            name1.setText(jsonDataFirstRank.get("name").toString());

            totDist2.setText(jsonDataSecondRank.get("distance").toString());
            totTime2.setText(jsonDataSecondRank.get("time").toString());
            name2.setText(jsonDataSecondRank.get("name").toString());

            totDist3.setText(jsonDataThirdRank.get("distance").toString());
            totTime3.setText(jsonDataThirdRank.get("time").toString());
            name3.setText(jsonDataThirdRank.get("name").toString());

        } catch (JSONException e) {
            System.out.println("BAD JSON CALL IN updateTextViews()");
        }

    }

}
