package com.test.sample.hirecooks.Activity.ManageAddress;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.test.sample.hirecooks.Adapter.Users.AddressAdapter;
import com.test.sample.hirecooks.Models.UsersResponse.UserResponse;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.RoomDatabase.SecondryAddress_DB.Address;
import com.test.sample.hirecooks.RoomDatabase.SecondryAddress_DB.AddressDataBaseClient;

import java.util.List;
import java.util.Objects;

public class SecondryAddressActivity extends AppCompatActivity {
    private Button buttonAddAddress;
    private RecyclerView recyclerView;
    private UserResponse userResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondry_address);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("My Address");
        initViews();
        getAddress();
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            userResponse = (UserResponse)bundle.getSerializable("User");
        }
    }

    private void initViews() {
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


    private void getAddress() {
        class GetAddress extends AsyncTask<Void, Void, List<Address>> {

            @Override
            protected List<com.test.sample.hirecooks.RoomDatabase.SecondryAddress_DB.Address> doInBackground(Void... voids) {
                List<com.test.sample.hirecooks.RoomDatabase.SecondryAddress_DB.Address> addresskList = AddressDataBaseClient
                        .getInstance(getApplicationContext())
                        .getAddressDatabase()
                        .addressDao()
                        .getAll();
                return addresskList;
            }

            @Override
            protected void onPostExecute(List<com.test.sample.hirecooks.RoomDatabase.SecondryAddress_DB.Address> address) {
                super.onPostExecute(address);
                AddressAdapter adapter = new AddressAdapter(SecondryAddressActivity.this, address);
                recyclerView.setAdapter(adapter);
            }
        }

        GetAddress gt = new GetAddress();
        gt.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAddress();
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
