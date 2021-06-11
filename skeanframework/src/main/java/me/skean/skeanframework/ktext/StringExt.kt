package me.skean.skeanframework.ktext

/**
 * Created by Skean on 21/6/11.
 */
fun String?.orDash(): String = this ?: "-"

fun String?.orBlank(): String = this ?: " "

fun String?.or(defaultValue: String): String = this ?: defaultValue
