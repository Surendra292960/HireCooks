package com.test.sample.hirecooks.Adapter.Users;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.APIUrl;
import com.test.sample.hirecooks.Utils.SharedPrefManager;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<User> users;
    private Context mCtx;

    public UserAdapter(Context mCtx, List<User> users) {
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
                Glide.with(mCtx).load(APIUrl.PROFILE_URL + user.getImage()).into(holder.profile_image);
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