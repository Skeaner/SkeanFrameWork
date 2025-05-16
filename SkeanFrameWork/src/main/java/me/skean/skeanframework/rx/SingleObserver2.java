
package me.skean.skeanframework.rx;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

public interface SingleObserver2<T> extends SingleObserver<T> {

    void onSubscribe2(@NonNull Disposable d);

    void onSuccess2(@NonNull T t);

    void onError2(@NonNull Throwable e);


}
