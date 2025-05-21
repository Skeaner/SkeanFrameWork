package me.skean.skeanframework.net;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ReflectUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.rxjava3.android.MainThreadDisposable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import retrofit2.Call;

public final class ProgressDownloadObservable extends Observable<Integer> {

    private final Call<?> rawCall;
    private okhttp3.Call newCall;
    private final File savedFile;
    private long totalBytes = 0L;
    private long savedBytes = 0L;
    private int currentPercentage = 0;
    private int progressInterval = 0;

    public ProgressDownloadObservable(Call<?> call, File savedFile, int progressInterval) {
        this.rawCall = call;
        this.savedFile = savedFile;
        this.progressInterval = progressInterval;
    }

    @Override
    protected void subscribeActual(Observer<? super Integer> observer) {
        observer.onSubscribe(new MainThreadDisposable() {
            @Override
            protected void onDispose() {
                if (newCall != null && !newCall.isCanceled() && newCall.isExecuted()) {
                    newCall.cancel();
                }
            }
        });
        Object obj = ReflectUtils.reflect(rawCall).field("delegate").get();
        OkHttpClient client = ReflectUtils.reflect(obj).field("callFactory").get();
        OkHttpClient newClient = client.newBuilder().addInterceptor(chain -> {
            okhttp3.Response rawResponse = chain.proceed(chain.request());
            if (rawResponse.body() == null) {
                return rawResponse;
            }
            return rawResponse.newBuilder().body(new ResponseBody() {
                private final ResponseBody body;
                private BufferedSource bufferedSource;

                {
                    body = rawResponse.body();
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
                                savedBytes += bytesRead != -1 ? bytesRead : 0;
                                int newPercentage = (int) ((savedBytes * 100L) / totalBytes);
                                if (newPercentage - currentPercentage >= progressInterval) {
                                    currentPercentage = newPercentage;
                                    observer.onNext(currentPercentage);
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
            newCall = newClient.newCall(rawCall.request());
            Response response = newCall.execute();
            if (response.code() == 200) {
                is = response.body().byteStream();
                fos = new FileOutputStream(savedFile);
                for (int bytes; (bytes = is.read(data)) != -1; ) {
                    fos.write(data, 0, bytes);
                }
                if (savedBytes != totalBytes) {
                    throw new Exception("下载出错, 下载大小与文件大小不符");
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

}
