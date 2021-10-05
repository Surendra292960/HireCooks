package com.test.sample.hirecooks.Activity.ProductDatails;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.test.sample.hirecooks.Models.SubCategory.Image;
import com.test.sample.hirecooks.R;

import java.util.Objects;

public class FullImageActivity extends AppCompatActivity {
    private ImageView selected_Image, full_Image;
    private Image image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        initViews();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            image = (Image)bundle.getSerializable("Image");
            Glide.with(this).load(image.getImage()).into(full_Image);
            Glide.with(this).load(image.getImage()).into(selected_Image);
        }

    }

    private void initViews() {
        full_Image = findViewById(R.id.full_image);
        selected_Image = findViewById(R.id.selected_image);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}