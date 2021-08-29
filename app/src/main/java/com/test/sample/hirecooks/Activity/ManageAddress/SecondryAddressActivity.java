package com.test.sample.hirecooks.Activity.ManageAddress;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.test.sample.hirecooks.Adapter.Users.AddressAdapter;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.MapLocationResponse.Maps;
import com.test.sample.hirecooks.Models.NewOrder.OrdersTable;
import com.test.sample.hirecooks.Models.UsersResponse.UserResponse;
import com.test.sample.hirecooks.Models.users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.Common;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.MapApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SecondryAddressActivity extends BaseActivity {
    private TextView buttonAddAddress;
    private RecyclerView recyclerView;
    private UserResponse userResponse;
    private ProgressBarUtil progressBarUtil;
    private MapApi mService = Common.getAPI();;
    private User user;
    private OrdersTable ordersTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondry_address);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("My Address");
        initViews();
        ApiServiceCall();
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            userResponse = (UserResponse)bundle.getSerializable("User");
            ordersTable = (OrdersTable)bundle.getSerializable("OrdersTable");
        }
    }

    private void initViews() {
        progressBarUtil = new ProgressBarUtil( this );
        user = SharedPrefManager.getInstance( this ).getUser();
        recyclerView = findViewById(R.id.recyclerview_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        buttonAddAddress = findViewById(R.id.floating_button_add);
        buttonAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(SecondryAddressActivity.this, SearchAddress.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("User", userResponse);
                    intent.putExtras(bundle);
                    startActivity(intent);
            }
        });
    }

    private void ApiServiceCall() {
        progressBarUtil.showProgress();
        mService.getAllAddress()
                .subscribeOn( Schedulers.io())
                .observeOn( AndroidSchedulers.mainThread())
                .subscribe(new Observer<Maps>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Maps result) {
                        callAdapter(result);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ShowToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        progressBarUtil.hideProgress();
                    }
                });

    }

    private void callAdapter(Maps result) {
        List<Map> mapList = new ArrayList<>(  );
        for(Map map:result.getMaps()){
            if(Objects.equals( map.getUserId(), user.getId() )) {
                mapList.add( map );
            }
        }
        AddressAdapter adapter = new AddressAdapter(SecondryAddressActivity.this, mapList,ordersTable);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApiServiceCall();
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
