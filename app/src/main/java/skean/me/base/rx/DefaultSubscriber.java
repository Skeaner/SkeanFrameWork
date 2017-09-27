package skean.me.base.rx;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * Subscriber 空实现
 */

public class DefaultSubscriber<T> implements Subscriber<T> {

    private boolean hasNext = false;

    public boolean isHasNext() {
        return hasNext;
    }

    @Override
    public void onSubscribe(Subscription s) {

    }

    @Override
    public void onNext(T t) {
        hasNext =true;
    }

    @Override
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @Override
    public void onComplete() {

    }
}
