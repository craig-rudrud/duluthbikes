package com.example.sam.duluthbikes;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sam.duluthbikes.fragments.FriendsFragment;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView friendName;
        public ImageView friendPicture;

        public ViewHolder(View v) {
            super(v);
            friendName = v.findViewById(R.id.friendName);
            friendPicture = v.findViewById(R.id.friendPicture);
        }
    }

    private List<Friend> mFriends;

    public FriendsAdapter(List<Friend> friends) {
        mFriends = friends;
    }

    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View friendView = inflater.inflate(R.layout.layout_friend, parent, false);

        return new ViewHolder(friendView);
    }

    @Override
    public void onBindViewHolder(FriendsAdapter.ViewHolder viewHolder, int position) {
        Friend friend = mFriends.get(position);

        TextView textView = viewHolder.friendName;
        textView.setText(friend.getName());

        ImageView imageView = viewHolder.friendPicture;
        imageView.setImageBitmap(friend.getProfilePicture());
    }

    @Override
    public int getItemCount() {
        return mFriends.size();
    }
}