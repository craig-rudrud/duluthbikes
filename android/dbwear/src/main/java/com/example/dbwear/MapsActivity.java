package com.example.dbwear;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.app.FragmentManager;

import android.os.Bundle;
import android.support.wear.widget.SwipeDismissFrameLayout;
import android.support.wearable.activity.WearableActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MapsActivity extends WearableActivity implements OnMapReadyCallback, MVP.View {

    /**
     * Map is initialized when it's fully loaded and ready to be used.
     *
     * @see #onMapReady(com.google.android.gms.maps.GoogleMap)
     */
    private GoogleMap mMap;
    private Location mLastLocation;
    ToggleButton mButton;
    ToggleButton mPauseResume;
    Presenter mPresenter;
    private ArrayList<LatLng> points;
    private LocationData locationData;
    private boolean animate;

    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        // Enables always on.
        setAmbientEnabled();

        setContentView(R.layout.activity_maps);

        animate = true;

        mPresenter = new Presenter(this.getApplicationContext(), this, this);
        mPresenter.clickStart();

        points = new ArrayList<>();

        final SwipeDismissFrameLayout swipeDismissRootFrameLayout =
                (SwipeDismissFrameLayout) findViewById(R.id.swipe_dismiss_root_container);
        final FrameLayout mapFrameLayout = (FrameLayout) findViewById(R.id.map_container);

        // Enables the Swipe-To-Dismiss Gesture via the root layout (SwipeDismissFrameLayout).
        // Swipe-To-Dismiss is a standard pattern in Wear for closing an app and needs to be
        // manually enabled for any Google Maps Activity. For more information, review our docs:
        // https://developer.android.com/training/wearables/ui/exit.html
        swipeDismissRootFrameLayout.addCallback(new SwipeDismissFrameLayout.Callback() {
            @Override
            public void onDismissed(SwipeDismissFrameLayout layout) {
                // Hides view before exit to avoid stutter.
                layout.setVisibility(View.GONE);
                finish();
            }
        });

        // Adjusts margins to account for the system window insets when they become available.
        swipeDismissRootFrameLayout.setOnApplyWindowInsetsListener(
                new View.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsets onApplyWindowInsets(View view, WindowInsets insets) {
                        insets = swipeDismissRootFrameLayout.onApplyWindowInsets(insets);

                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mapFrameLayout.getLayoutParams();

                        // Sets Wearable insets to FrameLayout container holding map as margins
                        params.setMargins(
                                insets.getSystemWindowInsetLeft(),
                                insets.getSystemWindowInsetTop(),
                                insets.getSystemWindowInsetRight(),
                                insets.getSystemWindowInsetBottom());

                        mapFrameLayout.setLayoutParams(params);

                        return insets;
                    }
                });

        pauseListener();
        mPauseResume.setEnabled(false);

        buttonListener();

        // Obtain the MapFragment and set the async listener to be notified when the map is ready.
        MapFragment mapFragment =
                (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        mPauseResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whenClicked(view);
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startClicker(view);
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Map is ready to be used.
        mMap = googleMap;

        // Inform user how to close app (Swipe-To-Close).
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(getApplicationContext(), R.string.intro_text, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

    }


    public void buttonListener() {
        mButton = (ToggleButton) findViewById(R.id.toggleButton);
        mButton.setTextOn(getString(R.string.text_on));
        mButton.setTextOff(getString(R.string.text_off));
        mButton.setChecked(true);
    }

    public void pauseListener() {
        mPauseResume = (ToggleButton) findViewById(R.id.toggleButton2);
        mPauseResume.setTextOn(getString(R.string.pause));
        mPauseResume.setTextOff(getString(R.string.resume));
        mPauseResume.setChecked(true);
    }

    public void startClicker(View v) {
        if (!mButton.isChecked()) {
            mButton.setChecked(false);
            mPauseResume.setEnabled(true);
        } else if (mButton.isChecked()) {
            mButton.setChecked(true);
            mPauseResume.setEnabled(false);
            locationData.getOurInstance(this).dataReset();
            mPresenter.finishRideButton();
        }
    }

    public void whenClicked(View v) {
       if (!mPauseResume.isChecked()) {
           mPauseResume.setChecked(false);
           try {
               mMap.setMyLocationEnabled(true);
           } catch (SecurityException e) {
               e.printStackTrace();
           }
           mPresenter.pauseRideButton();
       } else if (mPauseResume.isChecked()) {
           mPauseResume.setChecked(true);
           try {
               mMap.setMyLocationEnabled(true);
           } catch (SecurityException e) {
               e.printStackTrace();
           }
           mPresenter.connectApi();
       }
    }

    @Override
    public GoogleApiClient getClient() {
        return LocationData.getOurInstance(this).getGoogleApiClient();
    }

    @Override
    public void setClient(GoogleApiClient googleApiClient) {
        LocationData.getOurInstance(this).setGoogleApiClient(googleApiClient);
    }

    @Override
    public void locationChanged(Location location) {
        if(location != null) {
            setLastLocation(location);
            LatLng latLng = new LatLng(getLastLocation().getLatitude(), getLastLocation().getLongitude());
            points.add(latLng);
            if (!mMap.isMyLocationEnabled() && mButton.isChecked()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    mMap.setMaxZoomPreference(18);
                }
            }
            locationData.getOurInstance(this.getBaseContext()).addPoint(latLng, location);
            LatLngBounds.Builder bounds = LocationData.getOurInstance(this.getBaseContext()).getBuilder();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds.build(), 100);
            if (animate) {
                animate = false;
                mMap.animateCamera(cu);
            } else mMap.moveCamera(cu);

        }
    }

    public Location getLastLocation(){ return mLastLocation; }
    public void setLastLocation(Location curr) { mLastLocation = curr; }

}
