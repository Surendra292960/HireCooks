/*
package com.test.sample.hirecooks.Adapter.Orders;

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
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.test.sample.hirecooks.Activity.Orders.RecievedOrderDetails;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.NewOrder.Order;
import com.test.sample.hirecooks.Models.NewOrder.OrdersTable;
import com.test.sample.hirecooks.Models.NewOrder.Root;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.OrderApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.accountkit.internal.AccountKitController.getApplicationContext;

public class RecievedOrdersAdapter extends RecyclerView.Adapter<RecievedOrdersAdapter.OrdersViewHolder> {
    private Context mCtx;
    private List<OrdersTable> orderList;
    private List<OrdersTable> updateOrderStatus;
    private List<Order> order;
    User user = SharedPrefManager.getInstance(mCtx).getUser();
    private String status;

    public RecievedOrdersAdapter(Context mCtx, List<OrdersTable> orderList) {
        this.mCtx = mCtx;
        this.orderList = orderList;
        this.order = order;
    }

    @Override
    public OrdersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recieved_orders_cardview, parent, false);
        return new OrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrdersViewHolder holder, int position) {
        final OrdersTable ordersTable = orderList.get(position);
        if(ordersTable!=null) {
            holder.user_name.setText("Name  :  "+ ordersTable.getUser_name());
            holder.payment_method.setText("Payment  :  "+"COD");
            holder.phone_number.setText("Phone  :  "+ordersTable.getUser_phone());
            holder.itemTotalAmount.setText("Total Amount  :   \u20B9 " + ordersTable.getTotal_amount());
            holder.order_id.setText("# " + ordersTable.getOrder_id());
            holder.order_date_time.setText("Order On  :  "+ordersTable.getOrder_date_time());
            holder.order_address.setText("Address  :  "+ordersTable.getOrder_address());
            holder.lottie_delivery.setVisibility( View.GONE );
            holder.lottie_prepaire.setVisibility( View.GONE );
            Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
            holder.image.startAnimation(animation1);
            if (ordersTable.getOrder_status().equalsIgnoreCase("Cancelled")) {
                holder.item_status.setTextColor(Color.RED);
                holder.item_status.setText(ordersTable.getOrder_status());
                holder.update_order_layout.setVisibility(View.GONE);
            }else {
                holder.item_status.setText(ordersTable.getOrder_status());
                holder.update_order_layout.setVisibility(View.VISIBLE);
            }
            if (ordersTable.getConfirm_status().equalsIgnoreCase("Confirmed")) {
                holder.order_confirm.setVisibility(View.VISIBLE);
                holder.order_confirm.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.style_color_primary));
                holder.order_confirm.setText(ordersTable.getConfirm_status());
                holder.accept_order.setVisibility(View.GONE);
                holder.image.clearAnimation();
            }else{
                holder.accept_order.setVisibility(View.VISIBLE);
                holder.order_confirm.setVisibility(View.GONE);

                holder.accept_order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        status = "Confirmed";
                        ordersTable.setConfirm_status( status );
                        acceptOrders(holder,ordersTable.getOrder_id(),ordersTable);
                        holder.accept_order.setVisibility(View.GONE);
                        holder.order_confirm.setVisibility(View.VISIBLE);
                        holder.order_confirm.setBackgroundColor( ContextCompat.getColor(mCtx, R.color.style_color_primary));
                        holder.image.clearAnimation();
                    }
                });
            }

            holder.itemImage.setAnimation(AnimationUtils.loadAnimation(mCtx, R.anim.fade_transition_animation));
            holder.cardlist_item.setAnimation(AnimationUtils.loadAnimation(mCtx, R.anim.fade_scale_animation));

            holder.order_pending.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    status = "Pending";
                    ordersTable.setOrder_status( status );
                    if (ordersTable.getConfirm_status().equalsIgnoreCase("Confirmed")) {
                        updateOrderStatus(holder,ordersTable.getOrder_id(),ordersTable);
                    }else{
                        showalertbox(v,"Accept Order First");
                    }
                }
            });
            holder.order_prepairing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    status = "Prepairing";
                    ordersTable.setOrder_status( status );
                    if (ordersTable.getConfirm_status().equalsIgnoreCase("Confirmed")) {
                        updateOrderStatus(holder,ordersTable.getOrder_id(),ordersTable);
                    }else{
                        showalertbox(v,"Accept Order First");
                    }
                }
            });  holder.order_delivered.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    status = "Delivered";
                    ordersTable.setOrder_status( status );
                    if (ordersTable.getConfirm_status().equalsIgnoreCase("Confirmed")) {
                        updateOrderStatus(holder,ordersTable.getOrder_id(),ordersTable);
                    }else{
                        showalertbox(v,"Accept Order First");
                    }
                }
            });  holder.order_cancelled.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    status = "Cancelled";
                    ordersTable.setOrder_status( status );
                    updateOrderStatus(holder,ordersTable.getOrder_id(),ordersTable);
                }
            });  holder.order_ontheway.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    status = "On The Way";
                    ordersTable.setOrder_status( status );
                    if (ordersTable.getConfirm_status().equalsIgnoreCase("Confirmed")) {
                        updateOrderStatus(holder,ordersTable.getOrder_id(), ordersTable);
                    }else{
                        showalertbox(v,"Accept Order First");
                    }
                }
            }); holder.order_details.setOnClickListener(new View.OnClickListener() {
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

    public void showalertbox(View views, String string) {
        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder( mCtx);
        View view =  LayoutInflater.from(views.getRootView().getContext()).inflate(R.layout.show_alert_message, null);
        TextView ask = view.findViewById( R.id.ask );
        TextView textView = view.findViewById( R.id.text );
        ask.setText( string );
        textView.setText( "Alert !" );
        AppCompatTextView cancelBtn = view.findViewById(R.id.exit_app_btn);
        dialogBuilder.setView(view);
        final android.app.AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        cancelBtn.setOnClickListener( v -> {
            try {
                dialog.dismiss();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        } );
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
    private void updateOrderStatus(OrdersViewHolder holder, Integer id, OrdersTable ordersTable) {
        updateOrderStatus = new ArrayList<>(  );
        updateOrderStatus.add( ordersTable );
        Gson gson = new Gson();
        String json = gson.toJson( updateOrderStatus );
        System.out.println( "Suree ; "+json );
        OrderApi mService = ApiClient.getClient().create(OrderApi.class);
        Call<List<Root>> call = mService.updateOrderStatus(id,updateOrderStatus);
        call.enqueue(new Callback<List<Root>>() {
            @Override
            public void onResponse(Call<List<Root>> call, Response<List<Root>> response) {
                if (response.code() == 200 ) {
                    for(Root root:response.body()){
                        if(root.getError()==false){
                            Toast.makeText(mCtx,root.getMessage(),Toast.LENGTH_LONG).show();
                            holder.accept_order.setVisibility(View.GONE);
                            notifyDataSetChanged();
                        }
                    }

                } else {
                    Toast.makeText(mCtx,status,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Root>> call, Throwable t) {
                Toast.makeText(mCtx,R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void acceptOrders(OrdersViewHolder holder, Integer id, OrdersTable order_confirm) {
        updateOrderStatus = new ArrayList<>(  );
        updateOrderStatus.add( order_confirm );
        OrderApi mService = ApiClient.getClient().create(OrderApi.class);
        Call<List<Root>> call = mService.acceptOrders(id,updateOrderStatus);
        call.enqueue(new Callback<List<Root>>() {
            @Override
            public void onResponse(Call<List<Root>> call, Response<List<Root>> response) {
                if (response.code() == 200 ) {
                    for(Root root:response.body()){
                        if(root.getError()==false){
                            Toast.makeText(mCtx,root.getMessage(),Toast.LENGTH_LONG).show();
                            holder.accept_order.setVisibility(View.GONE);
                        }else{
                            Toast.makeText(mCtx,root.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(mCtx,response.code(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Root>> call, Throwable t) {
                Toast.makeText(mCtx,R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
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
        LottieAnimationView lottie_delivery,lottie_prepaire;
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
            lottie_delivery = itemView.findViewById(R.id.lottie_delivery);
            lottie_prepaire = itemView.findViewById(R.id.lottie_prepaire);
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
*/