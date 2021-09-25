package com.test.sample.hirecooks.Activity.Users;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.test.sample.hirecooks.Adapter.Images.ImagesAdapter;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.ImagesResponse.Image;
import com.test.sample.hirecooks.Models.ImagesResponse.Images;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailsActivity extends AppCompatActivity {
    private User user;
    private TextView textViewName, textViewEmail, textViewGender, textViewUserType, textViewPhone, textViewAddress, text_action_bottom1, text_action_bottom2;
    private RecyclerView recyclerView;
    private List<Image> usersImagesList;
    private List<String> usersImagesUrlList;
    private ImagesAdapter adapter;
    private View appRoot;
    private int userId;
    private ProgressBarUtil progressBarUtil;
    private ArrayList<Uri> imagesList = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        userId = SharedPrefManager.getInstance(this).getUser().getId();
        initializeViews();
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            user = (User)bundle.getSerializable("User");
        }
        getUserImages(user.getId());

        if(user!=null){
            textViewName.setText(user.getName());
            textViewEmail.setText("Email:  "+user.getEmail());
            textViewGender.setText("Gender:  "+user.getGender());
            textViewUserType.setText("UserTypeAdapter:  "+user.getUserType());
            textViewPhone.setText("phone:  "+user.getPhone());
            textViewAddress.setText("Address:  "+user.getAddress());
        }
    }

    private void initializeViews() {
        progressBarUtil = new ProgressBarUtil(this);
        appRoot = findViewById(R.id.appRoot);
        recyclerView = findViewById(R.id.user_images_viewPager);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        textViewName = findViewById(R.id.textViewName);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewGender = findViewById(R.id.textViewGender);
        textViewUserType = findViewById(R.id.textViewUserType);
        textViewPhone = findViewById(R.id.textViewPhone);
        textViewAddress = findViewById(R.id.textViewAddress);
        text_action_bottom1 = findViewById(R.id.text_action_bottom1);
        text_action_bottom2 = findViewById(R.id.text_action_bottom2);

        text_action_bottom2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.activity_calender_view, null);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
                calendar.set(Calendar.DAY_OF_MONTH, 9);
                calendar.set(Calendar.YEAR, 2012);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                calendar.add(Calendar.YEAR, 1);
                CalendarView calendarView = view.findViewById(R.id.calendarView);
                TextView txtDate = view.findViewById(R.id.date);

                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                        txtDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                        textViewAddress.setText("Address:  "+user.getAddress()+"\n "+ dayOfMonth + "-" + (month + 1) + "-" + year);
                    }
                });

                BottomSheetDialog dialog = new BottomSheetDialog(UserDetailsActivity.this);
                dialog.setContentView(view);
                dialog.show();
                dialog.setCancelable(true);
            }
        });

        text_action_bottom1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
                RadioButton mPerson = view.findViewById(R.id.booking_personal);
                RadioButton mBusiness = view.findViewById(R.id.booking_company);
                LinearLayout mPersonLay = view.findViewById(R.id.personal_layout);
                LinearLayout mBusinessLay = view.findViewById(R.id.business_layout);
                TextView price = view.findViewById(R.id.price);
                Button btn = view.findViewById(R.id.proceed_payment);
                price.setText("252 \u20B9");

                mPerson.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPersonLay.setVisibility(View.VISIBLE);
                        mBusinessLay.setVisibility(View.GONE);
                    }
                });

                mBusiness.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBusinessLay.setVisibility(View.VISIBLE);
                        mPersonLay.setVisibility(View.GONE);
                    }
                });

                BottomSheetDialog dialog = new BottomSheetDialog(UserDetailsActivity.this);
                dialog.setContentView(view);
                dialog.show();

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
                        TextView price = view.findViewById(R.id.price);
                        price.setText("1000000");
                        BottomSheetDialog dialog = new BottomSheetDialog(UserDetailsActivity.this);
                        dialog.setContentView(view);
                        dialog.show();
                    }
                });
            }
        });
    }

   private void getUserImages(Integer userId) {
    progressBarUtil.showProgress();
    UserApi mService = ApiClient.getClient().create(UserApi.class);
    Call<Images> call = mService.getUserImages(userId);
    call.enqueue(new Callback<Images>() {
        @Override
        public void onResponse(@NonNull Call<Images> call, @NonNull Response<Images> response) {
            if(response.code()==200){
                if(response.body()!=null){
                    progressBarUtil.hideProgress();
                    usersImagesList = response.body().getImages();
                    if(usersImagesList!=null&&usersImagesList.size()!=0){
                        String images = usersImagesList.get(0).getImages();
                        String [] imagesArray = images.split(",");
                        System.out.println("Suree box"+imagesArray);
                        adapter = new ImagesAdapter(UserDetailsActivity.this,imagesArray);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }
        }

        @Override
        public void onFailure(@NonNull Call<Images> call, @NonNull Throwable t) {
            progressBarUtil.hideProgress();
            Toast.makeText(UserDetailsActivity.this,R.string.error,Toast.LENGTH_LONG).show();
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
