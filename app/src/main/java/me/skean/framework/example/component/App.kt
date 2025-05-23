package me.skean.framework.example.component

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.room.RoomDatabase
import cn.numeron.okhttp.log.LogLevel
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.chibatching.kotpref.Kotpref
import com.jeremyliao.liveeventbus.LiveEventBus
import com.tencent.bugly.crashreport.CrashReport
import me.skean.framework.example.BuildConfig
import me.skean.framework.example.db.AppDatabase
import me.skean.framework.example.db.Migrations
import me.skean.framework.example.constant.Events
import me.skean.framework.example.net.ArticleApi
import me.skean.framework.example.net.DouBanApi
import me.skean.skeanframework.component.SkeanFrameWork
import me.skean.skeanframework.component.SkeanFrameworkModules
import me.skean.skeanframework.ktext.checkUpdateByPgyerApi
import me.skean.skeanframework.utils.AppStatusTracker
import me.skean.skeanframework.utils.AppStatusTracker.StatusCallback
import me.skean.skeanframework.utils.LogFileWriter
import me.skean.skeanframework.utils.NetworkUtil
import me.skean.skeanframework.utils.ReportUtils
import net.sqlcipher.database.SQLiteDatabase.getBytes
import net.sqlcipher.database.SupportFactory
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import java.io.File


/**
 * App的Application
 */
class App : Application(), StatusCallback {

    @SuppressLint("StaticFieldLeak")
    companion object {
        @JvmStatic
        val instance: App get() = _instance

        @JvmStatic
        val context: Context get() = _instance.applicationContext
        private lateinit var _instance: App

        @JvmStatic
        val appExternalDatabaseDir: String?
            get() {
                context.getExternalFilesDir(null).let {
                    val file = File(it, "Database").apply { FileUtils.createOrExistsDir(this) }
                    return file.absolutePath
                }
            }

    }

    private val TAG = BuildConfig.APP_TAG
    private var database: AppDatabase? = null

    override fun onCreate() {
        super.onCreate()
        _instance = this
        //AppStatusTracker初始化
        AppStatusTracker.init(this)
        AppStatusTracker.getInstance().statusCallback = this
        //初始化框架
        SkeanFrameWork.init(this)
        NetworkUtil.init(this, if (BuildConfig.FLAVOR == "production") LogLevel.BASIC else LogLevel.BODY)
        //AndroidUtils初始化
        Utils.init(this)
        LogUtils.getConfig() //.setLogSwitch(BuildConfig.DEBUG)
            .setGlobalTag(TAG)
            .setLogHeadSwitch(true)
            .setLog2FileSwitch(BuildConfig.USE_FILE_LOGGER)
            .setFilePrefix("new")
            .setFileWriter(LogFileWriter(5 * 1024 * 1024)).isSingleTagSwitch = true
        //数据库初始化
        initDatabase()
        //初始化Kotpref
        Kotpref.init(this)
        //初始化Koin
        startKoin {
            androidContext(this@App) //注入context
            androidLogger(Level.ERROR) //使用Android的Log
            modules(SkeanFrameworkModules.module, //传入框架的注入对象模块
                module {//传入App的注入对象模块
                    single { database!!.dummyDao }
                    single { NetworkUtil.createService<DouBanApi>() }
                    single { NetworkUtil.createService<ArticleApi>() }

                })
        }
        checkUpdateByPgyerApi()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        //初始化上报的工具
        if (BuildConfig.USE_FILE_LOGGER) { //内网的保存在本地文件中
            ReportUtils.getInstance().init(this)
        } else { //外网的使用BUGLY
            CrashReport.initCrashReport(applicationContext)
        }
    }

    /**
     * 数据库初始化
     */
   private fun initDatabase() {
        //使用加密数据库
        val factory = SupportFactory(getBytes("sjkmm".toCharArray()))
        database = Room.databaseBuilder(this, AppDatabase::class.java, "$TAG.db")
            .openHelperFactory(factory)
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            // .allowMainThreadQueries() //是否允许在主线程进行操作
            .addMigrations(*Migrations.COLLECTIONS)
            .build()
    }


    ///////////////////////////////////////////////////////////////////////////
    // 修改app的数据库指向位置, 保存在SD上面
    ///////////////////////////////////////////////////////////////////////////
    override fun openOrCreateDatabase(name: String?, mode: Int, factory: SQLiteDatabase.CursorFactory?): SQLiteDatabase {
        return super.openOrCreateDatabase(getDatabasePath(name).absolutePath, mode, factory)
    }

    override fun openOrCreateDatabase(
        name: String?,
        mode: Int,
        factory: SQLiteDatabase.CursorFactory?,
        errorHandler: DatabaseErrorHandler?
    ): SQLiteDatabase {
        return super.openOrCreateDatabase(getDatabasePath(name).absolutePath, mode, factory, errorHandler)
    }


    override fun deleteDatabase(name: String?): Boolean {
        return super.deleteDatabase(getDatabasePath(name).absolutePath)
    }

    override fun getDatabasePath(name: String?): File {
        val dbName = name ?: "db"
        return if (BuildConfig.EXTERNAL_DB) {
            File(appExternalDatabaseDir, dbName)
        } else super.getDatabasePath(dbName)
    }

    override fun onToForeground() {
        LogUtils.iTag(TAG, "恢复到前台了")
        LiveEventBus.get<Any?>(Events.FOREGROUND)
            .post(null)
    }

    override fun onToBackground() {
        LogUtils.iTag(TAG, "进入了后台")
        LiveEventBus.get<Any?>(Events.BACKGROUND)
            .post(null)
    }


}