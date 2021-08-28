package com.test.sample.hirecooks.Adapter.Users;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.test.sample.hirecooks.Activity.ManageAddress.UpdateAddressActivity;
import com.test.sample.hirecooks.Activity.Orders.PlaceOrderActivity;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.R;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.TasksViewHolder> {
    private Context mCtx;
    private List<Map> addressList;

    public AddressAdapter(Context mCtx, List<Map> addressList) {
        this.mCtx = mCtx;
        this.addressList = addressList;
    }

    @NonNull
    @Override
    public TasksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_address, parent, false);
        return new TasksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TasksViewHolder holder, int position) {
        Map address = addressList.get(position);
        holder.address.setText(address.getAddress());
        holder.sub_address.setText(address.getSubAddress());
        holder.location_tag.setText(address.getLocationType());

        holder.edit_address.setOnClickListener( v -> {
            Bundle bundle = new Bundle(  );
            Intent intent = new Intent(mCtx, UpdateAddressActivity.class);
            bundle.putSerializable("address", address);
            intent.putExtras( bundle );
            mCtx.startActivity(intent);
        } );
    }

    @Override
    public int getItemCount() {
        return addressList==null?0:addressList.size();
    }

    class TasksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView address, edit_address,location_tag, sub_address;

        TasksViewHolder(View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address);
            edit_address = itemView.findViewById(R.id.edit_address);
            sub_address = itemView.findViewById(R.id.sub_address);
            location_tag = itemView.findViewById(R.id.location_tag);
           itemView.setOnClickListener(this);
    }

        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            Map address = addressList.get(getAdapterPosition());
            Intent intent = new Intent(mCtx, PlaceOrderActivity.class);
            bundle.putSerializable("address", address);
            intent.putExtras(bundle);
            mCtx.startActivity(intent);
            ((Activity)mCtx).finish();
        }
    }
}