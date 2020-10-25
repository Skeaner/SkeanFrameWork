package me.skean.skeanframework.ktext

import com.blankj.utilcode.util.SizeUtils
import java.math.RoundingMode
import java.text.DecimalFormat

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

fun Double.toPrice(): Double? {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING
    return df.format(this).toDouble()
}

fun Float.toPrice(): Float? {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING
    return df.format(this).toFloat()
}