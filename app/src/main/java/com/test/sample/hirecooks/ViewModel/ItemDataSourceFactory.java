package com.test.sample.hirecooks.ViewModel;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import com.test.sample.hirecooks.ViewModel.Repositories.ItemDataSource;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

public class ItemDataSourceFactory extends DataSource.Factory {
    //creating the mutable live data
    private MutableLiveData<PageKeyedDataSource<Integer, Subcategory>> itemLiveDataSource = new MutableLiveData<>();
 
    @Override
    public DataSource<Integer, Subcategory> create() {
        //getting our data source object
        ItemDataSource itemDataSource = new ItemDataSource();
        //posting the datasource to get the values
        itemLiveDataSource.postValue(itemDataSource);
        //returning the datasource
        return itemDataSource;
    }
    //getter for itemlivedatasource
    public MutableLiveData<PageKeyedDataSource<Integer, Subcategory>> getItemLiveDataSource() {
        return itemLiveDataSource;
    }
}