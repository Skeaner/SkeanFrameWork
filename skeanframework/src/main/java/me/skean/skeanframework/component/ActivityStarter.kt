package me.skean.skeanframework.component

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import java.lang.RuntimeException

/**
 * Activity启动器的伴生对象启动器
 */
open class ActivityStarter {
    open fun start(host: Any, intent: Intent, requestCode: Int?) {
        checkHost(host)
        if (requestCode == null) {
            if (host is Activity) host.startActivity(intent)
            else if (host is Fragment) host.startActivity(intent)
        }
        else {
            if (host is Activity) host.startActivityForResult(intent, requestCode)
            else if (host is Fragment) host.startActivityForResult(intent, requestCode)
        }
    }

    open fun checkHost(host: Any) {
        if (!(host is Activity || host is Fragment)) {
            throw  RuntimeException("host只能是Activity或者Fragment")
        }
    }

    open fun getContextFromHost(host: Any): Context {
        return when (host) {
            is Activity -> host
            is Fragment -> host.requireContext()
            else -> throw  RuntimeException("host只能是Activity或者Fragment")
        }
    }
}