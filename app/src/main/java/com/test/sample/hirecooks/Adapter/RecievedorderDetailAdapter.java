package com.test.sample.hirecooks.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Activity.Orders.RecievedOrderDetails;
import com.test.sample.hirecooks.Models.NewOrder.Order;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import com.test.sample.hirecooks.R;

import java.util.ArrayList;
import java.util.List;

public class RecievedorderDetailAdapter extends RecyclerView.Adapter<RecievedorderDetailAdapter.MyViewHolder> {
    List<Order> orderList;
    Context context;
    List<Subcategory> cartList = new ArrayList<>();
    String weight;
    private int discount = 0, discountPercentage = 0, displayrate = 0,sellRate = 0,displayRate = 0, SubTotal = 0,Quantity = 0;

    public RecievedorderDetailAdapter(Context context, List<Order> orderList) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecievedorderDetailAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate( R.layout.item_horizontal_layout, parent, false);
        return new RecievedorderDetailAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecievedorderDetailAdapter.MyViewHolder holder, final int position) {
        final Order order = orderList.get(position);

        if(order!=null){
            holder.add_item_layout.setVisibility( View.GONE );
            holder.discription.setVisibility( View.GONE );
            holder.item_qty.setVisibility( View.VISIBLE );
            holder.name.setText(order.getName());
            holder.item_qty.setText("Quantity : "+order.getQuantity());
            if(order.getImages().size()!=0&&order.getImages()!=null){
                Picasso.with(context).load(order.getImages().get( 0 ).getImage()).into(holder.imageView);
            }

            if (order.getSellRate() != 0 && order.getDisplayRate()!= 0) {
                holder.sellrate.setText("\u20B9 " + order.getSellRate());
                SpannableString spanString = new SpannableString("\u20B9 " + order.getDisplayRate());
                spanString.setSpan(new StrikethroughSpan(), 0, spanString.length(), 0);
                holder.displayRate.setText(spanString);
                discount = (order.getDiscount());
                displayrate = (order.getDisplayRate());
                discountPercentage = (discount * 100 / displayrate);
                holder.discount.setText("Save " + discountPercentage + " %");
            }
        }

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order!=null) {
                    Intent intent = new Intent(context, RecievedOrderDetails.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Orders", order);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            }
        });
/*
        holder.cardview.setOnClickListener( v -> {

        } );*/
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView discount, name, sellrate, quantity, displayRate,item_not_in_stock,discription,item_short_desc,add_,item_qty;
        TextView add_item, remove_item;
        LinearLayout add_item_layout,quantity_ll,order_not_accepting;
        CardView cardview;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
            discount = itemView.findViewById(R.id.item_discount);
            sellrate = itemView.findViewById(R.id.item_sellrate);
            displayRate = itemView.findViewById(R.id.item_displayrate);
            quantity = itemView.findViewById(R.id.item_count);
            add_item = itemView.findViewById(R.id.add_item);
            remove_item = itemView.findViewById(R.id.remove_item);
            add_item_layout = itemView.findViewById(R.id.add_item_layout);
            add_ = itemView.findViewById(R.id.add_);
            quantity_ll = itemView.findViewById(R.id.quantity_ll);
            // available_stock = itemView.findViewById(R.id.available_stock);
            cardview = itemView.findViewById(R.id.card_view);
            item_not_in_stock = itemView.findViewById(R.id.item_not_in_stock);
            discription = itemView.findViewById(R.id.item_description);
            item_short_desc = itemView.findViewById(R.id.item_short_desc);
            item_qty = itemView.findViewById(R.id.item_qty);
            order_not_accepting = itemView.findViewById(R.id.order_not_accepting);
        }
    }
}
