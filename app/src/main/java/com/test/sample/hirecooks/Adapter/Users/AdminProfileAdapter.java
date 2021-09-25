package com.test.sample.hirecooks.Adapter.Users;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Activity.ManageAccount.ManageProducts.ProductCategoryList;
import com.test.sample.hirecooks.Activity.ManageAddress.SecondryAddressActivity;
import com.test.sample.hirecooks.Activity.Orders.RecievedOrderActivity;
import com.test.sample.hirecooks.Activity.Users.FirmUsers.AdminChatActivity;
import com.test.sample.hirecooks.Activity.Users.FirmUsers.AllFirmUsersReportActivity;
import com.test.sample.hirecooks.Activity.Users.FirmUsers.FirmUserActivity;
import com.test.sample.hirecooks.Activity.Users.FirmUsers.FirmUserList;
import com.test.sample.hirecooks.Models.Offer.Offer;
import com.test.sample.hirecooks.R;

import java.util.List;

public class AdminProfileAdapter extends RecyclerView.Adapter<AdminProfileAdapter.ViewHolder> {
    private Context mCtx;
    private List<Offer> offers;

    public AdminProfileAdapter(Context mCtx, List<Offer> offers) {
        this.mCtx = mCtx;
        this.offers = offers;
    }

    @Override
    public AdminProfileAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate( R.layout.profile_admin_dash, null);
        AdminProfileAdapter.ViewHolder viewHolder = new AdminProfileAdapter.ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AdminProfileAdapter.ViewHolder holder, int position) {
        Offer offer = offers.get(position);
        if(offer!=null){
            holder.card_name.setText(offer.getName());
            holder.card_detail.setText(offer.getLink());
            holder.progress_dialog.setVisibility( View.VISIBLE );
            Picasso.with(mCtx).load(offer.getRoundIcon()).into( holder.card_image, new Callback() {
                @Override
                public void onSuccess() {
                    holder.progress_dialog.setVisibility( View.GONE );
                }

                @Override
                public void onError() {

                }
            } );
            holder.card_view.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(offer.getName().equalsIgnoreCase( "Account" )){
                        ((Activity)mCtx).startActivity( new Intent(mCtx, ProductCategoryList.class ) );
                    }  if(offer.getName().equalsIgnoreCase( "Employee" )){
                        ((Activity)mCtx).startActivity( new Intent(mCtx, FirmUserActivity.class ) );
                    }  if(offer.getName().equalsIgnoreCase( "Report" )){
                        ((Activity)mCtx).startActivity( new Intent(mCtx, FirmUserList.class  ) );
                    }  if(offer.getName().equalsIgnoreCase( "All Employees" )){
                        ((Activity)mCtx).startActivity( new Intent(mCtx, AllFirmUsersReportActivity.class  ) );
                    }  if(offer.getName().equalsIgnoreCase( "Recieved Order" )){
                        ((Activity)mCtx).startActivity( new Intent(mCtx, RecievedOrderActivity.class ) );
                    }  if(offer.getName().equalsIgnoreCase( "Manage Your Address" )){
                        ((Activity)mCtx).startActivity( new Intent(mCtx, SecondryAddressActivity.class ) );
                    }  if(offer.getName().equalsIgnoreCase( "Collaboration" )){
                        ((Activity)mCtx).startActivity( new Intent(mCtx, AdminChatActivity.class ) );
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
        public TextView card_name,card_detail;
        private ImageView card_image;
        private CardView card_view;
        private ProgressBar progress_dialog;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            card_view = itemLayoutView.findViewById(R.id.card_view);
            card_image = itemLayoutView.findViewById(R.id.card_image);
            card_name = itemLayoutView.findViewById(R.id.card_name);
            card_detail = itemLayoutView.findViewById(R.id.card_detail);
            progress_dialog = itemLayoutView.findViewById(R.id.progress_dialog);
        }
    }
}