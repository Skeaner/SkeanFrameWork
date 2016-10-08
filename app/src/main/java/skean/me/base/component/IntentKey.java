package skean.me.base.component;

import android.net.Uri;

/**
 * 一些在Intent中使用的Key
 */
public class IntentKey {
    // FIXME: 2016/9/30 修改包名
    public static final String AUTHORITY = "yzsm.com.hzcloud";
    public static final String ACTION_PREFIX = AUTHORITY + ".actions.";
    public static final String EXTRA_PREFIX = AUTHORITY + ".extras.";

    ///////////////////////////////////////////////////////////////////////////
    // 检查应用更新(蒲公英)
    ///////////////////////////////////////////////////////////////////////////
    public static final String ACTION_CHECK_UPDATE_IN_PGYER = ACTION_PREFIX + "CHECK_UPDATE_IN_PGYER";
    public static final String EXTRA_SHOW_TIPS = EXTRA_PREFIX + "EXTRA_SHOW_TIPS";


    //下载App
    public static final String ACTION_DOWNLOAD_APP = ACTION_PREFIX + "ACTION_DOWNLOAD_APP";
    public static final String EXTRA_DOWNLOAD_URL = EXTRA_PREFIX + "EXTRA_DOWNLOAD_URL";

}
