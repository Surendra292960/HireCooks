package com.test.sample.hirecooks.Activity.Users.FirmUsers;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.Users.UserResponse;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateFirmUser extends AppCompatActivity {
    private Button buttonUpdate;
    private EditText editTextUsername,editTextUserBike, editTextEmail,editTextPhone, editTextFirmId,editTextUserType;
    private RadioGroup radioGender;
    private User user;
    private ProgressBarUtil progressBarUtil;
    private UserApi mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_update_profile);
        initViews();
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            user = (User) bundle.getSerializable( "User" );
            if(user!=null){
                setUser(user);
            }
        }
    }

    private void initViews() {
        progressBarUtil = new ProgressBarUtil(this);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        editTextUserBike = findViewById(R.id.editTextUserBike);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextUserType = findViewById(R.id.editTextUserType);
        editTextFirmId = findViewById(R.id.editTextFirmId);
        radioGender = findViewById(R.id.radioGender);
    }

    private void setUser(User user) {
        editTextUsername.setText(user.getName());
        editTextEmail.setText(user.getEmail());
        editTextPhone.setText(user.getPhone());
        editTextUserType.setText(user.getUserType());
        editTextFirmId.setText(user.getFirmId());
        if(user.getUserType().equalsIgnoreCase( "Rider" )){
            editTextUserBike.setVisibility( View.VISIBLE );
            editTextUserBike.setText(user.getBikeNumber());
        }else{
            editTextUserBike.setText(user.getBikeNumber());
        }

        if (user.getGender().equalsIgnoreCase("male")) {
            radioGender.check(R.id.radioMale);
        } else {
            radioGender.check(R.id.radioFemale);
        }

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData(user);

            }
        });
    }

    private void validateData(User user) {
        String image,signupDate,password,pincode,address = null;
        final RadioButton radioSex = this.findViewById(radioGender.getCheckedRadioButtonId());
        String name = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String firmId = editTextFirmId.getText().toString().trim();
        String bikeNumber = editTextUserBike.getText().toString().trim();
        String userType = user.getUserType();
        image = user.getImage();
        password = null;
        signupDate = user.getSignupDate();
        pincode = user.getPincode();
        address = user.getAddress();

        String gender = radioSex.getText().toString();

        if (TextUtils.isEmpty(name)) {
            editTextUsername.setError("Please enter name");
            editTextUsername.requestFocus();
            return;
        }if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Please enter email");
            editTextEmail.requestFocus();
            return;
        }if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email");
            editTextEmail.requestFocus();
            return;
        }if (TextUtils.isEmpty(phone)) {
            editTextEmail.setError("Please enter phone");
            editTextEmail.requestFocus();
            return;
        }if (TextUtils.isEmpty(firmId)) {
            editTextEmail.setError("Please enter firmId");
            editTextEmail.requestFocus();
            return;
        }

        List<UserResponse> exampleList = new ArrayList<>(  );
        List<User> userList = new ArrayList<>(  );
        UserResponse example = new UserResponse();
        User users = new User(  );
        users.setName( name );
        users.setEmail( email );
        users.setPhone( phone );
        users.setGender( gender );
        users.setUserType( userType );
        users.setFirmId( firmId );
        users.setBikeNumber( bikeNumber );
        userList.add( users );
        example.setUsers( userList );
        exampleList.add( example );
        updateUser(exampleList);
        Gson gson = new Gson();
        String json = gson.toJson( exampleList );
        System.out.println( "Suree : "+json );
    }


    private void updateUser(List<UserResponse> exampleList) {
        progressBarUtil.showProgress();
        mService = ApiClient.getClient().create(UserApi.class);
        Call<List<UserResponse>> call = mService.updateUser(user.getId(),exampleList);
        call.enqueue(new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                if(response.code()==200) {
                    progressBarUtil.hideProgress();
                    for(UserResponse example:response.body()){
                        Toast.makeText( UpdateFirmUser.this, example.getMessage(), Toast.LENGTH_LONG ).show();
                        if(!example.getError()) {
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                progressBarUtil.hideProgress();
                Toast.makeText(UpdateFirmUser.this, t.getMessage(), Toast.LENGTH_LONG).show();
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
