package com.test.sample.hirecooks.Activity.SubCategory.VendersSubCategory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Activity.AddorRemoveCallbacks;
import com.test.sample.hirecooks.Activity.CheckOut.CheckoutActivity;
import com.test.sample.hirecooks.Activity.ProductDatails.ProductDetailsActivity;
import com.test.sample.hirecooks.Adapter.Category.VendersCategoryAdapter;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Models.BannerResponse.Banner;
import com.test.sample.hirecooks.Models.BannerResponse.Banners;
import com.test.sample.hirecooks.Models.Cart.Cart;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.NewProductsSubCategory.NewProductSubcategories;
import com.test.sample.hirecooks.Models.NewProductsSubCategory.NewProductSubcategory;
import com.test.sample.hirecooks.Models.OfferSubCategory.OffersSubcategories;
import com.test.sample.hirecooks.Models.OfferSubCategory.OffersSubcategory;
import com.test.sample.hirecooks.Models.SubCategory.Response.SubCategories;
import com.test.sample.hirecooks.Models.SubCategory.Response.SubCategory;
import com.test.sample.hirecooks.Models.UsersResponse.UserResponse;
import com.test.sample.hirecooks.Models.VendersCategory.Result;
import com.test.sample.hirecooks.Models.VendersCategory.VendersCategory;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.LocalStorage;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.DividerItemDecoration;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.WebApis.ProductApi;
import com.test.sample.hirecooks.WebApis.UserApi;
import com.test.sample.hirecooks.WebApis.VendersApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VendersSubcategoryActivity extends BaseActivity {
    private RecyclerView vender_offers_subcategory,vender_new_product_category_recycler,vender_popular_product_recycler,vender_category_rv;
    private ProgressBarUtil progressBarUtil;
    private List<OffersSubcategory> offersSubcategories;
    private FrameLayout no_result_found;
    private UserResponse vender;
    private RelativeLayout bottom_anchor_layout;
    private TextView item_count,checkout_amount,checkout;
    private List<Cart> cartList;
    private List<SubCategory> vendersSubcategories;
    private List<NewProductSubcategory> newProductSubcategories;
    private List<Banner> banners;
    private ImageView image3;
    private View bottom_anchor;
    private SliderLayout sliderLayout;
    private List<VendersCategory> vendersCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venders_subcategory);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        intitViews();
        getCart();
        //getVendersCategory();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null&&Constants.NEARBY_USER_LOCATION!=null) {
            vender = (UserResponse) bundle.getSerializable("Vender");
            if (vender != null) {
                Objects.requireNonNull(getSupportActionBar()).setTitle(vender.getName());
                getVendersOfferSubCategory();
                getVendersSubCategory();
                getVendersNewSubProductCategory();
                getBannerImage();
            }
        }
    }

    private void intitViews() {
        progressBarUtil = new ProgressBarUtil(this);
        vender_category_rv=findViewById(R.id.vender_category_rv);
        vender_new_product_category_recycler=findViewById(R.id.vender_new_product_category_recycler);
        vender_popular_product_recycler=findViewById(R.id.vender_popular_product_recycler);
        vender_offers_subcategory=findViewById(R.id.vender_offers_subcategory);
        sliderLayout=findViewById(R.id.sliderLayout);
        image3 = findViewById(R.id.image3);
        no_result_found=findViewById(R.id.no_result_found);
        bottom_anchor_layout = findViewById(R.id.bottom_anchor_layout);

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

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VendersSubcategoryActivity.this, CheckoutActivity.class));
            }
        });
    }

    private void getVendersCategory() {
        VendersApi mService = ApiClient.getClient().create(VendersApi.class);
        Call<Result> call = mService.getVendersCategory();
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                int statusCode = response.code();
                if(statusCode==200){
                    vendersCategory = response.body().getVendersCategory();
                    ArrayList<VendersCategory> list = new ArrayList<>();
                    ArrayList<VendersCategory> filteredList = new ArrayList<>();
                    if(vendersCategory!=null&&vendersCategory.size()!=0) {
                        for (VendersCategory vendersCategory : vendersCategory) {
                            for (Map map : Constants.NEARBY_USER_LOCATION) {
                                if (map.getFirm_id().equalsIgnoreCase(vendersCategory.getFirmId()) && vender.getFirmId().equalsIgnoreCase(vendersCategory.getFirmId())) {
                                    list.add(vendersCategory);
                                    Set<VendersCategory> newList = new LinkedHashSet<>(list);
                                    filteredList = new ArrayList<>(newList);
                                }
                            }
                        }
                    }
                     if(filteredList!=null){
                         vender_category_rv.setVisibility(View.VISIBLE);
                         VendersCategoryAdapter mAdapter = new VendersCategoryAdapter(VendersSubcategoryActivity.this,filteredList);
                         RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(VendersSubcategoryActivity.this, LinearLayoutManager.HORIZONTAL, false);
                         vender_category_rv.setLayoutManager(mLayoutManager);
                         vender_category_rv.setAdapter(mAdapter);
                     }else{
                         vender_category_rv.setVisibility(View.GONE);
                     }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
            System.out.println("Suree :"+t.getMessage());
            }
        });
    }

    private void getBannerImage() {
        UserApi mService = ApiClient.getClient().create(UserApi.class);
        Call<Banners> call = mService.getBanners();
        call.enqueue(new Callback<Banners>() {
            @SuppressLint("ShowToast")
            @Override
            public void onResponse(Call<Banners> call, Response<Banners> response) {
                int statusCode = response.code();
                if(statusCode==200) {
                    if (response.body() != null) {
                        banners = new ArrayList<>();
                        banners = response.body().getBanners();
                        List<Banner> list = new ArrayList<>();
                        List<Banner> filteredList = new ArrayList<>();
                        if(banners!=null&&banners.size()!=0) {
                            for (Banner banner : banners) {
                                for (Map map : Constants.NEARBY_USER_LOCATION) {
                                    if (map.getFirm_id().equalsIgnoreCase(banner.getFirmId()) && vender.getFirmId().equalsIgnoreCase(banner.getFirmId())) {
                                        list.add(banner);
                                        Set<Banner> newList = new LinkedHashSet<>(list);
                                        filteredList = new ArrayList<>(newList);
                                    }
                                }
                            }
                        }
                        displayImage(filteredList);
                    }
                }
            }
            @SuppressLint("ShowToast")
            @Override
            public void onFailure(Call<Banners> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Suree: "+t.getMessage(),Toast.LENGTH_LONG);
            }
        });
    }

    private void displayImage(List<Banner> banners) {
        HashMap<String,String> bannerMap=new HashMap<>();
        for(Banner item:banners)
            bannerMap.put(item.getName(),item.getLink());

        for(String name:bannerMap.keySet()){
            TextSliderView textSliderView=new TextSliderView(this);
            textSliderView/*.description(name)*/.image(bannerMap.get(name)).setScaleType(BaseSliderView.ScaleType.Fit);
            sliderLayout.addSlider(textSliderView);
            sliderLayout.startAutoCycle();
        }
    }

    private void getVendersOfferSubCategory() {
        progressBarUtil.showProgress();
        ProductApi mService = ApiClient.getClient().create(ProductApi.class);
        Call<OffersSubcategories> call = mService.getOfferSubCategory();
        call.enqueue(new Callback<OffersSubcategories>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<OffersSubcategories> call, Response<OffersSubcategories> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    offersSubcategories = response.body().getOffersSubcategory();
                    progressBarUtil.hideProgress();
                    List<OffersSubcategory> list = new ArrayList<>();
                    List<OffersSubcategory> filteredList = new ArrayList<>();
                    if(offersSubcategories!=null&&offersSubcategories.size()!=0){
                        for (OffersSubcategory offersSubcategory : offersSubcategories) {
                            for (Map map : Constants.NEARBY_USER_LOCATION) {
                                if (map.getFirm_id().equalsIgnoreCase(offersSubcategory.getFirm_id()) && vender.getFirmId().equalsIgnoreCase(offersSubcategory.getFirm_id())) {
                                    list.add(offersSubcategory);
                                    Set<OffersSubcategory> newList = new LinkedHashSet<>(list);
                                    filteredList = new ArrayList<>(newList);
                                }
                            }
                        }
                    }
                    if(filteredList!=null){
                        Constants.OFFER_SUBCATEGORY = filteredList;
                        vender_offers_subcategory.setVisibility(View.VISIBLE);
                        vender_popular_product_recycler.setHasFixedSize(true);
                        VendersOfferSubCategoryAdapter mAdapter = new VendersOfferSubCategoryAdapter(VendersSubcategoryActivity.this,filteredList);
                        vender_offers_subcategory.setLayoutManager(new GridLayoutManager(VendersSubcategoryActivity.this, 2));
                        vender_offers_subcategory.setAdapter(mAdapter);
                        runLayoutAnimation(vender_offers_subcategory);
                    }else{
                        vender_offers_subcategory.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(VendersSubcategoryActivity.this, R.string.failed_due_to + statusCode, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<OffersSubcategories> call, Throwable t) {
                progressBarUtil.hideProgress();
                System.out.println("Suree : " + t.getMessage());

            }
        });
    }

    private void getVendersSubCategory() {
        progressBarUtil.showProgress();
        ProductApi  mService = ApiClient.getClient().create(ProductApi.class);
        Call<SubCategories> call = mService.getSubCategory();
        call.enqueue(new Callback<SubCategories>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<SubCategories> call, Response<SubCategories> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    vendersSubcategories = response.body().getSubcategory();
                    progressBarUtil.hideProgress();
                    if(vendersSubcategories!=null&&vendersSubcategories.size()!=0){
                        List<SubCategory> list = new ArrayList<>();
                        List<SubCategory> filteredList = new ArrayList<>();
                        for (SubCategory vendersSubcategory : vendersSubcategories) {
                            for (Map map : Constants.NEARBY_USER_LOCATION) {
                                if (map.getFirm_id().equalsIgnoreCase(vendersSubcategory.getFirm_id())&&vender.getFirmId().equalsIgnoreCase(vendersSubcategory.getFirm_id())) {
                                    list.add(vendersSubcategory);
                                    Set<SubCategory> newList = new LinkedHashSet<>(list);
                                    filteredList = new ArrayList<>(newList);
                                }
                            }
                        }
                        if(filteredList!=null&&filteredList.size()!=0) {
                            no_result_found.setVisibility(View.GONE);
                            Constants.SUBCATEGORY = filteredList;
                            vender_popular_product_recycler.setVisibility(View.VISIBLE);
                            vender_popular_product_recycler.setHasFixedSize(true);
                            vender_popular_product_recycler.setLayoutManager(new GridLayoutManager(VendersSubcategoryActivity.this, 2));
                            VendresSubCategoryAdapter mAdapter = new VendresSubCategoryAdapter(VendersSubcategoryActivity.this, filteredList);
                            vender_popular_product_recycler.setAdapter(mAdapter);
                            runLayoutAnimation(vender_popular_product_recycler);
                        }else{
                            vender_popular_product_recycler.setVisibility(View.GONE);
                        }
                    }else{
                        no_result_found.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(VendersSubcategoryActivity.this, R.string.failed_due_to + statusCode, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SubCategories> call, Throwable t) {
                progressBarUtil.hideProgress();
                System.out.println("Suree : " + t.getMessage());
            }
        });
    }

    private void getVendersNewSubProductCategory() {
        progressBarUtil.showProgress();
        ProductApi mService = ApiClient.getClient().create(ProductApi.class);
        Call<NewProductSubcategories> call = mService.getNewProductSubCategory();
        call.enqueue(new Callback<NewProductSubcategories>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<NewProductSubcategories> call, Response<NewProductSubcategories> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    newProductSubcategories = response.body().getNewProductSubcategory();
                    progressBarUtil.hideProgress();
                    if(newProductSubcategories!=null&&newProductSubcategories.size()!=0){
                        List<NewProductSubcategory> list = new ArrayList<>();
                        List<NewProductSubcategory> filteredList = new ArrayList<>();
                        for (NewProductSubcategory newProductSubcategory : newProductSubcategories) {
                            for (Map map : Constants.NEARBY_USER_LOCATION) {
                                if (map.getFirm_id().equalsIgnoreCase(newProductSubcategory.getFirm_id())&&vender.getFirmId().equalsIgnoreCase(newProductSubcategory.getFirm_id())) {
                                    list.add(newProductSubcategory);
                                    Set<NewProductSubcategory> newList = new LinkedHashSet<>(list);
                                    filteredList = new ArrayList<>(newList);
                                }
                            }
                        }
                        if(filteredList!=null&&filteredList.size()!=0){
                            vender_new_product_category_recycler.setHasFixedSize(true);
                            no_result_found.setVisibility(View.GONE);
                            vender_new_product_category_recycler.setVisibility(View.VISIBLE);
                            vender_new_product_category_recycler.setLayoutManager(new LinearLayoutManager(VendersSubcategoryActivity.this));
                            VendersNewProductSubCategoryAdapter mAdapter = new VendersNewProductSubCategoryAdapter(VendersSubcategoryActivity.this, filteredList);
                            vender_new_product_category_recycler.setAdapter(mAdapter);
                            runLayoutAnimation(vender_new_product_category_recycler);
                        }else{
                            vender_new_product_category_recycler.setVisibility(View.GONE);
                        }

                    }else{
                        no_result_found.setVisibility(View.VISIBLE);
                    }

                } else {
                    Toast.makeText(VendersSubcategoryActivity.this, R.string.failed_due_to + statusCode, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<NewProductSubcategories> call, Throwable t) {
                progressBarUtil.hideProgress();
                System.out.println("Suree : " + t.getMessage());
            }
        });
    }

    private void runLayoutAnimation(RecyclerView recyclerView){
        Context context = recyclerView.getContext();
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_animation_fall_down);
        recyclerView.setLayoutAnimation(layoutAnimationController);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    boolean isBottomAnchorNavigationHide = false;
    private void animateBottomAnchor(final boolean hide) {
        if (isBottomAnchorNavigationHide && hide || !isBottomAnchorNavigationHide && !hide) return;
        isBottomAnchorNavigationHide = hide;
        int moveY = hide ? (2 * bottom_anchor.getHeight()) : 0;
        bottom_anchor.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onResume() {
        getCart();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class VendersOfferSubCategoryAdapter extends RecyclerView.Adapter<VendersSubcategoryActivity.VendersOfferSubCategoryAdapter.MyViewHolder> {
        List<OffersSubcategory> offerSubcategoryList;
        Context context;
        String Tag;
        LocalStorage localStorage;
        Gson gson;
        List<Cart> cartList = new ArrayList<>();
        String weight;
        private int discount = 0, discountPercentage = 0, displayrate = 0, sellRate = 0,displayRate = 0, SubTotal = 0,Quantity = 0;

        public VendersOfferSubCategoryAdapter(Context context, List<OffersSubcategory> offerSubcategoryList) {
            this.offerSubcategoryList = offerSubcategoryList;
            this.context = context;
        }

        @NonNull
        @Override
        public VendersSubcategoryActivity.VendersOfferSubCategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subcategory_cardview, parent, false);
            return new VendersSubcategoryActivity.VendersOfferSubCategoryAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final VendersSubcategoryActivity.VendersOfferSubCategoryAdapter.MyViewHolder holder, final int position) {
            final OffersSubcategory offerSubcategory = offerSubcategoryList.get(position);
            localStorage = new LocalStorage(context);
            gson = new Gson();
            cartList = ((BaseActivity) context).getCartList();

            if (offerSubcategory != null) {
                if(offerSubcategory.getStock()==1){
                    holder.item_not_in_stock.setVisibility(View.GONE);
                    holder.add_item_layout.setVisibility(View.VISIBLE);
                }else{
                    holder.item_not_in_stock.setVisibility(View.VISIBLE);
                    holder.add_item_layout.setVisibility(View.GONE);
                }
                holder.name.setText(offerSubcategory.getName());
                holder.discription.setText(offerSubcategory.getDiscription());
                Picasso.with(context).load(offerSubcategory.getLink()).into(holder.imageView);

                if (offerSubcategory.getSellRate() != null && offerSubcategory.getSellRate() != 0 && offerSubcategory.getDisplayRate() != null && offerSubcategory.getDisplayRate() != 0) {
                    holder.sellrate.setText("\u20B9 " + offerSubcategory.getSellRate());
                    SpannableString spanString = new SpannableString("\u20B9 " + offerSubcategory.getDisplayRate());
                    spanString.setSpan(new StrikethroughSpan(), 0, spanString.length(), 0);
                    holder.displayRate.setText(spanString);
                    discount = (offerSubcategory.getDisplayRate() - offerSubcategory.getSellRate());
                    displayrate = (offerSubcategory.getDisplayRate());
                    discountPercentage = (discount * 100 / displayrate);
                    holder.discount.setText("Save " + discountPercentage + " %");
                }

                if (!cartList.isEmpty()) {
                    for (int i = 0; i < cartList.size(); i++) {
                        if (cartList.get(i).getId()==offerSubcategory.getId()&&cartList.get(i).getName().equalsIgnoreCase(offerSubcategory.getName())) {
                            holder.quantity.setText("" + cartList.get(i).getItemQuantity());
                            Quantity = cartList.get(i).getItemQuantity();
                            sellRate = offerSubcategory.getSellRate();
                            SubTotal = (sellRate * Quantity);
                        }
                    }
                } else {
                    holder.quantity.setText("0");
                }

                holder.add_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sellRate = offerSubcategory.getSellRate();
                        displayRate = offerSubcategory.getDisplayRate();
                        String count = holder.quantity.getText().toString();
                        Quantity = Integer.parseInt(count);
                        if (Quantity >= 1) {
                            holder.quantity.setText("" + (Quantity));
                            if(Quantity<5){
                                Quantity++;
                                for (int i = 0; i < cartList.size(); i++) {
                                    if (cartList.get(i).getId()==offerSubcategory.getId()&&cartList.get(i).getName().equalsIgnoreCase(offerSubcategory.getName())) {
                                        SubTotal = (sellRate * Quantity);
                                        cartList.get(i).setItemQuantity(Quantity);
                                        cartList.get(i).setTotalAmount(SubTotal);
                                        String cartStr = gson.toJson(cartList);
                                        localStorage.setCart(cartStr);
                                        notifyItemChanged(position);
                                        getCart();


                                    }
                                }
                            }else{
                                Toast.makeText(context,"Cant't be add more than 5",Toast.LENGTH_LONG).show();
                            }
                        } else {

                            if(offerSubcategory.getId()!=0&&offerSubcategory.getName()!=null&&offerSubcategory.getLink()!=null&&offerSubcategory.getDiscription()!=null&&sellRate!=0&&displayrate!=0&&offerSubcategory.getFirm_id()!=null){
                                sellRate = offerSubcategory.getSellRate();
                                displayRate = offerSubcategory.getDisplayRate();
                                if (Quantity == 0) {
                                    Quantity = 1;
                                    SubTotal = (sellRate * Quantity);
                                    if (context instanceof VendersSubcategoryActivity) {
                                        Cart cart = new Cart(offerSubcategory.getId(), offerSubcategory.getName(), offerSubcategory.getLink(), offerSubcategory.getDiscription(), sellRate, displayRate, SubTotal, 1, weight,offerSubcategory.getFirm_id());
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
                        sellRate = offerSubcategory.getSellRate();
                        displayRate = offerSubcategory.getDisplayRate();
                        String count = holder.quantity.getText().toString();
                        Quantity = Integer.parseInt(count);
                        if (Quantity > 1) {
                            Quantity--;
                            holder.quantity.setText("" + (Quantity));
                            for (int i = 0; i < cartList.size(); i++) {
                                if (cartList.get(i).getId()==offerSubcategory.getId()&&cartList.get(i).getName().equalsIgnoreCase(offerSubcategory.getName())) {
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
                        bundle.putSerializable("OfferSubCategory",offerSubcategoryList.get(position));
                        intent.putExtras(bundle);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {

            return offerSubcategoryList==null?0:offerSubcategoryList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView discount, name, sellrate, quantity,displayRate,item_not_in_stock,discription;
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
                cardview = itemView.findViewById(R.id.cardview);
                discription = itemView.findViewById(R.id.item_descriptions);
                item_not_in_stock = itemView.findViewById(R.id.item_not_in_stock);
            }
        }
    }

    public class VendresSubCategoryAdapter extends RecyclerView.Adapter<VendersSubcategoryActivity.VendresSubCategoryAdapter.MyViewHolder> {
        List<SubCategory> productList;
        Context context;
        String Tag;
        LocalStorage localStorage;
        Gson gson;
        List<Cart> cartList = new ArrayList<>();
        String weight;
        private int discount = 0, discountPercentage = 0, displayrate = 0,sellRate = 0,displayRate = 0, SubTotal = 0,Quantity = 0;

        public VendresSubCategoryAdapter(Context context, List<SubCategory> productList) {
            this.productList = productList;
            this.context = context;
        }

        @NonNull
        @Override
        public VendersSubcategoryActivity.VendresSubCategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subcategory_cardview, parent, false);
            return new VendersSubcategoryActivity.VendresSubCategoryAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final VendersSubcategoryActivity.VendresSubCategoryAdapter.MyViewHolder holder, final int position) {

            final SubCategory product = productList.get(position);
            localStorage = new LocalStorage(context);
            gson = new Gson();
            cartList = ((BaseActivity) context).getCartList();

            if(product!=null){
                if(product.getStock()==1){
                    holder.add_item_layout.setVisibility(View.VISIBLE);
                }else{
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
                                if (context instanceof VendersSubcategoryActivity) {
                                    Cart cart = new Cart(product.getId(), product.getName(),  product.getLink(), product.getDiscription(), sellRate, displayRate, SubTotal, 1, weight, product.getFirm_id());
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
                    bundle.putSerializable("SubCategory",productList.get(position));
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

    public class VendersNewProductSubCategoryAdapter extends RecyclerView.Adapter<VendersSubcategoryActivity.VendersNewProductSubCategoryAdapter.ViewHolder> {
        private Context mCtx;
        private List<NewProductSubcategory> categories;
        Gson gson;
        LocalStorage localStorage;
        List<Cart> cartList = new ArrayList<>();
        String Tag;
        String weight;
        private int discount = 0, discountPercentage = 0, displayrate = 0, sellRate = 0,displayRate = 0, SubTotal = 0, Quantity = 0;

        public VendersNewProductSubCategoryAdapter(Context mCtx, List<NewProductSubcategory> categories) {

            this.mCtx = mCtx;
            this.categories = categories;
        }

        @Override
        public VendersSubcategoryActivity.VendersNewProductSubCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_product_subcategory, null);
            VendersSubcategoryActivity.VendersNewProductSubCategoryAdapter.ViewHolder viewHolder = new VendersSubcategoryActivity.VendersNewProductSubCategoryAdapter.ViewHolder(itemLayoutView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(VendersSubcategoryActivity.VendersNewProductSubCategoryAdapter.ViewHolder holder, int position) {
            NewProductSubcategory subcategory = categories.get(position);

            if (subcategory != null) {
                if(subcategory.getStock()==1){
                    holder.add_.setVisibility(View.VISIBLE);
                    holder.item_not_in_stock.setVisibility(View.GONE);
                }else{
                    holder.add_.setVisibility(View.GONE);
                    holder.item_not_in_stock.setVisibility(View.VISIBLE);
                }
                localStorage = new LocalStorage(mCtx);
                gson = new Gson();
                cartList = ((BaseActivity) mCtx).getCartList();

                holder.item_name.setText(subcategory.getName());

                holder.item_descriptions.setText(subcategory.getDiscription());
                if (subcategory.getSellRate() != null && subcategory.getSellRate() != 0 && subcategory.getDisplayRate() != null && subcategory.getDisplayRate() != 0) {
                    holder.item_sellrate.setText("\u20B9 " + subcategory.getSellRate());
                    SpannableString spanString = new SpannableString("\u20B9 " + subcategory.getDisplayRate());
                    spanString.setSpan(new StrikethroughSpan(), 0, spanString.length(), 0);
                    holder.item_displayrate.setText(spanString);
                    discount = (subcategory.getDisplayRate() - subcategory.getSellRate());
                    displayrate = (subcategory.getDisplayRate());
                    discountPercentage = (discount * 100 / displayrate);
                    holder.item_discount.setText("Save " + discountPercentage + " %");
                    for (int i = 1; i < subcategory.getWeight().length(); i++) {
                        String text = subcategory.getWeight();
                        String[] textArray = text.split(",");
                        holder.weight_gridview.setHasFixedSize(true);
                        holder.weight_gridview.setLayoutManager(new LinearLayoutManager(mCtx));
                        VendersSubcategoryActivity.VendersNewProductSubCategoryAdapter.TextViewAdapter mAdapter = new VendersSubcategoryActivity.VendersNewProductSubCategoryAdapter.TextViewAdapter(mCtx, textArray);
                        holder.weight_gridview.setAdapter(mAdapter);
                        holder.weight_gridview.setLayoutManager(new GridLayoutManager(mCtx, 4));
                        holder.weight_gridview.addItemDecoration(new DividerItemDecoration(0));
                        holder.weight_gridview.setItemAnimator(new DefaultItemAnimator());
                    }
                }

                Picasso.with(mCtx).load(subcategory.getLink()).into(holder.item_image);

                if (!cartList.isEmpty()) {
                    for (int i = 0; i < cartList.size(); i++) {
                        if (cartList.get(i).getId()==subcategory.getId()&&cartList.get(i).getName().equalsIgnoreCase(subcategory.getName())) {
                            holder.add_.setVisibility(View.GONE);
                            holder.quantity_ll.setVisibility(View.VISIBLE);
                            holder.quantity.setText(""+cartList.get(i).getItemQuantity());
                            Quantity = cartList.get(i).getItemQuantity();
                            sellRate = subcategory.getSellRate();
                            SubTotal = (sellRate * Quantity);
                        }
                    }
                } else {
                    holder.quantity.setText("0");
                }

                holder.add_.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(weight!=null) {
                            holder.add_.setVisibility(View.GONE);
                            holder.quantity_ll.setVisibility(View.VISIBLE);
                            sellRate = subcategory.getSellRate();
                            displayRate = subcategory.getDisplayRate();
                            Quantity = 1;
                            if (subcategory.getId() != 0 && subcategory.getName() != null && subcategory.getLink() != null && subcategory.getDiscription() != null && sellRate != 0 && displayrate != 0 && weight != null && subcategory.getFirm_id() != null) {
                                sellRate = subcategory.getSellRate();
                                displayRate = subcategory.getDisplayRate();
                                Quantity = 1;
                                SubTotal = (sellRate * Quantity);
                                if (mCtx instanceof VendersSubcategoryActivity) {
                                    Cart cart = new Cart(subcategory.getId(), subcategory.getName(), subcategory.getLink(), subcategory.getDiscription(), sellRate, displayRate, SubTotal, 1, weight, subcategory.getFirm_id());
                                    cartList = ((BaseActivity) mCtx).getCartList();
                                    cartList.add(cart);
                                    String cartStr = gson.toJson(cartList);
                                    localStorage.setCart(cartStr);
                                    ((AddorRemoveCallbacks) mCtx).onAddProduct();
                                    notifyItemChanged(position);
                                    weight = null;
                                    getCart();
                                }
                            }else {
                                Toast.makeText(mCtx, "This item can`t be add please remove item and try again", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mCtx, "Please select weight", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                holder.add_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sellRate = subcategory.getSellRate();
                        displayRate = subcategory.getDisplayRate();
                        String count = holder.quantity.getText().toString();
                        Quantity = Integer.parseInt(count);
                        if (Quantity >= 1) {
                            Quantity++;
                            holder.quantity.setText("" + (Quantity));
                            for (int i = 0; i < cartList.size(); i++) {

                                if (cartList.get(i).getId()==subcategory.getId()&&cartList.get(i).getName().equalsIgnoreCase(subcategory.getName())) {
                                    SubTotal = (sellRate * Quantity);
                                    cartList.get(i).setItemQuantity(Quantity);
                                    cartList.get(i).setTotalAmount(SubTotal);
                                    String cartStr = gson.toJson(cartList);
                                    localStorage.setCart(cartStr);
                                    notifyItemChanged(position);
                                    getCart();
                                }
                            }
                        } else {

                        }
                    }
                });

                holder.remove_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sellRate = subcategory.getSellRate();
                        displayRate = subcategory.getDisplayRate();
                        String count = holder.quantity.getText().toString();
                        Quantity = Integer.parseInt(count);
                        if (Quantity > 1) {
                            Quantity--;
                            holder.quantity.setText(""+(Quantity));
                            for (int i = 0; i < cartList.size(); i++) {
                                if (cartList.get(i).getId()==subcategory.getId()&&cartList.get(i).getName().equalsIgnoreCase(subcategory.getName())) {
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
        }

        @Override
        public int getItemCount() {
            return categories == null ? 0 : categories.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView item_name, item_discount, item_sellrate, item_displayrate, item_descriptions, quantity,item_not_in_stock,add_;
            public TextView add_item, remove_item;
            public ImageView item_image;
            public RecyclerView weight_gridview;
            LinearLayout add_item_layout,quantity_ll;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                item_name = itemLayoutView.findViewById(R.id.item_name);
                item_sellrate = itemLayoutView.findViewById(R.id.item_sellrate);
                item_displayrate = itemLayoutView.findViewById(R.id.item_displayrate);
                item_descriptions = itemLayoutView.findViewById(R.id.item_descriptions);
                item_discount = itemLayoutView.findViewById(R.id.item_discount);
                item_image = itemLayoutView.findViewById(R.id.item_image);
                add_item = itemLayoutView.findViewById(R.id.add_item);
                remove_item = itemLayoutView.findViewById(R.id.remove_item);
                quantity = itemLayoutView.findViewById(R.id.item_count);
                weight_gridview = itemLayoutView.findViewById(R.id.weight_gridview);
                add_item_layout = itemView.findViewById(R.id.add_item_layout);
                item_not_in_stock = itemView.findViewById(R.id.item_not_in_stock);
                add_ = itemView.findViewById(R.id.add_);
                quantity_ll = itemView.findViewById(R.id.quantity_ll);

                itemLayoutView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                 /* Intent intent = new Intent(v.getContext(), SecondPage.class);
                    v.getContext().startActivity(intent);
                    Toast.makeText(v.getContext(), "os version is: " + feed.getTitle(), Toast.LENGTH_SHORT).show();*/
                    }
                });
            }
        }

        public class TextViewAdapter extends RecyclerView.Adapter<VendersSubcategoryActivity.VendersNewProductSubCategoryAdapter.TextViewAdapter.ViewHolder> {

            private Context mCtx;
            private final String[] textViewValues;

            public TextViewAdapter(Context mCtx, String[] textViewValues) {
                this.mCtx = mCtx;
                this.textViewValues = textViewValues;
            }

            @NonNull
            @Override
            public VendersSubcategoryActivity.VendersNewProductSubCategoryAdapter.TextViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.text, null);
                VendersSubcategoryActivity.VendersNewProductSubCategoryAdapter.TextViewAdapter.ViewHolder viewHolder = new VendersSubcategoryActivity.VendersNewProductSubCategoryAdapter.TextViewAdapter.ViewHolder(itemLayoutView);
                return viewHolder;
            }

            @Override
            public void onBindViewHolder(@NonNull VendersSubcategoryActivity.VendersNewProductSubCategoryAdapter.TextViewAdapter.ViewHolder holder, int position) {
                holder.item_weight.setText(textViewValues[position]);
                holder.item_weight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // weight = textViewValues[position];

                        weight= String.valueOf(textViewValues[position]);
                        notifyDataSetChanged();
                    }
                });

                if(weight==String.valueOf(textViewValues[position])){
                    holder.item_weight.setBackgroundColor(Color.parseColor("#567845"));
                    holder.item_weight.setTextColor(Color.parseColor("#ffffff"));
                }
                else
                {
                    holder.item_weight.setBackgroundColor(Color.parseColor("#ffffff"));
                    holder.item_weight.setTextColor(Color.parseColor("#000000"));
                }
            }

            @Override
            public int getItemCount() {
                return textViewValues == null ? 0 : textViewValues.length;
            }

            public class ViewHolder extends RecyclerView.ViewHolder {
                public Button item_weight;

                public ViewHolder(View itemLayoutView) {
                    super(itemLayoutView);
                    item_weight = itemLayoutView.findViewById(R.id.item_weight);
                }
            }
        }
    }

}
