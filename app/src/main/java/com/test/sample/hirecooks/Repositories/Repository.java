package com.test.sample.hirecooks.Repositories;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.res.Configuration;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.test.sample.hirecooks.Adapter.Venders.VendersAdapter;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.Category.CategoryResponse;
import com.test.sample.hirecooks.Models.CooksPromotion.CooksPromotion;
import com.test.sample.hirecooks.Models.MapLocationResponse.Example;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.Offer.Offer;
import com.test.sample.hirecooks.Models.SubCategory.SubcategoryResponse;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.Constants;
import com.test.sample.hirecooks.WebApis.MapApi;
import com.test.sample.hirecooks.WebApis.ProductApi;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {
    private static final String TAG = Repository.class.getSimpleName();
    private final Application application;
    private MutableLiveData<List<CategoryResponse>> mutableCategoryLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Offer>> mutableOfferLiveData = new MutableLiveData<>();
    private MutableLiveData<List<CooksPromotion>> mutableCooksPromotionLiveData = new MutableLiveData<>();

    public Repository(Application application) {
        this.application = application;
    }

    public LiveData<SubcategoryResponse> searchAllProducts(String search_key) {
        final MutableLiveData<SubcategoryResponse> data = new MutableLiveData<>();
        ProductApi mService = ApiClient.getClient().create(ProductApi.class);
        Call<ArrayList<SubcategoryResponse>> call = mService.searchAllProducts(search_key);
        call.enqueue(new Callback<ArrayList<SubcategoryResponse>>() {
            @SuppressLint({"WrongConstant"})
            @Override
            public void onResponse(@NonNull Call<ArrayList<SubcategoryResponse>> call, @NonNull Response<ArrayList<SubcategoryResponse>> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    if(response.body()!=null&&response.body().size()!=0){
                        for(SubcategoryResponse example:response.body()){
                            if(!example.getError()){
                                if(example.getSubcategory()!=null&&example.getSubcategory().size()!=0){
                                    List<Subcategory> list = new ArrayList<>();
                                    List<Subcategory> searchData = new ArrayList<>();
                                    for (Subcategory subcategory : example.getSubcategory()) {
                                        for (Map map : Constants.NEARBY_VENDERS_LOCATION) {
                                            if (map.getFirm_id().equalsIgnoreCase(subcategory.getFirmId())) {
                                                list.add(subcategory);
                                                Set<Subcategory> newList = new LinkedHashSet<>(list);
                                                searchData = new ArrayList<>(newList);
                                            }
                                        }
                                    }
                                    if(searchData.size() != 0) {
                                        SubcategoryResponse response1 = new SubcategoryResponse();
                                        response1.setSubcategory(searchData);
                                        data.setValue(response1);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<SubcategoryResponse>> call, Throwable t) {
                System.out.println("Suree : " + t.getMessage());
            }
        });
        return data;
    }

    public MutableLiveData<List<CategoryResponse>> getCategory() {
        mutableCategoryLiveData = new MutableLiveData<>();
        UserApi mService = ApiClient.getClient().create(UserApi.class);
        Call<List<CategoryResponse>> call = mService.getCategory();
        call.enqueue(new Callback<List<CategoryResponse>>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(@NonNull Call<List<CategoryResponse>> call, @NonNull Response<List<CategoryResponse>> response) {
                int statusCode = response.code();
                if(statusCode==200){
                    for(CategoryResponse categoryResponse:response.body()){
                        if(!categoryResponse.getError()){
                            mutableCategoryLiveData.postValue(response.body());
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoryResponse>> call, @NonNull Throwable t) {

            }
        });
        return mutableCategoryLiveData;
    }

    @SuppressLint("WrongConstant")
    public MutableLiveData<List<Offer>> getCategoryOffers() {
        List<Offer> offers = new ArrayList<>();
        offers.add(new Offer(0,"Grocery", "https://cmkt-image-prd.freetls.fastly.net/0.1.0/ps/4021761/910/607/m2/fpnw/wm1/zpope64kaakcxgygqinp9cz1axdtaj45we4zeeuv9uv24vuadgujqxshj5pkqrwe-.jpg?1518944844&s=03a65531af02f4353d74c0816dae35a8","https://cmkt-image-prd.freetls.fastly.net/0.1.0/ps/4021761/910/607/m2/fpnw/wm1/zpope64kaakcxgygqinp9cz1axdtaj45we4zeeuv9uv24vuadgujqxshj5pkqrwe-.jpg?1518944844&s=03a65531af02f4353d74c0816dae35a8","#ff6347"));
        offers.add(new Offer(1,"Food", "https://i.pinimg.com/originals/1f/28/13/1f28133d604d126080bda739a02847cc.jpg","https://cmkt-image-prd.freetls.fastly.net/0.1.0/ps/4021761/910/607/m2/fpnw/wm1/zpope64kaakcxgygqinp9cz1axdtaj45we4zeeuv9uv24vuadgujqxshj5pkqrwe-.jpg?1518944844&s=03a65531af02f4353d74c0816dae35a8","#FF9933"));
        offers.add(new Offer(2,"Icecream", "https://5.imimg.com/data5/KH/TW/MY-9134447/big-cone-ice-cream-500x500.jpg","https://cmkt-image-prd.freetls.fastly.net/0.1.0/ps/4021761/910/607/m2/fpnw/wm1/zpope64kaakcxgygqinp9cz1axdtaj45we4zeeuv9uv24vuadgujqxshj5pkqrwe-.jpg?1518944844&s=03a65531af02f4353d74c0816dae35a8","#ff6347"));
        offers.add(new Offer(3,"Carts", "https://img.freepik.com/free-photo/closeup-smartphone-fruits-vegetables_23-2148216120.jpg?size=626&ext=jpg","https://cmkt-image-prd.freetls.fastly.net/0.1.0/ps/4021761/910/607/m2/fpnw/wm1/zpope64kaakcxgygqinp9cz1axdtaj45we4zeeuv9uv24vuadgujqxshj5pkqrwe-.jpg?1518944844&s=03a65531af02f4353d74c0816dae35a8","#FF9933"));
        offers.add(new Offer(4,"Vegitable", "https://www.vegetables.co.nz/assets/Uploads/vegetables.jpg","https://cmkt-image-prd.freetls.fastly.net/0.1.0/ps/4021761/910/607/m2/fpnw/wm1/zpope64kaakcxgygqinp9cz1axdtaj45we4zeeuv9uv24vuadgujqxshj5pkqrwe-.jpg?1518944844&s=03a65531af02f4353d74c0816dae35a8","#ff6347"));
        offers.add(new Offer(5,"Fast Food", "https://i2.wp.com/www.eatthis.com/wp-content/uploads/2018/05/mcdonalds-burger-fries-soda.jpg?fit=1024%2C750&ssl=1","https://cmkt-image-prd.freetls.fastly.net/0.1.0/ps/4021761/910/607/m2/fpnw/wm1/zpope64kaakcxgygqinp9cz1axdtaj45we4zeeuv9uv24vuadgujqxshj5pkqrwe-.jpg?1518944844&s=03a65531af02f4353d74c0816dae35a8","#ff6347"));
        offers.add(new Offer(6,"Milk", "https://cdn1.sph.harvard.edu/wp-content/uploads/sites/30/2012/09/calcium_and_milk-300x194.jpg","https://cmkt-image-prd.freetls.fastly.net/0.1.0/ps/4021761/910/607/m2/fpnw/wm1/zpope64kaakcxgygqinp9cz1axdtaj45we4zeeuv9uv24vuadgujqxshj5pkqrwe-.jpg?1518944844&s=03a65531af02f4353d74c0816dae35a8","#ff6347"));
        offers.add(new Offer(7,"Fruits", "https://i.ytimg.com/vi/4gi05GOe4Ew/maxresdefault.jpg","https://cmkt-image-prd.freetls.fastly.net/0.1.0/ps/4021761/910/607/m2/fpnw/wm1/zpope64kaakcxgygqinp9cz1axdtaj45we4zeeuv9uv24vuadgujqxshj5pkqrwe-.jpg?1518944844&s=03a65531af02f4353d74c0816dae35a8","#ff6347"));
        mutableOfferLiveData.setValue(offers);
        return mutableOfferLiveData;
    }



    @SuppressLint("WrongConstant")
    public MutableLiveData<List<CooksPromotion>> getCooksPromotions() {
        List<CooksPromotion> cooksPromotion = new ArrayList<>();
        cooksPromotion.add(new CooksPromotion(0,"Robert Disuja","North Tadka", "https://cdn.cdnparenting.com/articles/2018/12/12122639/1206944158-H-1024x700.jpg","https://image.flaticon.com/icons/svg/196/196907.svg","#ff6347"));
        cooksPromotion.add(new CooksPromotion(1,"Robert Disuja","South Dish", "https://www.thespruceeats.com/thmb/6Cofsx3edLVIJ76F6EDwYBqNs_k=/3752x2110/smart/filters:no_upscale()/south-indian-food-548291937-588b4fcd5f9b5874ee174914.jpg","https://image.flaticon.com/icons/svg/196/196907.svg","#FF9933"));
        cooksPromotion.add(new CooksPromotion(2,"Robert Disuja","Maharashtrian Tadka", "https://3.bp.blogspot.com/-ntTxWOO7Kns/VxVUOcL4O0I/AAAAAAAADZc/7hfjhMherIYrMxQQ1snTg57_CF1Y96r6QCLcB/s1600/DSC_0189_Fotor%2Bnew2.jpg","https://image.flaticon.com/icons/svg/196/196907.svg","#FF9933"));
        cooksPromotion.add(new CooksPromotion(3,"Robert Disuja","Vegitables", "https://www.vegetables.co.nz/assets/Uploads/vegetables.jpg","https://image.flaticon.com/icons/svg/196/196907.svg","#ff6347"));
        cooksPromotion.add(new CooksPromotion(5,"Robert Disuja","Gujrti Tadka", "https://zaykakatadka.files.wordpress.com/2014/11/dhokla2.jpg","https://image.flaticon.com/icons/svg/196/196907.svg","#ff6347"));

        mutableCooksPromotionLiveData.setValue(cooksPromotion);
        return mutableCooksPromotionLiveData;
    }

    private MutableLiveData<List<Map>> getUsersLiveLocation(User user) {
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
                }
            }

            @Override
            public void onFailure(Call<com.test.sample.hirecooks.Models.MapLocationResponse.Result> call, Throwable t) {

            }
        });
        return null;
    }

    public MutableLiveData<List<Map>> getNearByVenders(final Map map) {
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
            }

            @Override
            public void onFailure(@NonNull Call<Example> call, @NonNull Throwable t) {
            }
        });
        return null;
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
                    //vendersList = new ArrayList<>();
                    List<User> filteredList = new ArrayList<>();
                    for (com.test.sample.hirecooks.Models.Users.Example example: response.body()) {
                        if(!example.getError()){
                            for (User users : example.getUsers()) {
                                for (Map map : Constants.NEARBY_VENDERS_LOCATION) {
                                    if (map.getFirm_id().equalsIgnoreCase( users.getFirmId() )) {
                                        if(users.getUserType().equalsIgnoreCase( "Admin" )||users.getUserType().equalsIgnoreCase( "Manager" )){
                                         /*   vendersList.add(users);
                                            Constants.USER_PROFILE = users.getImage();
                                            Set<User> newList = new LinkedHashSet<>(vendersList);
                                            filteredList = new ArrayList<>(newList);
                                            Constants.NEARBY_VENDERS = filteredList;
                                            System.out.println("Suree : " +users.getName());*/
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @SuppressLint("ShowToast")
            @Override
            public void onFailure(@NonNull Call<List<com.test.sample.hirecooks.Models.Users.Example>> call, @NonNull Throwable t) {

            }
        });
    }
}