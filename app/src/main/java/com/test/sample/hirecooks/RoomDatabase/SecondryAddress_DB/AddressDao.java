package com.test.sample.hirecooks.RoomDatabase.SecondryAddress_DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AddressDao {

    @Query("SELECT * FROM address")
    List<Address> getAll();

    @Insert
    void insert(Address address);

    @Delete
    void delete(Address address);

    @Update
    void update(Address address);

}