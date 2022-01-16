package com.test.sample.hirecooks.RoomDatabase.LocalStorage.Database;
import android.content.Context;
import android.os.AsyncTask;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.Dao.Converters;
import com.test.sample.hirecooks.RoomDatabase.LocalStorage.Dao.Dao;
import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

@androidx.room.Database(entities = {Subcategory.class}, version = 2)
@TypeConverters({Converters.class})
public abstract class RoomDatabase extends androidx.room.RoomDatabase {
    private static final String DATABASE_NAME = "Database";

    public abstract Dao dao();

    private static volatile RoomDatabase INSTANCE;

    public static RoomDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (RoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, RoomDatabase.class,
                            DATABASE_NAME)
                            .addCallback(callback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    static Callback callback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateAsynTask(INSTANCE);
        }
    };

    static class PopulateAsynTask extends AsyncTask<Void, Void, Void> {
        private Dao dao;
        PopulateAsynTask(RoomDatabase userDatabase) {
            dao = userDatabase.dao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            dao.deleteAllWishList();
            return null;
        }
    }
}
