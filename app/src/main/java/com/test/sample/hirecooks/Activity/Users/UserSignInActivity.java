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

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.Users.Example;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

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
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mAuth = FirebaseAuth.getInstance();
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
                startActivity(new Intent(UserSignInActivity.this, PhoneVerification.class) .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        });
        txtForgotPasswrd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserSignInActivity.this,ForgotPasswordActivity.class) .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        });

        // Provides Key Hash
        printKeyHash();
    }

    @Override
    public void onClick(View view) {
        if (view == buttonSignIn) {
          validation();
        }
    }

    private void validation() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        if(TextUtils.isEmpty(email)){
            editTextEmail.setError( "Please Enter email" );
            editTextEmail.requestFocus();
            return;
        } if(TextUtils.isEmpty(password )){
            editTextPassword.setError( "Please Enter email" );
            editTextPassword.requestFocus();
            return;
        } if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email");
            editTextEmail.requestFocus();
            return;
        }
        List<Example> exampleList = new ArrayList<>();
        Example exampls = new Example();
        List<User> userList = new ArrayList<>();
        User user = new User();
        user.setEmail( editTextEmail.getText().toString() );
        user.setPassword( editTextPassword.getText().toString() );
        userList.add( user );
        exampls.setUsers( userList );
        exampleList.add( exampls );
        signIn(exampleList);
    }

    private void signIn(List<Example> exampleList) {
        progressBarUtil.showProgress();
        mService = ApiClient.getClient().create(UserApi.class);
        Call<List<Example>> call = mService.userLogin(exampleList);
        call.enqueue(new Callback<List<Example>>() {
            @Override
            public void onResponse(Call<List<Example>> call, Response<List<Example>> response) {
                int statusCode = response.code();
                if(statusCode==200){
                    progressBarUtil.hideProgress();
                    for(Example example:response.body()){
                        ShowToast( example.getMessage() );
                        if(!example.getError()) {
                           for(User user:example.getUsers()){
                               updateUserStatus(user.getId(),1,user.getEmail());
                               SharedPrefManager.getInstance(getApplicationContext()).userLogin( user);
                               startActivity(new Intent(getApplicationContext(), MainActivity.class) .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                               finish();
                           }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Example>> call, Throwable t) {
                progressBarUtil.hideProgress();
                System.out.println("Suree :"+ t.getMessage());
                ShowToast("Please Check Intenet Connection");
            }
        });
    }

    private void siginWithFirebase(String email ,String password){
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(UserSignInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            Log.d("Suree", "Signin with firebase");

                        } else {
                            String message = "Somthing is wrong, we will fix it soon...";
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                        }
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