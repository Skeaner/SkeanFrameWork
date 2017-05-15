package skean.me.base.component;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.multidex.MultiDexApplication;

import com.baidu.mapapi.SDKInitializer;
import com.blankj.utilcode.utils.Utils;
import com.pgyersdk.crash.PgyCrashManager;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.io.File;

import skean.me.base.utils.FileUtil;

/**
 * App的Application
 */
public final class AppApplication extends MultiDexApplication {

    private Object tempObject = null;

    private static AppApplication instance;

    // FIXME: 替换为自己对应的标签
    public static final String TAG = "FUCK";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        FlowManager.init(new FlowConfig.Builder(this).openDatabasesOnInit(true).build());
        SDKInitializer.initialize(getApplicationContext());
        PgyCrashManager.register(this);
        Utils.init(this);
    }

    public static AppApplication getInstance() {
        return instance;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
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
        return new File(getAppExternalStorageDirectory(), name);
    }

}
