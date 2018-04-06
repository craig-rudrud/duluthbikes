package com.example.sam.duluthbikes.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sam.duluthbikes.LoginActivity;
import com.example.sam.duluthbikes.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class SettingsFragment extends Fragment {

    View myView;
    Button loginButton;
    Button eraseAllRides;
    File profile;
    TextView username;
    TextView email;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_settings, container, false);

        profile = new File("sdcard/Profile.txt");

        final boolean isLoggedIn = getLoginStatus();

        username = myView.findViewById(R.id.usernameTextView);
        email = myView.findViewById(R.id.emailTextView);
        if(isLoggedIn) {
            username.setText(getString(R.string.Username) + ": " + getUsername());
            email.setText(getString(R.string.prompt_email) + ": " + getEmail());
        }
        else {
            username.setText(getString(R.string.noUsername));
            email.setText("");
        }

        loginButton = (Button)myView.findViewById(R.id.loginButton);
        loginButton.setText((isLoggedIn) ? getString(R.string.logout) : getString(R.string.login));
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLoggedIn) {
                    DialogInterface.OnClickListener dialogBox = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    profile.delete();
                                    mGoogleSignInClient.signOut()
                                            .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Intent login = new Intent(getActivity(), LoginActivity.class);
                                                    startActivity(login);
                                                }
                                            });
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(myView.getContext());
                    builder.setMessage(getString(R.string.logoutMessage))
                            .setPositiveButton("Yes", dialogBox)
                            .setNegativeButton("No", dialogBox)
                            .show();
                }
                else {
                    Intent login = new Intent(getActivity(), LoginActivity.class);
                    startActivity(login);
                }
            }
        });

        eraseAllRides = (Button)myView.findViewById(R.id.button_eraseAllRides);
        eraseAllRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogBox = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choice) {
                        switch (choice) {
                            case DialogInterface.BUTTON_POSITIVE:
                                eraseAllRides();
                                Toast.makeText(getActivity().getApplicationContext(),
                                        getString(R.string.eraseAllRidesToast), Toast.LENGTH_SHORT)
                                        .show();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(myView.getContext());
                builder.setMessage(getString(R.string.eraseAllRidesPopup))
                        .setPositiveButton("Yes", dialogBox)
                        .setNegativeButton("No", dialogBox)
                        .show();
            }
        });

        return myView;
    }

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

    }

    private void eraseAllRides() {

        SharedPreferences totalStats = getActivity()
                .getSharedPreferences(getString(R.string.lifetimeStats_file_key), 0);
        totalStats.edit().clear().commit();
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    private boolean getLoginStatus() {

        return profile.exists();
    }

    private String getUsername() {

        String username = "";

        try {
            FileInputStream in = new FileInputStream(profile);
            Scanner s = new Scanner(in);
            username = s.nextLine();
            s.close();
            in.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return username;
    }

    private String getEmail() {

        String email = "";

        try {
            FileInputStream in = new FileInputStream(profile);
            Scanner s = new Scanner(in);
            while(s.hasNextLine()) {
                email = s.nextLine();
            }
            s.close();
            in.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return email;
    }
}
