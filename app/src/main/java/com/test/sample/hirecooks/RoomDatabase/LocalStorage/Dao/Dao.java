package com.test.sample.hirecooks.RoomDatabase.LocalStorage.Dao;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@androidx.room.Dao
public interface Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertToWishList(List<Subcategory> userList);

    @Query("SELECT * FROM subcategory")
    LiveData<List<Subcategory>> getAllWishList();

    @Query("DELETE FROM subcategory")
    void deleteAllWishList();

    @Update
    void updateWishList(Subcategory subcategory);

    @Delete
    void deleteWishListItem(Subcategory product);
/*
    @Delete("DELETE FROM subcategory")
    void deleteWishList(Subcategory subcategory);*/
}
