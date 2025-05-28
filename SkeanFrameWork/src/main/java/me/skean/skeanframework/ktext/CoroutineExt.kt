@file:JvmName("CoroutineExt")

package me.skean.skeanframework.ktext

import kotlinx.coroutines.delay

/**
 * Created by Skean on 2025/05/28.
 */
suspend inline fun <reified R> launchMinElapsed(minElapsedMillis: Long = 500, action: () -> R): R {
    val start = System.currentTimeMillis()
    val result = action.invoke()
    val end = System.currentTimeMillis()
    val elapsed = end - start
    val delta = minElapsedMillis - elapsed
    if (delta > 100) delay(delta)
    return result
}