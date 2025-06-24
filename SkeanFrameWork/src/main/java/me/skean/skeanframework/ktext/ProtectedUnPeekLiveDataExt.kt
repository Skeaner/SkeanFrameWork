@file:JvmName("ProtectedUnPeekLiveDataExt")
package com.kunminx.architecture.ui.callback

import com.kunminx.architecture.domain.message.MutableResult
import me.hgj.jetpackmvvm.callback.livedata.event.EventLiveData

/**
 * Created by Skean on 2025/06/23.
 */

fun <T> EventLiveData<T>.setAllowNullValue(isAllowNullValue: Boolean): EventLiveData<T> {
    this.isAllowNullValue = isAllowNullValue
    return this
}

fun <T> UnPeekLiveData<T>.setAllowNullValue(isAllowNullValue: Boolean): UnPeekLiveData<T> {
    this.isAllowNullValue = isAllowNullValue
    return this
}

fun <T> MutableResult<T>.setAllowNullValue(isAllowNullValue: Boolean): MutableResult<T> {
    this.isAllowNullValue = isAllowNullValue
    return this
}
