package com.test.sample.hirecooks.ViewModel.Repositories;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import com.test.sample.hirecooks.Models.SubCategory.SubcategoryResponse;
import com.test.sample.hirecooks.WebApis.ProductApi;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemDataSource extends PageKeyedDataSource<Integer, Subcategory> {
    public static ProductApi mService;
    //the size of a page that we want
    public static final int PAGE_SIZE = 10;
    //we will start from the first page which is 1
    private static final int FIRST_PAGE = 1;
    //we need to fetch from stackoverflow
    private static final String SITE_NAME = "stackoverflow";
    //this will be called once to load the initial data
    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, Subcategory> callback) {
                mService = ApiClient.getClient().create(ProductApi.class);
                mService.getNearBySubCategoryByPaging(2,FIRST_PAGE, PAGE_SIZE)
                .enqueue(new Callback<List<SubcategoryResponse>>() {
                    @Override
                    public void onResponse(Call<List<SubcategoryResponse>> call, Response<List<SubcategoryResponse>> response) {
                        if (response.body() != null) {
                            for(SubcategoryResponse example:response.body()) {
                                if (!example.getError()) {

                                    callback.onResult(example.getSubcategory(), null, FIRST_PAGE + 1);
                                }
                            }
                        }
                    }
 
                    @Override
                    public void onFailure(Call<List<SubcategoryResponse>> call, Throwable t) {
                        
                    }
                });
    }
 
    //this will load the previous page
    @Override
    public void loadBefore(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, Subcategory> callback) {
                mService = ApiClient.getClient().create(ProductApi.class);
                mService.getNearBySubCategoryByPaging(2,params.key, PAGE_SIZE)
                .enqueue(new Callback<List<SubcategoryResponse>>() {
                    @Override
                    public void onResponse(Call<List<SubcategoryResponse>> call, Response<List<SubcategoryResponse>> response) {
                        //if the current page is greater than one
                        //we are decrementing the page number
                        //else there is no previous page
                        Integer adjacentKey = (params.key > 1) ? params.key - 1 : null;
                        if (response.body() != null) {
                            for(SubcategoryResponse example:response.body()) {
                                if (!example.getError()) {

                                    //passing the loaded data
                                    //and the previous page key
                                    callback.onResult(example.getSubcategory(), adjacentKey);
                                }
                            }
                        }
                    }
 
                    @Override
                    public void onFailure(Call<List<SubcategoryResponse>> call, Throwable t) {
                        
                    }
                });
    }

    //this will load the next page
    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, Subcategory> callback) {
                mService = ApiClient.getClient().create(ProductApi.class);
                mService.getNearBySubCategoryByPaging(2,params.key, PAGE_SIZE)
                .enqueue(new Callback<List<SubcategoryResponse>>() {
                    @Override
                    public void onResponse(Call<List<SubcategoryResponse>> call, Response<List<SubcategoryResponse>> response) {
                        
                        if (response.body() != null) {
                            for(SubcategoryResponse example:response.body()){
                                if(!example.getError()){
                                    //if the response has next page
                                    //incrementing the next page number
                                    Integer key = example.has_more ? params.key + 1 : null;

                                    //passing the loaded data and next page value
                                    callback.onResult(example.getSubcategory(), key);
                                }

                            }
                        }
                    }
 
                    @Override
                    public void onFailure(Call<List<SubcategoryResponse>> call, Throwable t) {
                        
                    }
                });
    }
}
 