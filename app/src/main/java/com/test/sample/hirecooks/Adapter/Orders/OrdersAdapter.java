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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.test.sample.hirecooks.Activity.ManageAddress.SecondryAddressActivity;
import com.test.sample.hirecooks.Activity.Orders.RecievedOrderDetails;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.NewOrder.Order;
import com.test.sample.hirecooks.Models.NewOrder.OrdersTable;
import com.test.sample.hirecooks.Models.NewOrder.OrdersResponse;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.WebApis.OrderApi;
import com.test.sample.hirecooks.databinding.OrdersCardviewBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.accountkit.internal.AccountKitController.getApplicationContext;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {
    private Context mCtx;
    private List<OrdersTable> orderList;
    private List<OrdersTable> updateOrderStatus;
    private List<Order> order;
    private String status,type;

    public OrdersAdapter(Context mCtx, List<OrdersTable> orderList, String type) {
        this.mCtx = mCtx;
        this.orderList = orderList;
        this.type = type;
        this.order = order;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(OrdersCardviewBinding.inflate(LayoutInflater.from(mCtx)));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final OrdersTable ordersTable = orderList.get(position);
        if(ordersTable!=null) {
            holder.binding.userName.setText("Name  :  "+ ordersTable.getUser_name());
            holder.binding.paymentMethod.setText("Payment  :  "+"COD");
            holder.binding.userPhoneNumber.setText("Phone  :  "+ordersTable.getUser_phone());
            holder.binding.orderTotalAmount.setText("Total Amount  :   \u20B9 " + ordersTable.getTotal_amount());
            holder.binding.orderId.setText("# " + ordersTable.getOrder_id());
            holder.binding.orderDateTime.setText("Order On  :  "+ordersTable.getOrder_date_time());
            holder.binding.orderAddress.setText("Address  :  "+ordersTable.getOrder_address());
            holder.binding.lottieDelivery.setVisibility( View.GONE );
            holder.binding.lottiePrepaire.setVisibility( View.GONE );
            Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
            holder.binding.image.startAnimation(animation1);
            if (ordersTable.getOrder_status().equalsIgnoreCase("Cancelled")) {
                holder.binding.itemStatus.setTextColor(Color.RED);
                holder.binding.itemStatus.setText(ordersTable.getOrder_status());
                holder.binding.updateOrderLayout.setVisibility(View.GONE);
            }else {
                holder.binding.itemStatus.setText(ordersTable.getOrder_status());
                holder.binding.updateOrderLayout.setVisibility(View.VISIBLE);
            }
            if (ordersTable.getConfirm_status().equalsIgnoreCase("Confirmed")) {
                holder.binding.confimOrder.setVisibility(View.VISIBLE);
                holder.binding.confimOrder.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.style_color_primary));
                holder.binding.confimOrder.setText(ordersTable.getConfirm_status());
                holder.binding.acceptOrder.setVisibility(View.GONE);
                holder.binding.image.clearAnimation();
            }else{
                holder.binding.acceptOrder.setVisibility(View.VISIBLE);
                holder.binding.confimOrder.setVisibility(View.GONE);

                holder.binding.acceptOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        status = "Confirmed";
                        ordersTable.setConfirm_status( status );
                        acceptOrders(holder,ordersTable.getOrder_id(),ordersTable);
                        holder.binding.acceptOrder.setVisibility(View.GONE);
                        holder.binding.confimOrder.setVisibility(View.VISIBLE);
                        holder.binding.confimOrder.setBackgroundColor( ContextCompat.getColor(mCtx, R.color.style_color_primary));
                        holder.binding.image.clearAnimation();
                    }
                });
            }

            holder.binding.productThumb.setAnimation(AnimationUtils.loadAnimation(mCtx, R.anim.fade_transition_animation));
            holder.binding.cardlistItem.setAnimation(AnimationUtils.loadAnimation(mCtx, R.anim.fade_scale_animation));

            holder.binding.orderPending.setOnClickListener(new View.OnClickListener() {
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
            holder.binding.orderPrepairing.setOnClickListener(new View.OnClickListener() {
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
            });  holder.binding.orderDelivered.setOnClickListener(new View.OnClickListener() {
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
            });  holder.binding.orderCancelled.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    status = "Cancelled";
                    ordersTable.setOrder_status( status );
                    updateOrderStatus(holder,ordersTable.getOrder_id(),ordersTable);
                }
            });  holder.binding.orderOntheway.setOnClickListener(new View.OnClickListener() {
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
            }); holder.binding.orderDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ordersTable.getOrders()!=null&&ordersTable.getOrders().size()!=0) {
                        if(holder.binding.orderProductLayout.getVisibility()==View.GONE){
                            holder.binding.orderProductLayout.setVisibility( View.VISIBLE );
                            RecievedorderDetailAdapter adapter = new RecievedorderDetailAdapter( mCtx,ordersTable.getOrders() );
                            holder.binding.orderProductRecycler.setAdapter( adapter );
                        }else if(holder.binding.orderProductLayout.getVisibility()==View.VISIBLE){
                            holder.binding.orderProductLayout.setVisibility( View.GONE );
                        }

                    }else{
                        holder.binding.orderProductLayout.setVisibility( View.GONE );
                    }
                }
            });

            if(type.equalsIgnoreCase("MyOrders")){
                holder.binding.updateOrderLayout.setVisibility( View.GONE );
                if(ordersTable.getOrder_status().equalsIgnoreCase( "Delivered" )||ordersTable.getOrder_status().equalsIgnoreCase( "Cancelled" )){
                    holder.binding.changeOrderAddress.setVisibility( View.GONE );
                }else{
                    holder.binding.changeOrderAddress.setVisibility( View.VISIBLE );
                    holder.binding.changeOrderAddress.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent( mCtx, SecondryAddressActivity.class );
                            Bundle bundle = new Bundle(  );
                            bundle.putSerializable( "OrdersTable",ordersTable );
                            intent.putExtras( bundle );
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            mCtx.startActivity( intent );
                        }
                    } );
                }
                if(ordersTable.getOrder_status().equalsIgnoreCase( "Cancelled" )){
                    holder.binding.itemStatus.setTextColor( Color.RED );
                    holder.binding.itemStatus.setText(ordersTable.getOrder_status());
                }else if(ordersTable.getOrder_status().equalsIgnoreCase( "Pending" )){
                    holder.binding.image.startAnimation(animation1);
                    holder.binding.itemStatus.setText(ordersTable.getOrder_status());
                }else if(ordersTable.getOrder_status().equalsIgnoreCase( "Delivered" )){
                    holder.binding.image.clearAnimation();
                    holder.binding.itemStatus.setText(ordersTable.getOrder_status());
                }else if(ordersTable.getOrder_status().equalsIgnoreCase( "Prepairing" )){
                    holder.binding.lottiePrepaire.setVisibility( View.VISIBLE );
                    holder.binding.itemStatus.setText(ordersTable.getOrder_status());
                }else if(ordersTable.getOrder_status().equalsIgnoreCase( "On The Way" )){
                    holder.binding.lottieDelivery.setVisibility( View.VISIBLE );
                    holder.binding.itemStatus.setText(ordersTable.getOrder_status());
                }else {
                    holder.binding.itemStatus.setText(ordersTable.getOrder_status());
                }
            }else{
                holder.binding.updateOrderLayout.setVisibility( View.VISIBLE );
            }
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
    private void updateOrderStatus(ViewHolder holder, Integer id, OrdersTable ordersTable) {
        updateOrderStatus = new ArrayList<>(  );
        updateOrderStatus.add( ordersTable );
        Gson gson = new Gson();
        String json = gson.toJson( updateOrderStatus );
        System.out.println( "Suree ; "+json );
        OrderApi mService = ApiClient.getClient().create(OrderApi.class);
        Call<List<OrdersResponse>> call = mService.updateOrderStatus(id,updateOrderStatus);
        call.enqueue(new Callback<List<OrdersResponse>>() {
            @Override
            public void onResponse(Call<List<OrdersResponse>> call, Response<List<OrdersResponse>> response) {
                if (response.code() == 200 ) {
                    for(OrdersResponse root:response.body()){
                        if(root.getError()==false){
                            Toast.makeText(mCtx,root.getMessage(),Toast.LENGTH_LONG).show();
                            holder.binding.acceptOrder.setVisibility(View.GONE);
                            notifyDataSetChanged();
                        }
                    }

                } else {
                    Toast.makeText(mCtx,status,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrdersResponse>> call, Throwable t) {
                Toast.makeText(mCtx,R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void acceptOrders(ViewHolder holder, Integer id, OrdersTable order_confirm) {
        updateOrderStatus = new ArrayList<>(  );
        updateOrderStatus.add( order_confirm );
        OrderApi mService = ApiClient.getClient().create(OrderApi.class);
        Call<List<OrdersResponse>> call = mService.acceptOrders(id,updateOrderStatus);
        call.enqueue(new Callback<List<OrdersResponse>>() {
            @Override
            public void onResponse(Call<List<OrdersResponse>> call, Response<List<OrdersResponse>> response) {
                if (response.code() == 200 ) {
                    for(OrdersResponse root:response.body()){
                        if(root.getError()==false){
                            Toast.makeText(mCtx,root.getMessage(),Toast.LENGTH_LONG).show();
                            holder.binding.acceptOrder.setVisibility(View.GONE);
                        }else{
                            Toast.makeText(mCtx,root.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(mCtx,response.code(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrdersResponse>> call, Throwable t) {
                Toast.makeText(mCtx,R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList==null?0:orderList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OrdersCardviewBinding binding;
        public ViewHolder(@NonNull OrdersCardviewBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        @Override
        public void onClick(View view) {
            System.out.println("click: " + getPosition());
        }
    }
}
