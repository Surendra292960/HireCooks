package com.test.sample.hirecooks.Activity.Users.FirmUsers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.lifecycle.ViewModelProvider;

import com.test.sample.hirecooks.Activity.Orders.PlaceOrderActivity;
import com.test.sample.hirecooks.Activity.Search.SearchResultActivity;
import com.test.sample.hirecooks.Adapter.Users.UserAdapter;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.NetworkUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.ViewModel.ViewModel;
import com.test.sample.hirecooks.databinding.ActivitySubCategoryBinding;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {
    private UserAdapter adapter;
    private List<User> filtereduserList;
    private User user;
    private ViewModel viewModel;
    private ActivitySubCategoryBinding binding;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubCategoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        user = SharedPrefManager.getInstance(this).getUser();
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        binding.mToolbarInterface.mToolbar.setVisibility(View.VISIBLE);
        binding.mToolbarInterface.search.setOnClickListener(v -> startActivity( new Intent( this, SearchResultActivity.class )));
        binding.add.setOnClickListener(v -> startActivity( new Intent( this, FirmUserSignupActivity.class )));
        binding.mToolbarInterface.goBack.setOnClickListener(view1 -> finish());
        checkInternet();
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            type = bundle.getString("Type");
            if(type.equalsIgnoreCase("ManageEmployee")&&user.getUserType().equalsIgnoreCase("Manager")){
                binding.add.setVisibility(View.VISIBLE);
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
                getUsers( user.getFirmId() );
            }
        }
        else {
            binding.subcategoryLayout.setVisibility( View.GONE );
            binding.noInternetConnectionLayout.setVisibility( View.VISIBLE );
        }
    }
    @SuppressLint("NewApi")
    private void getUsers(String firmId) {
        viewModel.getUserByFirmId(firmId).observe(this, userResponses -> userResponses.forEach(userResponse -> {
        if (!userResponse.getError()) {
            binding.recyclerview.setVisibility(View.VISIBLE);
            filtereduserList = new ArrayList<>();
            for(User user : userResponse.getUsers()){
                if(user.getUserType().equalsIgnoreCase( "Employee" )||user.getUserType().equalsIgnoreCase( "Rider" )) {
                    filtereduserList.add(user);
                }
            }
            adapter = new UserAdapter(this,filtereduserList,type);
            binding.recyclerview.setAdapter(adapter);
        } else {
            binding.recyclerview.setVisibility(View.GONE);
            binding.noResultFound.setVisibility(View.VISIBLE);
        }
    }));
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
        List<User>  filteredList = new ArrayList<>();
        try{
            for(int i=0;i<filtereduserList.size();i++) {
                String userName = "";

                if(filtereduserList.get (i).getName()!=null){
                    userName= filtereduserList.get (i).getName();
                }

                if(userName.toLowerCase().contains(s.toLowerCase())) {
                    filteredList.add(filtereduserList.get(i));
                }
            }

            adapter = new UserAdapter( UserActivity.this, filteredList,type);
            binding.recyclerview.setAdapter (adapter);
            adapter.notifyDataSetChanged();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(user!=null&&user.getFirmId()!=null){
            getUsers( user.getFirmId() );
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
