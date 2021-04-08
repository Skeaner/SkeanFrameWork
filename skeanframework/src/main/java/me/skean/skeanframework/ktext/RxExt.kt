package me.skean.skeanframework.ktext

import io.reactivex.disposables.Disposable

/**
 * Created by Skean on 21/4/8.
 */
fun Disposable?.isNotNullAndNotDisposed(): Boolean {
    return this != null && !this.isDisposed
}

fun Disposable?.disposeIfNotNullAndNotDisposed(): Boolean {
    if (this != null && !this.isDisposed) {
        this.dispose()
        return true
    }
    return false
}

fun Disposable?.doIfNotNullAndNotDisposed(action: Disposable.() -> Unit): Boolean {
    if (this != null && !this.isDisposed) {
        this.action()
        return true
    }
    return false
}


