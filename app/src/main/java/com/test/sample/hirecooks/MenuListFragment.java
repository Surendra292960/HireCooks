package com.test.sample.hirecooks;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.test.sample.hirecooks.Activity.Orders.RecievedOrderActivity;
import com.test.sample.hirecooks.Activity.PaymentGateway.PaymentGatewayActivity;
import com.test.sample.hirecooks.Activity.SubCategory.SubCategoryActivity.SubCategoryActivity;
import com.test.sample.hirecooks.Activity.Users.MessageActivity;
import com.test.sample.hirecooks.Activity.Users.UserSignInActivity;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Fragments.Home.HomeFragment;
import com.test.sample.hirecooks.Models.users.User;
import com.test.sample.hirecooks.Utils.ProgressRequestBody;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.Utils.UploadCallBack;
import com.test.sample.hirecooks.WebApis.UserApi;
import java.io.File;
import java.util.Objects;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static android.app.Activity.RESULT_OK;

public class MenuListFragment extends Fragment implements UploadCallBack {
    private static final int PICK_FILE_REQUEST = 1222;
    private Uri selectedFileUri;
    //private CircleImageView img_avatar;
    private UserApi mService;
    private User user = SharedPrefManager.getInstance(getActivity()).getUser();

    public static MenuListFragment newInstance() {
        Bundle args = new Bundle();
        MenuListFragment fragment = new MenuListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        TextView userName = view.findViewById(R.id.title);
        TextView userPhone = view.findViewById(R.id.phone);
     /*   img_avatar = view.findViewById(R.id.ivMenuUserProfilePhoto);
        img_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });*/

       /* if (!TextUtils.isEmpty(user.getImage())) {
            Picasso.with(getActivity()).load(APIUrl.PROFILE_URL+user.getImage()).placeholder(R.drawable.ic_account_circle_black_24dp).into(img_avatar);
        }*/
        userName.setText("Welcome:  "+user.getName());
        userPhone.setText("Phone:  "+user.getPhone());
        NavigationView vNavigation = view.findViewById(R.id.vNavigation);
        vNavigation.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.menu_message) {
                startActivity( new Intent(getActivity(), MessageActivity.class));
            } else if (id == R.id.logout) {
                logout();
            } else if (id == R.id.menu_help) {
                startActivity( new Intent(getActivity(), PaymentGatewayActivity.class));
            } else if (id == R.id.menu_about) {
                Intent intent = new Intent(getActivity(), TestActivity.class);
                intent.putExtra("orderid", "1234");
                intent.putExtra("custid", "1");
                startActivity(intent);
            } else if (id == R.id.menu_offers) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(getActivity(), SubCategoryActivity.class);
                bundle.putSerializable("CategoryName" , "Offers");
                intent.putExtras(bundle);
                startActivity(intent);
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
                }
            } else if (id == R.id.menu_popularproducts) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(getActivity(), SubCategoryActivity.class);
                bundle.putSerializable("CategoryName" , "Popular Products");
                intent.putExtras(bundle);
                startActivity(intent);
            } else if (id == R.id.menu_newproducts) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(getActivity(), SubCategoryActivity.class);
                bundle.putSerializable("CategoryName" , "New Products");
                intent.putExtras(bundle);
                startActivity(intent);
            } else if (id == R.id.menu_share) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "HireCook app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            } else if (id == R.id.menu_recieved_orders) {
                if(!user.getUserType().equalsIgnoreCase("User")&&!user.getFirmId().equals("Not_Available")){
                    startActivity(new Intent(getActivity(), RecievedOrderActivity.class));
                }
            }
            /*else if (id == R.id.menu_bottom) {
                //startActivity(new Intent(getActivity(), BottomNavigationActitivity.class));
                HomeFragment nextFrag = new HomeFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(MenuListFragment.this)
                        .replace(((ViewGroup)getView().getParent()).getId(), nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }*/
            return false;
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
                       // img_avatar.setImageURI(selectedFileUri);
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
    private void logout() {
        SharedPrefManager.getInstance(getActivity()).logout();
        startActivity(new Intent(getActivity(), UserSignInActivity.class));
        Objects.requireNonNull(getActivity()).finish();
    }
}