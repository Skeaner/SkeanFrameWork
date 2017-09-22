package skean.me.base.rx;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.annotations.NonNull;
import skean.me.base.widget.LoadingDialog2;

/**
 * 自动显示进度Dialog的Subscriber
 */
public abstract class ProgressSubscriber<T> implements Subscriber<T> {

    private Context context;
    private String message = "请稍候";
    private boolean cancelable = true;
    private Subscription subscription;
    protected SubscriptionWrapper subscriptionWrapper;
    private LoadingDialog2 LoadingDialog;

    public ProgressSubscriber(Context context) {
        this.context = context;
    }

    public ProgressSubscriber(Context context, boolean cancelable) {
        this.context = context;
        this.cancelable = cancelable;
    }

    public ProgressSubscriber(Context context, boolean cancelable, String message) {
        this.context = context;
        this.message = message;
        this.cancelable = cancelable;
    }


    /**
     * @deprecated 为了正确地关闭ProgressDialog, 请使用 {@link #onSubscribe2(SubscriptionWrapper)} 代替
     */
    @Override
    @Deprecated
    public void onSubscribe(Subscription s) {
        subscription = s;
        subscriptionWrapper = new SubscriptionWrapper();
        onSubscribe2(subscriptionWrapper);
        LoadingDialog = LoadingDialog2.show(context, message, cancelable, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (subscription != null) {
                    subscription.cancel();
                }
                subscription = null;
            }
        });
    }

    /**
     * 参加 {@link #onSubscribe(Subscription)} 的方法说明
     */
    public void onSubscribe2(@NonNull SubscriptionWrapper s) {
    }

    @Override
    public abstract void onNext(@NonNull T t);

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
        LoadingDialog.dismiss();
    }

    @Override
    public void onComplete() {
        subscription = null;
        LoadingDialog.dismiss();
    }

    /**
     * Subscription包装类, 以便正确关闭ProgressDialog
     */
    public class SubscriptionWrapper implements Subscription {

        @Override
        public void request(long n) {
            if (subscription != null) subscription.request(n);
        }

        @Override
        public void cancel() {
            if (subscription != null) {
                subscription.cancel();
            }
            subscription = null;
            if (LoadingDialog != null) LoadingDialog.dismiss();
        }
    }
}
