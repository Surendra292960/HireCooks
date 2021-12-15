package com.test.sample.hirecooks.ViewModel;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.test.sample.hirecooks.Models.Category.Category;
import com.test.sample.hirecooks.Models.Category.CategoryResponse;
import com.test.sample.hirecooks.Models.CooksPromotion.CooksPromotion;
import com.test.sample.hirecooks.Models.Offer.Offer;
import com.test.sample.hirecooks.Repositories.Repository;

import java.util.Collections;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class HomeFragViewModel extends AndroidViewModel {
    private final Repository repository;

    public HomeFragViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
    }

    public LiveData<List<CategoryResponse>> getCategoryResponse() {
        return repository.getCategory();
    }
    public LiveData<List<Offer>> getCategoryOffer() {
        return repository.getCategoryOffers();
    }
    public LiveData<List<CooksPromotion>> getCooksPromotion() {
        return repository.getCooksPromotions();
    }
}
