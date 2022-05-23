package me.skean.skeanframework.ktext

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.fragment.app.Fragment
import com.dylanc.activityresult.launcher.BaseActivityResultLauncher
import me.skean.skeanframework.utils.ActivityLifeDelegate
import me.skean.skeanframework.utils.FragmentLifeDelegate
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Created by Skean on 2022/5/16.
 */

class LauncherDelegate<T : BaseActivityResultLauncher<*, *>>(
    clazz: Class<T>,
    caller: ActivityResultCaller
) : ReadOnlyProperty<ActivityResultCaller, T> {
    private var launcher = clazz.getConstructor(ActivityResultCaller::class.java).newInstance(caller)

    override fun getValue(thisRef: ActivityResultCaller, property: KProperty<*>): T = launcher
}

inline fun <reified T : BaseActivityResultLauncher<*, *>> ComponentActivity.injectLauncher() = LauncherDelegate(T::class.java, this)

inline fun <reified T : BaseActivityResultLauncher<*, *>> Fragment.injectLauncher() = LauncherDelegate(T::class.java, this)


inline fun <reified T : Any> ComponentActivity.autoInject(
    vararg parameters: Pair<String, Any?>,
    noinline construct: (activity: Activity, parameters: Map<String, Any?>) -> T? = { _, _ -> null },
    noinline init: (T) -> Unit = {}
) = ActivityLifeDelegate(T::class.java, this, parameters, construct, init)

inline fun <reified T : Any> Fragment.autoInject(
    vararg parameters: Pair<String, Any?>,
    noinline construct: (fragment: Fragment, parameters: Map<String, Any?>) -> T? = { _, _ -> null },
    noinline init: (T) -> Unit = {}
) = FragmentLifeDelegate(T::class.java, this, parameters, construct, init)
