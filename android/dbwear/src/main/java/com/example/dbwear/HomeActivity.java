package com.example.dbwear;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.MapFragment;

import java.util.Map;

public class HomeActivity extends WearableActivity {

    private TextView mTextView;
    private Button mStartButton;
    private Presenter mPresenter;
    private MapFragment mMapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //mTextView = (TextView) findViewById(R.id.text);
        mStartButton = (Button) findViewById(R.id.button);

        // Enables Always-on
        setAmbientEnabled();

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.clickStart();
                Intent intent = new Intent(HomeActivity.this, MapsActivity.class);
                startActivity(intent);
                //mMapFragment = (MapFragment) getFragmentManager().


            }
        });
    }
}
