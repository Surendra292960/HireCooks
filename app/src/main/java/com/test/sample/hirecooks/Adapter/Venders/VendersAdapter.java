package com.test.sample.hirecooks.Adapter.Venders;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Activity.SubCategory.VendersSubCategory.VendersSubcategoryActivity;
import com.test.sample.hirecooks.Models.UsersResponse.UserResponse;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.APIUrl;

import java.util.List;

public class VendersAdapter extends RecyclerView.Adapter<VendersAdapter.ViewHolder> {
    private Context mCtx;
    private List<UserResponse> vender;

    public VendersAdapter(Context mCtx, List<UserResponse> vender) {
        this.mCtx = mCtx;
        this.vender = vender;
    }

    @Override
    public VendersAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.venders_recyclerview, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VendersAdapter.ViewHolder holder, int position) {
        UserResponse venders = vender.get(position);
        if(venders!=null){
            if(!venders.getImage().isEmpty()){
                Picasso.with(mCtx).load(APIUrl.PROFILE_URL+venders.getImage()).into(holder.venders_image);
            }
            holder.text.setText(venders.getName());
            holder.vender_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Intent intent = new Intent(mCtx, VendersSubcategoryActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Vender", vender.get(position));
                        intent.putExtras(bundle);
                        mCtx.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity) mCtx).toBundle());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return vender==null?0:vender.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView venders_image;
        private LinearLayout vender_layout;
        private TextView text;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            venders_image = itemLayoutView.findViewById(R.id.venders_image);
            vender_layout = itemLayoutView.findViewById(R.id.vender_layout);
            text = itemLayoutView.findViewById(R.id.text);
        }
    }
}