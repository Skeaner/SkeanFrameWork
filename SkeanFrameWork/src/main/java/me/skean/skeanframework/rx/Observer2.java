
package me.skean.skeanframework.rx;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public interface Observer2<T> extends Observer<T> {

    void onSubscribe2(@NonNull Disposable d);

    void onNext2(@NonNull T t);

    void onError2(@NonNull Throwable e);

    void onComplete2();

}
