package com.example.sam.duluthbikes;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;


//app:headerLayout="@layout/nav_header"


import com.example.sam.duluthbikes.fragments.AboutFragment;
import com.example.sam.duluthbikes.fragments.DiscountFragment;
import com.example.sam.duluthbikes.fragments.EventsFragment;
import com.example.sam.duluthbikes.fragments.HomeFragment;
import com.example.sam.duluthbikes.fragments.ReportAppFragment;
import com.example.sam.duluthbikes.fragments.ReportFragment;
import com.example.sam.duluthbikes.fragments.RideHistoryFragment;
import com.example.sam.duluthbikes.fragments.SettingsFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import static java.lang.Math.abs;

/**
 * Home screen
 */
public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, ModelViewPresenterComponents.View {

    private int mRequestCode;
    private Presenter mPresenter;

    private String name = "";
    private String email = "";

    private GoogleMap mMap;

    Location mLastLocation;
    private boolean automaticTracking = false;
    private int counter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // R.id.toolbar = in menu_bar.xml
        //setSupportActionBar(toolbar);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // request permission to access location
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    mRequestCode);
            return;
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString("name");
            email = extras.getString("email");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);

        TextView Name = (TextView) hView.findViewById(R.id.studentEmail);
        TextView Email = (TextView) hView.findViewById(R.id.userName);

        Name.setText(name);
        Email.setText(email);

        ImageView img = (ImageView) hView.findViewById(R.id.imageView);
        Uri fileURL = getIntent().getData();

//        if (null != fileURL) {
//            Glide.with(getApplicationContext())
//                    .load(fileURL)
//                    .apply(RequestOptions.circleCropTransform())
//                    .into(img);
//        }

    }

    /**
     * Required by OnMapReadyCallback interface
     * Called when the map is ready to be used.
     * https://developers.google.com/android/reference/com/google/android/gms/maps/OnMapReadyCallback
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void startMainActivity(View view) {
        automaticTracking = false;
        startSession();
    }

    public void startSession(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("autoTracking", automaticTracking);
        intent.putExtra("value", false);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_home) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new HomeFragment())
                    .commit();
        } else if (id == R.id.nav_ride_history) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new RideHistoryFragment())
                    .commit();
        } else if (id == R.id.nav_events) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new EventsFragment())
                    .commit();
        } else if (id == R.id.nav_discount) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new DiscountFragment())
                    .commit();
        } else if (id == R.id.nav_report) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new ReportFragment())
                    .commit();
        } else if (id == R.id.nav_reportApp) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new ReportAppFragment())
                    .commit();
        } else if (id == R.id.nav_settings) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new SettingsFragment())
                    .commit();
        } else if (id == R.id.nav_about) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new AboutFragment())
                    .commit();
        } else if (id == R.id.nav_settings) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new SettingsFragment())
                    .commit();
        }
        else if (id == R.id.nav_leaderboard) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new LeaderboardFragment())
                    .commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void resetStatsClick(View view) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FireResetStatsDialogFragment resetDialog = new FireResetStatsDialogFragment();
        resetDialog.show(fragmentManager, "test");
    }


    public void signOutClick(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /** Created by Mackenzie Fulton
     *
     * Function to bring you to the notification settings fragment and notification settings view
     *
     * @param view the current view
     */
    public void notificationMenuClick(View view) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, new NotificationsSettingsFragment())
                .commit();
    }

    /*
    These 2 functions define the onClick functions to the buttons, and 2 public methods to call the
    presenter mediator methods
     */
    public JSONArray GetLocalLeaderboard() {
        JSONArray data = mPresenter.getLeaderboardFromServer(ModelViewPresenterComponents.LOCAL);
        return data;
    }

    public JSONArray GetGlobalLeaderboard() {
        JSONArray data = mPresenter.getLeaderboardFromServer(ModelViewPresenterComponents.GLOBAL);
        return data;
    }

    /*
    These below functions are required to implement to consider this class a View, as defined in
    ModelViewPresenterComponents
     */

    public void locationChanged(Location location) {

    }

    public void userResults(String results) {

    }

    public void setClient(GoogleApiClient googleApiClient) {

    }

    public GoogleApiClient getClient() {
        return null;
    }

}
