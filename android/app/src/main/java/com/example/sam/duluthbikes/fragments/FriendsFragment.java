package com.example.sam.duluthbikes.fragments;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.example.sam.duluthbikes.Friend;
import com.example.sam.duluthbikes.FriendsAdapter;
import com.example.sam.duluthbikes.Model;
import com.example.sam.duluthbikes.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class FriendsFragment extends Fragment {

    private View myView;
    private EditText mSearchBar;
    private RecyclerView mFriendsView;
    private FriendsAdapter mFriendsAdapter;
    private ArrayList<Friend> friendList;
    private ProgressBar mSearchProgress;
    private File profile;
    private String personId;
    private int loginStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_friends, container, false);

        profile = new File("sdcard/Profile.txt");
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (acct != null) {
            loginStatus = 2;
            personId = acct.getId();
        } else {
            loginStatus = (profile.exists()) ? 1 : 0;
            personId = getUsername();
        }

        friendList = getFriendList();

        mSearchBar = myView.findViewById(R.id.friendSearchBar);
        mSearchBar.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    Friend friend = getFriend(mSearchBar.getText().toString());
                    if (friend != null) {
                        handled = true;
                    }
                }
                return handled;
            }
        });

        mFriendsView = myView.findViewById(R.id.friendsView);
        mFriendsView.setHasFixedSize(true);
        mFriendsView.setItemAnimator(new DefaultItemAnimator());

        mFriendsAdapter = new FriendsAdapter(getContext(), friendList);
        mFriendsView.setAdapter(mFriendsAdapter);
        mFriendsView.setLayoutManager(new LinearLayoutManager(getContext()));

        mSearchProgress = myView.findViewById(R.id.friendSearchProgress);
        mSearchProgress.setVisibility(View.INVISIBLE);

        return myView;
    }

    // TODO: Implement
    private Friend getFriend(String friendName) {
        Friend friend = null;

        return friend;
    }

    private ArrayList<Friend> getFriendList() {
        ArrayList<Friend> list = new ArrayList<>();

        try {
            Model model = new Model();
            JSONArray array = model.getFriends("test2");
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    Log.d("name: ", array.getString(i));
                    list.add(new Friend(array.getString(i), BitmapFactory.decodeResource(getResources(), R.drawable.default_profile_pic)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Collections.sort(list, new Comparator<Friend>() {
            @Override
            public int compare(Friend friend1, Friend friend2) {
                String name1 = friend1.getName();
                String name2 = friend2.getName();
                return name1.compareToIgnoreCase(name2);
            }
        });

        return list;
    }

    private String getUsername() {

        String username = null;

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
