package com.test.sample.hirecooks.Activity.Search;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StrikethroughSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.bumptech.glide.Glide;
import com.test.sample.hirecooks.Activity.AddorRemoveCallbacks;
import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.Activity.Orders.PlaceOrderActivity;
import com.test.sample.hirecooks.Activity.ProductDatails.DetailsActivity;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.SubCategory.Example;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.LocalStorage;
import com.test.sample.hirecooks.Utils.Action;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.NetworkUtil;
import com.test.sample.hirecooks.WebApis.ProductApi;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

public class SearchResultActivity extends BaseActivity {
    //private EditText searchBar;
    private LinearLayout search_lay;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private List<Subcategory> cartList;
    private List<Example> examples;
    private RelativeLayout bottom_anchor_layout;
    private View bottom_anchor;
    private LinearLayout all_search_layout,no_internet_connection_layout;
    private TextView item_count,checkout_amount,checkout,back_button,clear_text,search;
    private View searchbar_interface_layout,no_result_found;
    private MaterialSearchBar searchBar;
    private List<String> suggestList=new ArrayList<>();
    private List<Subcategory> localDataSource=new ArrayList<>();
    private SubcategoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_search_result);
        initViews();
        if(NetworkUtil.checkInternetConnection(this)) {
            all_search_layout.setVisibility( View.VISIBLE );
            no_internet_connection_layout.setVisibility( View.GONE );
            getCart();
        }
        else {
            all_search_layout.setVisibility( View.GONE );
            no_internet_connection_layout.setVisibility( View.VISIBLE );
        }
    }

    private void initViews() {
        all_search_layout = findViewById(R.id.all_search_layout);
        no_internet_connection_layout = findViewById(R.id.no_internet_connection_layout);
        recyclerView = findViewById(R.id.subcategory_recycler);
        toolbar = findViewById(R.id.toolbar);
        bottom_anchor_layout = findViewById(R.id.bottom_anchor_layout);
        searchbar_interface_layout = findViewById(R.id.searchbar_interface_layout);
        View views = findViewById(R.id.footerView);
        item_count =  views.findViewById(R.id.item_count);
        bottom_anchor =  views.findViewById(R.id.bottom_anchor);
        checkout_amount = views.findViewById(R.id.checkout_amount);
        checkout = views.findViewById(R.id.checkout);
        searchBar = findViewById(R.id.searchBar);

        checkout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( SearchResultActivity.this, PlaceOrderActivity.class )  .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        searchBar.openSearch();
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                startSearch(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest=new ArrayList<>();
                for(String search:suggestList)
                {
                    if(search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                searchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {
                startSearch(s);
            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(!enabled)
                    recyclerView.setAdapter(mAdapter); // restores full list of drinks
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {
                switch (buttonCode) {
                    case MaterialSearchBar.BUTTON_NAVIGATION:
                        //drawer.openDrawer(GravityCompat.START);
                        break;
                    case MaterialSearchBar.BUTTON_SPEECH:
                        break;
                    case MaterialSearchBar.BUTTON_BACK:
                        searchBar.closeSearch();
                        finish();
                        break;
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
        int moveY = hide ? -(2 * searchBar.getHeight()) : 0;
        searchBar.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }


    private void buildSuggestList(List<Subcategory> subcategoryList) {
        for(Subcategory subcategory:subcategoryList)
            suggestList.add(subcategory.getName());
        searchBar.setLastSuggestions(suggestList);
    }

    private void startSearch(CharSequence text) {
        try {
            if (text.toString().toLowerCase().contains(String.valueOf(text).toLowerCase())) {
                List<Subcategory> result=new ArrayList<>();
                for(Subcategory subcategory:localDataSource)
                    if(subcategory.getName().contains(text))
                        result.add(subcategory);
                searchAllProducts( text.toString() );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCart() {
        cartList = new ArrayList<>();
        cartList = getnewCartList();
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

    @Override
    public void onResume() {
        super.onResume();
       getCart();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SearchResultActivity.this, MainActivity.class) .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
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


    private void searchAllProducts(String search_key) {
        ProductApi mService = ApiClient.getClient().create(ProductApi.class);
        Call<ArrayList<Example>> call = mService.searchAllProducts(search_key);
        call.enqueue(new Callback<ArrayList<Example>>() {
            @SuppressLint({"WrongConstant"})
            @Override
            public void onResponse(@NonNull Call<ArrayList<Example>> call, @NonNull Response<ArrayList<Example>> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    examples = new ArrayList<>(  );
                    examples = response.body();
                    if(examples!=null&&examples.size()!=0){
                        for(Example example:examples){
                            if(!example.getError()){
                                if(example.getSubcategory()!=null&&example.getSubcategory().size()!=0){
                                    List<Subcategory> list = new ArrayList<>();
                                    List<Subcategory> filteredList = new ArrayList<>();
                                    for (Subcategory subcategory : example.getSubcategory()) {
                                        for (Map map : Constants.NEARBY_VENDERS_LOCATION) {
                                            if (map.getFirm_id().equalsIgnoreCase(subcategory.getFirmId())) {
                                                list.add(subcategory);
                                                Set<Subcategory> newList = new LinkedHashSet<>(list);
                                                filteredList = new ArrayList<>(newList);
                                            }
                                        }
                                    }
                                    if(filteredList.size() != 0) {
                                        Constants.SUBCATEGORYs = filteredList;
                                       // buildSuggestList(filteredList);
                                        setDisplay(filteredList);
                                    }else{
                                      //  no_result_found.setVisibility(GONE);
                                        recyclerView.setVisibility( GONE);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Example>> call, Throwable t) {
                System.out.println("Suree : " + t.getMessage());
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setDisplay(List<Subcategory> filteredList) {
       // localDataSource=filteredList;
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        mAdapter = new SubcategoryAdapter( SearchResultActivity.this, filteredList);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }


    public class SubcategoryAdapter extends RecyclerView.Adapter<SearchResultActivity.SubcategoryAdapter.MyViewHolder> {
        List<Subcategory> productList;
        Context context;
        String Tag;
        LocalStorage localStorage;
        Gson gson;
        List<Subcategory> cartList = new ArrayList<>();
        String weight;
        private int discount = 0, discountPercentage = 0, displayrate = 0,sellRate = 0,displayRate = 0, SubTotal = 0,Quantity = 0;

        public SubcategoryAdapter(Context context, List<Subcategory> productList) {
            this.productList = productList;
            this.context = context;
        }

        @NonNull
        @Override
        public SearchResultActivity.SubcategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal_layout, parent, false);
            return new SearchResultActivity.SubcategoryAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final SearchResultActivity.SubcategoryAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            final Subcategory product = productList.get(position);
            localStorage = new LocalStorage(context);
            gson = new Gson();
            cartList = ((BaseActivity) context).getnewCartList();

            if(product!=null){
                if(product.getAcceptingOrder()==0){
                    holder.order_not_accepting.setVisibility( View.VISIBLE);
                    holder.add_item_layout.setVisibility( GONE);

                }else{
                    holder.order_not_accepting.setVisibility( GONE);
                    holder.add_item_layout.setVisibility( View.GONE);
                }

                holder.name.setText(product.getName());
                holder.item_short_desc.setText(product.getDiscription());
                //holder.discription.setText(product.getDetailDiscription());

                if(product.getImages()!=null&&product.getImages().size()!=0){
                    Glide.with(context).load(product.getImages().get( 0 ).getImage()).into(holder.imageView);
                }

                if (product.getSellRate() != 0 && product.getDisplayRate()!= 0) {
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
                        holder.add_.setVisibility(GONE);
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
                    holder.add_.setVisibility(GONE);
                    holder.quantity_ll.setVisibility(View.VISIBLE);
                    sellRate = product.getSellRate();
                    displayRate = product.getDisplayRate();
                    Quantity = 1;
                    if (product.getId() != 0 && product.getName() != null && product.getLink2() != null && product.getDiscription() != null && sellRate != 0 && displayrate != 0 && product.getFirmId() != null) {
                        SubTotal = (sellRate * Quantity);
                        if (context instanceof SearchResultActivity) {
                            Subcategory cart = new Subcategory(product.getId(),product.getSubcategoryid(),product.getLastUpdate(),product.getSearchKey(), product.getName(), product.getProductUniquekey(), product.getLink2(),  product.getLink3(),  product.getLink4(),product.getShieldLink(), product.getDiscription(), product.getDetailDiscription(), sellRate, displayRate,product.getFirmId(),product.getFirmLat(),product.getFirmLng(),product.getFirmAddress(),product.getFrimPincode(),product.getColors(),product.getImages(),product.getSizes(),product.getWeights(), SubTotal, 1,product.getBrand(),product.getGender(),product.getAge(),product.getAcceptingOrder());
                            cartList = ((BaseActivity) context).getnewCartList();
                            cartList.add(cart);
                            String cartStr = gson.toJson(cartList);
                            localStorage.setCart(cartStr);
                            ((AddorRemoveCallbacks) context).onAddProduct();
                            notifyItemChanged(position);
                            getCart();
                        }
                    }else{
                        holder.add_.setVisibility(View.VISIBLE);
                        holder.quantity_ll.setVisibility(GONE);
                        Toast.makeText(context, "This item can`t be add please remove item and try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.add_item.setOnClickListener( v -> {
                holder.add_.setVisibility(GONE);
                holder.quantity_ll.setVisibility(View.VISIBLE);
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
                    if (product.getId() != 0 && product.getName() != null && product.getLink2() != null && product.getDiscription() != null && sellRate != 0 && displayrate != 0 && product.getFirmId() != null) {
                        sellRate = product.getSellRate();
                        displayRate = product.getDisplayRate();
                        if (Quantity == 0) {
                            Quantity = 1;
                            SubTotal = (sellRate * Quantity);
                            if (context instanceof SearchResultActivity) {
                                Subcategory cart = new Subcategory(product.getId(),product.getSubcategoryid(),product.getLastUpdate(),product.getSearchKey(), product.getName(), product.getProductUniquekey(), product.getLink2(),  product.getLink3(),  product.getLink4(),product.getShieldLink(), product.getDiscription(), product.getDetailDiscription(), sellRate, displayRate,product.getFirmId(),product.getFirmLat(),product.getFirmLng(),product.getFirmAddress(),product.getFrimPincode(),product.getColors(),product.getImages(),product.getSizes(),product.getWeights(), SubTotal, 1,product.getBrand(),product.getGender(),product.getAge(),product.getAcceptingOrder());
                                cartList = ((BaseActivity) context).getnewCartList();
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
                        holder.add_.setVisibility(View.VISIBLE);
                        holder.quantity_ll.setVisibility(GONE);
                        Toast.makeText(context, "This item can`t be add please remove item and try again", Toast.LENGTH_SHORT).show();
                    }
                }
            } );

            holder.remove_item.setOnClickListener( v -> {
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
            } );


            holder.cardview.setOnClickListener( v -> {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(context, DetailsActivity.class);
                bundle.putSerializable("SubCategory",productList.get(position));
                intent.putExtras(bundle);
                intent .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } );
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
            TextView discount, name, sellrate, quantity, displayRate,item_not_in_stock,discription,item_short_desc,add_;
            TextView add_item, remove_item;
            LinearLayout add_item_layout,quantity_ll;
            FrameLayout order_not_accepting;
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
                order_not_accepting = itemView.findViewById(R.id.order_not_accepting);
            }
        }
    }
}