package com.test.sample.hirecooks.Adapter.Orders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Activity.Orders.RecievedOrderDetails;
import com.test.sample.hirecooks.Models.Order.Order;
import com.test.sample.hirecooks.Models.users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.SharedPrefManager;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder> {
    private Context mCtx;
    private List<Order> orderList;
    User user = SharedPrefManager.getInstance(mCtx).getUser();
    private List<String> listStatus;

    public OrdersAdapter(Context mCtx, List<Order> orderList) {
        this.mCtx = mCtx;
        this.orderList = orderList;
    }

    @Override
    public OrdersAdapter.OrdersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_order, parent, false);
        return new OrdersAdapter.OrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrdersAdapter.OrdersViewHolder holder, int position) {
        final Order order = orderList.get(position);
        if(order!=null) {
            holder.itemName.setText(order.getProductName());
            holder.itemTotalAmount.setText("\u20B9 " + order.getProductTotalAmount());
            //holder.item_count.setText("Quantity: " + order.getProductQuantity());
            holder.item_with_quantity.setText(order.getProductQuantity()+" x "+order.getProductName());
            holder.order_id.setText("order id : # "+order.getOrderId());
            holder.order_date_time.setText(order.getOrderDateTime());
            holder.order_address.setText(order.getOrderSubAddress().replaceAll("null", ""));
            if (order.getOrderStatus().equalsIgnoreCase("Cancelled")){
                holder.item_status.setTextColor(Color.RED);
                holder.item_status.setText(order.getOrderStatus());
            }
            if (order.getPaymentMethod().equalsIgnoreCase("COD")){
                holder.payment_method.setTextColor(Color.RED);
                holder.payment_method.setText("Payment COD");
            }else{
                holder.payment_method.setText("Paid");
            }
            if(order.getOrderConfirm().equalsIgnoreCase("Confirmed")){
                holder.confim_order.setVisibility(View.VISIBLE);
                holder.confim_order.setText(order.getOrderConfirm());
            }else{
                holder.confim_order.setVisibility(View.GONE);

            }
            holder.item_status.setText(order.getOrderStatus());
            holder.itemImage.setAnimation(AnimationUtils.loadAnimation(mCtx,R.anim.fade_transition_animation));
            holder.cardlist_item.setAnimation(AnimationUtils.loadAnimation(mCtx,R.anim.fade_scale_animation));
            Picasso.with(mCtx).load(order.getProductImage()).into(holder.itemImage);

            holder.cardlist_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!user.getUserType().equalsIgnoreCase("User")) {
                        Intent intent = new Intent(mCtx, RecievedOrderDetails.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Orders", order);
                        intent.putExtras(bundle);
                        mCtx.startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return orderList==null?0:orderList.size();
    }

    class OrdersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView itemName, itemTotalAmount, order_id, order_date_time,order_address, item_status,item_with_quantity,confim_order,payment_method;
        ImageView itemImage;
        CardView cardlist_item;

        public OrdersViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemTotalAmount = itemView.findViewById(R.id.order_totalAmount);
            itemImage = itemView.findViewById(R.id.product_thumb);
           // item_count = itemView.findViewById(R.id.item_quantity);
            order_id = itemView.findViewById(R.id.order_id);
            order_date_time = itemView.findViewById(R.id.order_date_time);
            order_address = itemView.findViewById(R.id.order_address);
            cardlist_item = itemView.findViewById(R.id.cardlist_item);
            item_status = itemView.findViewById(R.id.item_status);
            confim_order = itemView.findViewById(R.id.confim_order);
            item_with_quantity = itemView.findViewById(R.id.item_with_quantity);
            payment_method = itemView.findViewById(R.id.payment_method);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            System.out.println("click: " + getPosition());
        }
    }
}
