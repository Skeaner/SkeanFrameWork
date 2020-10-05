package me.skean.framework.example.db.pojo

import androidx.room.ColumnInfo

class NameCount {
    @ColumnInfo(name = "full_name")
    var fullName: String? = null
    var count: Int? = null
}