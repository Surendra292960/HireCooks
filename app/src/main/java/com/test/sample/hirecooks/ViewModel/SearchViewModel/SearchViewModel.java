package com.test.sample.hirecooks.ViewModel.SearchViewModel;

import android.app.Application;

import com.test.sample.hirecooks.Repositories.Repository;
import com.test.sample.hirecooks.Models.SubCategory.SubcategoryResponse;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import static com.test.sample.hirecooks.Utils.Constants.SEARCH_KEY;

public class SearchViewModel extends AndroidViewModel {

    private Repository repository;
    private LiveData<SubcategoryResponse> searchResponseLiveData;

    public SearchViewModel(@NonNull Application application) {
        super(application);

        repository = new Repository(application);
        this.searchResponseLiveData = repository.searchAllProducts(SEARCH_KEY);
    }

    public LiveData<SubcategoryResponse> getSearchResponseLiveData() {
        return searchResponseLiveData;
    }
}