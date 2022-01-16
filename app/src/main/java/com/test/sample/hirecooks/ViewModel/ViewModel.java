package com.test.sample.hirecooks.ViewModel;
import android.app.Application;
import android.content.Context;
import com.test.sample.hirecooks.Models.Category.CategoryResponse;
import com.test.sample.hirecooks.Models.CooksPromotion.CooksPromotion;
import com.test.sample.hirecooks.Models.MapLocationResponse.Maps;
import com.test.sample.hirecooks.Models.Menue.Menue;
import com.test.sample.hirecooks.Models.NewOrder.OrdersResponse;
import com.test.sample.hirecooks.Models.Offer.Offer;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import com.test.sample.hirecooks.Models.SubCategory.SubcategoryResponse;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.Models.Users.UserResponse;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.Database.RoomDatabase;
import com.test.sample.hirecooks.ViewModel.Repositories.Repository;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ViewModel extends AndroidViewModel {
    private Repository repository;
    private AlertDialog alertDialog;
    private RoomDatabase database;

    public ViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
        alertDialog = new AlertDialog(application);
        database = RoomDatabase.getInstance(application);
    }

    public void onBtnClick(Context context, User firm_user, String userAction, String btn1, String btn2) {
        alertDialog.userAlert(context,firm_user,userAction, btn1, btn2);
    }

    public LiveData<List<Offer>> getCategoryOffer() {
        return repository.getCategoryOffers();
    }
    public LiveData<List<CooksPromotion>> getCooksPromotion() {
        return repository.getCooksPromotions();
    }

    /*
    Menue Data
     */
    public LiveData<List<Menue>> getMenue() {
        return repository.getMenue();
    }

    /*
    Category Data
     */
    public LiveData<List<CategoryResponse>> getCategory(Integer id) {
        return repository.getCategory(id);
    }
    /*
    Subcategory Data
     */
    public LiveData<List<SubcategoryResponse>> searchNearByProducts(Integer id, String search_key) {
        return repository.searchNearByProducts(id,search_key);
    }

    public LiveData<List<SubcategoryResponse>> getNearBySubCategory(Integer id) {
        return repository.getNearBySubCategory(id);
    }

    public LiveData<List<SubcategoryResponse>> getNearBySubCategoryBySubId(Integer userId, Integer sub_id) {
        return repository.getNearBySubCategoryBySub_id(userId,sub_id);
    }

    public LiveData<List<SubcategoryResponse>> getSubCategoryBySub_id(Integer sub_id) {
        return repository.getNearBySubCategoryBySub_id(sub_id);
    }
    /*WishList*/

    public LiveData<List<Subcategory>> insertToWishList(List<Subcategory> subcategoryList) {
        return repository.insertToWishList(subcategoryList);
    }
    public LiveData<List<Subcategory>> getAllWishList() {
        return repository.getAllWishList();
    }
    public void deleteWishListItem(Subcategory subcategory) {
        repository.deleteWishListItem(subcategory);
    }
    public void deleteAllWishListItem() {
        repository.deleteAllWishListItem();
    }

    /*
    Order Data
     */
    public LiveData<List<OrdersResponse>> getCurrentOrders(String order_status) {
        return repository.getCurrentOrders(order_status);
    }

    public LiveData<List<OrdersResponse>> getOrdersByOrderId(String id) {
        return repository.getOrdersByOrderId(id);
    }

    public LiveData<List<OrdersResponse>> getOrdersByUserId(Integer id) {
        return repository.getOrdersByUserId(id);
    }

    /*Address Map Location
    */

    public MutableLiveData<List<Maps>> getAddressByUserId(int id) {
        return repository.getAddressByUserId(id);
    }

    /*User*/

    public MutableLiveData<List<UserResponse>> getUserByFirmId(String id) {
        return repository.getUserByFirmId(id);
    }

    public MutableLiveData<List<UserResponse>> deleteUser(int id) {
        return repository.deleteUser(id);
    }
}
