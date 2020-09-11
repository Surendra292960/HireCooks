package com.test.sample.hirecooks.Activity.Users;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.users.Result;
import com.test.sample.hirecooks.Models.users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfile extends AppCompatActivity {
    private Button buttonUpdate;
    private EditText editTextUsername, editTextEmail,editTextPhone, editTextFirmId,editTextUserType;
    private RadioGroup radioGender;
    private User user;
    private ProgressBarUtil progressBarUtil;
    private UserApi mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Update Profile");
        user = SharedPrefManager.getInstance(this).getUser();
        progressBarUtil = new ProgressBarUtil(this);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextUserType = findViewById(R.id.editTextUserType);
        editTextFirmId = findViewById(R.id.editTextFirmId);
        radioGender = findViewById(R.id.radioGender);
        editTextUsername.setText(user.getName());
        editTextEmail.setText(user.getEmail());
        editTextPhone.setText(user.getPhone());
        editTextUserType.setText(user.getUserType());
        editTextFirmId.setText(user.getFirmId());

        if (user.getGender().equalsIgnoreCase("male")) {
            radioGender.check(R.id.radioMale);
        } else {
            radioGender.check(R.id.radioFemale);
        }

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });

        NestedScrollView nested_content = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        nested_content.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < oldScrollY) { // up
                    // animateNavigation(false);
                    // animateToolBar(false);
                }
                if (scrollY > oldScrollY) { // down
                    // animateNavigation(true);
                    //  animateToolBar(true);
                }
            }
        });
    }

    boolean isNavigationHide = false;

/*    private void animateNavigation(final boolean hide) {
        if (isNavigationHide && hide || !isNavigationHide && !hide) return;
        isNavigationHide = hide;
        int moveY = hide ? (2 * this.mNavigationView.getHeight()) : 0;
        this.mNavigationView.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    boolean isToolBarHide = false;

    private void animateToolBar(final boolean hide) {
        if (isToolBarHide && hide || !isToolBarHide && !hide) return;
        isToolBarHide = hide;
        int moveY = hide ? -(2 * this.toolbar_layout.getHeight()) : 0;
        this.toolbar_layout.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }*/

    private void updateUser() {
        progressBarUtil.showProgress();
        final RadioButton radioSex = this.findViewById(radioGender.getCheckedRadioButtonId());
        String name = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String firmId = SharedPrefManager.getInstance(this).getUser().getFirmId();
        String userType = SharedPrefManager.getInstance(this).getUser().getUserType();
        String image = null;
        String bikeNumber = "Null";
        String gender = radioSex.getText().toString();

        mService = ApiClient.getClient().create(UserApi.class);
        User user = new User(SharedPrefManager.getInstance(this).getUser().getId(), name, email, phone, gender, firmId, userType);
        Call<Result> call = mService.updateUser(user.getId(), user.getName(), user.getEmail(),user.getPhone(), user.getGender(), user.getFirmId(), user.getUserType(), bikeNumber);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                int statusCode = response.code();
                if(statusCode==200&&response.body().getError()==false){
                    progressBarUtil.hideProgress();
                    if (!response.body().getError()) {
                        Constants.CurrentUser = response.body();
                        SharedPrefManager.getInstance(UpdateProfile.this).userLogin(Constants.CurrentUser.getUser());
                        Toast.makeText(UpdateProfile.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        UpdateProfile.this.finish();
                    }
                }else{
                    Toast.makeText(UpdateProfile.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
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
