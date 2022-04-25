@file:JvmName("RxExt")

package me.skean.skeanframework.ktext

import android.content.Context
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import me.skean.skeanframework.rx.*
import me.skean.skeanframework.widget.LoadingDialog2

/**
 * Created by Skean on 21/4/8.
 */

fun <T> Single<T>.subscribeOnIoObserveOnMainThread(): Single<T> {
    return this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.subscribeOnIoObserveOnMainThread(): Observable<T> {
    return this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun <T> Flowable<T>.subscribeOnIoObserveOnMainThread(): Flowable<T> {
    return this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}


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


inline fun <T> applyAutoLoading(context: Context, message: String = "请稍后", cancelable: Boolean = true, crossinline onCancel: (Unit) -> Unit = { })
        : DialogProgressTransformer<T> {
    val dialog = LoadingDialog2(context)
    dialog.setMessage(message)
    dialog.setCancelable(cancelable)
    val cancelObservable = DialogCancelObservable(dialog).doOnNext { onCancel.invoke(Unit) }
    return DialogProgressTransformer<T>(dialog, cancelObservable)
}


inline fun <T> Single<T>.applyAutoLoading(context: Context, message: String = "请稍后", cancelable: Boolean = true, crossinline onCancel: (Unit) -> Unit = { })
        : Single<T> {
    return this.compose(me.skean.skeanframework.ktext.applyAutoLoading<T>(context, message, cancelable, onCancel))
}


inline fun <T> Observable<T>.applyAutoLoading(context: Context, message: String = "请稍后", cancelable: Boolean = true, crossinline onCancel: (Unit) -> Unit = { })
        : Observable<T> {
    return this.compose(me.skean.skeanframework.ktext.applyAutoLoading<T>(context, message, cancelable, onCancel))
}

inline fun <T> Flowable<T>.applyAutoLoading(context: Context, message: String = "请稍后", cancelable: Boolean = true, crossinline onCancel: (Unit) -> Unit = { })
        : Flowable<T> {
    return this.compose(me.skean.skeanframework.ktext.applyAutoLoading<T>(context, message, cancelable, onCancel))
}


fun <T> applyAutoRefresh(loader: SmartRefreshLayout, refresh: Boolean, checkSuccessAndNoMore: (T) -> Pair<Boolean,Boolean>)
        : SmartRefreshLayoutTransformer<T> {
    return SmartRefreshLayoutTransformer<T>(loader, refresh, checkSuccessAndNoMore)
}

fun <T> Single<T>.applyAutoRefresh(loader: SmartRefreshLayout, refresh: Boolean, checkSuccessAndNoMore: (T) -> Pair<Boolean,Boolean>)
        : Single<T> {
    return this.compose(me.skean.skeanframework.ktext.applyAutoRefresh<T>(loader, refresh, checkSuccessAndNoMore))
}


fun <T> Observable<T>.applyAutoRefresh(loader: SmartRefreshLayout, refresh: Boolean, checkSuccessAndNoMore: (T) -> Pair<Boolean,Boolean>)
        : Observable<T> {
    return this.compose(me.skean.skeanframework.ktext.applyAutoRefresh<T>(loader, refresh, checkSuccessAndNoMore))
}

fun <T> Flowable<T>.applyAutoRefresh(loader: SmartRefreshLayout, refresh: Boolean, checkSuccessAndNoMore: (T) -> Pair<Boolean,Boolean>)
        : Flowable<T> {
    return this.compose(me.skean.skeanframework.ktext.applyAutoRefresh<T>(loader, refresh, checkSuccessAndNoMore))
}
