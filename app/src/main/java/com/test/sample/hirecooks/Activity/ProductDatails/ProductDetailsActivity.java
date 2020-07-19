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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Activity.AddorRemoveCallbacks;
import com.test.sample.hirecooks.Activity.CheckOut.CheckoutActivity;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Models.Cart.Cart;
import com.test.sample.hirecooks.Models.OfferSubCategory.OffersSubcategory;
import com.test.sample.hirecooks.Models.SearchSubCategory.Search;
import com.test.sample.hirecooks.Models.SubCategory.Response.SubCategory;
import com.test.sample.hirecooks.Models.VendersSubCategory.VendersSubcategory;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.LocalStorage;
import com.test.sample.hirecooks.Utils.Constants;
import java.util.ArrayList;
import java.util.List;
import static android.view.View.GONE;

public class ProductDetailsActivity extends BaseActivity {
    private RelativeLayout bottom_anchor_layout;
    private TextView item_not_in_stock, itemAdd, itemRemove,item_count,checkout_amount,checkout,item_sellrate,item_name,addToCart,quantity;
    private List<Cart> cartList;
    private List<Cart> FavouriteList;
    private int Quantity = 0, FavQuantity = 0, sellRate = 0,displayRate = 0, SubTotal = 0;
    private String weight;
    private RecyclerView recyclerView;
    private SubCategory subCategory;
    private OffersSubcategory offerSubCategory;
    private List<SubCategory> subCategoryList;
    private List<OffersSubcategory> offersSubcategoryList;
    private List<Search> searchList;
    private SubCategoryHorizontalAdapter subcategoryAdapter;
    private OfferSubCategoryHorizontalAdapter offerSubCategoryadapter;
    private SearchHorizontalAdapter searchAdapter;
    private ImageView image1,image2,image3,image4,item_favourite;
    private View bottom_anchor;
    private LinearLayout layout_action_share, layout_action_favourite,add_item_layout,add_quantity_layout,order_not_accepting;
    private LocalStorage localStorage;
    private Gson gson;
    private Search search;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        initViews();
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            subCategory = (SubCategory)bundle.getSerializable("SubCategory");
            offerSubCategory = (OffersSubcategory)bundle.getSerializable("OfferSubCategory");
            search = (Search)bundle.getSerializable("Search");
            if(subCategory!=null||offerSubCategory!=null||search!=null){
                getData();
            }
        }
        getCart();
        getFavourites();
    }

    @SuppressLint("WrongConstant")
    private void getData() {
        if(subCategory!=null){
            subCategoryList = new ArrayList<>();
            subCategoryList.add(subCategory);
            if(subCategoryList!=null&&subCategoryList.size()!=0){
                for(SubCategory subCategory:subCategoryList) {
                    if (!subCategory.getLink().isEmpty()) { Picasso.with(this).load(subCategory.getLink()).into(image1);
                    }else{image1.setVisibility(GONE);}
                    if (!subCategory.getLink2().isEmpty()){ Picasso.with(this).load(subCategory.getLink2()).into(image2);
                    }else{image2.setVisibility(GONE);}
                    if(!subCategory.getLink3().isEmpty()) { Picasso.with(this).load(subCategory.getLink3()).into(image3);
                    }else{image3.setVisibility(GONE);}
                    if(!subCategory.getLink4().isEmpty()) { Picasso.with(this).load(subCategory.getLink4()).into(image4);
                    }else{ image4.setVisibility(GONE);}
                }
            }

            if(subCategory.getStock()==1){
                addToCart.setVisibility(View.VISIBLE);
                item_not_in_stock.setVisibility(GONE);
            }else{
                addToCart.setVisibility(GONE);
                item_not_in_stock.setVisibility(View.VISIBLE);
            }
            if(subCategory.getAcceptingOrder()==1){
                order_not_accepting.setVisibility(View.GONE);
                add_item_layout.setVisibility(View.VISIBLE);
            }else{
                order_not_accepting.setVisibility(View.VISIBLE);
                add_item_layout.setVisibility(View.GONE);
            }
            item_name.setText(subCategory.getName());
            item_sellrate.setText("\u20B9   "+subCategory.getSellRate());
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(ProductDetailsActivity.this));
            subcategoryAdapter = new SubCategoryHorizontalAdapter(ProductDetailsActivity.this, Constants.SUBCATEGORY);
            recyclerView.setAdapter(subcategoryAdapter);
            subcategoryAdapter.notifyDataSetChanged();
        }else if(offerSubCategory!=null){
            offersSubcategoryList = new ArrayList<>();
            offersSubcategoryList.add(offerSubCategory);
            if(offersSubcategoryList!=null&&offersSubcategoryList.size()!=0){
                for(OffersSubcategory offersSubcategory:offersSubcategoryList){
                    if (!offersSubcategory.getLink().isEmpty()) { Picasso.with(this).load(offersSubcategory.getLink()).into(image1);
                    }else{image1.setVisibility(GONE);}
                    if (!offersSubcategory.getLink2().isEmpty()){ Picasso.with(this).load(offersSubcategory.getLink2()).into(image2);
                    }else{image2.setVisibility(GONE);}
                    if(!offersSubcategory.getLink3().isEmpty()) { Picasso.with(this).load(offersSubcategory.getLink3()).into(image3);
                    }else{image3.setVisibility(GONE);}
                    if(!offersSubcategory.getLink4().isEmpty()) { Picasso.with(this).load(offersSubcategory.getLink4()).into(image4);
                    }else{ image4.setVisibility(GONE);}
                }
            }
            if(offerSubCategory.getStock()==1){
                addToCart.setVisibility(View.VISIBLE);
                item_not_in_stock.setVisibility(GONE);
            }else{
                addToCart.setVisibility(GONE);
                item_not_in_stock.setVisibility(View.VISIBLE);
            }
            if(offerSubCategory.getAcceptingOrder()==1){
                order_not_accepting.setVisibility(View.GONE);
                add_item_layout.setVisibility(View.VISIBLE);
            }else{
                order_not_accepting.setVisibility(View.VISIBLE);
                add_item_layout.setVisibility(View.GONE);
            }
            item_name.setText(offerSubCategory.getName());
            item_sellrate.setText("\u20B9  "+offerSubCategory.getSellRate());
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(ProductDetailsActivity.this));
            offerSubCategoryadapter = new OfferSubCategoryHorizontalAdapter(ProductDetailsActivity.this, Constants.OFFER_SUBCATEGORY);
            recyclerView.setAdapter(offerSubCategoryadapter);
            offerSubCategoryadapter.notifyDataSetChanged();
        }else if(search!=null){
            searchList = new ArrayList<>();
            searchList.add(search);
            if(searchList!=null&&searchList.size()!=0){
                for(Search search:searchList){
                    if (!search.getLink().isEmpty()) { Picasso.with(this).load(search.getLink()).into(image1);
                    }else{image1.setVisibility(GONE);}
                    if (!search.getLink2().isEmpty()){ Picasso.with(this).load(search.getLink2()).into(image2);
                    }else{image2.setVisibility(GONE);}
                    if(!search.getLink3().isEmpty()) { Picasso.with(this).load(search.getLink3()).into(image3);
                    }else{image3.setVisibility(GONE);}
                    if(!search.getLink4().isEmpty()) { Picasso.with(this).load(search.getLink4()).into(image4);
                    }else{ image4.setVisibility(GONE);}
                }
            }

            if(search.getStock()==1){
                addToCart.setVisibility(View.VISIBLE);
                item_not_in_stock.setVisibility(GONE);
            }else{
                addToCart.setVisibility(GONE);
                item_not_in_stock.setVisibility(View.VISIBLE);
            }
            if(search.getAcceptingOrder()==1){
                order_not_accepting.setVisibility(View.GONE);
                add_item_layout.setVisibility(View.VISIBLE);
            }else{
                order_not_accepting.setVisibility(View.VISIBLE);
                add_item_layout.setVisibility(View.GONE);
            }
            item_name.setText(search.getName());
            item_sellrate.setText("\u20B9  "+search.getSellRate());
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(ProductDetailsActivity.this));
            searchAdapter = new SearchHorizontalAdapter(ProductDetailsActivity.this, Constants.SEARCH);
            recyclerView.setAdapter(searchAdapter);
            searchAdapter.notifyDataSetChanged();
        }
    }

    private void initViews() {
        localStorage = new LocalStorage(this);
        gson = new Gson();

        recyclerView = findViewById(R.id.subcategory_recycler);
        bottom_anchor_layout = findViewById(R.id.bottom_anchor_layout);
        layout_action_share = findViewById(R.id.layout_action_share);
        layout_action_favourite = findViewById(R.id.layout_action_favourite);
        item_favourite = findViewById(R.id.item_favourite);
        add_item_layout = findViewById(R.id.add_item_layout);
        add_quantity_layout = findViewById(R.id.quantity_ll);
        addToCart = findViewById(R.id.add_);
        quantity = findViewById(R.id.item_count);
        item_not_in_stock = findViewById(R.id.item_not_in_stock);
        order_not_accepting = findViewById(R.id.order_not_accepting);
        itemRemove = findViewById(R.id.remove_item);
        itemAdd = findViewById(R.id.add_item);
        item_sellrate = findViewById(R.id.product_sellrate);
        item_name = findViewById(R.id.product_name);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);

        View view = findViewById(R.id.footerView);
        item_count = view.findViewById(R.id.item_count);
        bottom_anchor = view.findViewById(R.id.bottom_anchor);
        checkout_amount = view.findViewById(R.id.checkout_amount);
        checkout = view.findViewById(R.id.checkout);

         checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductDetailsActivity.this, CheckoutActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        layout_action_favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(subCategory!=null&&FavQuantity==0){
                    sellRate = subCategory.getSellRate();
                    displayRate = subCategory.getDisplayRate();
                    FavQuantity = 1;
                    if (subCategory.getId() != 0 && subCategory.getName() != null && subCategory.getLink() != null && subCategory.getDiscription() != null && sellRate != 0 && displayRate != 0 && subCategory.getFirm_id() != null) {
                        SubTotal = (sellRate * FavQuantity);
                        if (ProductDetailsActivity.this instanceof ProductDetailsActivity) {
                            Cart cart = new Cart(subCategory.getId(), subCategory.getName(),  subCategory.getLink(), subCategory.getDiscription(), sellRate, displayRate, SubTotal, 1,weight, subCategory.getFirm_id());
                            FavouriteList = getFavourite();
                            FavouriteList.add(cart);
                            String favourite = gson.toJson(FavouriteList);
                            localStorage.setFavourite(favourite);
                            ((AddorRemoveCallbacks) ProductDetailsActivity.this).onAddProduct();
                            subcategoryAdapter.notifyDataSetChanged();
                            getFavourites();
                            Toast.makeText(ProductDetailsActivity.this, "Added as Favourite", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(ProductDetailsActivity.this, "This item can`t be add please remove item and try again", Toast.LENGTH_SHORT).show();
                    }
                } else if(offerSubCategory!=null&&FavQuantity==0){
                    sellRate = offerSubCategory.getSellRate();
                    displayRate = offerSubCategory.getDisplayRate();
                    FavQuantity = 1;
                    if (offerSubCategory.getId() != 0 && offerSubCategory.getName() != null && offerSubCategory.getLink() != null && offerSubCategory.getDiscription() != null && sellRate != 0 && displayRate != 0 && offerSubCategory.getFirm_id() != null) {
                        SubTotal = (sellRate * FavQuantity);
                        if (ProductDetailsActivity.this instanceof ProductDetailsActivity) {
                            Cart cart = new Cart(offerSubCategory.getId(), offerSubCategory.getName(),  offerSubCategory.getLink(), offerSubCategory.getDiscription(), sellRate, displayRate, SubTotal, 1,weight, offerSubCategory.getFirm_id());
                            FavouriteList = getFavourite();
                            FavouriteList.add(cart);
                            String favourite = gson.toJson(FavouriteList);
                            localStorage.setFavourite(favourite);
                            ((AddorRemoveCallbacks) ProductDetailsActivity.this).onAddProduct();
                            offerSubCategoryadapter.notifyDataSetChanged();
                            getFavourites();
                            Toast.makeText(ProductDetailsActivity.this, "Added as Favourite", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(ProductDetailsActivity.this, "This item can`t be add please remove item and try again", Toast.LENGTH_SHORT).show();
                    }
                }else if(search!=null&&FavQuantity==0){
                    sellRate = search.getSellRate();
                    displayRate = search.getDisplayRate();
                    FavQuantity = 1;
                    if (search.getId() != 0 && search.getName() != null && search.getLink() != null && search.getDiscription() != null && sellRate != 0 && displayRate != 0 && search.getFirmId() != null) {
                        SubTotal = (sellRate * FavQuantity);
                        if (ProductDetailsActivity.this instanceof ProductDetailsActivity) {
                            Cart cart = new Cart(search.getId(), search.getName(),  search.getLink(), search.getDiscription(), sellRate, displayRate, SubTotal, 1,weight, search.getFirmId());
                            FavouriteList = getFavourite();
                            FavouriteList.add(cart);
                            String favourite = gson.toJson(FavouriteList);
                            localStorage.setFavourite(favourite);
                            ((AddorRemoveCallbacks) ProductDetailsActivity.this).onAddProduct();
                            searchAdapter.notifyDataSetChanged();
                            getFavourites();
                            Toast.makeText(ProductDetailsActivity.this, "Added as Favourite", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(ProductDetailsActivity.this, "This item can`t be add please remove item and try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        add_item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(subCategory!=null){
                    addToCart.setVisibility(View.GONE);
                    add_quantity_layout.setVisibility(View.VISIBLE);
                    sellRate = subCategory.getSellRate();
                    displayRate = subCategory.getDisplayRate();
                    Quantity = 1;
                    if (subCategory.getId() != 0 && subCategory.getName() != null && subCategory.getLink() != null && subCategory.getDiscription() != null && sellRate != 0 && displayRate != 0 && subCategory.getFirm_id() != null) {
                        SubTotal = (sellRate * Quantity);
                        if (ProductDetailsActivity.this instanceof ProductDetailsActivity) {
                            Cart cart = new Cart(subCategory.getId(), subCategory.getName(),  subCategory.getLink(), subCategory.getDiscription(), sellRate, displayRate, SubTotal, 1,weight, subCategory.getFirm_id());
                            cartList = getCartList();
                            cartList.add(cart);
                            String cartStr = gson.toJson(cartList);
                            localStorage.setCart(cartStr);
                            ((AddorRemoveCallbacks) ProductDetailsActivity.this).onAddProduct();
                            subcategoryAdapter.notifyDataSetChanged();
                            getCart();
                        }
                    }else{
                        addToCart.setVisibility(View.VISIBLE);
                        add_quantity_layout.setVisibility(GONE);
                        Toast.makeText(ProductDetailsActivity.this, "This item can`t be add please remove item and try again", Toast.LENGTH_SHORT).show();
                    }
                } else if(offerSubCategory!=null){
                    addToCart.setVisibility(View.GONE);
                    add_quantity_layout.setVisibility(View.VISIBLE);
                    sellRate = offerSubCategory.getSellRate();
                    displayRate = offerSubCategory.getDisplayRate();
                    Quantity = 1;
                    if (offerSubCategory.getId() != 0 && offerSubCategory.getName() != null && offerSubCategory.getLink() != null && offerSubCategory.getDiscription() != null && sellRate != 0 && displayRate != 0 && offerSubCategory.getFirm_id() != null) {
                        SubTotal = (sellRate * Quantity);
                        if (ProductDetailsActivity.this instanceof ProductDetailsActivity) {
                            Cart cart = new Cart(offerSubCategory.getId(), offerSubCategory.getName(),  offerSubCategory.getLink(), offerSubCategory.getDiscription(), sellRate, displayRate, SubTotal, 1,weight, offerSubCategory.getFirm_id());
                            cartList = getCartList();
                            cartList.add(cart);
                            String cartStr = gson.toJson(cartList);
                            localStorage.setCart(cartStr);
                            ((AddorRemoveCallbacks) ProductDetailsActivity.this).onAddProduct();
                            offerSubCategoryadapter.notifyDataSetChanged();
                            getCart();
                        }
                    }else{
                        addToCart.setVisibility(View.VISIBLE);
                        add_quantity_layout.setVisibility(GONE);
                        Toast.makeText(ProductDetailsActivity.this, "This item can`t be add please remove item and try again", Toast.LENGTH_SHORT).show();
                    }
                }else if(search!=null){
                    addToCart.setVisibility(View.GONE);
                    add_quantity_layout.setVisibility(View.VISIBLE);
                    sellRate = search.getSellRate();
                    displayRate = search.getDisplayRate();
                    Quantity = 1;
                    if (search.getId() != 0 && search.getName() != null && search.getLink() != null && search.getDiscription() != null && sellRate != 0 && displayRate != 0 && search.getFirmId() != null) {
                        SubTotal = (sellRate * Quantity);
                        if (ProductDetailsActivity.this instanceof ProductDetailsActivity) {
                            Cart cart = new Cart(search.getId(), search.getName(),  search.getLink(), search.getDiscription(), sellRate, displayRate, SubTotal, 1,weight, search.getFirmId());
                            cartList = getCartList();
                            cartList.add(cart);
                            String cartStr = gson.toJson(cartList);
                            localStorage.setCart(cartStr);
                            ((AddorRemoveCallbacks) ProductDetailsActivity.this).onAddProduct();
                            searchAdapter.notifyDataSetChanged();
                            getCart();
                        }
                    }else{
                        addToCart.setVisibility(View.VISIBLE);
                        add_quantity_layout.setVisibility(GONE);
                        Toast.makeText(ProductDetailsActivity.this, "This item can`t be add please remove item and try again", Toast.LENGTH_SHORT).show();
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
                             subcategoryAdapter.notifyDataSetChanged();
                             getCart();
                         }
                     }
                 }
             }else if(offerSubCategory!=null){
                 sellRate = offerSubCategory.getSellRate();
                 displayRate = offerSubCategory.getDisplayRate();
                 String count = quantity.getText().toString();
                 Quantity = Integer.parseInt(count);
                 if (Quantity >= 1) {
                     Quantity++;
                     quantity.setText(""+(Quantity));
                     for (int i = 0; i < cartList.size(); i++) {
                         if (cartList.get(i).getId()==offerSubCategory.getId()&&cartList.get(i).getName().equalsIgnoreCase(offerSubCategory.getName())&&cartList.get(i).getSellRate()==offerSubCategory.getSellRate()) {
                             SubTotal = (sellRate * Quantity);
                             cartList.get(i).setItemQuantity(Quantity);
                             cartList.get(i).setTotalAmount(SubTotal);
                             String cartStr = gson.toJson(cartList);
                             localStorage.setCart(cartStr);
                             offerSubCategoryadapter.notifyDataSetChanged();
                             getCart();
                         }
                     }
                 }
             }else if(search!=null){
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
                             searchAdapter.notifyDataSetChanged();
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
                                subcategoryAdapter.notifyDataSetChanged();
                                getCart();
                            }
                        }
                    }
                }else if(offerSubCategory!=null){
                    sellRate = offerSubCategory.getSellRate();
                    displayRate = offerSubCategory.getDisplayRate();
                    String count = quantity.getText().toString();
                    Quantity = Integer.parseInt(count);
                    if (Quantity > 1) {
                        Quantity--;
                        quantity.setText(""+(Quantity));
                        for (int i = 0; i < cartList.size(); i++) {
                            if (cartList.get(i).getId()==offerSubCategory.getId()&&cartList.get(i).getName().equalsIgnoreCase(offerSubCategory.getName())&&cartList.get(i).getSellRate()==offerSubCategory.getSellRate()) {
                                SubTotal = (sellRate * Quantity);
                                cartList.get(i).setItemQuantity(Quantity);
                                cartList.get(i).setTotalAmount(SubTotal);
                                String cartStr = gson.toJson(cartList);
                                localStorage.setCart(cartStr);
                                offerSubCategoryadapter.notifyDataSetChanged();
                                getCart();
                            }
                        }
                    }
                }else if(search!=null){
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
                                searchAdapter.notifyDataSetChanged();
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
             if(subCategory!=null&&subCategory.getLink()!=null){
                 Intent shareIntent = new Intent();
                 shareIntent.setAction(Intent.ACTION_SEND);
                 shareIntent.putExtra(Intent.EXTRA_STREAM, subCategory.getLink());
                 shareIntent.setType("image/jpeg");
                 startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send)));
             }else if(offerSubCategory!=null&&offerSubCategory.getLink()!=null){
                 Intent shareIntent = new Intent();
                 shareIntent.setAction(Intent.ACTION_SEND);
                 shareIntent.putExtra(Intent.EXTRA_STREAM, offerSubCategory.getLink());
                 shareIntent.setType("image/jpeg");
                 startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send)));
             }else if(search!=null&&search.getLink()!=null){
                 Intent shareIntent = new Intent();
                 shareIntent.setAction(Intent.ACTION_SEND);
                 shareIntent.putExtra(Intent.EXTRA_STREAM, search.getLink());
                 shareIntent.setType("image/jpeg");
                 startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send)));
             }
            }
        });

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
                }else if(offerSubCategory!=null){
                    if (FavouriteList.get(i).getId()==offerSubCategory.getId()&&FavouriteList.get(i).getName().equalsIgnoreCase(offerSubCategory.getName())) {
                        item_favourite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
                        FavQuantity = FavouriteList.get(i).getItemQuantity();
                    }else{
                        FavQuantity = 0;
                    }
                }else if(search!=null){
                    if (FavouriteList.get(i).getId()==search.getId()&&FavouriteList.get(i).getName().equalsIgnoreCase(search.getName())) {
                        item_favourite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
                        FavQuantity = FavouriteList.get(i).getItemQuantity();
                    }else{
                        FavQuantity = 0;
                    }
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
                if(subCategory!=null){
                    if (cartList.get(i).getId()==subCategory.getId()&&cartList.get(i).getName().equalsIgnoreCase(subCategory.getName())) {
                        addToCart.setVisibility(GONE);
                        add_quantity_layout.setVisibility(View.VISIBLE);
                        quantity.setText(""+cartList.get(i).getItemQuantity());
                    }
                }else if(offerSubCategory!=null){
                    if (cartList.get(i).getId()==offerSubCategory.getId()&&cartList.get(i).getName().equalsIgnoreCase(offerSubCategory.getName())) {
                        addToCart.setVisibility(GONE);
                        add_quantity_layout.setVisibility(View.VISIBLE);
                        quantity.setText(""+cartList.get(i).getItemQuantity());
                    }
                }else if(search!=null){
                    if (cartList.get(i).getId()==search.getId()&&cartList.get(i).getName().equalsIgnoreCase(search.getName())) {
                        addToCart.setVisibility(GONE);
                        add_quantity_layout.setVisibility(View.VISIBLE);
                        quantity.setText(""+cartList.get(i).getItemQuantity());
                    }
                }
            }
            bottom_anchor_layout.setVisibility(View.VISIBLE);
            checkout_amount.setText("₹  " + getTotalPrice());
            item_count.setText("" + cartCount());

        } else {
            bottom_anchor_layout.setVisibility(GONE);
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
                        holder.quantity_ll.setVisibility(GONE);
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

            holder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(context, ProductDetailsActivity.class);
                    bundle.putSerializable("OfferSubCategory",productList.get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                    ((ProductDetailsActivity) context).finish();
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
                item_not_in_stock = itemView.findViewById(R.id.item_not_in_stock);
                order_not_accepting = itemView.findViewById(R.id.order_not_accepting);
                cardview = itemView.findViewById(R.id.card_view);
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
                    holder.item_not_in_stock.setVisibility(GONE);
                }else{
                    // holder.add_item_layout.setVisibility(View.GONE);
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
                        holder.quantity_ll.setVisibility(GONE);
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
            holder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(context, ProductDetailsActivity.class);
                    bundle.putSerializable("SubCategory",productList.get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                    ((ProductDetailsActivity) context).finish();
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
                item_not_in_stock = itemView.findViewById(R.id.item_not_in_stock);
                order_not_accepting = itemView.findViewById(R.id.order_not_accepting);
                cardview = itemView.findViewById(R.id.card_view);
                discription = itemView.findViewById(R.id.item_short_desc);
            }
        }
    }

    public class SearchHorizontalAdapter extends RecyclerView.Adapter<ProductDetailsActivity.SearchHorizontalAdapter.MyViewHolder> {
        List<Search> productList;
        Context context;
        LocalStorage localStorage;
        Gson gson;
        List<Cart> cartList = new ArrayList<>();
        String weight;
        private int discount = 0, discountPercentage = 0, displayrate = 0,sellRate = 0,displayRate = 0, SubTotal = 0,Quantity = 0;

        public SearchHorizontalAdapter(Context context, List<Search> productList) {
            this.productList = productList;
            this.context = context;
        }

        @NonNull
        @Override
        public ProductDetailsActivity.SearchHorizontalAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal_layout, parent, false);
            return new ProductDetailsActivity.SearchHorizontalAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final ProductDetailsActivity.SearchHorizontalAdapter.MyViewHolder holder, final int position) {

            final Search product = productList.get(position);
            localStorage = new LocalStorage(context);
            gson = new Gson();
            cartList = ((BaseActivity) context).getCartList();

            if(product!=null){
                if(product.getStock()==1){
                    // holder.add_item_layout.setVisibility(View.VISIBLE);
                    holder.add_.setVisibility(View.VISIBLE);
                    holder.item_not_in_stock.setVisibility(GONE);
                }else{
                    // holder.add_item_layout.setVisibility(View.GONE);
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
                        holder.quantity_ll.setVisibility(GONE);
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
            holder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(context, ProductDetailsActivity.class);
                    bundle.putSerializable("Search",productList.get(position));
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                    ((ProductDetailsActivity) context).finish();
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
                item_not_in_stock = itemView.findViewById(R.id.item_not_in_stock);
                order_not_accepting = itemView.findViewById(R.id.order_not_accepting);
                cardview = itemView.findViewById(R.id.card_view);
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
                    holder.item_not_in_stock.setVisibility(GONE);
                }else{
                    // holder.add_item_layout.setVisibility(View.GONE);
                    holder.add_.setVisibility(GONE);
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
                        holder.quantity_ll.setVisibility(GONE);
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
