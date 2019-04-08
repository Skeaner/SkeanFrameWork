package skean.me.base.component;

import android.net.Uri;

import skean.yzsm.com.framework.BuildConfig;

/**
 * 一些在Intent中使用的Key
 */
public class IntentKey {

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID;
    public static final String ACTION_PREFIX = AUTHORITY + ".actions.";
    public static final String EXTRA_PREFIX = AUTHORITY + ".extras.";

    ///////////////////////////////////////////////////////////////////////////
    // 下载更新
    ///////////////////////////////////////////////////////////////////////////
    public static final String ACTION_DOWNLOAD_APP = ACTION_PREFIX + "ACTION_DOWNLOAD_APP";
    public static final String EXTRA_DOWNLOAD_URL = EXTRA_PREFIX + "EXTRA_DOWNLOAD_URL";

    ///////////////////////////////////////////////////////////////////////////
    // 强制更新退出了
    ///////////////////////////////////////////////////////////////////////////
    public static final String ACTION_FORCE_UPDATE_EXIT = ACTION_PREFIX + "ACTION_FORCE_UPDATE_EXIT";

}
