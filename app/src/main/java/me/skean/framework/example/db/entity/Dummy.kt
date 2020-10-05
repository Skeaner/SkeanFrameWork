package me.skean.framework.example.db.entity

import android.graphics.Bitmap
import androidx.room.*
import java.util.*

@Entity(indices = [Index("full_name")])
// @Fts4
class Dummy {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null

    @ColumnInfo(name = "full_name")
    var fullName: String? = null

    /**
     * 在版本2中添加
     */
    var created: Date? = null

    @Ignore
    var picture: Bitmap? = null


}