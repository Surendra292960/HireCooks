package com.test.sample.hirecooks.Activity.Home;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.test.sample.hirecooks.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
       /* Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service running");
        ContextCompat.startForegroundService(this, serviceIntent);*/

        Intent intent = new Intent(SplashActivity.this, LandingScreen.class);
        startActivity(intent);
        finish();
    }
}
