package skean.me.base.db;

import com.raizlabs.android.dbflow.sql.QueryBuilder;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * 给AlterTableMigration添加了addColumnWithDefault方法;
 */
@SuppressWarnings("unchecked")
public class AlterTableMigration2<Model extends BaseModel> extends AlterTableMigration<Model> {

    private ArrayList<QueryBuilder> builderList;

    public AlterTableMigration2(Class<Model> table) {
        super(table);
    }

    public ArrayList<QueryBuilder> getParentColumnDefinitions() {
        if (builderList == null) {
            try {
                Field f = AlterTableMigration.class.getDeclaredField("columnDefinitions");
                f.setAccessible(true);
                if (f.get(this) == null) {
                    builderList = new ArrayList<>();
                    f.set(this, builderList);
                } else builderList = (ArrayList<QueryBuilder>) f.get(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return builderList;
    }

    public <ColumnType> AlterTableMigration<Model> addColumnWithDefault(Class<ColumnType> columnType,
                                                                        String columnName,
                                                                        String defaultValue) {
        QueryBuilder queryBuilder = (new QueryBuilder()).appendQuoted(columnName)
                                                        .appendSpace()
                                                        .appendType(columnType.getName())
                                                        .appendSpace()
                                                        .append("DEFAULT")
                                                        .appendSpace()
                                                        .appendQuoted(defaultValue);
        getParentColumnDefinitions().add(queryBuilder);
        return this;
    }

}
