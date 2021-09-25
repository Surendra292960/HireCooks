package com.test.sample.hirecooks.Activity.Favourite;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Activity.AddorRemoveCallbacks;
import com.test.sample.hirecooks.Activity.Orders.PlaceOrderActivity;
import com.test.sample.hirecooks.Activity.ProductDatails.DetailsActivity;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.LocalStorage;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.view.View.GONE;

public class FavouriteActivity extends BaseActivity {
    private RecyclerView subcategory_recycler;
    private List<Subcategory> cartList;
    private RelativeLayout bottom_anchor_layout;
    private TextView item_count,checkout_amount,checkout;
    private View bottom_anchor,appRoot;
    private LinearLayout no_internet_connection_layout,favourite_layout;
    private FrameLayout no_result_found;
    private List<Subcategory> FavouriteList;
    private boolean checkNet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_favourite );
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("My WishList");
        this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initViews();
        if(NetworkUtil.checkInternetConnection(this)) {
            favourite_layout.setVisibility( View.VISIBLE );
            no_internet_connection_layout.setVisibility(View.GONE );
        }
        else {
            favourite_layout.setVisibility( View.GONE );
            no_result_found.setVisibility( View.GONE );
            no_internet_connection_layout.setVisibility( View.VISIBLE );
        }
    }

    private void initViews() {
        subcategory_recycler = findViewById( R.id.subcategory_recycler );
        appRoot = findViewById(R.id.appRoot);
        favourite_layout = findViewById(R.id.favourite_layout);
        no_internet_connection_layout = findViewById(R.id.no_internet_connection_layout);
        bottom_anchor_layout = findViewById(R.id.bottom_anchor_layout);
        no_result_found = findViewById(R.id.no_result_found);

        View view = findViewById(R.id.footerView);
        item_count =  view.findViewById(R.id.item_count);
        bottom_anchor =  view.findViewById(R.id.bottom_anchor);
        checkout_amount = view.findViewById(R.id.checkout_amount);
        checkout = view.findViewById(R.id.checkout);
        getCart();
        getFavourites();
        checkout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( FavouriteActivity.this, PlaceOrderActivity.class ) .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK) );
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

    boolean isSearchBarHide = false;


    private void getCart() {
        cartList = new ArrayList<>();
        cartList = getnewCartList();
        if (!cartList.isEmpty()) {
            animateBottomAnchor(false);
            for(int i=0; i<cartList.size(); i++){
                if(cartList.get(i).getItemQuantity()<=1&&cartList.size()<=1){
                    bottom_anchor_layout.setAnimation( AnimationUtils.loadAnimation(this,R.anim.fade_transition_animation));
                }
            }
            bottom_anchor_layout.setVisibility(View.VISIBLE);
            checkout_amount.setText("₹  " + getTotalPrice());
            item_count.setText("" + cartCount());

        } else {
            bottom_anchor_layout.setVisibility( GONE);
        }
    }

    private void getFavourites(){
        FavouriteList = new ArrayList<>();
        FavouriteList = getFavourite();
        if (!FavouriteList.isEmpty()) {
            no_result_found.setVisibility(View.GONE);
            SubcategoryAdapter adapter = new SubcategoryAdapter(FavouriteActivity.this, FavouriteList);
            subcategory_recycler.setAdapter(adapter);
            for(int i=0; i<FavouriteList.size(); i++) {
                if (FavouriteList.get(i).getItemQuantity() <= 1 && FavouriteList.size() <= 1) {

                }
            }
        }else{
            no_result_found.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(checkNet) {
            getFavourites();
        }
    }

    public class SubcategoryAdapter extends RecyclerView.Adapter<FavouriteActivity.SubcategoryAdapter.MyViewHolder> {
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
        public FavouriteActivity.SubcategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal_layout, parent, false);
            return new FavouriteActivity.SubcategoryAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final FavouriteActivity.SubcategoryAdapter.MyViewHolder holder, final int position) {

            final Subcategory product = productList.get(position);
            localStorage = new LocalStorage(context);
            gson = new Gson();
            cartList = ((BaseActivity) context).getnewCartList();

            if(product!=null){
                if(product.getAcceptingOrder()==0){
                    holder.order_not_accepting.setVisibility( View.VISIBLE);
                    holder.add_item_layout.setVisibility( GONE);

                }else{
                    holder.order_not_accepting.setVisibility( GONE);
                    holder.add_item_layout.setVisibility( GONE);
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
                        if (context instanceof FavouriteActivity) {
                            Subcategory cart = new Subcategory(product.getId(),product.getSubcategoryid(),product.getLastUpdate(),product.getSearchKey(), product.getName(), product.getProductUniquekey(), product.getLink2(),  product.getLink3(),  product.getLink4(),product.getShieldLink(), product.getDiscription(), product.getDetailDiscription(), sellRate, displayRate,product.getFirmId(),product.getFirmLat(),product.getFirmLng(),product.getFirmAddress(),product.getFrimPincode(),product.getColors(),product.getImages(),product.getSizes(),product.getWeights(), SubTotal, 1,product.getBrand(),product.getGender(),product.getAge(),product.getAcceptingOrder());
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
                            if (context instanceof FavouriteActivity) {
                                Subcategory cart = new Subcategory(product.getId(),product.getSubcategoryid(),product.getLastUpdate(),product.getSearchKey(), product.getName(), product.getProductUniquekey(), product.getLink2(),  product.getLink3(),  product.getLink4(),product.getShieldLink(), product.getDiscription(), product.getDetailDiscription(), sellRate, displayRate,product.getFirmId(),product.getFirmLat(),product.getFirmLng(),product.getFirmAddress(),product.getFrimPincode(),product.getColors(),product.getImages(),product.getSizes(),product.getWeights(), SubTotal, 1,product.getBrand(),product.getGender(),product.getAge(),product.getAcceptingOrder());
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
                intent .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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
            LinearLayout add_item_layout,quantity_ll;
            FrameLayout order_not_accepting;
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
