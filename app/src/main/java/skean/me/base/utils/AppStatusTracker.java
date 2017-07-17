package skean.me.base.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by Jimmy on 2017/5/11.
 */

public class AppStatusTracker implements Application.ActivityLifecycleCallbacks {
    private static AppStatusTracker tracker;
    private Application application;
    private boolean isForground;
    private int activeCount;
    private long timestamp;
    private StatusCallback statusCallback;

    private interface StatusCallback {
        void onToForeground();

        void onToBackground();
    }

    private AppStatusTracker(Application application) {
        this.application = application;
        application.registerActivityLifecycleCallbacks(this);
    }

    public static void init(Application application) {
        tracker = new AppStatusTracker(application);
    }

    public static AppStatusTracker getInstance() {
        return tracker;
    }

    public boolean isForground() {
        return isForground;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (activeCount == 0) {
            timestamp = System.currentTimeMillis();
        }
        activeCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (!isForground && statusCallback != null) statusCallback.onToForeground();
        isForground = true;
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        activeCount--;
        if (activeCount == 0) {
            if (isForground && statusCallback != null) statusCallback.onToBackground();
            isForground = false;
            timestamp = System.currentTimeMillis() - timestamp;
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public StatusCallback getStatusCallback() {
        return statusCallback;
    }

    public void setStatusCallback(StatusCallback statusCallback) {
        this.statusCallback = statusCallback;
    }
}
