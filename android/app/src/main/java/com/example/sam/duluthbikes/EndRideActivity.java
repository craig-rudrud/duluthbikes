package com.example.sam.duluthbikes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * End of the Ride activity
 * Displays statistics of the ride and allows to return to the home screen
 */

public class EndRideActivity extends AppCompatActivity{
    Bundle data;
    Float totDistance;
    Long totTime;
    TextView dist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_ride);

        UnitConverter converter = new UnitConverter();

        TextView rideDate = findViewById(R.id.dateLabel);
        dist = findViewById(R.id.distance);
        TextView timeLapsed = findViewById(R.id.timeLapsed);
        TextView avSpeed = findViewById(R.id.averageSpeed);
        TextView startTime = findViewById(R.id.startTime);
        TextView endTime = findViewById(R.id.endTime);

        data = getIntent().getExtras();
        Long sTime =  data.getLong("startTime");
        Long fTime = data.getLong("endTime");
        Double distance = data.getDouble("dis");

        //data format definitions
        SimpleDateFormat timef = new SimpleDateFormat("hh:mm:ss a");
        SimpleDateFormat datef = new SimpleDateFormat("MM-dd-yyyy");
        DecimalFormat df = new DecimalFormat("#.##");

        Long timelapse = fTime - sTime;

        initializeTotals();

        //format data entries
        Double distKM = Double.valueOf(df.format(converter.getDistInKm(distance)));
        Double averKmH = Double.valueOf(df.format(converter.getKmPerHour(distance, timelapse)));
        String dateOfRide = datef.format(fTime);
        String timeFinish = timef.format(fTime);
        String timeStart = timef.format(sTime);

        rideDate.setText(dateOfRide);
        dist.setText(Double.toString(distKM));
        timeLapsed.setText(converter.convertHoursMinSecToString(timelapse));
        avSpeed.setText(Double.toString(averKmH));
        startTime.setText(timeStart);
        endTime.setText(timeFinish);

        Button shareButton = (Button)findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareRide();
            }
        });

    }


    private void initializeTotals(){
        SharedPreferences totalstats = getSharedPreferences(getString(R.string.lifetimeStats_file_key), 0);
        totDistance = totalstats.getFloat(getString(R.string.lifetimeStats_totDist), 0);
        totTime = totalstats.getLong(getString(R.string.lifetimeStats_totTime), 0);
    }


    public void doneWithRide(View view){
        Intent menu = new Intent(this.getApplicationContext(),MenuActivity.class);
        startActivity(menu);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    public void shareRide() {

        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        String path = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpeg";

        View v1 = findViewById(R.id.statsLayout);
        v1.setDrawingCacheEnabled(true);
        Bitmap bitmap = (addWatermark(getResources(), Bitmap.createBitmap(v1.getDrawingCache())));
        v1.setDrawingCacheEnabled(false);

        File image = new File(path);
        try {
            FileOutputStream outputStream = new FileOutputStream(image);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(image));
        shareIntent.putExtra(Intent.EXTRA_TEXT, "I just biked " + dist.getText().toString() + " km with Duluth Bikes!");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.shareRideTo)));
    }

    /**
     * @author Android Guys
     * Source URL: https://androidluckyguys.wordpress.com/2017/08/14/add-watermark-to-captured-image/
     *
     * Embeds an image watermark over a source image to produce
     * a watermarked one.
     * @param res The image file used as the watermark.
     * @param source The source image file.
     *
     * Adds a watermark on the given image.
     */
    public static Bitmap addWatermark(Resources res, Bitmap source) {
        int w, h;
        Canvas c;
        Paint paint;
        Bitmap bmp, watermark;
        Matrix matrix;
        float scale;
        RectF r;
        w = source.getWidth();
        h = source.getHeight();
        // Create the new bitmap
        bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
        // Copy the original bitmap into the new one
        c = new Canvas(bmp);
        c.drawBitmap(source, 0, 0, paint);
        // Load the watermark
        watermark = BitmapFactory.decodeResource(res, R.drawable.duluth_bikes_logo);
        // Scale the watermark to be approximately 40% of the source image height
        scale = (float) (((float) h * 0.25) / (float) watermark.getHeight());
        // Create the matrix
        matrix = new Matrix();
        matrix.postScale(scale, scale);
        // Determine the post-scaled size of the watermark
        r = new RectF(0, 0, watermark.getWidth(), watermark.getHeight());
        matrix.mapRect(r);
        // Move the watermark to the bottom right corner
        matrix.postTranslate(w - r.width(), h - r.height());
        // Draw the watermark
        c.drawBitmap(watermark, matrix, paint);
        // Free up the bitmap memory
        watermark.recycle();
        return bmp;
    }
}
