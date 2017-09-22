package skean.me.base.rx;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import skean.me.base.widget.LoadingDialog2;

/**
 * 自动显示进度Dialog的Observer
 */
public abstract class ProgressObserver<T> implements Observer<T> {

    private Context context;
    private String message = "请稍候";
    private boolean cancelable = true;
    private Disposable disposable;
    protected DisposableWrapper disposableWrapper;
    private LoadingDialog2 loadingDialog;

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


    /**
     * @deprecated 为了正确地关闭ProgressDialog, 请使用 {@link #onSubscribe2(DisposableWrapper)} 代替
     */
    @Override
    @Deprecated
    public void onSubscribe(@NonNull Disposable d) {
        disposable = d;
        disposableWrapper = new DisposableWrapper();
        onSubscribe2(disposableWrapper);
        loadingDialog = LoadingDialog2.show(context, message, cancelable, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (disposable != null && !disposable.isDisposed()) {
                    disposable.dispose();
                }
                disposable = null;
            }
        });
    }

    /**
     * 参加 {@link #onSubscribe(Disposable)}的方法说明
     */
    public void onSubscribe2(@NonNull DisposableWrapper d) {
    }

    @Override
    public abstract void onNext(@NonNull T t);

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
        loadingDialog.dismiss();
    }

    @Override
    public void onComplete() {
        disposable = null;
        loadingDialog.dismiss();
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
