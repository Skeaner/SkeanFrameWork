package me.skean.skeanframework.rx;

import org.reactivestreams.Subscription;

/**
 * Subscriber 空实现
 */

public abstract class DefaultSubscriber<T> implements Subscriber2<T> {

    private boolean hasNext = false;
    private boolean hasComplete = false;

    public boolean isHasNext() {
        return hasNext;
    }

    public boolean isComplete() {
        return hasComplete;
    }

    @Override
    public final void onSubscribe(Subscription s) {
        onSubscribe2(s);
    }

    @Override
    public final void onNext(T t) {
        hasNext = true;
        onNext2(t);
    }

    @Override
    public final void onError(Throwable t) {
        t.printStackTrace();
        onError2(t);
    }

    @Override
    public final void onComplete() {
        hasComplete = true;
        onComplete2();
    }

    @Override
    public void onSubscribe2(Subscription s) {
    }

    @Override
    public void onError2(Throwable t) {
    }

    @Override
    public void onComplete2() {
    }
}
