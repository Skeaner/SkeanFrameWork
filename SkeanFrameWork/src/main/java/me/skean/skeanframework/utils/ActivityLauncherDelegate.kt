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

/**
 * <pre>
 *     author: dhl
 *     date  : 2020/12/15
 *     desc  :
 * </pre>
 */

class ActivityLauncherDelegate<T : BaseActivityResultLauncher<*, *>>(
    classes: Class<T>,
    activity: Activity
) : ReadOnlyProperty<Activity, T?> {

    private var launcher: T? = null

    init {
        when (activity) {
            is ComponentActivity -> activity.lifecycle.observe(create = {
                launcher = classes.getConstructor(ActivityResultCaller::class.java).newInstance(activity)
            })
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    activity.observe(create = {
                        launcher = classes.getConstructor(ActivityResultCaller::class.java).newInstance(activity)
                    })
                }
            }
        }

    }

    override fun getValue(thisRef: Activity, property: KProperty<*>): T? {
        return launcher
    }

}