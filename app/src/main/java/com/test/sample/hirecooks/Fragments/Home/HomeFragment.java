package com.test.sample.hirecooks.Fragments.Home;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.test.sample.hirecooks.Activity.Cooks.CooksActivity;
import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.Adapter.Category.CategoryAdapter;
import com.test.sample.hirecooks.Adapter.Category.CircularImageCategoryAdapter;
import com.test.sample.hirecooks.Adapter.Cooks.CooksPromotionAdapter;
import com.test.sample.hirecooks.Adapter.CooksPromotion.ToolPromotionAdapter;
import com.test.sample.hirecooks.Adapter.Offer.OfferAdapter;
import com.test.sample.hirecooks.Adapter.Venders.VendersAdapter;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Libraries.Slider.SliderLayout;
import com.test.sample.hirecooks.Models.Category.Category;
import com.test.sample.hirecooks.Models.Category.CategoryResponse;
import com.test.sample.hirecooks.Models.MapLocationResponse.Example;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Services.GPSReciever.MyReceiver;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.NetworkUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.ViewModel.HomeFragViewModel;
import com.test.sample.hirecooks.WebApis.MapApi;
import com.test.sample.hirecooks.WebApis.UserApi;
import com.test.sample.hirecooks.databinding.FragmentHomeBinding;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private CategoryAdapter mAdapter;
    MainActivity mainActivity;
    private User user = SharedPrefManager.getInstance(getContext()).getUser();
    private List<User> vendersList;
    private BaseActivity baseActivity;
    List<Category> mCategory = new ArrayList<>();
    List<Category> mOfferCategory  = new ArrayList<>();
    List<Category> mNewProductsCat  = new ArrayList<>();
    private HomeFragViewModel viewModel;
    private FragmentHomeBinding binding;
    private BroadcastReceiver myReceiver = null;
    private boolean isConnected;
    private MyReceiver MyReceiver;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myReceiver = new MyReceiver();
        broadcastIntent();
    }

    private void broadcastIntent() {
        mainActivity.registerReceiver(myReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint({"WrongConstant", "NewApi"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        baseActivity = new BaseActivity();
        initViews();
        if(NetworkUtil.checkInternetConnection(mainActivity)) {
            binding.homeLayout.setVisibility( View.VISIBLE );
            binding.noInternetConnectionLayout.setVisibility( View.GONE );
            getMapDetails();
            callViews();
        }
        else {
            binding.homeLayout.setVisibility( View.GONE );
            binding.noInternetConnectionLayout.setVisibility( View.VISIBLE );
        }
    }

    private void callViews() {
        viewModel=new ViewModelProvider(mainActivity).get(HomeFragViewModel.class);
        viewModel.getCategoryResponse().observe(mainActivity, new Observer<List<CategoryResponse>>() {
            @SuppressLint("NewApi")
            @Override
            public void onChanged(List<CategoryResponse> categoryResponses) {
                categoryResponses.forEach(category ->{
                    for(Category categories:category.getCategory()){
                        if(categories.getCategoryid()==1){
                            mOfferCategory.add( categories );
                            Set<Category> list = new LinkedHashSet<>(mOfferCategory);
                            List<Category> offers = new ArrayList<>(list);
                            OfferAdapter mAdapter = new OfferAdapter(mainActivity);
                            binding.offerRecyclerView.setAdapter(mAdapter);
                            mAdapter.setCategory(offers);

                        }else if(categories.getCategoryid()==2){
                            mCategory.add( categories );
                            Set<Category> list = new LinkedHashSet<>(mCategory);
                            List<Category> categoryList = new ArrayList<>(list);
                            mAdapter = new CategoryAdapter(mainActivity);
                            binding.myRecyclerView.setAdapter(mAdapter);
                            mAdapter.setCategory(categoryList);

                        }else if(categories.getCategoryid()==3){
                            mNewProductsCat.add( categories );
                            Set<Category> list = new LinkedHashSet<>(mNewProductsCat);
                            List<Category> newProduct = new ArrayList<>(list);
                            mAdapter = new CategoryAdapter(mainActivity);
                            binding.myRecyclerView3.setAdapter(mAdapter);
                            mAdapter.setCategory(newProduct);
                        }
                    }
                });
            }
        });

        viewModel.getCategoryOffer().observe(mainActivity,offers -> {
            CircularImageCategoryAdapter adapter = new CircularImageCategoryAdapter(mainActivity);
            binding.myRecyclerView2.setAdapter(adapter);
            adapter.setCategoryOffers(offers);

            ToolPromotionAdapter Adapter = new ToolPromotionAdapter(mainActivity);
            binding.toolPager.setAdapter ( Adapter );
            Adapter.setPromotion(offers);
        });

        viewModel.getCooksPromotion().observe(mainActivity,promotions -> {
            CooksPromotionAdapter mAdapter = new CooksPromotionAdapter( mainActivity);
            binding.cooksPromotionRecycler.setAdapter ( mAdapter );
            mAdapter.setCategory(promotions);
        });
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
                if(response.code() == 200 && response.body() != null) {
                    try{
                        Constants.NEARBY_COOKS = response.body().getMaps();
                        for(Map maps:response.body().getMaps()){
                            if(!maps.getFirm_id().equalsIgnoreCase( "Not_Available" )&&map.getFirm_id()!=null){
                                Constants.NEARBY_VENDERS_LOCATION = response.body().getMaps();
                            }
                        }
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
        Call<List<com.test.sample.hirecooks.Models.Users.Example>> call = service.getUsers();
        call.enqueue(new Callback<List<com.test.sample.hirecooks.Models.Users.Example>>() {
            @SuppressLint({"ShowToast", "WrongConstant"})
            @Override
            public void onResponse(@NonNull Call<List<com.test.sample.hirecooks.Models.Users.Example>> call, @NonNull Response<List<com.test.sample.hirecooks.Models.Users.Example>> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    vendersList = new ArrayList<>();
                    List<User> filteredList = new ArrayList<>();
                    for (com.test.sample.hirecooks.Models.Users.Example example: response.body()) {
                        if(!example.getError()){
                            for (User users : example.getUsers()) {
                                for (Map map : Constants.NEARBY_VENDERS_LOCATION) {
                                    if (map.getFirm_id().equalsIgnoreCase( users.getFirmId() )) {
                                        if(users.getUserType().equalsIgnoreCase( "Admin" )||users.getUserType().equalsIgnoreCase( "Manager" )){
                                            vendersList.add(users);
                                            Constants.USER_PROFILE = users.getImage();
                                            Set<User> newList = new LinkedHashSet<>(vendersList);
                                            filteredList = new ArrayList<>(newList);
                                            Constants.NEARBY_VENDERS = filteredList;
                                            System.out.println("Suree : " +users.getName());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(filteredList!=null&&filteredList.size()!=0){
                        VendersAdapter adapter = new VendersAdapter(mainActivity,filteredList);
                        binding.vendersRecyclerView.setAdapter(adapter);
                        binding.vendersRecyclerView.setVisibility(View.VISIBLE);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mainActivity);
                        if (mainActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
                        }else{
                            linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
                        }
                        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                        params.setMargins(20, 20, 20, 20);
                        linearLayoutManager.canScrollHorizontally();

                        binding.vendersRecyclerView.setLayoutManager(linearLayoutManager);
                        binding.vendersRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    }else{
                        binding.vendersRecyclerView.setVisibility(View.GONE);
                    }
                }else{
                    baseActivity.ShowToast(getResources().getString(R.string.failed_due_to)+statusCode);
                }
            }

            @SuppressLint("ShowToast")
            @Override
            public void onFailure(@NonNull Call<List<com.test.sample.hirecooks.Models.Users.Example>> call, @NonNull Throwable t) {
                Toast.makeText(mainActivity, "Retry", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"WrongConstant", "ClickableViewAccessibility"})
    private void initViews() {
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
            binding.slider.addSlider(textSliderView);
        }

        binding.slider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        binding.slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        binding.slider.setCustomAnimation(new DescriptionAnimation());
        binding.slider.setDuration(4000);


        NestedScrollView nested_content = (NestedScrollView) binding.nestedScrollView;
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

        binding.cookSeeall.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public void onStop() {
        binding.slider.stopAutoCycle();
        super.onStop();
       // unregisterReceiver(myReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
      // mainActivity.unregisterReceiver(myReceiver);
    }
}
