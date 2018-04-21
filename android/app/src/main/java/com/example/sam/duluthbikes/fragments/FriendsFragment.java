package com.example.sam.duluthbikes.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.example.sam.duluthbikes.Friend;
import com.example.sam.duluthbikes.FriendsAdapter;
import com.example.sam.duluthbikes.Presenter;
import com.example.sam.duluthbikes.R;
import com.example.sam.duluthbikes.SearchFriendAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
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
    private Presenter mPresenter;
    private int numberOfFriends;
    private TextView friendCountView;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_friends, container, false);

        mPresenter = new Presenter();

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
        numberOfFriends = friendList.size();
        friendCountView = myView.findViewById(R.id.friendCount);
        if(mPresenter.getLoginStatus()) {
            friendCountView.setText("You are logged in");
        }
        else {
            friendCountView.setText("You are not logged in");
        }

        mSearchBar = myView.findViewById(R.id.friendSearchBar);
        mSearchBar.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard();
                    Friend friend = getFriend(mSearchBar.getText().toString());
                    if (friend != null) {
                        friendList.clear();
                        friendList.add(friend);
                        mFriendsView.setAdapter(new SearchFriendAdapter(getContext(), friendList));
                        mFriendsView.setLayoutManager(new LinearLayoutManager(getContext()));
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

    private Friend getFriend(String friendName) {
        Friend friend = null;
        JSONArray array = mPresenter.getUsernames();
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                try {
                    if (friendName.equals(array.getString(i))) {
                        String result = array.getString(i);
                        friend = new Friend(result, BitmapFactory.decodeResource(getResources(), R.drawable.default_profile_pic));
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return friend;
    }

    private ArrayList<Friend> getFriendList() {
        ArrayList<Friend> list = new ArrayList<>();

        try {
            JSONArray array = mPresenter.getFriends(getUsername());
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

    private void hideKeyboard() {
        InputMethodManager input = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (input != null) {
            input.hideSoftInputFromWindow(Objects.requireNonNull(getActivity().getCurrentFocus()).getWindowToken(), 0);
        }
    }
}
