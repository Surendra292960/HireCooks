/*
package com.test.sample.hirecooks.Adapter.Checkout;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Models.Cart.Cart;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.LocalStorage;

import java.util.List;

public class CheckoutCartAdapter extends RecyclerView.Adapter<CheckoutCartAdapter.MyViewHolder> {
    List<Cart> cartList;
    Context mCtx;
    int SubTotal = 0, sellRate = 0, Quantity = 0;
    LocalStorage localStorage;
    Gson gson;

    public CheckoutCartAdapter(List<Cart> cartList, Context mCtx) {
        this.cartList = cartList;
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_cart_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Cart cart = cartList.get(position);
        localStorage = new LocalStorage(mCtx);
        gson = new Gson();
        if(cart!=null){
            if(cart.getWeight()!=null) {
                holder.attribute.setText("" + cart.getWeight());
            }else{
                holder.attribute.setText(cart.getDesc());
            }
            holder.product_name.setText(cart.getName());
            sellRate = cart.getSellRate();
            Quantity = cart.getItemQuantity();
            SubTotal = (sellRate*Quantity);
            holder.total_amount.setText("\u20B9 "+SubTotal);
           // holder.product_sellRate.setText("\u20B9: "+sellRate);
            holder.imageView.setAnimation(AnimationUtils.loadAnimation(mCtx, R.anim.fade_transition_animation));
            Picasso.with(mCtx).load(cart.getLink()).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return cartList==null?0:cartList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView product_name,product_sellRate,attribute,total_amount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.product_image);
            product_name = itemView.findViewById(R.id.product_name);
            attribute = itemView.findViewById(R.id.product_attribute);
            total_amount = itemView.findViewById(R.id.total_amount);
        }
    }
}
*/
