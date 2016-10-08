package skean.me.base.net;

import android.os.Message;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * 下载进度的辅助工具
 */
public class DownloadHelper implements ProgressHelper {

    int progress = 0;
    private ProgressHandler mHandler;

    private ProgressResponseBody.Listener listener = new ProgressResponseBody.Listener() {
        //该方法在子线程中运行
        @Override
        public void onResponseProgress(long bytesWritten, long total, boolean done) {
            if (mHandler != null) {
                //不知道为什么立刻发送完成会解析包失败, 这里延迟100毫秒才发送
                if (done) mHandler.sendEmptyMessageDelayed(ProgressHandler.MSG_DONE, 100);
                else {
                    int tempProgress = (int) (bytesWritten * 100 / total);
                    if (tempProgress - progress >= 1) {
                        progress = tempProgress;
                        Message m = new Message();
                        m.what = ProgressHandler.MSG_PROGRESS;
                        m.arg1 = progress;
                        mHandler.sendMessage(m);
                    }
                }
            }
        }
    };

    @Override
    public OkHttpClient getOkHttpClient(OkHttpClient.Builder builder) {
        if (builder == null) {
            builder = new OkHttpClient.Builder();
        }
        //添加拦截器，自定义ResponseBody，添加下载进度
        builder.networkInterceptors()
               .add(new Interceptor() {
                   @Override
                   public okhttp3.Response intercept(Chain chain) throws IOException {
                       okhttp3.Response originalResponse = chain.proceed(chain.request());
                       return originalResponse.newBuilder()
                                              .body(new ProgressResponseBody(originalResponse.body(), listener))
                                              .build();
                   }
               });
        return builder.build();
    }

    public void setProgressHandler(ProgressHandler mHandler) {
        this.mHandler = mHandler;
    }

    public int getProgress() {
        return progress;
    }
}