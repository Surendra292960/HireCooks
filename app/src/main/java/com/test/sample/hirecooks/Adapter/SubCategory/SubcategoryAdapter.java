package com.test.sample.hirecooks.Adapter.SubCategory;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.test.sample.hirecooks.Activity.AddorRemoveCallbacks;
import com.test.sample.hirecooks.Activity.Favourite.FavouriteActivity;
import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.Activity.ProductDatails.DetailsActivity;
import com.test.sample.hirecooks.Activity.SubCategory.SubCategoryActivity;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.LocalStorage;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.ViewModel.ViewModel;
import com.test.sample.hirecooks.databinding.ItemHorizontalLayoutBinding;
import com.test.sample.hirecooks.databinding.SubcategoryLayoutBinding;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.View.GONE;
import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;
import static com.test.sample.hirecooks.Models.Chat.ListObject.TYPE_CARDVIEW_GRID;
import static com.test.sample.hirecooks.Models.Chat.ListObject.TYPE_CARDVIEW_HORIZONTAL;

public class SubcategoryAdapter extends PagedListAdapter<Subcategory, RecyclerView.ViewHolder> {
    private List<Subcategory> productList;
    private Context context;
    private ViewModel viewModel;
    private int providedViewType = -1;
    private int discount = 0, discountPercentage = 0, displayrate = 0;
    private List<Subcategory> FavouriteList;
    private int Quantity = 0, FavQuantity = 0, sellRate = 0,displayRate = 0, SubTotal = 0;
    LocalStorage localStorage;
    Gson gson;

    public SubcategoryAdapter(Context context, List<Subcategory> productList, int providedViewType) {
        super(DIFF_CALLBACK);
        this.productList = productList;
        this.context = context;
        this.providedViewType = providedViewType;
        viewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(ViewModel.class);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case TYPE_CARDVIEW_GRID:
                viewHolder = new GridViewHolder(SubcategoryLayoutBinding.inflate(LayoutInflater.from(context)));
                break;
            case TYPE_CARDVIEW_HORIZONTAL:
                viewHolder = new HorizontalViewHolder(ItemHorizontalLayoutBinding.inflate(LayoutInflater.from(context)));
                break;
        }
        return viewHolder;
    }

    public Subcategory getItem(int position) {
        return productList.get(position);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        localStorage = new LocalStorage(context);
        gson = new Gson();
        FavouriteList = ((BaseActivity) context).getFavourite();
        switch (viewHolder.getItemViewType()) {
            case TYPE_CARDVIEW_GRID:
                Subcategory verticalProduct = productList.get(position);
                GridViewHolder gridViewHolder = (GridViewHolder) viewHolder;
                gridViewHolder.bind(verticalProduct);
                break;
            case TYPE_CARDVIEW_HORIZONTAL:
                Subcategory horizontalProduct = productList.get(position);
                HorizontalViewHolder horizontalViewHolder = (HorizontalViewHolder) viewHolder;
                horizontalViewHolder.bind(horizontalProduct);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //  super.getItemViewType(position);
        if(providedViewType==0){
            return TYPE_CARDVIEW_GRID;
        }else if(providedViewType==1){
            return TYPE_CARDVIEW_HORIZONTAL;
        }
        return -1;
    }


    private static DiffUtil.ItemCallback<Subcategory> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Subcategory>() {
                @Override
                public boolean areItemsTheSame(Subcategory oldItem, Subcategory newItem) {
                    return oldItem.id == newItem.id;
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(Subcategory oldItem, Subcategory newItem) {
                    return oldItem.equals(newItem);
                }
            };



    public class GridViewHolder extends RecyclerView.ViewHolder {
        SubcategoryLayoutBinding binding;
        public GridViewHolder(SubcategoryLayoutBinding gridBinding) {
            super(gridBinding.getRoot());
            binding = gridBinding;
        }

        @SuppressLint("SetTextI18n")
        public void bind(Subcategory product) {
            if(product!=null){
                getFavourites(binding.itemFavourite, product);
            if (product.getAcceptingOrder() == 0) {
                binding.orderNotAccepting.setVisibility(View.VISIBLE);
            } else {
                binding.orderNotAccepting.setVisibility(GONE);
            }
            binding.itemName.setVisibility(View.VISIBLE);
            binding.itemName.setText(product.getName());
            binding.itemShortDesc.setText(product.getDiscription());
            if (product.getImages() != null && product.getImages().size() != 0) {
                Glide.with(context).load(product.getImages().get(0).getImage()).into(binding.itemImage);
            }

            if (product.getSellRate() != 0 && product.getDisplayRate() != 0) {
                binding.itemSellrate.setText("\u20B9 " + product.getSellRate());
                SpannableString spanString = new SpannableString("\u20B9 " + product.getDisplayRate());
                spanString.setSpan(new StrikethroughSpan(), 0, spanString.length(), 0);
                binding.itemDisplayrate.setText(spanString);
                discount = (product.getDisplayRate() - product.getSellRate());
                displayrate = (product.getDisplayRate());
                discountPercentage = (discount * 100 / displayrate);
                if (discountPercentage != 0 && discountPercentage > 0) {
                    binding.itemDiscount.setText(discountPercentage + " %" + " Off ");
                }
            }

                binding.productsLayout.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(context, DetailsActivity.class);
                    bundle.putSerializable("SubCategory", product);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                });

                binding.addFav.setOnClickListener(v -> {
                    if (product != null && FavQuantity == 0) {
                        sellRate = product.getSellRate();
                        displayRate = product.getDisplayRate();
                        FavQuantity = 1;
                        if (product.getId() != 0 && product.getName() != null && product.getLink2() != null && product.getDiscription() != null && sellRate != 0 && product.getDisplayRate() != 0 && product.getFirmId() != null) {
                            SubTotal = (sellRate * FavQuantity);
                            if (context instanceof MainActivity||context instanceof SubCategoryActivity) {
                                Subcategory cart = new Subcategory(product.getId(),product.getSubcategoryid(),product.getLastUpdate(),product.getSearchKey(), product.getName(), product.getProductUniquekey(), product.getLink2(),  product.getLink3(),  product.getLink4(),product.getShieldLink(), product.getDiscription(), product.getDetailDiscription(), sellRate, displayRate,product.getFirmId(),product.getFirmLat(),product.getFirmLng(),product.getFirmAddress(),product.getFrimPincode(),product.getColors(),product.getImages(),product.getSizes(),product.getWeights(), SubTotal, 1,product.getBrand(),product.getGender(),product.getAge(),product.getAcceptingOrder());
                                FavouriteList = ((BaseActivity) context).getFavourite();
                                FavouriteList.add( cart );
                                String favourite = gson.toJson( FavouriteList );
                                localStorage.setFavourite( favourite );
                                ((AddorRemoveCallbacks) context).onAddProduct();
                                getFavourites(binding.itemFavourite, product);
                                Toast.makeText( context, "Added to Wishlist", Toast.LENGTH_SHORT ).show();
                            }
                        } else {
                            Toast.makeText( context, "This item can`t be add please remove item and try again", Toast.LENGTH_SHORT ).show();
                        }
                    }else{
                        for (int i = 0; i < FavouriteList.size(); i++) {
                            if (FavouriteList.get(i).getId()==product.getId()&&FavouriteList.get(i).getName().equalsIgnoreCase(product.getName())&&FavouriteList.get(i).getSellRate()==product.getSellRate()) {
                                FavouriteList.remove(FavouriteList.get(i));
                                Gson gson = new Gson();
                                String favourite = gson.toJson(FavouriteList);
                                Log.d("FAVOURITE", favourite);
                                localStorage.setFavourite(favourite);
                                getFavourites(binding.itemFavourite, product);
                                Toast.makeText( context, "Removed from Wishlist", Toast.LENGTH_SHORT ).show();
                            }
                        }
                    }
                });
            }
        }
    }

    private void getFavourites(ImageView itemFavourite, Subcategory product){
        FavouriteList = new ArrayList<>();
        FavouriteList = ((BaseActivity) context).getFavourite();
        if (!FavouriteList.isEmpty()) {
            for(int i=0; i<FavouriteList.size(); i++) {
                if (FavouriteList.get(i).getItemQuantity() <= 1 && FavouriteList.size() <= 1) {
                    //FavQuantity = cartList.get(i).getItemQuantity();
                }
                if(product!=null){
                    if (FavouriteList.get(i).getId()==product.getId()&&FavouriteList.get(i).getName().equalsIgnoreCase(product.getName())) {
                        itemFavourite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
                        FavQuantity = FavouriteList.get(i).getItemQuantity();
                    }else{
                        itemFavourite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
                        FavQuantity = 0;
                    }
                }
            }
        }else {
            itemFavourite.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
            FavQuantity = 0;
        }
    }

    public class HorizontalViewHolder extends RecyclerView.ViewHolder{
        ItemHorizontalLayoutBinding binding;
        public HorizontalViewHolder(ItemHorizontalLayoutBinding horizontalBinding) {
            super(horizontalBinding.getRoot());
            binding = horizontalBinding;
        }

        @SuppressLint("ResourceAsColor")
        public void bind(Subcategory product) {
            if(product!=null){
                if(product.getAcceptingOrder()==0){
                    binding.orderNotAccepting.setVisibility( View.VISIBLE);
                }else{
                    binding.orderNotAccepting.setVisibility( GONE);
                }
                if(context instanceof FavouriteActivity){
                    binding.addItemLayout.setVisibility(View.VISIBLE);
                    binding.add.setText("Remove");
                    binding.add.setTextColor( Color.parseColor("#ff6347"));
                    binding.orderNotAccepting.setVisibility( GONE);
                }else {
                    binding.addItemLayout.setVisibility(GONE);
                }
                binding.itemName.setVisibility(View.VISIBLE);
                binding.itemName.setText(product.getName());
                binding.itemShortDesc.setText(product.getDiscription());
                if(product.getImages()!=null&&product.getImages().size()!=0){
                    Glide.with(context).load(product.getImages().get( 0 ).getImage()).into( binding.itemImage);
                }

                if (product.getSellRate() != 0 && product.getDisplayRate()!= 0) {
                    binding.itemSellrate.setText("\u20B9 " + product.getSellRate());
                    SpannableString spanString = new SpannableString("\u20B9 " + product.getDisplayRate());
                    spanString.setSpan(new StrikethroughSpan(), 0, spanString.length(), 0);
                    binding.itemDisplayrate.setText(spanString);
                    discount = (product.getDisplayRate() - product.getSellRate());
                    displayrate = (product.getDisplayRate());
                    discountPercentage = (discount * 100 / displayrate);
                    if(discountPercentage!=0&&discountPercentage>0){
                        binding.itemDiscount.setText(discountPercentage + " %"+" Off " );
                    }
                }
                binding.add.setOnClickListener(v-> {
//                            removeFav(product);
                            Snackbar snackbar = Snackbar.make(binding.productsLayout, product.getName() + " removed from wishlist! ", Snackbar.LENGTH_LONG);
                            snackbar.show();
                });
                binding.productsLayout.setOnClickListener( v -> {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(context, DetailsActivity.class);
                    bundle.putSerializable("SubCategory",product);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                } );
            }
        }
    }
}