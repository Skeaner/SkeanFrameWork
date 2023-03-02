package me.skean.skeanframework.ktext

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.MetaDataUtils
import com.blankj.utilcode.util.ToastUtils
import com.pgyer.pgyersdk.PgyerSDKManager
import com.pgyer.pgyersdk.callback.CheckoutCallBack
import com.pgyer.pgyersdk.model.CheckSoftModel
import me.skean.skeanframework.component.UpdateDialog
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

fun Activity.checkUpdateByPgyerSdk() {
    PgyerSDKManager.checkVersionUpdate(this, object : CheckoutCallBack {
        override fun onNewVersionExist(model: CheckSoftModel?) {
            //检查版本成功（有新版本）
            /**
             *   CheckSoftModel 参数介绍
             *
             *    private int buildBuildVersion;//蒲公英生成的用于区分历史版本的build号
             *     private String forceUpdateVersion;//强制更新版本号（未设置强置更新默认为空）
             *     private String forceUpdateVersionNo;//强制更新的版本编号
             *     private boolean needForceUpdate;//    是否强制更新
             *     private boolean buildHaveNewVersion;//是否有新版本
             *     private String downloadURL;//应用安装地址
             *     private String buildVersionNo;//上传包的版本编号，默认为1 (即编译的版本号，一般来说，编译一次会
             *    变动一次这个版本号, 在 Android 上叫 Version Code。对于 iOS 来说，是字符串类型；对于 Android 来
             *    说是一个整数。例如：1001，28等。)
             *     private String buildVersion;//版本号, 默认为1.0 (是应用向用户宣传时候用到的标识，例如：1.1、8.2.1等。)
             *     private String buildShortcutUrl;//    应用短链接
             *     private String buildUpdateDescription;//    应用更新说明
             */
            UpdateDialog.show(
                this@checkUpdateByPgyerSdk,
                model?.buildVersion,
                model?.buildUpdateDescription,
                model?.downloadURL,
                model?.isNeedForceUpdate ?: false
            )
        }

        override fun onNonentityVersionExist(msg: String?) {
        }

        override fun onFail(msg: String?) {
            ToastUtils.showShort("检查APP更新失败: $msg")
        }

    })
}

fun Context.checkUpdateByPgyerApi() {
    val apiKey = MetaDataUtils.getMetaDataInApp("PGYER_API_KEY")
    val appKey = MetaDataUtils.getMetaDataInApp("PGYER_APP_KEY")
    val appVersionName = AppUtils.getAppVersionName()
    val appVersionCode = AppUtils.getAppVersionCode()
    NetworkUtil.createService<PgyerApi>()
        .checkUpdate(appKey, apiKey, appVersionName, appVersionCode)
        .subscribeOnIoObserveOnMainThread()
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
