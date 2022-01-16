package com.test.sample.hirecooks.Activity.Search;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.test.sample.hirecooks.Activity.Orders.PlaceOrderActivity;
import com.test.sample.hirecooks.Adapter.SubCategory.SubcategoryAdapter;
import com.test.sample.hirecooks.Models.Chat.ListObject;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.NetworkUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.ViewModel.ViewModel;
import com.test.sample.hirecooks.databinding.ActivitySubCategoryBinding;
import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends BaseActivity {
    private List<Subcategory> cartList;
    private ActivitySubCategoryBinding binding;
    private ViewModel viewModel;
    private User user;
    private SubcategoryAdapter mAdapter;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        binding = ActivitySubCategoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        user = SharedPrefManager.getInstance(this).getUser();
        initViews();
        if(NetworkUtil.checkInternetConnection(this)) {
            binding.subcategoryLayout.setVisibility( View.VISIBLE );
            binding.noInternetConnectionLayout.setVisibility( View.GONE );
            getCart();
        }
        else {
            binding.subcategoryLayout.setVisibility( View.GONE );
            binding.noInternetConnectionLayout.setVisibility( View.VISIBLE );
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initViews() {
        binding.footerView.checkout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( SearchResultActivity.this, PlaceOrderActivity.class ));
            }
        });
        binding.mToolbarInterface.searchBar.setVisibility(View.VISIBLE);
        binding.mToolbarInterface.searchBar.openSearch();
        binding.mToolbarInterface.searchBar.addTextChangeListener(new TextWatcher() {
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
        binding.mToolbarInterface.searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(!enabled){
                    binding.recyclerview.setAdapter(mAdapter);
                }
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
                        binding.mToolbarInterface.searchBar.closeSearch();
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
        int moveY = hide ? (2 * binding.footerView.bottomAnchor.getHeight()) : 0;
        binding.footerView.bottomAnchor.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    boolean isSearchBarHide = false;

    private void animateSearchBar(final boolean hide) {
        if (isSearchBarHide && hide || !isSearchBarHide && !hide) return;
        isSearchBarHide = hide;
        int moveY = hide ? -(2 * binding.mToolbarInterface.mToolbarLayout.getHeight()) : 0;
        binding.mToolbarInterface.mToolbarLayout.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
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

    @SuppressLint({"NewApi", "NotifyDataSetChanged"})
    private void searchAllProducts(String search_key) {
        if(!search_key.isEmpty()&&search_key!=null){
            viewModel.searchNearByProducts(user.getId(), search_key).observe(this,subcategoryResponses -> subcategoryResponses.forEach(subcategory->{ if(!subcategory.getError()&&subcategory.getSubcategory()!=null&&subcategory.getSubcategory().size()!=0){
                binding.recyclerview.setVisibility(View.VISIBLE);
                mAdapter = new SubcategoryAdapter( SearchResultActivity.this, subcategory.getSubcategory(), ListObject.TYPE_CARDVIEW_HORIZONTAL);
                binding.recyclerview.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                }
            }));
        }
    }

    private void getCart() {
        cartList = new ArrayList<>();
        cartList = getnewCartList();
        if (!cartList.isEmpty()) {
            animateBottomAnchor(false);
            for(int i=0; i<cartList.size(); i++){
                if(cartList.get(i).getItemQuantity()<=1&&cartList.size()<=1){
                    binding.bottomAnchorLayout.setAnimation(AnimationUtils.loadAnimation(this,R.anim.fade_transition_animation));
                }
            }
            binding.bottomAnchorLayout.setVisibility(View.VISIBLE);
            binding.footerView.checkoutAmount.setText("₹  " + getTotalPrice());
            binding.footerView.itemCount.setText("" + cartCount());

        } else {
            binding.bottomAnchorLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getCart();
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