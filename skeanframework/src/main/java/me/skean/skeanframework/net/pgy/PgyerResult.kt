package me.skean.skeanframework.net.pgy

/**
 * Created by Skean on 2023/3/8.
 */
data class PgyerResult<T>(
    val code: Int,
    val `data`: T?,
    val message: String
)