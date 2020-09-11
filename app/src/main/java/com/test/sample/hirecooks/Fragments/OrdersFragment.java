package com.test.sample.hirecooks.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.Adapter.Orders.OrdersAdapter;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.Order.Order;
import com.test.sample.hirecooks.Models.Order.Results;
import com.test.sample.hirecooks.Models.users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.OrderApi;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersFragment extends Fragment {
    private RecyclerView recyclerView;
    private OrderApi mService;
    private User user;
    private List<Order> ordersList,filteredList;
    private OrdersAdapter ordersAdapter;
    private LinearLayout no_orders;
    private Context context;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    MainActivity mainActivity;

    public OrdersFragment() {
        // Required empty public constructor
    }

    public static OrdersFragment newInstance() {
        Bundle args = new Bundle();
        OrdersFragment fragment = new OrdersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        context = container.getContext();
        user = SharedPrefManager.getInstance(getActivity()).getUser();
        recyclerView = view.findViewById(R.id.orders_recycler);
        no_orders = view.findViewById(R.id.no_orders);


        NestedScrollView nested_content = (NestedScrollView) view.findViewById(R.id.nested_scroll_view);
        nested_content.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < oldScrollY) { // up
                    animateNavigation(false);
                   // animateToolBar(false);
                }
                if (scrollY > oldScrollY) { // down
                    animateNavigation(true);
                   // animateToolBar(true);
                }
            }
        });

        mSwipeRefreshLayout = view.findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        getOrders();
                    }
                }, 3000);
            }
        });

        getOrders();
        return view;
    }



    boolean isNavigationHide = false;

    private void animateNavigation(final boolean hide) {
        if (isNavigationHide && hide || !isNavigationHide && !hide) return;
        isNavigationHide = hide;
        int moveY = hide ? (2 * mainActivity.mNavigationView.getHeight()) : 0;
        mainActivity.mNavigationView.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }


    private void getOrders() {
        mService = ApiClient.getClient().create(OrderApi.class);
        Call<Results> call = mService.getOrderByUserId(user.getId());
        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {
                if (response.code() == 200 ) {
                    List<Order> orders = response.body().getOrder();
                    ordersList = new ArrayList<>();
                    filteredList = new ArrayList<>();
                    if(orders!=null&&orders.size()!=0){
                        for(Order order:orders){
                            if(order!=null&&order.getUserId()==(user.getId())){
                                ordersList.add(order);
                                Set<Order> newList = new LinkedHashSet<>(ordersList);
                                filteredList = new ArrayList<>(newList);
                            }
                        }
                    }
                    if(filteredList!=null&&filteredList.size()!=0){
                        recyclerView.setVisibility(View.VISIBLE);
                        no_orders.setVisibility(View.GONE);
                        ordersAdapter = new OrdersAdapter(getActivity(),filteredList);
                        recyclerView.setAdapter(ordersAdapter);
                    }else{
                        recyclerView.setVisibility(View.GONE);
                        no_orders.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<Results> call, Throwable t) {
                System.out.println("Suree :"+t.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        getOrders();
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        getOrders();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }
}
