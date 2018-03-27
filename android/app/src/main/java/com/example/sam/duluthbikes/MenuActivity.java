package com.example.sam.duluthbikes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // request permission to access location
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    mRequestCode);
            return;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Presenter mPresenter = new Presenter(this.getApplicationContext(), this, this);
        mPresenter.clickStart();
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
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public Location getLastLocation(){ return mLastLocation; }

    public void setLastLocation(Location curr) { mLastLocation = curr; }

    @Override
    public void locationChanged(Location location) {
        if (location != null) {
            if (mLastLocation == null) {
                setLastLocation(location);
            }
            float time = (mLastLocation.getTime() - location.getTime()) / 1000;
            float speed = location.distanceTo(mLastLocation) / time;
            speed = Math.abs(speed);
            setLastLocation(location);
            if(automaticTracking) {
                if (speed*3.6 > 10) {
                    counter++;
                }
                if ((speed*3.6 >= 10) && (counter >= 4)) {
                    startSession();
                }
            }
        }
    }

    @Override
    public void userResults(String results) {

    }

    @Override
    public void setClient(GoogleApiClient googleApiClient) {
        LocationData.getOurInstance(this).setGoogleClient(googleApiClient);
    }

    @Override
    public GoogleApiClient getClient() {
        return LocationData.getOurInstance(this).getGoogleClient();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
    }

    public void toggleAutoTracking(View view){
        automaticTracking = !automaticTracking;
    }
}


