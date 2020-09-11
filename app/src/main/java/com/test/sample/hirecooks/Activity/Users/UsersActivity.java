package com.test.sample.hirecooks.Activity.Users;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.test.sample.hirecooks.Adapter.Users.UsersAdapter;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.UsersResponse.UserResponse;
import com.test.sample.hirecooks.Models.UsersResponse.UsersResponse;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.OnButtonClickListener;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersActivity extends AppCompatActivity {
    private RecyclerView usersRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private UsersAdapter adapter;
    private ProgressBarUtil progressBarUtil;
    private List<UserResponse> usersList;
    private View appRoot;
    private OnButtonClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN;

        getWindow().addFlags(flags);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Explode());
        }
        setContentView(R.layout.activity_users);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        progressBarUtil = new ProgressBarUtil(this);
        appRoot = findViewById(R.id.appRoot);
        Fresco.initialize(this);
        getUsers();
        usersRecyclerView = findViewById(R.id.users_list_recycler_view);
        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.style_color_accent);
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

        final LinearLayoutManager layoutParams = new LinearLayoutManager(this);
        usersRecyclerView.setLayoutManager(layoutParams);
        UsersAdapter adapter = new UsersAdapter(this,usersList,listener);
        usersRecyclerView.setAdapter(adapter);
        usersRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                mSwipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
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
                    progressBarUtil.hideProgress();
                    mSwipeRefreshLayout.onFinishTemporaryDetach();
                    if (response.body() != null) {
                        usersList = response.body().getUsersResponses();
                        adapter = new UsersAdapter(UsersActivity.this, response.body().getUsersResponses(),listener);
                        usersRecyclerView.setAdapter(adapter);
                        Toast.makeText(getApplicationContext(), "Suree: " + response.code(), Toast.LENGTH_LONG);
                    } else {
                        Toast.makeText(getApplicationContext(), "Suree: " + response.code() + response.errorBody() + response.message(), Toast.LENGTH_LONG).show();
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
            inflater.inflate(R.menu.searchbar, menu);
            MenuItem searchViewItem = menu.findItem(R.id.search);
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
            searchView.setQueryHint("Search User");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchUserByName(query);
                    searchView.clearFocus();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchUserByName(newText);
                    return false;
                }
            });
            return super.onCreateOptionsMenu(menu);
        }

        private void searchUserByName(String s) {
            List<UserResponse>  filteredList = new ArrayList<>();
            try{
                for(int i=0;i<usersList.size();i++) {
                    String cityName = "";

                    if(usersList.get (i).getName()!=null){
                        cityName= usersList.get (i).getName();
                    }

                    if(cityName.toLowerCase().contains(s.toLowerCase())) {
                        filteredList.add(usersList.get(i));
                    }
                }

                adapter = new UsersAdapter (UsersActivity.this, filteredList,listener);
                usersRecyclerView.setAdapter (adapter);
                adapter.notifyDataSetChanged();

            }catch (Exception e){
                e.printStackTrace();
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
