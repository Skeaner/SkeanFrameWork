package me.skean.skeanframework.ktext

/**
 * 所有的扩展
 */
fun Any?.toStringIfNullEmpty(): String {
    return this?.toString() ?: ""
}