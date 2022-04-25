
package me.skean.skeanframework.rx;

import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public interface SingleObserver2<T> extends SingleObserver<T> {

    void onSubscribe2(@NonNull Disposable d);

    void onSuccess2(@NonNull T t);

    void onError2(@NonNull Throwable e);


}
