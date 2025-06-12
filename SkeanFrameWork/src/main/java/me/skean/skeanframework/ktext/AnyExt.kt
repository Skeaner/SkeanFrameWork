package me.skean.skeanframework.ktext

import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils

/**
 * 所有的扩展
 */
fun Any?.toStringIfNullEmpty(): String {
    return this?.toString() ?: ""
}

fun <T> Any?.castTo(): T? {
    return this as? T
}


@JvmOverloads
fun Any?.showToast(short: Boolean = true) {
    this?.let {
        val text = if (this is Throwable) this.message else this.toString()
        if (short) ToastUtils.showShort(text) else ToastUtils.showLong(text)
    }
}

@JvmOverloads
fun Any?.logV(tag: String = LogUtils.getConfig().globalTag) {
    this?.let { LogUtils.vTag(tag, it); }
}

@JvmOverloads
fun Any?.logD(tag: String = LogUtils.getConfig().globalTag) {
    this?.let { LogUtils.dTag(tag, it); }
}

@JvmOverloads
fun Any?.logI(tag: String = LogUtils.getConfig().globalTag) {
    this?.let { LogUtils.iTag(tag, it); }
}


@JvmOverloads
fun Any?.logW(tag: String = LogUtils.getConfig().globalTag) {
    this?.let { LogUtils.wTag(tag, it); }
}

@JvmOverloads
fun Any?.logE(tag: String = LogUtils.getConfig().globalTag) {
    if (this is Throwable) {
        this.printStackTrace()
    }
    this?.let { LogUtils.eTag(tag, it); }
}