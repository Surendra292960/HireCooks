package com.test.sample.hirecooks.ViewModel.Repositories;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.firebase.database.DatabaseException;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.Category.CategoryResponse;
import com.test.sample.hirecooks.Models.CooksPromotion.CooksPromotion;
import com.test.sample.hirecooks.Models.MapLocationResponse.MapResponse;
import com.test.sample.hirecooks.Models.MapLocationResponse.Maps;
import com.test.sample.hirecooks.Models.Menue.Menue;
import com.test.sample.hirecooks.Models.Menue.MenueResponse;
import com.test.sample.hirecooks.Models.NewOrder.OrdersResponse;
import com.test.sample.hirecooks.Models.Offer.Offer;
import com.test.sample.hirecooks.Models.SubCategory.SubcategoryResponse;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import com.test.sample.hirecooks.Models.Users.UserResponse;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.Dao.Dao;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.Database.RoomDatabase;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.Common;
import com.test.sample.hirecooks.WebApis.MapApi;
import com.test.sample.hirecooks.WebApis.OrderApi;
import com.test.sample.hirecooks.WebApis.ProductApi;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository extends BaseActivity {
    private static final String TAG = Repository.class.getSimpleName();
    private final Application application;
    private final Dao dao;
    private MapApi mService = Common.getAPI();
    private MutableLiveData<List<Subcategory>> mutableWishListLiveData = new MutableLiveData<>();
    private MutableLiveData<List<SubcategoryResponse>> mutableSubCategoryLiveData = new MutableLiveData<>();
    private MutableLiveData<List<CategoryResponse>> mutableCategoryLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Offer>> mutableOfferLiveData = new MutableLiveData<>();
    private MutableLiveData<List<CooksPromotion>> mutableCooksPromotionLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Menue>> mutableMenueLiveData = new MutableLiveData<>();
    private List<Subcategory> cartlist;
    private MutableLiveData<List<OrdersResponse>> mutableOrdersDataLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Maps>> mutableMapLiveData = new MutableLiveData<>();
    private MutableLiveData<List<UserResponse>> mutableUserLiveData = new MutableLiveData<>();
    private static RoomDatabase database;

    public Repository(Application application) {
        this.application = application;
        database = RoomDatabase.getInstance(application);
        dao = database.dao();
    }

    /*
    SubCategory Data
     */
    public MutableLiveData<List<SubcategoryResponse>> searchNearByProducts(int id, String search_key) {
        mutableSubCategoryLiveData = new MutableLiveData<>();
        MapApi mService = ApiClient.getClient().create(MapApi.class);
        Call<MapResponse> call = mService.getMapDetails(id);
        call.enqueue(new Callback<MapResponse>() {
            @Override
            public void onResponse(Call<MapResponse> call, Response<MapResponse> response) {
                if (response.code() == 200 && response.body() != null && response.body().getMaps() != null) {
                    ProductApi mService = ApiClient.getClient().create(ProductApi.class);
                    Call<List<SubcategoryResponse>> calls = mService.searchNearBySubCategory(response.body().getMaps().getLatitude(), response.body().getMaps().getLongitude(), search_key);
                    calls.enqueue(new Callback<List<SubcategoryResponse>>() {
                        @SuppressLint("WrongConstant")
                        @Override
                        public void onResponse(Call<List<SubcategoryResponse>> call, Response<List<SubcategoryResponse>> response) {
                            int statusCode = response.code();
                            if (statusCode == 200) {
                                if(response.body()!=null&&response.body().size()!=0){
                                    mutableSubCategoryLiveData.setValue(response.body());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<SubcategoryResponse>> call, Throwable t) {
                            System.out.println("Suree : " + t.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<MapResponse> call, Throwable t) {

            }
        });
        return mutableSubCategoryLiveData;
    }

    public MutableLiveData<List<SubcategoryResponse>> getNearBySubCategory(int id) {
        mutableSubCategoryLiveData = new MutableLiveData<>();
        MapApi mService = ApiClient.getClient().create(MapApi.class);
        Call<MapResponse> call = mService.getMapDetails(id);
        call.enqueue(new Callback<MapResponse>() {
            @Override
            public void onResponse(Call<MapResponse> call, Response<MapResponse> response) {
                if (response.code() == 200 && response.body() != null && response.body().getMaps() != null) {
                    ProductApi mService = ApiClient.getClient().create(ProductApi.class);
                    Call<List<SubcategoryResponse>> calls = mService.getNearBySubCategory(response.body().getMaps().getLatitude(), response.body().getMaps().getLongitude());
                    calls.enqueue(new Callback<List<SubcategoryResponse>>() {
                        @SuppressLint("WrongConstant")
                        @Override
                        public void onResponse(Call<List<SubcategoryResponse>> call, Response<List<SubcategoryResponse>> response) {
                            int statusCode = response.code();
                            if (statusCode == 200) {
                                if(response.body()!=null&&response.body().size()!=0){
                                    mutableSubCategoryLiveData.setValue(response.body());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<SubcategoryResponse>> call, Throwable t) {
                            System.out.println("Suree : " + t.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<MapResponse> call, Throwable t) {

            }
        });
        return mutableSubCategoryLiveData;
    }

    public MutableLiveData<List<SubcategoryResponse>> getNearBySubCategoryBySub_id(int userId, int sub_id) {
        mutableSubCategoryLiveData = new MutableLiveData<>();
        MapApi mService = ApiClient.getClient().create(MapApi.class);
        Call<MapResponse> call = mService.getMapDetails(userId);
        call.enqueue(new Callback<MapResponse>() {
            @Override
            public void onResponse(Call<MapResponse> call, Response<MapResponse> response) {
                if (response.code() == 200 && response.body() != null && response.body().getMaps() != null) {
                    ProductApi mService = ApiClient.getClient().create(ProductApi.class);
                    Call<List<SubcategoryResponse>> calls = mService.getNearBySubCategoryBySub_id(response.body().getMaps().getLatitude(), response.body().getMaps().getLongitude(),sub_id);
                    calls.enqueue(new Callback<List<SubcategoryResponse>>() {
                        @SuppressLint("WrongConstant")
                        @Override
                        public void onResponse(Call<List<SubcategoryResponse>> call, Response<List<SubcategoryResponse>> response) {
                            int statusCode = response.code();
                            if (statusCode == 200) {
                                if(response.body()!=null&&response.body().size()!=0){
                                    mutableSubCategoryLiveData.setValue(response.body());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<SubcategoryResponse>> call, Throwable t) {
                            System.out.println("Suree : " + t.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<MapResponse> call, Throwable t) {

            }
        });
        return mutableSubCategoryLiveData;
    }

    public MutableLiveData<List<SubcategoryResponse>> getNearBySubCategoryBySub_id(int sub_id) {
        mutableSubCategoryLiveData = new MutableLiveData<>();
        ProductApi mService = ApiClient.getClient().create(ProductApi.class);
        Call<ArrayList<SubcategoryResponse>> calls = mService.getSubCategorysBySub_id(sub_id);
        calls.enqueue(new Callback<ArrayList<SubcategoryResponse>>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<ArrayList<SubcategoryResponse>> call, Response<ArrayList<SubcategoryResponse>> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    if(response.body()!=null&&response.body().size()!=0){
                        mutableSubCategoryLiveData.setValue(response.body());
                        for(SubcategoryResponse subcategoryResponse:response.body()){
                            insert(subcategoryResponse.getSubcategory());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<SubcategoryResponse>> call, Throwable t) {
                System.out.println("Suree : " + t.getMessage());
            }
        });
        return mutableSubCategoryLiveData;
    }

    public LiveData<List<Subcategory>> insertToWishList(List<Subcategory> subcategoryList) {
        mutableWishListLiveData = new MutableLiveData<>();
        mutableWishListLiveData.setValue(subcategoryList);
        insert(subcategoryList);
        return mutableWishListLiveData;
    }

    public LiveData<List<Subcategory>> getAllWishList() {
        return database.dao().getAllWishList();
    }

    public void insert(List<Subcategory> subcategoryList){
        new InsertAsynKTask(database).execute(subcategoryList);
    }

    static class InsertAsynKTask extends AsyncTask<List<Subcategory>,Void,Void> {
        private final Dao dao;

        InsertAsynKTask(RoomDatabase database){
            dao = database.dao();
        }
        @Override
        protected Void doInBackground(List<Subcategory>... lists) {
            try{
                 dao.insertToWishList(lists[0]);
            }catch (DatabaseException e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public void deleteWishListItem(final Subcategory subcategory) {
        class DeleteWishListItem extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                try{
                    database.dao().deleteWishListItem(subcategory);
                }catch (DatabaseException e){
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }
        DeleteWishListItem dt = new DeleteWishListItem();
        dt.execute();
    }

    public void deleteAllWishListItem() {
        class DeleteAllWishListItem extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                try{
                    database.dao().deleteAllWishList();
                }catch (DatabaseException e){
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }
        DeleteAllWishListItem dt = new DeleteAllWishListItem();
        dt.execute();
    }
    /*
    Category Data
     */
    public MutableLiveData<List<CategoryResponse>> getCategory(int id) {
        mutableCategoryLiveData = new MutableLiveData<>();
        UserApi mService = ApiClient.getClient().create(UserApi.class);
        Call<List<CategoryResponse>> call = mService.getCategoryByCatId(id);
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

    /*
    Menue data Calling Api`s
     */

    public MutableLiveData<List<Menue>> getMenue() {
        mutableMenueLiveData = new MutableLiveData<>();
        UserApi mService = ApiClient.getClient().create(UserApi.class);
        Call<List<MenueResponse>> call = mService.getMenue();
        call.enqueue(new Callback<List<MenueResponse>>() {
            @SuppressLint("ShowToast")
            @Override
            public void onResponse(@NonNull Call<List<MenueResponse>> call, @NonNull Response<List<MenueResponse>> response) {
                if(response.code()==200) {
                    for(MenueResponse menueResponse: Objects.requireNonNull(response.body())){
                        if(!menueResponse.getError()){
                            mutableMenueLiveData.setValue(menueResponse.getMenue());
                        }
                    }
                }
            }
            @SuppressLint("ShowToast")
            @Override
            public void onFailure(@NonNull Call<List<MenueResponse>> call, @NonNull Throwable t) {

            }
        });
        return mutableMenueLiveData;
    }

    public MutableLiveData<List<Menue>> addMenue() {
        mutableMenueLiveData = new MutableLiveData<>();
        UserApi mService = ApiClient.getClient().create(UserApi.class);
        Call<List<MenueResponse>> call = mService.addMenue();
        call.enqueue(new Callback<List<MenueResponse>>() {
            @SuppressLint("ShowToast")
            @Override
            public void onResponse(@NonNull Call<List<MenueResponse>> call, @NonNull Response<List<MenueResponse>> response) {
                if(response.code()==200) {
                    for(MenueResponse menueResponse: Objects.requireNonNull(response.body())){
                        if(!menueResponse.getError()){
                            mutableMenueLiveData.setValue(menueResponse.getMenue());
                        }
                    }
                }
            }
            @SuppressLint("ShowToast")
            @Override
            public void onFailure(@NonNull Call<List<MenueResponse>> call, @NonNull Throwable t) {

            }
        });
        return mutableMenueLiveData;
    }

    public MutableLiveData<List<Menue>> updateMenus() {
        mutableMenueLiveData = new MutableLiveData<>();
        UserApi mService = ApiClient.getClient().create(UserApi.class);
        Call<List<MenueResponse>> call = mService.updateMenus();
        call.enqueue(new Callback<List<MenueResponse>>() {
            @SuppressLint("ShowToast")
            @Override
            public void onResponse(@NonNull Call<List<MenueResponse>> call, @NonNull Response<List<MenueResponse>> response) {
                if(response.code()==200) {
                    for(MenueResponse menueResponse: Objects.requireNonNull(response.body())){
                        if(!menueResponse.getError()){
                            mutableMenueLiveData.setValue(menueResponse.getMenue());
                        }
                    }
                }
            }
            @SuppressLint("ShowToast")
            @Override
            public void onFailure(@NonNull Call<List<MenueResponse>> call, @NonNull Throwable t) {

            }
        });
        return mutableMenueLiveData;
    }

    /*
    Orders Data
     */

    public MutableLiveData<List<OrdersResponse>> getCurrentOrders(String order_status) {
        mutableOrdersDataLiveData = new MutableLiveData<List<OrdersResponse>>();
        OrderApi mService = ApiClient.getClient().create(OrderApi.class);
        Call<List<OrdersResponse>> call = mService.getCurrentOrders(order_status);
        call.enqueue(new Callback<List<OrdersResponse>>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(Call<List<OrdersResponse>> call, Response<List<OrdersResponse>> response) {
                if (response.code() == 200 ) {
                    mutableOrdersDataLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<OrdersResponse>> call, Throwable t) {

            }
        });
        return mutableOrdersDataLiveData;
    }

    public MutableLiveData<List<OrdersResponse>> getOrdersByOrderId(String id) {
        mutableOrdersDataLiveData = new MutableLiveData<>();
        OrderApi mService = ApiClient.getClient().create(OrderApi.class);
        Call<List<OrdersResponse>> call = mService.getOrdersByOrderId(id);
        call.enqueue(new Callback<List<OrdersResponse>>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(Call<List<OrdersResponse>> call, Response<List<OrdersResponse>> response) {
                if (response.code() == 200 ) {
                    mutableOrdersDataLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<OrdersResponse>> call, Throwable t) {

            }
        });
        return mutableOrdersDataLiveData;
    }

    public MutableLiveData<List<OrdersResponse>> getOrdersByUserId(Integer id) {
        mutableOrdersDataLiveData = new MutableLiveData<>();
        OrderApi mService = ApiClient.getClient().create(OrderApi.class);
        Call<List<OrdersResponse>> call = mService.getOrdersByUserId(id);
        call.enqueue(new Callback<List<OrdersResponse>>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(Call<List<OrdersResponse>> call, Response<List<OrdersResponse>> response) {
                if (response.code() == 200 ) {
                    mutableOrdersDataLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<OrdersResponse>> call, Throwable t) {

            }
        });
        return mutableOrdersDataLiveData;
    }

    /*User Address*/

    public MutableLiveData<List<Maps>> getAddressByUserId(int id) {
        mService.getAddressByUserId(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Maps>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<Maps> result) {
                        mutableMapLiveData.setValue(result);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return mutableMapLiveData;
    }


    /*User */

    public MutableLiveData<List<UserResponse>> getUserByFirmId(String firmId){
        mutableUserLiveData = new MutableLiveData<>();
        UserApi service = ApiClient.getClient().create(UserApi.class);
        Call<List<UserResponse>> call = service.getUserByFirmId(firmId);
        call.enqueue(new Callback<List<UserResponse>>() {
            @SuppressLint("ShowToast")
            @Override
            public void onResponse(@NonNull Call<List<UserResponse>> call, @NonNull Response<List<UserResponse>> response) {
                if (response.code() == 200) {
                    mutableUserLiveData.setValue(response.body());
                }
            }

            @SuppressLint("ShowToast")
            @Override
            public void onFailure(@NonNull Call<List<UserResponse>> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.error + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return mutableUserLiveData;
    }

    public MutableLiveData<List<UserResponse>> deleteUser(int id) {
        mutableUserLiveData = new MutableLiveData<>();
        UserApi service = ApiClient.getClient().create(UserApi.class);
        Call<List<UserResponse>> call = service.deleteProfile(id);
        call.enqueue(new Callback<List<UserResponse>>() {
            @SuppressLint("ShowToast")
            @Override
            public void onResponse(@NonNull Call<List<UserResponse>> call, @NonNull Response<List<UserResponse>> response) {
                if (response.code() == 200) {
                    mutableUserLiveData.setValue(response.body());
                }
            }

            @SuppressLint("ShowToast")
            @Override
            public void onFailure(@NonNull Call<List<UserResponse>> call, @NonNull Throwable t) {
            }
        });
        return mutableUserLiveData;
    }
}