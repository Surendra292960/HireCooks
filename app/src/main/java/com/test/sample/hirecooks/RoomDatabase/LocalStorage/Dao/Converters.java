package com.test.sample.hirecooks.RoomDatabase.LocalStorage.Dao;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.sample.hirecooks.Models.SubCategory.Color;
import com.test.sample.hirecooks.Models.SubCategory.Image;
import com.test.sample.hirecooks.Models.SubCategory.Size;
import com.test.sample.hirecooks.Models.SubCategory.Weight;

import java.lang.reflect.Type;
import java.util.List;

import androidx.room.TypeConverter;

public class Converters {
    @TypeConverter
    public static List<Color> fromColorString(String value) {
        Type listType = new TypeToken<List<Color>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }
    @TypeConverter
    public static String fromClorArrayList(List<Color> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static List<Image> fromImageString(String value) {
        Type listType = new TypeToken<List<Image>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }
    @TypeConverter
    public static String fromImageArrayList(List<Image> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static List<Size> fromSizeString(String value) {
        Type listType = new TypeToken<List<Size>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }
    @TypeConverter
    public static String fromSizeArrayList(List<Size> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static List<Weight> fromWeightString(String value) {
        Type listType = new TypeToken<List<Weight>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }
    @TypeConverter
    public static String fromWeightArrayList(List<Weight> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}