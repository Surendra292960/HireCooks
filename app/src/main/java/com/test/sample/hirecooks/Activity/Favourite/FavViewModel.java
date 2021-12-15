package com.test.sample.hirecooks.Activity.Favourite;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.LocalStorage;
import com.test.sample.hirecooks.Utils.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class FavViewModel extends ViewModel {

    MutableLiveData<ArrayList<Subcategory>> favLiveData;
    ArrayList<Subcategory> favArrayList;
    private LocalStorage localStorage;

    public FavViewModel() {
        favLiveData = new MutableLiveData<>();
        // call your Rest API in init method
        init();
    }

    public MutableLiveData<ArrayList<Subcategory>> getSubcategoryMutableLiveData() {
        return favLiveData;
    }

    public void init(){
        populateList();
        favLiveData.setValue(favArrayList);
    }

    public void populateList(){
/*
        Subcategory subcategory = new Subcategory();
        subcategory.setName("Darknight");
        subcategory.setDetailDiscription("Best rating movie");

        favArrayList = new ArrayList<>();
        favArrayList.add(subcategory);
        favArrayList.add(subcategory);
        favArrayList.add(subcategory);
        favArrayList.add(subcategory);
        favArrayList.add(subcategory);
        favArrayList.add(subcategory);*/
    }

    public void deleteFavItem(int position, List<Subcategory> favouriteList, Context mCtx){
        localStorage = new LocalStorage(mCtx);
        favouriteList.remove(position);
        Gson gson = new Gson();
        String favourite = gson.toJson(favouriteList);
        Log.d("FAVOURITE", favourite);
        localStorage.setFavourite(favourite);
    }

    private List<Subcategory> getFavItem(List<Subcategory> favouriteList){
        /*favouriteList = localStorage.getFavourite();
        return favouriteList;*/
        return favouriteList;
    }
}