package me.skean.skeanframework.rx;

import android.content.Context;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import me.skean.skeanframework.widget.LoadingDialog2;

/**
 * 自动显示进度Dialog的SingleObserver
 */
public abstract class ProgressSingleObserver<T> implements SingleObserver2<T> {

    private Context context;
    private String message = "请稍候";
    private boolean cancelable = true;
    private Disposable disposable;
    private DisposableWrapper disposableWrapper;
    private LoadingDialog2 loadingDialog;



    public ProgressSingleObserver(Context context) {
        this.context = context;
    }

    public ProgressSingleObserver(Context context, boolean cancelable) {
        this.context = context;
        this.cancelable = cancelable;
    }

    public ProgressSingleObserver(Context context, boolean cancelable, String message) {
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
    public final void onSuccess(T t) {
        disposable = null;
        loadingDialog.dismiss();
        onSuccess2(t);
    }


    @Override
    public final void onError(@NonNull Throwable e) {
        e.printStackTrace();
        loadingDialog.dismiss();
        onError2(e);
    }


    @Override
    public void onSubscribe2(Disposable d) {
    }

    @Override
    public void onError2(Throwable e) {
    }

    @Override
    public void onSuccess2(T t) {
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
