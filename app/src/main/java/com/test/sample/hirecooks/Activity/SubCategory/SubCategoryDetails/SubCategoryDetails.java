package com.test.sample.hirecooks.Activity.SubCategory.SubCategoryDetails;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Activity.Cart.CartActivity;
import com.test.sample.hirecooks.Models.Cart.Cart;
import com.test.sample.hirecooks.Models.SubCategory.Response.SubCategory;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.Common;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;

import java.util.HashSet;
import java.util.Objects;

public class SubCategoryDetails extends AppCompatActivity {
    private TextView textViewName, textViewDiscount, textViewDescription, removeItem, addItem, textViewCount,
            itemQuantityBottomView,checkout_amount,textViewSellRate, textViewDisplayRate;
    private Button textAddToCart;
    private RecyclerView recyclerView;
    private SimpleDraweeView imageView;
    private ProgressBarUtil progressBarUtil;
    private View appRoot;
    private SubCategory SubCategory;
    private int itemCount = 0,sellRate = 0, totalAmount = 0, itemQuantity = 0,discount = 0, displayrate = 0, discountPercentage = 0;
    private HashSet<Cart> carts;
    private FrameLayout item_counter;
  //  private SubCategoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category_details);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(Common.CATEGORY_NAME);
        initViews();
        //getCart();
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            SubCategory = (SubCategory)bundle.getSerializable("SubCategory");
            if(SubCategory!=null){
                textViewName.setText(SubCategory.getName());
                textViewDescription.setText(SubCategory.getDiscription());
                Picasso.with(this).load(SubCategory.getLink()).into(imageView);

                if(SubCategory.getSellRate()!=null&&SubCategory.getSellRate()!=0 && SubCategory.getDisplayRate()!=null&&SubCategory.getDisplayRate()!=0) {
                    textViewSellRate.setText("\u20B9 " + SubCategory.getSellRate());

                    SpannableString spanString = new SpannableString("\u20B9 "+SubCategory.getDisplayRate());
                    spanString.setSpan(new StrikethroughSpan(), 0, spanString.length(), 0);
                    textViewDisplayRate.setText(spanString);

                    discount = (SubCategory.getDisplayRate()-SubCategory.getSellRate());
                    displayrate = (SubCategory.getDisplayRate());
                    discountPercentage = (discount*100/displayrate);
                    textViewDiscount.setText("Save "+discountPercentage+" %");
                }
            }else{
                SubCategory = (SubCategory)bundle.getSerializable("Cart");
                if(SubCategory!=null){
                    textViewName.setText(SubCategory.getName());
                    textViewDescription.setText(SubCategory.getDiscription());
                    Picasso.with(this).load(SubCategory.getLink()).into(imageView);
                    if(SubCategory.getSellRate()!=null&&SubCategory.getSellRate()!=0 && SubCategory.getDisplayRate()!=null&&SubCategory.getDisplayRate()!=0) {
                        textViewSellRate.setText("\u20B9 " + SubCategory.getSellRate());

                        SpannableString spanString = new SpannableString("\u20B9 "+SubCategory.getDisplayRate());
                        spanString.setSpan(new StrikethroughSpan(), 0, spanString.length(), 0);
                        textViewDisplayRate.setText(spanString);

                        discount = (SubCategory.getDisplayRate()-SubCategory.getSellRate());
                        displayrate = (SubCategory.getDisplayRate());
                        discountPercentage = (discount*100/displayrate);
                        textViewDiscount.setText("Save "+discountPercentage+" %");
                    }
                }
            }
        }
    }

    @SuppressLint("WrongConstant")
    private void initViews() {
        progressBarUtil = new ProgressBarUtil(this);
        appRoot = findViewById(R.id.appRoot);
        imageView = findViewById(R.id.imageView);
        textViewName = findViewById(R.id.textViewName);
        textViewDiscount = findViewById(R.id.textViewDiscount);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewSellRate = findViewById(R.id.textViewSellRate);
        textViewDisplayRate = findViewById(R.id.textViewDisplayRate);
        addItem = findViewById(R.id.add_to_cart);
        recyclerView = findViewById(R.id.subcategory_details_recycler);
/*
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SubCategoryDetails.this));
        mAdapter = new SubCategoryAdapter(SubCategoryDetails.this, Constants.SUBCATEGORY);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(SubCategoryDetails.this, 2));
        recyclerView.addItemDecoration(new DividerItemDecoration(5));
        recyclerView.setItemAnimator(new DefaultItemAnimator());*/

        View view = findViewById(R.id.footerView);
        itemQuantityBottomView =  (TextView) view.findViewById(R.id.item_count);
        checkout_amount = (TextView) view.findViewById(R.id.checkout_amount);
        item_counter = (FrameLayout) view.findViewById(R.id.item_counter);
        item_counter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SubCategoryDetails.this,CartActivity.class));
            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem.setEnabled(false);
            }
        });
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
}
