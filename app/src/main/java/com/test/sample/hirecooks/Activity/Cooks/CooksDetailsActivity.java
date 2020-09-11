package com.test.sample.hirecooks.Activity.Cooks;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Adapter.Cooks.CooksImagesAdapter;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.MapLocationResponse.Result;
import com.test.sample.hirecooks.Models.UsersResponse.UserResponse;
import com.test.sample.hirecooks.Models.cooks.Request.CooksImages;
import com.test.sample.hirecooks.Models.cooks.Request.CooksImagesResult;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.APIUrl;
import com.test.sample.hirecooks.WebApis.CookImages;
import com.test.sample.hirecooks.WebApis.MapApi;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CooksDetailsActivity extends AppCompatActivity {
    private UserResponse users;
    private RecyclerView cooks_images_recycler;
    private TextView name,address,phone_number,profile_type,cook_name;
    private ImageView imageView, cook_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooks_details);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Cooks Details");
        initViews();
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            users = (UserResponse)bundle.getSerializable("Cooks");
          if(users!=null){
              getLocation(users);
              getCookImages(users);
          }
        }
    }

    @SuppressLint("WrongConstant")
    private void initViews() {
        cooks_images_recycler = findViewById(R.id.cooks_images_recycler);
        phone_number = findViewById(R.id.phone_number);
        address = findViewById(R.id.address);
        imageView = findViewById(R.id.imageView);
        cook_image = findViewById(R.id.cook_image);
        name = findViewById(R.id.name);
        cook_name = findViewById(R.id.cook_name);
        profile_type = findViewById(R.id.profile_type);
    }


    @SuppressLint("WrongConstant")
    private void setData(Map maps) {
       if(maps.getAddress()!=null){
           address.setText(maps.getAddress());
       }
       if(users.getImage()!=null){
           if(users.getImage().equalsIgnoreCase("https://")){
               Picasso.with(this).load(users.getImage()).into(cook_image);
           }else{
               Picasso.with(this).load(APIUrl.PROFILE_URL+users.getImage()).into(cook_image);
           }
       }
        phone_number.setText(users.getPhone());
        name.setText(users.getName());
        cook_name.setText(users.getName());
        profile_type.setText(users.getUserType());

    }

    private void getLocation(UserResponse userResponse) {
        MapApi service = ApiClient.getClient().create(MapApi.class);
        Call<Result> call = service.getMapDetails(userResponse.getId());
        call.enqueue(new Callback<Result>() {
            @SuppressLint("ShowToast")
            @Override
            public void onResponse(@NonNull Call<Result> call, @NonNull Response<Result> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    if(response.body().getMaps()!=null){
                        setData(response.body().getMaps());
                    }else{
                        Toast.makeText(CooksDetailsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @SuppressLint("ShowToast")
            @Override
            public void onFailure(@NonNull Call<Result> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.error + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getCookImages(UserResponse users) {
        CookImages service = ApiClient.getClient().create(CookImages.class);
        Call<CooksImagesResult> call = service.getCookImagesByUserId(users.getId());
        call.enqueue(new Callback<CooksImagesResult>() {
            @SuppressLint("ShowToast")
            @Override
            public void onResponse(@NonNull Call<CooksImagesResult> call, @NonNull Response<CooksImagesResult> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    if(response.body().getImages()!=null){
                        getCategoryCooks(response.body().getImages());
                    }else{
                        Toast.makeText(CooksDetailsActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @SuppressLint("ShowToast")
            @Override
            public void onFailure(@NonNull Call<CooksImagesResult> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.error + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint("WrongConstant")
    private void getCategoryCooks(List<CooksImages> cooksImages) {
        CooksImagesAdapter mAdapter = new CooksImagesAdapter( this,cooksImages);
        cooks_images_recycler.setAdapter ( mAdapter );

        LinearLayoutManager tlinearLayoutManager = new LinearLayoutManager(this);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            tlinearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            tlinearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
        }
        cooks_images_recycler.setLayoutManager(tlinearLayoutManager);
        cooks_images_recycler.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
