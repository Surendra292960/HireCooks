package com.test.sample.hirecooks.Adapter.Cooks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.test.sample.hirecooks.Activity.Cooks.CooksDetailsActivity;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.APIUrl;

import java.util.List;

public class CooksAdapter extends RecyclerView.Adapter<CooksAdapter.ViewHolder> {
    private List<User> users;
    private Context mCtx;

    public CooksAdapter(Context mCtx, List<User> users) {
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final User user = users.get(position);
        if(user.getImage()!=null) {
            if (user.getImage().contains("https://")) {
                Glide.with(mCtx).load(user.getImage()).into(holder.profile_image);
            } else if (user.getImage().contains(" ")) {

            } else {
                Glide.with(mCtx).load(APIUrl.PROFILE_URL + user.getImage()).into(holder.profile_image);
            }
        }
        holder.textViewName.setText(user.getName());
        holder.textViewEmail.setText(user.getEmail());
        holder.profile_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(mCtx, CooksDetailsActivity.class);
                bundle.putSerializable("Cooks",users.get(position));
                intent.putExtras(bundle);
                mCtx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users == null ? 0 : users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName,textViewEmail;
        private ImageView profile_image;
        private CardView profile_layout;

        ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
            profile_image = itemView.findViewById(R.id.profile_image);
            profile_layout = itemView.findViewById(R.id.profile_layout);
        }
    }
}