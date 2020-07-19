package com.test.sample.hirecooks.Activity.Cart;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.kinda.alert.KAlertDialog;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Activity.CheckOut.CheckoutActivity;
import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Models.Cart.Cart;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.LocalStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CartActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private TextView item_count,checkout_amount,checkout;
    private FrameLayout empty_cart_img;
    private AppCompatButton shop_now;
    private View appRoot,bottom_anchor;
    private List<Cart> cartList;
    private RelativeLayout bottom_anchor_layout;
    private int Quantity = 0, sellRate = 0,displayRate = 0, SubTotal = 0,weight = 0;
    private LocalStorage localStorage;
    private Gson gson;
    private CartAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Cart");
        initViews();
        getCart();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerview_cart);
        appRoot = findViewById(R.id.appRoot);
        bottom_anchor_layout = findViewById(R.id.bottom_anchor_layout);
        empty_cart_img = findViewById(R.id.empty_cart_img);
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
                        getCart();
                    }
                }, 3000);
            }
        });

        shop_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(CartActivity.this, MainActivity.class)
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

    private void getCart() {
        cartList = new ArrayList<>();
        cartList = getCartList();
        if(!cartList.isEmpty()){
            animateBottomAnchor(false);
            bottom_anchor_layout.setVisibility(View.VISIBLE);
            checkout_amount.setText("₹  "+getTotalPrice());
            empty_cart_img.setVisibility(View.GONE);
            item_count.setText(""+cartCount());
            adapter = new CartAdapter(CartActivity.this, cartList);
            recyclerView.setAdapter(adapter);
            System.out.println("Suree wieght: "+ cartList.get(0).getWeight());
        }else{
            bottom_anchor_layout.setVisibility(View.GONE);
            empty_cart_img.setVisibility(View.VISIBLE);
        }

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CartActivity.this, CheckoutActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }

    @Override
    protected void onRestart() {
        getCart();
        super.onRestart();
    }

    @Override
    protected void onResume() {
        getCart();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        this.finish();
        startActivity(new Intent(CartActivity.this, MainActivity.class)
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
            startActivity(new Intent(CartActivity.this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        }else if ( id == R.id.ic_delete ) {
           if(!cartList.isEmpty()) {
               alertDialog();
           }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void alertDialog() {
        KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.SUCCESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        new KAlertDialog(this, KAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Won't be able to recover products!")
                .setConfirmText("Yes,delete it!")
                .setConfirmClickListener(new KAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(KAlertDialog sDialog) {
                        sDialog
                                .setTitleText("Deleted!")
                                .setContentText("Your products has been deleted!")
                                .setConfirmText("OK")
                                .setConfirmClickListener(null)
                                .changeAlertType(KAlertDialog.SUCCESS_TYPE);
                        if(cartList.size()!=0&&cartList!=null){
                            localStorage.deleteCart();
                            adapter.notifyDataSetChanged();
                            empty_cart_img.setVisibility(View.VISIBLE);
                            /* mState = "HIDE_MENU";*/
                            invalidateOptionsMenu();
                            pDialog.dismiss();
                        }
                    }
                })
                .show();
    }

    public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
        private Context mCtx;
        private List<Cart> cartList;

        public CartAdapter(Context mCtx, List<Cart> cartList) {
            this.mCtx = mCtx;
            this.cartList = cartList;
        }


        @Override
        public CartAdapter.CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_cart, parent, false);
            return new CartAdapter.CartViewHolder(view);
        }


        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(CartAdapter.CartViewHolder holder, int position) {
            if(cartList!=null){
                localStorage = new LocalStorage(mCtx);
                gson = new Gson();
                Cart cart = cartList.get(position);
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

                holder.itemAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sellRate = cart.getSellRate();
                        displayRate = cart.getDisplayRate();
                        String count = holder.itemQuantity.getText().toString();
                        Quantity = Integer.parseInt(count);
                        if (Quantity >= 1) {
                            Quantity++;
                            holder.itemQuantity.setText(""+(Quantity));
                            for (int i = 0; i < cartList.size(); i++) {
                                if (cartList.get(i).getId()==cart.getId()&&cartList.get(i).getName().equalsIgnoreCase(cart.getName())&&cartList.get(i).getSellRate()==cart.getSellRate()) {
                                    SubTotal = (sellRate * Quantity);
                                    cartList.get(i).setItemQuantity(Quantity);
                                    cartList.get(i).setTotalAmount(SubTotal);
                                    String cartStr = gson.toJson(cartList);
                                    localStorage.setCart(cartStr);
                                    notifyItemChanged(position);
                                    getCart();
                                }
                            }
                        }
                    }
                });

                holder.itemRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sellRate = cart.getSellRate();
                        displayRate = cart.getDisplayRate();
                        String count = holder.itemQuantity.getText().toString();
                        Quantity = Integer.parseInt(count);
                        if (Quantity > 1) {
                            Quantity--;
                            holder.itemQuantity.setText(""+(Quantity));
                            for (int i = 0; i < cartList.size(); i++) {
                                if (cartList.get(i).getId()==cart.getId()&&cartList.get(i).getName().equalsIgnoreCase(cart.getName())&&cartList.get(i).getSellRate()==cart.getSellRate()) {
                                    SubTotal = (sellRate * Quantity);
                                    cartList.get(i).setItemQuantity(Quantity);
                                    cartList.get(i).setTotalAmount(SubTotal);
                                    String cartStr = gson.toJson(cartList);
                                    localStorage.setCart(cartStr);
                                    notifyItemChanged(position);
                                    getCart();
                                }
                            }
                        }
                    }
                });

                holder.itemDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View currentFocus = ((Activity)mCtx).getCurrentFocus();
                        if (currentFocus != null) {
                            currentFocus.clearFocus();
                        }
                        cartList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, cartList.size());
                        Gson gson = new Gson();
                        String cartStr = gson.toJson(cartList);
                        Log.d("CART", cartStr);
                        localStorage.setCart(cartStr);
                        ((CartActivity) mCtx).updateTotalPrice();
                        getCart();
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
            return cartList==null?0:cartList.size();
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