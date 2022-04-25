package me.skean.skeanframework.rx;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public interface Subscriber2<T> extends Subscriber<T> {
    void onSubscribe2(Subscription s);

    void onNext2(T t);

    void onError2(Throwable t);

    void onComplete2();
}
