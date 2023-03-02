package me.skean.skeanframework.net.pgy

data class PgyerResult<T>(
    val code: Int,
    val `data`: T?,
    val message: String
)