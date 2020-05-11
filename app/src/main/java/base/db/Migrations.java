package base.db;


import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Skean on 19/9/18.
 */
public class Migrations {

    public interface Migration {
        Integer getVersion();

        void runMigration(Database db);
    }

    public static List<Migration> getMigrations() {
        List<Migration> migrations = new ArrayList<>();
//        migrations.add(new MigrationV1001());
        Collections.sort(migrations, (m1, m2) -> m1.getVersion().compareTo(m2.getVersion()));
        return migrations;
    }

}
