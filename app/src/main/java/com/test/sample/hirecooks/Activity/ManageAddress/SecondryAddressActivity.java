package com.test.sample.hirecooks.Activity.ManageAddress;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.test.sample.hirecooks.Adapter.Users.UserAddressAdapter;
import com.test.sample.hirecooks.Models.NewOrder.OrdersTable;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.ViewModel.ViewModel;
import com.test.sample.hirecooks.databinding.ActivitySecondryAddressBinding;

public class SecondryAddressActivity extends BaseActivity {
    private User userResponse;
    private User user;
    private OrdersTable ordersTable;
    private ActivitySecondryAddressBinding binding;
    private ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySecondryAddressBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        binding.mToolbarInterface.mToolbar.setVisibility(View.VISIBLE);
        binding.mToolbarInterface.goBack.setOnClickListener(view1 -> finish());
        user = SharedPrefManager.getInstance(this).getUser();
        initViews();
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            userResponse = (User)bundle.getSerializable("User");
            ordersTable = (OrdersTable)bundle.getSerializable("OrdersTable");
        }
    }

    @SuppressLint("NewApi")
    private void initViews() {
        binding.floatingButtonAdd.setOnClickListener(v->{
            Intent intent = new Intent(SecondryAddressActivity.this, SearchAddress.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("User", userResponse);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        viewModel.getAddressByUserId(user.getId()).observe(this, maps -> maps.forEach(map->{
            if(!map.getError()&&map.getMaps()!=null&&map.getMaps().size()!=0){
                binding.recyclerview.setVisibility(View.VISIBLE);
                UserAddressAdapter adapter = new UserAddressAdapter(SecondryAddressActivity.this, map.getMaps(),ordersTable);
                binding.recyclerview.setAdapter(adapter);
            }
        }));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
