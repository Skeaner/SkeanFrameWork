package base.rx;

import android.content.Context;

import org.reactivestreams.Subscription;

import io.reactivex.annotations.NonNull;
import base.widget.LoadingDialog2;

/**
 * 自动显示进度Dialog的Subscriber
 */
public abstract class ProgressSubscriber<T> implements Subscriber2<T> {

    private Context context;
    private String message = "请稍候";
    private boolean cancelable = true;
    private Subscription subscription;
    private SubscriptionWrapper subscriptionWrapper;
    private LoadingDialog2 LoadingDialog;

    private boolean hasNext = false;
    private boolean hasComplete = false;

    public boolean isHasNext() {
        return hasNext;
    }

    public boolean isComplete() {
        return hasComplete;
    }

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

    @Override
    public final void onSubscribe(Subscription s) {
        subscription = s;
        subscriptionWrapper = new SubscriptionWrapper();
        onSubscribe2(subscriptionWrapper);
        LoadingDialog = LoadingDialog2.show(context, message, cancelable, dialog -> {
            if (subscription != null) {
                subscription.cancel();
            }
            subscription = null;
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
        LoadingDialog.dismiss();
        onError2(e);
    }

    @Override
    public void onComplete() {
        subscription = null;
        LoadingDialog.dismiss();
        hasComplete = true;
        onComplete2();
    }

    @Override
    public void onSubscribe2(Subscription s) {
    }

    @Override
    public void onError2(Throwable t) {
    }

    @Override
    public void onComplete2() {
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
