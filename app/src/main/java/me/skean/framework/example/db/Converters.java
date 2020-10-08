package me.skean.framework.example.db;

import java.util.Date;

import androidx.room.TypeConverter;

public final class Converters {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date != null ? date.getTime() : null;
    }

}
