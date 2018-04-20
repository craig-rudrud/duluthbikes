package com.example.sam.duluthbikes.fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sam.duluthbikes.Model;
import com.example.sam.duluthbikes.R;

import java.util.Objects;

public class FriendViewFragment extends Fragment {

    private Model model;
    private View myView;
    private TextView mName;
    private boolean isFriend;
    private Button friendButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_view_friend, container, false);
        Bundle bundle = getArguments();

        model = new Model();

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
        boolean isFriendAdded = model.addFriend(mName.getText().toString());
        if(isFriendAdded) {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.friendAdded), Toast.LENGTH_SHORT).show();
            friendButton.setText(getString(R.string.removeFriend));
            isFriend = true;
        }
    }

    private void removeFriend() {
        boolean isFriendRemoved = model.removeFriend(mName.getText().toString());
        if(isFriendRemoved) {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.friendRemoved), Toast.LENGTH_SHORT).show();
            friendButton.setText(getString(R.string.addFriend));
            isFriend = false;
        }
    }
}