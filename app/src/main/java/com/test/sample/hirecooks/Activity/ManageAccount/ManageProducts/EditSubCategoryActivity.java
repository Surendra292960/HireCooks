package com.test.sample.hirecooks.Activity.ManageAccount.ManageProducts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.Category.Category;
import com.test.sample.hirecooks.Models.SubCategory.Example;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import com.test.sample.hirecooks.Models.users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.ProductApi;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

public class EditSubCategoryActivity extends AppCompatActivity {
    private RecyclerView subcategory_recycler;
    private RelativeLayout bottom_anchor_layout;
    private TextView item_count,checkout_amount,checkout;
    private View searchbar_interface_layout,bottom_anchor,appRoot;
    private String categoryName;
    private Category category;
    private FloatingActionButton add_subcategory;
    private LinearLayout no_result_found;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_edit_sub_category );
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Subcategory");
        this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        user = SharedPrefManager.getInstance( this ).getUser();
        subcategory_recycler = findViewById( R.id.edit_subcategory_recycler );
        add_subcategory = findViewById( R.id.add_subcategory );
        appRoot = findViewById(R.id.appRoot);
        no_result_found = findViewById(R.id.no_result_found);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            categoryName= bundle.getString("CategoryName");
            category = (Category) bundle.getSerializable("Category");
            if(category.getCategoryid()!=0){
                getSubCategory(category.getId());
            }else {
                Toast.makeText( this, "Comming soon", Toast.LENGTH_SHORT ).show();
            }
        }
        add_subcategory.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( EditSubCategoryActivity.this, StartEditSubCategoryActivity.class );
                Bundle bundle = new Bundle();
                if(category!=null) {
                    bundle.putSerializable( "mCategory", category );
                    intent.putExtras( bundle );
                    startActivity( intent );
                }
            }
        } );
    }

    private void getSubCategory(int id) {
        ProductApi mService = ApiClient.getClient().create(ProductApi.class);
        Call<ArrayList<Example>> call = mService.getSubCategorysBySub_id(id);
        call.enqueue(new Callback<ArrayList<Example>>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<ArrayList<Example>> call, Response<ArrayList<Example>> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    if(response.body()!=null&&response.body().size()!=0){
                        List<Subcategory> subcategoryList = new ArrayList<>(  );
                        List<Subcategory> filteredList = new ArrayList<>(  );
                        for(Example example:response.body()){
                            if(example.getError()==false){
                                for(Subcategory subcategory:example.getSubcategory()){
                                    if(subcategory.getFirmId().equalsIgnoreCase( user.getFirmId() )){
                                        subcategoryList.add( subcategory );
                                        Set<Subcategory> newList = new LinkedHashSet<>(subcategoryList);
                                        filteredList = new ArrayList<>(newList);
                                    }
                                }
                                if(filteredList.size()!=0&&filteredList!=null) {
                                    subcategory_recycler.setHasFixedSize( true );
                                    SubcategoryAdapter mAdapter = new SubcategoryAdapter( EditSubCategoryActivity.this, filteredList );
                                    subcategory_recycler.setAdapter( mAdapter );
                                }else {
                                    no_result_found.setVisibility(View.VISIBLE);
                                }
                                if(user.getUserType().equalsIgnoreCase( "SuperAdmin" )) {
                                    subcategory_recycler.setHasFixedSize( true );
                                    SubcategoryAdapter mAdapter = new SubcategoryAdapter( EditSubCategoryActivity.this, example.getSubcategory() );
                                    subcategory_recycler.setAdapter( mAdapter );
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(EditSubCategoryActivity.this);
                                    if (EditSubCategoryActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                        linearLayoutManager.setOrientation(LinearLayout.VERTICAL);
                                    }else{
                                        linearLayoutManager.setOrientation(LinearLayout.VERTICAL);
                                    }
                                    RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                                    params.setMargins(20, 20, 20, 20);
                                    linearLayoutManager.canScrollHorizontally();

                                    subcategory_recycler.setLayoutManager(linearLayoutManager);
                                    subcategory_recycler.setItemAnimator(new DefaultItemAnimator());
                                }
                            }else{
                                Toast.makeText( EditSubCategoryActivity.this, example.getMessage(), Toast.LENGTH_SHORT ).show();
                                no_result_found.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else {
                    Toast.makeText( EditSubCategoryActivity.this, R.string.failed_due_to + statusCode, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Example>> call, Throwable t) {
                System.out.println("Suree : " + t.getMessage());
            }
        });
    }

    public class SubcategoryAdapter extends RecyclerView.Adapter<EditSubCategoryActivity.SubcategoryAdapter.MyViewHolder> {
        List<Subcategory> productList;
        Context context;
        String Tag;
        private int discount = 0, discountPercentage = 0, displayrate = 0,sellRate = 0,displayRate = 0, SubTotal = 0,Quantity = 0;

        public SubcategoryAdapter(Context context, List<Subcategory> productList) {
            this.productList = productList;
            this.context = context;
        }

        @NonNull
        @Override
        public EditSubCategoryActivity.SubcategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_subcategory_adadpter_layout, parent, false);
            return new EditSubCategoryActivity.SubcategoryAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final EditSubCategoryActivity.SubcategoryAdapter.MyViewHolder holder, final int position) {
            final Subcategory product = productList.get(position);
            if(product!=null){
                if(product.getAcceptingOrder()==0){
                    holder.order_not_accepting.setVisibility(View.VISIBLE);
                }else{
                    holder.order_not_accepting.setVisibility( GONE);
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

            holder.cardview.setOnClickListener( v -> {
                showalertbox(productList.get( position ));
            } );
        }

        private void showalertbox(Subcategory subcategory) {
            final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder( EditSubCategoryActivity.this);
            LayoutInflater inflater = EditSubCategoryActivity.this.getLayoutInflater();
            View view = inflater.inflate(R.layout.edit_subcategory_alert,null);
            AppCompatTextView CancelBtn = view.findViewById(R.id.no_btn);
            AppCompatTextView editBtn = view.findViewById(R.id.edit_btn);
            dialogBuilder.setView(view);
            final android.app.AlertDialog dialog = dialogBuilder.create();
            dialog.show();
            CancelBtn.setOnClickListener( v -> {
                try {
                    dialog.dismiss();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            } );
            editBtn.setOnClickListener( v -> {
                try {
                    dialog.dismiss();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Intent intent = new Intent(EditSubCategoryActivity.this, StartEditSubCategoryActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("SubCategory", subcategory);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
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
            TextView discount, name, sellrate,displayRate,discription,item_short_desc;
            TextView add_item, remove_item;
            LinearLayout add_item_layout,order_not_accepting;
            CardView cardview;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.item_image);
                name = itemView.findViewById(R.id.item_name);
                discount = itemView.findViewById(R.id.item_discount);
                sellrate = itemView.findViewById(R.id.item_sellrate);
                displayRate = itemView.findViewById(R.id.item_displayrate);
                add_item = itemView.findViewById(R.id.add_item);
                remove_item = itemView.findViewById(R.id.remove_item);
                cardview = itemView.findViewById(R.id.card_view);
                discription = itemView.findViewById(R.id.item_description);
                item_short_desc = itemView.findViewById(R.id.item_short_desc);
                order_not_accepting = itemView.findViewById(R.id.order_not_accepting);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
       if(category.getId()!=0){
           getSubCategory(category.getId());
       }
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
