package me.skean.skeanframework.net;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ReflectUtils;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.rxjava3.android.MainThreadDisposable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;
import retrofit2.Call;

public final class ProgressUploadObservable extends Observable<Integer> {

    private final Call<?> rawCall;
    private okhttp3.Call newCall;
    private long totalBytes = 0L;
    private long savedBytes = 0L;
    private int currentPercentage = 0;
    private int progressInterval = 0;

    public ProgressUploadObservable(Call<?> call, int progressInterval) {
        this.rawCall = call;
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
            Request rawRequest = chain.request();
            if (rawRequest.body() == null) {
                return chain.proceed(rawRequest);
            }
            Request progressRequest = rawRequest.newBuilder().method(rawRequest.method(), new RequestBody() {
                private final RequestBody body;
                private BufferedSink bufferedSink;

                {
                    body = rawRequest.body();
                    totalBytes = body.contentLength();
                }

                @Nullable
                @Override
                public MediaType contentType() {
                    return body.contentType();
                }

                @Override
                public long contentLength() throws IOException {
                    return body.contentLength();
                }

                @Override
                public void writeTo(@NonNull BufferedSink rawSink) throws IOException {
                    if (bufferedSink == null) {
                        ForwardingSink progressSink = new ForwardingSink(rawSink) {
                            @Override
                            public void write(@NonNull Buffer source, long byteCount) throws IOException {
                                super.write(source, byteCount);
                                savedBytes += byteCount != -1 ? byteCount : 0;
                                int newPercentage = (int) ((savedBytes * 100L) / totalBytes);
                                if (newPercentage - currentPercentage >= progressInterval) {
                                    currentPercentage = newPercentage;
                                    observer.onNext(currentPercentage);
                                }
                            }
                        };
                        bufferedSink = Okio.buffer(progressSink);
                        body.writeTo(bufferedSink);
                        bufferedSink.flush();
                    }
                }
            }).build();
            return chain.proceed(progressRequest);
        }).build();
        try {
            newCall = newClient.newCall(rawCall.request());
            Response response = newCall.execute();
            if (response.code() == 200) {
                if (savedBytes != totalBytes) {
                    throw new Exception("上传出错, 上传大小与文件大小不符");
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
    }

}


