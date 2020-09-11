package com.test.sample.hirecooks.Adapter;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Activity.Orders.RecievedOrderDetails;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.Order.Order;
import com.test.sample.hirecooks.Models.Order.Results;
import com.test.sample.hirecooks.Models.users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.Utils.Utils;
import com.test.sample.hirecooks.WebApis.OrderApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.accountkit.internal.AccountKitController.getApplicationContext;

public class RecievedOrdersAdapter extends RecyclerView.Adapter<RecievedOrdersAdapter.OrdersViewHolder> {
    private Context mCtx;
    private List<Order> orderList;
    User user = SharedPrefManager.getInstance(mCtx).getUser();
    private String status;

    public RecievedOrdersAdapter(Context mCtx, List<Order> orderList) {
        this.mCtx = mCtx;
        this.orderList = orderList;
    }

    @Override
    public RecievedOrdersAdapter.OrdersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recieved_orders_cardview, parent, false);
        return new RecievedOrdersAdapter.OrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecievedOrdersAdapter.OrdersViewHolder holder, int position) {
        final Order order = orderList.get(position);
        if(order!=null) {
            holder.itemName.setText(order.getProductName());
            holder.itemTotalAmount.setText("\u20B9 " + order.getProductTotalAmount());
            holder.item_with_quantity.setText(order.getProductQuantity()+" x "+order.getProductName());
            holder.order_id.setText("Order ID: # " + order.getOrderId());
            holder.order_date_time.setText(order.getOrderDateTime());
            holder.order_address.setText(order.getOrderAddress());
            Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
            holder.image.startAnimation(animation1);
            if (order.getOrderStatus().equalsIgnoreCase("Cancelled")) {
                holder.item_status.setTextColor(Color.RED);
                holder.item_status.setText(order.getOrderStatus());
                holder.cardlist_item.setCardBackgroundColor(Color.DKGRAY);
                holder.itemName.setTextColor(Color.WHITE);
                holder.itemTotalAmount.setTextColor(Color.WHITE);
                holder.item_with_quantity.setTextColor(Color.WHITE);
                holder.order_date_time.setTextColor(Color.WHITE);
                holder.order_address.setTextColor(Color.WHITE);
                holder.update_order_layout.setVisibility(View.GONE);
            }else {
                holder.item_status.setText(order.getOrderStatus());
                holder.update_order_layout.setVisibility(View.VISIBLE);
            }
            if (order.getOrderConfirm().equalsIgnoreCase("Confirmed")) {
                holder.order_confirm.setVisibility(View.VISIBLE);
                holder.order_confirm.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.style_color_primary));
                holder.order_confirm.setText(order.getOrderConfirm());
                holder.accept_order.setVisibility(View.GONE);
                holder.image.clearAnimation();
            }else{
                holder.accept_order.setVisibility(View.VISIBLE);
                holder.order_confirm.setVisibility(View.GONE);
                holder.accept_order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        acceptOrders(holder,order.getId(),"Confirmed");
                        holder.accept_order.setVisibility(View.GONE);
                        holder.order_confirm.setVisibility(View.VISIBLE);
                        holder.order_confirm.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.style_color_primary));
                        holder.image.clearAnimation();
                    }
                });
            }

            holder.itemImage.setAnimation(AnimationUtils.loadAnimation(mCtx, R.anim.fade_transition_animation));
            holder.cardlist_item.setAnimation(AnimationUtils.loadAnimation(mCtx, R.anim.fade_scale_animation));
            Picasso.with(mCtx).load(order.getProductImage()).into(holder.itemImage);

            holder.cardlist_item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Utils.vibrate(mCtx);
                    showAlertDialog(mCtx,user,order);
                    return false;
                }
            });

            holder.order_pending.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    status = "Pending";
                    updateOrderStatus(order.getId(),status);
                }
            });  holder.order_prepairing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    status = "Prepairing";
                    if (order.getOrderConfirm().equalsIgnoreCase("Confirmed")) {
                        updateOrderStatus(order.getId(), status);
                    }else{
                        Toast.makeText(mCtx,"Accept Order First",Toast.LENGTH_LONG).show();
                    }
                }
            });  holder.order_delivered.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    status = "Delivered";
                    if (order.getOrderConfirm().equalsIgnoreCase("Confirmed")) {
                        updateOrderStatus(order.getId(), status);
                    }else{
                        Toast.makeText(mCtx,"Accept Order First",Toast.LENGTH_LONG).show();
                    }
                }
            });  holder.order_cancelled.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    status = "Cancelled";
                    updateOrderStatus(order.getId(),status);
                }
            });  holder.order_ontheway.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    status = "On The Way";
                    if (order.getOrderConfirm().equalsIgnoreCase("Confirmed")) {
                        updateOrderStatus(order.getId(), status);
                    }else{
                        Toast.makeText(mCtx,"Accept Order First",Toast.LENGTH_LONG).show();
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
    private void updateOrderStatus(Integer id, String order_status) {
        OrderApi mService = ApiClient.getClient().create(OrderApi.class);
        Call<Results> call = mService.updateOrderStatus(id,order_status);
        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {
                if (response.code() == 200 ) {
                    Toast.makeText(mCtx,response.body().getMessage(),Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mCtx,response.body().getMessage(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Results> call, Throwable t) {
                Toast.makeText(mCtx,R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void acceptOrders(OrdersViewHolder holder, Integer id, String order_confirm) {
        OrderApi mService = ApiClient.getClient().create(OrderApi.class);
        Call<Results> call = mService.acceptOrders(id,order_confirm);
        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {
                if (response.code() == 200 ) {
                    Toast.makeText(mCtx,response.body().getMessage(),Toast.LENGTH_LONG).show();
                    holder.accept_order.setVisibility(View.GONE);
                } else {
                    Toast.makeText(mCtx,response.body().getMessage(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Results> call, Throwable t) {
                Toast.makeText(mCtx,R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList==null?0:orderList.size();
    }

    class OrdersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView itemName, itemTotalAmount, item_count, order_id, order_date_time,order_address,item_status,image,item_with_quantity;
        ImageView itemImage;
        CardView cardlist_item;
        LinearLayout update_order_layout;
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
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            System.out.println("click: " + getPosition());
        }
    }
}
