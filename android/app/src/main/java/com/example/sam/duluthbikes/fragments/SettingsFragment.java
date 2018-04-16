package com.example.sam.duluthbikes.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sam.duluthbikes.LoginActivity;
import com.example.sam.duluthbikes.Model;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
    String personId;
    Uri personPhoto;

    /*
    LOGIN STATUS CODES:
        0 = not logged in
        1 = logged in
        2 = logged in with Google
     */
    int loginStatus;

    static final int UPLOAD_IMAGE = 1;

    String localPath;

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
            personId = acct.getId();
            personPhoto = acct.getPhotoUrl();
            loginStatus = 2;
        }
        else {
            loginStatus = (profile.exists()) ? 1 : 0;
            personId = getUsername();
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

        localPath = getLocalPath();

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

        if (loginStatus == 0) {
            profilePicture.setImageResource(R.drawable.default_profile_pic);
        }
        else {
            File file = new File(getLocalPath());
            if (!file.exists()) {
                // Retrieve the image from the server
                Model model = new Model();
                String image = model.getPicture(personId+getString(R.string.profilePicLocation));
                if(image == null) {
                    byte[] data = Base64.decode(image, Base64.DEFAULT);
                    Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                    profilePicture.setImageBitmap(bm);
                }
                else {
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
                    } else if (loginStatus == 1) {
                        profilePicture.setImageResource(R.drawable.default_profile_pic);
                    }
                }
            }
            else {
                Picasso.with(getContext()).load(file).placeholder(R.drawable.default_profile_pic)
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

        if(requestCode == UPLOAD_IMAGE) {
            if(resultCode == Activity.RESULT_OK) {
                Uri image = data.getData();
                try {
                    Bitmap bm = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), image);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] bytes = stream.toByteArray();
                    String string = Base64.encodeToString(bytes, Base64.DEFAULT);

                    Model model = new Model();
                    model.sendPicture("", personId+getString(R.string.profilePicLocation), string);

                    FileOutputStream out = new FileOutputStream(localPath);
                    out.write(bytes);
                    out.close();

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

    private String getLocalPath() {
        String path = null;
        switch(loginStatus) {
            case 2:
                path = "sdcard/" + personId + getString(R.string.profilePicLocation) + ".jpeg";
                break;
            case 1:
                path = "sdcard/" + getUsername() + getString(R.string.profilePicLocation) + ".jpeg";
                break;
            case 0:
                path = "sdcard/" + getUsername() + getString(R.string.profilePicLocation) + ".jpeg";
                break;
        }
        return path;
    }
}
