package com.example.sam.duluthbikes.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.sam.duluthbikes.MyAdapter;
import com.example.sam.duluthbikes.R;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {

    private View myView;
    private EditText mSearchBar;
    private RecyclerView mFriendsView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private String[] myDataset;
    private ArrayList<Friend> friendList;
    private ProgressBar mSearchProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_friends, container, false);

        friendList = getFriendList();

        mSearchBar = myView.findViewById(R.id.friendSearchBar);
        mSearchBar.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if(i == EditorInfo.IME_ACTION_SEARCH) {
                    Friend friend = getFriend(mSearchBar.getText().toString());
                    if(friend != null) {
                        handled = true;
                    }
                }
                return handled;
            }
        });

        mFriendsView = myView.findViewById(R.id.friendsView);
        mFriendsView.setHasFixedSize(true);
        mFriendsView.setItemAnimator(new DefaultItemAnimator());

        mLayoutManager = new LinearLayoutManager(getContext());
        mFriendsView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(myDataset);
        mFriendsView.setAdapter(mAdapter);

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
        ArrayList<Friend> list = null;

        //TODO: Get each friend and add them to the list

        return list;
    }
}
