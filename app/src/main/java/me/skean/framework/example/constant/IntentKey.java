package me.skean.framework.example.constant;

import me.skean.framework.example.BuildConfig;

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


}
