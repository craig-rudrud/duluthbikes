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

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;

import java.text.DecimalFormat;

/**
 * Home screen
 */

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, ModelViewPresenterComponents.View
        {

    private int mRequestCode;
    Location mLastLocation;
    float speed;


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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
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
                if(location!=null) {
                    setLastLocation(location);
                    LatLng latLng =
                            new LatLng(getLastLocation().getLatitude(), getLastLocation().getLongitude());
                    LocationData locationData = null;
                    locationData.getOurInstance(this.getBaseContext()).addPoint(latLng, location);
                    LatLngBounds.Builder bounds = LocationData.getOurInstance(this.getBaseContext()).getBuilder();
                    float time = (mLastLocation.getTime() - location.getTime()) / 1000;
                    float speed = location.distanceTo(mLastLocation) / time;
                    double sp = Math.sqrt(Math.pow(location.getLongitude() - mLastLocation.getLongitude(),2) + Math.pow(location.getLatitude() - mLastLocation.getLatitude(),2)) / (location.getTime() - this.mLastLocation.getTime());
                    location.setSpeed((float)sp);
                    if(location.getSpeed() > 0 ){
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
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
        }