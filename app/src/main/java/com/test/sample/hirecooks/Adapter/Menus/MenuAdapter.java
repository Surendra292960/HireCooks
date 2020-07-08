package com.test.sample.hirecooks.Adapter.Menus;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Models.MenuResponse.Menu;
import com.test.sample.hirecooks.Models.cooks.Cook;
import com.test.sample.hirecooks.R;

import org.w3c.dom.Text;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder>  {
    private Context mCtx;
    private List<Menu> menus;

    public MenuAdapter(Context mCtx, List<Menu> menus) {
        this.mCtx = mCtx;
        this.menus = menus;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false);
        return new MenuAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Menu menu = menus.get(position);
        holder.menu_name.setText(menu.getName());
        holder.menu_descount.setText(menu.getDiscount());
        Picasso.with(mCtx).load(menu.getLink()).into(holder.ivFeedCenter);
    }

    @Override
    public int getItemCount() {
        return menus==null ? 0 : menus.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFeedCenter;
        ImageView ivUserProfile;
        TextView menu_name,menu_descount,menu_price;

        public ViewHolder(@NonNull View view) {
            super(view);

            ivFeedCenter = view.findViewById(R.id.ivFeedCenter);
            ivUserProfile = view.findViewById(R.id.ivUserProfile);
            menu_name = view.findViewById(R.id.menu_name);
            menu_descount = view.findViewById(R.id.menu_descount);
            menu_price = view.findViewById(R.id.menu_price);
        }
    }
}
