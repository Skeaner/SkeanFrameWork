package me.skean.skeanframework.ktext

import android.app.Activity
import com.dylanc.activityresult.launcher.BaseActivityResultLauncher
import me.skean.skeanframework.utils.ActivityLauncherDelegate

inline fun <reified T : BaseActivityResultLauncher<*, *>> Activity.launcher() =
    ActivityLauncherDelegate(T::class.java, this)

//inline fun <reified T : ViewBinding> AppCompatActivity.viewbind() =
//    ActivityViewBinding(T::class.java, this)
//
//inline fun <reified T : ViewBinding> FragmentActivity.viewbind() =
//    ActivityViewBinding(T::class.java, this)

//inline fun <reified T : ViewBinding> Fragment.viewbind() =
//    FragmentViewBinding(T::class.java, this)