package com.test.sample.hirecooks.Activity.Users;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Models.users.Result;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.security.MessageDigest;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSignInActivity extends BaseActivity implements View.OnClickListener {
    private EditText editTextEmail,editTextPassword;
    private Button buttonSignIn;
    private TextView txtSignUp,txtForgotPasswrd;
    private View appRoot;
    private ProgressBarUtil progressBarUtil;
    private UserApi mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        progressBarUtil = new ProgressBarUtil(this);
        appRoot = findViewById(R.id.appRoot);
        txtSignUp = findViewById(R.id.txtSignUp);
        txtForgotPasswrd = findViewById(R.id.txtForgotPasswrd);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        buttonSignIn.setOnClickListener(this);

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserSignInActivity.this, PhoneVerification.class));
                finish();
            }
        });
        txtForgotPasswrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserSignInActivity.this,ForgotPasswordActivity.class));
                finish();
            }
        });

        // Provides Key Hash
        printKeyHash();
    }

    @Override
    public void onClick(View view) {
        if (view == buttonSignIn) {
            SignInValidation();
        }
    }

    private void SignInValidation() {
        String email = Objects.requireNonNull(editTextEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(editTextPassword.getText()).toString().trim();

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Please enter email");
            editTextEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Please enter password");
            editTextPassword.requestFocus();
            return;
        }
        ResultSignIn(email,password);
    }


    private void ResultSignIn(String email, String password) {
        progressBarUtil.showProgress();
        mService = ApiClient.getClient().create(UserApi.class);
        Call<Result> call = mService.userLogin(email, password);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                int statusCode = response.code();
                if(statusCode==200&&response.body().getError()==false){
                    progressBarUtil.hideProgress();
                    assert response.body() != null;
                    if (!response.body().getError()) {
                        Constants.CurrentUser = response.body();
                        if(Constants.CurrentUser.getUser().getUserType().equalsIgnoreCase("User")||
                                Constants.CurrentUser.getUser().getUserType().equalsIgnoreCase("Manager")||
                                Constants.CurrentUser.getUser().getUserType().equalsIgnoreCase("SuperAdmin")||
                                Constants.CurrentUser.getUser().getUserType().equalsIgnoreCase("Cook")){
                            ShowToast("Login Successfull");
                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(Constants.CurrentUser.getUser());
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else{
                            ShowToast("You Can`t Access");
                        }
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
    // Provides Key Hash
    private void printKeyHash() {
        try{
            PackageInfo info=getPackageManager().getPackageInfo("com.test.sample.hirecooks", PackageManager.GET_SIGNATURES);
            for(Signature signature:info.signatures){
                MessageDigest md=MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("KEYHASH", Base64.encodeToString(md.digest(),Base64.DEFAULT));
                System.out.println("Suree: "+Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }

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