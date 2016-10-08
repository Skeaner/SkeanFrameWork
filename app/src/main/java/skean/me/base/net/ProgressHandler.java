package skean.me.base.net;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import com.badoo.mobile.util.WeakHandler;

/**
 *
 */
public class ProgressHandler extends WeakHandler {

    public static final int MSG_DONE = 1;
    public static final int MSG_PROGRESS = 2;

    public static ProgressHandler newInstance(ProgressHandlerCallback callback) {
        return new ProgressHandler(Looper.getMainLooper(), callback);
    }

    private ProgressHandler(@NonNull Looper looper, Handler.Callback callback) {
        super(looper, callback);
    }

    public abstract static class ProgressHandlerCallback implements Handler.Callback {

        protected abstract void onProgress(int percent);

        protected abstract void onDone();

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PROGRESS:
                    onProgress(msg.arg1);
                    break;
                case MSG_DONE:
                    onDone();
                    break;
                default:
                    break;
            }
            return false;
        }
    }
}
