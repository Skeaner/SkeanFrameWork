package me.skean.skeanframework.ktext

import com.blankj.utilcode.util.ToastUtils

/**
 * Created by Skean on 21/6/11.
 */
fun String?.orDash(): String = this ?: "-"

fun String?.orBlank(): String = this ?: " "

fun String?.or(defaultValue: String): String = this ?: defaultValue

fun String?.showAsShortToast() {
    if (this != null) {
        ToastUtils.showShort(this)
    }
}

fun String?.showAsLongToast() {
    if (this != null) {
        ToastUtils.showLong(this)
    }
}

fun String.subStringByKey(key: String, startIndex: Int = 0, keySearchStartIndex: Int = 0, ignoreCase: Boolean = false, includeKey: Boolean = false)
        : String {
    val appendText = if (includeKey) "" else key
    return substring(startIndex, indexOf(key, keySearchStartIndex, ignoreCase)) + appendText
}
