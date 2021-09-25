package com.test.sample.hirecooks.Adapter.Venders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Activity.Venders.VendersSubCategory.VendersSubCategoryActivity;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.APIUrl;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VendersAdapter extends RecyclerView.Adapter<VendersAdapter.ViewHolder> {
    private Context mCtx;
    private List<User> vender;

    public VendersAdapter(Context mCtx, List<User> vender) {
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
        User venders = vender.get(position);
        if(venders!=null){
            if(!venders.getImage().isEmpty()){
                holder.progress_dialog.setVisibility( View.VISIBLE );
                Picasso.with(mCtx).load( APIUrl.PROFILE_URL+venders.getImage()).into( holder.venders_image, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progress_dialog.setVisibility( View.GONE );
                    }

                    @Override
                    public void onError() {

                    }
                } );
            }
           // holder.text.setText(venders.getName());
            holder.vender_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mCtx, VendersSubCategoryActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Vender", vender.get(position));
                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    mCtx.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return vender==null?0:vender.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView venders_image;
        private LinearLayout vender_layout;
        private TextView text;
        private ProgressBar progress_dialog;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            venders_image = itemLayoutView.findViewById(R.id.venders_image);
            vender_layout = itemLayoutView.findViewById(R.id.vender_layout);
            progress_dialog = itemLayoutView.findViewById(R.id.progress_dialog);
          //  text = itemLayoutView.findViewById(R.id.text);
        }
    }
}