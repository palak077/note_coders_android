package com.example.note_coders_android.utils.typeConverters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;

import com.example.note_coders_android.data.entities.Category;

public class CategoryTypeConverter {

    @TypeConverter
    public static String toString(Category category) {
        return new Gson().toJson(category);
    }

    @TypeConverter
    public static Category toCategory(String value) {
        return new Gson().fromJson(value, Category.class);
    }

}
