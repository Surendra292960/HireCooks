package com.test.sample.hirecooks.Activity.Cooks;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.test.sample.hirecooks.Adapter.Cooks.CooksAdapter;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.UsersResponse.UserResponse;
import com.test.sample.hirecooks.Models.UsersResponse.UsersResponse;
import com.test.sample.hirecooks.Models.cooks.Cooks;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CooksActivity extends AppCompatActivity {
    private ProgressBarUtil progressBarUtil;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView profile_list_recycler_view;
    private Cooks cooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Cooks");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            cooks = (Cooks) bundle.getSerializable("Cooks");
            if (cooks != null) {
            }
        }
        initViews();
    }

    private void initViews() {
            progressBarUtil = new ProgressBarUtil(this);
            mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
            profile_list_recycler_view = findViewById(R.id.profile_list_recycler_view);
            mSwipeRefreshLayout.setColorScheme(R.color.green_light, R.color.red, R.color.style_color_primary);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(false);
                            getUsers();
                        }
                    }, 3000);
                }
            });

            getUsers();
    }

    private void getUsers() {
        progressBarUtil.showProgress();
        UserApi service = ApiClient.getClient().create(UserApi.class);
        Call<UsersResponse> call = service.getUsers();
        call.enqueue(new Callback<UsersResponse>() {
            @SuppressLint("ShowToast")
            @Override
            public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    List<UserResponse> filterCook = new ArrayList<>();
                    progressBarUtil.hideProgress();
                    mSwipeRefreshLayout.onFinishTemporaryDetach();
                    if (response.body() != null) {
                        for (UserResponse userResponse : response.body().getUsersResponses()) {
                            if (userResponse.getUserType().equalsIgnoreCase("Cook")) {
                                for(Map map: Constants.NEARBY_COOKS) {
                                    if(userResponse.getId().equals(map.getUserId())){
                                        filterCook.add(userResponse);
                                    }
                                }
                            }
                        }
                        CooksAdapter adapter = new CooksAdapter(CooksActivity.this, filterCook);
                        profile_list_recycler_view.setAdapter(adapter);
                    }
                }
            }

            @SuppressLint("ShowToast")
            @Override
            public void onFailure(@NonNull Call<UsersResponse> call, @NonNull Throwable t) {
                progressBarUtil.hideProgress();
                Toast.makeText(getApplicationContext(), R.string.error + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.getItem(0);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setFocusable(true);
        searchItem.expandActionView();
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
        }
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
