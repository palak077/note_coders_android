package com.example.note_coders_android.utils.typeConverters;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateTypeConverters {

    @TypeConverter
    public static Date toDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long toLong(Date value) {
        return value == null ? null : value.getTime();
    }

}
