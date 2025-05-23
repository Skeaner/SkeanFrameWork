package me.skean.skeanframework.rx

import io.reactivex.rxjava3.core.Flowable
import java.util.concurrent.TimeUnit
import  io.reactivex.rxjava3.functions.Function


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