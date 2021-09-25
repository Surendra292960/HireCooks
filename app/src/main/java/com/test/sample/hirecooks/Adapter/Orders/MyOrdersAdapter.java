package com.test.sample.hirecooks.Adapter.Orders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.test.sample.hirecooks.Activity.ManageAddress.SecondryAddressActivity;
import com.test.sample.hirecooks.Models.NewOrder.OrdersTable;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.SharedPrefManager;

import java.util.List;

import static com.facebook.accountkit.internal.AccountKitController.getApplicationContext;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.OrdersViewHolder> {
    private Context mCtx;
    private List<OrdersTable> orderList;
    User user = SharedPrefManager.getInstance(mCtx).getUser();

    public MyOrdersAdapter(Context mCtx, List<OrdersTable> orderList) {
        this.mCtx = mCtx;
        this.orderList = orderList;
    }

    @Override
    public MyOrdersAdapter.OrdersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recieved_orders_cardview, parent, false);
        return new MyOrdersAdapter.OrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyOrdersAdapter.OrdersViewHolder holder, int position) {
        final OrdersTable ordersTable = orderList.get(position);
        if(ordersTable!=null) {
            holder.user_name.setText("Name  :  "+ user.getName());
            holder.payment_method.setText("Payment  :  "+ordersTable.getPayment_type());
            holder.shipping_charge.setText("Shipping Charge  :  \u20B9 "+ordersTable.getShipping_price());
            holder.phone_number.setText("Phone  :  "+user.getPhone());
            holder.itemTotalAmount.setText("Total Amount  :   \u20B9 " + ordersTable.getTotal_amount());
            holder.order_id.setText("# " + ordersTable.getOrder_id());
            holder.order_date_time.setText("Order On  :  "+ordersTable.getOrder_date_time());
            holder.order_address.setText("Address  :  "+ordersTable.getOrder_address());
            Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
            holder.itemImage.setAnimation(AnimationUtils.loadAnimation(mCtx, R.anim.fade_transition_animation));
            holder.cardlist_item.setAnimation(AnimationUtils.loadAnimation(mCtx, R.anim.fade_scale_animation));
            holder.update_order_layout.setVisibility( View.GONE );
            if(ordersTable.getOrder_status().equalsIgnoreCase( "Cancelled" )){
                holder.item_status.setTextColor( Color.RED );
                holder.item_status.setText(ordersTable.getOrder_status());
            }else if(ordersTable.getOrder_status().equalsIgnoreCase( "Pending" )){
                holder.image.startAnimation(animation1);
                holder.item_status.setText(ordersTable.getOrder_status());
            }else if(ordersTable.getOrder_status().equalsIgnoreCase( "Delivered" )){
                holder.image.clearAnimation();
                holder.item_status.setText(ordersTable.getOrder_status());
            }else if(ordersTable.getOrder_status().equalsIgnoreCase( "Prepairing" )){
                holder.lottie_prepaire.setVisibility( View.VISIBLE );
                holder.item_status.setText(ordersTable.getOrder_status());
            }else if(ordersTable.getOrder_status().equalsIgnoreCase( "On The Way" )){
                holder.lottie_delivery.setVisibility( View.VISIBLE );
                holder.item_status.setText(ordersTable.getOrder_status());
            }else {
                holder.item_status.setText(ordersTable.getOrder_status());
            }
            if(ordersTable.getOrder_status().equalsIgnoreCase( "Delivered" )||ordersTable.getOrder_status().equalsIgnoreCase( "Cancelled" )){
                holder.change_order_address.setVisibility( View.GONE );
            }else{
                holder.change_order_address.setVisibility( View.VISIBLE );
                holder.change_order_address.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent( mCtx,SecondryAddressActivity.class );
                        Bundle bundle = new Bundle(  );
                        bundle.putSerializable( "OrdersTable",ordersTable );
                        intent.putExtras( bundle );
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        mCtx.startActivity( intent );
                    }
                } );
            }

            holder.order_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ordersTable.getOrders()!=null&&ordersTable.getOrders().size()!=0) {
                        if(holder.order_product_layout.getVisibility()==View.GONE){
                            holder.order_product_layout.setVisibility( View.VISIBLE );
                            RecievedorderDetailAdapter adapter = new RecievedorderDetailAdapter( mCtx,ordersTable.getOrders() );
                            holder.order_product_recycler.setAdapter( adapter );
                        }else if(holder.order_product_layout.getVisibility()==View.VISIBLE){
                            holder.order_product_layout.setVisibility( View.GONE );
                        }

                    }else{
                        holder.order_product_layout.setVisibility( View.GONE );
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
        TextView shipping_charge,itemName,user_name,payment_method,phone_number, itemTotalAmount, item_count, order_id, order_date_time,order_address,item_status,image,item_with_quantity;
        ImageView itemImage;
        AppCompatButton order_details;
        CardView cardlist_item;
        LinearLayout update_order_layout,order_product_layout;
        RecyclerView order_product_recycler;
        LottieAnimationView lottie_prepaire,lottie_delivery;
        AppCompatButton accept_order,order_pending,order_prepairing,order_delivered,order_cancelled,order_ontheway,order_confirm,change_order_address;

        public OrdersViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemTotalAmount = itemView.findViewById(R.id.order_totalAmount);
            itemImage = itemView.findViewById(R.id.product_thumb);
            //item_count = itemView.findViewById(R.id.item_quantity);
            order_id = itemView.findViewById(R.id.order_id);
            order_date_time = itemView.findViewById(R.id.order_date_time);
            order_address = itemView.findViewById(R.id.order_address);
            cardlist_item = itemView.findViewById(R.id.cardlist_item);
            image = itemView.findViewById(R.id.image);
            accept_order = itemView.findViewById(R.id.accept_order);
            order_pending = itemView.findViewById(R.id.order_pending);
            order_prepairing = itemView.findViewById(R.id.order_prepairing);
            order_delivered = itemView.findViewById(R.id.order_delivered);
            order_cancelled = itemView.findViewById(R.id.order_cancelled);
            order_ontheway = itemView.findViewById(R.id.order_ontheway);
            item_status = itemView.findViewById(R.id.item_status);
            order_confirm = itemView.findViewById(R.id.confim_order);
            lottie_prepaire = itemView.findViewById(R.id.lottie_prepaire);
            lottie_delivery = itemView.findViewById(R.id.lottie_delivery);
            item_with_quantity = itemView.findViewById(R.id.item_with_quantity);
            update_order_layout = itemView.findViewById(R.id.update_order_layout);
            order_product_layout = itemView.findViewById(R.id.order_product_layout);
            order_product_recycler = itemView.findViewById(R.id.order_product_recycler);
            order_details = itemView.findViewById(R.id.order_details);
            payment_method = itemView.findViewById(R.id.payment_method);
            user_name = itemView.findViewById(R.id.user_name);
            phone_number = itemView.findViewById(R.id.user_phone_number);
            shipping_charge = itemView.findViewById(R.id.shipping_charge);
            change_order_address = itemView.findViewById(R.id.change_order_address);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            System.out.println("click: " + getPosition());
        }
    }
}
