package com.test.sample.hirecooks.Activity.Search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Activity.AddorRemoveCallbacks;
import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.Activity.ProductDatails.ProductDetailsActivity;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.Cart.Cart;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.SearchSubCategory.Result;
import com.test.sample.hirecooks.Models.SearchSubCategory.Search;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.LocalStorage;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.WebApis.ProductApi;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultActivity extends BaseActivity {
    private EditText searchBar;
    private RecyclerView recyclerView;
    private List<Search> search;
    private List<Search> searchList;
    private Toolbar toolbar;
    private List<Cart> cartList;
    private ImageButton clear_text,back_button;
    private RelativeLayout bottom_anchor_layout;
    private View bottom_anchor;
    private TextView item_count,checkout_amount,checkout;
    private View searchbar_interface_layout;
    private FrameLayout no_result_found, no_vender_found,no_search_result_found;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_search_result);
        initViews();
        getCart();
        gsearchAllProducts();
    }

    private void initViews() {
        searchBar = findViewById(R.id.searchBar);
        recyclerView = findViewById(R.id.subcategory_recycler);
        no_result_found = findViewById(R.id.no_result_found);
        no_vender_found = findViewById(R.id.no_vender_found);
        toolbar = findViewById(R.id.toolbar);
        bottom_anchor_layout = findViewById(R.id.bottom_anchor_layout);
        no_search_result_found=findViewById(R.id.no_search_result_found);

        View views = findViewById(R.id.footerView);
        item_count =  views.findViewById(R.id.item_count);
        bottom_anchor =  views.findViewById(R.id.bottom_anchor);
        checkout_amount = views.findViewById(R.id.checkout_amount);
        checkout = views.findViewById(R.id.checkout);

        View search_view = findViewById(R.id.search_bar);
        searchBar=search_view.findViewById(R.id.searchBar);
        clear_text=search_view.findViewById(R.id.clear_text);
        back_button=search_view.findViewById(R.id.back_button);
        searchbar_interface_layout=search_view.findViewById(R.id.searchbar_interface_layout);

        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear_text.setVisibility(View.VISIBLE);
                back_button.setVisibility(View.VISIBLE);
            }
        });
        clear_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBar.setText("");
            }
        });
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back_button.setVisibility(View.GONE);
                startActivity(new Intent(SearchResultActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                startSearch(charSequence);
                if(charSequence.length() > 0){
                    clear_text.setVisibility(View.VISIBLE);
                }else{
                    clear_text.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                startSearch(charSequence);
                if(charSequence.length() > 0){
                    clear_text.setVisibility(View.VISIBLE);
                }else{
                    clear_text.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                startSearch(editable.toString());
                if(editable.toString().length() > 0){
                    clear_text.setVisibility(View.VISIBLE);
                }else{
                    clear_text.setVisibility(View.GONE);
                }
            }
        });

        NestedScrollView nested_content = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        nested_content.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < oldScrollY) { // up
                    animateBottomAnchor(false);
                    animateSearchBar(false);
                }
                if (scrollY > oldScrollY) { // down
                    animateBottomAnchor(true);
                    animateSearchBar(true);
                }
            }
        });

        getCart();
    }

    boolean isBottomAnchorNavigationHide = false;

    private void animateBottomAnchor(final boolean hide) {
        if (isBottomAnchorNavigationHide && hide || !isBottomAnchorNavigationHide && !hide) return;
        isBottomAnchorNavigationHide = hide;
        int moveY = hide ? (2 * bottom_anchor.getHeight()) : 0;
        bottom_anchor.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    boolean isSearchBarHide = false;

    private void animateSearchBar(final boolean hide) {
        if (isSearchBarHide && hide || !isSearchBarHide && !hide) return;
        isSearchBarHide = hide;
        int moveY = hide ? -(2 * searchbar_interface_layout.getHeight()) : 0;
        searchbar_interface_layout.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }


    private void startSearch(CharSequence text) {
        List<Search> filterList = new ArrayList<>();
        try {
            if (searchList != null && searchList.size() != 0) {
                for (int i = 0; i < searchList.size(); i++) {
                    String cityName = "";

                    if (searchList.get(i).getName() != null) {
                        cityName = searchList.get(i).getName();
                    }

                    if (cityName.toLowerCase().contains(String.valueOf(text).toLowerCase())) {
                        filterList.add(searchList.get(i));
                    }
                }

                if (filterList.size() != 0 && filterList != null) {
                    no_search_result_found.setVisibility(View.GONE);
                    recyclerView.setLayoutManager(new GridLayoutManager(SearchResultActivity.this,2));
                    SearchAdapter adapter = new SearchAdapter(SearchResultActivity.this, filterList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    no_search_result_found.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCart() {
        cartList = new ArrayList<>();
        cartList = getCartList();
        if (!cartList.isEmpty()) {
            animateBottomAnchor(false);
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

    private void gsearchAllProducts() {
        ProductApi mService = ApiClient.getClient().create(ProductApi.class);
        Call<Result> call = mService.searchProducts();
        call.enqueue(new Callback<Result>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    searchList = new ArrayList<>();
                    search = response.body().getSearch();
                    List<Search> list = new ArrayList<>();
                    List<Search> filteredList = new ArrayList<>();
                    if(search!=null&&search.size()!=0){
                        for(Search search:search){
                            for(Map map: Constants.NEARBY_VENDERS_LOCATION){
                                if(map.getFirm_id().equalsIgnoreCase(search.getFirmId())){
                                    list.add(search);
                                    Set<Search> newList = new LinkedHashSet<>(list);
                                    filteredList = new ArrayList<>(newList);
                                }
                            }
                        }
                        if(filteredList!=null&&filteredList.size()!=0){
                            Constants.SEARCH = filteredList;
                            searchList = filteredList;
                            no_vender_found.setVisibility(View.GONE);
                            no_result_found.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new GridLayoutManager(SearchResultActivity.this,2));
                            SearchAdapter adapter = new SearchAdapter(SearchResultActivity.this, filteredList);
                            recyclerView.setAdapter(adapter);
                        }else{
                            recyclerView.setVisibility(View.GONE);
                            no_vender_found.setVisibility(View.VISIBLE);
                        }
                    }else{
                        no_result_found.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(SearchResultActivity.this, R.string.failed_due_to + statusCode, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                System.out.println("Suree : " + t.getMessage());

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        gsearchAllProducts();
    }

    @Override
    public void onBackPressed() {
        this.finish();
        startActivity(new Intent(SearchResultActivity.this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


    public class SearchAdapter extends RecyclerView.Adapter<SearchResultActivity.SearchAdapter.MyViewHolder> {
        List<Search> productList;
        Context context;
        LocalStorage localStorage;
        Gson gson;
        List<Cart> cartList = new ArrayList<>();
        String weight;
        private int discount = 0, discountPercentage = 0, displayrate = 0,sellRate = 0,displayRate = 0, SubTotal = 0,Quantity = 0;

        public SearchAdapter(Context context, List<Search> productList) {
            this.productList = productList;
            this.context = context;
        }

        @NonNull
        @Override
        public SearchResultActivity.SearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subcategory_cardview, parent, false);
            return new SearchResultActivity.SearchAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final SearchResultActivity.SearchAdapter.MyViewHolder holder, final int position) {

            final Search product = productList.get(position);
            localStorage = new LocalStorage(context);
            gson = new Gson();
            cartList = ((BaseActivity) context).getCartList();

            if(product!=null){
                if(product.getStock()==1){
                    holder.add_item_layout.setVisibility(View.GONE);
                    // holder.add_item_layout.setVisibility(View.VISIBLE);
                }else{
                    holder.add_item_layout.setVisibility(View.GONE);
                }
                if(product!=null){
                    // holder.add_item_layout.setVisibility(View.VISIBLE);
                    holder.add_item_layout.setVisibility(View.GONE);
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
                                getCart();
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
                                    getCart();
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
                    Intent intent = new Intent(context, ProductDetailsActivity.class);
                    bundle.putSerializable("Search",productList.get(position));
                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
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
    }
}