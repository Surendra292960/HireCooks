package com.test.sample.hirecooks.Activity.ManageAccount.ManageProducts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Scene;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.Category.Category;
import com.test.sample.hirecooks.Models.Category.Example;
import com.test.sample.hirecooks.Models.Offer.Offer;
import com.test.sample.hirecooks.Models.users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditCategoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private String categoryName;
    private Offer category;
    private UserApi mService;
    private EditCategoryAdapter mAdapter;
    private List<Category> mCategory;
    private FloatingActionButton add_category;
    private User user;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_edit_category );
        recyclerView = findViewById( R.id.edit_category_recycler );
        add_category = findViewById( R.id.add_category );
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Products  Category");
        user = SharedPrefManager.getInstance( this ).getUser();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null&& Constants.NEARBY_VENDERS_LOCATION !=null) {
            categoryName= bundle.getString("CategoryName");
            category = (Offer) bundle.getSerializable("Category");
            if(category.getId()!=0){
                getCategory(category.getId());
            }else {
                Toast.makeText( this, "Comming soon", Toast.LENGTH_SHORT ).show();
            }
            if(mCategory!=null){
                getSupportActionBar().setTitle(category.getName()+" Category");
            }
        }

        if(user.getUserType().equalsIgnoreCase( "Admin" )&&user.getUserType().equalsIgnoreCase( "SuperAdmin" )){
            add_category.setVisibility( View.VISIBLE );
            add_category.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(category.getId()!=0&&category!=null){
                        Intent intent = new Intent(EditCategoryActivity.this, StartEditCategory.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("CreateCategory", category.getId());
                        intent.putExtras(bundle);
                        startActivity(intent);

                    }
                }
            } );
        }
    }

    private void getCategory(int id) {
        mCategory = new ArrayList<>(  );
        mService = ApiClient.getClient().create( UserApi.class);
        Call<List<Example>> call = mService.getCategoryByCatId(id);
        call.enqueue(new Callback<List<Example>>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<List<Example>> call, Response<List<Example>> response) {
                int statusCode = response.code();
                if(statusCode==200){
                    for(Example example:response.body()){
                        if(example.getError()==false){
                          if(response.body().size()!=0){
                              for(Category category:example.getCategory()){
                                  if(category!=null){
                                      mCategory.add( category );
                                      mAdapter = new EditCategoryAdapter( EditCategoryActivity.this,mCategory);
                                      recyclerView.setAdapter(mAdapter);
                                      GridLayoutManager linearLayoutManager = new GridLayoutManager(EditCategoryActivity.this,2);
                                      if (EditCategoryActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                          linearLayoutManager.setOrientation(LinearLayout.VERTICAL);
                                      }else{
                                          linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
                                      }
                                      RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                                      params.setMargins(20, 20, 20, 20);
                                      linearLayoutManager.canScrollHorizontally();

                                      recyclerView.setLayoutManager(linearLayoutManager);
                                      recyclerView.setItemAnimator(new DefaultItemAnimator());
                                  }
                              }
                          }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<com.test.sample.hirecooks.Models.Category.Example>> call, Throwable t) {
                Toast.makeText( EditCategoryActivity.this, t.getMessage(), Toast.LENGTH_SHORT ).show();
            }
        });
    }


    public class EditCategoryAdapter extends RecyclerView.Adapter<EditCategoryAdapter.ViewHolder> {
        private Context mCtx;
        private List<Category> categories;
        Scene aScene;
        private ViewGroup sceneRoot;

        public EditCategoryAdapter(Context mCtx, List<Category> categories) {
            this.mCtx = mCtx;
            this.categories = categories;
        }

        @Override
        public EditCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_cardview, null);
            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(EditCategoryAdapter.ViewHolder holder, int position) {
            Category category = categories.get(position);
            holder.categoryName.setText(category.getName());
            Picasso.with(mCtx).load(category.getLink()).into(holder.categoryImage);
            holder.categoryLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showalertbox(categories.get( position ));
                }
            });
        }

        private void showalertbox(Category category) {
            final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder( EditCategoryActivity.this);
            LayoutInflater inflater = EditCategoryActivity.this.getLayoutInflater();
            View view = inflater.inflate(R.layout.edit_subcategory_alert,null);
            AppCompatTextView productsBtn = view.findViewById(R.id.no_btn);
            productsBtn.setText( "Products" );
            AppCompatTextView editBtn = view.findViewById(R.id.edit_btn);
            dialogBuilder.setView(view);
            final android.app.AlertDialog dialog = dialogBuilder.create();
            dialog.show();
            productsBtn.setOnClickListener( v -> {
                try {
                    dialog.dismiss();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Intent intent = new Intent(mCtx, EditSubCategoryActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Category", category);
                        intent.putExtras(bundle);
                        // Check if we're running on Android 5.0 or higher
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            mCtx.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity) mCtx).toBundle());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        } else {
                            // Swap without transition
                            mCtx.startActivity(intent);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            } );
            editBtn.setOnClickListener( v -> {
                try {
                    dialog.dismiss();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Intent intent = new Intent(mCtx, StartEditCategory.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Category", category);
                        intent.putExtras(bundle);
                        // Check if we're running on Android 5.0 or higher
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            mCtx.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity) mCtx).toBundle());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        } else {
                            // Swap without transition
                            mCtx.startActivity(intent);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            } );
        }
        @Override
        public int getItemCount() {
            return categories==null?0:categories.size();
        }
        class ViewHolder extends RecyclerView.ViewHolder {
            public TextView categoryName;
            public ImageView categoryImage;
            public LinearLayout categoryLayout;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                categoryLayout = itemLayoutView.findViewById(R.id.category);
                categoryName = itemLayoutView.findViewById(R.id.category_name);
                categoryImage = itemLayoutView.findViewById(R.id.category_image);

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(category!=null&&category.getId()!=0){
            getCategory(category.getId());
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
