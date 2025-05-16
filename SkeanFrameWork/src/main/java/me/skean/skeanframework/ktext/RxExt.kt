@file:JvmName("RxExt")

package me.skean.skeanframework.ktext

import android.content.Context
import androidx.lifecycle.Lifecycle
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.trello.rxlifecycle4.LifecycleProvider
import io.reactivex.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import me.skean.skeanframework.model.LoadingStatus
import me.skean.skeanframework.rx.*
import me.skean.skeanframework.widget.LoadingDialog2

/**
 * Created by Skean on 21/4/8.
 */

fun <T : Any> Single<T>.subscribeOnIoObserveOnMainThread(): Single<T> {
    return this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun <T : Any> Observable<T>.subscribeOnIoObserveOnMainThread(): Observable<T> {
    return this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}

fun <T : Any> Flowable<T>.subscribeOnIoObserveOnMainThread(): Flowable<T> {
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
    crossinline onNext2: (T) -> Unit
): DefaultObserver<T> {
    return object : DefaultObserver<T>() {

        override fun onSubscribe2(d: Disposable) {
            onSubscribe2.invoke(d)
        }

        override fun onNext2(t: T & Any) {
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
    crossinline onNext2: (T) -> Unit
): ProgressObserver<T> {
    return object : ProgressObserver<T>(context) {

        override fun onSubscribe2(d: Disposable) {
            onSubscribe2.invoke(d)
        }

        override fun onNext2(t: T & Any) {
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
    crossinline onSuccess2: (T) -> Unit
): DefaultSingleObserver<T> {
    return object : DefaultSingleObserver<T>() {
        override fun onSuccess2(t: T & Any) {
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
    crossinline onSuccess2: (T) -> Unit
): ProgressSingleObserver<T> {
    return object : ProgressSingleObserver<T>(context) {
        override fun onSuccess2(t: T & Any) {
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


inline fun <T> applyAutoLoading(
    context: Context,
    message: String = "请稍后",
    cancelable: Boolean = true,
    crossinline onCancel: (Unit) -> Unit = { }
)
        : DialogProgressTransformer<T> {
    val dialog = LoadingDialog2(context)
    dialog.setMessage(message)
    dialog.setCancelable(cancelable)
    val cancelObservable = DialogCancelObservable(dialog).doOnNext { onCancel.invoke(Unit) }
    return DialogProgressTransformer<T>(dialog, cancelObservable)
}


inline fun <T : Any> Single<T>.applyAutoLoading(
    context: Context,
    message: String = "请稍后",
    cancelable: Boolean = true,
    crossinline onCancel: (Unit) -> Unit = { }
)
        : Single<T> {
    return this.compose(me.skean.skeanframework.ktext.applyAutoLoading<T>(context, message, cancelable, onCancel))
}


inline fun <T : Any> Observable<T>.applyAutoLoading(
    context: Context,
    message: String = "请稍后",
    cancelable: Boolean = true,
    crossinline onCancel: (Unit) -> Unit = { }
)
        : Observable<T> {
    return this.compose(me.skean.skeanframework.ktext.applyAutoLoading<T>(context, message, cancelable, onCancel))
}

inline fun <T : Any> Flowable<T>.applyAutoLoading(
    context: Context,
    message: String = "请稍后",
    cancelable: Boolean = true,
    crossinline onCancel: (Unit) -> Unit = { }
)
        : Flowable<T> {
    return this.compose(me.skean.skeanframework.ktext.applyAutoLoading<T>(context, message, cancelable, onCancel))
}


fun <T : Any> applyAutoRefresh(loader: SmartRefreshLayout, refresh: Boolean, checkSuccessAndNoMore: (T) -> Pair<Boolean, Boolean>)
        : SmartRefreshLayoutTransformer<T> {
    return SmartRefreshLayoutTransformer<T>(loader, refresh, checkSuccessAndNoMore)
}

fun <T : Any> Single<T>.applyAutoRefresh(
    loader: SmartRefreshLayout,
    refresh: Boolean,
    checkSuccessAndNoMore: (T) -> Pair<Boolean, Boolean>
)
        : Single<T> {
    return this.compose(me.skean.skeanframework.ktext.applyAutoRefresh<T>(loader, refresh, checkSuccessAndNoMore))
}


fun <T : Any> Observable<T>.applyAutoRefresh(
    loader: SmartRefreshLayout,
    refresh: Boolean,
    checkSuccessAndNoMore: (T) -> Pair<Boolean, Boolean>
)
        : Observable<T> {
    return this.compose(me.skean.skeanframework.ktext.applyAutoRefresh<T>(loader, refresh, checkSuccessAndNoMore))
}

fun <T : Any> Flowable<T>.applyAutoRefresh(
    loader: SmartRefreshLayout,
    refresh: Boolean,
    checkSuccessAndNoMore: (T) -> Pair<Boolean, Boolean>
)
        : Flowable<T> {
    return this.compose(me.skean.skeanframework.ktext.applyAutoRefresh<T>(loader, refresh, checkSuccessAndNoMore))
}


fun <T : Any> Single<T>.composeLoadingStatus(onLoadingStatus: (LoadingStatus) -> Unit): Single<T> {
    return this.doOnSubscribe { onLoadingStatus.invoke(LoadingStatus.loading()) }
        .doOnSuccess { onLoadingStatus.invoke(LoadingStatus.success()) }
        .doOnError { onLoadingStatus.invoke(LoadingStatus.fail(tips = it.localizedMessage)) }
}


fun <T : Any> Observable<T>.composeLoadingStatus(onLoadingStatus: (LoadingStatus) -> Unit)
        : Observable<T> {
    return this.doOnSubscribe { onLoadingStatus.invoke(LoadingStatus.loading()) }
        .doOnNext { onLoadingStatus.invoke(LoadingStatus.success()) }
        .doOnError { onLoadingStatus.invoke(LoadingStatus.fail(tips = it.localizedMessage)) }
}

fun <T : Any> Flowable<T>.composeLoadingStatus(onLoadingStatus: (LoadingStatus) -> Unit)
        : Flowable<T> {
    return this.doOnSubscribe { onLoadingStatus.invoke(LoadingStatus.loading()) }
        .doOnNext { onLoadingStatus.invoke(LoadingStatus.success()) }
        .doOnError { onLoadingStatus.invoke(LoadingStatus.fail(tips = it.localizedMessage)) }
}


fun <T : Any> Single<T>.bindToVmLifecycle(lifecycle: LifecycleProvider<Any>): Single<T> {
    return compose<T>(lifecycle.bindToLifecycle())
}

fun <T : Any> Observable<T>.bindToVmLifecycle(lifecycle: LifecycleProvider<Any>): Observable<T> {
    return compose<T>(lifecycle.bindToLifecycle())
}

fun <T : Any> Flowable<T>.bindToVmLifecycle(lifecycle: LifecycleProvider<Any>): Flowable<T> {
    return compose<T>(lifecycle.bindToLifecycle())
}
