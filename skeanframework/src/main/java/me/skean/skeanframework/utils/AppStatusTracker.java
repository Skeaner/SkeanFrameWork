package me.skean.skeanframework.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.HashSet;
import java.util.Set;

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

    private Set<String> createdActivitiesNames = new HashSet<>();

    public interface StatusCallback {
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
        createdActivitiesNames.add(activity.getClass().getName());
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
        createdActivitiesNames.remove(activity.getClass().getName());
    }

    public StatusCallback getStatusCallback() {
        return statusCallback;
    }

    public void setStatusCallback(StatusCallback statusCallback) {
        this.statusCallback = statusCallback;
    }

    public boolean isActivityCreated(String clazzName) {
        return createdActivitiesNames.contains(clazzName);
    }
}
