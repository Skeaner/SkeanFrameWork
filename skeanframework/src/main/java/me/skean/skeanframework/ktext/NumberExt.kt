package me.skean.skeanframework.ktext

import com.blankj.utilcode.util.SizeUtils

/**
 * Created by Skean on 20/9/30.
 */
fun Number.dp2px(): Int {
    return SizeUtils.dp2px(this.toFloat())
}

fun Number.px2dp(): Int {
    return SizeUtils.px2dp(this.toFloat())
}

fun Number.sp2px(): Int {
    return SizeUtils.sp2px(this.toFloat())
}

fun Number.px2sp(): Int {
    return SizeUtils.px2sp(this.toFloat())
}