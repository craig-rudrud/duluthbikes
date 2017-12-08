package com.example.sam.duluthbikes;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
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
    private int hour;
    private int minute;
    private Context context;

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

        context = getActivity().getApplicationContext();

        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNotifications();
            }
        });

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

    /* Sets the notification to go off at the determined time

    UNDER CONSTRUCTION:  Currently trying to get it to output a test notification
     */
    private void setNotifications() {
        hour = timePicker.getHour();
        minute = timePicker.getMinute();

        activateNotifications();
    }

    private void activateNotifications() {
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent = new Intent();

        PendingIntent pendingIntent = PendingIntent
                .getActivity(context, 0, intent, 0);

        Notification bikeTime = new Notification.Builder(getContext())
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(getResources().getString(R.string.bike_time))
                .setSmallIcon(R.drawable.ic_action_logo)
                .setContentIntent(pendingIntent)
                .setSound(soundUri)
                .setVibrate(new long[] {0, 1000, 500, 250}).build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, bikeTime);
    }
}