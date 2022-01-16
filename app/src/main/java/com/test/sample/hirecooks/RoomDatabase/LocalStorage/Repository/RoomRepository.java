/*
package com.test.sample.hirecooks.RoomDatabase.LocalStorage.Repository;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;

import com.nineoldandroids.animation.Keyframe;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.MapLocationResponse.MapResponse;
import com.test.sample.hirecooks.Models.SubCategory.SubcategoryResponse;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.Dao.Dao;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.Database.RoomDatabase;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.RoomModel.Subcategory;
import com.test.sample.hirecooks.ViewModel.Repositories.Repository;
import com.test.sample.hirecooks.WebApis.MapApi;
import com.test.sample.hirecooks.WebApis.ProductApi;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomRepository {
    private static final String TAG = Repository.class.getSimpleName();
    private final Application application;
    private static Dao dao;
    private static RoomDatabase database;
    private MutableLiveData<List<Subcategory>> mutableSubCategoryLiveData = new MutableLiveData<>();

    public RoomRepository(@NonNull Application application) {
        this.application = application;
        database = RoomDatabase.getInstance(application);
        dao = database.dao();
        mutableSubCategoryLiveData = dao.getAll();
    }

    public void insert(List<Subcategory> subcategoryList) {
        new InsertNoteAsyncTask(dao).execute(subcategoryList);
    }
    public void update(Subcategory subcategoryList) {
        new UpdateNoteAsyncTask(dao).execute(subcategoryList);
    }
    public void delete(Subcategory subcategoryList) {
        new DeleteNoteAsyncTask(dao).execute(subcategoryList);
    }
    public void deleteAll() {
        new DeleteAllNotesAsyncTask(dao).execute();
    }
    public MutableLiveData<List<Subcategory>> getAll() {
        return mutableSubCategoryLiveData;
    }


    private static class InsertNoteAsyncTask extends AsyncTask<List<Subcategory>, Void, Void> { //static : doesnt have reference to the
        // repo itself otherwise it could cause memory leak!
        private Dao dao;
        private InsertNoteAsyncTask(Dao dao) {
            this.dao = dao;
        }
        @Override
        protected Void doInBackground(List<Subcategory>... subcategoryLists) { // ...  is similar to array
            dao.Insert(subcategoryLists[0]); //single subcategoryList
            return null;
        }
    }
    private static class UpdateNoteAsyncTask extends AsyncTask<Subcategory, Void, Void> {
        private Dao dao;
        private UpdateNoteAsyncTask(Dao dao) { //constructor as the class is static
            this.dao = dao;
        }
        @Override
        protected Void doInBackground(Subcategory... subcategoryLists) {
            dao.Update(subcategoryLists[0]);
            return null;
        }
    }
    private static class DeleteNoteAsyncTask extends AsyncTask<Subcategory, Void, Void> {
        private Dao dao;
        private DeleteNoteAsyncTask(Dao dao) {
            this.dao = dao;
        }
        @Override
        protected Void doInBackground(Subcategory... subcategoryLists) {
            dao.Delete(subcategoryLists[0]);
            return null;
        }
    }
    private static class DeleteAllNotesAsyncTask extends AsyncTask<Void, Void, Void> {
        private Dao dao;
        private DeleteAllNotesAsyncTask(Dao dao) {
            this.dao = dao;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            dao.DeleteAll();
            return null;
        }
    }
}
*/
