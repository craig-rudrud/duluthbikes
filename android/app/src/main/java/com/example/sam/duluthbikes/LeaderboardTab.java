package com.example.sam.duluthbikes;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by jacob on 11/18/17.
 */

public class LeaderboardTab extends Fragment {

    View myView;
    Button updateLeaderboardButton;
    String type;
    TextView name;
    TextView totDistance;
    TextView totTime;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_leaderboard_tab, container, false);

        updateLeaderboardButton = (Button) myView.findViewById(R.id.refreshLeaderboard);

        updateLeaderboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals(ModelViewPresenterComponents.LOCAL)) {
                    ((MenuActivity)getActivity()).onGetLocalLeaderboard(myView);
                }
                else if (type.equals(ModelViewPresenterComponents.GLOBAL)) {
                    ((MenuActivity)getActivity()).onGetGlobalLeaderboard(myView);
                }
            }
        });

        return myView;
    }

    public void setType(String type) {
        this.type = type;
    }

}
