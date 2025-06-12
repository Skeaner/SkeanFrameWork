package me.skean.framework.example.component

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleService
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import me.skean.framework.example.R
import me.skean.framework.example.constant.ACTION_DOWNLOAD_APP
import me.skean.framework.example.constant.AUTHORITY
import me.skean.framework.example.constant.EXTRA_DOWNLOAD_URL
import me.skean.skeanframework.rx.DefaultObserver
import me.skean.skeanframework.utils.NetworkUtil.downloadObservable
import java.io.File

/**
 * App的后台基础服务
 */
class AppService : LifecycleService() {

    companion object {

        @JvmStatic
        fun startDownloadApp(context: Context, url: String?) {
            val intent = Intent(context, AppService::class.java)
            intent.setAction(ACTION_DOWNLOAD_APP)
            intent.putExtra(EXTRA_DOWNLOAD_URL, url)
            context.startService(intent)
        }
    }

    private val TAG: String = "AppService"
    private var binder: AppServiceBinder = AppServiceBinder()
    private lateinit var nManager: NotificationManager

    ///////////////////////////////////////////////////////////////////////////
    // 生命周期
    ///////////////////////////////////////////////////////////////////////////
    inner class AppServiceBinder : Binder() {
        val service: AppService get() = this@AppService
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        nManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        onStart(intent, startId)
        return START_NOT_STICKY
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        onHandleIntent(intent)
    }

    private fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            when (intent.action) {
                ACTION_DOWNLOAD_APP -> {
                    val url = intent.getStringExtra(EXTRA_DOWNLOAD_URL)
                    if (url != null) downloadApp(url)
                    else ToastUtils.showLong("没有找到下载地址")
                }
                else -> Log.i(TAG, "未知Intent: $intent")
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 更新
    ///////////////////////////////////////////////////////////////////////////
    private fun downloadApp(url: String) {
        val apkFile = File(externalCacheDir, "update.apk")
        if (!FileUtils.createOrExistsFile(apkFile)) {
            Toast.makeText(this, me.skean.skeanframework.R.string.createFileFail, Toast.LENGTH_SHORT).show()
            return
        }
        downloadObservable(url, apkFile)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DefaultObserver<Int?>() {
                override fun onNext2(percentage: Int) {
                    nManager.notify(
                        1,
                        Notification.Builder(this@AppService).setContentTitle(getString(me.skean.skeanframework.R.string.updatingApp))
                            .setContentText(
                                getString(
                                    me.skean.skeanframework.R.string.downloadProgress,
                                    percentage
                                )
                            )
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setOngoing(true)
                            .setProgress(100, percentage, false)
                            .build()
                    )
                }

                override fun onError2(e: Throwable) {
                    val intent = Intent(this@AppService, AppService::class.java).setAction(ACTION_DOWNLOAD_APP)
                        .putExtra(EXTRA_DOWNLOAD_URL, url)
                    val pi = PendingIntent.getService(
                        this@AppService,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    nManager.notify(
                        1,
                        Notification.Builder(this@AppService).setContentTitle(getString(me.skean.skeanframework.R.string.updatingApp))
                            .setContentText(getString(me.skean.skeanframework.R.string.downloadFailClickRetry))
                            .setContentIntent(pi)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setOngoing(false)
                            .setAutoCancel(false)
                            .build()
                    )
                }

                override fun onComplete2() {
                    val installIntent = Intent(Intent.ACTION_VIEW)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        installIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        val contentUri = FileProvider.getUriForFile(this@AppService, "$AUTHORITY.fileprovider", apkFile)
                        installIntent.setDataAndType(contentUri, "application/vnd.android.package-archive")
                    } else {
                        installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive")
                    }
                    val pi = PendingIntent.getActivity(
                        this@AppService,
                        0,
                        installIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    nManager.notify(
                        1,
                        Notification.Builder(this@AppService).setContentTitle(getString(me.skean.skeanframework.R.string.updatingApp))
                            .setContentText(getString(me.skean.skeanframework.R.string.downloadFinishClickInstall))
                            .setContentIntent(pi)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setOngoing(false)
                            .setAutoCancel(false)
                            .setProgress(100, 100, false)
                            .build()
                    )
                    startActivity(installIntent)
                }
            })
    }

}
