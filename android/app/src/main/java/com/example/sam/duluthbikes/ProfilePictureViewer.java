package com.example.sam.duluthbikes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class ProfilePictureViewer extends AppCompatActivity {

    ImageView mProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture_viewer);

        mProfilePicture = findViewById(R.id.profilePictureView);
        Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("image");
        Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        mProfilePicture.setImageBitmap(bm);
    }
}
