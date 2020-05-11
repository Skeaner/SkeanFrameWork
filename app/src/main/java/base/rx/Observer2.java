
package base.rx;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public interface Observer2<T> extends Observer<T> {

    void onSubscribe2(@NonNull Disposable d);

    void onNext2(@NonNull T t);

    void onError2(@NonNull Throwable e);

    void onComplete2();

}
