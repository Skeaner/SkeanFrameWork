package me.skean.framework.example.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import kotlin.Metadata;
import me.skean.framework.example.db.dao.DummyDao;
import me.skean.framework.example.db.entity.Dummy;
import me.skean.framework.example.db.entity.DummyChild;

import org.jetbrains.annotations.Nullable;

@Database(entities = {Dummy.class, DummyChild.class}, version = 3)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    @Nullable
    public abstract DummyDao getDummyDao();
}
