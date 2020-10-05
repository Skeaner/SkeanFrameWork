package me.skean.skeanframework.db

class DBDefinition(
        var formatVersion: Int? = null, // 1
        var database: Database? = null
) {
    class Database(
            var version: Int? = null, // 1
            var identityHash: String? = null, // f4bd5b42bfe1a92d364ae6059368b077
            var entities: List<Entity?>? = null,
            var setupQueries: List<String?>? = null
    )

    class Entity(
            var tableName: String? = null, // Dummy
            var createSql: String? = null, // CREATE TABLE IF NOT EXISTS `${TABLE_NAME}` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `full_name` TEXT)
            var fields: List<Field?>? = null,
            var primaryKey: PrimaryKey? = null,
            var indices: List<Index?>? = null,
            var foreignKeys: List<ForeignKey?>? = null
    )

    class Field(
            var fieldPath: String? = null, // id
            var columnName: String? = null, // id
            var affinity: String? = null, // INTEGER
            var notNull: Boolean? = null // false
    )

    class PrimaryKey(
            var columnNames: List<String?>? = null,
            var autoGenerate: Boolean? = null // true
    )

    class Index(
            var name: String? = null, // index_Dummy_full_name
            var unique: Boolean? = null, // false
            var columnNames: List<String?>? = null,
            var createSql: String? = null // CREATE INDEX IF NOT EXISTS `index_Dummy_full_name` ON `${TABLE_NAME}` (`full_name`)
    )

    class ForeignKey(
            var table: String? = null, // Dummy
            var onDelete: String? = null, // NO ACTION
            var onUpdate: String? = null, // NO ACTION
            var columns: List<String?>? = null,
            var referencedColumns: List<String?>? = null
    )
}