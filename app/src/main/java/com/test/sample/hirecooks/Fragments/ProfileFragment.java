package com.test.sample.hirecooks.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.users.Result;
import com.test.sample.hirecooks.Models.users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.APIUrl;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.Utils.ProgressRequestBody;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.Utils.UploadCallBack;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.io.File;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.test.sample.hirecooks.Utils.Constants.USER_PROFILE;

public class ProfileFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener, UploadCallBack {
    private AppCompatButton buttonUpdate;
    private TextInputEditText editTextUsername, editTextEmail,editTextPhone,
            editTextFirmId,editTextUserType;
    private TextView edit_profile_image;
    private RadioGroup radioGender;
    private View appRoot;
    private CircleImageView profile_image;
    private User user;
    private ProgressBarUtil progressBarUtil;
    private static final int PICK_FILE_REQUEST = 1222;
    private Uri selectedFileUri;
    private UserApi mService;

    public static ProfileFragment newInstance() {
        Bundle args = new Bundle();
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        progressBarUtil = new ProgressBarUtil(getActivity());
        buttonUpdate = view.findViewById(R.id.buttonUpdate);
        appRoot = view.findViewById(R.id.appRoot);
        editTextUsername = view.findViewById(R.id.editTextUsername);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        editTextUserType = view.findViewById(R.id.editTextUserType);
        editTextFirmId = view.findViewById(R.id.editTextFirmId);
        profile_image = view.findViewById(R.id.profile_image);
        edit_profile_image = view.findViewById(R.id.edit_profile_image);
        radioGender = view.findViewById(R.id.radioGender);

        user = SharedPrefManager.getInstance(getActivity()).getUser();

        editTextUsername.setText(user.getName());
        editTextEmail.setText(user.getEmail());
        editTextPhone.setText(user.getPhone());
        editTextUserType.setText(user.getUserType());
        editTextFirmId.setText(user.getFirmId());
        if (!TextUtils.isEmpty(user.getImage())) {
            Picasso.with(getActivity()).load(APIUrl.PROFILE_URL+USER_PROFILE).into(profile_image);
        }

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
        
        edit_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user!=null)
                    chooseImage();
            }
        });
        return view;
    }

    private void chooseImage() {
        startActivityForResult(Intent.createChooser(FileUtils.createGetContentIntent(),"Select a File"), PICK_FILE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == RESULT_OK)) {
            if (requestCode == PICK_FILE_REQUEST) {
                if (data != null) {
                    selectedFileUri = data.getData();
                    if (selectedFileUri != null && !Objects.requireNonNull(selectedFileUri.getPath()).isEmpty()) {
                         profile_image.setImageURI(selectedFileUri);
                        uploadFile();
                    }
                }
            }
        }
    }

    private void uploadFile() {
        if(selectedFileUri!=null) {
            File file=FileUtils.getFile(getActivity(),selectedFileUri);
            String fileName= SharedPrefManager.getInstance(getActivity()).getUser().getPhone() + FileUtils.getExtension(file.toString());
            ProgressRequestBody requestFile=new ProgressRequestBody(file, this);
            final MultipartBody.Part body=MultipartBody.Part.createFormData("uploaded_file",fileName,requestFile);
            final MultipartBody.Part userPhone=MultipartBody.Part.createFormData("phone",SharedPrefManager.getInstance(getActivity()).getUser().getPhone());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mService = ApiClient.getClient().create(UserApi.class);
                    mService.uploadFile(userPhone,body)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                    Toast.makeText(getActivity(), response.body(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }).start();
        }
    }


    @Override
    public void onProgressUpdate(int percentage) {

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private void updateUser() {
        progressBarUtil.showProgress();
        final RadioButton radioSex = getActivity().findViewById(radioGender.getCheckedRadioButtonId());
        String name = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String firmId = SharedPrefManager.getInstance(getActivity()).getUser().getFirmId();
        String userType = SharedPrefManager.getInstance(getActivity()).getUser().getUserType();
        String image = null;
        String bikeNumber = "Null";
        String gender = radioSex.getText().toString();

        mService = ApiClient.getClient().create(UserApi.class);
        User user = new User(SharedPrefManager.getInstance(getActivity()).getUser().getId(), name, email, phone, gender, firmId, userType);
        Call<Result> call = mService.updateUser(user.getId(), user.getName(), user.getEmail(),user.getPhone(), user.getGender(), user.getFirmId(), user.getUserType(), bikeNumber);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                int statusCode = response.code();
                if(statusCode==200&&response.body().getError()==false){
                    progressBarUtil.hideProgress();
                    if (!response.body().getError()) {
                        Constants.CurrentUser = response.body();
                        SharedPrefManager.getInstance(getActivity()).userLogin(Constants.CurrentUser.getUser());
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressBarUtil.hideProgress();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}