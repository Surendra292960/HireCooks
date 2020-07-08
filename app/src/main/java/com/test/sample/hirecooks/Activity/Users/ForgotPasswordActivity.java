package com.test.sample.hirecooks.Activity.Users;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.BaseActivity;
import com.test.sample.hirecooks.Models.users.Result;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener{
    private EditText editTextEmail,editTextPassword,editTextConPassword;
    private TextView button_submit_password;
    private View appRoot;
    private ProgressBarUtil progressBarUtil;
    private UserApi mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initViews();
    }

    private void initViews() {
        progressBarUtil = new ProgressBarUtil(this);
        appRoot = findViewById(R.id.appRoot);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConPassword = findViewById(R.id.editTextConPassword);
        button_submit_password = findViewById(R.id.button_submit_password);
        button_submit_password.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view == button_submit_password) {
            ForgotPasswordValidation();
        }
    }

    private void ForgotPasswordValidation() {
        String email = Objects.requireNonNull(editTextEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(editTextPassword.getText()).toString().trim();
        String cpassword = Objects.requireNonNull(editTextConPassword.getText()).toString().trim();
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Please enter email");
            editTextEmail.requestFocus();
            return;
        }if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email");
            editTextEmail.requestFocus();
            return;
        }if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Please enter password");
            editTextPassword.requestFocus();
            return;
        }if (TextUtils.isEmpty(cpassword)) {
            editTextConPassword.setError("Please enter confirm password");
            editTextConPassword.requestFocus();
            return;
        }
        if (password.equals(cpassword)) {

        }else{
            editTextConPassword.setError("Password are not matching");
            editTextConPassword.requestFocus();
            return;
        }
        forgotPassword(email,password);
    }

    private void forgotPassword(String email, String password) {
        progressBarUtil.showProgress();
        mService = ApiClient.getClient().create(UserApi.class);
        Call<Result> call = mService.forgotPassword(email, password);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                int statusCode = response.code();
                if(statusCode==200&&response.body().getError()==false){
                    progressBarUtil.hideProgress();
                    assert response.body() != null;
                    if (!response.body().getError()) {
                        ShowToast(response.body().getMessage());
                        finish();
                        startActivity(new Intent(getApplicationContext(), UserSignInActivity.class));
                    } else {
                        ShowToast(response.body().getMessage());
                    }
                }else{
                    ShowToast(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressBarUtil.hideProgress();
                System.out.println("Suree :"+ t.getMessage());
                ShowToast("Please Check Intenet Connection");
            }
        });
    }
}
