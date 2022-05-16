package me.skean.skeanframework.ktext

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Fragment
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.BindingLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

@JvmOverloads
fun Lifecycle.observe(
    create: (() -> Unit)? = null,
    start: (() -> Unit)? = null,
    resume: (() -> Unit)? = null,
    pause: (() -> Unit)? = null,
    stop: (() -> Unit)? = null,
    destroyed: (() -> Unit)? = null
) {
    addObserver(LifecycleObserver(lifecycle = this, create, start, resume, pause, stop, destroyed))
}

class LifecycleObserver(
    var lifecycle: Lifecycle?,
    var create: (() -> Unit)?,
    var start: (() -> Unit)?,
    var resume: (() -> Unit)?,
    var pause: (() -> Unit)?,
    var stop: (() -> Unit)?,
    var destroyed: (() -> Unit)?
) : BindingLifecycleObserver() {

    override fun onCreate(owner: LifecycleOwner) {
        create?.invoke()
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        start?.invoke()
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        resume?.invoke()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        pause?.invoke()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        stop?.invoke()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        destroyed?.invoke()
        lifecycle?.apply {
            removeObserver(this@LifecycleObserver)
            lifecycle = null
        }
        create = null
        start = null
        resume = null
        pause = null
        stop = null
        destroyed = null
    }
}

fun Activity.observe(
    create: (() -> Unit)? = null,
    start: (() -> Unit)? = null,
    resume: (() -> Unit)? = null,
    pause: (() -> Unit)? = null,
    stop: (() -> Unit)? = null,
    destroyed: (() -> Unit)? = null
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        registerActivityLifecycleCallbacks(LifecycleCallbacks(create, start, resume, pause, stop, destroyed))
    }
}

class LifecycleCallbacks(
    var create: (() -> Unit)?,
    var start: (() -> Unit)?,
    var resume: (() -> Unit)?,
    var pause: (() -> Unit)?,
    var stop: (() -> Unit)?,
    var destroyed: (() -> Unit)?
) :
    Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        create?.invoke()
    }

    override fun onActivityStarted(activity: Activity) {
        start?.invoke()
    }

    override fun onActivityResumed(activity: Activity) {
        resume?.invoke()
    }

    override fun onActivityPaused(activity: Activity) {
        pause?.invoke()
    }

    override fun onActivityStopped(activity: Activity) {
        stop?.invoke()
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        destroyed?.invoke()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activity.unregisterActivityLifecycleCallbacks(this)
        }
        create = null
        start = null
        resume = null
        pause = null
        stop = null
        destroyed = null
    }
}
