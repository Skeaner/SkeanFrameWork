package me.skean.framework.example.db.entity

import androidx.room.*
import java.util.*

@Entity(foreignKeys = [ForeignKey(entity = Dummy::class, parentColumns = ["id"], childColumns = ["pid"])],
    indices = [Index(value = ["pid"])])
class DummyChild {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null

    @ColumnInfo(name = "full_name")
    var fullName: String? = null

    var pid: Long? = null

    /**
     * 在版本3中添加
     */
    var created: Date? = null
}