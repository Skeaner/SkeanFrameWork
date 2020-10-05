package me.skean.framework.example.component

import android.content.Context
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.multidex.MultiDexApplication
import androidx.room.Room
import androidx.room.RoomDatabase
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.tencent.bugly.Bugly
import me.skean.framework.example.BuildConfig
import me.skean.framework.example.EventBusIndex
import me.skean.framework.example.db.AppDatabase
import me.skean.framework.example.db.Migrations
import me.skean.framework.example.event.BackgroundEvent
import me.skean.framework.example.event.ForegroundEvent
import me.skean.skeanframework.component.SkeanFrameWork
import me.skean.skeanframework.utils.AppStatusTracker
import me.skean.skeanframework.utils.AppStatusTracker.StatusCallback
import me.skean.skeanframework.utils.LogFileWriter
import me.skean.skeanframework.utils.NetworkUtil.init
import me.skean.skeanframework.utils.ReportUtils
import net.sqlcipher.database.SQLiteDatabase.getBytes
import net.sqlcipher.database.SupportFactory
import okhttp3.logging.HttpLoggingInterceptor
import org.greenrobot.eventbus.EventBus
import java.io.File


/**
 * App的Application
 */
class App : MultiDexApplication(), StatusCallback {

    companion object {
        @JvmStatic
        var instance: App? = null
            private set

        @JvmStatic
        val context: Context? = instance?.applicationContext

        @JvmStatic
        val appExternalFilesRootDir: String?
            get() {
                val file = instance?.getExternalFilesDir(null)?.apply { FileUtils.createOrExistsDir(this) }
                return file?.absolutePath
            }

        /**
         *
         * @param type The type of files directory to return. May be  null
         *            for the root of the files directory or one of the following
         *            constants for a subdirectory:
         *            Environment#DIRECTORY_MUSIC,
         *            Environment#DIRECTORY_PODCASTS,
         *            Environment#DIRECTORY_RINGTONES,
         *            Environment#DIRECTORY_ALARMS,
         *            Environment#DIRECTORY_NOTIFICATIONS,
         *            Environment#DIRECTORY_PICTURES,
         *            Environment#DIRECTORY_MOVIES.
         * @return String?
         */
        @JvmStatic
        fun getExternalFilesDir(type: String?): String? {
            val file = instance?.getExternalFilesDir(type)?.apply { FileUtils.createOrExistsDir(this) }
            return file?.absolutePath
        }

        @JvmStatic
        val appExternalDatabaseDir: String?
            get() {
                appExternalFilesRootDir?.let {
                    val file = File(appExternalFilesRootDir, "Database").apply { FileUtils.createOrExistsDir(this) }
                    return file.absolutePath
                }
                return null
            }

        @JvmStatic
        val appExternalCacheDir: String?
            get() {
                return instance?.externalCacheDir?.apply { FileUtils.createOrExistsDir(this) }?.absolutePath
            }
    }

    private val TAG = BuildConfig.APP_TAG
    private var tempObject: Any? = null
    var database: AppDatabase? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        //AppStatusTracker初始化
        AppStatusTracker.init(this)
        AppStatusTracker.getInstance().statusCallback = this
        //初始化框架
        SkeanFrameWork.init(this)
        init(this, if (BuildConfig.FLAVOR == "production") HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.BODY)
        //AndroidUtils初始化
        Utils.init(this)
        LogUtils.getConfig() //.setLogSwitch(BuildConfig.DEBUG)
                .setGlobalTag(TAG)
                .setLogHeadSwitch(true)
                .setLog2FileSwitch(BuildConfig.LOG_TO_FILE)
                .setFilePrefix("new")
                .setFileWriter(LogFileWriter(5 * 1024 * 1024)).isSingleTagSwitch = true
        //初始化EventBus
        EventBus.builder().throwSubscriberException(true).addIndex(EventBusIndex()).installDefaultEventBus()
        //初始化上报的工具
        if (BuildConfig.IS_INTRANET) { //内网的保存在本地文件中
            ReportUtils.getInstance().init(context)
        }
        else { //外网的使用BUGLY
            Bugly.init(applicationContext, BuildConfig.BUGLY_APPID, BuildConfig.DEBUG)
        }
        //数据库初始化
        initDatabase()
    }

    /**
     * 数据库初始化
     */
    fun initDatabase() {
        //使用加密数据库
        val factory = SupportFactory(getBytes("sjkmm".toCharArray()))
        database = Room.databaseBuilder(this, AppDatabase::class.java, "$TAG.db")
                .openHelperFactory(factory)
                .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                // .allowMainThreadQueries() //是否允许在主线程进行操作
                .addMigrations(*Migrations.COLLECTIONS)
                .build()
    }

    fun <Temp> setTempObject(tempObject: Temp) {
        this.tempObject = tempObject
    }

    fun <Temp> getTempObject(): Temp? {
        return tempObject as Temp?
    }

    fun releaseTempObject() {
        tempObject = null
    }

    ///////////////////////////////////////////////////////////////////////////
    // 修改app的数据库指向位置, 保存在SD上面
    ///////////////////////////////////////////////////////////////////////////
    override fun openOrCreateDatabase(name: String, mode: Int, factory: SQLiteDatabase.CursorFactory): SQLiteDatabase {
        return super.openOrCreateDatabase(getDatabasePath(name).absolutePath, mode, factory)
    }

    override fun openOrCreateDatabase(name: String, mode: Int, factory: SQLiteDatabase.CursorFactory, errorHandler: DatabaseErrorHandler?): SQLiteDatabase {
        return super.openOrCreateDatabase(getDatabasePath(name).absolutePath, mode, factory, errorHandler)
    }

    override fun deleteDatabase(name: String): Boolean {
        return super.deleteDatabase(getDatabasePath(name).absolutePath)
    }

    override fun getDatabasePath(name: String): File {
        return if (BuildConfig.EXTERNAL_DB) {
            File(appExternalDatabaseDir, name)
        }
        else super.getDatabasePath(name)
    }

    override fun onToForeground() {
        LogUtils.iTag(TAG, "恢复到前台了")
        EventBus.getDefault().post(ForegroundEvent())
    }

    override fun onToBackground() {
        LogUtils.iTag(TAG, "进入了后台")
        EventBus.getDefault().post(BackgroundEvent())
    }


}