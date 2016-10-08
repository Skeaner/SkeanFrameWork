package skean.me.base.component;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
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
import skean.me.base.net.DownloadHelper;
import skean.me.base.net.PgyAppInfo;
import skean.me.base.net.PgyVersionInfo;
import skean.me.base.net.PgyerService;
import skean.me.base.net.ProgressHandler;
import skean.me.base.utils.AppCommonUtils;
import skean.me.base.utils.FileUtil;
import skean.me.base.utils.PackageUtils;
import skean.me.base.widget.UpdateDialog;
import skean.yzsm.com.hzevent.R;

/**
 * App的后台基础服务
 */
public class AppBaseService extends Service {

    public static final String TAG = "AppService";
    protected Context context;
    protected AppServiceBinder binder = new AppServiceBinder();

    AppApplication appApplication;
    NotificationManager nManager;

    int tempProgress = 0;
    public static final int DOWNLOAD_NOTICE_ID = 1;
    DownloadHelper helper;

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
        public AppBaseService getService() {
            return AppBaseService.this;
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
        return new AppVersion("", "");
    }

    ///////////////////////////////////////////////////////////////////////////
    // 便利启动方法
    ///////////////////////////////////////////////////////////////////////////

    public static void startCheckUpdateInPGYER(Context context, boolean showTips) {
        Intent intent = new Intent(context, AppBaseService.class);
        intent.setAction(IntentKey.ACTION_CHECK_UPDATE_IN_PGYER);
        intent.putExtra(IntentKey.EXTRA_SHOW_TIPS, showTips);
        context.startService(intent);
    }

    public static void startDownloadApp(Context context, String url) {
        Intent intent = new Intent(context, AppBaseService.class);
        intent.setAction(IntentKey.ACTION_DOWNLOAD_APP);
        intent.putExtra(IntentKey.EXTRA_DOWNLOAD_URL, url);
        context.startService(intent);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 更新
    ///////////////////////////////////////////////////////////////////////////

    private void checkUpdateInPGYER(final boolean showTips) {
        AppCommonUtils.baseRetrofit(PgyerService.BASE_URL)
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
        Context c = AppBaseService.this;
        c.startActivity(new Intent(c, UpdateDialog.class).putExtra(UpdateDialog.EXTRA_VERSION, v)
                                                         .putExtra(UpdateDialog.EXTRA_CHANGELOG, changeLog)
                                                         .putExtra(UpdateDialog.EXTRA_URL, url)
                                                         .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    private void downloadApp(final String url) {
        File apkFile;
        if ((apkFile = createTempApk()) == null) return;
        helper = new DownloadHelper();
        helper.setProgressHandler(ProgressHandler.newInstance(getProgressCallback(apkFile)));
        AppCommonUtils.progressRetrofit(url, helper).create(CommonService.class).downLoad().enqueue(getResponseCallBack(apkFile));
    }

    protected void downloadApp(String appid, String apiKey) {
        File apkFile;
        if ((apkFile = createTempApk()) == null) return;
        helper = new DownloadHelper();
        helper.setProgressHandler(ProgressHandler.newInstance(getProgressCallback(apkFile)));
        AppCommonUtils.progressRetrofit(PgyerService.BASE_URL, helper)
                      .create(PgyerService.class)
                      .downLoadApk(appid, apiKey)
                      .enqueue(getResponseCallBack(apkFile));
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

    private ProgressHandler.ProgressHandlerCallback getProgressCallback(final File apkFile) {
        return new ProgressHandler.ProgressHandlerCallback() {
            @Override
            protected void onProgress(int percent) {
                nManager.notify(DOWNLOAD_NOTICE_ID,
                                new Notification.Builder(context).setContentTitle(getString(R.string.updatingApp))
                                                                 .setContentText(getString(R.string.downloadProgress, percent) + "%")
                                                                 .setSmallIcon(R.drawable.ic_launcher)
                                                                 .setOngoing(true)
                                                                 .setProgress(100, percent, false)
                                                                 .getNotification());
            }

            @Override
            protected void onDone() {
                Intent intent = new Intent(Intent.ACTION_VIEW).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                              .setDataAndType(Uri.fromFile(apkFile), APK_MIME_TYPE);
                PendingIntent pi = PendingIntent.getActivity(AppBaseService.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                nManager.notify(DOWNLOAD_NOTICE_ID,
                                new Notification.Builder(context).setContentTitle(getString(R.string.updatingApp))
                                                                 .setContentText(getString(R.string.downloadFinishClickInstall))
                                                                 .setContentIntent(pi)
                                                                 .setSmallIcon(R.drawable.ic_launcher)
                                                                 .setOngoing(false)
                                                                 .setAutoCancel(false)
                                                                 .setProgress(100, 100, false)
                                                                 .getNotification());
                context.startActivity(intent);
            }

        };
    }

    private Callback<ResponseBody> getResponseCallBack(final File apkFile) {
        return new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    FileUtil.storeFile(apkFile, response.body().byteStream());
                } catch (IOException e) {
                    e.printStackTrace();
                    onFailure(call, e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Intent intent = new Intent(AppBaseService.this, AppBaseService.class).setAction(IntentKey.ACTION_DOWNLOAD_APP);
                PendingIntent pi = PendingIntent.getService(AppBaseService.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
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
