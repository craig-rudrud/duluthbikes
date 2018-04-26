package com.example.sam.duluthbikes.fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sam.duluthbikes.Presenter;
import com.example.sam.duluthbikes.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class FriendViewFragment extends Fragment {

    private View myView;
    private TextView mName;
    private boolean isFriend;
    private Button friendButton;
    private Presenter mPresenter;
    private int loginStatus;
    private File profile;
    private String personId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_view_friend, container, false);
        Bundle bundle = getArguments();

        mPresenter = new Presenter();

        profile = new File("sdcard/Profile.txt");
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (acct != null) {
            loginStatus = 2;
            personId = acct.getId();
        } else {
            loginStatus = (profile.exists()) ? 1 : 0;
            personId = getCurrentUser();
        }

        mName = myView.findViewById(R.id.personName);
        mName.setText(Objects.requireNonNull(bundle.get("name")).toString());

        ImageView mPicture = myView.findViewById(R.id.profilePicture);
        byte[] image = bundle.getByteArray("image");
        assert image != null;
        mPicture.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));

        isFriend = Objects.requireNonNull(bundle.get("isFriend")).toString().equals("true");

        friendButton = myView.findViewById(R.id.friendButton);
        friendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFriend) {
                    addFriend();
                }
                else {
                    removeFriend();
                }
            }
        });

        if(!isFriend) {
            friendButton.setText(getString(R.string.addFriend));
        }
        else {
            friendButton.setText(getString(R.string.removeFriend));
        }

        return myView;
    }

    private void addFriend() {
        boolean isFriendAdded = mPresenter.addFriendByUser(getCurrentUser(), mName.getText().toString());
        if(isFriendAdded) {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.friendAdded), Toast.LENGTH_SHORT).show();
            friendButton.setText(getString(R.string.removeFriend));
            isFriend = true;
        }
    }

    private void removeFriend() {
        boolean isFriendRemoved = mPresenter.removeFriendByUser(getCurrentUser(), mName.getText().toString());
        if(isFriendRemoved) {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.friendRemoved), Toast.LENGTH_SHORT).show();
            friendButton.setText(getString(R.string.addFriend));
            isFriend = false;
        }
    }

    private String getCurrentUser() {

        String username = "";

        switch(loginStatus) {
            case 2:
                username = personId;
                break;
            case 1:
                try {
                    FileInputStream in = new FileInputStream(profile);
                    Scanner s = new Scanner(in);
                    username = s.nextLine();
                    s.close();
                    in.close();
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
}