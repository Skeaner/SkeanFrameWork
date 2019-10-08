package skean.me.base.component;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.tencent.bugly.Bugly;

import org.greenrobot.greendao.database.Database;

import java.io.File;
import java.util.List;

import androidx.multidex.MultiDexApplication;
import skean.me.base.db.Migrations;
import skean.me.base.db.entity.DaoMaster;
import skean.me.base.db.entity.DaoSession;
import skean.me.base.utils.AppStatusTracker;
import skean.me.base.utils.FileUtil;
import skean.yzsm.com.framework.BuildConfig;

/**
 * App的Application
 */
public final class AppApplication extends MultiDexApplication implements AppStatusTracker.StatusCallback {

    private Object tempObject = null;
    private static AppApplication instance;
    private DaoSession daoSession;


    public static final String TAG = BuildConfig.APP_TAG;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //Bugly初始化
        Bugly.init(getApplicationContext(), BuildConfig.BUGLY_APPID, BuildConfig.DEBUG);
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
        //AndroidUtils初始化
        Utils.init(this);
        LogUtils.getConfig().setLogSwitch(true).setGlobalTag(TAG).setLogHeadSwitch(false);
        //AppStatusTracker初始化
        AppStatusTracker.init(this);
        AppStatusTracker.getInstance().setStatusCallback(this);
    }

    public static AppApplication getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public static String getAppExternalStorageDirectory() {
        File file = FileUtil.initializeFile(new File(Environment.getExternalStorageDirectory(), TAG), true);
        return file.getAbsolutePath();
    }

    public static String getAppPicturesDirectory() {
        File file = FileUtil.initializeFile(new File(String.format("%s/%s/Picture", Environment.getExternalStorageDirectory(), TAG)), true);
        return file.getAbsolutePath();
    }

    public <Temp> void setTempObject(Temp tempObject) {
        this.tempObject = tempObject;
    }

    @SuppressWarnings("unchecked")
    public <Temp> Temp getTempObject() {
        return (Temp) tempObject;
    }

    public void releaseTempObject1() {
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
        if (BuildConfig.USE_EXTERNAL_DB) {
            return new File(getAppExternalStorageDirectory(), name);
        }
        return super.getDatabasePath(name);
    }

    @Override
    public void onToForeground() {
        LogUtils.i(TAG, "恢复到前台了");
    }

    @Override
    public void onToBackground() {
        LogUtils.i(TAG, "进入了后台");
    }
}
