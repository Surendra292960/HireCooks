/*
package com.test.sample.hirecooks.RoomDatabase.LocalStorage.viewmodel;


import android.app.Application;

import com.test.sample.hirecooks.Models.Category.CategoryResponse;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.Database.RoomDatabase;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.Repository.RoomRepository;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.RoomModel.Subcategory;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class RoomViewModel extends AndroidViewModel {
    private RoomRepository repository;
    private MutableLiveData<List<Subcategory>> mutableSubCategoryLiveData;

    public RoomViewModel(@NonNull Application application) {
        super(application);
        repository = new RoomRepository(application);
        mutableSubCategoryLiveData = repository.getAll();
    }

    public void insert(List<Subcategory> subcategoryList) {
        repository.insert(subcategoryList);
    }
    public void update(Subcategory subcategoryList) {
        repository.update(subcategoryList);
    }
    public void delete(Subcategory subcategoryList) {
        repository.delete(subcategoryList);
    }
    public void deleteAll() {
        repository.deleteAll();
    }
    public MutableLiveData<List<Subcategory>> getAll() {
        return mutableSubCategoryLiveData;
    }
}*/
