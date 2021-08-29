package com.test.sample.hirecooks.Adapter.Users;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.test.sample.hirecooks.Activity.ManageAddress.UpdateAddressActivity;
import com.test.sample.hirecooks.Activity.Orders.PlaceOrderActivity;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.NewOrder.OrdersTable;
import com.test.sample.hirecooks.Models.NewOrder.Root;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.WebApis.OrderApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.TasksViewHolder> {
    private Context mCtx;
    private OrdersTable ordersTable;
    private List<Map> addressList;
    private List<Root> rootList;
    private List<OrdersTable> ordersTables;

    public AddressAdapter(Context mCtx, List<Map> addressList, OrdersTable ordersTable) {
        this.mCtx = mCtx;
        this.addressList = addressList;
        this.ordersTable = ordersTable;
    }

    @NonNull
    @Override
    public TasksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_address, parent, false);
        return new TasksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TasksViewHolder holder, int position) {
        final Map mAddress = addressList.get(position);
        holder.address.setText(mAddress.getAddress());
        holder.sub_address.setText(mAddress.getSubAddress());
        holder.location_tag.setText(mAddress.getLocationType());

        holder.edit_address.setOnClickListener( v -> {
            Bundle bundle = new Bundle(  );
            Intent intent = new Intent(mCtx, UpdateAddressActivity.class);
            bundle.putSerializable("address", addressList.get( position ));
            intent.putExtras( bundle );
            mCtx.startActivity(intent);
        } );
        holder.place_item_view.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ordersTable!=null){
                    updateOrderAddress(addressList.get(position));
                }else{
                    Bundle bundle = new Bundle();
                    Map address = addressList.get(position);
                    Intent intent = new Intent(mCtx, PlaceOrderActivity.class);
                    bundle.putSerializable("address", address);
                    intent.putExtras(bundle);
                    mCtx.startActivity(intent);
                    ((Activity)mCtx).finish();
                }
            }
        } );
    }

    @Override
    public int getItemCount() {
        return addressList==null?0:addressList.size();
    }

    class TasksViewHolder extends RecyclerView.ViewHolder{
        TextView address, edit_address,location_tag, sub_address;
        LinearLayout place_item_view;

        TasksViewHolder(View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address);
            edit_address = itemView.findViewById(R.id.edit_address);
            sub_address = itemView.findViewById(R.id.sub_address);
            location_tag = itemView.findViewById(R.id.location_tag);
            place_item_view = itemView.findViewById(R.id.place_item_view);
        }
    }

    private void updateOrderAddress(Map mAddress) {
        Root root = new Root();
        ordersTables = new ArrayList<>(  );
        rootList = new ArrayList<>(  );
        OrdersTable ordersTbl = new OrdersTable();
        ordersTbl.setOrder_id(  ordersTable.getOrder_id());
        ordersTbl.setOrder_latitude(  mAddress.getLatitude());
        ordersTbl.setOrder_longitude(  mAddress.getLongitude());
        ordersTbl.setOrder_sub_address( mAddress.getSubAddress() );
        ordersTbl.setOrder_address(mAddress.getAddress()  );
        ordersTbl.setOrder_pincode( mAddress.getPincode() );
        ordersTables.add( ordersTbl );
        root.setOrders_table( ordersTables );
        rootList.add( root );
        OrderApi mService = ApiClient.getClient().create(OrderApi.class);
        Call<List<Root>> call = mService.updateOrderAddresss( ordersTable.getOrder_id(),rootList );
        call.enqueue( new Callback<List<Root>>() {
            @Override
            public void onResponse(Call<List<Root>> call, Response<List<Root>> response) {
                if(response.code()==200){
                    Toast.makeText( mCtx, "Success", Toast.LENGTH_SHORT ).show();
                }else{
                    Toast.makeText( mCtx, "Failed "+response.code(), Toast.LENGTH_SHORT ).show();
                }
            }

            @Override
            public void onFailure(Call<List<Root>> call, Throwable t) {
                Toast.makeText( mCtx, "Failed "+t.getMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );
    }
}