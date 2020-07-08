package impl.component;

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

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.io.File;
import java.io.IOException;

import impl.constant.IntentKey;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import base.net.CommonService;
import base.net.ProgressInterceptor;
import base.utils.NetworkUtil;

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
                case IntentKey.ACTION_DOWNLOAD_APP:
                    String url = intent.getStringExtra(IntentKey.EXTRA_DOWNLOAD_URL);
                    if (url != null) downloadApp(url);
                    else ToastUtils.showLong("没有找到下载地址");
                    break;
                default:
                    Log.i(TAG, "未知Intent: " + intent);
                    break;
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 便利启动方法
    ///////////////////////////////////////////////////////////////////////////

    public static void startDownloadApp(Context context, String url) {
        Intent intent = new Intent(context, AppService.class);
        intent.setAction(IntentKey.ACTION_DOWNLOAD_APP);
        intent.putExtra(IntentKey.EXTRA_DOWNLOAD_URL, url);
        context.startService(intent);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 更新
    ///////////////////////////////////////////////////////////////////////////



    private void downloadApp(final String url) {
        File apkFile;
        if ((apkFile = createTempApk()) == null) return;
        NetworkUtil.progressRetrofit(CommonService.BASE_URL, null, getDownloadCallBack())
                   .create(CommonService.class)
                   .downLoad(url)
                   .enqueue(getResponseCallBack(apkFile, url));
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
                    FileIOUtils.writeFileFromIS(apkFile, response.body().byteStream());
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
                } catch (Exception e) {
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
