package skean.me.base.db;

import android.database.Cursor;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.QueryBuilder;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

/**
 * 扩展功能的BaseMigration
 */
public abstract class ExtendMigration extends BaseMigration {

    public void createTable(DatabaseWrapper database, Class<? extends BaseModel> modelClass) {
        ModelAdapter<? extends BaseModel> adapter = FlowManager.getModelAdapter(modelClass);
        database.execSQL(adapter.getCreationQuery());
    }

    /**
     * 重命名表格
     */
    public void renameTable(DatabaseWrapper database, Class<? extends BaseModel> modelClass, String newTableName) {
        String renameQuery = new QueryBuilder(getAlterTableQueryBuilder(modelClass)).append("RENAME")
                                                                                    .appendSpaceSeparated("TO")
                                                                                    .append(newTableName)
                                                                                    .toString();
        database.execSQL(renameQuery);
    }

    /**
     * 添加列
     */
    public void addTableColumn(DatabaseWrapper database, Class<? extends BaseModel> modelClass, String columnName, SQLiteType type) {
        if (verifyTableAndColumn(database, modelClass, columnName)) {
            String addQuery = new QueryBuilder(getAlterTableQueryBuilder(modelClass)).append("ADD COLUMN")
                                                                                     .appendSpaceSeparated(QueryBuilder.quoteIfNeeded(columnName))
                                                                                     .appendSQLiteType(type)
                                                                                     .toString();
            database.execSQL(addQuery);
        }
    }

    /**
     * 添加列(有默认值)
     */
    public void addTableColumnWithDefault(DatabaseWrapper database,
                                          Class<? extends BaseModel> modelClass,
                                          String columnName,
                                          SQLiteType type,
                                          String defaultValue) {
        if (verifyTableAndColumn(database, modelClass, columnName)) {
            String addQuery = new QueryBuilder(getAlterTableQueryBuilder(modelClass)).append("ADD COLUMN")
                                                                                     .appendSpaceSeparated(QueryBuilder.quoteIfNeeded(columnName))
                                                                                     .appendSQLiteType(type)
                                                                                     .appendSpaceSeparated("DEFAULT")
                                                                                     .appendQuoted(defaultValue)
                                                                                     .toString();
            database.execSQL(addQuery);
        }
    }

    /**
     * 添加外键的列
     */
    public void addForeignKeyColumn(DatabaseWrapper database,
                                    Class<? extends BaseModel> modelClass,
                                    String columnName,
                                    SQLiteType type,
                                    String referenceClause) {
        if (verifyTableAndColumn(database, modelClass, columnName)) {
            String addQuery = new QueryBuilder(getAlterTableQueryBuilder(modelClass)).append("ADD COLUMN")
                                                                                     .appendSpaceSeparated(QueryBuilder.quoteIfNeeded(columnName))
                                                                                     .appendSQLiteType(type)
                                                                                     .appendSpaceSeparated("REFERENCES")
                                                                                     .append(referenceClause)
                                                                                     .toString();
            database.execSQL(addQuery);
        }
    }

    /**
     * 删除数据
     */
    public void deleteData(DatabaseWrapper database, Class<? extends BaseModel> modelClass, OperatorGroup where) {
        if (verifyTable(database, modelClass)) {
            QueryBuilder builder = new QueryBuilder(getDeleteQueryBuilder(modelClass));
            if (where != null) {
                builder.append("WHERE ").append(where.getQuery());
            }
            String deleteQuery = builder.toString();
            database.execSQL(deleteQuery);
        }
    }


    /**
     * 构造修改表格的QueryBuilder
     */
    private QueryBuilder getAlterTableQueryBuilder(Class<? extends BaseModel> modelClass) {
        String tableName = FlowManager.getTableName(modelClass);
        return new QueryBuilder().append("ALTER").appendSpaceSeparated("TABLE").appendQuotedIfNeeded(tableName).appendSpace();
    }



    /**
     * 构造删除表格的QueryBuilder
     */
    private QueryBuilder getDeleteQueryBuilder(Class<? extends BaseModel> modelClass) {
        String tableName = FlowManager.getTableName(modelClass);
        return new QueryBuilder().append("DELETE").appendSpaceSeparated("FROM").appendQuotedIfNeeded(tableName).appendSpace();
    }



    /**
     * 检查数据库, 表格是否存在
     */
    private boolean verifyTable(DatabaseWrapper database, Class<? extends BaseModel> modelClass) {
        Cursor cursorToCheckColumnFor = SQLite.select().from(modelClass).limit(0).query(database);
        return cursorToCheckColumnFor != null;
    }


    /**
     * 检查数据库, 表格是否存在和列是否已创建
     */
    private boolean verifyTableAndColumn(DatabaseWrapper database, Class<? extends BaseModel> modelClass, String columnDefinitionName) {
        boolean verify;
        Cursor cursorToCheckColumnFor = SQLite.select().from(modelClass).limit(0).query(database);
        if (cursorToCheckColumnFor != null) {
            String columnName = QueryBuilder.stripQuotes(columnDefinitionName);
            verify = cursorToCheckColumnFor.getColumnIndex(columnName) == -1;
            cursorToCheckColumnFor.close();
        } else {
            verify = false;
        }
        return verify;
    }
}