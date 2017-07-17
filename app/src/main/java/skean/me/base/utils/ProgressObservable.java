package skean.me.base.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;
import skean.me.base.widget.LoadingDialog2;

public class ProgressObservable {
    /**
     * @param source     the source observable you are going to observe
     * @param context    used for creating the progress dialog
     * @param message    for the dialog
     * @param cancelable for the dialog
     * @return an Observable<T> that will present a progress UI while it is being subscribed to.
     * Dismissing the dialog will also unsubscribe the subscriber. When the observable finishes the
     * dialog will be dismissed automatically.
     */

    public static <T> Observable<T> fromObservable(final Observable<T> source,
                                                   final Context context,
                                                   final String message,
                                                   final boolean cancelable) {
        Observable.OnSubscribe<T> wrappedSubscription = new Observable.OnSubscribe<T>() {

            @Override
            public void call(final Subscriber<? super T> subscriber) {
                final LoadingDialog2 ld;
                ld = LoadingDialog2.show(context, message, cancelable, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        subscriber.unsubscribe();
                    }
                });
                Subscription progressSubscription = Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        ld.dismiss();
                    }
                });
                subscriber.add(progressSubscription);
                source.subscribe(subscriber);

            }
        };
        return Observable.unsafeCreate(wrappedSubscription);
    }

    public static <T> Observable<T> fromObservable(final Observable<T> source, final Context context) {
        return fromObservable(source, context, "请稍后", false);
    }


}