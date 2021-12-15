package com.test.sample.hirecooks.Activity.Favourite;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.gson.Gson;
import com.bumptech.glide.Glide;
import com.test.sample.hirecooks.Activity.AddorRemoveCallbacks;
import com.test.sample.hirecooks.Activity.Orders.PlaceOrderActivity;
import com.test.sample.hirecooks.Activity.ProductDatails.DetailsActivity;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.LocalStorage;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.NetworkUtil;
import com.test.sample.hirecooks.databinding.ActivityFavouriteBinding;
import com.test.sample.hirecooks.databinding.ItemHorizontalLayoutBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static android.view.View.GONE;

public class FavouriteActivity extends BaseActivity {
    private List<Subcategory> cartList;
    private List<Subcategory> FavouriteList;
    private boolean checkNet = false;
    private ActivityFavouriteBinding favouriteBinding;
    private FavViewModel favViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        favouriteBinding = ActivityFavouriteBinding.inflate(getLayoutInflater());
        View view = favouriteBinding.getRoot();
        setContentView(view);
        favViewModel = ViewModelProviders.of(this).get(FavViewModel.class);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("My WishList");
        this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initViews();
        if(NetworkUtil.checkInternetConnection(this)) {
            favouriteBinding.favouriteLayout.setVisibility( View.VISIBLE );
            favouriteBinding.noInternetConnectionLayout.setVisibility(View.GONE );
        }
        else {
            favouriteBinding.favouriteLayout.setVisibility( View.GONE );
            favouriteBinding.noResultFound.setVisibility( View.GONE );
            favouriteBinding.noInternetConnectionLayout.setVisibility( View.VISIBLE );
        }
    }

    private void initViews() {
        getCart();
        getFavourites();
        favouriteBinding.footerView.checkout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( FavouriteActivity.this, PlaceOrderActivity.class ) .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK) );
            }
        });
        favouriteBinding.swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        favouriteBinding.swipeToRefresh.setRefreshing(false);
                        getFavourites();
                    }
                }, 3000);
            }
        });
    }

    boolean isBottomAnchorNavigationHide = false;

    private void animateBottomAnchor(final boolean hide) {
        if (isBottomAnchorNavigationHide && hide || !isBottomAnchorNavigationHide && !hide) return;
        isBottomAnchorNavigationHide = hide;
        int moveY = hide ? (2 * favouriteBinding.footerView.bottomAnchor.getHeight()) : 0;
        favouriteBinding.footerView.bottomAnchor.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    boolean isSearchBarHide = false;


    private void getCart() {
        cartList = new ArrayList<>();
        cartList = getnewCartList();
        if (!cartList.isEmpty()) {
            animateBottomAnchor(false);
            for(int i=0; i<cartList.size(); i++){
                if(cartList.get(i).getItemQuantity()<=1&&cartList.size()<=1){
                    favouriteBinding.bottomAnchorLayout.setAnimation( AnimationUtils.loadAnimation(this,R.anim.fade_transition_animation));
                }
            }
            favouriteBinding.bottomAnchorLayout.setVisibility(View.VISIBLE);
            favouriteBinding.footerView.checkoutAmount.setText("₹  " + getTotalPrice());
            favouriteBinding.footerView.itemCount.setText("" + cartCount());

        } else {
            favouriteBinding.bottomAnchorLayout.setVisibility( GONE);
        }
    }

    private void getFavourites(){
        FavouriteList = new ArrayList<>();
        FavouriteList = getFavourite();
        if (!FavouriteList.isEmpty()) {
            favouriteBinding.noResultFound.setVisibility(View.GONE);
            FavouriteAdapter adapter = new FavouriteAdapter(FavouriteActivity.this, FavouriteList);
            favouriteBinding.favRecycler.setAdapter(adapter);
            for(int i=0; i<FavouriteList.size(); i++) {
                if (FavouriteList.get(i).getItemQuantity() <= 1 && FavouriteList.size() <= 1) {

                }
            }
        }else{
            favouriteBinding.noResultFound.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(checkNet) {
            getFavourites();
        }
    }

    public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.MyViewHolder> {
        List<Subcategory> productList;
        Context mCtx;
        String Tag;
        LocalStorage localStorage;
        Gson gson;
        List<Subcategory> cartList = new ArrayList<>();
        String weight;
        private int discount = 0, discountPercentage = 0, displayrate = 0,sellRate = 0,displayRate = 0, SubTotal = 0,Quantity = 0;

        public FavouriteAdapter(Context mCtx, List<Subcategory> productList) {
            this.productList = productList;
            this.mCtx = mCtx;
        }

        @NonNull
        @Override
        public FavouriteAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            return new MyViewHolder(ItemHorizontalLayoutBinding.inflate(LayoutInflater.from(mCtx)));
        }

        @Override
        public void onBindViewHolder(@NonNull final FavouriteAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

            final Subcategory product = productList.get(position);
            localStorage = new LocalStorage(mCtx);
            gson = new Gson();
            cartList = ((BaseActivity) mCtx).getnewCartList();

            if(product!=null){
                if(product.getAcceptingOrder()==0){
                    holder.binding.orderNotAccepting.setVisibility( View.VISIBLE);
                    holder.binding.addItemLayout.setVisibility( GONE);

                }else{
                    holder.binding.orderNotAccepting.setVisibility( GONE);
                    holder.binding.addItemLayout.setVisibility( GONE);
                }

                holder.binding.deleteItem.setVisibility(View.VISIBLE);
                holder.binding.itemName.setText(product.getName());
                holder.binding.itemShortDesc.setText(product.getDiscription());
                holder.binding.itemDescription.setText(product.getDetailDiscription());
                if(product.getImages()!=null&&product.getImages().size()!=0){
                    Glide.with(FavouriteActivity.this).load(product.getImages().get( 0 ).getImage()).into(holder.binding.itemImage);
                }

                if (product.getSellRate() != 0 && product.getDisplayRate()!= 0) {
                    holder.binding.itemSellrate.setText("\u20B9 " + product.getSellRate());
                    SpannableString spanString = new SpannableString("\u20B9 " + product.getDisplayRate());
                    spanString.setSpan(new StrikethroughSpan(), 0, spanString.length(), 0);
                    holder.binding.itemSellrate.setText(spanString);
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
                        if (mCtx instanceof FavouriteActivity) {
                            Subcategory cart = new Subcategory(product.getId(),product.getSubcategoryid(),product.getLastUpdate(),product.getSearchKey(), product.getName(), product.getProductUniquekey(), product.getLink2(),  product.getLink3(),  product.getLink4(),product.getShieldLink(), product.getDiscription(), product.getDetailDiscription(), sellRate, displayRate,product.getFirmId(),product.getFirmLat(),product.getFirmLng(),product.getFirmAddress(),product.getFrimPincode(),product.getColors(),product.getImages(),product.getSizes(),product.getWeights(), SubTotal, 1,product.getBrand(),product.getGender(),product.getAge(),product.getAcceptingOrder());
                            cartList = ((BaseActivity) mCtx).getnewCartList();
                            cartList.add(cart);
                            String cartStr = gson.toJson(cartList);
                            localStorage.setCart(cartStr);
                            ((AddorRemoveCallbacks) mCtx).onAddProduct();
                            notifyItemChanged(position);
                            getCart();
                        }
                    }else{
                        holder.binding.add.setVisibility(View.VISIBLE);
                        holder.binding.quantityLl.setVisibility(GONE);
                        Toast.makeText(mCtx, "This item can`t be add please remove item and try again", Toast.LENGTH_SHORT).show();
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
                            if (mCtx instanceof FavouriteActivity) {
                                Subcategory cart = new Subcategory(product.getId(),product.getSubcategoryid(),product.getLastUpdate(),product.getSearchKey(), product.getName(), product.getProductUniquekey(), product.getLink2(),  product.getLink3(),  product.getLink4(),product.getShieldLink(), product.getDiscription(), product.getDetailDiscription(), sellRate, displayRate,product.getFirmId(),product.getFirmLat(),product.getFirmLng(),product.getFirmAddress(),product.getFrimPincode(),product.getColors(),product.getImages(),product.getSizes(),product.getWeights(), SubTotal, 1,product.getBrand(),product.getGender(),product.getAge(),product.getAcceptingOrder());
                                cartList = ((BaseActivity) mCtx).getnewCartList();
                                cartList.add(cart);
                                String cartStr = gson.toJson(cartList);
                                localStorage.setCart(cartStr);
                                ((AddorRemoveCallbacks) mCtx).onAddProduct();
                                notifyItemChanged(position);
                                getCart();

                            }
                        } else {
                            Toast.makeText(mCtx, "Please Add Quantity", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        holder.binding.add.setVisibility(View.VISIBLE);
                        holder.binding.quantityLl.setVisibility(GONE);
                        Toast.makeText(mCtx, "This item can`t be add please remove item and try again", Toast.LENGTH_SHORT).show();
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

            holder.binding.deleteItem.setOnClickListener( v -> {
                favViewModel.deleteFavItem(position,FavouriteList,FavouriteActivity.this);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, FavouriteList.size());
                getFavourites();
            } );


            holder.binding.cardView.setOnClickListener( v -> {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(mCtx, DetailsActivity.class);
                bundle.putSerializable("SubCategory",productList.get(position));
                intent.putExtras(bundle);
                intent .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                mCtx.startActivity(intent);
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
