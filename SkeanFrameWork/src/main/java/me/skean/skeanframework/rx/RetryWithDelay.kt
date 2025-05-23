package me.skean.skeanframework.rx

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import org.reactivestreams.Publisher
import java.util.concurrent.TimeUnit
import  io.reactivex.rxjava3.functions.Function




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



