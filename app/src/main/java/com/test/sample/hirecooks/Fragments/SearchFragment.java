package com.test.sample.hirecooks.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.test.sample.hirecooks.Adapter.SearchAdapter.SearchAdapter;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.SearchSubCategory.Result;
import com.test.sample.hirecooks.Models.SearchSubCategory.Search;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.Utils.DividerItemDecoration;
import com.test.sample.hirecooks.WebApis.ProductApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.accountkit.internal.AccountKitController.getApplicationContext;

public class SearchFragment extends Fragment {
    private MaterialSearchBar searchBar;
    private RecyclerView recyclerView;
    private List<Search> search;
    private FrameLayout no_result_found;
    Toolbar toolbar;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        Bundle args = new Bundle();
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gsearchAllProducts();
    }

    private void gsearchAllProducts() {
        ProductApi mService = ApiClient.getClient().create(ProductApi.class);
        Call<Result> call = mService.searchProducts();
        call.enqueue(new Callback<Result>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    search = response.body().getSearch();
                    Constants.SEARCH = search;
                    if (search != null && search.size() != 0) {
                        no_result_found.setVisibility(View.GONE);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        SearchAdapter mAdapter = new SearchAdapter(getActivity(), search);
                        recyclerView.setAdapter(mAdapter);
                        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                        recyclerView.addItemDecoration(new DividerItemDecoration(5));
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                    } else {
                        no_result_found.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.failed_due_to + statusCode, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                System.out.println("Suree : " + t.getMessage());

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchBar = view.findViewById(R.id.searchBar);
        recyclerView = view.findViewById(R.id.subcategory_recycler);
        no_result_found = view.findViewById(R.id.no_result_found);
        toolbar = view.findViewById(R.id.toolbar);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest = new ArrayList<>();
             /*   for(String search:suggestList)
                {
                    if(search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);
                }*/
                searchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
               /* if(!enabled)
                    recyclerView.setAdapter(adapter); */// restores full list of drinks
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
        return view;
    }

    private void startSearch(CharSequence text) {
        List<Search> filterList = new ArrayList<>();
        try {
            if (search != null && search.size() != 0) {
                for (int i = 0; i < search.size(); i++) {
                    String cityName = "";

                    if (search.get(i).getName() != null) {
                        cityName = search.get(i).getName();
                    }

                    if (cityName.toLowerCase().contains(String.valueOf(text).toLowerCase())) {
                        filterList.add(search.get(i));
                    }
                }

                if (filterList.size() != 0 && filterList != null) {
                    SearchAdapter adapter = new SearchAdapter(getActivity(), filterList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "Searched Data not found", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}