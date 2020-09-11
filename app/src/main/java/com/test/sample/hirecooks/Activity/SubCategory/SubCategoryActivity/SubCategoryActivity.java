package com.test.sample.hirecooks.Activity.SubCategory.SubCategoryActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
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
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Activity.AddorRemoveCallbacks;
import com.test.sample.hirecooks.Activity.Orders.PlaceOrderActivity;
import com.test.sample.hirecooks.Activity.ProductDatails.ProductDetailsActivity;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.Cart.Cart;
import com.test.sample.hirecooks.Models.Category.Category;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.NewProductsCategory.NewProductCategory;
import com.test.sample.hirecooks.Models.NewProductsSubCategory.NewProductSubcategories;
import com.test.sample.hirecooks.Models.NewProductsSubCategory.NewProductSubcategory;
import com.test.sample.hirecooks.Models.OfferCategory.OffersCategory;
import com.test.sample.hirecooks.Models.OfferSubCategory.OffersSubcategories;
import com.test.sample.hirecooks.Models.OfferSubCategory.OffersSubcategory;
import com.test.sample.hirecooks.Models.SubCategory.Response.SubCategories;
import com.test.sample.hirecooks.Models.SubCategory.Response.SubCategory;
import com.test.sample.hirecooks.Models.users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.LocalStorage;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.DividerItemDecoration;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.ProductApi;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubCategoryActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private List<SubCategory> subCategories;
    private List<NewProductSubcategory> newProductSubcategories;
    private List<OffersSubcategory> offersSubcategories;
    private ProgressBarUtil progressBarUtil;
    private Category category;
    private OffersCategory offersCategory;
    private NewProductCategory newProductCategory;
    private EditText searchBar;
    private RelativeLayout bottom_anchor_layout;
    private TextView item_count,checkout_amount,checkout;
    private List<Cart> cartList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String categoryName;
    private FrameLayout no_result_found, no_vender_found,no_search_result_found;
    private List<Map> maps;
    private User user;
    private ImageButton clear_text,back_button;
    private View searchbar_interface_layout,bottom_anchor,appRoot;
    private FloatingActionButton sort;
    private int categoryId,offercategoryId,newsubcategoryId = 0;
    private String CATEGORY_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        progressBarUtil = new ProgressBarUtil(this);
        initViews();
        getCart();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null&&Constants.NEARBY_VENDERS_LOCATION !=null) {
            categoryName= bundle.getString("CategoryName");
            category = (Category) bundle.getSerializable("Category");
            newProductCategory = (NewProductCategory) bundle.getSerializable("NewProductCategory");
            offersCategory = (OffersCategory) bundle.getSerializable("offersCategory");

            if(categoryName==null){
                if (category != null) {
                    categoryId = category.getCategoryid();
                    getSubCategory(categoryId);
                } else if (newProductCategory != null) {
                    newsubcategoryId = newProductCategory.getCategoryid();
                    getNewSubProductCategory(newsubcategoryId);
                } else if (offersCategory != null) {
                    offercategoryId = offersCategory.getCategoryid();
                    getOfferSubCategory(offercategoryId);
                }
            }
            else {
                if (categoryName.equalsIgnoreCase("Offers")) {
                    CATEGORY_NAME = categoryName;
                    getOfferSubCategory();
                } else if (categoryName.equalsIgnoreCase("Popular Products")) {
                    CATEGORY_NAME = categoryName;
                    getSubCategory();
                } else if (categoryName.equalsIgnoreCase("New Products")) {
                    CATEGORY_NAME = categoryName;
                    getNewSubProductCategory();
                }
            }
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

    private void initViews() {
        user = SharedPrefManager.getInstance(this).getUser();
        appRoot = findViewById(R.id.appRoot);
        sort = findViewById(R.id.sort);
        recyclerView = findViewById(R.id.subcategory_recycler);
        no_result_found=findViewById(R.id.no_result_found);
        no_vender_found=findViewById(R.id.no_vender_found);
        no_search_result_found=findViewById(R.id.no_search_result_found);
        bottom_anchor_layout = findViewById(R.id.bottom_anchor_layout);

        View search_view = findViewById(R.id.search_bar);
        searchBar=search_view.findViewById(R.id.searchBar);
        clear_text=search_view.findViewById(R.id.clear_text);
        back_button=search_view.findViewById(R.id.back_button);
        searchbar_interface_layout=search_view.findViewById(R.id.searchbar_interface_layout);

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
                    animateSearchBar(false);
                    animateBottomAnchor(false);
                }
                if (scrollY > oldScrollY) { // down
                    animateSearchBar(true);
                    animateBottomAnchor(true);
                }
            }
        });

        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorScheme(R.color.green_light, R.color.red, R.color.style_color_primary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Rect rect = new Rect();
                        mSwipeRefreshLayout.getDrawingRect(rect);
                        mSwipeRefreshLayout.setProgressViewOffset(false, 0, rect.centerY() - (mSwipeRefreshLayout.getProgressCircleDiameter() / 2));

                        mSwipeRefreshLayout.setRefreshing(false);
                        if(category!=null){
                            getSubCategory(category.getCategoryid());
                        }else if(offersCategory!=null){
                            getOfferSubCategory(offersCategory.getCategoryid());
                        }else if(newProductCategory!=null){
                            getNewSubProductCategory(newProductCategory.getCategoryid());
                        } else if (categoryName!=null&&categoryName.equalsIgnoreCase("Offers")) {
                            getOfferSubCategory();
                        } else if (categoryName!=null&&categoryName.equalsIgnoreCase("Popular Products")) {
                            getSubCategory();
                        } else if (categoryName!=null&&categoryName.equalsIgnoreCase("New Products")) {
                            getNewSubProductCategory();
                        }
                        //getUsers();
                    }
                }, 1000);
            }
        });

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SubCategoryActivity.this, PlaceOrderActivity.class));
            }
        });

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"Low to High", "High to Low", "Sort By Name"};
                AlertDialog.Builder builder = new AlertDialog.Builder(SubCategoryActivity.this);
                builder.setTitle("Sort By")
                        .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ShowToast(" " + items[which]);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            }
        });

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
    }

    private void startSearch(CharSequence text) {
        List<OffersSubcategory>  offerFilteredList = new ArrayList<>();
        List<SubCategory>  subCategoryFilteredList = new ArrayList<>();
        List<NewProductSubcategory>  newProductSubcategoryFilteredList = new ArrayList<>();
        try{
            if(offersSubcategories!=null&&offersSubcategories.size()!=0){
                for(int i=0;i<offersSubcategories.size();i++) {
                    String cityName = "";

                    if(offersSubcategories.get (i).getName()!=null){
                        cityName= offersSubcategories.get (i).getName();
                    }

                    if(cityName.toLowerCase().contains(String.valueOf(text).toLowerCase())) {
                        offerFilteredList.add(offersSubcategories.get(i));
                    }
                }

                if(offerFilteredList.size()!=0&&offerFilteredList!=null){
                    no_search_result_found.setVisibility(View.GONE);
                    recyclerView.setLayoutManager(new GridLayoutManager(SubCategoryActivity.this,2));
                    OfferSubCategoryAdapter adapter = new OfferSubCategoryAdapter (SubCategoryActivity.this, offerFilteredList);
                    recyclerView.setAdapter (adapter);
                    adapter.notifyDataSetChanged();
                }else{
                    no_search_result_found.setVisibility(View.VISIBLE);
                }
            }

            if(subCategories!=null&&subCategories.size()!=0){
                for(int i=0;i<subCategories.size();i++) {
                    String cityName = "";

                    if(subCategories.get (i).getName()!=null){
                        cityName= subCategories.get (i).getName();
                    }

                    if(cityName.toLowerCase().contains(String.valueOf(text).toLowerCase())) {
                        subCategoryFilteredList.add(subCategories.get(i));
                    }
                }

                if(subCategoryFilteredList.size()!=0&&subCategoryFilteredList!=null){
                    no_search_result_found.setVisibility(View.GONE);
                    recyclerView.setLayoutManager(new GridLayoutManager(SubCategoryActivity.this,2));
                    SubCategoryAdapter adapter = new SubCategoryAdapter (SubCategoryActivity.this, subCategoryFilteredList);
                    recyclerView.setAdapter (adapter);
                    adapter.notifyDataSetChanged();
                }else{
                    no_search_result_found.setVisibility(View.VISIBLE);
                }
            }

            if(newProductSubcategories!=null&&newProductSubcategories.size()!=0){
                for(int i=0;i<newProductSubcategories.size();i++) {
                    String cityName = "";

                    if(newProductSubcategories.get (i).getName()!=null){
                        cityName= newProductSubcategories.get (i).getName();
                    }

                    if(cityName.toLowerCase().contains(String.valueOf(text).toLowerCase())) {
                        newProductSubcategoryFilteredList.add(newProductSubcategories.get(i));
                    }
                }

                if(newProductSubcategoryFilteredList.size()!=0&&newProductSubcategoryFilteredList!=null){
                    no_search_result_found.setVisibility(View.GONE);
                    recyclerView.setLayoutManager(new LinearLayoutManager(SubCategoryActivity.this));
                    NewProductSubCategoryAdapter adapter = new NewProductSubCategoryAdapter (SubCategoryActivity.this, newProductSubcategoryFilteredList);
                    recyclerView.setAdapter (adapter);
                    adapter.notifyDataSetChanged();
                }else{
                    no_search_result_found.setVisibility(View.VISIBLE);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
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

    private void getOfferSubCategory(final int subcategoryid) {
        progressBarUtil.showProgress();
        ProductApi mService = ApiClient.getClient().create(ProductApi.class);
        Call<OffersSubcategories> call = mService.getOfferSubCategory(subcategoryid);
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
                        for(OffersSubcategory offersSubcategory:offersSubcategories){
                            for(Map map: Constants.NEARBY_VENDERS_LOCATION){
                                if(map.getFirm_id().equalsIgnoreCase(offersSubcategory.getFirm_id())){
                                    list.add(offersSubcategory);
                                    Set<OffersSubcategory> newList = new LinkedHashSet<>(list);
                                    filteredList = new ArrayList<>(newList);
                                }
                            }
                        }
                        if(filteredList!=null&&filteredList.size()!=0){
                            Constants.OFFER_SUBCATEGORY = filteredList;
                            no_result_found.setVisibility(View.GONE);
                            no_vender_found.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new GridLayoutManager(SubCategoryActivity.this,2));
                            OfferSubCategoryAdapter mAdapter = new OfferSubCategoryAdapter(SubCategoryActivity.this, filteredList);
                            recyclerView.setAdapter(mAdapter);
                        }else{
                            no_vender_found.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    }else{
                        no_result_found.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(SubCategoryActivity.this, R.string.failed_due_to + statusCode, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<OffersSubcategories> call, Throwable t) {
                progressBarUtil.hideProgress();
                System.out.println("Suree : " + t.getMessage());

            }
        });
    }

    private void getSubCategory(final int subcategoryid) {
        progressBarUtil.showProgress();
        ProductApi  mService = ApiClient.getClient().create(ProductApi.class);
        Call<SubCategories> call = mService.getSubCategory(subcategoryid);
        call.enqueue(new Callback<SubCategories>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<SubCategories> call, Response<SubCategories> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    subCategories = response.body().getSubcategory();
                    progressBarUtil.hideProgress();
                    if(subCategories!=null&&subCategories.size()!=0){
                        List<SubCategory> list = new ArrayList<>();
                        List<SubCategory> filteredList = new ArrayList<>();
                        for (SubCategory subCategory : subCategories) {
                            for (Map map : Constants.NEARBY_VENDERS_LOCATION) {
                                if (map.getFirm_id().equalsIgnoreCase(subCategory.getFirm_id())) {
                                    list.add(subCategory);
                                    Set<SubCategory> newList = new LinkedHashSet<>(list);
                                    filteredList = new ArrayList<>(newList);
                                }
                            }
                        }
                        if(filteredList!=null&&filteredList.size()!=0) {
                            Constants.SUBCATEGORY = filteredList;
                            no_result_found.setVisibility(View.GONE);
                            no_vender_found.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new GridLayoutManager(SubCategoryActivity.this,2));
                            SubCategoryAdapter mAdapter = new SubCategoryAdapter(SubCategoryActivity.this, filteredList);
                            recyclerView.setAdapter(mAdapter);
                            runLayoutAnimation(recyclerView);
                        }else{
                            no_vender_found.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    }else{
                        no_result_found.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(SubCategoryActivity.this, R.string.failed_due_to + statusCode, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SubCategories> call, Throwable t) {
                progressBarUtil.hideProgress();
                System.out.println("Suree : " + t.getMessage());
            }
        });
    }

    private void getNewSubProductCategory(final int subcategoryid) {
        ProductApi mService = ApiClient.getClient().create(ProductApi.class);
        Call<NewProductSubcategories> call = mService.getNewProductSubCategory(subcategoryid);
        call.enqueue(new Callback<NewProductSubcategories>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<NewProductSubcategories> call, Response<NewProductSubcategories> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    newProductSubcategories = response.body().getNewProductSubcategory();
                    if(newProductSubcategories!=null&&newProductSubcategories.size()!=0){
                        List<NewProductSubcategory> list = new ArrayList<>();
                        List<NewProductSubcategory> filteredList = new ArrayList<>();
                        for (NewProductSubcategory newProductSubcategory : newProductSubcategories) {
                            for (Map map : Constants.NEARBY_VENDERS_LOCATION) {
                                if (map.getFirm_id().equalsIgnoreCase(newProductSubcategory.getFirm_id())) {
                                    list.add(newProductSubcategory);
                                    Set<NewProductSubcategory> newList = new LinkedHashSet<>(list);
                                    filteredList = new ArrayList<>(newList);
                                }
                            }
                        }
                        if(filteredList!=null&&filteredList.size()!=0){
                            no_result_found.setVisibility(View.GONE);
                            no_vender_found.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(SubCategoryActivity.this));
                            NewProductSubCategoryAdapter mAdapter = new NewProductSubCategoryAdapter(SubCategoryActivity.this, filteredList);
                            recyclerView.setAdapter(mAdapter);
                        }else{
                            no_vender_found.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                    }else{
                        no_result_found.setVisibility(View.VISIBLE);
                    }

                } else {
                    Toast.makeText(SubCategoryActivity.this, R.string.failed_due_to + statusCode, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<NewProductSubcategories> call, Throwable t) {
                System.out.println("Suree : " + t.getMessage());
            }
        });
    }

    private void getOfferSubCategory() {
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
                    if(offersSubcategories!=null&&offersSubcategories.size()!=0){
                        List<OffersSubcategory> list = new ArrayList<>();
                        List<OffersSubcategory> filteredList = new ArrayList<>();
                        for (OffersSubcategory offersSubcategory : offersSubcategories) {
                            for (Map map : Constants.NEARBY_VENDERS_LOCATION) {
                                if (map.getFirm_id().equalsIgnoreCase(offersSubcategory.getFirm_id())) {
                                    list.add(offersSubcategory);
                                    Set<OffersSubcategory> newList = new LinkedHashSet<>(list);
                                    filteredList = new ArrayList<>(newList);
                                }/*else if (map.getFirm_id().equalsIgnoreCase(offersSubcategory.getFirm_id())&&vender.getFirmId().equalsIgnoreCase(offersSubcategory.getFirm_id())) {
                                    list.add(offersSubcategory);
                                    Set<OffersSubcategory> newList = new LinkedHashSet<>(list);
                                    filteredList = new ArrayList<>(newList);
                                }*/
                            }
                        }
                        if(filteredList!=null&&filteredList.size()!=0){
                            Constants.OFFER_SUBCATEGORY = filteredList;
                            no_result_found.setVisibility(View.GONE);
                            no_vender_found.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new GridLayoutManager(SubCategoryActivity.this,2));
                            OfferSubCategoryAdapter mAdapter = new OfferSubCategoryAdapter(SubCategoryActivity.this, filteredList);
                            recyclerView.setAdapter(mAdapter);
                        }else{
                            no_vender_found.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                    }else{
                        no_result_found.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(SubCategoryActivity.this, R.string.failed_due_to + statusCode, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<OffersSubcategories> call, Throwable t) {
                progressBarUtil.hideProgress();
                System.out.println("Suree : " + t.getMessage());
            }
        });
    }

    private void getSubCategory() {
        progressBarUtil.showProgress();
        ProductApi  mService = ApiClient.getClient().create(ProductApi.class);
        Call<SubCategories> call = mService.getSubCategory();
        call.enqueue(new Callback<SubCategories>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<SubCategories> call, Response<SubCategories> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    subCategories = response.body().getSubcategory();
                    progressBarUtil.hideProgress();
                    if(subCategories!=null&&subCategories.size()!=0){
                        List<SubCategory> list = new ArrayList<>();
                        List<SubCategory> filteredList = new ArrayList<>();
                        for (SubCategory subCategory : subCategories) {
                            for (Map map : Constants.NEARBY_VENDERS_LOCATION) {
                                if (map.getFirm_id().equalsIgnoreCase(subCategory.getFirm_id())) {
                                    list.add(subCategory);
                                    Set<SubCategory> newList = new LinkedHashSet<>(list);
                                    filteredList = new ArrayList<>(newList);
                                }/*else if (map.getFirm_id().equalsIgnoreCase(subCategory.getFirm_id())&&vender.getFirmId().equalsIgnoreCase(subCategory.getFirm_id())) {
                                    list.add(subCategory);
                                    Set<SubCategory> newList = new LinkedHashSet<>(list);
                                    filteredList = new ArrayList<>(newList);
                                }*/
                            }
                        }
                        if(filteredList!=null&&filteredList.size()!=0){
                            Constants.SUBCATEGORY = filteredList;
                            no_result_found.setVisibility(View.GONE);
                            no_vender_found.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new GridLayoutManager(SubCategoryActivity.this,2));
                            SubCategoryAdapter mAdapter = new SubCategoryAdapter(SubCategoryActivity.this, filteredList);
                            recyclerView.setAdapter(mAdapter);
                            runLayoutAnimation(recyclerView);
                        }else{
                            no_vender_found.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                    }else{
                        no_result_found.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(SubCategoryActivity.this, R.string.failed_due_to + statusCode, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SubCategories> call, Throwable t) {
                progressBarUtil.hideProgress();
                System.out.println("Suree : " + t.getMessage());

            }
        });
    }

    private void getNewSubProductCategory() {
        ProductApi mService = ApiClient.getClient().create(ProductApi.class);
        Call<NewProductSubcategories> call = mService.getNewProductSubCategory();
        call.enqueue(new Callback<NewProductSubcategories>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<NewProductSubcategories> call, Response<NewProductSubcategories> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    newProductSubcategories = response.body().getNewProductSubcategory();
                    if(newProductSubcategories!=null&&newProductSubcategories.size()!=0){
                        List<NewProductSubcategory> list = new ArrayList<>();
                        List<NewProductSubcategory> filteredList = new ArrayList<>();
                        for (NewProductSubcategory newProductSubcategory : newProductSubcategories) {
                            for (Map map : Constants.NEARBY_VENDERS_LOCATION) {
                                if (map.getFirm_id().equalsIgnoreCase(newProductSubcategory.getFirm_id())) {
                                    list.add(newProductSubcategory);
                                    Set<NewProductSubcategory> newList = new LinkedHashSet<>(list);
                                    filteredList = new ArrayList<>(newList);
                                }
                            }
                        }
                        if(filteredList!=null&&filteredList.size()!=0){
                            no_result_found.setVisibility(View.GONE);
                            no_vender_found.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(SubCategoryActivity.this));
                            NewProductSubCategoryAdapter mAdapter = new NewProductSubCategoryAdapter(SubCategoryActivity.this, filteredList);
                            recyclerView.setAdapter(mAdapter);
                        }else{
                            no_vender_found.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                    }else{
                        no_result_found.setVisibility(View.VISIBLE);
                    }

                } else {
                    Toast.makeText(SubCategoryActivity.this, R.string.failed_due_to + statusCode, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<NewProductSubcategories> call, Throwable t) {
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

    @Override
    protected void onRestart() {
        super.onRestart();
        getCart();
        if(CATEGORY_NAME==null) {
            if (offercategoryId!=0) {
                getOfferSubCategory(offercategoryId);
            } else if (categoryId!=0) {
                getSubCategory(categoryId);
            } else if (newsubcategoryId!=0) {
                getNewSubProductCategory(newsubcategoryId);
            }
        }else {
            if (CATEGORY_NAME.equalsIgnoreCase("Offers")) {
                getOfferSubCategory();
            } else if (CATEGORY_NAME.equalsIgnoreCase("Popular Products")) {
                getSubCategory();
            } else if (CATEGORY_NAME.equalsIgnoreCase("New Products")) {
                getNewSubProductCategory();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCart();
        if(CATEGORY_NAME==null) {
            if (offercategoryId!=0) {
                getOfferSubCategory(offercategoryId);
            } else if (categoryId!=0) {
                getSubCategory(categoryId);
            } else if (newsubcategoryId!=0) {
                getNewSubProductCategory(newsubcategoryId);
            }
        }else {
            if (CATEGORY_NAME.equalsIgnoreCase("Offers")) {
                getOfferSubCategory();
            } else if (CATEGORY_NAME.equalsIgnoreCase("Popular Products")) {
                getSubCategory();
            } else if (CATEGORY_NAME.equalsIgnoreCase("New Products")) {
                getNewSubProductCategory();
            }
        }
    }

    @Override
    public void onBackPressed() {
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

    public class OfferSubCategoryAdapter extends RecyclerView.Adapter<OfferSubCategoryAdapter.MyViewHolder> {
        List<OffersSubcategory> offerSubcategoryList;
        Context context;
        String Tag;
        LocalStorage localStorage;
        Gson gson;
        List<Cart> cartList = new ArrayList<>();
        String weight;
        private int discount = 0, discountPercentage = 0, displayrate = 0, sellRate = 0,displayRate = 0, SubTotal = 0,Quantity = 0;

        public OfferSubCategoryAdapter(Context context, List<OffersSubcategory> offerSubcategoryList) {
            this.offerSubcategoryList = offerSubcategoryList;
            this.context = context;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subcategory_cardview, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
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
                if(offerSubcategory.getAcceptingOrder()==1){
                    holder.order_not_accepting.setVisibility(View.GONE);
                    holder.add_item_layout.setVisibility(View.VISIBLE);
                }else{
                    holder.order_not_accepting.setVisibility(View.VISIBLE);
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
                                   if (context instanceof SubCategoryActivity) {
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
            TextView discount, name, sellrate, quantity,displayRate,order_not_accepting,item_not_in_stock,discription;
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
                order_not_accepting = itemView.findViewById(R.id.order_not_accepting);
            }
        }
    }

    public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.MyViewHolder> {
        List<SubCategory> productList;
        Context context;
        String Tag;
        LocalStorage localStorage;
        Gson gson;
        List<Cart> cartList = new ArrayList<>();
        String weight;
        private int discount = 0, discountPercentage = 0, displayrate = 0,sellRate = 0,displayRate = 0, SubTotal = 0,Quantity = 0;

        public SubCategoryAdapter(Context context, List<SubCategory> productList) {
            this.productList = productList;
            this.context = context;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subcategory_cardview, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

            final SubCategory product = productList.get(position);
            localStorage = new LocalStorage(context);
            gson = new Gson();
            cartList = ((BaseActivity) context).getCartList();

            if(product!=null){
                if(product.getStock()==1){
                    holder.item_not_in_stock.setVisibility(View.GONE);
                    holder.add_item_layout.setVisibility(View.VISIBLE);
                }else{
                    holder.add_item_layout.setVisibility(View.GONE);
                    holder.item_not_in_stock.setVisibility(View.VISIBLE);
                }
                if(product.getAcceptingOrder()==1){
                    holder.order_not_accepting.setVisibility(View.GONE);
                    holder.add_item_layout.setVisibility(View.VISIBLE);
                }else{
                    holder.order_not_accepting.setVisibility(View.VISIBLE);
                    holder.add_item_layout.setVisibility(View.GONE);
                }
         /*       if(product.getAvailableStock()>0){
                    holder.item_not_in_stock.setVisibility(View.GONE);
                    holder.not_available.setVisibility(View.GONE);
                    holder.add_item_layout.setVisibility(View.VISIBLE);
                    holder.available_stock.setVisibility(View.VISIBLE);
                    holder.available_stock.setText("Available Stock: "+product.getAvailableStock());
                }else{
                    holder.item_not_in_stock.setVisibility(View.GONE);
                    holder.not_available.setVisibility(View.VISIBLE);
                    holder.available_stock.setVisibility(View.GONE);
                    holder.add_item_layout.setVisibility(View.GONE);
                    holder.add_item_layout.setVisibility(View.GONE);
                }*/

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
                                if (context instanceof SubCategoryActivity) {
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
            TextView discount, name, sellrate, quantity, displayRate,order_not_accepting,available_stock,item_not_in_stock,discription;
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
                item_not_in_stock = itemView.findViewById(R.id.item_not_in_stock);
                discription = itemView.findViewById(R.id.item_descriptions);
                order_not_accepting = itemView.findViewById(R.id.order_not_accepting);
            }
        }
    }

    public class NewProductSubCategoryAdapter extends RecyclerView.Adapter<NewProductSubCategoryAdapter.ViewHolder> {
        private Context mCtx;
        private List<NewProductSubcategory> categories;
        Gson gson;
        LocalStorage localStorage;
        List<Cart> cartList = new ArrayList<>();
        String Tag;
        String weight;
        private int discount = 0, discountPercentage = 0, displayrate = 0, sellRate = 0,displayRate = 0, SubTotal = 0, Quantity = 0, mSellrate = 0, mPerGmPrice = 0, mSellRate = 0;

        public NewProductSubCategoryAdapter(Context mCtx, List<NewProductSubcategory> categories) {

            this.mCtx = mCtx;
            this.categories = categories;
        }

        @Override
        public NewProductSubCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_product_subcategory, null);
            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(NewProductSubCategoryAdapter.ViewHolder holder, int position) {
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
                        TextViewAdapter mAdapter = new TextViewAdapter(mCtx, textArray);
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
                            mSellRate = subcategory.getSellRate();
                            displayRate = subcategory.getDisplayRate();

                         //   double value = 0.0,mPerGmPrice = 0.0,mSellRate = 0.0,mWeight = 0.0,mSubTotal = 0.0;
                       /*     value = ((double) sellRate) / 1000;
                           // mPerGmPrice = ((int)sellRate/1000);
                            mPerGmPrice = value;
                            holder.item_sellrate.setText("\u20B9 " + mPerGmPrice);
                            if(weight.contains("gm")){
                                weight = weight.replace("gm", "").trim();
                                mWeight = Integer.parseInt(weight);
                                mSellRate = (int)(mPerGmPrice * mWeight);
                                weight = weight+" gm";
                                holder.item_sellrate.setText("\u20B9 " + mSellRate);

                            }else if(weight.contains("dozan")){
                                weight = weight.replace("dozan", "").trim();
                                mSellRate = (int)subcategory.getSellRate();
                                weight = weight+" dozan";
                                holder.item_sellrate.setText("\u20B9 " + mSellRate);

                            }else if(weight.contains("kg")){
                                weight = weight.replace("kg", "").trim();
                                mWeight = Double.parseDouble(weight) * 1000;
                                mSellRate = mPerGmPrice * mWeight;
                                weight = weight+" kg";
                                holder.item_sellrate.setText("\u20B9 " + mSellRate);
                            }*/

                            Quantity = 1;
                            if (subcategory.getId() != 0 && subcategory.getName() != null && subcategory.getLink() != null && subcategory.getDiscription() != null && mSellRate != 0 && displayrate != 0 && weight != null && subcategory.getFirm_id() != null) {
                                sellRate = subcategory.getSellRate();
                                displayRate = subcategory.getDisplayRate();
                                Quantity = 1;
                                SubTotal = (int) (mSellRate * Quantity);
                                if (mCtx instanceof SubCategoryActivity) {
                                    Cart cart = new Cart(subcategory.getId(), subcategory.getName(), subcategory.getLink(), subcategory.getDiscription(), SubTotal, displayRate, SubTotal, 1, weight, subcategory.getFirm_id());
                                    cartList = ((BaseActivity) mCtx).getCartList();
                                    cartList.add(cart);
                                    String cartStr = gson.toJson(cartList);
                                    localStorage.setCart(cartStr);
                                    ((AddorRemoveCallbacks) mCtx).onAddProduct();
                                    notifyItemChanged(position);
                                   // weight = null;
                                    getCart();
                                   // startActivity(new Intent(mCtx, PlaceOrderActivity.class));
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

        public class TextViewAdapter extends RecyclerView.Adapter<TextViewAdapter.ViewHolder> {

            private Context mCtx;
            private final String[] textViewValues;

            public TextViewAdapter(Context mCtx, String[] textViewValues) {
                this.mCtx = mCtx;
                this.textViewValues = textViewValues;
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.text, null);
                TextViewAdapter.ViewHolder viewHolder = new TextViewAdapter.ViewHolder(itemLayoutView);
                return viewHolder;
            }

            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
                else {
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

