package com.test.sample.hirecooks.Activity.Menus;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.test.sample.hirecooks.Adapter.Menus.MenuAdapter;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.BannerResponse.Banner;
import com.test.sample.hirecooks.Models.BannerResponse.Banners;
import com.test.sample.hirecooks.Models.MenuResponse.Menus;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity {
    private RecyclerView menusRecyclerView;
    private MenuAdapter adapter;
    private UserApi mService;
    private SliderLayout sliderLayout;
    private ConstraintLayout appRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle("Menu");
        menusRecyclerView = findViewById(R.id.menus_list_recycler_view);
        sliderLayout = findViewById(R.id.slider);
        appRoot = findViewById(R.id.appRoot);
        getBannerImage();
        getMenus();
    }

    private void getMenus() {
        mService = ApiClient.getClient().create(UserApi.class);
        Call<Menus> call = mService.getMenu();
        call.enqueue(new Callback<Menus>() {
            @SuppressLint("ShowToast")
            @Override
            public void onResponse(Call<Menus> call, Response<Menus> response) {
                int statusCode = response.code();
                if(statusCode==200) {
                    if (response.body() != null) {
                        adapter = new MenuAdapter(MenuActivity.this, response.body().getMenus());
                        menusRecyclerView.setAdapter(adapter);
                        Toast.makeText(getApplicationContext(), "Suree: " + response.code(), Toast.LENGTH_LONG);
                    } else {
                        Toast.makeText(getApplicationContext(), "Suree: " + response.code() + response.errorBody() + response.message(), Toast.LENGTH_LONG);
                    }
                }
            }
            @SuppressLint("ShowToast")
            @Override
            public void onFailure(Call<Menus> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Suree: "+t.getMessage(),Toast.LENGTH_LONG);
            }
        });
    }

    private void getBannerImage() {
        mService = ApiClient.getClient().create(UserApi.class);
        Call<Banners> call = mService.getBanners();
        call.enqueue(new Callback<Banners>() {
            @SuppressLint("ShowToast")
            @Override
            public void onResponse(Call<Banners> call, Response<Banners> response) {
                int statusCode = response.code();
                if(statusCode==200) {
                    if (response.body() != null) {
                        displayImage(response.body().getBanners());
                        Toast.makeText(getApplicationContext(), "Suree: " + response.code(), Toast.LENGTH_LONG);
                    } else {
                        Toast.makeText(getApplicationContext(), "Suree: " + response.code() + response.errorBody() + response.message(), Toast.LENGTH_LONG);
                    }
                }
            }
            @SuppressLint("ShowToast")
            @Override
            public void onFailure(Call<Banners> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Suree: "+t.getMessage(),Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void displayImage(List<Banner> banners) {
        HashMap<String,String> bannerMap=new HashMap<>();
        for(Banner item:banners)
            bannerMap.put(item.getName(),item.getLink());

        for(String name:bannerMap.keySet()){
            TextSliderView textSliderView=new TextSliderView(this);
            textSliderView.description(name)
                    .image(bannerMap.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);

            sliderLayout.addSlider(textSliderView);
        }

    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
