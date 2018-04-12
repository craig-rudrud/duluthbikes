package com.example.sam.duluthbikes.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sam.duluthbikes.LoginActivity;
import com.example.sam.duluthbikes.ProfilePictureViewer;
import com.example.sam.duluthbikes.R;
import com.example.sam.duluthbikes.UnitConverter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Scanner;

public class SettingsFragment extends Fragment {

    View myView;
    Button loginButton;
    Button mEraseAllRides;
    File profile;
    TextView username;
    TextView email;
    ImageView profilePicture;
    TextView totalDistance;
    TextView totalTime;

    GoogleSignInClient mGoogleSignInClient;
    String personName;
    String personEmail;
    Uri personPhoto;

    /*
    LOGIN STATUS CODES:
        0 = not logged in
        1 = logged in
        2 = logged in with Google
     */
    int loginStatus;

    static final int UPLOAD_IMAGE = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_settings, container, false);
        DecimalFormat df = new DecimalFormat("#.##");
        UnitConverter converter = new UnitConverter();

        profile = new File("sdcard/Profile.txt");
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (acct != null) {
            personName = acct.getDisplayName();
            personEmail = acct.getEmail();
            personPhoto = acct.getPhotoUrl();
            loginStatus = 2;
        }
        else {
            loginStatus = (profile.exists()) ? 1 : 0;
        }

        username = myView.findViewById(R.id.usernameTextView);
        email = myView.findViewById(R.id.emailTextView);
        profilePicture = myView.findViewById(R.id.profilePicture);

        username.setText(getUsername());
        email.setText(getEmail());
        setProfilePicture();

        SharedPreferences totalStats = getContext().getSharedPreferences(getString(R.string.lifetimeStats_file_key), 0);
        totalDistance = myView.findViewById(R.id.totalDistanceAmt);
        totalTime = myView.findViewById(R.id.totalTimeAmt);

        Float totDist = totalStats.getFloat(getString(R.string.lifetimeStats_totDist), 0);
        Double mTotDist = Double.valueOf(df.format(converter.getDistInKm(totDist.doubleValue())));
        totalDistance.setText(String.valueOf(mTotDist)+" km");

        Long totTime = totalStats.getLong(getString(R.string.lifetimeStats_totTime), 0);
        totalTime.setText(converter.convertHoursMinSecToString(totTime));

        loginButton = myView.findViewById(R.id.loginButton);
        makeLoginButton();

        mEraseAllRides = myView.findViewById(R.id.button_eraseAllRides);
        makeEraseAllRidesButton();

        return myView;
    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

    }

    private void makeEraseAllRidesButton() {

        mEraseAllRides.setOnClickListener(new View.OnClickListener() {
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
    }

    private void eraseAllRides() {

        SharedPreferences totalStats = getActivity()
                .getSharedPreferences(getString(R.string.lifetimeStats_file_key), 0);
        totalStats.edit().clear().commit();
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    private void makeLoginButton() {

        loginButton.setText((loginStatus > 0) ? getString(R.string.logout) : getString(R.string.login));
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginStatus > 0) {
                    DialogInterface.OnClickListener dialogBox = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    if (loginStatus == 2) {
                                        profile.delete();
                                        mGoogleSignInClient.signOut()
                                                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Intent login = new Intent(getActivity(), LoginActivity.class);
                                                        startActivity(login);
                                                    }
                                                });
                                    }
                                    else if (loginStatus == 1) {
                                        profile.delete();
                                        Intent login = new Intent(getActivity(), LoginActivity.class);
                                        startActivity(login);
                                    }
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
    }

    private String getUsername() {

        String username = "";

        switch(loginStatus) {
            case 2:
                username = personName;
                break;
            case 1:
                try {
                    FileInputStream in = new FileInputStream(profile);
                    Scanner s = new Scanner(in);
                    username = s.nextLine();
                    s.close();
                    in.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 0:
                username = getString(R.string.noUsername);
                break;
        }

        return username;
    }

    private String getEmail() {

        String email = "";

        switch(loginStatus) {
            case 2:
                email = personEmail;
                break;
            case 1:
                try {
                    FileInputStream in = new FileInputStream(profile);
                    Scanner s = new Scanner(in);
                    for (int i = 0; i < 2; i++) {
                        email = s.nextLine();
                    }
                    s.close();
                    in.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 0:
                break;
        }

        return email;
    }

    private void setProfilePicture() {

        // TODO: Retrieve picture from server as byte array then decode it as an image
        if (loginStatus == 2) {
            String url = "https:/lh5.googleusercontent.com" + personPhoto.getPath();
            Picasso.with(getContext()).load(url).placeholder(R.drawable.default_profile_pic)
                    .error(R.drawable.default_profile_pic)
                    .into(profilePicture, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                        }
                    });
        }
        else {
            profilePicture.setImageResource(R.drawable.default_profile_pic);
        }

        // Allow the user to VIEW their profile picture when they tap on it
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent = new Intent(getActivity(), ProfilePictureViewer.class);

                Bitmap b = ((BitmapDrawable)profilePicture.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                Bundle extras = new Bundle();
                extras.putByteArray("image", stream.toByteArray());
                viewIntent.putExtras(extras);

                startActivity(viewIntent);
            }
        });

        // Allow the user to UPDATE their profile picture when they long press it
        profilePicture.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Source: https://stackoverflow.com/a/9107983
                // Modified by Duluth Bikes
                startActivityForResult(new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                        UPLOAD_IMAGE);
                return true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Source: https://stackoverflow.com/a/9107983
        // Heavily modified by Duluth Bikes
        if(requestCode == UPLOAD_IMAGE && resultCode == Activity.RESULT_OK) {

            Uri selectedImage = data.getData();
            Bitmap bm;
            HttpURLConnection urlConnection;

            try {
                URL url = new URL("http://ukko.d.umn.edu:23405");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.setRequestProperty("Content-Type", "image/jpeg");

                bm = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage);
                ByteArrayOutputStream stream = (ByteArrayOutputStream) urlConnection.getOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                stream.close();
                urlConnection.disconnect();

                Toast.makeText(getActivity().getApplicationContext(),
                        getString(R.string.profilePicUpdateGood), Toast.LENGTH_SHORT)
                        .show();

                profilePicture.setImageBitmap(bm);
            } catch (IOException e) {
                e.printStackTrace();

                Toast.makeText(getActivity().getApplicationContext(),
                        getString(R.string.profilePicUpdateFail), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
