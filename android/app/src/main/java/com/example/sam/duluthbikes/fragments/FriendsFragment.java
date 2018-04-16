package com.example.sam.duluthbikes.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.example.sam.duluthbikes.R;

public class FriendsFragment extends Fragment {

    View myView;
    EditText mSearchBar;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_friends, container, false);

        getFriendsList();

        mSearchBar = myView.findViewById(R.id.friendSearchBar);
        mSearchBar.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if(i == EditorInfo.IME_ACTION_SEARCH) {
                    getFriend(mSearchBar.getText().toString());
                    handled = true;
                }
                return handled;
            }
        });

        return myView;
    }

    // TODO: Implement
    private void getFriend(String friend) {

    }

    // TODO: Implement
    private void getFriendsList() {

    }
}
