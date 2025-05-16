package me.skean.skeanframework.rx;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableTransformer;

/**
 * Common interface for all RxService extensions.
 *
 * Useful if you are writing utilities on top of rxlifecycle-components,
 * or you are implementing your own component not supported in this library.
 */
public interface ServiceLifecycleProvider {

    Observable<ServiceEvent> lifecycle();

    <T> ObservableTransformer<? super T, ? extends T> bindUntilEvent(ServiceEvent event);

    <T> ObservableTransformer<? super T, ? extends T> bindToLifecycle();
}