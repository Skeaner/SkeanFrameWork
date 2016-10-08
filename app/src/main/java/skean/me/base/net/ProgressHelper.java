package skean.me.base.net;

import okhttp3.OkHttpClient;

/**
 * 进度Helper的接口
 */
public interface ProgressHelper {
    OkHttpClient getOkHttpClient(OkHttpClient.Builder builder);
}
