package com.example.sam.duluthbikes;

import android.app.AlarmManager;
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
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;

/**
 * Created by Mackenzie Fulton on 11/16/2017.
 * gilbe935@d.umn.edu if there are any questions!
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
    /** Class constructor */
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // variable definitions
        myView = inflater.inflate(R.layout.activity_notifications, container, false);
        toggleNotifications = (ToggleButton) myView.findViewById(R.id.toggle_notifications);
        timePicker = (TimePicker) myView.findViewById(R.id.time_picker);
        setTime = (Button) myView.findViewById(R.id.set_time);
        context = getActivity().getApplicationContext();

        // determine if toggle is active/inactive on open
        toggleNotifications.setChecked(isAlarmActive());
        allowSet();

        // set set_time button behavior
        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scheduleNotifications(view);
            }
        });

        // set toggle_notifications behavior
        toggleNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allowSet();
            }
        });

        return myView;
    }

    // Check to see if the alarm is active when opening the app to determine the toggle state.
    private boolean isAlarmActive() {
        return (PendingIntent.getBroadcast(context,
                100,
                new Intent(getActivity(), NotificationsReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);
    }

    /* If the toggle is turned on, let the user interact with the time picker and set button
    If the toggle is turned off, cancel the ongoing alarm and disable the time picker and set button
     */
    private void allowSet() {
        if (!toggleNotifications.isChecked()) {
            setTime.setEnabled(false);
            timePicker.setEnabled(false);
            cancelAlarm();
        }
        else {
            setTime.setEnabled(true);
            timePicker.setEnabled(true);
        }
    }

    /**
     * Cancels the ongoing alarm
     */
    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context.getApplicationContext(), NotificationsReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                100,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        try {
            alarmManager.cancel(pendingIntent);
        }
        catch (NullPointerException e) {
            System.out.println("Null pointer exception in " +
                    "NotificationsSettingFragment->cancelAlarm->alarmManager.cancel(pendingIntent");
            e.printStackTrace();
        }
    }

    private void scheduleNotifications(View view) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
        calendar.set(Calendar.MINUTE, timePicker.getMinute());

        Intent intent = new Intent(context.getApplicationContext(), NotificationsReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(),
                100,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        try {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);
        }
        catch (NullPointerException e) {
            System.out.println("Null pointer exception in" +
                    "NotificationsSettingFragment->scheduleNotifications->alarmManager.setRepeating(...)");
            e.printStackTrace();
        }

        Toast toast = Toast.makeText(context,
                getResources().getText(R.string.notifications_set)
                        + " "
                        + String.valueOf(timePicker.getHour())
                        + ":"
                        + String.valueOf(timePicker.getMinute())
                        + ".",
                Toast.LENGTH_LONG);
        toast.show();
    }
}