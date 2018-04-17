package com.example.sam.duluthbikes.fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import com.example.sam.duluthbikes.FriendsAdapter;
import com.example.sam.duluthbikes.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FriendsFragment extends Fragment {

    private View myView;
    private EditText mSearchBar;
    private RecyclerView mFriendsView;
    private FriendsAdapter mFriendsAdapter;
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
        list.add(new Friend("Craig Rudrud", BitmapFactory.decodeResource(getResources(), R.drawable.default_profile_pic)));

        //TODO: Get each friend and add them to the list

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

    public void viewFriend(View v) {
        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, new FriendViewFragment())
                .addToBackStack("FriendsFragment")
                .commit();
    }
}
