@file:JvmName("PermissionExt")

package me.skean.skeanframework.ktext

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import skean.yzsm.com.easypermissiondialog.EasyPermissionDialog

/**
 * Created by Skean on 22/2/27.
 */


fun XXPermissions.request(onGranted: (Boolean) -> Unit, onDenied: () -> Unit = {}, onNever: () -> Unit = {}) {
    this.request(object : OnPermissionCallback {
        override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
            onGranted(allGranted)
        }

        override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
            if (doNotAskAgain) onNever() else onDenied()
        }


    })
}


inline fun FragmentActivity.requestPermissionCombined(vararg permissions: String, crossinline onGranted: () -> Unit) {
    val xxPermissions = XXPermissions.with(this).permission(permissions)
    xxPermissions.request(object : OnPermissionCallback {
        override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
            if (allGranted) onGranted()
        }

        override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
            EasyPermissionDialog.build(this@requestPermissionCombined).permissions(permissions).show(doNotAskAgain) {
                if (it) xxPermissions.request(this)
            }
        }
    })
}


inline fun Fragment.requestPermissionCombined(vararg permissions: String, crossinline onGranted: () -> Unit) {
    val xxPermissions = XXPermissions.with(this).permission(permissions)
    xxPermissions.request(object : OnPermissionCallback {
        override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
            if (allGranted) onGranted()
        }

        override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
            EasyPermissionDialog.build(this@requestPermissionCombined).permissions(permissions).show(doNotAskAgain) {
                if (it) xxPermissions.request(this)
            }
        }
    })
}
