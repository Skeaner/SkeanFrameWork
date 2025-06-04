@file:JvmName("CoroutineExt")

package me.skean.skeanframework.ktext

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

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

inline fun defaultExceptionHandler(
    showToast: Boolean = true,
    crossinline onError: (Throwable) -> Unit = {}
): CoroutineExceptionHandler {
    return CoroutineExceptionHandler { coroutineContext, throwable ->
        throwable.printStackTrace()
        if (showToast) throwable.showToast()
        onError(throwable)
    }
}