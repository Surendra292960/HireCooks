package com.test.sample.hirecooks.Activity.Users;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.test.sample.hirecooks.Activity.Users.FirmUsers.FirmUserSignupActivity;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.Users.UserResponse;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.APIUrl;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.facebook.drawee.backends.pipeline.Fresco;

public class UsersActivity extends AppCompatActivity {
    private RecyclerView usersRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private UserAdapter adapter;
    private ProgressBarUtil progressBarUtil;
    private List<User> usersList;
    private View appRoot;
    private FloatingActionButton add_user;
    private User user;
    private ArrayList<User> filterusersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        progressBarUtil = new ProgressBarUtil(this);
        //Fresco.initialize(this);
        initViews();
        getUsers();

    }

    private void initViews() {
        user = SharedPrefManager.getInstance( this ).getUser();
        appRoot = findViewById(R.id.appRoot);
        add_user = findViewById(R.id.add_user);
        add_user.setVisibility( View.VISIBLE );
        usersRecyclerView = findViewById(R.id.users_list_recycler_view);
        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.style_color_accent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed( new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        getUsers();
                    }
                }, 3000);
            }
        });

        final LinearLayoutManager layoutParams = new LinearLayoutManager(this);
        usersRecyclerView.setLayoutManager(layoutParams);
        adapter = new UserAdapter(this,usersList);
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

        add_user.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( UsersActivity.this, FirmUserSignupActivity.class ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        } );
    }

    private void getUsers() {
        progressBarUtil.showProgress();
        UserApi service = ApiClient.getClient().create(UserApi.class);
        Call<List<UserResponse>> call = service.getUsers();
        call.enqueue(new Callback<List<UserResponse>>() {
            @SuppressLint("ShowToast")
            @Override
            public void onResponse(@NonNull Call<List<UserResponse>> call, @NonNull Response<List<UserResponse>> response) {
                if (response.code() == 200) {
                    progressBarUtil.hideProgress();
                    mSwipeRefreshLayout.onFinishTemporaryDetach();
                    usersList = new ArrayList<>(  );
                   filterusersList = new ArrayList<>(  );
                  for(UserResponse example:response.body()){
                   if(!example.getError()){
                      for(User users:example.getUsers()){
                          if(users.getUserType().equalsIgnoreCase( "Admin" )||users.getUserType().equalsIgnoreCase( "Manager" )){
                              usersList.add( users );
                              Set<User> set = new HashSet<>( usersList );
                              filterusersList = new ArrayList<>(set);
                          }
                      }
                       if(filterusersList!=null&&filterusersList.size()!=0){
                           adapter = new UserAdapter(UsersActivity.this, filterusersList);
                           usersRecyclerView.setAdapter(adapter);
                       }
                   }
                  }
                }
            }

            @SuppressLint("ShowToast")
            @Override
            public void onFailure(@NonNull Call<List<UserResponse>> call, @NonNull Throwable t) {
                progressBarUtil.hideProgress();
                Toast.makeText(getApplicationContext(), R.string.error + t.getMessage(), Toast.LENGTH_LONG).show();
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
                for(int i=0;i<usersList.size();i++) {
                    String userName = "";

                    if(usersList.get (i).getName()!=null){
                        userName= usersList.get (i).getName();
                    }

                    if(userName.toLowerCase().contains(s.toLowerCase())) {
                        filteredList.add(usersList.get(i));
                    }
                }

                adapter = new UserAdapter (UsersActivity.this, filteredList);
                usersRecyclerView.setAdapter (adapter);
                adapter.notifyDataSetChanged();

            }catch (Exception e){
                e.printStackTrace();
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
    public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
        private List<User> users;
        private Context mCtx;

        public UserAdapter(Context mCtx, List<User> users) {
            this.users = users;
            this.mCtx = mCtx;
        }

        @NonNull
        @Override
        public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_profiles, parent, false);
            return new UserAdapter.ViewHolder(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull final UserAdapter.ViewHolder holder, final int position) {
            final User user = users.get(position);
            if(user.getId()== SharedPrefManager.getInstance( mCtx ).getUser().getId()){
                holder.profile_layout.removeAllViews();
                return;
            }
            if(user.getImage()!=null) {
                if (user.getImage().contains("https://")) {
                    Glide.with(mCtx).load(user.getImage()).into(holder.profile_image);
                } else if (user.getImage().contains(" ")) {

                } else {
                    Glide.with(mCtx).load( APIUrl.PROFILE_URL + user.getImage()).into(holder.profile_image);
                }
            }
            holder.textViewName.setText(user.getName());
            holder.textViewEmail.setVisibility( View.GONE );
            if(user.getStatus().equalsIgnoreCase( "1" )){
                holder.status.setVisibility( View.VISIBLE );
                holder.status.setText( "Active" );
                holder.status.setBackgroundColor( android.graphics.Color.parseColor("#567845"));
                holder.status.setTextColor( android.graphics.Color.parseColor("#ffffff"));
            }else{
                holder.status.setVisibility( View.VISIBLE );
                holder.status.setText( "SignOut" );
                holder.status.setBackgroundColor( android.graphics.Color.parseColor( "#ff0000" ));
                holder.status.setTextColor( android.graphics.Color.parseColor("#ffffff"));
            }
            holder.profile_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showalertbox(user);
                }
            });
        }

        private void showalertbox(User user) {
            final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder( UsersActivity.this);
            LayoutInflater inflater = UsersActivity.this.getLayoutInflater();
            View view = inflater.inflate(R.layout.edit_subcategory_alert,null);
            AppCompatTextView noBtn = view.findViewById(R.id.no_btn);
            AppCompatTextView editBtn = view.findViewById(R.id.edit_btn);
            editBtn.setText( "Delete" );
            dialogBuilder.setView(view);
            final android.app.AlertDialog dialog = dialogBuilder.create();
            dialog.show();
            noBtn.setOnClickListener( v -> {
                try {
                    dialog.dismiss();
                
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            } );
            editBtn.setOnClickListener( v -> {
                dialog.dismiss();
                deleteUser(user);
            } );
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

    private void deleteUser(User user) {
        UserApi mService = ApiClient.getClient().create( UserApi.class );
        Call<List<UserResponse>> call = mService.deleteUser( user.getId() );
        call.enqueue( new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                if(response.code()==200){
                    for(UserResponse example:response.body()){
                        Toast.makeText( UsersActivity.this, example.getMessage(), Toast.LENGTH_SHORT ).show();
                        if(!example.getError()){
                           getUsers();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
            System.out.println( "Suree : "+t.getMessage() );
            }
        } );
    }
}
