package skean.me.base.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

/**
 * 包信息工具类 <p/>
 */
public class PackageUtils {

    public static String getVersionName(Context context) {
        return getVersionName(context, context.getPackageName());
    }

    public static String getVersionName(Context context, String targetPackageName) {
        String versionName = "应用未安装";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(targetPackageName, 0);
            versionName = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static int getVersionCode(Context context) {
        return getVersionCode(context, context.getPackageName());
    }

    public static int getVersionCode(Context context, String targetPackageName) {
        int versionCode = -1;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(targetPackageName, 0);
            versionCode = info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static boolean isPackageExist(Context context, String targetPackageName) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(targetPackageName, 0);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 获取ApiKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return apiKey;
        //我发现的主要功能有: DNS缓存,避免每次都
    }
}
