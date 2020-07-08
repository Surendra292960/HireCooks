package com.test.sample.hirecooks.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.Activity.Users.UserSignInActivity;
import com.test.sample.hirecooks.Activity.Users.UserSignUpActivity;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import androidx.appcompat.app.AppCompatActivity;

public class LandingScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_screen);
        initializeViews();
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void initializeViews() {
        Button buttonSignInUser, buttonSignUpUser;
        buttonSignInUser = findViewById(R.id.buttonSignInUser);
        buttonSignUpUser = findViewById(R.id.buttonSignUpUser);
        buttonSignInUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LandingScreen.this, UserSignInActivity.class));
                finish();
            }
        });
        buttonSignUpUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LandingScreen.this, UserSignUpActivity.class));
                finish();
            }
        });
    }
}
