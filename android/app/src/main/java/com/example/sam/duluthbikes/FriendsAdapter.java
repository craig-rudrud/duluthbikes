package com.example.sam.duluthbikes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sam.duluthbikes.fragments.FriendViewFragment;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView friendName;
        public ImageView friendPicture;

        public ViewHolder(View v) {
            super(v);
            friendName = v.findViewById(R.id.friendName);
            friendPicture = v.findViewById(R.id.friendPicture);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();

                    bundle.putString("name", friendName.getText().toString());

                    Bitmap b = ((BitmapDrawable)friendPicture.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    b.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    bundle.putByteArray("image", stream.toByteArray());

                    bundle.putString("isFriend", "false");

                    FriendViewFragment friendViewFragment = new FriendViewFragment();
                    friendViewFragment.setArguments(bundle);

                    android.support.v4.app.FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, friendViewFragment)
                            .addToBackStack("FriendsFragment")
                            .commit();
                }
            });
        }
    }

    private List<Friend> mFriends;
    private Context mContext;

    public FriendsAdapter(Context context, List<Friend> friends) {
        mContext = context;
        mFriends = friends;
    }

    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
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