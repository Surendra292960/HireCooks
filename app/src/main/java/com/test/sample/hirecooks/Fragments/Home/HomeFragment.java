package com.test.sample.hirecooks.Fragments.Home;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.Adapter.Menus.MenuAdapter;
import com.test.sample.hirecooks.Adapter.SubCategory.SubcategoryAdapter;
import com.test.sample.hirecooks.Libraries.Slider.SliderLayout;
import com.test.sample.hirecooks.Models.Chat.ListObject;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Services.GPSReciever.MyReceiver;
import com.test.sample.hirecooks.Utils.NetworkUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.ViewModel.ViewModel;
import com.test.sample.hirecooks.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {
    MainActivity  mainActivity;
    private  User user;
    List<Subcategory> filteredList;
    private  ViewModel viewModel;
    private  FragmentHomeBinding binding;
    private BroadcastReceiver myReceiver = null;

    public HomeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("NewApi")
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
        user = SharedPrefManager.getInstance(getContext()).getUser();
        if(NetworkUtil.checkInternetConnection(mainActivity)) {
            binding.homeLayout.setVisibility( View.VISIBLE );
            binding.noInternetConnectionLayout.setVisibility( View.GONE );
            initViews();
            getBanner();

        }
        else {
            binding.homeLayout.setVisibility( View.GONE );
            binding.noInternetConnectionLayout.setVisibility( View.VISIBLE );
        }
    }

    private void getBanner() {
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
    }

    @SuppressLint("NewApi")
    public  void initViews() {
        viewModel=new ViewModelProvider(mainActivity).get(ViewModel.class);
        viewModel.getMenue().observe(mainActivity, menue -> {
            if(menue!=null && menue.size()!=0){
                MenuAdapter mAdapter = new MenuAdapter(mainActivity);
                binding.menueRecyclerView.setAdapter(mAdapter);
                mAdapter.setMenue(menue);
            }
        });
        viewModel.getNearBySubCategory(user.getId()).observe(mainActivity, subcategoryResponses -> subcategoryResponses.forEach(subcategory ->{
            if(!subcategory.getError()&&subcategory.getSubcategory()!=null&&subcategory.getSubcategory().size()!=0){
                GridLayoutManager gridLayoutManager = new GridLayoutManager(mainActivity,2);
                SubcategoryAdapter mAdapter = new SubcategoryAdapter( mainActivity, subcategory.getSubcategory(), ListObject.TYPE_CARDVIEW_GRID);
                binding.subcategoryRecycler.setLayoutManager(gridLayoutManager);
                binding.subcategoryRecycler.setAdapter(mAdapter);
            }
        }));

        NestedScrollView nested_content = binding.nestedScrollView;
        nested_content.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY < oldScrollY) { // up
                bottomNavigationView(false);
                animateToolBar(false);
            }
            if (scrollY > oldScrollY) { // down
                bottomNavigationView(true);
                animateToolBar(true);
            }
        });
    }

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

     boolean isNavigationHide = false;

    private  void bottomNavigationView(final boolean hide) {
        if (isNavigationHide && hide || !isNavigationHide && !hide) return;
        isNavigationHide = hide;
        int moveY = hide ? (2 * mainActivity.bottomNavigationView.getHeight()) : 0;
        mainActivity.bottomNavigationView.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

     boolean isToolBarHide = false;

    private  void animateToolBar(final boolean hide) {
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
