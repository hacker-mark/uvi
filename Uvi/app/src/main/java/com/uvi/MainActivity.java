package com.uvi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Mark on 18/04/2015.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button requestRideButton = (Button)findViewById(R.id.request_ride);
        requestRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RequestRideActivity.class);
                startActivity(intent);
            }
        });

        Button driveRideButton = (Button)findViewById(R.id.drive_ride);
        driveRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DriveRideActivity.class);
                startActivity(intent);
            }
        });
    }

}
