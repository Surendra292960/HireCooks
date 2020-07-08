package com.test.sample.hirecooks.Activity.ProductDatails;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Activity.AddorRemoveCallbacks;
import com.test.sample.hirecooks.Activity.CheckOut.CheckoutActivity;
import com.test.sample.hirecooks.BaseActivity;
import com.test.sample.hirecooks.Models.Cart.Cart;
import com.test.sample.hirecooks.Models.OfferSubCategory.OffersSubcategory;
import com.test.sample.hirecooks.Models.SubCategory.Response.SubCategory;
import com.test.sample.hirecooks.Models.VendersSubCategory.VendersSubcategory;
import com.test.sample.hirecooks.Models.users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.LocalStorage;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductDetailsActivity extends BaseActivity {
    private RelativeLayout bottom_anchor_layout;
    private TextView item_count,checkout_amount,checkout,item_short_desc,item_sellrate,item_name;
    private List<Cart> cartList;
    private FrameLayout no_result_found, no_vender_found,no_search_result_found;
    private User user;
    private RecyclerView recyclerView;
    private SubCategory subCategory;
    private VendersSubcategory vendersSubCategory;
    private OffersSubcategory offerSubCategory;
    private SubCategoryHorizontalAdapter adapter;
    private ImageView subcategory_image;
    private  ImageView collapsingToolbarImageView;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        initViews();
        getCart();
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            subCategory = (SubCategory)bundle.getSerializable("SubCategory");
            vendersSubCategory = (VendersSubcategory)bundle.getSerializable("VendersSubCategory");
            offerSubCategory = (OffersSubcategory)bundle.getSerializable("OfferSubCategory");
            if(subCategory!=null||offerSubCategory!=null||vendersSubCategory!=null){
                getData();
            }
        }
    }

    private void getData() {
        if(subCategory!=null){
            item_name.setText(subCategory.getName());
            item_sellrate.setText("\u20B9  "+subCategory.getSellRate());
            item_short_desc.setText(subCategory.getDiscription());
            Picasso.with(this).load (subCategory.getLink()).placeholder(R.drawable.no_image).error(R.drawable.no_image).into (collapsingToolbarImageView);
            Collections.reverse(Constants.SUBCATEGORY);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(ProductDetailsActivity.this));
            adapter = new SubCategoryHorizontalAdapter(ProductDetailsActivity.this, Constants.SUBCATEGORY);
            recyclerView.setAdapter(adapter);
        }else if(vendersSubCategory!=null){
            item_name.setText(vendersSubCategory.getName());
            item_sellrate.setText("\u20B9  "+vendersSubCategory.getSellRate());
            item_short_desc.setText(vendersSubCategory.getDiscription());
            Picasso.with(this).load (vendersSubCategory.getLink()).placeholder(R.drawable.no_image).error(R.drawable.no_image).into (collapsingToolbarImageView);
            Collections.reverse(Constants.NEARBY_VENDERS_SUBCATEGORY);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(ProductDetailsActivity.this));
            VendersSubCategoryHorizontalAdapter adapter = new VendersSubCategoryHorizontalAdapter(ProductDetailsActivity.this, Constants.NEARBY_VENDERS_SUBCATEGORY);
            recyclerView.setAdapter(adapter);
        }else if(offerSubCategory!=null){
            item_name.setText(offerSubCategory.getName());
            item_sellrate.setText("\u20B9  "+offerSubCategory.getSellRate());
            item_short_desc.setText(offerSubCategory.getDiscription());
            Picasso.with(this).load (offerSubCategory.getLink()).placeholder(R.drawable.no_image).error(R.drawable.no_image).into (collapsingToolbarImageView);
            Collections.reverse(Constants.OFFER_SUBCATEGORY);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(ProductDetailsActivity.this));
            OfferSubCategoryHorizontalAdapter adapter = new OfferSubCategoryHorizontalAdapter(ProductDetailsActivity.this, Constants.OFFER_SUBCATEGORY);
            recyclerView.setAdapter(adapter);
        }
    }

    private void initViews() {
        user = SharedPrefManager.getInstance(this).getUser();
        recyclerView = findViewById(R.id.subcategory_recycler);
        no_result_found = findViewById(R.id.no_result_found);
        no_search_result_found = findViewById(R.id.no_search_result_found);
        bottom_anchor_layout = findViewById(R.id.bottom_anchor_layout);
        subcategory_image = findViewById(R.id.subcategory_image);
        item_sellrate = findViewById(R.id.item_sellrate);
        item_name = findViewById(R.id.item_name);
        item_short_desc = findViewById(R.id.item_short_desc);

        View view = findViewById(R.id.footerView);
        item_count = view.findViewById(R.id.item_count);
        checkout_amount = view.findViewById(R.id.checkout_amount);
        checkout = view.findViewById(R.id.checkout);


        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarImageView = findViewById(R.id.subcategory_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);
        //setTitle(Constants.VENDER_NAME);

        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });
         checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductDetailsActivity.this, CheckoutActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }

    private void getCart() {
        cartList = new ArrayList<>();
        cartList = getCartList();
        if (!cartList.isEmpty()) {
            for(int i=0; i<cartList.size(); i++){
                if(cartList.get(i).getItemQuantity()<=1&&cartList.size()<=1){
                    bottom_anchor_layout.setAnimation(AnimationUtils.loadAnimation(this,R.anim.fade_transition_animation));
                }
            }
            bottom_anchor_layout.setVisibility(View.VISIBLE);
            checkout_amount.setText("₹  " + getTotalPrice());
            item_count.setText("" + cartCount());

        } else {
            bottom_anchor_layout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class OfferSubCategoryHorizontalAdapter extends RecyclerView.Adapter<ProductDetailsActivity.OfferSubCategoryHorizontalAdapter.MyViewHolder> {
        List<OffersSubcategory> productList;
        Context context;
        LocalStorage localStorage;
        Gson gson;
        List<Cart> cartList = new ArrayList<>();
        String weight;
        private int discount = 0, discountPercentage = 0, displayrate = 0,sellRate = 0,displayRate = 0, SubTotal = 0,Quantity = 0;

        public OfferSubCategoryHorizontalAdapter(Context context, List<OffersSubcategory> productList) {
            this.productList = productList;
            this.context = context;
        }

        @NonNull
        @Override
        public ProductDetailsActivity.OfferSubCategoryHorizontalAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal_layout, parent, false);
            return new ProductDetailsActivity.OfferSubCategoryHorizontalAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final ProductDetailsActivity.OfferSubCategoryHorizontalAdapter.MyViewHolder holder, final int position) {

            final OffersSubcategory product = productList.get(position);
            localStorage = new LocalStorage(context);
            gson = new Gson();
            cartList = ((BaseActivity) context).getCartList();

            if(product!=null){
                if(product.getStock()==1){
                   // holder.add_item_layout.setVisibility(View.VISIBLE);
                    holder.add_.setVisibility(View.VISIBLE);
                    holder.item_not_in_stock.setVisibility(View.GONE);
                }else{
                   // holder.add_item_layout.setVisibility(View.GONE);
                    holder.add_.setVisibility(View.GONE);
                    holder.item_not_in_stock.setVisibility(View.VISIBLE);
                }

                holder.name.setText(product.getName());
                holder.discription.setText(product.getDiscription());
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
                        holder.add_.setVisibility(View.GONE);
                        holder.quantity_ll.setVisibility(View.VISIBLE);
                        holder.quantity.setText(""+cartList.get(i).getItemQuantity());
                        Quantity = cartList.get(i).getItemQuantity();
                        sellRate = product.getSellRate();
                        SubTotal = (sellRate * Quantity);
                    }
                }
            } else {

                holder.quantity.setText("0");
            }

            holder.add_.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.add_.setVisibility(View.GONE);
                    holder.quantity_ll.setVisibility(View.VISIBLE);
                    sellRate = product.getSellRate();
                    displayRate = product.getDisplayRate();
                    Quantity = 1;
                    if (product.getId() != 0 && product.getName() != null && product.getLink() != null && product.getDiscription() != null && sellRate != 0 && displayrate != 0 && product.getFirm_id() != null) {
                        SubTotal = (sellRate * Quantity);
                        if (context instanceof ProductDetailsActivity) {
                            Cart cart = new Cart(product.getId(), product.getName(),  product.getLink(), product.getDiscription(), sellRate, displayRate, SubTotal, 1, weight, product.getFirm_id());
                            cartList = ((BaseActivity) context).getCartList();
                            cartList.add(cart);
                            String cartStr = gson.toJson(cartList);
                            localStorage.setCart(cartStr);
                            ((AddorRemoveCallbacks) context).onAddProduct();
                            notifyItemChanged(position);
                            getCart();
                        }
                    }else{
                        holder.add_.setVisibility(View.VISIBLE);
                        holder.quantity_ll.setVisibility(View.GONE);
                        Toast.makeText(context, "This item can`t be add please remove item and try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });

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
                                getCart();

                            }
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
                                getCart();
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
            TextView discount, name, sellrate, quantity, displayRate,item_not_in_stock,discription,add_;
            TextView add_item, remove_item;
            LinearLayout add_item_layout,quantity_ll;
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
                item_not_in_stock = itemView.findViewById(R.id.item_not_in_stock);
                // available_stock = itemView.findViewById(R.id.available_stock);
                cardview = itemView.findViewById(R.id.cardview);
                //   not_available = itemView.findViewById(R.id.not_available);
                discription = itemView.findViewById(R.id.item_short_desc);
            }
        }
    }

    public class SubCategoryHorizontalAdapter extends RecyclerView.Adapter<ProductDetailsActivity.SubCategoryHorizontalAdapter.MyViewHolder> {
        List<SubCategory> productList;
        Context context;
        LocalStorage localStorage;
        Gson gson;
        List<Cart> cartList = new ArrayList<>();
        String weight;
        private int discount = 0, discountPercentage = 0, displayrate = 0,sellRate = 0,displayRate = 0, SubTotal = 0,Quantity = 0;

        public SubCategoryHorizontalAdapter(Context context, List<SubCategory> productList) {
            this.productList = productList;
            this.context = context;
        }

        @NonNull
        @Override
        public ProductDetailsActivity.SubCategoryHorizontalAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal_layout, parent, false);
            return new ProductDetailsActivity.SubCategoryHorizontalAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final ProductDetailsActivity.SubCategoryHorizontalAdapter.MyViewHolder holder, final int position) {

            final SubCategory product = productList.get(position);
            localStorage = new LocalStorage(context);
            gson = new Gson();
            cartList = ((BaseActivity) context).getCartList();

            if(product!=null){
                if(product.getStock()==1){
                    // holder.add_item_layout.setVisibility(View.VISIBLE);
                    holder.add_.setVisibility(View.VISIBLE);
                    holder.item_not_in_stock.setVisibility(View.GONE);
                }else{
                    // holder.add_item_layout.setVisibility(View.GONE);
                    holder.add_.setVisibility(View.GONE);
                    holder.item_not_in_stock.setVisibility(View.VISIBLE);
                }

                if(product.getAvailableStock()>0){
                    holder.add_item_layout.setVisibility(View.VISIBLE);
                   // holder.available_stock.setVisibility(View.VISIBLE);
                   // holder.not_available.setVisibility(View.GONE);
                   // holder.available_stock.setText("Available Stock: "+product.getAvailableStock());
                }else{
                   // holder.available_stock.setVisibility(View.GONE);
                    holder.not_available.setVisibility(View.VISIBLE);
                    holder.add_item_layout.setVisibility(View.GONE);
                    holder.add_item_layout.setVisibility(View.GONE);
                }

                holder.name.setText(product.getName());
                holder.discription.setText(product.getDiscription());
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
                        holder.add_.setVisibility(View.GONE);
                        holder.quantity_ll.setVisibility(View.VISIBLE);
                        holder.quantity.setText(""+cartList.get(i).getItemQuantity());
                        Quantity = cartList.get(i).getItemQuantity();
                        sellRate = product.getSellRate();
                        SubTotal = (sellRate * Quantity);
                    }
                }
            } else {

                holder.quantity.setText("0");
            }

            holder.add_.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.add_.setVisibility(View.GONE);
                    holder.quantity_ll.setVisibility(View.VISIBLE);
                    sellRate = product.getSellRate();
                    displayRate = product.getDisplayRate();
                    Quantity = 1;
                    if (product.getId() != 0 && product.getName() != null && product.getLink() != null && product.getDiscription() != null && sellRate != 0 && displayrate != 0 && product.getFirm_id() != null) {
                        SubTotal = (sellRate * Quantity);
                        if (context instanceof ProductDetailsActivity) {
                            Cart cart = new Cart(product.getId(), product.getName(),  product.getLink(), product.getDiscription(), sellRate, displayRate, SubTotal, 1, weight, product.getFirm_id());
                            cartList = ((BaseActivity) context).getCartList();
                            cartList.add(cart);
                            String cartStr = gson.toJson(cartList);
                            localStorage.setCart(cartStr);
                            ((AddorRemoveCallbacks) context).onAddProduct();
                            notifyItemChanged(position);
                            getCart();
                        }
                    }else{
                        holder.add_.setVisibility(View.VISIBLE);
                        holder.quantity_ll.setVisibility(View.GONE);
                        Toast.makeText(context, "This item can`t be add please remove item and try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });

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
                                getCart();

                            }
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
                                getCart();
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
            TextView discount, name, sellrate, quantity, displayRate,not_available,item_not_in_stock,discription,add_;
            TextView add_item, remove_item;
            LinearLayout add_item_layout,quantity_ll;
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
                item_not_in_stock = itemView.findViewById(R.id.item_not_in_stock);
               // available_stock = itemView.findViewById(R.id.available_stock);
                cardview = itemView.findViewById(R.id.cardview);
             //   not_available = itemView.findViewById(R.id.not_available);
                discription = itemView.findViewById(R.id.item_short_desc);
            }
        }
    }

    public class VendersSubCategoryHorizontalAdapter extends RecyclerView.Adapter<ProductDetailsActivity.VendersSubCategoryHorizontalAdapter.MyViewHolder> {
        List<VendersSubcategory> productList;
        Context context;
        LocalStorage localStorage;
        Gson gson;
        List<Cart> cartList = new ArrayList<>();
        String weight;
        private int discount = 0, discountPercentage = 0, displayrate = 0,sellRate = 0,displayRate = 0, SubTotal = 0,Quantity = 0;

        public VendersSubCategoryHorizontalAdapter(Context context, List<VendersSubcategory> productList) {
            this.productList = productList;
            this.context = context;
        }

        @NonNull
        @Override
        public ProductDetailsActivity.VendersSubCategoryHorizontalAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal_layout, parent, false);
            return new ProductDetailsActivity.VendersSubCategoryHorizontalAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final ProductDetailsActivity.VendersSubCategoryHorizontalAdapter.MyViewHolder holder, final int position) {

            final VendersSubcategory product = productList.get(position);
            localStorage = new LocalStorage(context);
            gson = new Gson();
            cartList = ((BaseActivity) context).getCartList();

            if(product!=null){
                if(product.getStock()==1){
                    // holder.add_item_layout.setVisibility(View.VISIBLE);
                    holder.add_.setVisibility(View.VISIBLE);
                    holder.item_not_in_stock.setVisibility(View.GONE);
                }else{
                    // holder.add_item_layout.setVisibility(View.GONE);
                    holder.add_.setVisibility(View.GONE);
                    holder.item_not_in_stock.setVisibility(View.VISIBLE);
                }

                holder.name.setText(product.getName());
                holder.discription.setText(product.getDiscription());
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
                        holder.add_.setVisibility(View.GONE);
                        holder.quantity_ll.setVisibility(View.VISIBLE);
                        holder.quantity.setText(""+cartList.get(i).getItemQuantity());
                        Quantity = cartList.get(i).getItemQuantity();
                        sellRate = product.getSellRate();
                        SubTotal = (sellRate * Quantity);
                    }
                }
            } else {

                holder.quantity.setText("0");
            }

            holder.add_.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.add_.setVisibility(View.GONE);
                    holder.quantity_ll.setVisibility(View.VISIBLE);
                    sellRate = product.getSellRate();
                    displayRate = product.getDisplayRate();
                    Quantity = 1;
                    if (product.getId() != 0 && product.getName() != null && product.getLink() != null && product.getDiscription() != null && sellRate != 0 && displayrate != 0 && product.getFirmId() != null) {
                        SubTotal = (sellRate * Quantity);
                        if (context instanceof ProductDetailsActivity) {
                            Cart cart = new Cart(product.getId(), product.getName(),  product.getLink(), product.getDiscription(), sellRate, displayRate, SubTotal, 1, weight, product.getFirmId());
                            cartList = ((BaseActivity) context).getCartList();
                            cartList.add(cart);
                            String cartStr = gson.toJson(cartList);
                            localStorage.setCart(cartStr);
                            ((AddorRemoveCallbacks) context).onAddProduct();
                            notifyItemChanged(position);
                            getCart();
                        }
                    }else{
                        holder.add_.setVisibility(View.VISIBLE);
                        holder.quantity_ll.setVisibility(View.GONE);
                        Toast.makeText(context, "This item can`t be add please remove item and try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });

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
                                getCart();

                            }
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
                                getCart();
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
            TextView discount, name, sellrate, quantity, displayRate,not_available,item_not_in_stock,discription,add_;
            TextView add_item, remove_item;
            LinearLayout add_item_layout,quantity_ll;
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
                item_not_in_stock = itemView.findViewById(R.id.item_not_in_stock);
                // available_stock = itemView.findViewById(R.id.available_stock);
                cardview = itemView.findViewById(R.id.cardview);
                //   not_available = itemView.findViewById(R.id.not_available);
                discription = itemView.findViewById(R.id.item_short_desc);
            }
        }
    }
}
