package com.test.sample.hirecooks.Activity.Category;
import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.test.sample.hirecooks.Activity.Orders.PlaceOrderActivity;
import com.test.sample.hirecooks.Activity.Search.SearchResultActivity;
import com.test.sample.hirecooks.Adapter.Category.CategoryAdapter;
import com.test.sample.hirecooks.Models.Menue.Menue;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.NetworkUtil;
import com.test.sample.hirecooks.ViewModel.ViewModel;
import com.test.sample.hirecooks.databinding.ActivitySubCategoryBinding;
import static android.view.View.GONE;

public class CategoryActivity extends BaseActivity {
    private ViewModel viewModel;
    private ActivitySubCategoryBinding binding;
    private Menue menue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubCategoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            menue = (Menue) bundle.getSerializable("Menue");
        }
        if(NetworkUtil.checkInternetConnection(this)) {
            binding.subcategoryLayout.setVisibility( View.VISIBLE );
            binding.noInternetConnectionLayout.setVisibility(View.GONE );
            if(menue!=null) {
                getCategory(menue.getId());
            }else {
                Toast.makeText( this, "Comming soon", Toast.LENGTH_SHORT ).show();
            }
        }
        else {
            binding.subcategoryLayout.setVisibility( View.GONE );
            binding.noInternetConnectionLayout.setVisibility( View.VISIBLE );
        }
        binding.mToolbarInterface.mToolbar.setVisibility(View.VISIBLE);
        binding.footerView.checkout.setOnClickListener(v -> startActivity( new Intent( this, PlaceOrderActivity.class ))); /*.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK) ));*/
        binding.mToolbarInterface.search.setOnClickListener(v -> startActivity( new Intent( this, SearchResultActivity.class )));// .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK) ));
        binding.mToolbarInterface.goBack.setOnClickListener(view1 -> finish());
    }

    @SuppressLint("NewApi")
    private void getCategory(Integer id) {
        if (menue != null) {
            viewModel.getCategory(id).observe(this, categoryResponses -> categoryResponses.forEach(category -> {
                if (!category.getError()&&category.getCategory() != null && category.getCategory().size() != 0) {
                    binding.recyclerview.setVisibility(View.VISIBLE);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
                    CategoryAdapter mAdapter = new CategoryAdapter(this);
                    binding.recyclerview.setLayoutManager(gridLayoutManager);
                    binding.recyclerview.setAdapter(mAdapter);
                    mAdapter.setCategory(category.getCategory());
                }
            }));
        }
        if(getCartList()!=null&&getCartList().size()!=0){
            animateBottomAnchor(false);
            for(int i=0; i<getCartList().size(); i++){
                if(getCartList().get(i).getItemQuantity()<=1&&getCartList().size()<=1){
                    binding.bottomAnchorLayout.setAnimation( AnimationUtils.loadAnimation(this, R.anim.fade_transition_animation));
                }
            }
            binding.bottomAnchorLayout.setVisibility(View.VISIBLE);
            binding.footerView.checkoutAmount.setText("₹  " + getTotalPrice());
            binding.footerView.itemCount.setText("" + cartCount());

        } else {
            binding.bottomAnchorLayout.setVisibility( GONE);
        }
        binding.nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY < oldScrollY) { // up
                animateBottomAnchor(false);
                //animateHeader(false);
            }
            if (scrollY > oldScrollY) { // down
                animateBottomAnchor(true);
                // animateHeader(true);
            }
        });
    }

    boolean isBottomAnchorNavigationHide = false;

    private void animateBottomAnchor(final boolean hide) {
        if (isBottomAnchorNavigationHide && hide || !isBottomAnchorNavigationHide && !hide) return;
        isBottomAnchorNavigationHide = hide;
        int moveY = hide ? (2 * binding.footerView.bottomAnchor.getHeight()) : 0;
        binding.footerView.bottomAnchor.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}