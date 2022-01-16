package com.test.sample.hirecooks.Adapter.Users;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.test.sample.hirecooks.Activity.ManageAccount.ManageProducts.EditMenuActivity;
import com.test.sample.hirecooks.Activity.ManageAddress.SecondryAddressActivity;
import com.test.sample.hirecooks.Activity.Orders.RecievedOrderActivity;
import com.test.sample.hirecooks.Activity.Users.FirmUsers.UserActivity;
import com.test.sample.hirecooks.Activity.Users.FirmUsers.AllFirmUsersReportActivity;
import com.test.sample.hirecooks.Models.Offer.Offer;
import com.test.sample.hirecooks.databinding.ProfileAdminDashBinding;

import java.util.List;

public class ProfileAdminDashAdapter extends RecyclerView.Adapter<ProfileAdminDashAdapter.ViewHolder> {
    private Context mCtx;
    private List<Offer> offers;

    public ProfileAdminDashAdapter(Context mCtx, List<Offer> offers) {
        this.mCtx = mCtx;
        this.offers = offers;
    }

    @Override
    public ProfileAdminDashAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ProfileAdminDashAdapter.ViewHolder(ProfileAdminDashBinding.inflate(LayoutInflater.from(mCtx)));

    }

    @Override
    public void onBindViewHolder(ProfileAdminDashAdapter.ViewHolder holder, int position) {
        Offer offer = offers.get(position);
        if(offer!=null){
            holder.binding.cardName.setText(offer.getName());
            holder.binding.cardDetail.setText(offer.getLink());
            holder.binding.progressDialog.setVisibility( View.GONE );
            Glide.with(mCtx).load(offer.getRoundIcon()).into( holder.binding.cardImage);
            holder.binding.cardView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(offer.getName().equalsIgnoreCase( "Account" )){
                        ((Activity)mCtx).startActivity( new Intent(mCtx, EditMenuActivity.class ) );
                    }  if(offer.getName().equalsIgnoreCase( "Manage Employee" )){
                        Intent intent = new Intent(mCtx, UserActivity.class);
                        intent.putExtra("Type", "ManageEmployee");
                        ((Activity)mCtx).startActivity(intent);
                    }  if(offer.getName().equalsIgnoreCase( "Report" )){
                        Intent intent = new Intent(mCtx, UserActivity.class);
                        intent.putExtra("Type", "Report");
                        ((Activity)mCtx).startActivity(intent);
                    }  if(offer.getName().equalsIgnoreCase( "All Employees" )){
                        ((Activity)mCtx).startActivity( new Intent(mCtx, AllFirmUsersReportActivity.class  ) );
                    }  if(offer.getName().equalsIgnoreCase( "Recieved Order" )){
                        ((Activity)mCtx).startActivity( new Intent(mCtx, RecievedOrderActivity.class ) );
                    }  if(offer.getName().equalsIgnoreCase( "Manage Your Address" )){
                        ((Activity)mCtx).startActivity( new Intent(mCtx, SecondryAddressActivity.class ) );
                    }  if(offer.getName().equalsIgnoreCase( "Collaboration" )){
                        Intent intent = new Intent(mCtx, UserActivity.class);
                        intent.putExtra("Type", "Collaboration");
                        ((Activity)mCtx).startActivity(intent);
                    }  if(offer.getName().equalsIgnoreCase( "Add" )){
                        //((Activity)mCtx).startActivity( new Intent(mCtx, ProductCategoryList.class ) );
                    }
                }
            } );
        }
    }

    @Override
    public int getItemCount() {
        return offers==null?0:offers.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
     ProfileAdminDashBinding binding;

        public ViewHolder(@NonNull ProfileAdminDashBinding itemLayoutView) {
            super(itemLayoutView.getRoot());
            binding = itemLayoutView;
        }
    }
}