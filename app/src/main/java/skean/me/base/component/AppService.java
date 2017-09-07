package skean.me.base.component;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import skean.me.base.net.CommonService;
import skean.me.base.net.DefaultSubscriber;
import skean.me.base.net.PgyAppInfo;
import skean.me.base.net.PgyVersionInfo;
import skean.me.base.net.PgyerService;
import skean.me.base.net.ProgressInterceptor;
import skean.me.base.utils.FileUtil;
import skean.me.base.utils.NetworkUtil;
import skean.me.base.utils.PackageUtils;
import skean.me.base.widget.ForceUpdateDialog;
import skean.yzsm.com.framework.R;

/**
 * App的后台基础服务
 */
public final class AppService extends Service {

    public static final String TAG = "AppService";
    protected Context context;
    protected AppServiceBinder binder = new AppServiceBinder();

    AppApplication appApplication;
    NotificationManager nManager;

    int tempProgress = 0;
    public static final int DOWNLOAD_NOTICE_ID = 1;

    public static final String APK_MIME_TYPE = "application/vnd.android.package-archive";

    protected class AppVersion {
        private String appId;
        private String apiKey;

        AppVersion(String appId, String apiKey) {
            this.appId = appId;
            this.apiKey = apiKey;
        }

        public String getAppId() {
            return appId;
        }

        public String getApiKey() {
            return apiKey;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 生命周期
    ///////////////////////////////////////////////////////////////////////////

    public class AppServiceBinder extends Binder {
        public AppService getService() {
            return AppService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        context = this;
        appApplication = (AppApplication) getApplication();
        nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStart(intent, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        onHandleIntent(intent);
    }

    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            switch (intent.getAction()) {
                case IntentKey.ACTION_CHECK_UPDATE_IN_PGYER:
                    checkUpdateInPGYER(intent.getBooleanExtra(IntentKey.EXTRA_SHOW_TIPS, false));
                    break;
                case IntentKey.ACTION_DOWNLOAD_APP:
                    String url = intent.getStringExtra(IntentKey.EXTRA_DOWNLOAD_URL);
                    if (url != null) downloadApp(url);
                    else downloadApp(getAppVersion().getAppId(), getAppVersion().getApiKey());
                    break;
                default:
                    Log.i(TAG, "未知Intent: " + intent);
                    break;
            }
        }
    }

    /**
     * 获取当前应用在蒲公英的对应值
     *
     * @return 对应版本
     */
    public AppVersion getAppVersion() {
        // FIXME: 2016/9/30 返回对应的版本
        return new AppVersion("2684dd49fe2e94318d64e27e4589bf20", "521cf96c91d333da545c8176e2bbdad2");
    }

    ///////////////////////////////////////////////////////////////////////////
    // 便利启动方法
    ///////////////////////////////////////////////////////////////////////////

    public static void startCheckUpdateInPGYER(Context context, boolean showTips) {
        Intent intent = new Intent(context, AppService.class);
        intent.setAction(IntentKey.ACTION_CHECK_UPDATE_IN_PGYER);
        intent.putExtra(IntentKey.EXTRA_SHOW_TIPS, showTips);
        context.startService(intent);
    }

    public static void startDownloadApp(Context context, String url) {
        Intent intent = new Intent(context, AppService.class);
        intent.setAction(IntentKey.ACTION_DOWNLOAD_APP);
        intent.putExtra(IntentKey.EXTRA_DOWNLOAD_URL, url);
        context.startService(intent);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 更新
    ///////////////////////////////////////////////////////////////////////////

    private void checkUpdateInPGYER(final boolean showTips) {
        NetworkUtil.baseRetrofit(PgyerService.BASE_URL)
                   .create(PgyerService.class)
                   .getAppInfo(getAppVersion().getAppId(), getAppVersion().getApiKey())
                   .subscribeOn(Schedulers.io())
                   .filter(new Func1<PgyAppInfo, Boolean>() {
                       @Override
                       public Boolean call(PgyAppInfo appInfo) {
                           return appInfo.getCode() == 0;
                       }
                   })
                   .flatMap(new Func1<PgyAppInfo, Observable<PgyVersionInfo>>() {
                       @Override
                       public Observable<PgyVersionInfo> call(PgyAppInfo appInfo) {
                           return Observable.from(appInfo.getData());
                       }
                   })
                   .filter(new Func1<PgyVersionInfo, Boolean>() {
                       @Override
                       public Boolean call(PgyVersionInfo versionInfo) {
                           return "1".equals(versionInfo.getAppIsLastest())
                                   //
                                   && Integer.valueOf(versionInfo.getAppVersionNo()) > PackageUtils.getVersionCode(context)//对比版本号
                                   ;
                       }
                   })
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(new DefaultSubscriber<PgyVersionInfo>() {
                       @Override
                       public void onCompleted() {
                           if (showTips && !getHasNext()) Toast.makeText(context, "当前已是最新版本", Toast.LENGTH_SHORT).show();
                           Log.i(TAG, "检查更新完毕: 已经是最新版本");
                       }

                       @Override
                       public void onError(Throwable e) {
                           if (showTips) Toast.makeText(context, "检查更新出错", Toast.LENGTH_SHORT).show();
                           Log.i(TAG, "检查更新出错: " + e.getMessage());
                       }

                       @Override
                       public void onNext(PgyVersionInfo versionInfo) {
                           showUpdateDialog(versionInfo.getAppVersion(), versionInfo.getAppUpdateDescription(), null);
                       }
                   });
    }

    private void showUpdateDialog(String v, String changeLog, String url) {
        Context c = AppService.this;
        boolean isForce = v.endsWith("f");
        String downloadUrl = new StringBuilder(PgyerService.BASE_URL).append("install/?")
                                                                     .append("aId=")
                                                                     .append(getAppVersion().getAppId())
                                                                     .append("&_api_key=")
                                                                     .append(getAppVersion().getApiKey())
                                                                     .toString();
        c.startActivity(new Intent(c, ForceUpdateDialog.class).putExtra(ForceUpdateDialog.EXTRA_VERSION, v)
                                                              .putExtra(ForceUpdateDialog.EXTRA_CHANGELOG, changeLog)
                                                              .putExtra(ForceUpdateDialog.EXTRA_URL, downloadUrl)
                                                              .putExtra(ForceUpdateDialog.EXTRA_FORCE, isForce)
                                                              .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    private void downloadApp(final String url) {
        File apkFile;
        if ((apkFile = createTempApk()) == null) return;
        NetworkUtil.progressRetrofit(CommonService.BASE_URL, null, getDownloadCallBack())
                   .create(CommonService.class)
                   .downLoad(url)
                   .enqueue(getResponseCallBack(apkFile, url));
    }

    protected void downloadApp(String appid, String apiKey) {
        File apkFile;
        if ((apkFile = createTempApk()) == null) return;
        NetworkUtil.progressRetrofit(PgyerService.BASE_URL, null, getDownloadCallBack())
                   .create(PgyerService.class)
                   .downLoadApk(appid, apiKey)
                   .enqueue(getResponseCallBack(apkFile, null));
    }

    private File createTempApk() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, R.string.noSdcardMountedDownloadFail, Toast.LENGTH_SHORT).show();
            return null;
        }
        File apkFile = new File(Environment.getExternalStorageDirectory(), "update.apk");
        boolean created = true;
        if (!apkFile.exists()) {
            try {
                created = apkFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                created = false;
            }
        }
        if (!created) {
            Toast.makeText(this, R.string.createFileFail, Toast.LENGTH_SHORT).show();
            apkFile = null;
        }
        return apkFile;
    }

    private ProgressInterceptor.DownloadListener getDownloadCallBack() {
        return new ProgressInterceptor.DownloadListener() {
            @Override
            public void downloadProgress(long bytesRead, long contentLength, int percentage, boolean done) {
                if (!done) {
                    nManager.notify(DOWNLOAD_NOTICE_ID,
                                    new Notification.Builder(context).setContentTitle(getString(R.string.updatingApp))
                                                                     .setContentText(getString(R.string.downloadProgress, percentage))
                                                                     .setSmallIcon(R.drawable.ic_launcher)
                                                                     .setOngoing(true)
                                                                     .setProgress(100, percentage, false)
                                                                     .getNotification());
                }
            }
        };
    }

    private Intent getInstallIntent(File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, IntentKey.AUTHORITY + ".fileprovider", apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).setDataAndType(Uri.fromFile(apkFile), APK_MIME_TYPE);
        }
        return intent;
    }

    private Callback<ResponseBody> getResponseCallBack(final File apkFile, final String url) {
        return new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    FileUtil.storeFile(apkFile, response.body().byteStream());
                    Intent installIntent = getInstallIntent(apkFile);
                    PendingIntent pi = PendingIntent.getActivity(AppService.this, 0, installIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    nManager.notify(DOWNLOAD_NOTICE_ID,
                                    new Notification.Builder(context).setContentTitle(getString(R.string.updatingApp))
                                                                     .setContentText(getString(R.string.downloadFinishClickInstall))
                                                                     .setContentIntent(pi)
                                                                     .setSmallIcon(R.drawable.ic_launcher)
                                                                     .setOngoing(false)
                                                                     .setAutoCancel(false)
                                                                     .setProgress(100, 100, false)
                                                                     .getNotification());
                    context.startActivity(installIntent);
                } catch (IOException e) {
                    e.printStackTrace();
                    onFailure(call, e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Intent intent = new Intent(AppService.this, AppService.class).setAction(IntentKey.ACTION_DOWNLOAD_APP)
                                                                             .putExtra(IntentKey.EXTRA_DOWNLOAD_URL, url);
                PendingIntent pi = PendingIntent.getService(AppService.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                nManager.notify(DOWNLOAD_NOTICE_ID,
                                new Notification.Builder(context).setContentTitle(getString(R.string.updatingApp))
                                                                 .setContentText(getString(R.string.downloadFailClickRetry))
                                                                 .setContentIntent(pi)
                                                                 .setSmallIcon(R.drawable.ic_launcher)
                                                                 .setOngoing(false)
                                                                 .setAutoCancel(false)
                                                                 .getNotification());
            }
        };
    }

}
