package com.test.sample.hirecooks.Activity.Cooks;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.test.sample.hirecooks.Adapter.Cooks.CooksAdapter;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.UsersResponse.UserResponse;
import com.test.sample.hirecooks.Models.UsersResponse.UsersResponse;
import com.test.sample.hirecooks.Models.cooks.Cooks;
import com.test.sample.hirecooks.Models.users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CooksActivity extends AppCompatActivity {
    private ProgressBarUtil progressBarUtil;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView profile_list_recycler_view;
    private Cooks cooks;
    private String type;
    private User user;
    private List<UserResponse> allCook,filterCook,searchList,cook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Nearby Cooks");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            cooks = (Cooks) bundle.getSerializable("Cooks");
            type= bundle.getString("type");
            if (cooks != null) {
            }
            if(type!=null){
                Objects.requireNonNull(getSupportActionBar()).setTitle(type);
            }
        }
        initViews();
    }

    private void initViews() {
            user = SharedPrefManager.getInstance( CooksActivity.this ).getUser();
            progressBarUtil = new ProgressBarUtil(this);
            mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
            profile_list_recycler_view = findViewById(R.id.profile_list_recycler_view);
            mSwipeRefreshLayout.setColorScheme(R.color.green_light, R.color.red, R.color.style_color_primary);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(false);
                            getUsers();
                        }
                    }, 3000);
                }
            });

            getUsers();
    }

    private void getUsers() {
        progressBarUtil.showProgress();
        UserApi service = ApiClient.getClient().create(UserApi.class);
        Call<UsersResponse> call = service.getUsers();
        call.enqueue(new Callback<UsersResponse>() {
            @SuppressLint("ShowToast")
            @Override
            public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    if(response.body().getUsersResponses()!=null){
                        progressBarUtil.hideProgress();
                        setCookData(response.body().getUsersResponses());
                    }
                }
            }

            @SuppressLint("ShowToast")
            @Override
            public void onFailure(@NonNull Call<UsersResponse> call, @NonNull Throwable t) {
                progressBarUtil.hideProgress();
                Toast.makeText(getApplicationContext(), R.string.error + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setCookData(List<UserResponse> userList) {
        cook = new ArrayList<>();
        filterCook = new ArrayList<>();
        allCook = new ArrayList<>();
        searchList = new ArrayList<>();
        mSwipeRefreshLayout.onFinishTemporaryDetach();
        if (userList!= null) {
            for (UserResponse userResponse : userList) {
                if (userResponse.getUserType().equalsIgnoreCase("Cook")) {
                    if (type != null && type.equalsIgnoreCase( "AllCooks" )) {
                        allCook.add( userResponse );
                        searchList.add( userResponse );
                    } else {
                        for (Map map : Constants.NEARBY_COOKS) {
                            if (userResponse.getId().equals(map.getUserId())) {
                                filterCook.add( userResponse );
                                searchList.add( userResponse );
                            }
                        }
                    }
                }
            }
            setAdapter(type,allCook,filterCook,cook);
        }
    }

    private void setAdapter(String type, List<UserResponse> allCook, List<UserResponse> filterCook, List<UserResponse> cook) {
        if(type!=null&&allCook!=null&&allCook.size()!=0){
            CooksAdapter adapter = new CooksAdapter(CooksActivity.this, allCook);
            profile_list_recycler_view.setAdapter(adapter);
        }else if(type==null&&filterCook!=null&&filterCook.size()!=0){
            CooksAdapter adapter = new CooksAdapter(CooksActivity.this, filterCook);
            profile_list_recycler_view.setAdapter(adapter);
        }else if(type==null&&cook!=null&&cook.size()!=0){
            CooksAdapter adapter = new CooksAdapter(CooksActivity.this, filterCook);
            profile_list_recycler_view.setAdapter(adapter);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.getItem(0);
        SearchManager searchManager = (SearchManager) getSystemService( Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setFocusable(true);
        searchItem.expandActionView();

        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(searchList.size()!=0){
                    startSearch( query );
                }else{
                    showalertbox("No Match found");
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(searchList.size()!=0){
                    startSearch( newText );
                }
                return false;
            }
        } );
        return super.onCreateOptionsMenu(menu);
    }

    public void showalertbox(String string) {
        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder( this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.show_alert_message,null);
        TextView ask = view.findViewById( R.id.ask );
        TextView textView = view.findViewById( R.id.text );
        ask.setText( string );
        textView.setText( "Alert !" );
        AppCompatTextView cancelBtn = view.findViewById(R.id.exit_app_btn);
        dialogBuilder.setView(view);
        final android.app.AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        cancelBtn.setOnClickListener( v -> {
            try {
                dialog.dismiss();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        } );
    }

    private void startSearch(CharSequence text) {
        List<UserResponse> filterList = new ArrayList<>();
        try {
            if (searchList != null && searchList.size() != 0) {
                for (int i = 0; i < searchList.size(); i++) {
                    String cookName = "";

                    if (searchList.get(i).getName() != null) {
                        cookName = searchList.get(i).getName();
                    }

                    if (cookName.toLowerCase().contains(String.valueOf(text).toLowerCase())) {
                        filterList.add(searchList.get(i));
                    }
                }

                if (filterList.size() != 0 && filterList != null) {
                    CooksAdapter adapter = new CooksAdapter(CooksActivity.this, filterList);
                    profile_list_recycler_view.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    //profile_list_recycler_view.setVisibility( View.GONE );
                    this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
