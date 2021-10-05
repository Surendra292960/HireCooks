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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hbb20.CountryCodePicker;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.TokenResponse.TokenResult;
import com.test.sample.hirecooks.Models.Users.Example;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.OnClickRateLimitedDecoratedListener;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.Utils.TrackGPS;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSignUpActivity extends BaseActivity {
    private static final String TAG = "UserSignUpActivity" ;
    SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
    private EditText editTextName, editTextEmail, editTextPassword,editTextConPassword,
            editTextPinCode,editTextFirmId,editTextAddress,editTextPhone;
    private RadioGroup radioGender;
   // private Spinner editTextUserType;
    private ProgressBarUtil progressBarUtil;
    private UserApi mService;
    private TextView location_picker, txtSignIn,verified;
    private Button buttonSignUp;
    private String deviceId;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private View appRoot;
    private CountryCodePicker ccp;
    private double mLatitude, mLongitude;
    GoogleMap mMap;
    private TrackGPS trackGPS;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
   // private List<String> user_type_list;

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
        verified = findViewById(R.id.verified);
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
       // editTextUserType = findViewById(R.id.editTextUserType);
        radioGender = findViewById(R.id.radioGender);
        radioGender = findViewById(R.id.radioGender);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        editTextPhone.setFocusableInTouchMode( true );

     /*   String[] userType_array = this.getResources().getStringArray(R.array.user_type);
        user_type_list = new ArrayList<>();
        for(String text:userType_array) {
            user_type_list.add(text);
        }
        UserTypeAdapter spinnerArrayAdapter = new UserTypeAdapter(this, user_type_list);
        editTextUserType.setAdapter( spinnerArrayAdapter );*/

        if(Constants.CurrentUserPhoneNumber!=null){
            verified.setVisibility(View.VISIBLE);
            editTextPhone.setText(Constants.CurrentUserPhoneNumber);
        }else{
            verified.setVisibility(View.GONE);
        }

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
                startActivity(new Intent(getApplicationContext(), UserSignInActivity.class) .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        });
    }


    private void getToken(final String phone, int userId) {
        deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        if(!token.isEmpty()){
                            sendTokenToServer(phone,token,0,deviceId,userId);
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
        //String user_type = editTextUserType.getSelectedItem().toString();
        String bikeNumber = "Not_Available";

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
        }/*if (!user_type.isEmpty()&&user_type.equalsIgnoreCase( "Please Select" )) {
            ShowToast( "Please Select User Type" );
            return;
        }*/
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

        List<Example> exampleList = new ArrayList<>(  );
        List<User> userList = new ArrayList<>(  );
        Example example = new Example();
        User user = new User(  );
        user.setName( name );
        user.setEmail( email );
        user.setPassword( password );
        user.setPhone( phone );
        user.setGender( gender );
        user.setUserType( "User" );
        user.setSignupDate( format.format(new Date() ) );
        user.setFirmId( "Not_Available" );
        user.setBikeNumber( bikeNumber );
        userList.add( user );
        example.setUsers( userList );
        exampleList.add( example );
        userSignUp(exampleList);
    }

    private void userSignUp( List<Example> exampleList) {
       progressBarUtil.showProgress();
        mService = ApiClient.getClient().create(UserApi.class);
        Call<List<Example>> call = mService.createUser(exampleList);
        call.enqueue(new Callback<List<Example>>() {
            @Override
            public void onResponse(Call<List<Example>> call, Response<List<Example>> response){
                if(response.code()==200){
                    progressBarUtil.hideProgress();
                    for(Example example:response.body()){
                        ShowToast(example.getMessage());
                        if(!example.getError()){
                          for(User user: example.getUsers()){
                              Constants.SIGNUP_USER = user;
                              if (user.getPhone()!=null) {
                                  getToken(user.getPhone(),user.getId());
                                  // sigupWithFirebase(email,password);
                              }
                              finish();
                              startActivity(new Intent(UserSignUpActivity.this,SignupAddressActivity.class) .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                              overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                          }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Example>> call, Throwable t) {
                progressBarUtil.hideProgress();
                System.out.println("Suree: "+t.getMessage());
                ShowToast("Please Check Intenet Connection");
            }
        });
    }

    private void sigupWithFirebase(String email ,String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(UserSignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                                Log.d("Suree", "Signup with firebase");
                        } else {
                            String message = "Somthing is wrong, we will fix it soon...";
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }
                        }
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
