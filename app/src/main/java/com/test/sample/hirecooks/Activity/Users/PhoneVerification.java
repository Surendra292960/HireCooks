package com.test.sample.hirecooks.Activity.Users;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Libraries.PinView.PinView;
import com.test.sample.hirecooks.Models.Users.Example;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneVerification extends AppCompatActivity {
    private String mVerificationId;
    private TextView editTextPhone,buttonResendOtp,user_phone_number;
    private PinView pinView;
    private FirebaseAuth mAuth;
    private LinearLayout enter_otp_layout,verify_phone_layout;
    private ProgressBarUtil progressBarUtil;
    private boolean mVerificationInProgress;
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
    private CountryCodePicker ccp;
    private EditText editTextCarrierNumber;
    private TextView timerTV;
    private boolean firstSend = true, timerOn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        progressBarUtil = new ProgressBarUtil(this);
        mAuth = FirebaseAuth.getInstance();
        pinView = findViewById(R.id.pinView);
        timerTV = findViewById(R.id.timerTV);
        user_phone_number = findViewById(R.id.user_phone_number);
        editTextPhone = findViewById(R.id.editTextPhone);
        buttonResendOtp = findViewById(R.id.buttonResendOtp);
        verify_phone_layout = findViewById(R.id.verify_phone_layout);
        verify_phone_layout.setVisibility(View.VISIBLE);
        enter_otp_layout = findViewById(R.id.enter_otp_layout);
        enter_otp_layout.setVisibility(View.GONE);
        ccp = findViewById(R.id.ccp);
        editTextCarrierNumber = findViewById(R.id.editTextPhone);
        ccp.registerCarrierNumberEditText(editTextCarrierNumber);

        findViewById(R.id.verify_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startResendTimer(30);
                firstSend = false;
                String phone = editTextPhone.getText().toString().trim();
                if(phone.isEmpty() || phone.length() < 10){
                    editTextPhone.setError("Enter a valid mobile");
                    editTextPhone.requestFocus();
                    return;
                }
                phone = phone.replace(" " , "");
                Constants.CurrentUserPhoneNumber = phone;
                phone = ccp.getFullNumberWithPlus();
                user_phone_number.setText(phone);
                 getUserByPhone(phone);
                //sendVerificationCode(phone);
            }
        });

        findViewById(R.id.buttonResendOtp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startResendTimer(30);
                firstSend = false;
                String phone = Constants.CurrentUserPhoneNumber;
                phone = phone.replace(" " , "");
                phone = ccp.getFullNumberWithPlus();
                user_phone_number.setText(phone);
                 getUserByPhone(phone);
                //sendVerificationCode(phone);
            }
        });

        findViewById(R.id.buttonSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = Objects.requireNonNull(pinView.getText()).toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    pinView.setError("Enter valid code");
                    pinView.requestFocus();
                    return;
                }
                verifyVerificationCode(code);
            }
        });
    }

    private void sendVerificationCode(String mobile) {
        verify_phone_layout.setVisibility(View.GONE);
        enter_otp_layout.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber( mobile, 60, TimeUnit.SECONDS, (Activity) TaskExecutors.MAIN_THREAD, mCallbacks);
        mVerificationInProgress = true;
    }

    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            mVerificationInProgress = false;
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                timerTV.setVisibility(View.VISIBLE);
                pinView.setText(code);
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            mVerificationInProgress = false;
            Toast.makeText(PhoneVerification.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
        }
    };


    private void verifyVerificationCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(PhoneVerification.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            Intent intent = new Intent(PhoneVerification.this, UserSignUpActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {
                            String message = "Somthing is wrong, we will fix it soon...";
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                        }
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    public void startResendTimer(int seconds) {
        timerTV.setVisibility(View.VISIBLE);
        buttonResendOtp.setEnabled(false);

        new CountDownTimer(seconds*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                String secondsString = Long.toString(millisUntilFinished/1000);
                if (millisUntilFinished<10000) {
                    secondsString = "0"+secondsString;
                }
                timerTV.setText(" (0:"+ secondsString+")");
            }

            public void onFinish() {
                buttonResendOtp.setEnabled(true);
                timerTV.setVisibility(View.GONE);
                timerOn=false;
            }
        }.start();
    }

    private void getUserByPhone(final String phone) {
        System.out.println( "Suree : "+phone );
        progressBarUtil.showProgress();
        UserApi service = ApiClient.getClient().create(UserApi.class);
        Call<List<Example>> call = service.checkUserPhone(phone);
        call.enqueue(new Callback<List<Example>>() {
            @Override
            public void onResponse(@NonNull Call<List<Example>> call, @NonNull Response<List<Example>> response) {
                if(response.code()==200) {
                    progressBarUtil.hideProgress();
                   for(Example example:response.body()){
                       if(!example.getError()){
                           sendVerificationCode(phone);
                       }else{
                           Toast.makeText( PhoneVerification.this, example.getMessage(), Toast.LENGTH_SHORT ).show();
                       }
                   }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Example>> call, @NonNull Throwable t) {
                progressBarUtil.hideProgress();
                Toast.makeText(getApplicationContext(), R.string.error+t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}