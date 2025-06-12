@file:JvmName("StringExt")

package me.skean.skeanframework.ktext


import android.graphics.Color
import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Created by Skean on 21/6/11.
 */
fun String?.orDash(): String = this ?: "-"

fun String?.orBlank(): String = this ?: " "

fun String?.or(defaultValue: String): String = this ?: defaultValue

fun String.subStringByKey(
    key: String,
    startIndex: Int = 0,
    keySearchStartIndex: Int = 0,
    ignoreCase: Boolean = false,
    includeKey: Boolean = false
)
        : String {
    val appendText = if (includeKey) "" else key
    return substring(startIndex, indexOf(key, keySearchStartIndex, ignoreCase)) + appendText
}

fun String.parseLocalDate(pattern: String = "yyyy-MM-dd"): LocalDate {
    val localDate = LocalDate.now()
    return LocalDate.parse(this, DateTimeFormatter.ofPattern(pattern))
}

fun String.parseLocalDateTime(pattern: String = "yyyy-MM-dd HH:mm:ss"): LocalDateTime {
    return LocalDateTime.parse(this, DateTimeFormatter.ofPattern(pattern))
}

fun String.parseColor() = Color.parseColor(this)


