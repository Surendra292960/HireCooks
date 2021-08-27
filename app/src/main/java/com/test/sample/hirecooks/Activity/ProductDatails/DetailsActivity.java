package com.test.sample.hirecooks.Activity.ProductDatails;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;
import com.test.sample.hirecooks.Activity.AddorRemoveCallbacks;
import com.test.sample.hirecooks.Activity.Orders.PlaceOrderActivity;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Libraries.Slider.ProductImgSlider;
import com.test.sample.hirecooks.Models.Images.Images;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.OfferSubCategory.OffersSubcategory;
import com.test.sample.hirecooks.Models.SearchSubCategory.Search;
import com.test.sample.hirecooks.Models.SubCategory.Color;
import com.test.sample.hirecooks.Models.SubCategory.ColorExample;
import com.test.sample.hirecooks.Models.SubCategory.Example;
import com.test.sample.hirecooks.Models.SubCategory.Response.SubCategory;
import com.test.sample.hirecooks.Models.SubCategory.Size;
import com.test.sample.hirecooks.Models.SubCategory.SizeExample;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import com.test.sample.hirecooks.Models.SubCategory.Weight;
import com.test.sample.hirecooks.Models.SubCategory.WeightExample;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.LocalStorage;
import com.test.sample.hirecooks.SubCategoryActivity;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.WebApis.ProductApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class DetailsActivity extends BaseActivity {
    private RelativeLayout bottom_anchor_layout;
    private TextView item_not_in_stock, product_discount,itemAdd, itemRemove,item_count,checkout_amount,checkout,product_displayRate,item_sellrate,item_name,addToCart,quantity;
    private List<Subcategory> cartList;
    private List<Subcategory> FavouriteList;
    private int Quantity = 0, FavQuantity = 0, sellRate = 0,displayRate = 0, SubTotal = 0;
    private RecyclerView subcategory_recycler;
    private Subcategory subCategory;
    ViewPager viewPager;
    private int dotscount;
    private ImageView[] dots;
    WormDotsIndicator sliderDotspanel;
    TextView[] dot;
    int page_position = 0;
    private List<SubCategory> subCategoryList;
    private List<OffersSubcategory> offersSubcategoryList;
    private List<Search> searchList;
   // private SubCategoryHorizontalAdapter subCategoryHorizontalAdapter;
    private ImageView image1,image2,image3,image4,item_favourite;
    private View bottom_anchor;
    private LinearLayout layout_action_share, layout_action_favourite,add_item_layout,add_quantity_layout,order_not_accepting;
    private LocalStorage localStorage;
    private Gson gson;
    private Search search;
    private Timer timer;
    private List<Images> images;
    private int discount;
    private RecyclerView weight_recycler,sizes_recycler,colors_recycler,images_recycler;
    private ProgressBar size_progress,color_progress,weight_progress;
    Size size;
    Weight weight;
    Color color;
    private List<Color> colorsList;
    private List<Size> sizeList;
    private List<Weight> weightList;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Product Details");
        initViews();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            subCategory = (Subcategory) bundle.getSerializable( "SubCategory" );
            if (subCategory != null) {
                getSubCategory(Integer.parseInt( subCategory.getSubcategoryid() ));
                getCart();
                if (subCategory.getImages() != null && subCategory.getImages().size() != 0) {
                    ProductImgSlider pagerAdapter = new ProductImgSlider( DetailsActivity.this, subCategory );
                    viewPager.setAdapter( pagerAdapter );
                    sliderDotspanel.setViewPager( viewPager );
                    if (subCategory.getSizes() != null && subCategory.getSizes().size() != 0) {
                        getProductColor( subCategory.getProductUniquekey() );
                        getProductSize( subCategory.getProductUniquekey() );
                        getProductWeight( subCategory.getProductUniquekey() );
                    }
                }
                if(subCategory.getSellRate()!=0&&subCategory.getDisplayRate()!=0&&!subCategory.getName().isEmpty()){
                    item_sellrate.setText("\u20B9 " + subCategory.getSellRate());
                    SpannableString spanString = new SpannableString("  \u20B9 " + subCategory.getDisplayRate());
                    spanString.setSpan(new StrikethroughSpan(), 0, spanString.length(), 0);
                    product_displayRate.setText(spanString);
                    discount = (subCategory.getDisplayRate() - subCategory.getSellRate());
                    product_discount.setText("Save " + (discount * 100 / subCategory.getDisplayRate()) + " %");
                }
            }
        }
        getCart();
        getFavourites();
    }

    private void initViews() {
        localStorage = new LocalStorage( this );
        gson = new Gson();

        viewPager = findViewById(R.id.viewPager);
        sliderDotspanel = findViewById(R.id.worm_dots_indicator);
        weight_recycler = findViewById(R.id.weight_recycler);
        sizes_recycler = findViewById(R.id.sizes_recycler);
        colors_recycler = findViewById(R.id.colors_recycler);
        images_recycler = findViewById(R.id.images_recycler);
        weight_progress= findViewById(R.id.weight_progress);
        color_progress= findViewById(R.id.color_progress);
        size_progress= findViewById(R.id.size_progress);
        subcategory_recycler = findViewById( R.id.subcategory_recycler );
        bottom_anchor_layout = findViewById( R.id.bottom_anchor_layout );
        layout_action_share = findViewById( R.id.layout_action_share );
        layout_action_favourite = findViewById( R.id.layout_action_favourite );
        item_favourite = findViewById( R.id.item_favourite );
        add_item_layout = findViewById( R.id.add_item_layout );
        add_quantity_layout = findViewById( R.id.quantity_ll );
        addToCart = findViewById( R.id.add_ );
        quantity = findViewById( R.id.item_count );
        item_not_in_stock = findViewById( R.id.item_not_in_stock );
        order_not_accepting = findViewById( R.id.order_not_accepting );
        itemRemove = findViewById( R.id.remove_item );
        itemAdd = findViewById( R.id.add_item );
        item_sellrate = findViewById( R.id.product_sellrate );
        product_displayRate = findViewById( R.id.product_displayRate );
        product_discount = findViewById( R.id.product_discount );
        item_name = findViewById( R.id.product_name );

        View view = findViewById( R.id.footerView );
        item_count = view.findViewById( R.id.item_count );
        bottom_anchor = view.findViewById( R.id.bottom_anchor );
        checkout_amount = view.findViewById( R.id.checkout_amount );
        checkout = view.findViewById( R.id.checkout );
        bottom_anchor_layout = findViewById(R.id.bottom_anchor_layout);

        checkout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( DetailsActivity.this, PlaceOrderActivity.class ) );
            }
        });

        layout_action_favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subCategory != null && FavQuantity == 0) {
                    sellRate = subCategory.getSellRate();
                    displayRate = subCategory.getDisplayRate();
                    FavQuantity = 1;
                    if (subCategory.getId() != 0 && subCategory.getName() != null && subCategory.getLink2() != null && subCategory.getDiscription() != null && sellRate != 0 && subCategory.getDisplayRate() != 0 && subCategory.getFirmId() != null) {
                        SubTotal = (sellRate * FavQuantity);
                        if (DetailsActivity.this instanceof DetailsActivity) {
                            Subcategory cart = new Subcategory(subCategory.getId(),subCategory.getSubcategoryid(),subCategory.getLastUpdate(),subCategory.getSearchKey(), subCategory.getName(), subCategory.getProductUniquekey(), subCategory.getLink2(),  subCategory.getLink3(),  subCategory.getLink4(),subCategory.getShieldLink(), subCategory.getDiscription(), subCategory.getDetailDiscription(), sellRate, displayRate,subCategory.getFirmId(),subCategory.getFirmLat(),subCategory.getFirmLng(),subCategory.getFirmAddress(),subCategory.getFrimPincode(),subCategory.getColors(),subCategory.getImages(),subCategory.getSizes(),subCategory.getWeights(), SubTotal, 1,subCategory.getBrand(),subCategory.getGender(),subCategory.getAge());
                            FavouriteList = getFavourite();
                            FavouriteList.add( cart );
                            String favourite = gson.toJson( FavouriteList );
                            localStorage.setFavourite( favourite );
                            ((AddorRemoveCallbacks) DetailsActivity.this).onAddProduct();
                            getFavourites();
                            Toast.makeText( DetailsActivity.this, "Added to Wishlist", Toast.LENGTH_SHORT ).show();
                        }
                    } else {
                        Toast.makeText( DetailsActivity.this, "This item can`t be add please remove item and try again", Toast.LENGTH_SHORT ).show();
                    }
                }else{
                    if(subCategory!=null&&FavouriteList.size()!=0&&FavQuantity>0){
                        FavQuantity = 0;
                        sellRate = subCategory.getSellRate();
                        displayRate = subCategory.getDisplayRate();;
                        for (int i = 0; i < FavouriteList.size(); i++) {
                            if (FavouriteList.get(i).getId()==subCategory.getId()&&FavouriteList.get(i).getName().equalsIgnoreCase(subCategory.getName())&&FavouriteList.get(i).getSellRate()==subCategory.getSellRate()) {
                                SubTotal = (sellRate * FavQuantity);
                                FavouriteList.get(i).setItemQuantity(FavQuantity);
                                FavouriteList.get(i).setTotalAmount(SubTotal);
                                String cartStr = gson.toJson(FavouriteList);
                                localStorage.setFavourite(cartStr);
                                item_favourite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
                                getFavourites();
                            }
                        }
                    }
                }
            }
        });
        
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(subCategory!=null){
                    if(subCategory.getWeights().size()!=0){
                        if(weight!=null){
                            weightList = new ArrayList<>(  );
                            weightList.add( weight );
                        }else{
                            showalertbox("Please Select Weight");
                            return;
                        }
                    }if(subCategory.getSizes().size()!=0){
                        if(size!=null){
                            sizeList = new ArrayList<>(  );
                            sizeList.add( size );
                        }else{
                            showalertbox("Please Select Size");
                            return;
                        }
                    }if(subCategory.getColors().size()!=0){
                        if(color!=null){
                            colorsList = new ArrayList<>(  );
                            colorsList.add( color );
                        }else{
                            showalertbox("Please Select Color");
                            return;
                        }
                    }
                    addToCart.setVisibility(View.GONE);
                    add_quantity_layout.setVisibility(View.VISIBLE);
                    sellRate = subCategory.getSellRate();
                    displayRate = subCategory.getDisplayRate();
                    Quantity = 1;
                    if (subCategory.getId() != 0 && subCategory.getName() != null && subCategory.getLink2() != null && subCategory.getDiscription() != null && sellRate != 0 && displayRate != 0 && subCategory.getFirmId() != null) {
                        SubTotal = (sellRate * Quantity);
                        if (DetailsActivity.this instanceof DetailsActivity) {
                            Subcategory cart = new Subcategory(subCategory.getId(),subCategory.getSubcategoryid(),subCategory.getLastUpdate(),subCategory.getSearchKey(), subCategory.getName(), subCategory.getProductUniquekey(), subCategory.getLink2(),  subCategory.getLink3(),  subCategory.getLink4(),subCategory.getShieldLink(), subCategory.getDiscription(), subCategory.getDetailDiscription(), sellRate, displayRate,subCategory.getFirmId(),subCategory.getFirmLat(),subCategory.getFirmLng(),subCategory.getFirmAddress(),subCategory.getFrimPincode(),colorsList,subCategory.getImages(),sizeList,weightList, SubTotal, 1,subCategory.getBrand(),subCategory.getGender(),subCategory.getAge());
                            cartList = getnewCartList();
                            cartList.add(cart);
                            String cartStr = gson.toJson(cartList);
                            localStorage.setCart(cartStr);
                            ((AddorRemoveCallbacks) DetailsActivity.this).onAddProduct();
                            color=null;
                            weight=null;
                            size=null;
                            getProductColor( subCategory.getProductUniquekey() );
                            getProductSize( subCategory.getProductUniquekey() );
                            getProductWeight( subCategory.getProductUniquekey() );
                            getCart();
                    }else{
                        addToCart.setVisibility(View.VISIBLE);
                        add_quantity_layout.setVisibility(GONE);
                        Toast.makeText(DetailsActivity.this, "This item can`t be add please remove item and try again", Toast.LENGTH_SHORT).show();
                    }
                    }
                }
            }
        });

        itemAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(subCategory!=null){
                    sellRate = subCategory.getSellRate();
                    displayRate = subCategory.getDisplayRate();
                    String count = quantity.getText().toString();
                    Quantity = Integer.parseInt(count);
                    if (Quantity >= 1) {
                        Quantity++;
                        quantity.setText(""+(Quantity));
                        for (int i = 0; i < cartList.size(); i++) {
                            if (cartList.get(i).getId()==subCategory.getId()&&cartList.get(i).getName().equalsIgnoreCase(subCategory.getName())&&cartList.get(i).getSellRate()==subCategory.getSellRate()) {
                                SubTotal = (sellRate * Quantity);
                                cartList.get(i).setItemQuantity(Quantity);
                                cartList.get(i).setTotalAmount(SubTotal);
                                String cartStr = gson.toJson(cartList);
                                localStorage.setCart(cartStr);
                             //   subcategoryAdapter.notifyDataSetChanged();
                                getCart();
                            }
                        }
                    }
                }
                 if(search!=null){
                    sellRate = search.getSellRate();
                    displayRate = search.getDisplayRate();
                    String count = quantity.getText().toString();
                    Quantity = Integer.parseInt(count);
                    if (Quantity >= 1) {
                        Quantity++;
                        quantity.setText(""+(Quantity));
                        for (int i = 0; i < cartList.size(); i++) {
                            if (cartList.get(i).getId()==search.getId()&&cartList.get(i).getName().equalsIgnoreCase(search.getName())&&cartList.get(i).getSellRate()==search.getSellRate()) {
                                SubTotal = (sellRate * Quantity);
                                cartList.get(i).setItemQuantity(Quantity);
                                cartList.get(i).setTotalAmount(SubTotal);
                                String cartStr = gson.toJson(cartList);
                                localStorage.setCart(cartStr);
                                //searchAdapter.notifyDataSetChanged();
                                getCart();
                            }
                        }
                    }
                }
            }
        });

        itemRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(subCategory!=null){
                    sellRate = subCategory.getSellRate();
                    displayRate = subCategory.getDisplayRate();
                    String count = quantity.getText().toString();
                    Quantity = Integer.parseInt(count);
                    if (Quantity > 1) {
                        Quantity--;
                        quantity.setText(""+(Quantity));
                        for (int i = 0; i < cartList.size(); i++) {
                            if (cartList.get(i).getId()==subCategory.getId()&&cartList.get(i).getName().equalsIgnoreCase(subCategory.getName())&&cartList.get(i).getSellRate()==subCategory.getSellRate()) {
                                SubTotal = (sellRate * Quantity);
                                cartList.get(i).setItemQuantity(Quantity);
                                cartList.get(i).setTotalAmount(SubTotal);
                                String cartStr = gson.toJson(cartList);
                                localStorage.setCart(cartStr);
                                getCart();
                            }
                        }
                    }
                } if(search!=null){
                    sellRate = search.getSellRate();
                    displayRate = search.getDisplayRate();
                    String count = quantity.getText().toString();
                    Quantity = Integer.parseInt(count);
                    if (Quantity > 1) {
                        Quantity--;
                        quantity.setText(""+(Quantity));
                        for (int i = 0; i < cartList.size(); i++) {
                            if (cartList.get(i).getId()==search.getId()&&cartList.get(i).getName().equalsIgnoreCase(search.getName())&&cartList.get(i).getSellRate()==search.getSellRate()) {
                                SubTotal = (sellRate * Quantity);
                                cartList.get(i).setItemQuantity(Quantity);
                                cartList.get(i).setTotalAmount(SubTotal);
                                String cartStr = gson.toJson(cartList);
                                localStorage.setCart(cartStr);
                               // searchAdapter.notifyDataSetChanged();
                                getCart();
                            }
                        }
                    }
                }
            }
        });

        layout_action_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(subCategory!=null&&subCategory.getLink2()!=null){
                    shareItem(subCategory.getLink2());
                }
            }
        });
    }

    private void getSubCategory(int subcategoryid) {
        ProductApi mService = ApiClient.getClient().create(ProductApi.class);
        Call<ArrayList<Example>> call = mService.getSubCategorysBySub_id(subcategoryid);
        call.enqueue(new Callback<ArrayList<Example>>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<ArrayList<Example>> call, Response<ArrayList<Example>> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    if(response.body()!=null&&response.body().size()!=0){
                        List<Subcategory> list = new ArrayList<>();
                        List<Subcategory> filteredList = new ArrayList<>();
                        for (Example example : response.body()) {
                            for (Subcategory subcategory : example.getSubcategory()) {
                                for (Map map : Constants.NEARBY_VENDERS_LOCATION) {
                                    if (map.getFirm_id().equalsIgnoreCase(subcategory.getFirmId())) {
                                        list.add(subcategory);
                                        Set<Subcategory> newList = new LinkedHashSet<>(list);
                                        filteredList = new ArrayList<>(newList);
                                    }
                                }
                            }
                        }
                        if(filteredList!=null&&filteredList.size()!=0) {
                            Constants.SUBCATEGORYs = filteredList;
                            subcategory_recycler.setVisibility( GONE);
                            //   no_vender_found.setVisibility(View.GONE);
                            subcategory_recycler.setVisibility(View.VISIBLE);
                            subcategory_recycler.setHasFixedSize(true);
                            // subcategory_recycler.setLayoutManager(new GridLayoutManager( SubCategoryActivity.this,2));
                            SubcategoryAdapter mAdapter = new SubcategoryAdapter(DetailsActivity.this, filteredList);
                            subcategory_recycler.setAdapter(mAdapter);
                        }else{
                            //no_vender_found.setVisibility(View.VISIBLE);
                            subcategory_recycler.setVisibility( GONE);
                        }
                    }else{
                        // no_result_found.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText( DetailsActivity.this, R.string.failed_due_to + statusCode, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Example>> call, Throwable t) {
                System.out.println("Suree : " + t.getMessage());
            }
        });
    }

    public void shareItem(String url) {
        Picasso.with(getApplicationContext()).load(url).into(new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                startActivity(Intent.createChooser(i, "Share Image"));
            }
            @Override public void onBitmapFailed(Drawable errorDrawable) { }
            @Override public void onPrepareLoad(Drawable placeHolderDrawable) { }
        });
    }

    public Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file =  new File(getExternalFilesDir( Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    boolean isBottomAnchorNavigationHide = false;

    private void animateBottomAnchor(final boolean hide) {
        if (isBottomAnchorNavigationHide && hide || !isBottomAnchorNavigationHide && !hide) return;
        isBottomAnchorNavigationHide = hide;
        int moveY = hide ? (2 * bottom_anchor.getHeight()) : 0;
        bottom_anchor.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    boolean isSearchBarHide = false;


    private void showalertbox(String string) {
        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder( DetailsActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.show_alert_message,null);
        TextView ask = view.findViewById( R.id.ask );
        TextView textView = view.findViewById( R.id.text );
        ask.setText( string );
        textView.setText( "Alert !" );
        AppCompatTextView cancelBtn = view.findViewById(R.id.exit_app_btn);
        dialogBuilder.setView(view);
        final android.app.AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        cancelBtn.setOnClickListener( v -> {
            try {
                dialog.dismiss();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        } );
    }

    private void getCart() {
        cartList = new ArrayList<>();
        cartList = getnewCartList();
        if (!cartList.isEmpty()) {
            animateBottomAnchor( false );
            for (int i = 0; i < cartList.size(); i++) {
                if (cartList.get( i ).getItemQuantity() <= 1 && cartList.size() <= 1) {
                    bottom_anchor_layout.setAnimation( AnimationUtils.loadAnimation( this, R.anim.fade_transition_animation ) );
                }
                if(subCategory!=null){
                    if (cartList.get(i).getId()==subCategory.getId()&&cartList.get(i).getName().equalsIgnoreCase(subCategory.getName())) {
                        addToCart.setVisibility(GONE);
                        add_quantity_layout.setVisibility(View.VISIBLE);
                        quantity.setText(""+cartList.get(i).getItemQuantity());
                    }
                }
            }
            bottom_anchor_layout.setVisibility( View.VISIBLE );
            checkout_amount.setText( "₹  " + getTotalPrice() );
            item_count.setText( "" + cartCount() );

        } else {
            bottom_anchor_layout.setVisibility( GONE );
        }
    }


    private void getFavourites(){
        FavouriteList = new ArrayList<>();
        FavouriteList = getFavourite();
        if (!FavouriteList.isEmpty()) {
            for(int i=0; i<FavouriteList.size(); i++) {
                if (FavouriteList.get(i).getItemQuantity() <= 1 && FavouriteList.size() <= 1) {
                    //FavQuantity = cartList.get(i).getItemQuantity();
                }
                if(subCategory!=null){
                    if (FavouriteList.get(i).getId()==subCategory.getId()&&FavouriteList.get(i).getName().equalsIgnoreCase(subCategory.getName())) {
                        item_favourite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
                        FavQuantity = FavouriteList.get(i).getItemQuantity();
                    }else{
                        FavQuantity = 0;
                    }
                }
            }
        }
    }

    private void getProductWeight(String productUniquekey) {
        weight_progress.setVisibility(View.VISIBLE);
        ProductApi mService = ApiClient.getClient().create( ProductApi.class );
        Call<List<WeightExample>> call = mService.getProductWeight( productUniquekey );
        call.enqueue( new Callback<List<WeightExample>>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<List<WeightExample>> call, Response<List<WeightExample>> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    for (WeightExample weightExample1 : response.body()) {
                        if (weightExample1.getError() == false) {
                            weight_progress.setVisibility( View.GONE );
                            weight_recycler.setVisibility(View.VISIBLE);
                            DetailsActivity.WeightAdapter adapter = new DetailsActivity.WeightAdapter( DetailsActivity.this,weightExample1.getWeights() );
                            weight_recycler.setAdapter( adapter );
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DetailsActivity.this);
                            if (DetailsActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
                            }else{
                                linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
                            }
                            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                            params.setMargins(20, 20, 20, 20);
                            linearLayoutManager.canScrollHorizontally();

                            weight_recycler.setLayoutManager(linearLayoutManager);
                            weight_recycler.setItemAnimator(new DefaultItemAnimator());
                        } else {
                            Toast.makeText( DetailsActivity.this, weightExample1.getMessage(), Toast.LENGTH_SHORT ).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<WeightExample>> call, Throwable t) {
                System.out.println( "Suree : " + t.getMessage() );
            }
        } );
    }

    private void getProductSize(String productUniquekey) {
        size_progress.setVisibility(View.VISIBLE);
        ProductApi mService = ApiClient.getClient().create(ProductApi.class);
        Call<List<SizeExample>> call = mService.getProductSize(productUniquekey);
        call.enqueue(new Callback<List<SizeExample>>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<List<SizeExample>> call, Response<List<SizeExample>> response) {
                int statusCode = response.code();
                if(statusCode==200) {
                    for(SizeExample sizeExample1:response.body()){
                        if(sizeExample1.getError()==false){
                            size_progress.setVisibility( View.GONE );
                            sizes_recycler.setVisibility(View.VISIBLE);
                            DetailsActivity.SizeAdapter adapter = new DetailsActivity.SizeAdapter( DetailsActivity.this,sizeExample1.getSizes() );
                            sizes_recycler.setAdapter( adapter );
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DetailsActivity.this);
                            if (DetailsActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
                            }else{
                                linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
                            }
                            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                            params.setMargins(20, 20, 20, 20);
                            linearLayoutManager.canScrollHorizontally();

                            sizes_recycler.setLayoutManager(linearLayoutManager);
                            sizes_recycler.setItemAnimator(new DefaultItemAnimator());
                        }else{
                            Toast.makeText( DetailsActivity.this, sizeExample1.getMessage(), Toast.LENGTH_SHORT ).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SizeExample>> call, Throwable t) {
                System.out.println("Suree : "+t.getMessage());
            }
        });
    }

    private void getProductColor(String productUniquekey) {
        color_progress.setVisibility(View.VISIBLE);
        ProductApi mService = ApiClient.getClient().create(ProductApi.class);
        Call<List<ColorExample>> call = mService.getProductColor(productUniquekey);
        call.enqueue(new Callback<List<ColorExample>>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<List<ColorExample>> call, Response<List<ColorExample>> response) {
                int statusCode = response.code();
                if(statusCode==200) {
                    for(ColorExample colorExample1:response.body()){
                        if(colorExample1.getError()==false){
                            color_progress.setVisibility( View.GONE );
                            colors_recycler.setVisibility(View.VISIBLE);
                            DetailsActivity.ColorsAdapter adapter = new DetailsActivity.ColorsAdapter( DetailsActivity.this,colorExample1.getColors() );
                            colors_recycler.setAdapter( adapter );
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DetailsActivity.this);
                            if (DetailsActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                linearLayoutManager.setOrientation( LinearLayout.HORIZONTAL);
                            }else{
                                linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
                            }
                            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                            params.setMargins(20, 20, 20, 20);
                            linearLayoutManager.canScrollHorizontally();

                            colors_recycler.setLayoutManager(linearLayoutManager);
                            colors_recycler.setItemAnimator(new DefaultItemAnimator());
                        }else{
                            Toast.makeText( DetailsActivity.this, colorExample1.getMessage(), Toast.LENGTH_SHORT ).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ColorExample>> call, Throwable t) {
                System.out.println("Suree : "+t.getMessage());
            }
        });
    }

    public class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.ViewHolder> {

        private Context mCtx;
        List<Weight> weights;

        public WeightAdapter(Context mCtx, List<Weight> weights) {
            this.mCtx = mCtx;
            this.weights = weights;
        }

        @NonNull
        @Override
        public WeightAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.text, null);
            WeightAdapter.ViewHolder viewHolder = new WeightAdapter.ViewHolder(itemLayoutView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull WeightAdapter.ViewHolder holder, int position) {
            final Weight weight1 = weights.get( position );
            if(weight1.getKg()!=0){
                holder.item_weight.setVisibility( View.VISIBLE );
                holder.item_weight_dozan.setVisibility( View.GONE );
                holder.item_weight_pond.setVisibility( View.GONE );
                holder.item_weight.setText(""+weight1.getKg());
            }else if(weight1.getDozan()!=0){
                holder.item_weight_dozan.setVisibility( View.VISIBLE );
                holder.item_weight.setVisibility( GONE );
                holder.item_weight_pond.setVisibility( GONE );
                holder.item_weight_dozan.setText(weight1.getDozan()+"\n Dozan");
            }else if(weight1.getPond()!=0){
                holder.item_weight_dozan.setVisibility( GONE );
                holder.item_weight.setVisibility( GONE );
                holder.item_weight_pond.setVisibility( VISIBLE );
                holder.item_weight_dozan.setText(""+weight1.getDozan());
            }
            holder.item_weight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    weight= weight1;
                    notifyDataSetChanged();
                }
            });holder.item_weight_dozan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    weight= weight1;
                    notifyDataSetChanged();
                }
            });holder.item_weight_pond.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    weight= weight1;
                    notifyDataSetChanged();
                }
            });

            if(weight==weight1){
                holder.item_weight.setBackgroundColor( android.graphics.Color.parseColor("#567845"));
                holder.item_weight.setTextColor( android.graphics.Color.parseColor("#ffffff"));
                holder.item_weight_dozan.setBackgroundColor( android.graphics.Color.parseColor("#567845"));
                holder.item_weight_dozan.setTextColor( android.graphics.Color.parseColor("#ffffff"));
                holder.item_weight_pond.setBackgroundColor( android.graphics.Color.parseColor("#567845"));
                holder.item_weight_pond.setTextColor( android.graphics.Color.parseColor("#ffffff"));
            }
            else
            {
                holder.item_weight.setBackgroundColor( android.graphics.Color.parseColor("#ffffff"));
                holder.item_weight.setTextColor( android.graphics.Color.parseColor("#000000"));
                holder.item_weight_dozan.setBackgroundColor( android.graphics.Color.parseColor("#ffffff"));
                holder.item_weight_dozan.setTextColor( android.graphics.Color.parseColor("#000000"));
                holder.item_weight_pond.setBackgroundColor( android.graphics.Color.parseColor("#ffffff"));
                holder.item_weight_pond.setTextColor( android.graphics.Color.parseColor("#000000"));
            }
        }

        @Override
        public int getItemCount() {
            return weights == null ? 0 : weights.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public Button item_weight;
            public TextView item_weight_dozan,item_weight_pond;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                item_weight = itemLayoutView.findViewById(R.id.item_weight);
                item_weight_dozan = itemLayoutView.findViewById(R.id.item_size_text);
                item_weight_pond = itemLayoutView.findViewById(R.id.item_weight_pond);
            }
        }
    }

    public class SizeAdapter extends RecyclerView.Adapter<SizeAdapter.ViewHolder> {
        private Context mCtx;
        List<Size> sizes;

        public SizeAdapter(Context mCtx, List<Size> sizes) {
            this.mCtx = mCtx;
            this.sizes = sizes;
        }

        @NonNull
        @Override
        public SizeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.text, null);
            SizeAdapter.ViewHolder viewHolder = new SizeAdapter.ViewHolder(itemLayoutView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull SizeAdapter.ViewHolder holder, int position) {
            final Size size1 = sizes.get( position );
            if(!size1.getSizeText().isEmpty()&&size1.getSizeText()!=null){
                holder.item_size_text.setVisibility( View.VISIBLE );
                holder.item_size.setVisibility( View.GONE );
                holder.item_size_text.setText( size1.getSizeText() );
            }else if(size1.getSizeNumber()!=0){
                holder.item_size.setVisibility( View.VISIBLE );
                holder.item_size_text.setVisibility( GONE );
                holder.item_size.setText(String.valueOf( size1.getSizeNumber()));
            }
            holder.item_size.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    size= size1;
                    notifyDataSetChanged();
                }
            });
            holder.item_size_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    size= size1;
                    notifyDataSetChanged();
                }
            });

            if(size==size1){
                holder.item_size.setBackgroundColor( android.graphics.Color.parseColor("#567845"));
                holder.item_size.setTextColor( android.graphics.Color.parseColor("#ffffff"));
                holder.item_size_text.setBackgroundColor( android.graphics.Color.parseColor("#567845"));
                holder.item_size_text.setTextColor( android.graphics.Color.parseColor("#ffffff"));
            }
            else
            {
                holder.item_size.setBackgroundColor( android.graphics.Color.parseColor("#ffffff"));
                holder.item_size.setTextColor( android.graphics.Color.parseColor("#000000"));
                holder.item_size_text.setBackgroundColor( android.graphics.Color.parseColor("#ffffff"));
                holder.item_size_text.setTextColor( android.graphics.Color.parseColor("#000000"));
            }
        }

        @Override
        public int getItemCount() {
            return sizes == null ? 0 : sizes.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public Button item_size;
            public TextView item_size_text;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                item_size = itemLayoutView.findViewById(R.id.item_weight);
                item_size_text = itemLayoutView.findViewById(R.id.item_size_text);
            }
        }
    }

    public class ColorsAdapter extends RecyclerView.Adapter<ColorsAdapter.ViewHolder> {
        private Context mCtx;
        List<com.test.sample.hirecooks.Models.SubCategory.Color> colors;

        public ColorsAdapter(Context mCtx, List<com.test.sample.hirecooks.Models.SubCategory.Color> colors) {
            this.mCtx = mCtx;
            this.colors = colors;
        }

        @NonNull
        @Override
        public ColorsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.text, null);
            ColorsAdapter.ViewHolder viewHolder = new ColorsAdapter.ViewHolder(itemLayoutView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ColorsAdapter.ViewHolder holder, int position) {
            final com.test.sample.hirecooks.Models.SubCategory.Color color1 = colors.get( position );
            holder.item_color.setVisibility( VISIBLE );
            holder.item_color.setText(color1.getColorName());
            holder.item_color.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    color= color1;
                    notifyDataSetChanged();
                }
            });

            if(color==color1){
                holder.item_color.setBackgroundColor( android.graphics.Color.parseColor("#567845"));
                holder.item_color.setTextColor( android.graphics.Color.parseColor("#ffffff"));
            }
            else
            {
                holder.item_color.setBackgroundColor( android.graphics.Color.parseColor("#ffffff"));
                holder.item_color.setTextColor( android.graphics.Color.parseColor("#000000"));
            }
        }

        @Override
        public int getItemCount() {
            return colors == null ? 0 : colors.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public Button item_color;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                item_color = itemLayoutView.findViewById(R.id.item_weight);
            }
        }
    }
    
    public class SubcategoryAdapter extends RecyclerView.Adapter<DetailsActivity.SubcategoryAdapter.MyViewHolder> {
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
        public DetailsActivity.SubcategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal_layout, parent, false);
            return new DetailsActivity.SubcategoryAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final DetailsActivity.SubcategoryAdapter.MyViewHolder holder, final int position) {

            final Subcategory product = productList.get(position);
            localStorage = new LocalStorage(context);
            gson = new Gson();
            cartList = ((BaseActivity) context).getnewCartList();

            if(product!=null){
                if(product.getAcceptingOrder()==0){
                    holder.order_not_accepting.setVisibility( GONE);
                    holder.add_item_layout.setVisibility(View.VISIBLE);
                }else{
                    holder.order_not_accepting.setVisibility(View.VISIBLE);
                    holder.add_item_layout.setVisibility( GONE);
                }
                if(product.getStock()==1){
                    holder.add_.setVisibility(View.VISIBLE);
                    holder.item_not_in_stock.setVisibility(GONE);
                }else{
                    holder.add_.setVisibility(GONE);
                    holder.item_not_in_stock.setVisibility(View.VISIBLE);
                }
                if(product.getAcceptingOrder()==1){
                    holder.order_not_accepting.setVisibility(View.GONE);
                    holder.add_item_layout.setVisibility(View.VISIBLE);
                }else{
                    holder.order_not_accepting.setVisibility(View.VISIBLE);
                    holder.add_item_layout.setVisibility(View.GONE);
                }

                holder.name.setText(product.getName());
                holder.item_short_desc.setText(product.getDiscription());
                holder.discription.setText(product.getDetailDiscription());
                if(product.getImages()!=null&&product.getImages().size()!=0){
                    Picasso.with(context).load(product.getImages().get( 0 ).getImage()).into(holder.imageView);
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
                        if (context instanceof SubCategoryActivity) {
                            Subcategory cart = new Subcategory(subCategory.getId(),subCategory.getSubcategoryid(),subCategory.getLastUpdate(),subCategory.getSearchKey(), subCategory.getName(), subCategory.getProductUniquekey(), subCategory.getLink2(),  subCategory.getLink3(),  subCategory.getLink4(),subCategory.getShieldLink(), subCategory.getDiscription(), subCategory.getDetailDiscription(), sellRate, displayRate,subCategory.getFirmId(),subCategory.getFirmLat(),subCategory.getFirmLng(),subCategory.getFirmAddress(),subCategory.getFrimPincode(),subCategory.getColors(),subCategory.getImages(),subCategory.getSizes(),subCategory.getWeights(), SubTotal, 1,subCategory.getBrand(),subCategory.getGender(),subCategory.getAge());
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
                            if (context instanceof SubCategoryActivity) {
                                Subcategory cart = new Subcategory(subCategory.getId(),subCategory.getSubcategoryid(),subCategory.getLastUpdate(),subCategory.getSearchKey(), subCategory.getName(), subCategory.getProductUniquekey(), subCategory.getLink2(),  subCategory.getLink3(),  subCategory.getLink4(),subCategory.getShieldLink(), subCategory.getDiscription(), subCategory.getDetailDiscription(), sellRate, displayRate,subCategory.getFirmId(),subCategory.getFirmLat(),subCategory.getFirmLng(),subCategory.getFirmAddress(),subCategory.getFrimPincode(),subCategory.getColors(),subCategory.getImages(),subCategory.getSizes(),subCategory.getWeights(), SubTotal, 1,subCategory.getBrand(),subCategory.getGender(),subCategory.getAge());
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
            LinearLayout add_item_layout,quantity_ll,order_not_accepting;
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

    @Override
    protected void onResume() {
        super.onResume();
        getProductColor( subCategory.getProductUniquekey() );
        getProductSize( subCategory.getProductUniquekey() );
        getProductWeight( subCategory.getProductUniquekey() );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
