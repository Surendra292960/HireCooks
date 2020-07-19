package com.test.sample.hirecooks.Activity.Users;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.hbb20.CountryCodePicker;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Models.TokenResponse.TokenResult;
import com.test.sample.hirecooks.Models.users.Result;
import com.test.sample.hirecooks.Models.users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.OnClickRateLimitedDecoratedListener;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.Utils.TrackGPS;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSignUpActivity extends BaseActivity {
    private static final String TAG = "UserSignUpActivity" ;
    private EditText editTextName, editTextEmail, editTextPassword,editTextConPassword,
            editTextPinCode,editTextFirmId,editTextUserType,editTextAddress,editTextPhone;
    private RadioGroup radioGender;
    private ProgressBarUtil progressBarUtil;
    private UserApi mService;
    private TextView location_picker, buttonSignUp, txtSignIn;
    private String deviceId;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private View appRoot;
    private CountryCodePicker ccp;
    private double mLatitude, mLongitude;
    GoogleMap mMap;
    private TrackGPS trackGPS;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initializeViews();
    }

    private void initializeViews() {
        try {
            Places.initialize(getApplicationContext(), Constants.locationApiKey);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        progressBarUtil = new ProgressBarUtil(this);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        appRoot = findViewById(R.id.appRoot);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        txtSignIn = findViewById(R.id.txtSignIn);
        editTextName = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPhone = findViewById(R.id.editTextPhone);
        ccp = findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(editTextPhone);
        editTextConPassword = findViewById(R.id.editTextConPassword);
        editTextAddress = findViewById(R.id.editTextAddress);
        location_picker = findViewById(R.id.location_picker);
        editTextPinCode = findViewById(R.id.editTextPinCode);
        editTextFirmId = findViewById(R.id.editTextFirmId);
        editTextUserType = findViewById(R.id.editTextUserType);
        radioGender = findViewById(R.id.radioGender);
        radioGender = findViewById(R.id.radioGender);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        editTextPhone.setText(Constants.CurrentUserPhoneNumber);
        buttonSignUp.setOnClickListener(new OnClickRateLimitedDecoratedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpValidation();
            }
        },5000));

        location_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(getApplicationContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UserSignInActivity.class));
                finish();
            }
        });
    }

    private void getToken(final String phone, int userId) {
        deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Toast.makeText(this, deviceId, Toast.LENGTH_SHORT).show();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("get token",newToken);
                if(!newToken.isEmpty()){
                    sendTokenToServer(phone,newToken,0,deviceId,userId);
                }else{
                    ShowToast("Token Not Generated");
                }
            }
        });
    }

    private void SignUpValidation() {
        final RadioButton radioSex = findViewById(radioGender.getCheckedRadioButtonId());
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String cpassword = editTextConPassword.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String firmId = "Not_Available";
        String user_type = "User";
        String bikeNumber = "Null";

        String gender = radioSex.getText().toString();

        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Please enter name");
            editTextName.requestFocus();
            return;
        }if (TextUtils.isEmpty(email)) {
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
        if (TextUtils.isEmpty(phone)) {
            editTextPhone.setError("Please enter phone");
            editTextPhone.requestFocus();
            return;
        }

        phone = ccp.getFullNumberWithPlus();
        phone = phone.replace(" " , "");
        userSignUp(name, email, password, phone, gender, user_type, firmId, bikeNumber);
    }

    private void userSignUp( String name, String email, String password, String phone, String gender, String userType, String firmId, String bikeNumber) {
       progressBarUtil.showProgress();
        mService = ApiClient.getClient().create(UserApi.class);
        final User user = new User(name, email, phone, password, gender, firmId, userType, bikeNumber);
        Call<Result> call = mService.createUser(user.getName(), user.getEmail(), user.getPhone(),user.getPassword(), user.getGender(), user.getFirmId(), user.getUserType(), user.getBikeNumber());
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response){
                int statusCode = response.code();
                assert response.body() != null;
                if(statusCode==200&& !response.body().getError()) {
                    System.out.println("Suree: "+response.message());
                    progressBarUtil.hideProgress();
                    Constants.SIGNUP_USER = response.body().getUser();
                    ShowToast(response.body().getMessage());
                    if (!response.body().getError()&&response.body().getUser().getPhone()!=null) {
                        getToken(response.body().getUser().getPhone(),response.body().getUser().getId());
                    }
                    finish();
                    startActivity(new Intent(UserSignUpActivity.this,SignupAddressActivity.class));
                }
                else{
                    ShowToast(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressBarUtil.hideProgress();
                System.out.println("Suree: "+t.getMessage());
                ShowToast("Please Check Intenet Connection");
            }
        });
    }

    private void sendTokenToServer(String phone, String newToken, int isServerToken, String deviceId, int userId) {
        Call<TokenResult> call1 = mService.registerToken(phone,newToken,deviceId,isServerToken,userId);
        call1.enqueue(new Callback<TokenResult>() {
            @Override
            public void onResponse(Call<TokenResult> call, Response<TokenResult> response) {
                int statusCode = response.code();
                if(statusCode==200&&response.body().getError()==false) {
                    progressBarUtil.hideProgress();
                    ShowToast("Token Saved Successfully");

                }
                else{
                    ShowToast("Failed due to :"+response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<TokenResult> call, Throwable t) {
                progressBarUtil.hideProgress();
                ShowToast("Please Check Intenet Connection");
                System.out.println("Suree: "+t.getMessage());
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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                    editTextAddress.setText(place.getName() + "," + place.getAddress());
                    Log.i(TAG, "Place: " + place.getName());

                    LatLng latLng = new LatLng(place.getLatLng().longitude, place.getLatLng().longitude);
                    getAddress(latLng);

                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    // TODO: Handle the error.
                    Status status = Autocomplete.getStatusFromIntent(data);
                    Log.i(TAG, status.getStatusMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                editTextPinCode.setText(address.getPostalCode());
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append(",");
                }
                result = address.getAddressLine(0);
                return result;
            }
            return result;
        } catch (IOException e) {
            Log.e("MapLocation", "Unable connect to Geocoder", e);
            return result;
        }
    }
}
