package skean.me.base.rx;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Observer默认空实现
 */
public class DefaultObserver<T> implements Observer<T> {

    private boolean hasNext = false;

    public boolean isHasNext() {
        return hasNext;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public void onNext(@NonNull T t) {
        hasNext = true;
    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {

    }
}
