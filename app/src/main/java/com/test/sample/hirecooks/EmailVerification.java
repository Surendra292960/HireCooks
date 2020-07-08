/*
package com.test.sample.hirecooks;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.Models.users.Results;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.WebApis.UserApi;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailVerification extends AppCompatActivity implements View.OnClickListener {
    private TextInputEditText editTextEmail, editTextPassword;
    private AppCompatButton refreshBtn, verifyBtn,buttonSignIn;
    private TextView status, verifyEmail, userId;
    private ProgressBarUtil progressBarUtil;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_and_phone_varification);
        progressBarUtil = new ProgressBarUtil(this);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        verifyEmail = findViewById(R.id.verifyEmail);
        userId = findViewById(R.id.userId);
        status = findViewById(R.id.status);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        buttonSignIn.setOnClickListener(this);
        buttonSignIn.setVisibility(View.GONE);
        refreshBtn = findViewById(R.id.refreshBtn);
        verifyBtn = findViewById(R.id.verifyBtn);
        verifyBtn.setOnClickListener(this);

        editTextEmail.setText(firebaseUser.getEmail());

        setInfo();

    }


    @Override
    public void onClick(View view) {
        if (view == verifyBtn) {
            SignInValidation();
        }else if(view== buttonSignIn){
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

        if(firebaseUser!=null&&firebaseUser.isEmailVerified()){
            ResultSignIn(email,password);
        }else{
            emailPasswordValidation(email,password);
        }
    }

    private void emailPasswordValidation(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Objects.requireNonNull(mAuth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Please Check your Email Address", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        setInfo();
                    }
                });
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setInfo() {

        if(firebaseUser!=null&& firebaseUser.isEmailVerified()){
            verifyEmail.setText(new StringBuilder("EMAIL: ").append(firebaseUser.getEmail()));
            userId.setText(new StringBuilder("UID: ").append(firebaseUser.getUid()));
            status.setText(new StringBuilder("Status: ").append(firebaseUser.isEmailVerified()));
            buttonSignIn.setVisibility(View.VISIBLE);
            startActivity(new Intent(this, PhoneVerification.class));
        }else{
            Toast.makeText(getApplicationContext(), "Please try after some time", Toast.LENGTH_SHORT).show();
            verifyEmail.setText("EMAIL: "+"Non");
            userId.setText("UID: "+" Non");
            status.setText("Status: " +"false");
        }
    }

    private void ResultSignIn(String email, String password) {
        progressBarUtil.showProgress();
        UserApi service = ApiClient.getClient().create(UserApi.class);
        Call<Results> call = service.userLogin(email, password);
        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {
                int statusCode = response.code();
                if(statusCode==200){
                    progressBarUtil.hideProgress();
                    assert response.body() != null;
                    if (!response.body().getError()) {
                      //  SharedPrefManager.getInstance(getApplicationContext()).userLogin(response.body().getUser());
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid email or password", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Results> call, Throwable t) {
                progressBarUtil.hideProgress();
                Toast.makeText(getApplicationContext(), "Error ", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}*/
