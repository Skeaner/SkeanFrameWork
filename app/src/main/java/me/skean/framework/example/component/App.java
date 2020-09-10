package me.skean.framework.example.component;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.tencent.bugly.Bugly;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.database.Database;

import java.io.File;
import java.util.List;

import androidx.multidex.MultiDexApplication;

import me.skean.framework.example.EventBusIndex;
import me.skean.framework.example.db.entity.DaoMaster;
import me.skean.framework.example.db.entity.DaoSession;
import me.skean.framework.example.event.BackgroundEvent;
import me.skean.framework.example.event.ForegroundEvent;
import me.skean.skeanframework.component.SkeanFrameWork;
import me.skean.skeanframework.db.Migrations;
import me.skean.skeanframework.utils.AppStatusTracker;
import me.skean.skeanframework.utils.LogFileWriter;
import me.skean.skeanframework.utils.NetworkUtil;
import me.skean.skeanframework.utils.ReportUtils;
import me.skean.framework.example.BuildConfig;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * App的Application
 */
public final class App extends MultiDexApplication implements AppStatusTracker.StatusCallback {

    private Object tempObject = null;
    private static App instance;
    private DaoSession daoSession;

    public static final String TAG = BuildConfig.APP_TAG;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //AppStatusTracker初始化
        AppStatusTracker.init(this);
        AppStatusTracker.getInstance().setStatusCallback(this);
        //初始化框架
        SkeanFrameWork.init(this);
        NetworkUtil.INSTANCE.init(this,
                                  BuildConfig.FLAVOR.equals("production") ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.BODY);
        //AndroidUtils初始化
        Utils.init(this);
        LogUtils.getConfig()
                //.setLogSwitch(BuildConfig.DEBUG)
                .setGlobalTag(TAG)
                .setLogHeadSwitch(true)
                .setLog2FileSwitch(BuildConfig.LOG_TO_FILE)
                .setFilePrefix("new")
                .setFileWriter(new LogFileWriter(5 * 1024 * 1024)) //log文件最大5M
                .setSingleTagSwitch(true);
        //初始化EventBus
        EventBus.builder().throwSubscriberException(true).addIndex(new EventBusIndex()).installDefaultEventBus();
        //初始化上报的工具
        if (BuildConfig.IS_INTRANET) { //内网的保存在本地文件中
            ReportUtils.getInstance().init(getContext());
        }
        else { //外网的使用BUGLY
            Bugly.init(getApplicationContext(), BuildConfig.BUGLY_APPID, BuildConfig.DEBUG);
        }
        //GreenDAO初始化
        DaoMaster.OpenHelper helper = new DaoMaster.OpenHelper(this, "db") {
            @Override
            public void onCreate(Database db) {
                super.onCreate(db);
            }

            @Override
            public void onUpgrade(Database db, int oldVersion, int newVersion) {
                List<Migrations.Migration> migrations = Migrations.getMigrations();
                for (Migrations.Migration migration : migrations) {
                    if (oldVersion < migration.getVersion()) {
                        migration.runMigration(db);
                    }
                }
            }
        };
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public static App getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public static String getAppExternalStorageDirectory() {
        File file = new File(Environment.getExternalStorageDirectory(), TAG);
        FileUtils.createOrExistsDir(file);
        return file.getAbsolutePath();
    }

    public static String getAppPicturesDirectory() {
        File file = new File(String.format("%s/%s/Picture", Environment.getExternalStorageDirectory(), TAG));
        FileUtils.createOrExistsDir(file);
        return file.getAbsolutePath();
    }

    public <Temp> void setTempObject(Temp tempObject) {
        this.tempObject = tempObject;
    }

    @SuppressWarnings("unchecked")
    public <Temp> Temp getTempObject() {
        return (Temp) tempObject;
    }

    public void releaseTempObject() {
        this.tempObject = null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 修改app的数据库指向位置, 保存在SD上面
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return super.openOrCreateDatabase(getDatabasePath(name).getAbsolutePath(), mode, factory);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name,
                                               int mode,
                                               SQLiteDatabase.CursorFactory factory,
                                               DatabaseErrorHandler errorHandler) {
        return super.openOrCreateDatabase(getDatabasePath(name).getAbsolutePath(), mode, factory, errorHandler);
    }

    @Override
    public boolean deleteDatabase(String name) {
        return super.deleteDatabase(getDatabasePath(name).getAbsolutePath());
    }

    @Override
    public File getDatabasePath(String name) {
        if (BuildConfig.EXTERNAL_DB) {
            return new File(getAppExternalStorageDirectory(), name);
        }
        return super.getDatabasePath(name);
    }

    @Override
    public void onToForeground() {
        LogUtils.iTag(TAG, "恢复到前台了");
        EventBus.getDefault().post(new ForegroundEvent());
    }

    @Override
    public void onToBackground() {
        LogUtils.iTag(TAG, "进入了后台");
        EventBus.getDefault().post(new BackgroundEvent());
    }
}
