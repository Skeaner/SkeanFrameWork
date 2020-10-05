package me.skean.framework.example.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(foreignKeys = [ForeignKey(entity = Dummy::class, parentColumns = ["id"], childColumns = ["pid"])])
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