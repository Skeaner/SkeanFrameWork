package me.skean.skeanframework.rx;

import io.reactivex.rxjava3.disposables.Disposable;

/**
 * Observer默认空实现
 */
public abstract class DefaultSingleObserver<T> implements SingleObserver2<T> {

    @Override
    public final void onSubscribe(Disposable d) {
        onSubscribe2(d);
    }

    @Override
    public final void onSuccess(T t) {
        onSuccess2(t);
    }

    @Override
    public final void onError(Throwable e) {
        e.printStackTrace();
        onError2(e);
    }


    @Override
    public void onSubscribe2(Disposable d) {
    }

    @Override
    public void onError2(Throwable e) {
    }
}
