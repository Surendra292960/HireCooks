package com.test.sample.hirecooks.Adapter.Users;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.test.sample.hirecooks.Activity.Chat.ChatActivity;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.APIUrl;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.ViewModel.ViewModel;
import com.test.sample.hirecooks.databinding.RecyclerviewProfilesBinding;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private final ViewModel viewModel;
    private List<User> users;
    private Context mCtx;
    private String type;

    public UserAdapter(Context mCtx, List<User> users ,String type) {
        this.users = users;
        this.mCtx = mCtx;
        this.type = type;
        viewModel = new ViewModelProvider((ViewModelStoreOwner) mCtx).get(ViewModel.class);
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
        holder.profile_layout.setOnClickListener(v -> {
           if(type.equalsIgnoreCase("Collaboration")){
               Intent intent = new Intent( mCtx, ChatActivity.class );
               Bundle bundle = new Bundle(  );
               bundle.putSerializable( "User",firm_user );
               intent.putExtras( bundle );
               mCtx.startActivity( intent );
           }else if(type.equalsIgnoreCase("Manage Employee")){
               viewModel.onBtnClick(mCtx,firm_user,"User Profile !", "Delete", "Update");
           }else if(type.equalsIgnoreCase("Report")){
               viewModel.onBtnClick(mCtx,firm_user,"User Profile !", "Trace", "Report");
           }
        });
    }

    /*private void deleteProfile(int id) {
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
    }*/

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