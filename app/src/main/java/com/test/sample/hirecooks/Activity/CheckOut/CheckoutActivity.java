package com.test.sample.hirecooks.Activity.CheckOut;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.fragment.app.FragmentTransaction;

import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.BaseActivity;
import com.test.sample.hirecooks.Fragments.ConfirmFragment;
import com.test.sample.hirecooks.R;

import java.util.Objects;

public class CheckoutActivity extends BaseActivity {

    private View appRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
       // getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("CheckOut");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, new ConfirmFragment());
        ft.commit();

        intiViews();
    }

    private void intiViews() {
        appRoot = findViewById(R.id.appRoot);
    }

    @Override
    public void onBackPressed() {
        this.finish();
        startActivity(new Intent(CheckoutActivity.this, MainActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            startActivity(new Intent(CheckoutActivity.this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
