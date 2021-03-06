package com.test.sample.hirecooks.Fragments.Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.test.sample.hirecooks.Activity.Cooks.CooksActivity;
import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.Adapter.Category.CategoryAdapter;
import com.test.sample.hirecooks.Adapter.Category.CircularImageCategoryAdapter;
import com.test.sample.hirecooks.Adapter.Category.NewProductCategoryAdapter;
import com.test.sample.hirecooks.Adapter.Cooks.CooksPromotionAdapter;
import com.test.sample.hirecooks.Adapter.CooksPromotion.ToolPromotionAdapter;
import com.test.sample.hirecooks.Adapter.Home.FeedProperties;
import com.test.sample.hirecooks.Adapter.Offer.OfferAdapter;
import com.test.sample.hirecooks.Adapter.Venders.VendersAdapter;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Libraries.Slider.SliderLayout;
import com.test.sample.hirecooks.Models.Category.Categories;
import com.test.sample.hirecooks.Models.Category.Category;
import com.test.sample.hirecooks.Models.CooksPromotion.CooksPromotion;
import com.test.sample.hirecooks.Models.MapLocationResponse.Example;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.NewProductsCategory.NewProductCategories;
import com.test.sample.hirecooks.Models.NewProductsCategory.NewProductCategory;
import com.test.sample.hirecooks.Models.Offer.Offer;
import com.test.sample.hirecooks.Models.OfferCategory.OffersCategories;
import com.test.sample.hirecooks.Models.OfferCategory.OffersCategory;
import com.test.sample.hirecooks.Models.UsersResponse.UserResponse;
import com.test.sample.hirecooks.Models.UsersResponse.UsersResponse;
import com.test.sample.hirecooks.Models.users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.NetworkUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.MapApi;
import com.test.sample.hirecooks.WebApis.ProductApi;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment{
    private Context context;
    private SliderLayout mDemoSlider;
    private ScrollView scrollView;
    private RecyclerView offer_recycler_view,recyclerView,recyclerView2,my_recycler_view3,venders_recycler_view,cooks_promotion_recycler,tool_Pager;
    private ArrayList<FeedProperties> os_versions;
    private AutoCompleteTextView autoComplete;
    private CategoryAdapter mAdapter;
    private LinearLayout searchResults;
    ArrayAdapter<String> stringAdapter;
    private UserApi mService;
    private List<Category> categories;
    MainActivity mainActivity;
    private TextView cook_seeall;
    private float mToolbarBarHeight;
    CoordinatorLayout.LayoutParams layoutParams;
    private List<Map> maps;
    private User user = SharedPrefManager.getInstance(getContext()).getUser();
    private List<UserResponse> vendersList;
    private BaseActivity baseActivity;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        context = container.getContext();
        initViews(view);
        if(NetworkUtil.checkInternetConnection(mainActivity)) {
            getMapDetails();
            getCategory();
            getOfferCategory();
            getNewProductCategory();
            getOffer();
            getCooks();
        }
        else {
            baseActivity.showAlert(getResources().getString(R.string.no_internet));
        }

        return view;
    }

    @SuppressLint("WrongConstant")
    private void getOffer() {
        List<Offer> offers = new ArrayList<>();
        offers.add(new Offer(0,"Grocery", "https://cmkt-image-prd.freetls.fastly.net/0.1.0/ps/4021761/910/607/m2/fpnw/wm1/zpope64kaakcxgygqinp9cz1axdtaj45we4zeeuv9uv24vuadgujqxshj5pkqrwe-.jpg?1518944844&s=03a65531af02f4353d74c0816dae35a8","https://cmkt-image-prd.freetls.fastly.net/0.1.0/ps/4021761/910/607/m2/fpnw/wm1/zpope64kaakcxgygqinp9cz1axdtaj45we4zeeuv9uv24vuadgujqxshj5pkqrwe-.jpg?1518944844&s=03a65531af02f4353d74c0816dae35a8","#ff6347"));
        offers.add(new Offer(1,"Food", "https://i.pinimg.com/originals/1f/28/13/1f28133d604d126080bda739a02847cc.jpg","https://cmkt-image-prd.freetls.fastly.net/0.1.0/ps/4021761/910/607/m2/fpnw/wm1/zpope64kaakcxgygqinp9cz1axdtaj45we4zeeuv9uv24vuadgujqxshj5pkqrwe-.jpg?1518944844&s=03a65531af02f4353d74c0816dae35a8","#FF9933"));
        offers.add(new Offer(2,"Icecream", "https://5.imimg.com/data5/KH/TW/MY-9134447/big-cone-ice-cream-500x500.jpg","https://cmkt-image-prd.freetls.fastly.net/0.1.0/ps/4021761/910/607/m2/fpnw/wm1/zpope64kaakcxgygqinp9cz1axdtaj45we4zeeuv9uv24vuadgujqxshj5pkqrwe-.jpg?1518944844&s=03a65531af02f4353d74c0816dae35a8","#ff6347"));
        offers.add(new Offer(3,"Carts", "https://img.freepik.com/free-photo/closeup-smartphone-fruits-vegetables_23-2148216120.jpg?size=626&ext=jpg","https://cmkt-image-prd.freetls.fastly.net/0.1.0/ps/4021761/910/607/m2/fpnw/wm1/zpope64kaakcxgygqinp9cz1axdtaj45we4zeeuv9uv24vuadgujqxshj5pkqrwe-.jpg?1518944844&s=03a65531af02f4353d74c0816dae35a8","#FF9933"));
        offers.add(new Offer(4,"Vegitable", "https://www.vegetables.co.nz/assets/Uploads/vegetables.jpg","https://cmkt-image-prd.freetls.fastly.net/0.1.0/ps/4021761/910/607/m2/fpnw/wm1/zpope64kaakcxgygqinp9cz1axdtaj45we4zeeuv9uv24vuadgujqxshj5pkqrwe-.jpg?1518944844&s=03a65531af02f4353d74c0816dae35a8","#ff6347"));
        offers.add(new Offer(5,"Fast Food", "https://i2.wp.com/www.eatthis.com/wp-content/uploads/2018/05/mcdonalds-burger-fries-soda.jpg?fit=1024%2C750&ssl=1","https://cmkt-image-prd.freetls.fastly.net/0.1.0/ps/4021761/910/607/m2/fpnw/wm1/zpope64kaakcxgygqinp9cz1axdtaj45we4zeeuv9uv24vuadgujqxshj5pkqrwe-.jpg?1518944844&s=03a65531af02f4353d74c0816dae35a8","#ff6347"));
        offers.add(new Offer(6,"Milk", "https://cdn1.sph.harvard.edu/wp-content/uploads/sites/30/2012/09/calcium_and_milk-300x194.jpg","https://cmkt-image-prd.freetls.fastly.net/0.1.0/ps/4021761/910/607/m2/fpnw/wm1/zpope64kaakcxgygqinp9cz1axdtaj45we4zeeuv9uv24vuadgujqxshj5pkqrwe-.jpg?1518944844&s=03a65531af02f4353d74c0816dae35a8","#ff6347"));
        offers.add(new Offer(7,"Fruits", "https://i.ytimg.com/vi/4gi05GOe4Ew/maxresdefault.jpg","https://cmkt-image-prd.freetls.fastly.net/0.1.0/ps/4021761/910/607/m2/fpnw/wm1/zpope64kaakcxgygqinp9cz1axdtaj45we4zeeuv9uv24vuadgujqxshj5pkqrwe-.jpg?1518944844&s=03a65531af02f4353d74c0816dae35a8","#ff6347"));

        getCategoryOffer(offers);
    }

    @SuppressLint("WrongConstant")
    private void getCooks() {
        List<CooksPromotion> cooksPromotion = new ArrayList<>();
        cooksPromotion.add(new CooksPromotion(0,"Robert Disuja","North Tadka", "https://cdn.cdnparenting.com/articles/2018/12/12122639/1206944158-H-1024x700.jpg","https://image.flaticon.com/icons/svg/196/196907.svg","#ff6347"));
        cooksPromotion.add(new CooksPromotion(1,"Robert Disuja","South Dish", "https://www.thespruceeats.com/thmb/6Cofsx3edLVIJ76F6EDwYBqNs_k=/3752x2110/smart/filters:no_upscale()/south-indian-food-548291937-588b4fcd5f9b5874ee174914.jpg","https://image.flaticon.com/icons/svg/196/196907.svg","#FF9933"));
        cooksPromotion.add(new CooksPromotion(2,"Robert Disuja","Maharashtrian Tadka", "https://3.bp.blogspot.com/-ntTxWOO7Kns/VxVUOcL4O0I/AAAAAAAADZc/7hfjhMherIYrMxQQ1snTg57_CF1Y96r6QCLcB/s1600/DSC_0189_Fotor%2Bnew2.jpg","https://image.flaticon.com/icons/svg/196/196907.svg","#FF9933"));
        cooksPromotion.add(new CooksPromotion(3,"Robert Disuja","Vegitables", "https://www.vegetables.co.nz/assets/Uploads/vegetables.jpg","https://image.flaticon.com/icons/svg/196/196907.svg","#ff6347"));
        cooksPromotion.add(new CooksPromotion(5,"Robert Disuja","Gujrti Tadka", "https://zaykakatadka.files.wordpress.com/2014/11/dhokla2.jpg","https://image.flaticon.com/icons/svg/196/196907.svg","#ff6347"));

        getCategoryCooks(cooksPromotion);
    }

    @SuppressLint("WrongConstant")
    private void getCategoryCooks(List<CooksPromotion> cooksPromotion) {
        CooksPromotionAdapter mAdapter = new CooksPromotionAdapter( getActivity(),cooksPromotion);
        cooks_promotion_recycler.setAdapter ( mAdapter );

        LinearLayoutManager tlinearLayoutManager = new LinearLayoutManager(mainActivity);
        if (mainActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            tlinearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            tlinearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
        }
        cooks_promotion_recycler.setLayoutManager(tlinearLayoutManager);
        cooks_promotion_recycler.setItemAnimator(new DefaultItemAnimator());
    }

    @SuppressLint("WrongConstant")
    private void getCategoryOffer(List<Offer> offers){
        if(offers.size()!=0&&offers!=null){
        CircularImageCategoryAdapter adapter = new CircularImageCategoryAdapter(mainActivity,offers);
        recyclerView2.setAdapter(adapter);

        ToolPromotionAdapter Adapter = new ToolPromotionAdapter( getActivity(),offers);
        tool_Pager.setAdapter ( Adapter );


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainActivity);
        if (mainActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
        }
            recyclerView2.setLayoutManager(linearLayoutManager);
            recyclerView2.setItemAnimator(new DefaultItemAnimator());

            LinearLayoutManager mlinearLayoutManager = new LinearLayoutManager(mainActivity);
            if (mainActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                mlinearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
            } else {
                mlinearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
            }
            tool_Pager.setLayoutManager(mlinearLayoutManager);
            tool_Pager.setItemAnimator(new DefaultItemAnimator());
        }
    }

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void getMapDetails() {
        MapApi mService = ApiClient.getClient().create(MapApi.class);
        Call<com.test.sample.hirecooks.Models.MapLocationResponse.Result> call = mService.getMapDetails(user.getId());
        call.enqueue(new Callback<com.test.sample.hirecooks.Models.MapLocationResponse.Result>() {
            @Override
            public void onResponse(Call<com.test.sample.hirecooks.Models.MapLocationResponse.Result> call, Response<com.test.sample.hirecooks.Models.MapLocationResponse.Result> response) {
                if (response.code() == 200 && response.body() != null && response.body().getMaps() != null) {
                    try{
                        getNearByVenders(response.body().getMaps());
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                } else {
                 //   baseActivity.ShowToast(getResources().getString(R.string.failed_due_to)+response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<com.test.sample.hirecooks.Models.MapLocationResponse.Result> call, Throwable t) {
            }
        });
    }

    public void getNearByVenders(final Map map) {
        MapApi mapApi = ApiClient.getClient().create(MapApi.class);
        Call<Example> call = mapApi.getNearByUsers(map.getLatitude(),map.getLongitude());
        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(@NonNull Call<Example> call, @NonNull Response<Example> response) {
                maps = new ArrayList<>();
                if(response.code() == 200 && response.body() != null) {
                    try{
                        maps = response.body().getMaps();
                        Constants.NEARBY_COOKS = response.body().getMaps();
                        Constants.NEARBY_VENDERS_LOCATION = response.body().getMaps();
                        getVenders();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else {
                    baseActivity.ShowToast(getResources().getString(R.string.failed_due_to)+response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Example> call, @NonNull Throwable t) {
            }
        });
    }

    private void getVenders() {
        UserApi service = ApiClient.getClient().create(UserApi.class);
        Call<UsersResponse> call = service.getUsers();
        call.enqueue(new Callback<UsersResponse>() {
            @SuppressLint({"ShowToast", "WrongConstant"})
            @Override
            public void onResponse(@NonNull Call<UsersResponse> call, @NonNull Response<UsersResponse> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    vendersList = new ArrayList<>();
                    List<UserResponse> list = new ArrayList<>();
                    List<UserResponse> filteredList = new ArrayList<>();
                    if (response.body() != null) {
                        list = response.body().getUsersResponses();
                        for (UserResponse usersResponse : list) {
                            if(usersResponse.getId()==user.getId()&&usersResponse.getEmail().equalsIgnoreCase(user.getEmail())){
                                Constants.USER_PROFILE = usersResponse.getImage();
                            }
                            for (Map map : Constants.NEARBY_VENDERS_LOCATION) {
                                if (map.getFirm_id().equalsIgnoreCase(usersResponse.getFirmId())) {
                                    if (!usersResponse.getUserType().equalsIgnoreCase("User")&&!usersResponse.getFirmId().equalsIgnoreCase("Not_Available")) {
                                        vendersList.add(usersResponse);
                                        Set<UserResponse> newList = new LinkedHashSet<>(vendersList);
                                        filteredList = new ArrayList<>(newList);
                                        Constants.NEARBY_VENDERS = filteredList;
                                    }
                                }
                            }
                        }
                    }
                    if(filteredList!=null&&filteredList.size()!=0){
                        VendersAdapter adapter = new VendersAdapter(mainActivity,filteredList);
                        venders_recycler_view.setAdapter(adapter);
                        venders_recycler_view.setVisibility(View.VISIBLE);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainActivity);
                        if (mainActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
                        }else{
                            linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                        params.setMargins(20, 20, 20, 20);
                        linearLayoutManager.canScrollHorizontally();

                        venders_recycler_view.setLayoutManager(linearLayoutManager);
                        venders_recycler_view.setItemAnimator(new DefaultItemAnimator());
                    }else{
                        venders_recycler_view.setVisibility(View.GONE);
                    }
                }else{
                    baseActivity.ShowToast(getResources().getString(R.string.failed_due_to)+statusCode);
                }
            }

            @SuppressLint("ShowToast")
            @Override
            public void onFailure(@NonNull Call<UsersResponse> call, @NonNull Throwable t) {
            }
        });
    }

    private void getOfferCategory() {
       ProductApi mService = ApiClient.getClient().create(ProductApi.class);
        Call<OffersCategories> call = mService.getOffersCategory();
        call.enqueue(new Callback<OffersCategories>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<OffersCategories> call, Response<OffersCategories> response) {
                int statusCode = response.code();
                if(statusCode==200){
                  List<OffersCategory> categories = response.body().getOffersCategory();
                    OfferAdapter adapter = new OfferAdapter(mainActivity,categories);
                    offer_recycler_view.setAdapter(adapter);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainActivity);
                    if (mainActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
                    }else{
                        linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
                    }
                    RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                    params.setMargins(20, 20, 20, 20);
                    linearLayoutManager.canScrollHorizontally();

                    offer_recycler_view.setLayoutManager(linearLayoutManager);
                    offer_recycler_view.setItemAnimator(new DefaultItemAnimator());

                }
                else{
                    baseActivity.ShowToast(getResources().getString(R.string.failed_due_to)+statusCode);
                }
            }

            @Override
            public void onFailure(Call<OffersCategories> call, Throwable t) {

            }
        });
    }

    private void getNewProductCategory() {
       ProductApi mService = ApiClient.getClient().create(ProductApi.class);
        Call<NewProductCategories> call = mService.getNewProductCategory();
        call.enqueue(new Callback<NewProductCategories>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<NewProductCategories> call, Response<NewProductCategories> response) {
                int statusCode = response.code();
                if(statusCode==200){
                  List<NewProductCategory> categories = response.body().getNewProductCategory();
                    my_recycler_view3.setHasFixedSize(true);
                    my_recycler_view3.setLayoutManager(new LinearLayoutManager(mainActivity));
                    NewProductCategoryAdapter adapter = new NewProductCategoryAdapter(mainActivity,categories);
                    my_recycler_view3.setAdapter(adapter);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainActivity);
                    if (mainActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
                    }else {
                        linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
                    }
                    my_recycler_view3.setLayoutManager(linearLayoutManager);
                    my_recycler_view3.setItemAnimator(new DefaultItemAnimator());

                }
                else{
                    baseActivity.ShowToast(getResources().getString(R.string.failed_due_to)+statusCode);
                }
            }

            @Override
            public void onFailure(Call<NewProductCategories> call, Throwable t) {

            }
        });
    }

    private void getCategory() {
        mService = ApiClient.getClient().create(UserApi.class);
        Call<Categories> call = mService.getCategory();
        call.enqueue(new Callback<Categories>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<Categories> call, Response<Categories> response) {
                int statusCode = response.code();
                if(statusCode==200){
                    categories = response.body().getCategory();
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
                    mAdapter = new CategoryAdapter(mainActivity,categories);
                    recyclerView.setAdapter(mAdapter);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainActivity);
                    if (mainActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
                    } else {
                        linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
                    }
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());

                }
                else{
                    baseActivity.ShowToast(getResources().getString(R.string.failed_due_to)+response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<Categories> call, Throwable t) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"WrongConstant", "ClickableViewAccessibility"})
    private void initViews(View view) {
        Fresco.initialize(mainActivity);
        scrollView = view.findViewById(R.id.scrollView);
        baseActivity = new BaseActivity();
        mDemoSlider = view.findViewById(R.id.slider);
        cook_seeall = view.findViewById(R.id.cook_seeall);
        cooks_promotion_recycler = view.findViewById(R.id.cooks_promotion_recycler);
        tool_Pager = view.findViewById(R.id.tool_pager);
        offer_recycler_view = view.findViewById(R.id.offer_recycler_view);
        recyclerView = view.findViewById(R.id.my_recycler_view);
        recyclerView2 = view.findViewById(R.id.my_recycler_view2);
        my_recycler_view3 = view.findViewById(R.id.my_recycler_view3);
        venders_recycler_view = view.findViewById(R.id.venders_recycler_view);

        String[] colors = getResources().getStringArray(R.array.colorList);
        stringAdapter = new ArrayAdapter<>(mainActivity, R.layout.row, colors);

        HashMap<String, Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("Groceroies",R.drawable.slide1);
        file_maps.put("Masala",R.drawable.slide2);
        file_maps.put("Vegitables",R.drawable.slide3);
        file_maps.put("Householdes", R.drawable.slide4);

        for(String name : file_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(mainActivity);
            textSliderView
                    //.description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    //.setOnSliderClickListener((BaseSliderView.OnSliderClickListener) mainActivity);
            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    //Toast.makeText(mainActivity,"clicked: "+textSliderView,Toast.LENGTH_LONG).show();
                }
            });

            textSliderView.bundle(new Bundle());
            mDemoSlider.addSlider(textSliderView);
        }

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);


        NestedScrollView nested_content = (NestedScrollView) view.findViewById(R.id.nested_scroll_view);
        nested_content.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < oldScrollY) { // up
                    animateNavigation(false);
                    animateToolBar(false);
                }
                if (scrollY > oldScrollY) { // down
                    animateNavigation(true);
                    animateToolBar(true);
                }
            }
        });

        cook_seeall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CooksActivity.class));
            }
        });
    }


    boolean isNavigationHide = false;

    private void animateNavigation(final boolean hide) {
        if (isNavigationHide && hide || !isNavigationHide && !hide) return;
        isNavigationHide = hide;
        int moveY = hide ? (2 * mainActivity.mNavigationView.getHeight()) : 0;
        mainActivity.mNavigationView.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    boolean isToolBarHide = false;

    private void animateToolBar(final boolean hide) {
        if (isToolBarHide && hide || !isToolBarHide && !hide) return;
        isToolBarHide = hide;
        int moveY = hide ? -(2 * mainActivity.toolbar_layout.getHeight()) : 0;
        mainActivity.toolbar_layout.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }

 /*   @Override
    public void onResume() {
        super.onResume();
        getMapDetails();
        getCategory();
        getOfferCategory();
        getNewProductCategory();
        getOffer();
    }*/

    @Override
    public void onStop() {
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }
}
