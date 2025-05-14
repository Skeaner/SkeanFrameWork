@file:JvmName("PermissionExt")

package me.skean.skeanframework.ktext

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.PermissionUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import me.skean.skeanframework.rx.DefaultObserver
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



//inline fun FragmentActivity.requestPermissionEachCombined(vararg permissions: String, crossinline onGranted: () -> Unit) {
//    val rxPermissions = RxPermissions(this)
//    rxPermissions.requestEachCombined(*permissions).subscribe(object : DefaultObserver<Permission>() {
//        override fun onNext2(p: Permission) {
//            if (p.granted) {
//                onGranted.invoke()
//            } else if (p.shouldShowRequestPermissionRationale) {
//                EasyPermissionDialog.build(this@requestPermissionEachCombined).permissions(*permissions).typeTemporaryDeny {
//                    if (it) rxPermissions.requestEachCombined(*permissions).subscribe(this)
//                }.show()
//            } else {
//                EasyPermissionDialog.build(this@requestPermissionEachCombined).permissions(*permissions).typeNeverAsk {
//                    if (it) onGranted.invoke()
//                }.show()
//            }
//        }
//    })
//}
//
//
//inline fun Fragment.requestPermissionEachCombined(vararg permissions: String, crossinline onGranted: () -> Unit) {
//    val rxPermissions = RxPermissions(this)
//    rxPermissions.requestEachCombined(*permissions).subscribe(object : DefaultObserver<Permission>() {
//        override fun onNext2(p: Permission) {
//            if (p.granted) {
//                onGranted.invoke()
//            } else if (p.shouldShowRequestPermissionRationale) {
//                EasyPermissionDialog.build(this@requestPermissionEachCombined).permissions(*permissions).typeTemporaryDeny {
//                    if (it) rxPermissions.requestEachCombined(*permissions).subscribe(this)
//                }.show()
//            } else {
//                EasyPermissionDialog.build(this@requestPermissionEachCombined).permissions(*permissions).typeNeverAsk {
//                    if (it) onGranted.invoke()
//                }.show()
//            }
//        }
//    })
//}
//
//inline fun FragmentActivity.requestPermissionEach(vararg permissions: String, crossinline onGranted: () -> Unit) {
//    val rxPermissions = RxPermissions(this)
//    rxPermissions.requestEach(*permissions).subscribe(object : DefaultObserver<Permission>() {
//        override fun onNext2(p: Permission) {
//            if (p.granted) {
//                if (PermissionUtils.hasSelfPermissions(this@requestPermissionEach, *permissions)) {
//                    onGranted.invoke()
//                }
//            } else if (p.shouldShowRequestPermissionRationale) {
//                EasyPermissionDialog.build(this@requestPermissionEach).permissions(*permissions).typeTemporaryDeny {
//                    if (it) rxPermissions.requestEach(*permissions).subscribe(this)
//                }.show()
//            } else {
//                EasyPermissionDialog.build(this@requestPermissionEach).permissions(*permissions).typeNeverAsk {
//                    if (it) onGranted.invoke()
//                }.show()
//            }
//        }
//    })
//}
//
//inline fun Fragment.requestPermissionEach(vararg permissions: String, crossinline onGranted: () -> Unit) {
//    val rxPermissions = RxPermissions(this)
//    rxPermissions.requestEach(*permissions).subscribe(object : DefaultObserver<Permission>() {
//        override fun onNext2(p: Permission) {
//            if (p.granted) {
//                if (PermissionUtils.hasSelfPermissions(this@requestPermissionEach.requireContext(), *permissions)) {
//                    onGranted.invoke()
//                }
//            } else if (p.shouldShowRequestPermissionRationale) {
//                EasyPermissionDialog.build(this@requestPermissionEach).permissions(*permissions).typeTemporaryDeny {
//                    if (it) rxPermissions.requestEach(*permissions).subscribe(this)
//                }.show()
//            } else {
//                EasyPermissionDialog.build(this@requestPermissionEach).permissions(*permissions).typeNeverAsk {
//                    if (it) onGranted.invoke()
//                }.show()
//            }
//        }
//    })
//}