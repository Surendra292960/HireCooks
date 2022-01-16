package com.test.sample.hirecooks.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.bumptech.glide.Glide;
import com.test.sample.hirecooks.Activity.Chat.ChatActivity;
import com.test.sample.hirecooks.Activity.Cooks.UpdateCookDetails;
import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.Activity.ManageAccount.ManageProducts.EditMenuActivity;
import com.test.sample.hirecooks.Activity.ManageAddress.SecondryAddressActivity;
import com.test.sample.hirecooks.Activity.Users.UpdateProfile;
import com.test.sample.hirecooks.Activity.Users.UsersActivity;
import com.test.sample.hirecooks.Adapter.Users.ProfileAdminDashAdapter;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.Offer.Offer;
import com.test.sample.hirecooks.Models.SubCategory.SubcategoryResponse;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.Models.Users.UserResponse;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.APIUrl;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.NetworkUtil;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.Utils.ProgressRequestBody;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.Utils.UploadCallBack;
import com.test.sample.hirecooks.WebApis.ProductApi;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.test.sample.hirecooks.Utils.Constants.USER_PROFILE;

public class ProfileFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener, UploadCallBack {
    private TextView user_name,shop;
    private View appRoot;
    private CircleImageView profile_image;
    private User user;
    private ProgressBarUtil progressBarUtil;
    private static final int PICK_FILE_REQUEST = 1222;
    private Uri selectedFileUri;
    private UserApi mService;
    private MainActivity mainActivity;
    private ImageButton profile_image_layout,edit_profile,sendBtn;
    private SwitchCompat order_not_accepting;
    private LinearLayout admin_dash_lay,user_layout,all_profile_layout,no_internet_connection_layout;
    private Button  manage_category, change_address,chat,logout,manage_cook,start;
    private CardView manage_account,manage_employee,manage_employee_report,manage_all_employee, manage_address,manage_recieved_orders;
    private BaseActivity baseActivity;
    private RecyclerView admin_dash_recycler;
    private User users;

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
        initViews(view);
        if(NetworkUtil.checkInternetConnection(mainActivity)) {
            all_profile_layout.setVisibility( View.VISIBLE );
            no_internet_connection_layout.setVisibility( View.GONE );
            getUserByFirmId(user.getFirmId());
        }
        else {
            all_profile_layout.setVisibility( View.GONE );
            no_internet_connection_layout.setVisibility( View.VISIBLE );
        }
        return view;
    }

    @SuppressLint("WrongConstant")
    private void initViews(View view) {
        progressBarUtil = new ProgressBarUtil(mainActivity);
        baseActivity = new BaseActivity();
        appRoot = view.findViewById(R.id.appRoot);
        start = view.findViewById(R.id.start);
        manage_category = view.findViewById(R.id.manage_category);
        all_profile_layout = view.findViewById(R.id.all_profile_layout);
        profile_image_layout = view.findViewById(R.id.profile_image_layout);
        profile_image = view.findViewById(R.id.profile_image);
        edit_profile = view.findViewById(R.id.edit_profile);
        user_name = view.findViewById(R.id.user_name);
        order_not_accepting = view.findViewById(R.id.order_not_accepting);
        no_internet_connection_layout = view.findViewById(R.id.no_internet_connection_layout);
        shop = view.findViewById(R.id.shop);
        sendBtn = view.findViewById(R.id.sendBtn);
        user_name.setVisibility(View.VISIBLE);

        admin_dash_lay = view.findViewById(R.id.admin_dash_lay);
        manage_account =  view.findViewById(R.id.manage_account);
        manage_employee =  view.findViewById(R.id.manage_employee);
        manage_employee_report =  view.findViewById(R.id.manage_employee_report);
        manage_all_employee =  view.findViewById(R.id.manage_all_employee);
        manage_address =  view.findViewById(R.id.manage_adress);
        user_layout =  view.findViewById(R.id.user_layout);
        manage_cook =  view.findViewById(R.id.manage_cook);
        change_address =  view.findViewById(R.id.change_address);
        admin_dash_recycler =  view.findViewById(R.id.admin_dash_recycler);
        //manage_recieved_orders =  view.findViewById(R.id.manage_recieved_orders);
        chat =  view.findViewById(R.id.chat);
        logout =  view.findViewById(R.id.logout);
        user = SharedPrefManager.getInstance(mainActivity).getUser();

        user_name.setText(user.getName());
        if (USER_PROFILE!=null) {
            Glide.with(mainActivity).load( APIUrl.PROFILE_URL+USER_PROFILE).into(profile_image);
        }
        if(user.getUserType().equalsIgnoreCase( "Admin" )||user.getUserType().equalsIgnoreCase( "Manager" )) {
            admin_dash_lay.setVisibility( View.VISIBLE );

        }
        else{
            admin_dash_lay.setVisibility( View.GONE );
        }
        if(user.getUserType().equalsIgnoreCase( "User" )
                ||user.getUserType().equalsIgnoreCase( "Cook" )
                ||user.getUserType().equalsIgnoreCase( "Rider" )
                ||user.getUserType().equalsIgnoreCase( "Employee" )){
            user_layout.setVisibility( View.VISIBLE );
            admin_dash_lay.setVisibility( View.GONE );
            change_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), SecondryAddressActivity.class) .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });
            if(user.getUserType().equalsIgnoreCase( "Employee" )||user.getUserType().equalsIgnoreCase( "Rider" )){
                chat.setVisibility( View.VISIBLE );
                chat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(users!=null){
                            Intent intent = new Intent( mainActivity, ChatActivity.class );
                            Bundle bundle = new Bundle(  );
                            bundle.putSerializable( "User",users );
                            intent.putExtras( bundle );
                            mainActivity.startActivity( intent );
                        }else{
                            Intent intent = new Intent( mainActivity, ChatActivity.class );
                            Bundle bundle = new Bundle(  );
                            bundle.putSerializable( "User",user );
                            intent.putExtras( bundle );
                            mainActivity.startActivity( intent );
                        }
                    }
                });
            }else{
                chat.setVisibility( View.GONE );
            }
            if(user.getUserType().equalsIgnoreCase( "Cook" )){
                manage_cook.setVisibility( View.VISIBLE );
                manage_cook.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity( new Intent(getActivity(), UpdateCookDetails.class) .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                } );
            }else{
                manage_cook.setVisibility( View.GONE );
            }
        }else if(user.getUserType().equalsIgnoreCase( "Admin" )||user.getUserType().equalsIgnoreCase( "Manager" )) {
            user_layout.setVisibility( View.GONE );
            admin_dash_lay.setVisibility( View.VISIBLE );
            List<Offer> offerList = new ArrayList<>();
            offerList.add( new Offer( 0, "Account", "Manage Your Account", "https://i.ibb.co/S5tq9C9/management-1.png", "RED" ) );
            offerList.add( new Offer( 1, "Manage Employee", "Manage Your Employee", "https://i.ibb.co/tBLnHGq/employee.png", "RED" ) );
            offerList.add( new Offer( 2, "Report", "Generate Your Employes Report", "https://i.ibb.co/vmqVpSR/report-1.png", "RED" ) );
            offerList.add( new Offer( 3, "All Employees", "Generate Your All Employes Report", "https://i.ibb.co/vmqVpSR/report-1.png", "RED" ) );
            offerList.add( new Offer( 4, "Recieved Order", "Manage Your Recieved Orders", "https://i.ibb.co/vXT6qG0/courier.png", "RED" ) );
            offerList.add( new Offer( 5, "Manage Your Address", "Manage Your Address", "https://i.ibb.co/XFBH6DZ/address.png", "RED" ) );
            offerList.add( new Offer( 6, "Collaboration", "Collaborate with your Employee", "https://i.ibb.co/8732tsP/deal.png", "RED" ) );
            offerList.add( new Offer( 7, "Ad", "Manage Your Add", "https://www.linkpicture.com/q/button_5.png", "RED" ) );

            ProfileAdminDashAdapter adapter = new ProfileAdminDashAdapter( mainActivity, offerList );
            admin_dash_recycler.setAdapter( adapter );
            GridLayoutManager linearLayoutManager = new GridLayoutManager( mainActivity, 2 );
            if (mainActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                linearLayoutManager.setOrientation( LinearLayout.VERTICAL );
            } else {
                linearLayoutManager.setOrientation( LinearLayout.VERTICAL );
            }
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams( RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT );
            params.setMargins( 20, 20, 20, 20 );
            linearLayoutManager.canScrollHorizontally();
            admin_dash_recycler.setLayoutManager( linearLayoutManager );
            admin_dash_recycler.setItemAnimator( new DefaultItemAnimator() );

            order_not_accepting.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<SubcategoryResponse> examples = new ArrayList<>(  );
                    List<Subcategory> subcategoryList = new ArrayList<>(  );
                    Subcategory subcategory = new Subcategory();
                    subcategoryList.add( subcategory );
                    SubcategoryResponse example = new SubcategoryResponse();
                    example.setSubcategory(  subcategoryList);
                    examples.add( example );
                    if (order_not_accepting.isChecked()) {
                        order_not_accepting.setShowText(true );
                        shop.setText( "Shop Open" );
                        subcategory.setAcceptingOrder( 1 );
                        orderNotAccepting(user.getFirmId(),examples);
                    }else{
                        order_not_accepting.setShowText(true );
                        shop.setText( "Shop Closed" );
                        subcategory.setAcceptingOrder( 0 );
                        orderNotAccepting(user.getFirmId(),examples);
                    }
                }
            } );
        }else if(user.getUserType().equalsIgnoreCase( "SuperAdmin" )){
            user_layout.setVisibility( View.GONE );
            admin_dash_lay.setVisibility( View.GONE );
            manage_category.setVisibility( View.VISIBLE );
            start.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity( new Intent( mainActivity, UsersActivity.class ));
                }
            });
            manage_category.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity( new Intent( mainActivity, EditMenuActivity.class ));
                }
            });
        }

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(getActivity(), MobileActivity.class));
            }
        });

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UpdateProfile.class) );
            }
        });

        profile_image_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user!=null)
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);

                    }
                chooseImage();
            }
        });


        NestedScrollView nested_content =  view.findViewById(R.id.nested_scroll_view);
        nested_content.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < oldScrollY) { // up
                    animateNavigation(false);
                    animateToolBar(false);
                }
                if (scrollY > oldScrollY) { // down
                    animateNavigation(true);
                    animateToolBar(true);
                }
            }
        });
    }

    private void getUserByFirmId(String firmId) {
        UserApi service = ApiClient.getClient().create(UserApi.class);
        Call<List<UserResponse>> call = service.getUserByFirmId(firmId);
        call.enqueue(new Callback<List<UserResponse>>() {
            @SuppressLint("ShowToast")
            @Override
            public void onResponse(@NonNull Call<List<UserResponse>> call, @NonNull Response<List<UserResponse>> response) {
                if (response.code() == 200) {
                    for(UserResponse example:response.body()){
                        if (!example.getError()){
                            for(User user:example.getUsers()){
                                if(user.getUserType().equalsIgnoreCase( "Admin" )){
                                    users = user;
                                }
                            }
                        }
                    }
                }
            }

            @SuppressLint("ShowToast")
            @Override
            public void onFailure(@NonNull Call<List<UserResponse>> call, @NonNull Throwable t) {
                System.out.println( "Suree : "+t.getMessage() );
            }
        });
    }

    private void orderNotAccepting(String firmId, List<SubcategoryResponse> examples) {
        ProductApi mService = ApiClient.getClient().create(ProductApi.class);
        Call<ArrayList<SubcategoryResponse>> call = mService.accepting_Orders(firmId,examples);
        call.enqueue(new Callback<ArrayList<SubcategoryResponse>>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<ArrayList<SubcategoryResponse>> call, Response<ArrayList<SubcategoryResponse>> response) {
                int statusCode = response.code();
                if (statusCode == 200) {

                }
            }

            @Override
            public void onFailure(Call<ArrayList<SubcategoryResponse>> call, Throwable t) {
                System.out.println("Suree : " + t.getMessage());
            }
        });
    }

    boolean isNavigationHide = false;

    private void animateNavigation(final boolean hide) {
        if (isNavigationHide && hide || !isNavigationHide && !hide) return;
        isNavigationHide = hide;
        int moveY = hide ? (2 * mainActivity.bottomNavigationView.getHeight()) : 0;
        mainActivity.bottomNavigationView.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    boolean isToolBarHide = false;

    private void animateToolBar(final boolean hide) {
        if (isToolBarHide && hide || !isToolBarHide && !hide) return;
        isToolBarHide = hide;
        int moveY = hide ? -(2 * mainActivity.toolbar_layout.getHeight()) : 0;
        mainActivity.toolbar_layout.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    private void chooseImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent , PICK_FILE_REQUEST );
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
            Random rnd = new Random();
            String orderNo = String.valueOf(100000000 + rnd.nextInt(900000000));
            File file=FileUtils.getFile(mainActivity,selectedFileUri);
            String fileName= orderNo + FileUtils.getExtension(file.toString());
            ProgressRequestBody requestFile=new ProgressRequestBody(file, this);
            final MultipartBody.Part body=MultipartBody.Part.createFormData("uploaded_file",fileName,requestFile);
            final MultipartBody.Part userEmail=MultipartBody.Part.createFormData("email",SharedPrefManager.getInstance(mainActivity).getUser().getEmail());
            System.out.println("Test "+userEmail);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mService = ApiClient.getClient().create(UserApi.class);
                    mService.uploadFile(userEmail,body)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                    Toast.makeText(mainActivity, response.body(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                    Toast.makeText(mainActivity, t.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }
}