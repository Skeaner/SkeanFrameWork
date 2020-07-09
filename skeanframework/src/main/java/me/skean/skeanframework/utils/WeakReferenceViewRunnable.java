package me.skean.skeanframework.utils;

import android.view.View;

import java.lang.ref.WeakReference;

/**
 * 对View的弱引用Runnable
 */
public abstract class WeakReferenceViewRunnable implements Runnable {
    WeakReference<View> ref;

    public WeakReferenceViewRunnable(View targetView) {
        ref = new WeakReference<>(targetView);
    }

    public View getView() {
        return ref.get();
    }
}
