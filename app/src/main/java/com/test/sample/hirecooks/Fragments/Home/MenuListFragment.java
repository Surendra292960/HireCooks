package com.test.sample.hirecooks.Fragments.Home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.mxn.soul.flowingdrawer_core.BuildConfig;
import com.test.sample.hirecooks.Activity.Favourite.FavouriteActivity;
import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.Activity.Orders.RecievedOrderActivity;
import com.test.sample.hirecooks.Activity.SubCategory.SubCategoryActivity.SubCategoryActivity;
import com.test.sample.hirecooks.Activity.Users.UserSignInActivity;
import com.test.sample.hirecooks.Models.users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.TestActivity;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.Utils.UploadCallBack;

import java.util.Objects;

public class MenuListFragment extends Fragment implements UploadCallBack {
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
        userName.setText("Welcome:  "+user.getName());
        userPhone.setText("Phone:  "+user.getPhone());
        NavigationView vNavigation = view.findViewById(R.id.vNavigation);
        vNavigation.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.menu_home) {
                startActivity( new Intent(getActivity(), MainActivity.class));
            }if (id == R.id.menu_message) {
                startActivity( new Intent(getActivity(), TestActivity.class));
            } else if (id == R.id.logout) {
                logout();
            } else if (id == R.id.menu_favourite) {
                startActivity( new Intent(getActivity(), FavouriteActivity.class));
            } else if (id == R.id.menu_offers) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(getActivity(), SubCategoryActivity.class);
                bundle.putSerializable("CategoryName" , "Offers");
                intent.putExtras(bundle);
                startActivity(intent);
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
                if(!user.getUserType().equalsIgnoreCase("User")&&!user.getUserType().equalsIgnoreCase("Cook")&&!user.getFirmId().equals("Not_Available")){
                    startActivity(new Intent(getActivity(), RecievedOrderActivity.class));
                }
            }
            return false;
        });

        return view;
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