package me.skean.skeanframework.model

/**
 * Created by Skean on 2022/4/21.
 */
data class AppResponse<T>(
    val success: Boolean, val msg: String? = null,
    val refresh: Boolean? = null, val noMore: Boolean? = null,
    val result: T?
)
