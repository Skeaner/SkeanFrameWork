package me.skean.skeanframework.rx;

import android.content.Context;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import me.skean.skeanframework.widget.LoadingDialog2;

/**
 * 自动显示进度Dialog的Observer
 */
public abstract class ProgressObserver<T> implements Observer2<T> {

    private Context context;
    private String message = "请稍候";
    private boolean cancelable = true;
    private Disposable disposable;
    private DisposableWrapper disposableWrapper;
    private LoadingDialog2 loadingDialog;

    private boolean hasNext = false;
    private boolean hasComplete = false;

    public boolean isHasNext() {
        return hasNext;
    }

    public boolean isComplete() {
        return hasComplete;
    }

    public ProgressObserver(Context context) {
        this.context = context;
    }

    public ProgressObserver(Context context, boolean cancelable) {
        this.context = context;
        this.cancelable = cancelable;
    }

    public ProgressObserver(Context context, boolean cancelable, String message) {
        this.context = context;
        this.message = message;
        this.cancelable = cancelable;
    }

    @Override
    public final void onSubscribe(@NonNull Disposable d) {
        disposable = d;
        disposableWrapper = new DisposableWrapper();
        onSubscribe2(disposableWrapper);
        loadingDialog = LoadingDialog2.show(context, message, cancelable, dialog -> {
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
            disposable = null;
        });
    }

    @Override
    public final void onNext(@NonNull T t) {
        hasNext = true;
        onNext2(t);
    }

    @Override
    public final void onError(@NonNull Throwable e) {
        e.printStackTrace();
        loadingDialog.dismiss();
        onError2(e);
    }

    @Override
    public final void onComplete() {
        hasComplete = true;
        disposable = null;
        loadingDialog.dismiss();
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

    /**
     * Disposable包装类, 以便正确关闭ProgressDialog
     */
    public class DisposableWrapper implements Disposable {

        @Override
        public void dispose() {
            if (disposable != null) {
                disposable.dispose();
            }
            disposable = null;
            if (loadingDialog != null) loadingDialog.dismiss();
        }

        @Override
        public boolean isDisposed() {
            return disposable == null || disposable.isDisposed();
        }
    }
}
