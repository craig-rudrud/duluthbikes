package com.example.sam.duluthbikes;

import android.graphics.Bitmap;

public class Friend {

    private String mName;
    private Bitmap mProfilePicture;

    public Friend(String mName, Bitmap mProfilePicture) {
        this.mName = mName;
        this.mProfilePicture = mProfilePicture;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public Bitmap getProfilePicture() {
        return mProfilePicture;
    }

    public void setProfilePicture(Bitmap mProfilePicture) {
        this.mProfilePicture = mProfilePicture;
    }
}
