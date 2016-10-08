package skean.me.base.net;

import android.util.Log;

import rx.Subscriber;

/**
 * 默认的Subscriber实现
 */
public class DefaultSubscriber<T> extends Subscriber<T> {

    private static final String TAG = "DefaultSubscriber";
    private boolean hasNext = false;

    @Override
    public void onCompleted() {
        Log.i(TAG, "onCompleted");
    }

    @Override
    public void onError(Throwable e) {
        Log.i(TAG, "onError: " + e.getMessage());
        e.printStackTrace();
    }

    @Override
    public void onNext(T item) {
        hasNext = true;
    }

    public boolean getHasNext() {
        return hasNext;
    }
}
