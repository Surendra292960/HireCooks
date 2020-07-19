/*
package com.test.sample.hirecooks.Adapter.SearchAdapter;
import android.content.Context;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Activity.AddorRemoveCallbacks;
import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Models.Cart.Cart;
import com.test.sample.hirecooks.Models.SearchSubCategory.Search;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.LocalStorage;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {
    List<Search> productList;
    Context context;
    String Tag;
    LocalStorage localStorage;
    MainActivity mainActivity;
    Gson gson;
    List<Cart> cartList = new ArrayList<>();
    String weight;
    private int discount = 0, discountPercentage = 0, displayrate = 0,sellRate = 0,displayRate = 0, SubTotal = 0,Quantity = 0;

    public SearchAdapter(Context context, List<Search> productList, MainActivity mainActivity) {
        this.productList = productList;
        this.context = context;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public SearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subcategory_cardview, parent, false);
        return new SearchAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchAdapter.MyViewHolder holder, final int position) {

        final Search product = productList.get(position);
        localStorage = new LocalStorage(context);
        gson = new Gson();
        cartList = ((BaseActivity) context).getCartList();

        if(product!=null){
            if(product.getStock()==1){
                holder.add_item_layout.setVisibility(View.VISIBLE);
            }else{
                holder.add_item_layout.setVisibility(View.GONE);
            }
            if(product!=null){
                holder.add_item_layout.setVisibility(View.VISIBLE);
                holder.available_stock.setVisibility(View.VISIBLE);

            }else{
                holder.available_stock.setVisibility(View.GONE);
                holder.add_item_layout.setVisibility(View.GONE);
            }
            holder.name.setText(product.getName());
            Picasso.with(context).load(product.getLink()).into(holder.imageView);

            if (product.getSellRate() != null && product.getSellRate() != 0 && product.getDisplayRate() != null && product.getDisplayRate() != 0) {
                holder.sellrate.setText("\u20B9 " + product.getSellRate());
                SpannableString spanString = new SpannableString("\u20B9 " + product.getDisplayRate());
                spanString.setSpan(new StrikethroughSpan(), 0, spanString.length(), 0);
                holder.displayRate.setText(spanString);
                discount = (product.getDisplayRate() - product.getSellRate());
                displayrate = (product.getDisplayRate());
                discountPercentage = (discount * 100 / displayrate);
                holder.discount.setText("Save " + discountPercentage + " %");
            }
        }

        if (!cartList.isEmpty()) {
            for (int i = 0; i < cartList.size(); i++) {
                if (cartList.get(i).getId()==product.getId()&&cartList.get(i).getName().equalsIgnoreCase(product.getName())) {
                    holder.quantity.setText(""+cartList.get(i).getItemQuantity());
                    Quantity = cartList.get(i).getItemQuantity();
                    sellRate = product.getSellRate();
                    SubTotal = (sellRate * Quantity);
                }
            }
        } else {

            holder.quantity.setText("0");
        }

        holder.add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellRate = product.getSellRate();
                displayRate = product.getDisplayRate();
                String count = holder.quantity.getText().toString();
                Quantity = Integer.parseInt(count);
                if (Quantity >= 1) {
                    Quantity++;
                    holder.quantity.setText(""+(Quantity));
                    for (int i = 0; i < cartList.size(); i++) {

                        if (cartList.get(i).getId()==product.getId()&&cartList.get(i).getName().equalsIgnoreCase(product.getName())) {
                            SubTotal = (sellRate * Quantity);
                            cartList.get(i).setItemQuantity(Quantity);
                            cartList.get(i).setTotalAmount(SubTotal);
                            String cartStr = gson.toJson(cartList);
                            localStorage.setCart(cartStr);
                            notifyItemChanged(position);
                          //  getCart();
                        }
                    }
                }else {
                    if (product.getId() != 0 && product.getName() != null && product.getLink() != null && product.getDiscription() != null && sellRate != 0 && displayrate != 0 && product.getFirmId() != null) {
                        sellRate = product.getSellRate();
                        displayRate = product.getDisplayRate();
                        if (Quantity == 0) {
                            Quantity = 1;
                            SubTotal = (sellRate * Quantity);
                            if (context instanceof MainActivity) {
                                Cart cart = new Cart(product.getId(), product.getName(),  product.getLink(), product.getDiscription(), sellRate, displayRate, SubTotal, 1, weight, product.getFirmId());
                                cartList = ((BaseActivity) context).getCartList();
                                cartList.add(cart);
                                String cartStr = gson.toJson(cartList);
                                localStorage.setCart(cartStr);
                                ((AddorRemoveCallbacks) context).onAddProduct();
                                notifyItemChanged(position);
                                //getCart();
                            }
                        } else {
                            Toast.makeText(context, "Please Add Quantity", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(context, "This item can`t be add please remove item and try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        holder.remove_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellRate = product.getSellRate();
                displayRate = product.getDisplayRate();
                String count = holder.quantity.getText().toString();
                Quantity = Integer.parseInt(count);
                if (Quantity > 1) {
                    Quantity--;
                    holder.quantity.setText(""+(Quantity));
                    for (int i = 0; i < cartList.size(); i++) {
                        if (cartList.get(i).getId()==product.getId()&&cartList.get(i).getName().equalsIgnoreCase(product.getName())) {
                            SubTotal = (sellRate * Quantity);
                            cartList.get(i).setItemQuantity(Quantity);
                            cartList.get(i).setTotalAmount(SubTotal);
                            String cartStr = gson.toJson(cartList);
                            localStorage.setCart(cartStr);
                            notifyItemChanged(position);
                           // getCart();
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView discount, name, sellrate, quantity, displayRate,available_stock;
        ImageView add_item, remove_item;
        LinearLayout add_item_layout;

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
            available_stock = itemView.findViewById(R.id.available_stock);
        }
    }
}*/
