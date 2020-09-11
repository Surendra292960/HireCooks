package com.test.sample.hirecooks.RoomDatabase.SecondryAddress_DB;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Address.class}, version = 2)
public abstract class AddressDatabase extends RoomDatabase {
    public abstract AddressDao addressDao();
}