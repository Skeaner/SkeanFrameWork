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
