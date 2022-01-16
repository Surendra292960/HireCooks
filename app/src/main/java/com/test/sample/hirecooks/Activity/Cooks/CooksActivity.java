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
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.test.sample.hirecooks.Activity.Orders.PlaceOrderActivity;
import com.test.sample.hirecooks.Activity.Search.SearchResultActivity;
import com.test.sample.hirecooks.Activity.Users.FirmUsers.FirmUserSignupActivity;
import com.test.sample.hirecooks.Adapter.Cooks.CooksAdapter;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.Users.UserResponse;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.Models.cooks.Cooks;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.NetworkUtil;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.ViewModel.ViewModel;
import com.test.sample.hirecooks.WebApis.UserApi;
import com.test.sample.hirecooks.databinding.ActivitySubCategoryBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CooksActivity extends BaseActivity {
    private ProgressBarUtil progressBarUtil;
    private Cooks cooks;
    private String type;
    private User user;
    private ViewModel viewModel;
    private ActivitySubCategoryBinding binding;
    private List<User> allCook,filterCook,searchList,cook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubCategoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        progressBarUtil = new ProgressBarUtil(this);
        user = SharedPrefManager.getInstance(this).getUser();
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        binding.mToolbarInterface.mToolbar.setVisibility(View.VISIBLE);
        binding.mToolbarInterface.search.setOnClickListener(v -> startActivity( new Intent( this, SearchResultActivity.class )));
        binding.add.setOnClickListener(v -> startActivity( new Intent( this, FirmUserSignupActivity.class )));
        binding.mToolbarInterface.goBack.setOnClickListener(view1 -> finish());
        checkInternet();
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            cooks = (Cooks) bundle.getSerializable("Cooks");
            type= bundle.getString("type");
            if (cooks != null) {
            }
            if(type!=null){
                // Objects.requireNonNull(getSupportActionBar()).setTitle(type);
            }
        }
        binding.swipeToRefresh.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            binding.swipeToRefresh.setRefreshing(false);
            checkInternet();
        }, 3000));
    }

    private void checkInternet(){
        if(NetworkUtil.checkInternetConnection(this)) {
            binding.subcategoryLayout.setVisibility( View.VISIBLE );
            binding.noInternetConnectionLayout.setVisibility(View.GONE );
            if(user!=null&&user.getFirmId()!=null){
                getUsers();
            }
        }
        else {
            binding.subcategoryLayout.setVisibility( View.GONE );
            binding.noInternetConnectionLayout.setVisibility( View.VISIBLE );
        }
    }

    private void getUsers() {
        progressBarUtil.showProgress();
        UserApi service = ApiClient.getClient().create(UserApi.class);
        Call<List<UserResponse>> call = service.getUsers();
        call.enqueue(new Callback<List<UserResponse>>() {
            @SuppressLint("ShowToast")
            @Override
            public void onResponse(@NonNull Call<List<UserResponse>> call, @NonNull Response<List<UserResponse>> response) {
                if(response.code()==200){
                    progressBarUtil.hideProgress();
                    binding.recyclerview.setVisibility(View.VISIBLE);
                    for(UserResponse example:response.body()){
                        if(!example.getError()){
                            setCookData( example.getUsers());
                        }
                    }
                }
            }

            @SuppressLint("ShowToast")
            @Override
            public void onFailure(@NonNull Call<List<UserResponse>> call, @NonNull Throwable t) {
                progressBarUtil.hideProgress();
                Toast.makeText(getApplicationContext(), R.string.error + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setCookData(List<User> userList) {
        cook = new ArrayList<>();
        filterCook = new ArrayList<>();
        allCook = new ArrayList<>();
        searchList = new ArrayList<>();
        binding.swipeToRefresh.onFinishTemporaryDetach();
        if (userList!= null) {
            for (User userResponse : userList) {
                if (userResponse.getUserType().equalsIgnoreCase("Cook")) {
                    if (type != null && type.equalsIgnoreCase( "AllCooks" )) {
                        allCook.add( userResponse );
                        searchList.add( userResponse );
                    } else {
                        for (Map map : Constants.NEARBY_COOKS) {
                            if (userResponse.getId()==map.getUserId()) {
                                filterCook.add( userResponse );
                                searchList.add( userResponse );
                            }
                        }
                    }
                }
            }
            setAdapter(type,allCook,filterCook,cook);
        }
    }

    private void setAdapter(String type, List<User> allCook, List<User> filterCook, List<User> cook) {
        if(type!=null&&allCook!=null&&allCook.size()!=0){
            CooksAdapter adapter = new CooksAdapter(CooksActivity.this, allCook);
            binding.recyclerview.setAdapter(adapter);
        }else if(type==null&&filterCook!=null&&filterCook.size()!=0){
            CooksAdapter adapter = new CooksAdapter(CooksActivity.this, filterCook);
            binding.recyclerview.setAdapter(adapter);
        }else if(type==null&&cook!=null&&cook.size()!=0){
            CooksAdapter adapter = new CooksAdapter(CooksActivity.this, filterCook);
            binding.recyclerview.setAdapter(adapter);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.getItem(0);
        SearchManager searchManager = (SearchManager) getSystemService( Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setFocusable(true);
        searchItem.expandActionView();

        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                startSearch( query );
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                startSearch( newText );
                return false;
            }
        } );
        return super.onCreateOptionsMenu(menu);
    }

    private void startSearch(CharSequence text) {
        List<User> filterList = new ArrayList<>();
        try {
            if (searchList != null && searchList.size() != 0) {
                for (int i = 0; i < searchList.size(); i++) {
                    String cookName = "";

                    if (searchList.get(i).getName() != null) {
                        cookName = searchList.get(i).getName();
                    }

                    if (cookName.toLowerCase().contains(String.valueOf(text).toLowerCase())) {
                        filterList.add(searchList.get(i));
                    }
                }

                CooksAdapter adapter = new CooksAdapter(CooksActivity.this, filterList);
                binding.recyclerview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
