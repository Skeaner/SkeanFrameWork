package me.skean.skeanframework.ktext

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.fragment.app.Fragment
import com.dylanc.activityresult.launcher.BaseActivityResultLauncher
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

inline fun <reified T : BaseActivityResultLauncher<*, *>> ComponentActivity.injectLauncher() = LauncherDelegate(T::class.java, this)

inline fun <reified T : BaseActivityResultLauncher<*, *>> Fragment.injectLauncher() = LauncherDelegate(T::class.java, this)

class LauncherDelegate<T : BaseActivityResultLauncher<*, *>>(
    clazz: Class<T>,
    caller: ActivityResultCaller
) : ReadOnlyProperty<ActivityResultCaller, T> {
    private var launcher = clazz.getConstructor(ActivityResultCaller::class.java).newInstance(caller)

    override fun getValue(thisRef: ActivityResultCaller, property: KProperty<*>) = launcher

}