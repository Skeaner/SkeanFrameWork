@file:JvmName("AndroidExt")

package me.skean.skeanframework.ktext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.MetaDataUtils
import com.blankj.utilcode.util.ToastUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import me.skean.skeanframework.R
import me.skean.skeanframework.component.function.UpdateDialog
import me.skean.skeanframework.net.pgy.PgyerApi
import me.skean.skeanframework.utils.NetworkUtil

/**
 * Created by Skean on 2022/4/27.
 */
inline fun <reified T : Activity> Activity.intent(): Intent {
    return Intent(this, T::class.java)
}

inline fun <reified T : Activity> Fragment.intent(): Intent {
    return Intent(requireContext(), T::class.java)
}


fun AppCompatActivity.initActionBar(
    toolbar: Toolbar?,
    showUp: Boolean = true,
    showTitle: Boolean = false,
    upAction: () -> Unit = { finish() }
) {
    this.setSupportActionBar(toolbar)
    toolbar?.setNavigationOnClickListener {
        upAction()
    }
    supportActionBar?.let {
        onSupportNavigateUp()
        it.setDisplayHomeAsUpEnabled(showUp)
        it.setDisplayShowTitleEnabled(showTitle)
    }
}

fun Context.checkUpdateByPgyerApi() {
    val appId = AppUtils.getAppPackageName()
    val apiKey = MetaDataUtils.getMetaDataInApp("PGYER_API_KEY")
    val appKey = MetaDataUtils.getMetaDataInApp("PGYER_APP_KEY")
    val appVersionName = AppUtils.getAppVersionName()
    val appVersionCode = AppUtils.getAppVersionCode()
    NetworkUtil.createService<PgyerApi>()
        .getAppInfo(appKey, apiKey)
        .subscribeOn(Schedulers.io())
        .map {
            if (it.code == 0) {
                if (it.data == null) {
                    throw RuntimeException("PGYER返回APP信息为空");
                } else {
                    if (appId != it.data.buildIdentifier) {
                        throw RuntimeException("PGYER配置的信息跟APP不一致!")
                    }
                }
            } else {
                throw RuntimeException(it.message)
            }
        }
        .flatMap { NetworkUtil.createService<PgyerApi>().checkUpdate(appKey, apiKey, appVersionName, appVersionCode) }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(defaultSingleObserver(onError2 = {
            ToastUtils.showShort("检查APP更新失败: ${it.localizedMessage}")
        }) { result ->
            if (result.code == 0) {
                result.data?.let {
                    if (appVersionName < it.buildVersion || appVersionCode < it.buildVersionNo.toInt()) {
                        UpdateDialog.show(
                            this@checkUpdateByPgyerApi,
                            it.buildVersion,
                            it.buildUpdateDescription,
                            it.downloadURL,
                            it.needForceUpdate
                        )
                    }
                }
            } else {
                ToastUtils.showShort("检查APP更新失败: ${result.message}")
            }
        })
}

fun Fragment.findParentNavController() = NavHostFragment.findNavController(requireParentFragment().requireParentFragment())

fun Fragment.findChildNavController(@IdRes navHostFragmentId: Int) =
    (childFragmentManager.findFragmentById(navHostFragmentId) as NavHostFragment).navController


fun AppCompatActivity.findNavController(@IdRes navHostFragmentId: Int) =
    (supportFragmentManager.findFragmentById(navHostFragmentId) as NavHostFragment).navController

fun AppCompatActivity.findNavController(v: FragmentContainerView) = v.getFragment<NavHostFragment>().navController


