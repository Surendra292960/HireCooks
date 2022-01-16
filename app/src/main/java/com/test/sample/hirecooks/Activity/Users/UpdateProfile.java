package com.test.sample.hirecooks.Activity.Users;
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
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfile extends AppCompatActivity {
    private Button buttonUpdate;
    private EditText editTextUsername, editTextEmail,editTextPhone, editTextFirmId,editTextUserType,editTextBikeNumber;
    private RadioGroup radioGender;
    private User user;
    private ProgressBarUtil progressBarUtil;
    private UserApi mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        initViews();
    }

    private void initViews() {
        user = SharedPrefManager.getInstance(this).getUser();
        progressBarUtil = new ProgressBarUtil(this);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextUserType = findViewById(R.id.editTextUserType);
        editTextFirmId = findViewById(R.id.editTextFirmId);
        editTextBikeNumber = findViewById(R.id.editTextUserBike);
        radioGender = findViewById(R.id.radioGender);

        editTextUsername.setText(user.getName());
        editTextEmail.setText(user.getEmail());
        editTextPhone.setText(user.getPhone());
        editTextUserType.setText(user.getUserType());
        editTextFirmId.setText(user.getFirmId());

        if(user.getUserType().equalsIgnoreCase( "Rider" )){
            editTextBikeNumber.setVisibility( View.VISIBLE );
            editTextBikeNumber.setText( user.getBikeNumber() );
        }else{
            editTextBikeNumber.setVisibility( View.GONE );
        }

        if (user.getGender().equalsIgnoreCase("male")) {
            radioGender.check(R.id.radioMale);
        } else {
            radioGender.check(R.id.radioFemale);
        }

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });
    }

    private void validation() {
        final RadioButton radioSex = this.findViewById(radioGender.getCheckedRadioButtonId());
        String name = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String bikeNumber = editTextBikeNumber.getText().toString().trim();
        String firmId = user.getFirmId();
        String userType = user.getUserType();
        String image = null;
        String gender = radioSex.getText().toString();

        if(TextUtils.isEmpty( name )){
            editTextUsername.setError("Please enter name");
            editTextUsername.requestFocus();
            return;
        } if(TextUtils.isEmpty( email )){
            editTextEmail.setError("Please enter email");
            editTextEmail.requestFocus();
            return;
        } if(TextUtils.isEmpty( phone )){
            editTextPhone.setError("Please enter phone");
            editTextPhone.requestFocus();
            return;
        }if(user.getUserType().equalsIgnoreCase( "Rider" )){
            if(TextUtils.isEmpty( bikeNumber )){
                editTextBikeNumber.setError("Please enter Bike Number");
                editTextBikeNumber.requestFocus();
                return;
            }
        }else{
            bikeNumber = "Not_Available";
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
                        Toast.makeText( UpdateProfile.this, example.getMessage(), Toast.LENGTH_SHORT ).show();
                        if(!example.getError()) {
                            for(User user1:example.getUsers()){
                                SharedPrefManager.getInstance( UpdateProfile.this ).userLogin(user1 );
                                Toast.makeText( UpdateProfile.this, example.getMessage(), Toast.LENGTH_LONG ).show();
                                UpdateProfile.this.finish();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                progressBarUtil.hideProgress();
                Toast.makeText(UpdateProfile.this, t.getMessage(), Toast.LENGTH_LONG).show();
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
