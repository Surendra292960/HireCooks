package com.test.sample.hirecooks.Activity.Users.FirmUsers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.bumptech.glide.Glide;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.Users.Example;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.APIUrl;
import com.test.sample.hirecooks.Utils.OnButtonClickListener;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirmUserActivity extends AppCompatActivity {
    private RecyclerView usersRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FirmUserAdapter adapter;
    private ProgressBarUtil progressBarUtil;
    private List<User> filtereduserList;
    private View appRoot;
    private OnButtonClickListener listener;
    private User user;
    private FloatingActionButton add_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        progressBarUtil = new ProgressBarUtil(this);
        appRoot = findViewById(R.id.appRoot);
        user = SharedPrefManager.getInstance( this ).getUser();
        usersRecyclerView = findViewById(R.id.users_list_recycler_view);
        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        add_user = findViewById(R.id.add_user);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.style_color_accent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if(user!=null&&user.getFirmId()!=null){
                            getUsers( user.getFirmId() );
                        }
                    }
                }, 3000);
            }
        });

        final LinearLayoutManager layoutParams = new LinearLayoutManager(this);
        usersRecyclerView.setLayoutManager(layoutParams);
        adapter = new FirmUserAdapter(this,filtereduserList);
        usersRecyclerView.setAdapter(adapter);
        usersRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition = (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                mSwipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        if(user!=null&&user.getFirmId()!=null&&user.getUserType().equalsIgnoreCase( "Admin" )||user.getUserType().equalsIgnoreCase( "Manager" )){
            add_user.setVisibility( View.VISIBLE );
            add_user.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity( new Intent( FirmUserActivity.this, FirmUserSignupActivity.class )  .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            } );
        }

            if(user!=null&&user.getFirmId()!=null){
                getUsers( user.getFirmId() );
            }
    }

    private void getUsers(String firmId) {
        progressBarUtil.showProgress();
        UserApi service = ApiClient.getClient().create(UserApi.class);
        Call<List<Example>> call = service.getUserByFirmId(firmId);
        call.enqueue(new Callback<List<Example>>() {
            @SuppressLint("ShowToast")
            @Override
            public void onResponse(@NonNull Call<List<Example>> call, @NonNull Response<List<Example>> response) {
                if (response.code() == 200) {
                    filtereduserList = new ArrayList<>(  );
                    List<User> usersList = new ArrayList<>(  );
                    progressBarUtil.hideProgress();
                    mSwipeRefreshLayout.onFinishTemporaryDetach();
                  for(Example example:response.body()){
                      if (!example.getError()){
                          for(User user:example.getUsers()){
                              if(user.getUserType().equalsIgnoreCase( "Employee" )||user.getUserType().equalsIgnoreCase( "Rider" )){
                                  usersList.add( user );
                                  Set<User> newList = new LinkedHashSet<>(usersList);
                                  filtereduserList = new ArrayList<>(newList);
                              }
                          }
                          if(filtereduserList!=null&&filtereduserList.size()!=0){
                              adapter = new FirmUserAdapter( FirmUserActivity.this, filtereduserList);
                              usersRecyclerView.setAdapter(adapter);
                          }
                      }
                  }
                }
            }

            @SuppressLint("ShowToast")
            @Override
            public void onFailure(@NonNull Call<List<Example>> call, @NonNull Throwable t) {
                progressBarUtil.hideProgress();
                System.out.println( "Suree : "+t.getMessage() );
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchbar, menu);
        MenuItem searchViewItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchView.setQueryHint("Search User");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUserByName(query);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchUserByName(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void searchUserByName(String s) {
        List<User>  filteredList = new ArrayList<>();
        try{
            for(int i=0;i<filtereduserList.size();i++) {
                String userName = "";

                if(filtereduserList.get (i).getName()!=null){
                    userName= filtereduserList.get (i).getName();
                }

                if(userName.toLowerCase().contains(s.toLowerCase())) {
                    filteredList.add(filtereduserList.get(i));
                }
            }

            adapter = new FirmUserAdapter ( FirmUserActivity.this, filteredList);
            usersRecyclerView.setAdapter (adapter);
            adapter.notifyDataSetChanged();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(user!=null&&user.getFirmId()!=null){
            getUsers( user.getFirmId() );
        }
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



    public class FirmUserAdapter extends RecyclerView.Adapter<FirmUserAdapter.ViewHolder> {
        private List<User> users;
        private Context mCtx;

        public FirmUserAdapter(Context mCtx, List<User> users) {
            this.users = users;
            this.mCtx = mCtx;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_profiles, parent, false);
            return new ViewHolder(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            final User firm_user = users.get(position);
            if(firm_user.getId()== SharedPrefManager.getInstance( mCtx ).getUser().getId()){
                holder.profile_layout.removeAllViews();
                return;
            }
            if(firm_user.getImage()!=null) {
                if (firm_user.getImage().contains("https://")) {
                    Glide.with(mCtx).load(firm_user.getImage()).into(holder.profile_image);
                } else if (firm_user.getImage().contains(" ")) {

                } else {
                    Glide.with(mCtx).load( APIUrl.PROFILE_URL + firm_user.getImage()).into(holder.profile_image);
                }
            }
            holder.textViewName.setText(firm_user.getName());
            holder.textViewEmail.setVisibility( View.GONE );
            if(firm_user.getStatus().equalsIgnoreCase( "1" )){
                holder.status.setVisibility( View.VISIBLE );
                holder.status.setText( "Online" );
                holder.status.setBackgroundColor( android.graphics.Color.parseColor("#567845"));
                holder.status.setTextColor( android.graphics.Color.parseColor("#ffffff"));
            }else{
                holder.status.setVisibility( View.VISIBLE );
                holder.status.setText( "Offline" );
                holder.status.setBackgroundColor( android.graphics.Color.parseColor( "#ff0000" ));
                holder.status.setTextColor( android.graphics.Color.parseColor("#ffffff"));
            }
            holder.profile_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showalertbox(firm_user);
                }
            });
        }

        private void showalertbox(User firm_user) {
            final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder( FirmUserActivity.this);
            LayoutInflater inflater = FirmUserActivity.this.getLayoutInflater();
            View view = inflater.inflate(R.layout.edit_subcategory_alert,null);
            TextView text = view.findViewById(R.id.text);
            AppCompatTextView deletBtn = view.findViewById(R.id.no_btn);
            deletBtn.setText( "Delete" );
            text.setText( "User Profile !" );
            AppCompatTextView editBtn = view.findViewById(R.id.edit_btn);
            dialogBuilder.setView(view);
            final android.app.AlertDialog dialog = dialogBuilder.create();
            dialog.show();
            deletBtn.setOnClickListener( v -> {
                try {
                    dialog.dismiss();
                    deleteProfile(firm_user.getId());
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            } );
            editBtn.setOnClickListener( v -> {
                try {
                    dialog.dismiss();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(mCtx, UpdateFirmUser.class);
                        bundle.putSerializable("User",firm_user);
                        intent.putExtras(bundle);
                        intent .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        mCtx.startActivity(intent);
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            } );
        }
        private void deleteProfile(int id) {
            UserApi service = ApiClient.getClient().create(UserApi.class);
            Call<List<String>> call = service.deleteProfile(id);
            call.enqueue(new Callback<List<String>>() {
                @SuppressLint("ShowToast")
                @Override
                public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                    if (response.code() == 200) {
                        Toast.makeText(mCtx, "Deleted Success fully", Toast.LENGTH_SHORT ).show();
                        if(user!=null&&user.getFirmId()!=null){
                            getUsers( user.getFirmId() );
                        }
                    }
                }

                @SuppressLint("ShowToast")
                @Override
                public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                    Log.d("TAG",t.getMessage());
                }
            });
        }
        @Override
        public int getItemCount() {
            return users == null ? 0 : users.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView textViewName,textViewEmail,status;
            private ImageView profile_image;
            private CardView profile_layout;

            ViewHolder(View itemView) {
                super(itemView);
                textViewName = itemView.findViewById(R.id.textViewName);
                textViewEmail = itemView.findViewById(R.id.textViewEmail);
                status = itemView.findViewById(R.id.status);
                profile_image = itemView.findViewById(R.id.profile_image);
                profile_layout = itemView.findViewById(R.id.profile_layout);
            }
        }
    }
}
