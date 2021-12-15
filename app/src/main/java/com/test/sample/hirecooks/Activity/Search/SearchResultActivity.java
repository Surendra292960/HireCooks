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
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.bumptech.glide.Glide;
import com.test.sample.hirecooks.Activity.AddorRemoveCallbacks;
import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.Activity.Orders.PlaceOrderActivity;
import com.test.sample.hirecooks.Activity.ProductDatails.DetailsActivity;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.SubCategory.SubcategoryResponse;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.LocalStorage;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.NetworkUtil;
import com.test.sample.hirecooks.ViewModel.SearchViewModel.SearchViewModel;
import com.test.sample.hirecooks.WebApis.ProductApi;
import com.test.sample.hirecooks.databinding.ActivitySearchResultBinding;
import com.test.sample.hirecooks.databinding.ItemHorizontalLayoutBinding;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

public class SearchResultActivity extends BaseActivity {
    private List<Subcategory> cartList;
    private List<SubcategoryResponse> examples;
    private RelativeLayout bottom_anchor_layout;
    private List<String> suggestList=new ArrayList<>();
    private List<Subcategory> localDataSource = new ArrayList<>();
    private SubcategoryAdapter mAdapter;
    private ActivitySearchResultBinding searchResultBinding;
    private SearchViewModel searchViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        searchResultBinding = ActivitySearchResultBinding.inflate(getLayoutInflater());
        View view = searchResultBinding.getRoot();
        setContentView(view);
        initViews();
        if(NetworkUtil.checkInternetConnection(this)) {
            searchResultBinding.allSearchLayout.setVisibility( View.VISIBLE );
            searchResultBinding.noInternetConnectionLayout.setVisibility( View.GONE );
            getCart();
        }
        else {
            searchResultBinding.allSearchLayout.setVisibility( View.GONE );
            searchResultBinding.noInternetConnectionLayout.setVisibility( View.VISIBLE );
        }
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);

    }

    private void initViews() {
        searchResultBinding.footerView.checkout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( SearchResultActivity.this, PlaceOrderActivity.class )  .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        searchResultBinding.searchBar.openSearch();
        searchResultBinding.searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                startSearch(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                startSearch(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                startSearch(s);
            }
        });
        searchResultBinding.searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(!enabled)
                    searchResultBinding.subcategoryRecycler.setAdapter(mAdapter); // restores full list of drinks
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
                        searchResultBinding.searchBar.closeSearch();
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
        int moveY = hide ? (2 * searchResultBinding.footerView.bottomAnchor.getHeight()) : 0;
        searchResultBinding.footerView.bottomAnchor.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    boolean isSearchBarHide = false;

    private void animateSearchBar(final boolean hide) {
        if (isSearchBarHide && hide || !isSearchBarHide && !hide) return;
        isSearchBarHide = hide;
        int moveY = hide ? -(2 * searchResultBinding.searchBar.getHeight()) : 0;
        searchResultBinding.searchBar.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    private void startSearch(CharSequence text) {
        try {
            if (text.toString().toLowerCase().contains(String.valueOf(text).toLowerCase())) {
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
                    searchResultBinding.bottomAnchorLayout.setAnimation(AnimationUtils.loadAnimation(this,R.anim.fade_transition_animation));
                }
            }
            searchResultBinding.bottomAnchorLayout.setVisibility(View.VISIBLE);
            searchResultBinding.footerView.checkoutAmount.setText("₹  " + getTotalPrice());
            searchResultBinding.footerView.itemCount.setText("" + cartCount());

        } else {
            searchResultBinding.bottomAnchorLayout.setVisibility(View.GONE);
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
        Call<ArrayList<SubcategoryResponse>> call = mService.searchAllProducts(search_key);
        call.enqueue(new Callback<ArrayList<SubcategoryResponse>>() {
            @SuppressLint({"WrongConstant"})
            @Override
            public void onResponse(@NonNull Call<ArrayList<SubcategoryResponse>> call, @NonNull Response<ArrayList<SubcategoryResponse>> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    examples = new ArrayList<>(  );
                    examples = response.body();
                    if(examples!=null&&examples.size()!=0){
                        for(SubcategoryResponse example:examples){
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
                                        setDisplay(filteredList);
                                    }else{
                                        //  no_result_found.setVisibility(GONE);
                                        searchResultBinding.subcategoryRecycler.setVisibility( GONE);
                                    }
                                }
                            }
                        }
                    } else{
                        searchResultBinding.subcategoryRecycler.setVisibility( GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<SubcategoryResponse>> call, Throwable t) {
                System.out.println("Suree : " + t.getMessage());
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setDisplay(List<Subcategory> filteredList) {
        //localDataSource=new ArrayList<>();
        //buildSuggestList(filteredList);
        searchResultBinding.subcategoryRecycler.setVisibility(View.VISIBLE);
        searchResultBinding.subcategoryRecycler.setHasFixedSize(true);
        mAdapter = new SubcategoryAdapter( SearchResultActivity.this, filteredList);
        searchResultBinding.subcategoryRecycler.setAdapter(mAdapter);
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
            return new SubcategoryAdapter.MyViewHolder(ItemHorizontalLayoutBinding.inflate(LayoutInflater.from(context)));
        }

        @Override
        public void onBindViewHolder(@NonNull final SearchResultActivity.SubcategoryAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            final Subcategory product = productList.get(position);
            localStorage = new LocalStorage(context);
            gson = new Gson();
            cartList = ((BaseActivity) context).getnewCartList();

            if(product!=null){
                if(product.getAcceptingOrder()==0){
                    holder.binding.orderNotAccepting.setVisibility( View.VISIBLE);
                    holder.binding.addItemLayout.setVisibility( GONE);

                }else{
                    holder.binding.orderNotAccepting.setVisibility( GONE);
                    holder.binding.addItemLayout.setVisibility( View.GONE);
                }

                holder.binding.itemName.setText(product.getName());
                holder.binding.itemShortDesc.setText(product.getDiscription());
                //holder.binding.discription.setText(product.getDetailDiscription());

                if(product.getImages()!=null&&product.getImages().size()!=0){
                    Glide.with(context).load(product.getImages().get( 0 ).getImage()).into(holder.binding.itemImage);
                }

                if (product.getSellRate() != 0 && product.getDisplayRate()!= 0) {
                    holder.binding.itemSellrate.setText("\u20B9 " + product.getSellRate());
                    SpannableString spanString = new SpannableString("\u20B9 " + product.getDisplayRate());
                    spanString.setSpan(new StrikethroughSpan(), 0, spanString.length(), 0);
                    holder.binding.itemDisplayrate.setText(spanString);
                    discount = (product.getDisplayRate() - product.getSellRate());
                    displayrate = (product.getDisplayRate());
                    discountPercentage = (discount * 100 / displayrate);
                    holder.binding.itemDiscount.setText("Save " + discountPercentage + " %");
                }
            }

            if (!cartList.isEmpty()) {
                for (int i = 0; i < cartList.size(); i++) {
                    if (cartList.get(i).getId()==product.getId()&&cartList.get(i).getName().equalsIgnoreCase(product.getName())) {
                        holder.binding.add.setVisibility(GONE);
                        holder.binding.quantityLl.setVisibility(View.VISIBLE);
                        holder.binding.itemQty.setText(""+cartList.get(i).getItemQuantity());
                        Quantity = cartList.get(i).getItemQuantity();
                        sellRate = product.getSellRate();
                        SubTotal = (sellRate * Quantity);
                    }
                }
            } else {

                holder.binding.itemQty.setText("0");
            }

            holder.binding.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.binding.add.setVisibility(GONE);
                    holder.binding.quantityLl.setVisibility(View.VISIBLE);
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
                        holder.binding.add.setVisibility(View.VISIBLE);
                        holder.binding.quantityLl.setVisibility(GONE);
                        Toast.makeText(context, "This item can`t be add please remove item and try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.binding.addItem.setOnClickListener( v -> {
                holder.binding.add.setVisibility(GONE);
                holder.binding.quantityLl.setVisibility(View.VISIBLE);
                sellRate = product.getSellRate();
                displayRate = product.getDisplayRate();
                String count = holder.binding.itemQty.getText().toString();
                Quantity = Integer.parseInt(count);
                if (Quantity >= 1) {
                    Quantity++;
                    holder.binding.itemQty.setText(""+(Quantity));
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
                        holder.binding.add.setVisibility(View.VISIBLE);
                        holder.binding.quantityLl.setVisibility(GONE);
                        Toast.makeText(context, "This item can`t be add please remove item and try again", Toast.LENGTH_SHORT).show();
                    }
                }
            } );

            holder.binding.removeItem.setOnClickListener( v -> {
                sellRate = product.getSellRate();
                displayRate = product.getDisplayRate();
                String count = holder.binding.itemQty.getText().toString();
                Quantity = Integer.parseInt(count);
                if (Quantity > 1) {
                    Quantity--;
                    holder.binding.itemQty.setText(""+(Quantity));
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


            holder.binding.cardView.setOnClickListener( v -> {
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
           ItemHorizontalLayoutBinding binding;

            public MyViewHolder(@NonNull ItemHorizontalLayoutBinding bind) {
                super(bind.getRoot());
                binding = bind;
            }
        }
    }

   /* public class SuggestListAdapter extends BaseAdapter {
        private List<String> suggestList;
        private Context mCtx;
        LayoutInflater inflter;

        public SuggestListAdapter(Context mCtx, List<String> suggestList) {
            this.mCtx = mCtx;
            this.suggestList = suggestList;
            inflter = (LayoutInflater.from(mCtx));
        }

        @Override
        public int getCount() {
            return suggestList==null?0:suggestList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.filter_value_item, null); // inflate the layout
            TextView icon =  view.findViewById(R.id.value); // get the reference of ImageView
            icon.setText(suggestList.get(i)); // set logo images
            icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchAllProducts(suggestList.get(i));
                    suggetion_grid.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            });
            return view;
        }
    }*/
}