@file:JvmName("RxExt")

package me.skean.skeanframework.ktext

import android.content.Context
import io.reactivex.disposables.Disposable
import me.skean.skeanframework.rx.DefaultObserver
import me.skean.skeanframework.rx.DefaultSingleObserver
import me.skean.skeanframework.rx.ProgressObserver
import me.skean.skeanframework.rx.ProgressSingleObserver

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

inline fun <T> defaultObserver(
        crossinline onSubscribe2: (Disposable) -> Unit = {},
        crossinline onError2: (Throwable) -> Unit = {},
        crossinline onComplete2: () -> Unit = {},
        crossinline onNext2: (T) -> Unit): DefaultObserver<T> {
    return object : DefaultObserver<T>() {

        override fun onSubscribe2(d: Disposable) {
            onSubscribe2.invoke(d)
        }

        override fun onNext2(t: T) {
            onNext2.invoke(t)
        }

        override fun onError2(e: Throwable) {
            onError2.invoke(e)
        }

        override fun onComplete2() {
            onComplete2.invoke()
        }
    }
}


inline fun <T> progressObserver(
        context: Context,
        crossinline onSubscribe2: (Disposable) -> Unit = {},
        crossinline onError2: (Throwable) -> Unit = {},
        crossinline onComplete2: () -> Unit = {},
        crossinline onNext2: (T) -> Unit): ProgressObserver<T> {
    return object : ProgressObserver<T>(context) {

        override fun onSubscribe2(d: Disposable) {
            onSubscribe2.invoke(d)
        }

        override fun onNext2(t: T) {
            onNext2.invoke(t)
        }

        override fun onError2(e: Throwable) {
            onError2.invoke(e)
        }

        override fun onComplete2() {
            onComplete2.invoke()
        }
    }
}

inline fun <T> defaultSingleObserver(
        crossinline onSubscribe2: (Disposable) -> Unit = {},
        crossinline onError2: (Throwable) -> Unit = {},
        crossinline onSuccess2: (T) -> Unit): DefaultSingleObserver<T> {
    return object : DefaultSingleObserver<T>() {
        override fun onSuccess2(t: T) {
            onSuccess2.invoke(t)
        }

        override fun onSubscribe2(d: Disposable) {
            onSubscribe2.invoke(d)
        }

        override fun onError2(e: Throwable) {
            onError2.invoke(e)
        }
    }
}

inline fun <T> progressSingleObserver(
        context: Context,
        crossinline onSubscribe2: (Disposable) -> Unit = {},
        crossinline onError2: (Throwable) -> Unit = {},
        crossinline onSuccess2: (T) -> Unit): ProgressSingleObserver<T> {
    return object : ProgressSingleObserver<T>(context) {
        override fun onSuccess2(t: T) {
            onSuccess2.invoke(t)
        }

        override fun onSubscribe2(d: Disposable) {
            onSubscribe2.invoke(d)
        }

        override fun onError2(e: Throwable) {
            onError2.invoke(e)
        }
    }
}

