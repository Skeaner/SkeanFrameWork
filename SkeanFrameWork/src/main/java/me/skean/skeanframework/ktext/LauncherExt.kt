package me.skean.skeanframework.ktext

import android.app.Activity
import androidx.activity.result.ActivityResultCaller
import com.dylanc.activityresult.launcher.BaseActivityResultLauncher
import me.skean.skeanframework.utils.ActivityLauncherDelegate
import me.skean.skeanframework.utils.ActivityLauncherDelegate2

inline fun <reified T : BaseActivityResultLauncher<*, *>> Activity.launcher() =
        ActivityLauncherDelegate2(T::class.java, this)

inline fun <reified T : BaseActivityResultLauncher<*, *>> Activity.launcher2(): T {
    return T::class.java.getConstructor(ActivityResultCaller::class.java).newInstance(this)
}

//inline fun <reified T : ViewBinding> AppCompatActivity.viewbind() =
//    ActivityViewBinding(T::class.java, this)
//
//inline fun <reified T : ViewBinding> FragmentActivity.viewbind() =
//    ActivityViewBinding(T::class.java, this)

//inline fun <reified T : ViewBinding> Fragment.viewbind() =
//    FragmentViewBinding(T::class.java, this)