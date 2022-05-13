package me.skean.framework.example.component

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import com.dylanc.activityresult.launcher.BaseActivityResultLauncher
import com.dylanc.activityresult.launcher.launchForFlow
import com.dylanc.activityresult.launcher.launchForResult
import com.dylanc.callbacks.Callback2

/**
 * Created by Skean on 2022/5/13.
 */
class AppActivityLauncher(caller: ActivityResultCaller) :
    BaseActivityResultLauncher<Intent, ActivityResult>(caller, ActivityResultContracts.StartActivityForResult()) {

    inline fun <reified T : Activity> launch(vararg pairs: Pair<String, *>, onActivityResult: Callback2<Int, Intent?>? = null) {
        launch(T::class.java, bundleOf(*pairs), onActivityResult)
    }

    @JvmOverloads
    fun <T : Activity> launch(clazz: Class<T>, extras: Bundle? = null, onActivityResult: Callback2<Int, Intent?>? = null) {
        val intent = Intent(context, clazz)
        extras?.let { intent.putExtras(it) }
        launch(intent, onActivityResult)
    }


    fun launch(intent: Intent, onActivityResult: Callback2<Int, Intent?>?) =
        launch(intent) { result ->
            if (onActivityResult != null) {
                onActivityResult(result.resultCode, result.data)
            }
        }


}