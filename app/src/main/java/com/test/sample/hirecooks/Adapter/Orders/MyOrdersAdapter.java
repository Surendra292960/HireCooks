package com.test.sample.hirecooks.Adapter.Orders;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.test.sample.hirecooks.Activity.Orders.RecievedOrderDetails;
import com.test.sample.hirecooks.Adapter.RecievedorderDetailAdapter;
import com.test.sample.hirecooks.Models.NewOrder.OrdersTable;
import com.test.sample.hirecooks.Models.Order.Order;
import com.test.sample.hirecooks.Models.users.User;
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
            holder.phone_number.setText("Phone  :  "+user.getPhone());
            holder.itemTotalAmount.setText("Total Amount  :   \u20B9 " + ordersTable.getTotal_amount());
            holder.order_id.setText("# " + ordersTable.getOrder_id());
            holder.order_date_time.setText("Order On  :  "+ordersTable.getOrder_date_time());
            holder.order_address.setText("Address  :  "+ordersTable.getOrder_address());
            Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
            holder.item_status.setText(ordersTable.getOrder_status());
            holder.itemImage.setAnimation(AnimationUtils.loadAnimation(mCtx, R.anim.fade_transition_animation));
            holder.cardlist_item.setAnimation(AnimationUtils.loadAnimation(mCtx, R.anim.fade_scale_animation));
            holder.update_order_layout.setVisibility( View.GONE );
            if(ordersTable.getOrder_status().equalsIgnoreCase( "Pending" )){
                holder.image.startAnimation(animation1);
            }else if(ordersTable.getOrder_status().equalsIgnoreCase( "Delivered" )){
                holder.image.clearAnimation();
            }

            holder.order_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ordersTable.getOrders()!=null&&ordersTable.getOrders().size()!=0) {
                        holder.order_product_layout.setVisibility( View.VISIBLE );
                        RecievedorderDetailAdapter adapter = new RecievedorderDetailAdapter( mCtx,ordersTable.getOrders() );
                        holder.order_product_recycler.setAdapter( adapter );

                    }else{
                        holder.order_product_layout.setVisibility( View.GONE );
                    }
                }
            });
        }
    }

    public void showAlertDialog(Context context, User user, Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("What do you wants to do ?");
        builder.setMessage("Are you sure");
        builder.setPositiveButton("Order Details", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!user.getUserType().equalsIgnoreCase("User")&&!user.getUserType().equalsIgnoreCase("Cook")) {
                    Intent intent = new Intent(mCtx, RecievedOrderDetails.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Orders", order);
                    intent.putExtras(bundle);
                    mCtx.startActivity(intent);
                }
                dialog.cancel();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public int getItemCount() {
        return orderList==null?0:orderList.size();
    }

    class OrdersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView itemName,user_name,payment_method,phone_number, itemTotalAmount, item_count, order_id, order_date_time,order_address,item_status,image,item_with_quantity;
        ImageView itemImage;
        AppCompatButton order_details;
        CardView cardlist_item;
        LinearLayout update_order_layout,order_product_layout;
        RecyclerView order_product_recycler;
        AppCompatButton accept_order,order_pending,order_prepairing,order_delivered,order_cancelled,order_ontheway,order_confirm;

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
            item_with_quantity = itemView.findViewById(R.id.item_with_quantity);
            update_order_layout = itemView.findViewById(R.id.update_order_layout);
            order_product_layout = itemView.findViewById(R.id.order_product_layout);
            order_product_recycler = itemView.findViewById(R.id.order_product_recycler);
            order_details = itemView.findViewById(R.id.order_details);
            payment_method = itemView.findViewById(R.id.payment_method);
            user_name = itemView.findViewById(R.id.user_name);
            phone_number = itemView.findViewById(R.id.user_phone_number);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            System.out.println("click: " + getPosition());
        }
    }
}
