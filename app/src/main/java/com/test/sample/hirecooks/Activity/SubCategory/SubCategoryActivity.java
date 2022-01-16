package com.test.sample.hirecooks.Activity.SubCategory;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.bumptech.glide.Glide;
import com.test.sample.hirecooks.Activity.Orders.PlaceOrderActivity;
import com.test.sample.hirecooks.Activity.Search.SearchResultActivity;
import com.test.sample.hirecooks.Adapter.SubCategory.SubcategoryAdapter;
import com.test.sample.hirecooks.Models.Category.Category;
import com.test.sample.hirecooks.Models.Chat.ListObject;
import com.test.sample.hirecooks.Models.Filter;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.NetworkUtil;
import com.test.sample.hirecooks.Utils.Preferences;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.ViewModel.ViewModel;
import com.test.sample.hirecooks.databinding.ActivitySubCategoryBinding;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import static android.view.View.GONE;

public class SubCategoryActivity extends BaseActivity {
    private List<Subcategory> cartList;
    private Category category;
    private List<Subcategory> filteredList;
    private static User vender1 = new User();
    private ActivitySubCategoryBinding binding;
    private ViewModel viewModel;
    private User user;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        binding = ActivitySubCategoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        viewModel=new ViewModelProvider(SubCategoryActivity.this).get(ViewModel.class);
        user = SharedPrefManager.getInstance(this).getUser();
        this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSubCategoryViews();
    }

    public void getSubCategoryViews() {
        getCart();
        binding.mToolbarInterface.mToolbar.setVisibility(View.VISIBLE);
        binding.mToolbarInterface.headerLay.setVisibility(View.VISIBLE);
        binding.footerView.checkout.setOnClickListener(v -> startActivity( new Intent( SubCategoryActivity.this, PlaceOrderActivity.class )));// .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK) ));
        binding.mToolbarInterface.search.setOnClickListener(v -> startActivity( new Intent( SubCategoryActivity.this, SearchResultActivity.class )));// .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK) ));
        binding.mToolbarInterface.goBack.setOnClickListener(view1 -> finish());
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            category = (Category) bundle.getSerializable("Category");
        }
        if(NetworkUtil.checkInternetConnection(this)) {
            binding.subcategoryLayout.setVisibility( View.VISIBLE );
            binding.noInternetConnectionLayout.setVisibility(View.GONE );
            if(category!=null) {
                getSubCategory(category.getId());
            }else {
                Toast.makeText( this, "Comming soon", Toast.LENGTH_SHORT ).show();
            }
        }
        else {
            binding.subcategoryLayout.setVisibility( View.GONE );
            binding.noInternetConnectionLayout.setVisibility( View.VISIBLE );
        }

        NestedScrollView nested_content = findViewById(R.id.nested_scroll_view);
        nested_content.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY < oldScrollY) { // up
                animateBottomAnchor(false);
                //animateHeader(false);
            }
            if (scrollY > oldScrollY) { // down
                animateBottomAnchor(true);
                // animateHeader(true);
            }
        });
        binding.swipeToRefresh.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            binding.swipeToRefresh.setRefreshing(false);
            if(category!=null&&category.getId()!=0){
                getSubCategory(category.getId());
            }
        }, 3000));

        binding.mToolbarInterface.sortLayout.setOnClickListener(v -> {
            List<String> prices = Arrays.asList(new String[]{"0-0", "0-100", "101-200", "201-1000"});
            if (!Preferences.filters.containsKey(Filter.INDEX_PRICE)) {
                Preferences.filters.put(Filter.INDEX_PRICE, new Filter("Price", prices, new ArrayList()));
            }
            SortSheetDialog bottomSheet = new SortSheetDialog(SubCategoryActivity.this, Preferences.filters);
            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
        }); binding.mToolbarInterface.genderLayout.setOnClickListener(v -> {
            GenderSheetDialog bottomSheet = new GenderSheetDialog(SubCategoryActivity.this,getList());
            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
        }); binding.mToolbarInterface.filterLayout.setOnClickListener(v -> {
            FilterSheetDialog bottomSheet = new FilterSheetDialog(SubCategoryActivity.this,getList());
            bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");
        });
    }

    @SuppressLint("NewApi")
    private void getSubCategory(int id) {
        viewModel.getNearBySubCategoryBySubId(user.getId(), id).observe(this, subcategoryResponses -> subcategoryResponses.forEach(subcategory ->{
            if(subcategory.getSubcategory()!=null&&subcategory.getSubcategory().size()!=0){
                filteredList = subcategory.getSubcategory();
                binding.recyclerview.setVisibility(View.VISIBLE);
                binding.noResultFound.setVisibility(GONE);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
                SubcategoryAdapter mAdapter = new SubcategoryAdapter( this, subcategory.getSubcategory(), ListObject.TYPE_CARDVIEW_GRID);
                binding.recyclerview.setLayoutManager(gridLayoutManager);
                binding.recyclerview.setAdapter(mAdapter);
            }else {
                binding.noResultFound.setVisibility(View.VISIBLE);
            }
        }));
    }

    private boolean priceContains(List<String> prices, Double price) {
        boolean flag = false;
        for (String p : prices) {
            String tmpPrices[] = p.split("-");
            if (price >= Double.valueOf(tmpPrices[0]) && price <= Double.valueOf(tmpPrices[1])) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    private List<User> getList() {
        List<User> offerList = new ArrayList<>();
        offerList.add(new User(0, "Female","https://cdn-icons-png.flaticon.com/512/417/417776.png"));
        offerList.add(new User(1,"Male","https://cdn-icons-png.flaticon.com/512/4128/4128176.png"));
        offerList.add(new User(2,"Girls","https://cdn-icons-png.flaticon.com/512/163/163811.png"));
        offerList.add(new User(3,"Boys","https://cdn-icons-png.flaticon.com/512/145/145867.png"));
        offerList.add(new User(4, "Female","https://cdn-icons-png.flaticon.com/512/417/417776.png"));
        offerList.add(new User(5,"Male","https://cdn-icons-png.flaticon.com/512/4128/4128176.png"));
        offerList.add(new User(6,"Girls","https://cdn-icons-png.flaticon.com/512/163/163811.png"));
        offerList.add(new User(7,"Boys","https://cdn-icons-png.flaticon.com/512/145/145867.png"));
        offerList.add(new User(0, "Female","https://cdn-icons-png.flaticon.com/512/417/417776.png"));
        offerList.add(new User(1,"Male","https://cdn-icons-png.flaticon.com/512/4128/4128176.png"));
        offerList.add(new User(2,"Girls","https://cdn-icons-png.flaticon.com/512/163/163811.png"));
        offerList.add(new User(3,"Boys","https://cdn-icons-png.flaticon.com/512/145/145867.png"));
        offerList.add(new User(4, "Female","https://cdn-icons-png.flaticon.com/512/417/417776.png"));
        offerList.add(new User(5,"Male","https://cdn-icons-png.flaticon.com/512/4128/4128176.png"));
        offerList.add(new User(6,"Girls","https://cdn-icons-png.flaticon.com/512/163/163811.png"));
        offerList.add(new User(7,"Boys","https://cdn-icons-png.flaticon.com/512/145/145867.png"));
        return offerList;
    }

    boolean isBottomAnchorNavigationHide = false;

    private void animateBottomAnchor(final boolean hide) {
        if (isBottomAnchorNavigationHide && hide || !isBottomAnchorNavigationHide && !hide) return;
        isBottomAnchorNavigationHide = hide;
        int moveY = hide ? (2 * binding.footerView.bottomAnchor.getHeight()) : 0;
        binding.footerView.bottomAnchor.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

   /* boolean isHeaderHide = false;

    private void animateHeader(final boolean hide) {
        if (isHeaderHide && hide || !isHeaderHide && !hide) return;
        isHeaderHide = hide;
        int moveY = hide ? -(2 * binding.headerView.headerLay.getHeight()) : 0;
        binding.headerView.headerLay.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }*/

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.getItem(0);
        SearchManager searchManager = (SearchManager) getSystemService( Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setFocusable(true);
        searchItem.expandActionView();

        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(filteredList.size()!=0){
                    startSearch( query );
                }else{
                    showalertbox("No Match found");
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(filteredList.size()!=0){
                    startSearch( newText );
                }
                return false;
            }
        } );
        return super.onCreateOptionsMenu(menu);
    }

    private void getCart() {
        cartList = new ArrayList<>();
        cartList = getnewCartList();
        if (!cartList.isEmpty()) {
            animateBottomAnchor(false);
            for(int i=0; i<cartList.size(); i++){
                if(cartList.get(i).getItemQuantity()<=1&&cartList.size()<=1){
                    binding.bottomAnchorLayout.setAnimation( AnimationUtils.loadAnimation(this,R.anim.fade_transition_animation));
                }
            }
            binding.bottomAnchorLayout.setVisibility(View.VISIBLE);
            binding.footerView.checkoutAmount.setText("₹  " + getTotalPrice());
            binding.footerView.itemCount.setText("" + cartCount());

        } else {
            binding.bottomAnchorLayout.setVisibility( GONE);
        }
    }

    private void startSearch(CharSequence text) {
        List<Subcategory> subcategory = new ArrayList<>();
        try {
            if (filteredList != null && filteredList.size() != 0) {
                for (int i = 0; i < filteredList.size(); i++) {
                    String productName = "";

                    if (filteredList.get(i).getName() != null) {
                        productName = filteredList.get(i).getName();
                    }

                    if (productName.toLowerCase().contains(String.valueOf(text).toLowerCase())) {
                        subcategory.add(filteredList.get(i));
                    }
                }

                SubcategoryAdapter mAdapter = new SubcategoryAdapter( SubCategoryActivity.this, subcategory, ListObject.TYPE_CARDVIEW_GRID);
                binding.recyclerview.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCart();
    }

    /*public class SubcategoryAdapter extends RecyclerView.Adapter<SubCategoryActivity.SubcategoryAdapter.MyViewHolder> {
        List<Subcategory> productList;
        Context context;
        private static final int LOADING = 0;
        private static final int ITEM = 1;
        private boolean isLoadingAdded = false;
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
        public SubCategoryActivity.SubcategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            return new SubcategoryAdapter.MyViewHolder(ItemHorizontalLayoutBinding.inflate(LayoutInflater.from(context)));
        }

        public Subcategory getItem(int position) {
            return productList.get(position);
        }
        @Override
        public void onBindViewHolder(@NonNull final SubCategoryActivity.SubcategoryAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            final Subcategory product = productList.get(position);
            localStorage = new LocalStorage(context);
            gson = new Gson();
            cartList = ((BaseActivity) context).getnewCartList();

            if(product!=null){
                if(product.getAcceptingOrder()==0){
                    holder.binding.orderNotAccepting.setVisibility( View.VISIBLE);
                    holder.binding.addItemLayout.setVisibility( GONE);

                }else{
                    holder.binding.orderNotAccepting.setVisibility( GONE);
                    holder.binding.addItemLayout.setVisibility( GONE);
                }

                holder.binding.itemName.setText(product.getName());
                holder.binding.itemShortDesc.setText(product.getDiscription());
                //holder.item_short_desc.setText(product.getDetailDiscription());
                holder.binding.progressDialog.setVisibility( View.GONE );
                if(product.getImages()!=null&&product.getImages().size()!=0){
                    Glide.with(context).load(product.getImages().get( 0 ).getImage()).into( holder.binding.itemImage);
                }

                if (product.getSellRate() != 0 && product.getDisplayRate()!= 0) {
                    holder.binding.itemSellrate.setText("\u20B9 " + product.getSellRate());
                    SpannableString spanString = new SpannableString("\u20B9 " + product.getDisplayRate());
                    spanString.setSpan(new StrikethroughSpan(), 0, spanString.length(), 0);
                    holder.binding.itemDisplayrate.setText(spanString);
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
                        if (context instanceof SubCategoryActivity) {
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
                        holder.binding.add.setVisibility(View.VISIBLE);
                        holder.binding.quantityLl.setVisibility(GONE);
                        Toast.makeText(context, "This item can`t be add please remove item and try again", Toast.LENGTH_SHORT).show();
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
                            if (context instanceof SubCategoryActivity) {
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
                        holder.binding.add.setVisibility(View.VISIBLE);
                        holder.binding.quantityLl.setVisibility(GONE);
                        Toast.makeText(context, "This item can`t be add please remove item and try again", Toast.LENGTH_SHORT).show();
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


            holder.binding.cardView.setOnClickListener( v -> {
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
        public final int getItemViewType(int position) {
            return position;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ItemHorizontalLayoutBinding binding;
            public MyViewHolder(@NonNull ItemHorizontalLayoutBinding bind) {
                super(bind.getRoot());
                binding = bind;
            }
        }
    }*/

    public static class SortSheetDialog extends BottomSheetDialogFragment {
        private Context mCtx;
        private  HashMap<Integer, Filter> user;

        public SortSheetDialog(Context mCtx, HashMap<Integer, Filter> user) {
            this.mCtx = mCtx;
            this.user = user;
        }

        @SuppressLint("WrongConstant")
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.sort_dialog, container, false);
            RecyclerView recyclerView = v.findViewById(R.id.sort_recycler);
            Button done = v.findViewById(R.id.done);
            ImageView cancel = v.findViewById(R.id.cancel);
            SortAdapter adapter = new SortAdapter(mCtx,user,recyclerView);
            recyclerView.setAdapter(adapter);

            cancel.setOnClickListener(view -> {Preferences.filters.get(Filter.INDEX_PRICE).setSelected(new ArrayList());dismiss();});
            done.setOnClickListener(view -> {
                ((SubCategoryActivity)mCtx).sortProduct("Price");
                dismiss();
            });

            return v;
        }
    }

    public static class SortAdapter extends RecyclerView.Adapter<SortAdapter.ViewHolder> {
        private Context mCtx;
        private RecyclerView recyclerView;
        HashMap<Integer, Filter> offerList;
        private int selectedPostion = 0;

        public SortAdapter(Context mCtx, HashMap<Integer, Filter> offerList, RecyclerView recyclerView) {
            this.mCtx = mCtx;
            this.offerList = offerList;
            this.recyclerView = recyclerView;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sort, null);
            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder myViewHolder, @SuppressLint("RecyclerView") int position) {
            myViewHolder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerView.setAdapter(new FilterValuesAdapter(mCtx, position));
                    selectedPostion = position;
                    notifyDataSetChanged();
                }
            });

            recyclerView.setAdapter(new FilterValuesAdapter(mCtx, selectedPostion));
            //myViewHolder.container.setBackgroundColor(selectedPostion == position ? Color.WHITE : Color.TRANSPARENT);
            myViewHolder.text.setText(offerList.get(position).getName());
            ((SubCategoryActivity)mCtx).yourPublicMethod(offerList.get(position).getName());

        }

        @Override
        public int getItemCount() {
            return offerList==null?0:offerList.size();
        }
        class ViewHolder extends RecyclerView.ViewHolder {
            public TextView text;
            private View container;

            public ViewHolder(View view) {
                super(view);
                container = view;
                text = view.findViewById(R.id.text);
            }
        }
    }

    private void yourPublicMethod(String text) {
        Toast.makeText(SubCategoryActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    private void sortProduct(String mString) {
        if(mString.equalsIgnoreCase("Price")){
            List<Subcategory> sortfilteredList = new ArrayList<>();
            if (!Preferences.filters.isEmpty()) {
                ArrayList<Subcategory> filteredItems = new ArrayList<>();
                List<String> prices = Preferences.filters.get(Filter.INDEX_PRICE).getSelected();
                for (Subcategory item : filteredList) {
                    boolean priceMatched = true;
                    if (prices.size() > 0 && !priceContains(prices, (double) item.getSellRate())) {
                        priceMatched = false;
                    }
                    if (priceMatched) {
                        filteredItems.add(item);
                    }
                }
                sortfilteredList = filteredItems;
            }
            Subcategory[] itemsArr3 = new Subcategory[sortfilteredList.size()];
            itemsArr3 = sortfilteredList.toArray(itemsArr3);
            Arrays.sort(itemsArr3, Subcategory.priceComparator);
            sortfilteredList = Arrays.asList(itemsArr3);
            SubcategoryAdapter subcategoryAdapter = new SubcategoryAdapter(this,sortfilteredList, ListObject.TYPE_CARDVIEW_GRID);
            binding.recyclerview.setAdapter(subcategoryAdapter);
        }else if(!mString.equalsIgnoreCase("Price")){
            List<Subcategory> sortfilteredList = new ArrayList<>();
            if (!mString.isEmpty()) {
                List<Subcategory> genderList = new ArrayList<>();
                for (Subcategory item : filteredList) {
                    if (mString.equalsIgnoreCase(item.getGender())) {
                        genderList.add(item);
                    }
                }
                sortfilteredList = genderList;
            }
            Subcategory[] itemsArr3 = new Subcategory[sortfilteredList.size()];
            itemsArr3 = sortfilteredList.toArray(itemsArr3);
            Arrays.sort(itemsArr3, Subcategory.genderComparator);
            sortfilteredList = Arrays.asList(itemsArr3);
            SubcategoryAdapter subcategoryAdapter = new SubcategoryAdapter(this,sortfilteredList, ListObject.TYPE_CARDVIEW_GRID);
            binding.recyclerview.setAdapter(subcategoryAdapter);
        }
    }

    public static class GenderSheetDialog extends BottomSheetDialogFragment {
        private Context mCtx;
        private  List<User> user;

        public GenderSheetDialog(Context mCtx, List<User> user) {
            this.mCtx = mCtx;
            this.user = user;
        }

        @SuppressLint("WrongConstant")
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.gender_sheet_dialog, container, false);
            RecyclerView recyclerView = v.findViewById(R.id.gender_recycler);
            ImageView cancel = v.findViewById(R.id.cancel);
            Button done = v.findViewById(R.id.done);
            GendersAdapter genderAdapter = new GendersAdapter(mCtx,user);
            recyclerView.setAdapter(genderAdapter);
            LinearLayoutManager tlinearLayoutManager = new LinearLayoutManager(mCtx);
            if (mCtx.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                tlinearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
            } else {
                tlinearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
            }
            recyclerView.setLayoutManager(tlinearLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            cancel.setOnClickListener(view -> dismiss());
            done.setOnClickListener(view -> {((SubCategoryActivity)mCtx).sortProduct(vender1.getGender());dismiss();});
            return v;
        }
    }


    public static class FilterSheetDialog extends BottomSheetDialogFragment {
        public RecyclerView recyclerView1,recyclerView2;
        public FilterSearchAdapter sortSearchAdapter;
        public AppCompatButton apply,clear_text;
        public ImageView cancel;
        public Context mCtx;
        public List<User> list;

        public FilterSheetDialog(Context mCtx, List<User> list) {
            this.mCtx = mCtx;
            this.list = list;
        }

        @SuppressLint("WrongConstant")
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.filter_dialog, container, false);
            recyclerView1 = v.findViewById(R.id.list1);
            recyclerView2 = v.findViewById(R.id.list2);
            cancel = v.findViewById(R.id.cancel);
            apply = v.findViewById(R.id.apply);
            clear_text = v.findViewById(R.id.clear_text);
            SearchView sort_serch = v.findViewById(R.id.sort_serch);

            FiltertAdapter sortAdapter = new FiltertAdapter(mCtx,list);
            recyclerView1.setAdapter(sortAdapter);

            sortSearchAdapter = new FilterSearchAdapter(mCtx,list);
            recyclerView2.setAdapter(sortSearchAdapter);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
            sort_serch.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if(list.size()!=0){
                        startsortSearch( query );
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if(list.size()!=0){
                        startsortSearch( newText );
                    }
                    return false;
                }
            } );
            return v;
        }

        private void startsortSearch(CharSequence text) {
            List<User> userList = new ArrayList<>();
            try {
                if (list != null && list.size() != 0) {
                    for (int i = 0; i < list.size(); i++) {
                        String productName = "";

                        if (list.get(i).getGender() != null) {
                            productName = list.get(i).getGender();
                        }

                        if (productName.toLowerCase().contains(String.valueOf(text).toLowerCase())) {
                            userList.add(list.get(i));
                        }
                    }

                    FilterSearchAdapter sortSearchAdapter = new FilterSearchAdapter(mCtx,userList);
                    recyclerView2.setAdapter(sortSearchAdapter);
                    sortSearchAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
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
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public static class FilterSearchAdapter extends RecyclerView.Adapter<FilterSearchAdapter.ViewHolder> {
        private Context mCtx;
        private List<User> offerList;
        private User sort = new User();

        public FilterSearchAdapter(Context mCtx, List<User> offerList) {
            this.mCtx = mCtx;
            this.offerList = offerList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            @SuppressLint("InflateParams") View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filter_search_text, null);
            return new ViewHolder(itemLayoutView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            if(offerList.size()!=0){
                User category = offerList.get(position);
                holder.sort_search_text.setText(category.getGender());
                holder.sort_search_text.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(View view) {
                        sort = category;
                        notifyDataSetChanged();

                    }
                });

                if(sort.equals(category)){
                    holder.sort_search_text.setChecked(true);
                    ((SubCategoryActivity)mCtx).yourPublicMethod(holder.sort_search_text.getText().toString());
                }else{
                    holder.sort_search_text.setChecked(false);
                }
            }
        }

        @Override
        public int getItemCount() {
            return offerList==null?0:offerList.size();
        }
        class ViewHolder extends RecyclerView.ViewHolder {
            public CheckBox sort_search_text;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                sort_search_text = itemLayoutView.findViewById(R.id.sort_search_text);
            }
        }
    }

    public static class FiltertAdapter extends RecyclerView.Adapter<FiltertAdapter.ViewHolder> {
        private Context mCtx;
        private List<User> offerList;
        private User sort = new User();

        public FiltertAdapter(Context mCtx, List<User> offerList) {
            this.mCtx = mCtx;
            this.offerList = offerList;
        }

        @Override
        public FiltertAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filter_text, null);
            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(FiltertAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            if(offerList.size()!=0){
                User category = offerList.get(position);
                holder.sort_text.setText(category.getGender());
                Glide.with(mCtx).load(category.getImage()).into(holder.sort_image);
                holder.filter_text_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sort = category;
                        notifyDataSetChanged();
                    }
                });

                if(sort.equals(category)){
                    holder.filter_text_lay.setBackgroundColor(android.graphics.Color.parseColor("#dddddd"));
                    holder.sort_text.setTextColor(android.graphics.Color.parseColor("#000000"));
                    ((SubCategoryActivity)mCtx).yourPublicMethod(holder.sort_text.getText().toString());
                }else{
                    holder.filter_text_lay.setBackgroundColor(android.graphics.Color.parseColor("#54595C"));
                    holder.sort_text.setTextColor(android.graphics.Color.parseColor("#ffffff"));
                }
            }
        }

        @Override
        public int getItemCount() {
            return offerList==null?0:offerList.size();
        }
        class ViewHolder extends RecyclerView.ViewHolder {
            public TextView sort_text;
            private ImageView sort_image;
            private LinearLayout filter_text_lay;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                sort_text = itemLayoutView.findViewById(R.id.sort_text);
                sort_image = itemLayoutView.findViewById(R.id.sort_image);
                filter_text_lay = itemLayoutView.findViewById(R.id.filter_text_lay);
            }
        }
    }

    public static class GendersAdapter extends RecyclerView.Adapter<GendersAdapter.ViewHolder> {
        private Context mCtx;
        private List<User> vender;

        public GendersAdapter(Context mCtx, List<User> vender) {
            this.mCtx = mCtx;
            this.vender = vender;
        }

        @Override
        public GendersAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gender_recycler, null);
            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(GendersAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            User venders = vender.get(position);
            if(venders!=null){
                if(!venders.getImage().isEmpty()){
                    Glide.with(mCtx).load( venders.getImage()).into( holder.venders_image );
                    holder.name.setText(venders.getGender());
                }
                holder.venders_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        vender1 = venders;
                        notifyDataSetChanged();
                    }
                });

                if(vender1.equals(venders)){
                    holder.venders_image.setBackgroundResource(R.drawable.selected_border);
                }else{
                    holder.venders_image.setBackgroundResource(R.drawable.select_border);
                }
            }
        }

        @Override
        public int getItemCount() {
            return vender==null?0:vender.size();
        }
        class ViewHolder extends RecyclerView.ViewHolder {
            private CircleImageView venders_image;
            private TextView name;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                venders_image = itemLayoutView.findViewById(R.id.venders_image);
                name = itemLayoutView.findViewById(R.id.name);
            }
        }
    }
}
