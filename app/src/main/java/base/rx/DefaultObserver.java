package base.rx;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Observer默认空实现
 */
public abstract class DefaultObserver<T> implements Observer2<T> {

    private boolean hasNext = false;
    private boolean hasComplete = false;

    public boolean isHasNext() {
        return hasNext;
    }

    public boolean isComplete() {
        return hasComplete;
    }

    @Override
    public final void onSubscribe(@NonNull Disposable d) {
        onSubscribe2(d);
    }

    @Override
    public final void onNext(@NonNull T t) {
        hasNext = true;
        onNext2(t);
    }

    @Override
    public final void onError(@NonNull Throwable e) {
        e.printStackTrace();
        onError2(e);
    }

    @Override
    public final void onComplete() {
        hasComplete = true;
        onComplete2();
    }

    @Override
    public void onSubscribe2(Disposable d) {
    }

    @Override
    public void onError2(Throwable e) {
    }

    @Override
    public void onComplete2() {
    }
}
