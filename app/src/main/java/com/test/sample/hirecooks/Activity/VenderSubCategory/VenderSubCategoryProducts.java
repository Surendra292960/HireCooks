package com.test.sample.hirecooks.Activity.VenderSubCategory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
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
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Activity.AddorRemoveCallbacks;
import com.test.sample.hirecooks.Activity.ProductDatails.DetailsActivity;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.Cart.Cart;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.SubCategory.Response.SubCategories;
import com.test.sample.hirecooks.Models.SubCategory.Response.SubCategory;
import com.test.sample.hirecooks.Models.UsersResponse.UserResponse;
import com.test.sample.hirecooks.Models.VendersCategory.VendersCategory;
import com.test.sample.hirecooks.Models.VendersSubCategory.VendersSubcategory;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.LocalStorage;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.WebApis.ProductApi;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VenderSubCategoryProducts extends BaseActivity implements MaterialSearchBar.OnSearchActionListener{
    private RecyclerView vender_subcategory_products_recycler;
    private VendersCategory vendersCategory;
    private ProgressBarUtil progressBarUtil;
    private List<SubCategory> vendersSubcategoryList;
    private TextView item_count,checkout_amount,checkout;
    private List<Cart> cartList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String categoryName;
    private View bottom_anchor;
    private FrameLayout no_result_found,no_search_result_found;
    private RelativeLayout bottom_anchor_layout;
    private MaterialSearchBar searchBar;
    List<String> suggestList=new ArrayList<>();
    List<VendersSubcategory> localDataSource=new ArrayList<>();
    private VenderSubCategoryAdapter adapter;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vender_sub_category_products);
        initViews();
        getCart();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null&& Constants.NEARBY_VENDERS_LOCATION !=null) {
            vendersCategory = (VendersCategory) bundle.getSerializable("VendersCategory");
            if (vendersCategory != null) {
                getVendersSubCategoryProducts(vendersCategory.getCategoryid());
            }
        }
    }

    private void initViews() {
        progressBarUtil = new ProgressBarUtil(this);
        vender_subcategory_products_recycler = findViewById(R.id.vender_subcategory_products_recycler);
        no_result_found = findViewById(R.id.no_result_found);
        no_search_result_found = findViewById(R.id.no_search_result_found);
        bottom_anchor_layout = findViewById(R.id.bottom_anchor_layout);
        searchBar = findViewById(R.id.searchBar);
        drawer = findViewById(R.id.drawer_layout);

        searchBar.setHint("Search ");
        searchBar.setCardViewElevation(10);
        searchBar.setSpeechMode(true);
        searchBar.setOnSearchActionListener(this);
        searchBar.setSpeechMode(true);
        searchBar.setRoundedSearchBarEnabled(true);

        View view = findViewById(R.id.footerView);
        item_count =  view.findViewById(R.id.item_count);
        bottom_anchor =  view.findViewById(R.id.bottom_anchor);
        checkout_amount = view.findViewById(R.id.checkout_amount);
        checkout = view.findViewById(R.id.checkout);

        NestedScrollView nested_content = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        nested_content.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < oldScrollY) { // up
                    // animateSearchBar(false);
                    animateBottomAnchor(false);
                }
                if (scrollY > oldScrollY) { // down
                    // animateSearchBar(true);
                    animateBottomAnchor(true);
                }
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
                        if (vendersCategory != null) {
                            getVendersSubCategoryProducts(vendersCategory.getCategoryid());
                        }
                    }
                }, 3000);
            }
        });

        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest = new ArrayList<>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                searchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled)
                    vender_subcategory_products_recycler.setAdapter(adapter); // restores full list of drinks
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

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

    private void startSearch(CharSequence text) {
        List<SubCategory>  subCategoryFilteredList = new ArrayList<>();
        List<SubCategory>  filteredList = new ArrayList<>();
        List<SubCategory>  list = new ArrayList<>();
        List<SubCategory> result=new ArrayList<>();
        try{
            for (SubCategory vendersSubcategory : vendersSubcategoryList) {
                for (Map map : Constants.NEARBY_VENDERS_LOCATION) {
                    for(UserResponse vender:Constants.NEARBY_VENDERS){
                        if (map.getFirm_id().equalsIgnoreCase(vendersSubcategory.getFirm_id())&&vender.getFirmId().equalsIgnoreCase(vendersSubcategory.getFirm_id())) {
                            list.add(vendersSubcategory);
                            Set<SubCategory> newList = new LinkedHashSet<>(list);
                            filteredList = new ArrayList<>(newList);
                        }
                    }
                }
            }

            if(filteredList!=null&&filteredList.size()!=0){
                for(int i=0;i<filteredList.size();i++) {
                    String cityName = "";

                    if(filteredList.get (i).getName()!=null){
                        cityName= filteredList.get (i).getName();
                    }

                    if(cityName.toLowerCase().contains(String.valueOf(text).toLowerCase())) {
                        subCategoryFilteredList.add(filteredList.get(i));
                      /*  for(VendersSubcategoryActivity product:localDataSource)
                            if(product.getName().contains(text))
                                result.add(product);*/
                    }
                }

                if(subCategoryFilteredList.size()!=0&&subCategoryFilteredList!=null){
                    vender_subcategory_products_recycler.setHasFixedSize(true);
                    no_search_result_found.setVisibility(View.GONE);
                    vender_subcategory_products_recycler.setLayoutManager(new GridLayoutManager(VenderSubCategoryProducts.this, 2));
                    adapter = new VenderSubCategoryAdapter(VenderSubCategoryProducts.this, subCategoryFilteredList);
                    vender_subcategory_products_recycler.setAdapter (adapter);
                    adapter.notifyDataSetChanged();
                }else{
                    no_search_result_found.setVisibility(View.VISIBLE);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
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

    private void getVendersSubCategoryProducts(final int subcategory) {
        progressBarUtil.showProgress();
        ProductApi mService = ApiClient.getClient().create(ProductApi.class);
        Call<SubCategories> call = mService.getSubCategory(subcategory);
        call.enqueue(new Callback<SubCategories>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<SubCategories> call, Response<SubCategories> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    vendersSubcategoryList = response.body().getSubcategory();
                    progressBarUtil.hideProgress();
                    if(vendersSubcategoryList!=null&&vendersSubcategoryList.size()!=0){
                        List<SubCategory> list = new ArrayList<>();
                        List<SubCategory> filteredList = new ArrayList<>();
                        for (SubCategory vendersSubcategory : vendersSubcategoryList) {
                            for (Map map : Constants.NEARBY_VENDERS_LOCATION) {
                                for(UserResponse vender:Constants.NEARBY_VENDERS){
                                    if (map.getFirm_id().equalsIgnoreCase(vendersSubcategory.getFirm_id())&&vender.getFirmId().equalsIgnoreCase(vendersSubcategory.getFirm_id())) {
                                        list.add(vendersSubcategory);
                                        Set<SubCategory> newList = new LinkedHashSet<>(list);
                                        filteredList = new ArrayList<>(newList);
                                    }
                                }
                            }
                        }
                        if(filteredList!=null&&filteredList.size()!=0){
                            Constants.SUBCATEGORY = filteredList;
                           /* displayLastDrink(filteredList);
                            buildSuggestList(filteredList);*/
                            vender_subcategory_products_recycler.setHasFixedSize(true);
                            no_result_found.setVisibility(View.GONE);
                            vender_subcategory_products_recycler.setVisibility(View.VISIBLE);
                            vender_subcategory_products_recycler.setLayoutManager(new GridLayoutManager(VenderSubCategoryProducts.this, 2));
                            adapter = new VenderSubCategoryAdapter(VenderSubCategoryProducts.this, filteredList);
                            vender_subcategory_products_recycler.setAdapter(adapter);
                            adapter.notifyDataSetChanged();


                        }else{
                            vender_subcategory_products_recycler.setVisibility(View.GONE);
                        }

                    }else{
                        no_result_found.setVisibility(View.VISIBLE);
                    }

                } else {
                    Toast.makeText(VenderSubCategoryProducts.this, R.string.failed_due_to + statusCode, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SubCategories> call, Throwable t) {
                progressBarUtil.hideProgress();
                System.out.println("Suree : " + t.getMessage());
            }
        });
    }

 /*   private void buildSuggestList(List<VendersSubcategoryActivity> products) {
        for(VendersSubcategoryActivity product:products)
            suggestList.add(product.getName());
        searchBar.setLastSuggestions(suggestList);
    }

    private void displayLastDrink(List<VendersSubcategoryActivity> products) {
        localDataSource=products;
        adapter=new VenderSubCategoryAdapter(this,products);
        vender_subcategory_products_recycler.setAdapter(adapter);
    }*/

    @Override
    protected void onResume() {
        getCart();
        getVendersSubCategoryProducts(vendersCategory.getCategoryid());
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        no_result_found.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            no_result_found.setVisibility(View.GONE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        String s = enabled ? "enabled" : "disabled";
        Toast.makeText(VenderSubCategoryProducts.this, "Search " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        startSearch(text.toString(), true, null, true);
    }

    @Override
    public void onButtonClicked(int buttonCode) {
        switch (buttonCode) {
            case MaterialSearchBar.BUTTON_NAVIGATION:
                drawer.openDrawer(GravityCompat.START);
                break;
            case MaterialSearchBar.BUTTON_SPEECH:
                break;
            case MaterialSearchBar.BUTTON_BACK:
                searchBar.closeSearch();
                break;
        }
    }

    public class VenderSubCategoryAdapter extends RecyclerView.Adapter<VenderSubCategoryProducts.VenderSubCategoryAdapter.MyViewHolder> {
        List<SubCategory> productList;
        Context mCtx;
        String Tag;
        LocalStorage localStorage;
        Gson gson;
        List<Cart> cartList = new ArrayList<>();
        String weight;
        private int discount = 0, discountPercentage = 0, displayrate = 0,sellRate = 0,displayRate = 0, SubTotal = 0,Quantity = 0;

        public VenderSubCategoryAdapter(Context mCtx, List<SubCategory> productList) {
            this.productList = productList;
            this.mCtx = mCtx;
        }

        @NonNull
        @Override
        public VenderSubCategoryProducts.VenderSubCategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subcategory_cardview, parent, false);
            return new VenderSubCategoryProducts.VenderSubCategoryAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final VenderSubCategoryProducts.VenderSubCategoryAdapter.MyViewHolder holder, final int position) {

            final SubCategory product = productList.get(position);
            localStorage = new LocalStorage(mCtx);
            gson = new Gson();
            cartList = ((BaseActivity) mCtx).getCartList();

            if(product!=null){
                if(product.getStock()==1){
                    holder.add_item_layout.setVisibility(View.VISIBLE);
                }else{
                    holder.add_item_layout.setVisibility(View.GONE);
                }
                holder.name.setText(product.getName());
                holder.discription.setText(product.getDiscription());
                Picasso.with(mCtx).load(product.getLink()).into(holder.imageView);

                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //startActivity(new Intent(mCtx, SubCategoryDetails.class));
                    }
                });

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
                                getCart();

                            }
                        }
                    }else {
                        if (product.getId() != 0 && product.getName() != null && product.getLink() != null && product.getDiscription() != null && sellRate != 0 && displayrate != 0 && product.getFirm_id() != null) {
                            sellRate = product.getSellRate();
                            displayRate = product.getDisplayRate();
                            if (Quantity == 0) {
                                Quantity = 1;
                                SubTotal = (sellRate * Quantity);
                                if (mCtx instanceof VenderSubCategoryProducts) {
                                    Cart cart = new Cart(product.getId(), product.getName(),  product.getLink(), product.getDiscription(), sellRate, displayRate, SubTotal, 1, weight, product.getFirm_id());
                                    cartList = ((BaseActivity) mCtx).getCartList();
                                    cartList.add(cart);
                                    String cartStr = gson.toJson(cartList);
                                    localStorage.setCart(cartStr);
                                    ((AddorRemoveCallbacks) mCtx).onAddProduct();
                                    notifyItemChanged(position);
                                    getCart();

                                }
                            } else {
                                Toast.makeText(mCtx, "Please Add Quantity", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(mCtx, "This item can`t be add please remove item and try again", Toast.LENGTH_SHORT).show();
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

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(mCtx, DetailsActivity.class);
                    bundle.putSerializable("SubCategory",productList.get(position));
                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mCtx.startActivity(intent);
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
            TextView discount, name, sellrate, quantity, displayRate,available_stock,not_available,discription;
            ImageView add_item, remove_item;
            LinearLayout add_item_layout;
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
                available_stock = itemView.findViewById(R.id.available_stock);
                cardview = itemView.findViewById(R.id.cardview);
                discription = itemView.findViewById(R.id.item_descriptions);
            }
        }
    }

}
