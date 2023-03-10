package me.skean.skeanframework.utils;

import android.app.Activity;
import android.content.Context;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.MetaDataUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.pgyer.pgyersdk.PgyerSDKManager;
import com.pgyer.pgyersdk.callback.CheckoutCallBack;
import com.pgyer.pgyersdk.model.CheckSoftModel;

import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import me.skean.skeanframework.component.UpdateDialog;
import me.skean.skeanframework.net.FileIOApi;
import me.skean.skeanframework.net.pgy.PgyerApi;
import me.skean.skeanframework.net.pgy.PgyerAppInfo;
import me.skean.skeanframework.net.pgy.PgyerResult;
import me.skean.skeanframework.rx.DefaultSingleObserver;

/**
 * Created by Skean on 2023/2/28.
 */
public class UpdateUtils {
    public static void checkUpdateByPgyerSdk(Activity activity) {
        PgyerSDKManager.checkVersionUpdate(activity, new CheckoutCallBack() {
            @Override
            public void onNewVersionExist(CheckSoftModel model) {
                //检查版本成功（有新版本）
                /**
                 *   CheckSoftModel 参数介绍
                 *
                 *    private int buildBuildVersion;//蒲公英生成的用于区分历史版本的build号
                 *     private String forceUpdateVersion;//强制更新版本号（未设置强置更新默认为空）
                 *     private String forceUpdateVersionNo;//强制更新的版本编号
                 *     private boolean needForceUpdate;//	是否强制更新
                 *     private boolean buildHaveNewVersion;//是否有新版本
                 *     private String downloadURL;//应用安装地址
                 *     private String buildVersionNo;//上传包的版本编号，默认为1 (即编译的版本号，一般来说，编译一次会
                 *    变动一次这个版本号, 在 Android 上叫 Version Code。对于 iOS 来说，是字符串类型；对于 Android 来
                 *    说是一个整数。例如：1001，28等。)
                 *     private String buildVersion;//版本号, 默认为1.0 (是应用向用户宣传时候用到的标识，例如：1.1、8.2.1等。)
                 *     private String buildShortcutUrl;//	应用短链接
                 *     private String buildUpdateDescription;//	应用更新说明
                 */
                UpdateDialog.show(activity,
                                  model.getBuildVersion(),
                                  model.getBuildUpdateDescription(),
                                  model.getDownloadURL(),
                                  model.isNeedForceUpdate());
            }

            @Override
            public void onNonentityVersionExist(String s) {

            }

            @Override
            public void onFail(String s) {
                ToastUtils.showShort("检查APP更新失败: " + s);
            }
        });
    }

    public static void checkUpdateByPgyerApi(Context context) {
        String appId = AppUtils.getAppPackageName();
        String apiKey = MetaDataUtils.getMetaDataInApp("PGYER_API_KEY");
        String appKey = MetaDataUtils.getMetaDataInApp("PGYER_APP_KEY");
        String appVersionName = AppUtils.getAppVersionName();
        int appVersionCode = AppUtils.getAppVersionCode();
        NetworkUtil.createService(PgyerApi.class)
                   .getAppInfo(appKey, apiKey)
                   .subscribeOn(Schedulers.io())
                   .map(r -> {
                       if (r.getCode() == 0) {
                           if (r.getData() == null) {
                               throw new RuntimeException("PGYER返回APP信息为空");
                           }
                           else if (!appId.equals(r.getData().getBuildIdentifier())) {
                               throw new RuntimeException("PGYER配置的信息跟APP不一致!");
                           }
                       }
                       else {
                           throw new RuntimeException(r.getMessage());
                       }
                       return r;
                   })
                   .flatMap(r -> NetworkUtil.createService(PgyerApi.class).checkUpdate(appKey, apiKey, appVersionName, appVersionCode))
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(new DefaultSingleObserver<PgyerResult<PgyerAppInfo>>() {
                       @Override
                       public void onSuccess2(PgyerResult<PgyerAppInfo> result) {
                           if (result.getCode() == 0) {
                               if (result.getData() != null) {
                                   PgyerAppInfo info = result.getData();
                                   if (appVersionName.compareTo(info.getBuildVersion()) < 0 || appVersionCode < Integer.parseInt(info.getBuildVersionNo())) {
                                       UpdateDialog.show(context,
                                                         info.getBuildVersion(),
                                                         info.getBuildUpdateDescription(),
                                                         info.getDownloadURL(),
                                                         info.getNeedForceUpdate());
                                   }
                               }
                           }
                           else {
                               ToastUtils.showShort("检查APP更新失败: " + result.getMessage());
                           }
                       }

                       @Override
                       public void onError2(Throwable e) {
                           super.onError2(e);
                           ToastUtils.showShort("检查APP更新失败: " + e.getLocalizedMessage());

                       }
                   });
    }
}
