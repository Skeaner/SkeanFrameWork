package me.skean.skeanframework.utils

import android.app.Activity
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.dylanc.activityresult.launcher.BaseActivityResultLauncher
import com.hi.dhl.binding.LifecycleFragment
import com.hi.dhl.binding.observerWhenDestroyed
import me.skean.skeanframework.ktext.observe
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


class ActivityLifeDelegate<T : Any>(
    classes: Class<T>,
    activity: Activity
) : Lazy<T> {

    override val value: T
        get() = launcher
    private lateinit var launcher: T

    init {
        when (activity) {
            is ComponentActivity -> activity.lifecycle.observe(create = {
                launcher = classes.getConstructor(Activity::class.java).newInstance(activity)
            })
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    activity.observe(create = {
                        launcher = classes.getConstructor(Activity::class.java).newInstance(activity)
                    })
                }
            }
        }
    }

    override fun isInitialized(): Boolean = this::launcher.isInitialized
}