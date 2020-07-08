package com.test.sample.hirecooks.Activity.Users;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.test.sample.hirecooks.Adapter.MessageAdapter;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.users.Messages;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {
    private RecyclerView usersRecyclerView;
    private MessageAdapter adapter;
    private View appRoot;
    private ProgressBarUtil progressBarUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        progressBarUtil = new ProgressBarUtil(this);
        Fresco.initialize(this);
        usersRecyclerView = findViewById(R.id.message_list_recycler_view);
        appRoot = findViewById(R.id.appRoot);
        getUsers();
    }

    private void getUsers() {
        progressBarUtil.showProgress();
        UserApi service = ApiClient.getClient().create(UserApi.class);
        Call<Messages> call = service.getMessages(SharedPrefManager.getInstance(MessageActivity.this).getUser().getId());
        call.enqueue(new Callback<Messages>() {
            @SuppressLint("ShowToast")
            @Override
            public void onResponse(@NonNull Call<Messages> call,  @NonNull Response<Messages> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    progressBarUtil.hideProgress();
                    if (response.body() != null) {
                        adapter = new MessageAdapter(MessageActivity.this, response.body().getMessages());
                        usersRecyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(getApplicationContext(), " " + response.code() + response.errorBody() + response.message(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @SuppressLint("ShowToast")
            @Override
            public void onFailure( @NonNull Call<Messages> call,  @NonNull Throwable t) {
                progressBarUtil.hideProgress();
                Toast.makeText(getApplicationContext(), R.string.error + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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