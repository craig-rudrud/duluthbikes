package com.example.sam.duluthbikes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.ToggleButton;

/**
 * Created by Mackenzie Fulton on 11/16/2017.
 *
 * Class to handle the the inner workings of the Notifications menu
 */

public class NotificationsSettingsFragment extends Fragment {

    // Private variables
    private View myView;
    private ToggleButton toggleNotifications;
    private TimePicker timePicker;
    private Button setTime;

    @Nullable
    @Override
    /* Class ctor
    Opens the layout, sets certain layout elements to variables, defines button click
    response, determines if toggle is active or inactive
     */
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_notifications, container, false);

        toggleNotifications = (ToggleButton) myView.findViewById(R.id.toggle_notifications);

        timePicker = (TimePicker) myView.findViewById(R.id.time_picker);

        setTime = (Button) myView.findViewById(R.id.set_time);

        determineSetTimeState();

        toggleNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                determineSetTimeState();
            }
        });

        return myView;
    }

    /* Determines if the notification toggle button is on or off; allowing or
    disallowing the user to press the "set" button
     */
    private void determineSetTimeState() {
        if (!toggleNotifications.isChecked()) {
            setTime.setEnabled(false);
            timePicker.setEnabled(false);
        } else {
            setTime.setEnabled(true);
            timePicker.setEnabled(true);
        }
    }
}