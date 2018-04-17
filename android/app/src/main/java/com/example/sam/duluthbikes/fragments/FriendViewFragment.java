package com.example.sam.duluthbikes.fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sam.duluthbikes.R;

public class FriendViewFragment extends Fragment {

    private View myView;
    private TextView mName;
    private ImageView mPicture;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_view_friend, container, false);
        Bundle bundle = getArguments();

        mName = myView.findViewById(R.id.personName);
        mName.setText(bundle.get("name").toString());

        mPicture = myView.findViewById(R.id.profilePicture);
        byte[] image = bundle.getByteArray("image");
        mPicture.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));

        return myView;
    }
}