package me.skean.skeanframework.rx

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import org.reactivestreams.Publisher
import java.util.concurrent.TimeUnit
import  io.reactivex.rxjava3.functions.Function


/**
 * 自动重试
 * @param maxRetries Int  最大重试次数, 0为无限重试
 * @param retryDelayMillis Int 每次重试的延迟毫秒数
 * @return Observable<T>
 */
fun <T : Any> Observable<T>.retryWhenWithTimesAndDelay(maxRetries: Int, retryDelayMillis: Int): Observable<T> {
    return this.retryWhen(RetryWithDelay(maxRetries, retryDelayMillis))
}

/**
 *
 * @param maxRetries Int 最大重试次数, 0为无限重试
 * @param retryDelayMillis Int Int 每次重试的延迟毫秒数
 * @return Single<T>
 */
fun <T : Any> Single<T>.retryWhenWithTimesAndDelay(maxRetries: Int, retryDelayMillis: Int): Single<T> {
    return this.retryWhen(RetryWithDelaySingle(maxRetries, retryDelayMillis))
}


class RetryWithDelay(private val maxRetries: Int, private val retryDelayMillis: Int) :
    Function<Observable<out Throwable>, Observable<*>> {
    private var retryCount = 0
    override fun apply(attempts: Observable<out Throwable>): Observable<*> {
        return attempts
            .flatMap { t ->
                if (maxRetries == 0 || ++retryCount < maxRetries) {
                    // When this Observable calls onNext, the original
                    // Observable will be retried (i.e. re-subscribed).
                    Observable.timer(
                        retryDelayMillis.toLong(),
                        TimeUnit.MILLISECONDS
                    )
                } else Observable.error<Any>(t)
            }
    }
}

class RetryWithDelaySingle(private val maxRetries: Int, private val retryDelayMillis: Int) :
    Function<Flowable<out Throwable>, Flowable<*>> {
    private var retryCount = 0

    override fun apply(attempts: Flowable<out Throwable>): Flowable<*> {
        return attempts
            .flatMap { t ->
                if (maxRetries == 0 || ++retryCount < maxRetries) {
                    // When this Observable calls onNext, the original
                    // Observable will be retried (i.e. re-subscribed).
                    Flowable.timer(
                        retryDelayMillis.toLong(),
                        TimeUnit.MILLISECONDS
                    )
                } else Flowable.error<Any>(t)
            }
    }
}

