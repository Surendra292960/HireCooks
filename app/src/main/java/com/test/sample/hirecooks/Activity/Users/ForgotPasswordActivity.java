package com.test.sample.hirecooks.Activity.Users;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.Users.UserResponse;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener{
    private EditText editTextEmail,editTextPassword,editTextConPassword;
    private Button button_submit_password;
    private View appRoot;
    private ProgressBarUtil progressBarUtil;
    private UserApi mService;
    private FirebaseAuth mAuth;
    private FirebaseUser user ;
    String newPassword = "SOME-SECURE-PASSWORD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_forgot_password);
        initViews();
    }

    private void initViews() {
        mAuth = FirebaseAuth.getInstance();
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
        Call<List<UserResponse>> call = mService.forgotPassword(email, password);
        call.enqueue(new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                if(response.code()==200) {
                    progressBarUtil.hideProgress();
                    for(UserResponse example:response.body()){
                        if(!example.getError()){
                            //updateFirebasePassword( password );
                            ShowToast( example.getMessage());
                            startActivity( new Intent( getApplicationContext(), UserSignInActivity.class ) .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK) );
                            finish();
                        }else{
                            ShowToast( example.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                progressBarUtil.hideProgress();
                System.out.println("Suree :"+ t.getMessage());
                ShowToast("Please Check Intenet Connection");
            }
        });
    }

    private void updateFirebasePassword(String password) {
        user = mAuth.getCurrentUser();
        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User password updated.");
                        }
                    }
                });
    }

}
