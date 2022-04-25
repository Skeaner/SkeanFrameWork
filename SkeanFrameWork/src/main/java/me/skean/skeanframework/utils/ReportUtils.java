package me.skean.skeanframework.utils;

import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;

/**
 * 上报问题处理器
 */
public class ReportUtils implements UncaughtExceptionHandler {

    private static final String TAG = ReportUtils.class.getSimpleName();

    private static ReportUtils mReportUtils;
    private Context mContext;
    private UncaughtExceptionHandler mDefaultHandler;

    /**
     * 获取CrashHandler实例，单例模式。
     */
    public static ReportUtils getInstance() {
        if (mReportUtils == null) {
            synchronized (ReportUtils.class) {
                if (mReportUtils == null) {
                    mReportUtils = new ReportUtils();
                }
            }
        }
        return mReportUtils;
    }

    public void init(Context context) {
        mContext = context;
        // 获取系统默认的uncaughtException处理类实例
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置成我们处理uncaughtException的类
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.d(TAG, "uncaughtException:" + ex);
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理异常就由系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    //处理异常事件
    public boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        ex.printStackTrace();
        new Thread(() -> {
            Looper.prepare();
            Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_SHORT).show();
            Looper.loop();
        }).start();
        // 收集设备参数信息
        logsDeviceInfo();
        // 收集错误日志
        LogUtils.eTag(TAG, "出错的信息", ex);
        return true;
    }

    //收集应用和设备信息
    private void logsDeviceInfo() {
        StringBuffer sb = new StringBuffer();
        //获取应用的信息
        sb.append("versionCode : ")
          .append(AppUtils.getAppVersionCode())
          .append("\n")
          .append("versionName : ")
          .append(AppUtils.getAppVersionName())
          .append("\n");
        //获取设备的信息
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                sb.append(field.getName()).append(" : ").append(field.get(null)).append("\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        fields = Build.VERSION.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                sb.append(field.getName()).append(" : ").append(field.get(null)).append("\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        LogUtils.iTag(TAG, sb.toString());
    }

}
