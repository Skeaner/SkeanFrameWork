package me.skean.skeanframework.rx;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.trello.rxlifecycle3.RxLifecycle;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.subjects.BehaviorSubject;

public class RxService extends Service implements ServiceLifecycleProvider {

    private final BehaviorSubject<ServiceEvent> lifecycleSubject = BehaviorSubject.create();

    @Override
    public final Observable<ServiceEvent> lifecycle() {
        return lifecycleSubject.toFlowable(BackpressureStrategy.LATEST).toObservable();
    }

    @Override
    public final <T> ObservableTransformer<? super T, ? extends T> bindUntilEvent(ServiceEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    public final <T> ObservableTransformer<? super T, ? extends T> bindToLifecycle() {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, ServiceEvent.DESTROY);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        lifecycleSubject.onNext(ServiceEvent.CREATE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        lifecycleSubject.onNext(ServiceEvent.BIND);
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        lifecycleSubject.onNext(ServiceEvent.START_COMMAND);
        return result;
    }

    @Override
    public void onDestroy() {
        lifecycleSubject.onNext(ServiceEvent.DESTROY);
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        lifecycleSubject.onNext(ServiceEvent.UNBIND);
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        lifecycleSubject.onNext(ServiceEvent.REBIND);
    }
}