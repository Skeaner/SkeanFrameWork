package me.skean.skeanframework.ktext

import androidx.fragment.app.FragmentActivity
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import me.skean.skeanframework.rx.DefaultObserver
import skean.yzsm.com.easypermissiondialog.EasyPermissionDialog

/**
 * Created by Skean on 22/2/27.
 */

inline fun FragmentActivity.requestPermissionEachCombined(vararg permissions: String,
                                                          crossinline onGranted: () -> Unit = { },
                                                          crossinline onAllow: () -> Unit = { }) {
    val rxPermissions = RxPermissions(this)
    rxPermissions.requestEachCombined(*permissions)
            .subscribe(object : DefaultObserver<Permission>() {
                override fun onNext2(p: Permission) {
                    if (p.granted) {
                        onGranted.invoke()
                    } else if (p.shouldShowRequestPermissionRationale) {
                        EasyPermissionDialog.build(this@requestPermissionEachCombined).permissions(*permissions).typeTemporaryDeny {
                            if (it) onAllow.invoke()
                        }.show()
                    } else {
                        EasyPermissionDialog.build(this@requestPermissionEachCombined).permissions(*permissions).typeNeverAsk {
                            if (it) onAllow.invoke()
                        }.show()
                    }
                }
            })
}