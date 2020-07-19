package com.test.sample.hirecooks.Activity.Favourite;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Models.Cart.Cart;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.LocalStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FavouriteActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private TextView item_count,checkout_amount,checkout;
    private FrameLayout no_result_found;
    private AppCompatButton shop_now;
    private View appRoot,bottom_anchor;
    private List<Cart> FavouriteList;
    private RelativeLayout bottom_anchor_layout;
    private int Quantity = 0, sellRate = 0,displayRate = 0, SubTotal = 0,weight = 0;
    private LocalStorage localStorage;
    private Gson gson;
    private FavouriteAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Favourite");
        initViews();
       // getCart();
        getFavourites();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerview_favourite);
        appRoot = findViewById(R.id.appRoot);
        bottom_anchor_layout = findViewById(R.id.bottom_anchor_layout);
        no_result_found = findViewById(R.id.no_result_found);
        shop_now = findViewById(R.id.shop_now);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        View view = findViewById(R.id.footerView);
        item_count =  view.findViewById(R.id.item_count);
        bottom_anchor =  view.findViewById(R.id.bottom_anchor);
        checkout_amount = view.findViewById(R.id.checkout_amount);
        checkout = view.findViewById(R.id.checkout);

        NestedScrollView nested_content = findViewById(R.id.nested_scroll_view);
        nested_content.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY < oldScrollY) { // up
                // animateSearchBar(false);
                animateBottomAnchor(false);
            }
            if (scrollY > oldScrollY) { // down
                // animateSearchBar(true);
                animateBottomAnchor(true);
            }
        });

        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        getFavourites();
                    }
                }, 3000);
            }
        });

        shop_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(FavouriteActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }

    boolean isBottomAnchorNavigationHide = false;

    private void animateBottomAnchor(final boolean hide) {
        if (isBottomAnchorNavigationHide && hide || !isBottomAnchorNavigationHide && !hide) return;
        isBottomAnchorNavigationHide = hide;
        int moveY = hide ? (2 * bottom_anchor.getHeight()) : 0;
        bottom_anchor.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    private void getFavourites(){
        FavouriteList = new ArrayList<>();
        FavouriteList = getFavourite();
        if (!FavouriteList.isEmpty()) {
            no_result_found.setVisibility(View.GONE);
            adapter = new FavouriteAdapter(FavouriteActivity.this, FavouriteList);
            recyclerView.setAdapter(adapter);
            for(int i=0; i<FavouriteList.size(); i++) {
                if (FavouriteList.get(i).getItemQuantity() <= 1 && FavouriteList.size() <= 1) {

                }
            }
        }else{
            no_result_found.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onRestart() {
        getFavourites();
        super.onRestart();
    }

    @Override
    protected void onResume() {
        getFavourites();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        this.finish();
        startActivity(new Intent(FavouriteActivity.this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater ( );
        menuInflater.inflate ( R.menu.clear_cart_menu , menu );
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            startActivity(new Intent(FavouriteActivity.this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        }else if ( id == R.id.ic_delete ) {
            if(!FavouriteList.isEmpty()) {
              //  alertDialog();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.CartViewHolder> {
        private Context mCtx;
        private List<Cart> FavouriteList;

        public FavouriteAdapter(Context mCtx, List<Cart> FavouriteList) {
            this.mCtx = mCtx;
            this.FavouriteList = FavouriteList;
        }


        @Override
        public FavouriteAdapter.CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_cart, parent, false);
            return new FavouriteAdapter.CartViewHolder(view);
        }


        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(FavouriteAdapter.CartViewHolder holder, int position) {
            if(FavouriteList!=null){
                localStorage = new LocalStorage(mCtx);
                gson = new Gson();
                Cart cart = FavouriteList.get(position);
                holder.itemName.setText(cart.getName());
                holder.itemDesc.setText(cart.getDesc());
                if(cart.getItemQuantity()>1){
                    holder.itemQuantity.setText(""+cart.getItemQuantity());
                    holder.itemTotalAmt.setText("₹ "+cart.getTotalAmount());
                }else{
                    holder.itemTotalAmt.setText("₹ "+cart.getSellRate());
                    holder.itemQuantity.setText(""+cart.getItemQuantity());
                }
                if(cart.getWeight()!=null){
                    holder.item_weight.setVisibility(View.VISIBLE);
                    holder.item_weight.setText(cart.getWeight());
                }else{
                    holder.item_weight.setVisibility(View.GONE);
                }
                holder.itemImage.setAnimation(AnimationUtils.loadAnimation(mCtx,R.anim.fade_transition_animation));
                Picasso.with(mCtx).load(cart.getLink()).into(holder.itemImage);

                holder.itemDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View currentFocus = ((Activity)mCtx).getCurrentFocus();
                        if (currentFocus != null) {
                            currentFocus.clearFocus();
                        }

                        FavouriteList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, FavouriteList.size());
                        Gson gson = new Gson();
                        String cartStr = gson.toJson(FavouriteList);
                        Log.d("FAVOURITE", cartStr);
                        localStorage.setFavourite(cartStr);
                       //((CartActivity) mCtx).updateTotalPrice();
                        getFavourites();
                    }
                });
            }
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            return FavouriteList==null?0:FavouriteList.size();
        }

        class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView itemName, itemDesc, itemTotalAmt,itemAdd,itemRemove,itemQuantity,itemDelete,item_weight;
            ImageView itemImage;
            CardView cardlist_item;

            public CartViewHolder(View itemView) {
                super(itemView);
                itemName = itemView.findViewById(R.id.item_name);
                itemDesc = itemView.findViewById(R.id.item_short_desc);
                itemTotalAmt = itemView.findViewById(R.id.total_Amount);
                itemImage = itemView.findViewById(R.id.product_thumb);
                itemAdd = itemView.findViewById(R.id.add_item);
                itemRemove = itemView.findViewById(R.id.remove_item);
                itemQuantity = itemView.findViewById(R.id.item_count);
                itemDelete = itemView.findViewById(R.id.item_delete);
                item_weight = itemView.findViewById(R.id.item_weight);
                cardlist_item = itemView.findViewById(R.id.cardlist_item);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
            }
        }
    }
}