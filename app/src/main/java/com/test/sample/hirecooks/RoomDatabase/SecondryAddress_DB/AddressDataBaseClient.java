package com.test.sample.hirecooks.RoomDatabase.SecondryAddress_DB;
import android.content.Context;

import androidx.room.Room;

public class AddressDataBaseClient {

    private Context mCtx;
    private static AddressDataBaseClient mInstance;

    //our app database object
    private AddressDatabase addressDatabase;

    private AddressDataBaseClient(Context mCtx) {
        this.mCtx = mCtx;

        //creating the app database with Room database builder
        //MyToDos is the name of the database
        addressDatabase = Room.databaseBuilder(mCtx, AddressDatabase.class, "MyToDos").build();
    }

    public static synchronized AddressDataBaseClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new AddressDataBaseClient(mCtx);
        }
        return mInstance;
    }

    public AddressDatabase getAddressDatabase() {
        return addressDatabase;
    }
}