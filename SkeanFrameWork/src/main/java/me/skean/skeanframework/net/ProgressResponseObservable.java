package me.skean.skeanframework.net;

import com.blankj.utilcode.util.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.concurrent.CancellationException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;
import me.skean.skeanframework.utils.NetworkUtil;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public final class ProgressResponseObservable extends Observable<Integer> {
    private final File file;
    private final String url;
    private boolean isDisposed = false;
    private long totalBytes = 0L;
    private long progressBytes = 0L;
    private int percentage = 0;

    public ProgressResponseObservable(String url, File file) {
        this.url = url;
        this.file = file;
    }

    @Override
    protected void subscribeActual(Observer<? super Integer> observer) {
        CancelListener cancelListener = new CancelListener(() -> {
            this.isDisposed = true;
        });
        observer.onSubscribe(cancelListener);
        OkHttpClient okHttpClient = NetworkUtil.newAppHttpBuilder().addInterceptor(chain -> {
            Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder().body(new ResponseBody() {
                private final ResponseBody body;
                private BufferedSource bufferedSource;

                {
                    body = originalResponse.body();
                    assert body != null;
                    totalBytes = body.contentLength();
                }

                @Nullable
                @Override
                public MediaType contentType() {
                    return body.contentType();
                }

                @Override
                public long contentLength() {
                    return body.contentLength();
                }

                @NonNull
                @Override
                public BufferedSource source() {
                    if (bufferedSource == null) {
                        bufferedSource = Okio.buffer(new ForwardingSource(body.source()) {
                            @Override
                            public long read(@NonNull Buffer sink, long byteCount) throws IOException {
                                long bytesRead = super.read(sink, byteCount);
                                progressBytes += bytesRead != -1 ? bytesRead : 0;
                                int newPercentage = (int) ((progressBytes * 100L) / totalBytes);
                                if (percentage != newPercentage) {
                                    LogUtils.i("download percentage " + percentage + "%");
                                    percentage = newPercentage;
                                    observer.onNext(percentage);
                                }
                                return bytesRead;
                            }
                        });
                    }
                    return bufferedSource;
                }
            }).build();
        }).build();

        byte[] data = new byte[8192];
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            Response response = okHttpClient.newCall(new Request.Builder().url(url).build()).execute();
            if (response.code() == 200) {
                is = response.body().byteStream();
                fos = new FileOutputStream(file);
                while (!isDisposed) {
                    int bytes = is.read(data);
                    if (bytes == -1) {
                        break;
                    }
                    fos.write(data, 0, bytes);
                }
                if (isDisposed) {
                    throw new InterruptedIOException("下载出错, 用户取消下载");
                }
                else if (progressBytes < totalBytes) {
                    throw new Exception("下载出错, 下载字节数少于原始字节数");
                }
                else if (progressBytes > totalBytes) {
                    throw new Exception("下载出错, 下载字节数大于原始字节数");
                }
                observer.onComplete();
            }
            else {
                throw new RuntimeException(response.code() + response.message());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            observer.onError(e);
            // if (isDisposed && e instanceof InterruptedIOException) {
            //     observer.onError(new CancellationException(e.getMessage()));
            // }
            // else observer.onError(e);
        }
        finally {
            try {
                is.close();
                fos.close();
            }
            catch (Exception e) {
            }
        }
    }

    static final class CancelListener extends MainThreadDisposable {
        interface Listener {
            void onDispose();
        }

        private final Listener listener;

        CancelListener(Listener listener) {
            this.listener = listener;
        }

        @Override
        protected void onDispose() {
            if (listener != null) listener.onDispose();
        }

    }
}
