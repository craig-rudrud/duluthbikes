package com.example.dbwear;

import android.Manifest;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.wearable.activity.WearableActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.MapFragment;

import java.util.Map;

public class HomeActivity extends Fragment {

    View mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.activity_home, container, false);
        return mView;

    /*private TextView mTextView;
    private Button mStartButton;
    private Presenter mPresenter;
    private MapFragment mMapFragment;*/


    /*@Override
    protected void onCreate(Bundle savedInstanceState) {

        //super.onCreate(savedInstanceState);

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
        });*/
    }
}
