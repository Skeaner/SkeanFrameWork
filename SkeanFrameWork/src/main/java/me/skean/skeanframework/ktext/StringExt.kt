@file:JvmName("StringExt")

package me.skean.skeanframework.ktext


import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils

/**
 * Created by Skean on 21/6/11.
 */
fun String?.orDash(): String = this ?: "-"

fun String?.orBlank(): String = this ?: " "

fun String?.or(defaultValue: String): String = this ?: defaultValue

@JvmOverloads
fun String?.showToast(short: Boolean = true) {
    this?.let {
        if (short) ToastUtils.showShort(this) else ToastUtils.showLong(this)
    }
}

@JvmOverloads
fun String?.logV(tag: String = LogUtils.getConfig().globalTag) {
    this?.let { LogUtils.vTag(tag, it); }
}

@JvmOverloads
fun String?.logD(tag: String = LogUtils.getConfig().globalTag) {
    this?.let { LogUtils.dTag(tag, it); }
}

@JvmOverloads
fun String?.logI(tag: String = LogUtils.getConfig().globalTag) {
    this?.let { LogUtils.iTag(tag, it); }
}


@JvmOverloads
fun String?.logW(tag: String = LogUtils.getConfig().globalTag) {
    this?.let { LogUtils.wTag(tag, it); }
}

@JvmOverloads
fun String?.logE(tag: String = LogUtils.getConfig().globalTag) {
    this?.let { LogUtils.eTag(tag, it); }
}

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
