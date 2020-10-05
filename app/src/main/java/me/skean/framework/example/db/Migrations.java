package me.skean.framework.example.db;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

import org.jetbrains.annotations.NotNull;

public final class Migrations {

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {

        public void migrate(@NotNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Dummy ADD COLUMN 'created' INTEGER DEFAULT " + System.currentTimeMillis());
        }
    };

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        public void migrate(@NotNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE DummyChild ADD COLUMN 'created' INTEGER ");
        }
    };

    public static final Migration[] COLLECTIONS = {MIGRATION_1_2, MIGRATION_2_3};

}
